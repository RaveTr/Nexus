package com.mememan.nexus.template.event.blueprint.server;

import com.mememan.nexus.event.blueprint.ConcreteEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.server.ServerLifeCycleEvent;

public abstract class ServerLifeCycleEventBlueprint<SLCE extends ServerLifeCycleEvent> extends ConcreteEventBlueprint<SLCE> {
    public static final ServerStartingEventBlueprint SERVER_STARTING = new ServerStartingEventBlueprint();
    public static final ServerStartedEventBlueprint SERVER_STARTED = new ServerStartedEventBlueprint();
    public static final ServerStoppingEventBlueprint SERVER_STOPPING = new ServerStoppingEventBlueprint();
    public static final ServerStoppedEventBlueprint SERVER_STOPPED = new ServerStoppedEventBlueprint();

    protected ServerLifeCycleEventBlueprint(Class<SLCE> eventInterface, SLCE defaultResult, ModSide eventSide) {
        super(eventInterface, defaultResult, false, eventSide);
    }

    @Override
    protected SLCE mergeListenerResults(SLCE curResult, SLCE newResult) {
        return newResult;
    }

    public static class ServerStartingEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStartingEvent>  {

        protected ServerStartingEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStartingEvent.class, null, ModSide.COMMON);
        }
    }

    public static class ServerStartedEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStartedEvent>  {

        protected ServerStartedEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStartedEvent.class, null, ModSide.COMMON);
        }
    }

    public static class ServerStoppingEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStoppingEvent>  {

        protected ServerStoppingEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStoppingEvent.class, null, ModSide.COMMON);
        }
    }

    public static class ServerStoppedEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStoppedEvent>  {

        protected ServerStoppedEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStoppedEvent.class, null, ModSide.COMMON);
        }
    }
}
