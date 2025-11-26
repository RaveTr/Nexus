package com.mememan.nexus.event.blueprint;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;

public abstract class ConcretePropagatingEventBlueprint<BE extends BaseEvent> extends PropagatingEventBlueprint<BE, BE> {

    protected ConcretePropagatingEventBlueprint(Class<BE> eventInterface, BE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }
}
