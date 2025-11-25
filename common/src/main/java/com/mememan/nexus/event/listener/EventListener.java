package com.mememan.nexus.event.listener;

import com.mememan.nexus.event.result.EventResult;

@FunctionalInterface
public interface EventListener<T, R> {

    EventResult<R> getResult(T event);

    default EventListener<T, R> andThen(EventListener<T, R> subsequentListener) {
        return subsequentListener;
    }
}
