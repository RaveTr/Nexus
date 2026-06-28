package com.mememan.nexus.template.event.blueprint.server;

import com.mememan.nexus.event.blueprint.ConcreteEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.server.ServerLifeCycleEvent;

public abstract class ServerLifeCycleEventBlueprint<SLCE extends ServerLifeCycleEvent> extends ConcreteEventBlueprint<SLCE> {
    public static final ServerStartingEventBlueprint SERVER_STARTING = new ServerStartingEventBlueprint();
    public static final ServerStartedEventBlueprint SERVER_STARTED = new ServerStartedEventBlueprint();
    public static final ServerStoppingEventBlueprint SERVER_STOPPING = new ServerStoppingEventBlueprint();
    public static final ServerStoppedEventBlueprint SERVER_STOPPED = new ServerStoppedEventBlueprint();
    public static final DataPackReloadStartEventBlueprint DATAPACK_RELOAD_START = new DataPackReloadStartEventBlueprint();
    public static final DataPackReloadEndEventBlueprint DATAPACK_RELOAD_END = new DataPackReloadEndEventBlueprint();
    public static final DataPackSyncEventBluePrint DATAPACK_SYNC = new DataPackSyncEventBluePrint();
    public static final DataPackIndividualSyncEventBluePrint DATAPACK_INDIVIDUAL_SYNC = new DataPackIndividualSyncEventBluePrint();

    protected ServerLifeCycleEventBlueprint(Class<SLCE> eventInterface, SLCE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }
    
    public static class ServerStartingEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStartingEvent>  {

        protected ServerStartingEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStartingEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class ServerStartedEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStartedEvent>  {

        protected ServerStartedEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStartedEvent.class, null, false, ModSide.COMMON);
        }
    }

    public static class ServerStoppingEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStoppingEvent>  {

        protected ServerStoppingEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStoppingEvent.class, null, false, ModSide.COMMON);
        }
    }

    public static class ServerStoppedEventBlueprint extends ServerLifeCycleEventBlueprint<ServerLifeCycleEvent.ServerStoppedEvent>  {

        protected ServerStoppedEventBlueprint() {
            super(ServerLifeCycleEvent.ServerStoppedEvent.class, null, false, ModSide.COMMON);
        }
    }

    public static class DataPackEventBlueprint<DPE extends ServerLifeCycleEvent.DataPackEvent> extends ServerLifeCycleEventBlueprint<DPE> {

        protected DataPackEventBlueprint(Class<DPE> eventInterface, DPE defaultResult, boolean isCancellable, ModSide eventSide) {
            super(eventInterface, defaultResult, isCancellable, eventSide);
        }
    }

    public static class DataPackReloadStartEventBlueprint extends DataPackEventBlueprint<ServerLifeCycleEvent.DataPackReloadStartEvent> {

        protected DataPackReloadStartEventBlueprint() {
            super(ServerLifeCycleEvent.DataPackReloadStartEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class DataPackReloadEndEventBlueprint extends DataPackEventBlueprint<ServerLifeCycleEvent.DataPackReloadEndEvent> {

        protected DataPackReloadEndEventBlueprint() {
            super(ServerLifeCycleEvent.DataPackReloadEndEvent.class, null, false, ModSide.COMMON);
        }
    }


    public static class DataPackSyncEventBluePrint extends DataPackEventBlueprint<ServerLifeCycleEvent.DataPackSyncEvent> {

        protected DataPackSyncEventBluePrint() {
            super(ServerLifeCycleEvent.DataPackSyncEvent.class, null, false, ModSide.COMMON);
        }
    }

    public static class DataPackIndividualSyncEventBluePrint extends DataPackEventBlueprint<ServerLifeCycleEvent.DataPackIndividualSyncEvent> {

        protected DataPackIndividualSyncEventBluePrint() {
            super(ServerLifeCycleEvent.DataPackIndividualSyncEvent.class, null, false, ModSide.COMMON);
        }
    }
}
