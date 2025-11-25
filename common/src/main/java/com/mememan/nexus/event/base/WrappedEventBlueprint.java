package com.mememan.nexus.event.base;

import com.mememan.nexus.event.listener.EventListener;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class WrappedEventBlueprint<T, R> implements EventBlueprint<EventListener<T, R>> {
    protected final Class<T> eventInterface;
    protected final R defaultResult;
    protected final boolean isCancellable;
    protected final ModSide eventSide;

    protected WrappedEventBlueprint(Class<T> eventInterface, R defaultResult, boolean isCancellable, ModSide eventSide) {
        this.eventInterface = eventInterface;
        this.defaultResult = defaultResult;
        this.isCancellable = isCancellable;
        this.eventSide = eventSide;

        NexusServices.EVENT_BUS.registerEventHook(this);
    }

    @Override
    public Class<EventListener<T, R>> getEventInterface() {
        return (Class<EventListener<T, R>>) (Class<?>) EventListener.class;
    }

    @Override
    public @Nullable EventListener<T, R> mergeListeners(EventListener<T, R>[] existingListeners) {
        return listener -> {
            R curResult = defaultResult;
            boolean cancelled = false;

            for (EventListener<T, R> existingListener : existingListeners) {
                EventResult<R> listenerResult = existingListener.getResult(listener);
                curResult = mergeListenerResults(curResult, listenerResult.actualResult());

                if (isCancellable()) {
                    cancelled |= listenerResult.cancelled();

                    if (cancelled && listenerResult.shortCircuit()) break;
                }
            }

            return new EventResult<>(curResult, cancelled);
        };
    }

    protected abstract R mergeListenerResults(R curResult, R newResult);

    @Override
    public ModSide getEventSide() {
        return eventSide;
    }

    @Override
    public <U> U fireEvent(Function<EventListener<T, R>, U> eventMapper) {
        return EventBlueprint.super.fireEvent(eventMapper);
    }

    public boolean isCancellable() {
        return isCancellable;
    }
}
