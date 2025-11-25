package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.event.base.ConcreteEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.TickEvent;

public class TickEventBlueprint<TE extends TickEvent> extends ConcreteEventBlueprint<TE> {

    protected TickEventBlueprint(Class<TE> eventInterface, TE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    @Override
    protected TE mergeListenerResults(TE curResult, TE newResult) {
        return null;
    }

    public static class Client {

        public static class Pre {

        }

        public static class Post {

        }
    }
}
