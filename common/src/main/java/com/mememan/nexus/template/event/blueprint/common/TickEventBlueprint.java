package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.event.blueprint.ConcreteEventBlueprint;
import com.mememan.nexus.event.blueprint.ConcretePropagatingEventBlueprint;
import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.TickEvent;

public class TickEventBlueprint<TE extends BaseEvent> extends ConcretePropagatingEventBlueprint<TE> {

    protected TickEventBlueprint(Class<TE> eventInterface, TE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    @Override
    protected TE mergeListenerResults(TE curResult, TE newResult) {
        return newResult;
    }

    public static class Common extends ConcreteEventBlueprint<TickEvent> {
        public static final Common COMMON = new Common();

        protected Common() {
            super(TickEvent.class, null, false, ModSide.COMMON);
        }

        @Override
        protected TickEvent mergeListenerResults(TickEvent curResult, TickEvent newResult) {
            return newResult;
        }
    }

    public static class Client extends ConcreteEventBlueprint<TickEvent.ClientTickEvent> {
        public static final Client CLIENT = new Client();

        protected Client() {
            super(TickEvent.ClientTickEvent.class, null, false, ModSide.CLIENT);
        }

        @Override
        protected TickEvent.ClientTickEvent mergeListenerResults(TickEvent.ClientTickEvent curResult, TickEvent.ClientTickEvent newResult) {
            return newResult;
        }
    }
}
