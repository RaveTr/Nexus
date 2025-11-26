package com.mememan.nexus.event.blueprint;

import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface EventBlueprint<T> {

    Class<T> getEventInterface();

    default Class<?> getActualEventType() {
        return getEventInterface();
    }

    @Nullable
    T mergeListeners(T[] existingListeners);

    ModSide getEventSide();

    default <R> R fireEvent(Function<T, R> eventMapper) {
        return NexusServices.EVENT_BUS.fireEventHook(getEventInterface(), eventMapper, getEventSide(), getActualEventType());
    }

    default void onEvent(T listener, int listenerPriority) {
        NexusServices.EVENT_BUS.onEvent(getEventInterface(), listener, getEventSide(), listenerPriority, getActualEventType());
    }

    default void onEvent(T listener) {
        onEvent(listener, 0);
    }

    default Map<Integer, List<T>> getListeners() {
        return NexusServices.EVENT_BUS.getListenersFor(getEventInterface(), getActualEventType());
    }

    default List<T> getListeners(int priority) {
        return getListeners().get(priority);
    }

    default List<T> getAllListeners() {
        return getListeners().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
