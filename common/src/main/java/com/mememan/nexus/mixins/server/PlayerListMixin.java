package com.mememan.nexus.mixins.server;

import com.mememan.nexus.template.event.blueprint.common.PlayerEventBlueprint;
import com.mememan.nexus.template.event.blueprint.server.ServerLifeCycleEventBlueprint;
import com.mememan.nexus.template.event.def.common.PlayerEvent;
import com.mememan.nexus.template.event.def.server.ServerLifeCycleEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin {@code class} to handle proper syncing of resource reload listener data for the appropriate
 * {@link PreparableReloadListener} instances by firing relevant event hooks. Also handles some additional player-related
 * event hooks.
 *
 * @see ServerLifeCycleEventBlueprint#DATAPACK_SYNC
 * @see ServerLifeCycleEventBlueprint#DATAPACK_INDIVIDUAL_SYNC
 * @see PlayerEventBlueprint#PLAYER_LOGIN
 * @see PlayerEventBlueprint#PLAYER_LOGOUT
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    @Final
    private List<ServerPlayer> players;

    private PlayerListMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (PlayerListMixin)");
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 5, shift = At.Shift.AFTER))
    private void nexus$handleSyncEventHooksForNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        ServerLifeCycleEventBlueprint.DATAPACK_SYNC.fireEvent(new ServerLifeCycleEvent.DataPackSyncEvent(server, server.getResourceManager(), (PlayerList) (Object) this, player));
        ServerLifeCycleEventBlueprint.DATAPACK_INDIVIDUAL_SYNC.fireEvent(new ServerLifeCycleEvent.DataPackIndividualSyncEvent(server, server.getResourceManager(), (PlayerList) (Object) this, player, true));
    }

    @Inject(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void nexus$handleSyncEventHooksForAllPlayers(CallbackInfo ci) {
        for (ServerPlayer targetPlayer : players) {
            ServerLifeCycleEventBlueprint.DATAPACK_INDIVIDUAL_SYNC.fireEvent(new ServerLifeCycleEvent.DataPackIndividualSyncEvent(server, server.getResourceManager(), (PlayerList) (Object) this, targetPlayer, false));
        }

        ServerLifeCycleEventBlueprint.DATAPACK_SYNC.fireEvent(new ServerLifeCycleEvent.DataPackSyncEvent(server, server.getResourceManager(), (PlayerList) (Object) this, null));
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void nexus$handlePlayerLoginEventHook(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        PlayerEventBlueprint.PLAYER_LOGIN.fireEvent(new PlayerEvent.PlayerLoginEvent(player));
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void nexus$handlePlayerLogoutEventHook(ServerPlayer player, CallbackInfo ci) {
        PlayerEventBlueprint.PLAYER_LOGOUT.fireEvent(new PlayerEvent.PlayerLogoutEvent(player));
    }
}
