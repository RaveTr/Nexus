package com.mememan.nexus.internal.logic;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncChunkPacket;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import com.mememan.nexus.template.event.blueprint.server.ServerLifeCycleEventBlueprint;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.Util;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Internal handler {@code class} that handles miscellaneous server-based logic tasks for different parts of Nexus API
 * (primarily via event hooks).
 *
 * @apiNote An assumption is made by this {@code class} that all event handlers routed (registered) through Nexus API
 * that all {@linkplain PreparableReloadListener PreparableReloadListeners} have been registered before post-load
 * state kicks in (i.e. the timing of {@link PostInit}).
 *
 * @see ServerLifeCycleEventBlueprint
 */
@PostInit
public final class NexusServerLifeCycleManager {
    private static final Object2ObjectOpenHashMap<ResourceLocation, Pair<PreparableReloadListener, ResourceReloadListenerConfig<PreparableReloadListener>>> CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS = Util.make(new Object2ObjectOpenHashMap<>(), syncableResourceReloadListenerMap -> {
        NexusServices.REGISTRAR.getMappedResourceReloadListeners().entrySet().stream()
                .filter(curListenerEntry -> curListenerEntry.getValue().second()
                        .map(curListenerConfig -> curListenerConfig.listenerPackType() == PackType.SERVER_DATA && curListenerConfig.shouldSyncToClient())
                        .orElse(false))
                .forEach(curListenerEntry -> syncableResourceReloadListenerMap.putIfAbsent(curListenerEntry.getKey(), ObjectObjectImmutablePair.of(curListenerEntry.getValue().first(), curListenerEntry.getValue().second().get())));
    });
    private static final int MAX_CHUNK_BYTES = 512 * 1024; // 512 KiB
    private static final int MAX_SINGLE_ENTRY_BYTES = (2 * 1024 * 1024) - (256 * 1024); // ~1.75 MiB (a little under 2 MB, which is the size limit imposed by both Varint21LengthFieldPrepender and Varint21FrameDecoder)

    private NexusServerLifeCycleManager() {

    }

    static {
        handleServerResourceReloadListeners();
    }

    private static void handleServerResourceReloadListeners() {
        ServerLifeCycleEventBlueprint.DATAPACK_INDIVIDUAL_SYNC.onEvent(event -> {
            ServerPlayer targetPlayer = event.getTargetPlayer();
            FriendlyByteBuf payloadSizer = new FriendlyByteBuf(Unpooled.buffer());

            CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS.forEach((curListenerId, curListenerPair) -> {
                PreparableReloadListener listener = curListenerPair.first();
                ResourceReloadListenerConfig<PreparableReloadListener> listenerConfig = curListenerPair.second();
                Optional<Function<PreparableReloadListener, Map<ResourceLocation, ?>>> listenerDataMapper = listenerConfig.dataMapGetter();

                listenerDataMapper.ifPresentOrElse(dataMapper -> {
                    Map<ResourceLocation, ?> fullEntries = dataMapper.apply(listener);

                    syncEntriesInChunks(curListenerId, fullEntries, targetPlayer, payloadSizer);
                }, () -> NexusConstants.LOGGER.warn("Skipping syncable resource reload listener of id '{}' due to missing data mapper.", curListenerId));
            });

            payloadSizer.release();
        });
    }

    private static <T> void syncEntriesInChunks(ResourceLocation listenerId, Map<ResourceLocation, T> fullEntries, ServerPlayer targetPlayer, FriendlyByteBuf payloadSizer) {
        if (fullEntries.isEmpty()) {
            DatapackEntriesSyncChunkPacket<T> terminalPacket = new DatapackEntriesSyncChunkPacket<>(listenerId, Map.of(), 0, true);
            NexusServices.NETWORK_MANAGER.sendToClient(terminalPacket, targetPlayer);

            return; // Little extra safeguard to flush whatever the client has JIC
        }

        Map<ResourceLocation, T> curChunk = new Object2ObjectOpenHashMap<>();
        int curChunkBytes = 0;
        int chunkIdx = 0;

        for (Map.Entry<ResourceLocation, T> curEntry : fullEntries.entrySet()) {
            int entrySize = measureEntrySize(payloadSizer, curEntry.getKey(), curEntry.getValue(), listenerId);

            if (curChunk.isEmpty() && entrySize > MAX_SINGLE_ENTRY_BYTES) {
                NexusConstants.LOGGER.warn("Skipping oversized datapack entry '{}' for listener of ID '{}' ({} bytes exceeds max single-entry cap of {} bytes).", curEntry.getKey(), listenerId, entrySize, MAX_SINGLE_ENTRY_BYTES);
                continue;
            }

            if (!curChunk.isEmpty() && !validateBufferCap(curChunkBytes, entrySize)) {
                DatapackEntriesSyncChunkPacket<T> chunkPacket = new DatapackEntriesSyncChunkPacket<>(listenerId, curChunk, chunkIdx++, false);
                NexusServices.NETWORK_MANAGER.sendToClient(chunkPacket, targetPlayer);

                curChunk = new Object2ObjectOpenHashMap<>();
                curChunkBytes = 0;
            }

            curChunk.put(curEntry.getKey(), curEntry.getValue());
            curChunkBytes += entrySize;
        }

        DatapackEntriesSyncChunkPacket<T> terminalPacket = new DatapackEntriesSyncChunkPacket<>(listenerId, curChunk, chunkIdx, true);
        NexusServices.NETWORK_MANAGER.sendToClient(terminalPacket, targetPlayer);
    }

    private static <T> int measureEntrySize(FriendlyByteBuf payloadSizer, ResourceLocation key, T value, ResourceLocation listenerId) { // TODO This can probably be optimized
        int before = payloadSizer.writerIndex();

        payloadSizer.writeResourceLocation(key);
        payloadSizer.writeWithCodec(NbtOps.INSTANCE, DatapackEntriesSyncChunkPacket.getCodecFromListenerById(listenerId), value);

        int size = payloadSizer.writerIndex() - before;

        payloadSizer.clear();

        return size;
    }

    private static boolean validateBufferCap(int currentChunkBytes, int entrySize) {
        return currentChunkBytes + entrySize <= MAX_CHUNK_BYTES;
    }
}
