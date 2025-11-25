package com.mememan.nexus.event.base;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.loader.ModSide;

public abstract class BaseEventBlueprint<BE extends BaseEvent, R> extends WrappedEventBlueprint<BE, R> {

    protected BaseEventBlueprint(Class<BE> eventInterface, R defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    public EventResult<R> fireEvent(BE event) {
        return fireEvent(finalListener -> finalListener.getResult(event));
    }
}
