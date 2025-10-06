package com.mememan.nexus.mixins.server;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncPacket;
import com.mememan.nexus.internal.services.FabricRegistrar;
import com.mememan.nexus.mixins.fabric_api.resource_loader.ResourceManagerHelperImplMixin;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Mixin {@code class} to handle proper syncing of resource reload listener data for the appropriate
 * {@link PreparableReloadListener} instances.
 *
 * @see ResourceManagerHelperImplMixin
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    private PlayerListMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (PlayerListMixin)");
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 5, shift = At.Shift.AFTER))
    private void nexus$syncResourcesForNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        FabricRegistrar.getCachedResourceReloadListeners().entrySet()
                .stream()
                .filter(curEntry -> curEntry.getValue().second()
                        .map(curConfig -> curConfig.listenerPackType() == PackType.SERVER_DATA && curConfig.shouldSyncToClient())
                        .orElse(false))
                .forEach((curEntry) -> {
                    ResourceLocation curListenerId = curEntry.getKey();
                    Pair<PreparableReloadListener, Optional<ResourceReloadListenerConfig<PreparableReloadListener>>> curListenerPair = curEntry.getValue();
                    PreparableReloadListener listener = curListenerPair.first();
                    ResourceReloadListenerConfig<PreparableReloadListener> listenerConfig = curListenerPair.second().get();
                    Optional<Function<PreparableReloadListener, Map<ResourceLocation, ?>>> listenerDataMapper = listenerConfig.dataMapGetter();

                    listenerDataMapper.ifPresentOrElse(dataMapper -> {
                        DatapackEntriesSyncPacket<?> syncPacket = new DatapackEntriesSyncPacket<>(curListenerId, dataMapper.apply(listener));

                        NexusServices.NETWORK_MANAGER.sendToClient(syncPacket, player);
                    }, () -> NexusConstants.LOGGER.warn("Skipping syncing resource reload listener of id '{}' due to missing data mapper (for player of UUID: {}).", curListenerId, player == null ? "null" : player.getUUID()));
                });
    }

    @Inject(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void nexus$syncResourcesForAllPlayers(CallbackInfo ci) {
        FabricRegistrar.getCachedResourceReloadListeners().entrySet()
                .stream()
                .filter(curEntry -> curEntry.getValue().second()
                        .map(curConfig -> curConfig.listenerPackType() == PackType.SERVER_DATA && curConfig.shouldSyncToClient())
                        .orElse(false))
                .forEach((curEntry) -> {
                    ResourceLocation curListenerId = curEntry.getKey();
                    Pair<PreparableReloadListener, Optional<ResourceReloadListenerConfig<PreparableReloadListener>>> curListenerPair = curEntry.getValue();
                    PreparableReloadListener listener = curListenerPair.first();
                    ResourceReloadListenerConfig<PreparableReloadListener> listenerConfig = curListenerPair.second().get();
                    Optional<Function<PreparableReloadListener, Map<ResourceLocation, ?>>> listenerDataMapper = listenerConfig.dataMapGetter();

                    listenerDataMapper.ifPresentOrElse(dataMapper -> {
                        DatapackEntriesSyncPacket<?> syncPacket = new DatapackEntriesSyncPacket<>(curListenerId, dataMapper.apply(listener));

                        NexusServices.NETWORK_MANAGER.sendToAllClients(syncPacket);
                    }, () -> NexusConstants.LOGGER.warn("Skipping syncing resource reload listener of id '{}' due to missing data mapper (for all players).", curListenerId));
                });
    }
}
