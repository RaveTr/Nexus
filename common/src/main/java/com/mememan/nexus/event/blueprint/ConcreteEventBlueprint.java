package com.mememan.nexus.event.blueprint;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;

public abstract class ConcreteEventBlueprint<BE extends BaseEvent> extends BaseEventBlueprint<BE, BE> {

    protected ConcreteEventBlueprint(Class<BE> eventInterface, BE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    @Override
    protected BE mergeListenerResults(BE curResult, BE newResult) {
        return newResult == null ? curResult : newResult;
    }
}
