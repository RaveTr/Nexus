package com.mememan.nexus.platform.services;

import com.mememan.nexus.event.blueprint.EventBlueprint;
import com.mememan.nexus.event.blueprint.WrappedEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A loader-agnostic {@code interface} for managing event implementations.
 * <br></br>
 * Events are simply hooks into different parts of Minecraft that fire whenever their pertaining/hooked-into code is
 * executed.
 * <br></br>
 * More closely follows Fabric's functional event declaration system, with a number of tweaks to make it more
 * convenient to use (such as the inclusion of overloads that use more strongly-defined blueprint event interfaces added
 * by Nexus API, priority-based listener registration, etc.). However, API classes and helpers exist that define blueprints
 * to enforce conventions, similar to Forge but with much more freedom, if the end-developer prefers working that way
 * (which is usually recommended for mods depending on Nexus API, see references below for more info).
 * <br></br>
 * This exists as a service in order to allow for proper interfacing with each modloader's event system/API, or to allow
 * for writing around each modloader's event system/API if said systems are designed way too differently to warrant a
 * convention-abiding implementation.
 *
 * @apiNote The current implementations for Neo/Forge and Fabric are fundamentally different in how they interface with
 * each loader's event APIs. On Fabric, Nexus delegates listener management to Fabric's Events API. On Neo/Forge, however,
 * Nexus implements its own simplistic thread-safe event system, closely mirroring Fabric in fundamental design decisions
 * but re-written to fit needs there.
 * <br></br>
 * The reason this decision was made in regards to Neo/Forge is due to the fact that the EventBus API is designed way
 * too different from the intended functional-style approach to events. Their system essentially accepts event methods
 * as inputs to each event bus for posting as listeners later on by generating classes that implement their IEventListener
 * {@code interface} and invoke the event method within said listener at runtime using ASM. While it's efficient (though
 * questionable in design) and therefore suitable for their needs, it means that the EventBus API interfacing with Nexus
 * API's event system would provide diminishing returns in terms of benefit from being directly compatible.
 * <br></br>
 * That being said, Nexus API keeps track of event types and their listeners almost identically across loader implementations,
 * which means that there shouldn't be any issues querying Nexus API whenever needed for event information, regardless
 * of project setup (single-loader or multi-loader).
 *
 * @see EventBlueprint
 * @see WrappedEventBlueprint
 */
public interface EventBus {

    /**
     * Registers an event hook for the provided event {@code interface}, using the provided {@code eventListenerMerger}.
     * Should generally be called in your mod initializer (or your event blueprint's constructor, see {@link EventBlueprint}).
     *
     * @param eventInterface The event {@code interface} to register the hook for. Must be a functional {@code interface}.
     * @param eventListenerMerger The merging function to use for merging event listeners' results.
     * @param eventSide The side on which the event should be registered. Determines the side on which listeners pertaining
     *                  to the provided {@code eventInterface} will run.
     *
     * @param <T> The event {@code interface} type.
     *
     * @throws IllegalArgumentException If the provided {@code eventInterface} is not a functional {@code interface}.
     */
    <T> void registerEventHook(Class<T> eventInterface, Function<T[], T> eventListenerMerger, ModSide eventSide);

    /**
     * Overloaded variant of {@link #registerEventHook(Class, Function, ModSide)}. Registers an event hook using the
     * provided {@link EventBlueprint}.
     *
     * @param eventBlueprint The {@link EventBlueprint} to register the hook for.
     *
     * @param <T> The event {@code interface} type.
     *
     * @throws IllegalArgumentException If the provided {@code eventInterface} is not a functional {@code interface}.
     */
    default <T> void registerEventHook(EventBlueprint<T> eventBlueprint) {
        registerEventHook(eventBlueprint.getEventInterface(), eventBlueprint::mergeListeners, eventBlueprint.getEventSide());
    }

    /**
     * Registers an event listener for the provided event {@code interface}.
     *
     * @param eventInterface The event {@code interface} to register the listener for. Must be a functional {@code interface}.
     * @param listener The listener instance to register.
     * @param listenerExecutionSide The side on which the listener should run.
     * @param listenerPriority The priority of the listener. Lower priority values are executed first.
     *
     * @param <T> The event {@code interface} type.
     */
    <T> void onEvent(Class<T> eventInterface, T listener, ModSide listenerExecutionSide, int listenerPriority);

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int)}. Registers an event listener for the provided
     * event {@code interface} with the provided listener priority on {@link ModSide#COMMON}.
     *
     * @param eventInterface The event {@code interface} to register the listener for. Must be a functional {@code interface}.
     * @param listener The listener instance to register.
     * @param listenerPriority The priority of the listener. Lower priority values are executed first.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(Class<T> eventInterface, T listener, int listenerPriority) {
        onEvent(eventInterface, listener, ModSide.COMMON, listenerPriority);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int)}. Registers an event listener for the provided
     * event {@code interface} with the provided listener priority on the provided {@code listenerExecutionSide}. Infers
     * the event {@code interface} from the provided listener using {@link #getFunctionalInterfaceClass(T)}.
     *
     * @param listener The listener instance to register.
     * @param listenerPriority The priority of the listener. Lower priority values are executed first.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener, ModSide listenerExecutionSide, int listenerPriority) {
        onEvent(getFunctionalInterfaceClass(listener), listener, listenerExecutionSide, listenerPriority);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int)}. Registers an event listener for the provided
     * event {@code interface} with the default priority of 0 on {@link ModSide#COMMON}. Infers the event
     * {@code interface} from the provided listener using {@link #getFunctionalInterfaceClass(T)}.
     *
     * @param listener The listener instance to register.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(Class<T> eventInterface, T listener) {
        onEvent(eventInterface, listener, 0);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int)}. Registers an event listener for the provided
     * event {@code interface} with the default priority of 0. Infers the event
     * {@code interface} from the provided listener using {@link #getFunctionalInterfaceClass(T)}.
     *
     * @param listener The listener instance to register.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener, ModSide listenerExecutionSide) {
        onEvent(listener, listenerExecutionSide, 0);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int)}. Registers an event listener for the provided
     * event {@code interface} with the provided listener priority on {@link ModSide#COMMON}. Infers the event
     * {@code interface} from the provided listener using {@link #getFunctionalInterfaceClass(T)}.
     *
     * @param listener The listener instance to register.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener, int listenerPriority) {
        onEvent(listener, ModSide.COMMON, listenerPriority);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int)}. Registers an event listener for the provided
     * event {@code interface} with the default priority of 0. Infers the event {@code interface} from the provided
     * listener using {@link #getFunctionalInterfaceClass(T)}.
     *
     * @param listener The listener instance to register.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener) {
        onEvent(listener, 0);
    }

    /**
     * Fires an event for the provided event {@code interface} on the provided {@code eventSide}. Infers the event
     * {@code interface} from the provided listener using {@link #getFunctionalInterfaceClass(T)}.
     *
     * @param eventInterface The event {@code interface} to fire.
     * @param eventListenerInvokerMapper The invoker mapper to use for firing the event. Used to be able to query results
     *                                   from the finalized listener invoker when it's run.
     * @param eventSide The side on which the event should be fired.
     *
     * @param <T> The event {@code interface} type.
     *
     * @return The result of the event listener invoker mapper. May be {@code null}.
     */
    @Nullable
    <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper, ModSide eventSide);

    /**
     * Overloaded variant of {@link #fireEventHook(Class, Function, ModSide)}. Fires an event for the provided event
     * {@code interface} on {@link ModSide#COMMON}, if possible. Infers the event {@code interface} from the provided
     * listener using {@link #getFunctionalInterfaceClass(T)}.
     *
     * @param eventInterface The event {@code interface} to fire.
     * @param eventListenerInvokerMapper The invoker mapper to use for firing the event. Used to be able to query results
     *                                   from the finalized listener invoker when it's run.
     *
     * @param <T> The event {@code interface} type.
     *
     * @return The result of the event listener invoker mapper. May be {@code null}.
     */
    @Nullable
    default <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper) {
        return fireEventHook(eventInterface, eventListenerInvokerMapper, ModSide.COMMON);
    }

    /**
     * Gets a copy of the {@link Map} of listeners for the provided event {@code interface}. The resultant {@link Map}
     * is sorted by priority in ascending order.
     *
     * @param eventInterface The event {@code interface} to get listeners for.
     *
     * @param <T> The event {@code interface} type.
     *
     * @return A map of listeners for the provided event {@code interface}, sorted by priority in ascending order
     */
    <T> Map<Integer, List<T>> getListenersFor(Class<T> eventInterface);

    /**
     * Attempts to evaluate the functional {@code interface} class that a lambda implements. For internal use only.
     *
     * @param lambda The lambda or method reference to evaluate the functional {@code interface} of.
     *
     * @return The functional {@code interface} pertaining to the provided {@code lambda}.
     *
     * @throws IllegalArgumentException If the lambda's functional {@code interface} cannot be determined (usually only
     * really odd edge cases, should never hit under normal circumstances due to preliminary checks in
     * {@link #registerEventHook(Class, Function, ModSide)} implementations and overloads).
     */
    private static <T> Class<T> getFunctionalInterfaceClass(T lambda) {
        if (lambda == null) throw new NullPointerException("Attempted to evaluate functional interface for null lambda!");
        if (lambda instanceof Class) return (Class<T>) lambda; // For direct implementations

        Class<?>[] pertainingImplementedInterfaces = lambda.getClass().getInterfaces();

        if (pertainingImplementedInterfaces.length == 1) return (Class<T>) pertainingImplementedInterfaces[0]; // For lambdas and method references

        Class<?> superclass = lambda.getClass().getSuperclass();

        if (superclass != null && superclass != Object.class) {
            pertainingImplementedInterfaces = superclass.getInterfaces();

            if (pertainingImplementedInterfaces.length == 1) return (Class<T>) pertainingImplementedInterfaces[0]; // For method references on classes
        }

        throw new IllegalArgumentException(String.format("Could not determine functional interface for lambda function: %s", lambda));
    }
}