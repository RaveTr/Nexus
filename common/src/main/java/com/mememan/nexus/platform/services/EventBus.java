package com.mememan.nexus.platform.services;

import com.mememan.nexus.event.base.EventBlueprint;
import com.mememan.nexus.loader.ModSide;

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
 */
public interface EventBus {


    <T> void onEvent(Class<T> eventInterface, T listener, ModSide listenerExecutionSide, int listenerPriority);

    default <T> void onEvent(Class<T> eventInterface, T listener, int listenerPriority) {
        onEvent(eventInterface, listener, ModSide.COMMON, listenerPriority);
    }

    default <T> void onEvent(T listener, ModSide listenerExecutionSide, int listenerPriority) {
        onEvent((Class<T>) listener.getClass(), listener, listenerExecutionSide, listenerPriority);
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

    <T> void fireEventHook(Class<T> eventInterface, Function<T[], T> eventReturnValueMapper, ModSide eventSide);

    default <T> void fireEventHook(EventBlueprint<T> eventBlueprint) {
        fireEventHook(eventBlueprint.getEventInterface(), eventBlueprint::mergeListeners, eventBlueprint.getEventSide());
    }
}