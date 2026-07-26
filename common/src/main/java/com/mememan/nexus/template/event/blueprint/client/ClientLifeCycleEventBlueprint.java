package com.mememan.nexus.template.event.blueprint.client;

import com.mememan.nexus.event.blueprint.ConcreteEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.client.ClientLifeCycleEvent;

public abstract class ClientLifeCycleEventBlueprint<CLCE extends ClientLifeCycleEvent> extends ConcreteEventBlueprint<CLCE> {
    public static final ResourcePackReloadPreStartEventBlueprint RESOURCEPACK_RELOAD_PRE_START = new ResourcePackReloadPreStartEventBlueprint();
    public static final ResourcePackReloadStartEventBlueprint RESOURCEPACK_RELOAD_START = new ResourcePackReloadStartEventBlueprint();
    public static final ResourcePackReloadEndEventBlueprint RESOURCEPACK_RELOAD_END = new ResourcePackReloadEndEventBlueprint();

    protected ClientLifeCycleEventBlueprint(Class<CLCE> eventInterface, CLCE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    public static class ResourcePackEventBlueprint<RPE extends ClientLifeCycleEvent.ResourcePackEvent> extends ClientLifeCycleEventBlueprint<RPE> {

        protected ResourcePackEventBlueprint(Class<RPE> eventInterface, RPE defaultResult, boolean isCancellable, ModSide eventSide) {
            super(eventInterface, defaultResult, isCancellable, eventSide);
        }
    }

    public static class ResourcePackReloadPreStartEventBlueprint extends ResourcePackEventBlueprint<ClientLifeCycleEvent.ResourcePackReloadPreStartEvent> {

        protected ResourcePackReloadPreStartEventBlueprint() {
            super(ClientLifeCycleEvent.ResourcePackReloadPreStartEvent.class, null, true, ModSide.CLIENT);
        }
    }

    public static class ResourcePackReloadStartEventBlueprint extends ResourcePackEventBlueprint<ClientLifeCycleEvent.ResourcePackReloadStartEvent> {

        protected ResourcePackReloadStartEventBlueprint() {
            super(ClientLifeCycleEvent.ResourcePackReloadStartEvent.class, null, false, ModSide.CLIENT);
        }
    }

    public static class ResourcePackReloadEndEventBlueprint extends ResourcePackEventBlueprint<ClientLifeCycleEvent.ResourcePackReloadEndEvent> {

        protected ResourcePackReloadEndEventBlueprint() {
            super(ClientLifeCycleEvent.ResourcePackReloadEndEvent.class, null, false, ModSide.CLIENT);
        }
    }
}
