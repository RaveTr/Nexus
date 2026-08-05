package com.mememan.nexus.template.event.def.common;

import com.mememan.nexus.loader.ModSide;
import net.minecraft.world.entity.player.Player;

public class PlayerEvent extends EntityEvent {

    public PlayerEvent(ModSide targetSide, Player targetPlayer) {
        super(targetSide, targetPlayer);
    }

    public PlayerEvent(Player targetPlayer) {
        super(targetPlayer);
    }

    public Player getTargetPlayer() {
        return (Player) super.getTargetEntity();
    }

    public static class PlayerLoginEvent extends PlayerEvent {

        public PlayerLoginEvent(Player targetPlayer) {
            super(targetPlayer);
        }
    }

    public static class PlayerLogoutEvent extends PlayerEvent {

        public PlayerLogoutEvent(Player targetPlayer) {
            super(targetPlayer);
        }
    }

    public static class PlayerDisconnectEvent extends PlayerEvent {
        protected final boolean wasIntegratedServer;
        protected final boolean wasOnRealms;

        public PlayerDisconnectEvent(Player targetPlayer, boolean wasIntegratedServer, boolean wasOnRealms) {
            super(ModSide.CLIENT, targetPlayer);

            this.wasIntegratedServer = wasIntegratedServer;
            this.wasOnRealms = wasOnRealms;
        }

        public boolean wasIntegratedServer() {
            return wasIntegratedServer;
        }

        public boolean wasOnRealms() {
            return wasOnRealms;
        }
    }
}
