package com.mememan.nexus.internal.network.packets.s2c;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.network.PacketContext;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Optional;

public class DatapackEntriesSyncPacket<T> {
    private final ResourceLocation listenerId;
    private final Map<ResourceLocation, T> entriesToSync;

    public DatapackEntriesSyncPacket(ResourceLocation listenerId, Map<ResourceLocation, T> entriesToSync) {
        this.listenerId = listenerId;
        this.entriesToSync = entriesToSync;
    }

    public static <T> DatapackEntriesSyncPacket<T> decode(FriendlyByteBuf buf) {
        ResourceLocation listenerId = buf.readResourceLocation();

        return new DatapackEntriesSyncPacket<>(listenerId, buf.readMap(Object2ObjectOpenHashMap::new, FriendlyByteBuf::readResourceLocation, (localBuf) -> localBuf.readWithCodec(NbtOps.INSTANCE, getCodecFromListenerById(listenerId))));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(listenerId);
        buf.writeMap(entriesToSync, FriendlyByteBuf::writeResourceLocation, (localBuf, value) -> localBuf.writeWithCodec(NbtOps.INSTANCE, getCodecFromListenerById(listenerId), value));
    }

    public PacketContext handlePacket() {
        return (ownerPlayer, curLevel, curConnection, curSide) -> {
            Pair<PreparableReloadListener, Optional<ResourceReloadListenerConfig<PreparableReloadListener>>> targetListenerPair = NexusServices.REGISTRAR.getMappedResourceReloadListeners().get(listenerId);
            PreparableReloadListener targetListenerConfig = targetListenerPair.first();

            targetListenerPair.second().ifPresentOrElse(reloadListenerConfig -> {
                reloadListenerConfig.resourceSyncOperation().ifPresent(syncOp -> syncOp.accept(targetListenerConfig, entriesToSync));
            }, () -> NexusConstants.LOGGER.warn("Somehow failed to run data syncing operation for listener of id '{}', listener config was unmapped/empty.", listenerId));
        };
    }

    protected static <T> Codec<T> getCodecFromListenerById(ResourceLocation listenerId) {
        Pair<PreparableReloadListener, Optional<ResourceReloadListenerConfig<PreparableReloadListener>>> targetListenerPair = NexusServices.REGISTRAR.getMappedResourceReloadListeners().get(listenerId);

        return (Codec<T>) Optional.ofNullable(targetListenerPair)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to sync data for invalid listener of id '%s'", listenerId))) // Buncha fallbacks JIC
                .second()
                .map(curConfig -> {
                    PackType targetPackType = curConfig.listenerPackType();
                    boolean shouldSyncToClient = curConfig.shouldSyncToClient();

                    if (targetPackType == PackType.CLIENT_RESOURCES || !shouldSyncToClient) throw new IllegalArgumentException(String.format("Attempted to sync data for unsyncable reload listener of id '%s' (listener PackType is '%s', shouldSyncToClient is set to: %s)", listenerId, targetPackType, shouldSyncToClient));

                    return curConfig;
                })
                .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to sync data for unsyncable reload listener of id '%s'", listenerId)))
                .dataCodecMapper()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to sync data for listener of id '%s' with no codec mapper", listenerId)))
                .apply(targetListenerPair.first());
    }
}
