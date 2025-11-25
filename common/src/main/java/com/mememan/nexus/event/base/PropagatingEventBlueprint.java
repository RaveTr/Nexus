package com.mememan.nexus.event.base;

import com.mememan.nexus.event.listener.EventListener;
import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.loader.ModSide;

import java.util.function.Function;

public abstract class PropagatingEventBlueprint<BE extends BaseEvent, R> extends BaseEventBlueprint<BE, R> {

    protected PropagatingEventBlueprint(Class<BE> eventInterface, R defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    @Override
    public <U> U fireEvent(Function<EventListener<BE, R>, U> eventMapper) {
        return super.fireEvent(eventMapper);
    }

    @Override
    public EventResult<R> fireEvent(BE event) {
        return super.fireEvent(event);
    }
}
