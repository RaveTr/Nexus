package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.event.blueprint.ConcreteEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.RegistryEvent;

public class RegistryEventBlueprint<RE extends RegistryEvent> extends ConcreteEventBlueprint<RE> {
    public static final MissingRegistryEntriesEventBlueprint MISSING_REGISTRY_ENTRIES = new MissingRegistryEntriesEventBlueprint();

    protected RegistryEventBlueprint(Class<RE> eventInterface, RE defaultResult, ModSide eventSide) {
        super(eventInterface, defaultResult, false, eventSide);
    }

    public static class MissingRegistryEntriesEventBlueprint extends RegistryEventBlueprint<RegistryEvent.MissingRegistryEntriesEvent> {

        protected MissingRegistryEntriesEventBlueprint() {
            super(RegistryEvent.MissingRegistryEntriesEvent.class, null, ModSide.COMMON);
        }
    }
}
