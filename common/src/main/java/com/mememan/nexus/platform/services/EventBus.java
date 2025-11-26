package com.mememan.nexus.platform.services;

import com.google.common.collect.ImmutableList;
import com.mememan.nexus.event.blueprint.EventBlueprint;
import com.mememan.nexus.event.blueprint.WrappedEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.util.ReflectionUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * Registers an event hook mapped to the provided {@code eventKey}, using the provided {@code eventListenerMerger}.
     * Should generally be called in your mod initializer (or your event blueprint's constructor, see {@link EventBlueprint}).
     *
     * @param eventKey The identifier {@link EventKey} to register the hook for. Must wrap around a functional {@code interface}.
     * @param eventListenerMerger The merging function to use for merging event listeners' results.
     * @param eventSide The side on which the event should be registered. Determines the side on which listeners pertaining
     *                  to the provided {@code eventInterface} will run.
     *
     * @param <T> The event {@code interface} type.
     *
     * @throws IllegalArgumentException If the provided {@code eventInterface} is not a functional {@code interface}.
     */
    <T> void registerEventHook(EventKey<T> eventKey, Function<T[], T> eventListenerMerger, ModSide eventSide);

    /**
     * Overloaded variant of {@link #registerEventHook(EventKey, Function, ModSide)}. Registers an event hook for the
     * provided event {@code interface}, using the provided {@code eventListenerMerger}. Should generally be called in
     * your mod initializer (or your event blueprint's constructor, see {@link EventBlueprint}).
     *
     * @param eventInterface The event {@code interface} to register the hook for. Must be a functional {@code interface}.
     * @param eventListenerMerger The merging function to use for merging event listeners' results.
     * @param eventSide The side on which the event should be registered. Determines the side on which listeners pertaining
     *                  to the provided {@code eventInterface} will run.
     *
     * @param <T> The event {@code interface} type.
     *
     * @throws IllegalArgumentException If the provided {@code eventInterface} is not a functional {@code interface}.
     *
     * @see #registerEventHook(EventKey, Function, ModSide)
     * @see #registerEventHook(EventBlueprint)
     */
    default <T> void registerEventHook(Class<T> eventInterface, Function<T[], T> eventListenerMerger, ModSide eventSide) {
        registerEventHook(new EventKey<>(eventInterface), eventListenerMerger, eventSide);
    }

    /**
     * Overloaded variant of {@link #registerEventHook(EventKey, Function, ModSide)}. Registers an event hook using the
     * provided {@link EventBlueprint}.
     *
     * @param eventBlueprint The {@link EventBlueprint} to register the hook for.
     *
     * @param <T> The event {@code interface} type.
     *
     * @throws IllegalArgumentException If the provided {@code eventInterface} is not a functional {@code interface}.
     */
    default <T> void registerEventHook(EventBlueprint<T> eventBlueprint) {
        registerEventHook(new EventKey<>(eventBlueprint.getEventInterface(), eventBlueprint.getActualEventType()), eventBlueprint::mergeListeners, eventBlueprint.getEventSide());
    }

    /**
     * Registers an event listener for the provided event {@code interface}.
     *
     * @param eventInterface The event {@code interface} to register the listener for. Must be a functional {@code interface}.
     * @param listener The listener instance to register.
     * @param listenerExecutionSide The side on which the listener should run.
     * @param listenerPriority The priority of the listener. Lower priority values are executed first.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     */
    <T> void onEvent(Class<T> eventInterface, T listener, ModSide listenerExecutionSide, int listenerPriority, Class<?>... associatedEventTypes);

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int, Class[])}. Registers an event listener for the provided
     * event {@code interface} with the provided listener priority on {@link ModSide#COMMON}.
     *
     * @param eventInterface The event {@code interface} to register the listener for. Must be a functional {@code interface}.
     * @param listener The listener instance to register.
     * @param listenerPriority The priority of the listener. Lower priority values are executed first.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(Class<T> eventInterface, T listener, int listenerPriority, Class<?>... associatedEventTypes) {
        onEvent(eventInterface, listener, ModSide.COMMON, listenerPriority, associatedEventTypes);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int, Class[])}. Registers an event listener for the provided
     * event {@code interface} with the provided listener priority on the provided {@code listenerExecutionSide}. Infers
     * the event {@code interface} from the provided listener using {@link ReflectionUtil#getFunctionalInterfaceClass(Object)}.
     *
     * @param listener The listener instance to register.
     * @param listenerPriority The priority of the listener. Lower priority values are executed first.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener, ModSide listenerExecutionSide, int listenerPriority, Class<?>... associatedEventTypes) {
        onEvent(ReflectionUtil.getFunctionalInterfaceClass(listener), listener, listenerExecutionSide, listenerPriority, associatedEventTypes);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int, Class[])}. Registers an event listener for the provided
     * event {@code interface} with the default priority of 0 on {@link ModSide#COMMON}. Infers the event
     * {@code interface} from the provided listener using {@link ReflectionUtil#getFunctionalInterfaceClass(Object)}.
     *
     * @param listener The listener instance to register.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(Class<T> eventInterface, T listener, Class<?>... associatedEventTypes) {
        onEvent(eventInterface, listener, 0, associatedEventTypes);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int, Class[])}. Registers an event listener for the provided
     * event {@code interface} with the default priority of 0. Infers the event
     * {@code interface} from the provided listener using {@link ReflectionUtil#getFunctionalInterfaceClass(Object)}.
     *
     * @param listener The listener instance to register.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener, ModSide listenerExecutionSide, Class<?>... associatedEventTypes) {
        onEvent(listener, listenerExecutionSide, 0, associatedEventTypes);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int, Class[])}. Registers an event listener for the provided
     * event {@code interface} with the provided listener priority on {@link ModSide#COMMON}. Infers the event
     * {@code interface} from the provided listener using {@link ReflectionUtil#getFunctionalInterfaceClass(Object)}.
     *
     * @param listener The listener instance to register.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener, int listenerPriority, Class<?>... associatedEventTypes) {
        onEvent(listener, ModSide.COMMON, listenerPriority, associatedEventTypes);
    }

    /**
     * Overloaded variant of {@link #onEvent(Class, T, ModSide, int, Class[])}. Registers an event listener for the provided
     * event {@code interface} with the default priority of 0. Infers the event {@code interface} from the provided
     * listener using {@link ReflectionUtil#getFunctionalInterfaceClass(Object)}.
     *
     * @param listener The listener instance to register.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     */
    default <T> void onEvent(T listener, Class<?>... associatedEventTypes) {
        onEvent(listener, 0, associatedEventTypes);
    }

    /**
     * Fires an event for the provided event {@code interface} on the provided {@code eventSide}. Infers the event
     * {@code interface} from the provided listener using {@link ReflectionUtil#getFunctionalInterfaceClass(Object)}.
     *
     * @param eventInterface The event {@code interface} to fire.
     * @param eventListenerInvokerMapper The invoker mapper to use for firing the event. Used to be able to query results
     *                                   from the finalized listener invoker when it's run.
     * @param eventSide The side on which the event should be fired.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     *
     * @return The result of the event listener invoker mapper. May be {@code null}.
     */
    @Nullable
    <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper, ModSide eventSide, Class<?>... associatedEventTypes);

    /**
     * Overloaded variant of {@link #fireEventHook(Class, Function, ModSide, Class[])}. Fires an event for the provided event
     * {@code interface} on {@link ModSide#COMMON}, if possible. Infers the event {@code interface} from the provided
     * listener using {@link ReflectionUtil#getFunctionalInterfaceClass(Object)}.
     *
     * @param eventInterface The event {@code interface} to fire.
     * @param eventListenerInvokerMapper The invoker mapper to use for firing the event. Used to be able to query results
     *                                   from the finalized listener invoker when it's run.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     *
     * @return The result of the event listener invoker mapper. May be {@code null}.
     */
    @Nullable
    default <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper, Class<?>... associatedEventTypes) {
        return fireEventHook(eventInterface, eventListenerInvokerMapper, ModSide.COMMON, associatedEventTypes);
    }

    /**
     * Gets a copy of the {@link Map} of listeners for the provided event {@code interface}. The resultant {@link Map}
     * is sorted by priority in ascending order.
     *
     * @param eventInterface The event {@code interface} to get listeners for.
     * @param associatedEventTypes An optional array of types to associate the {@code eventInterface} with, for lookup.
     *                             Primarily useful in cases where unresolvable generic types are present in the event
     *                             {@code interface}.
     *
     * @param <T> The event {@code interface} type.
     *
     * @return A map of listeners for the provided event {@code interface}, sorted by priority in ascending order
     */
    <T> Map<Integer, List<T>> getListenersFor(Class<T> eventInterface, Class<?>... associatedEventTypes);

    /**
     * Identifier data-holding {@code class} used for event type lookup in loader-specific implementations of {@link EventBus}.
     * Used instead of direct {@link Class} objects in order to account for generic types or similar that may need to be
     * associated with a given event {@code interface}.
     *
     * @param <T> The event {@code interface} type. Must be functional.
     */
    class EventKey<T> {
        protected final Class<T> eventInterface;
        protected final List<Class<?>> associatedTypes = new ObjectArrayList<>();

        public EventKey(Class<T> eventInterface, Class<?>... associatedTypes) {
            if (!eventInterface.isInterface() || Arrays.stream(eventInterface.getMethods()).filter(curMethod -> Modifier.isAbstract(curMethod.getModifiers()) && !Modifier.isStatic(curMethod.getModifiers())).count() != 1) {
                throw new IllegalArgumentException(String.format("Attempted to declare event of type %s, but it isn't a functional interface!", eventInterface.getName()));
            }

            this.eventInterface = eventInterface;
            this.associatedTypes.addAll(List.of(associatedTypes));
        }

        /**
         * Gets the wrapped functional event {@code interface}.
         *
         * @return The event {@code interface}.
         */
        public Class<T> getEventInterface() {
            return eventInterface;
        }

        /**
         * Retrieves an immutable copy of all associated types.
         *
         * @return An immutable copy of all associated types.
         */
        public List<Class<?>> getAssociatedTypes() {
            return ImmutableList.copyOf(associatedTypes);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EventKey<T> eventKey = (EventKey<T>) o;

            return Objects.equals(eventInterface, eventKey.eventInterface) && Objects.equals(associatedTypes, eventKey.associatedTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventInterface, associatedTypes);
        }
    }
}