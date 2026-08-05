package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.PlayerEvent;

public class PlayerEventBlueprint<PE extends PlayerEvent> extends EntityEventBlueprint<PE> {
    public static final PlayerLoginEventBlueprint PLAYER_LOGIN = new PlayerLoginEventBlueprint();
    public static final PlayerLogoutEventBlueprint PLAYER_LOGOUT = new PlayerLogoutEventBlueprint();
    public static final PlayerDisconnectEventBlueprint PLAYER_DISCONNECT = new PlayerDisconnectEventBlueprint();

    protected PlayerEventBlueprint(Class<PE> eventInterface, PE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    public static class PlayerLoginEventBlueprint extends PlayerEventBlueprint<PlayerEvent.PlayerLoginEvent> {

        protected PlayerLoginEventBlueprint() {
            super(PlayerEvent.PlayerLoginEvent.class, null, false, ModSide.COMMON);
        }
    }

    public static class PlayerLogoutEventBlueprint extends PlayerEventBlueprint<PlayerEvent.PlayerLogoutEvent> {

        protected PlayerLogoutEventBlueprint() {
            super(PlayerEvent.PlayerLogoutEvent.class, null, false, ModSide.COMMON);
        }
    }

    public static class PlayerDisconnectEventBlueprint extends PlayerEventBlueprint<PlayerEvent.PlayerDisconnectEvent> {

        protected PlayerDisconnectEventBlueprint() {
            super(PlayerEvent.PlayerDisconnectEvent.class, null, false, ModSide.CLIENT);
        }
    }
}
