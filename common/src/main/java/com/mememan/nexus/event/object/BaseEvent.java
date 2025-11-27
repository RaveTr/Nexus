package com.mememan.nexus.event.object;

import com.mememan.nexus.loader.ModSide;

public abstract class BaseEvent {
    protected final ModSide eventSide;

    public BaseEvent(ModSide eventSide) {
        this.eventSide = eventSide;
    }

    public ModSide getEventSide() {
        return eventSide;
    }
}
