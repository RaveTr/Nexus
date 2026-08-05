package com.mememan.nexus.internal.network.packets.s2c;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.network.PacketContext;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import com.mememan.nexus.template.event.blueprint.common.PlayerEventBlueprint;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Optional;

public class DatapackEntriesSyncChunkPacket<T> {
    private static final Map<ResourceLocation, Object> CLIENT_CHUNK_DATA_STORE = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()); // Cuz Fabric loves being quirky n different by running its stuff on the netty thread instead of the main thread at the target side (bruh)
    private final ResourceLocation listenerId;
    private final Map<ResourceLocation, T> chunkedEntriesToSync;
    private final int chunkIdx;
    private final boolean isTerminalChunk;

    public DatapackEntriesSyncChunkPacket(ResourceLocation listenerId, Map<ResourceLocation, T> chunkedEntriesToSync, int chunkIdx, boolean isTerminalChunk) {
        this.listenerId = listenerId;
        this.chunkedEntriesToSync = chunkedEntriesToSync;
        this.chunkIdx = chunkIdx;
        this.isTerminalChunk = isTerminalChunk;
    }

    public static <T> DatapackEntriesSyncChunkPacket<T> decode(FriendlyByteBuf buf) {
        ResourceLocation listenerId = buf.readResourceLocation();

        return new DatapackEntriesSyncChunkPacket<>(listenerId, buf.readMap(Object2ObjectOpenHashMap::new, FriendlyByteBuf::readResourceLocation, (localBuf) -> localBuf.readWithCodec(NbtOps.INSTANCE, getCodecFromListenerById(listenerId))), buf.readVarInt(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(listenerId);
        buf.writeMap(chunkedEntriesToSync, FriendlyByteBuf::writeResourceLocation, (localBuf, value) -> localBuf.writeWithCodec(NbtOps.INSTANCE, getCodecFromListenerById(listenerId), value));
        buf.writeVarInt(chunkIdx);
        buf.writeBoolean(isTerminalChunk);
    }

    public PacketContext handlePacket() {
        return (ownerPlayer, curLevel, curConnection, curSide) -> {
            Pair<PreparableReloadListener, Optional<ResourceReloadListenerConfig<PreparableReloadListener>>> targetListenerPair = NexusServices.REGISTRAR.getMappedResourceReloadListeners().get(listenerId);
            PreparableReloadListener targetListenerConfig = targetListenerPair.first();

            targetListenerPair.second().ifPresentOrElse(reloadListenerConfig -> {
                try {
                    if (chunkedEntriesToSync.isEmpty()) NexusConstants.LOGGER.warn("Datapack chunk with index {} (from listener of id: {}) was empty.", chunkIdx, listenerId);
                    else CLIENT_CHUNK_DATA_STORE.putAll(chunkedEntriesToSync);
                } catch (Throwable t) {
                    NexusConstants.LOGGER.warn("Failed to enqueue datapack chunk with index {} (from listener of id: {}) on client for syncing, but an exception/error was caught (see stacktrace below). Chunk may be partially or completely unsynced.", chunkIdx, listenerId, t);
                }

                if (isTerminalChunk) {
                    try {
                        reloadListenerConfig.resourceSyncOperation().ifPresent(syncOp -> syncOp.accept(targetListenerConfig, CLIENT_CHUNK_DATA_STORE));
                    } finally {
                        CLIENT_CHUNK_DATA_STORE.clear();
                    }
                }
            }, () -> NexusConstants.LOGGER.warn("Somehow failed to run data syncing operation for listener of id {}, listener config was unmapped/empty.", listenerId));
        };
    }

    public static <T> Codec<T> getCodecFromListenerById(ResourceLocation listenerId) {
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

    @PostInit
    private static class ClientDatapackChunkHandler {

        static {
            PlayerEventBlueprint.PLAYER_DISCONNECT.onEvent(event -> {
                CLIENT_CHUNK_DATA_STORE.clear();
            });
        }
    }
}
