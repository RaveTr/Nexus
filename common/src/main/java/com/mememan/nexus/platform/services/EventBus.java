package com.mememan.nexus.platform.services;

import com.mememan.nexus.event.base.EventBlueprint;
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
 * by Nexus API).
 * <br></br>
 * This exists as a service in order to allow for proper interfacing with each modloader's event system/API.
 */
public interface EventBus {

    <T> void registerEventHook(Class<T> eventInterface, Function<T[], T> eventListenerMerger, ModSide eventSide);

    default <T> void registerEventHook(EventBlueprint<T> eventBlueprint) {
        registerEventHook(eventBlueprint.getEventInterface(), eventBlueprint::mergeListeners, eventBlueprint.getEventSide());
    }

    <T> void onEvent(Class<T> eventInterface, T listener, ModSide listenerExecutionSide, int listenerPriority);

    default <T> void onEvent(Class<T> eventInterface, T listener, int listenerPriority) {
        onEvent(eventInterface, listener, ModSide.COMMON, listenerPriority);
    }

    default <T> void onEvent(T listener, ModSide listenerExecutionSide, int listenerPriority) {
        onEvent(getFunctionalInterfaceClass(listener), listener, listenerExecutionSide, listenerPriority);
    }

    default <T> void onEvent(Class<T> eventInterface, T listener) {
        onEvent(eventInterface, listener, 0);
    }

    default <T> void onEvent(T listener, ModSide listenerExecutionSide) {
        onEvent(listener, listenerExecutionSide, 0);
    }

    default <T> void onEvent(T listener, int listenerPriority) {
        onEvent(listener, ModSide.COMMON, listenerPriority);
    }

    default <T> void onEvent(T listener) {
        onEvent(listener, 0);
    }

    @Nullable
    <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper, ModSide eventSide);

    @Nullable
    default <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper) {
        return fireEventHook(eventInterface, eventListenerInvokerMapper, ModSide.COMMON);
    }

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

        throw new IllegalArgumentException("Could not determine functional interface for " + lambda);
    }
}