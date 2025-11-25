package com.mememan.nexus.event.base;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;

public abstract class ConcreteEventBlueprint<BE extends BaseEvent> extends BaseEventBlueprint<BE, BE> {

    protected ConcreteEventBlueprint(Class<BE> eventInterface, BE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }
}
