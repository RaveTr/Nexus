package com.mememan.nexus.event.blueprint;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.loader.ModSide;

import java.util.function.Consumer;

public abstract class ConcreteEventBlueprint<BE extends BaseEvent> extends BaseEventBlueprint<BE, BE> {

    protected ConcreteEventBlueprint(Class<BE> eventInterface, BE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    public void onEvent(Consumer<BE> eventConsumer) {
        onEvent(eventConsumer, 0);
    }

    public void onEvent(Consumer<BE> eventConsumer, int listenerPriority) {
        onEvent(event -> {
            eventConsumer.accept(event);
            return EventResult.success(event);
        }, listenerPriority);
    }

    @Override
    protected BE mergeListenerResults(BE curResult, BE newResult) {
        return newResult == null ? curResult : newResult;
    }
}
