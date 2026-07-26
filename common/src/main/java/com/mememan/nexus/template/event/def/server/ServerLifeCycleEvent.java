package com.mememan.nexus.template.event.def.server;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public abstract class ServerLifeCycleEvent extends BaseEvent {
    protected final MinecraftServer targetServer;

    public ServerLifeCycleEvent(MinecraftServer targetServer) {
        super(ModSide.COMMON);

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

    public static abstract class DataPackEvent extends ServerLifeCycleEvent {
        protected final ResourceManager serverResourceManager;

        public DataPackEvent(MinecraftServer targetServer, ResourceManager serverResourceManager) {
            super(targetServer);

            this.serverResourceManager = serverResourceManager;
        }

        public ResourceManager getServerResourceManager() {
            return serverResourceManager;
        }

        @Override
        public boolean readyForTicking() {
            return true; // Doesn't matter that the server thread itself is blocked; the server has always been ready to tick once it fully initialized everything
        }
    }

    public static class DataPackReloadStartEvent extends DataPackEvent {

        public DataPackReloadStartEvent(MinecraftServer targetServer, ResourceManager serverResourceManager) {
            super(targetServer, serverResourceManager);
        }
    }

    public static class DataPackReloadEndEvent extends DataPackEvent {
        protected final boolean successfullyReloaded;

        public DataPackReloadEndEvent(MinecraftServer targetServer, ResourceManager serverResourceManager, boolean successfullyReloaded) {
            super(targetServer, serverResourceManager);

            this.successfullyReloaded = successfullyReloaded;
        }

        public boolean hasSuccessfullyReloaded() {
            return successfullyReloaded;
        }
    }

    public static class DataPackSyncEvent extends DataPackEvent {
        protected final PlayerList playerList;
        @Nullable
        protected final ServerPlayer targetPlayer;

        public DataPackSyncEvent(MinecraftServer targetServer, ResourceManager serverResourceManager, PlayerList playerList, @Nullable ServerPlayer targetPlayer) {
            super(targetServer, serverResourceManager);

            this.playerList = playerList;
            this.targetPlayer = targetPlayer;
        }

        public PlayerList getPlayerList() {
            return playerList;
        }

        public @Nullable ServerPlayer getTargetPlayer() {
            return targetPlayer;
        }
    }

    public static class DataPackIndividualSyncEvent extends DataPackSyncEvent {
        protected final boolean justJoined;

        public DataPackIndividualSyncEvent(MinecraftServer targetServer, ResourceManager serverResourceManager, PlayerList playerList, @Nullable ServerPlayer targetPlayer, boolean justJoined) {
            super(targetServer, serverResourceManager, playerList, targetPlayer);

            this.justJoined = justJoined;
        }

        public boolean playerJustJoined() {
            return justJoined;
        }

        @Contract("->!null")
        @Override
        public ServerPlayer getTargetPlayer() {
            return super.getTargetPlayer();
        }
    }
}
