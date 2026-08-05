package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.event.blueprint.ConcreteEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.EntityEvent;

public class EntityEventBlueprint<EE extends EntityEvent> extends ConcreteEventBlueprint<EE> {

    protected EntityEventBlueprint(Class<EE> eventInterface, EE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }
}
