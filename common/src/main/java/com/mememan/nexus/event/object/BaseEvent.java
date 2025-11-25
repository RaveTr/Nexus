package com.mememan.nexus.event.object;

public abstract class BaseEvent {
    protected boolean cancelled = false;

    public BaseEvent() {

    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        setCancelled(true);
    }
}
