package com.mememan.nexus.template.event.def.server;

import com.mememan.nexus.event.object.BaseEvent;
import net.minecraft.server.MinecraftServer;

public abstract class ServerLifeCycleEvent extends BaseEvent {
    protected final MinecraftServer targetServer;

    public ServerLifeCycleEvent(MinecraftServer targetServer) {
        this.targetServer = targetServer;
    }

    public MinecraftServer getServer() {
        return targetServer;
    }

    public abstract boolean readyForTicking();

    public static class ServerStartingEvent extends ServerLifeCycleEvent {

        public ServerStartingEvent(MinecraftServer targetServer) {
            super(targetServer);
        }

        @Override
        public boolean readyForTicking() {
            return false;
        }
    }

    public static class ServerStartedEvent extends ServerLifeCycleEvent {

        public ServerStartedEvent(MinecraftServer targetServer) {
            super(targetServer);
        }

        @Override
        public boolean readyForTicking() {
            return true;
        }
    }

    public static class ServerStoppingEvent extends ServerLifeCycleEvent {

        public ServerStoppingEvent(MinecraftServer targetServer) {
            super(targetServer);
        }

        @Override
        public boolean readyForTicking() {
            return false;
        }
    }

    public static class ServerStoppedEvent extends ServerLifeCycleEvent {

        public ServerStoppedEvent(MinecraftServer targetServer) {
            super(targetServer);
        }

        @Override
        public boolean readyForTicking() {
            return false;
        }
    }
}
