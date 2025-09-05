package com.mememan.nexus.resource.config;

import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncPacket;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.resource.reload_listeners.DefaultedCodecResourceReloadListener;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Configurator {@code record} object primarily used in {@link NexusServices#REGISTRAR} in conjunction with
 * {@linkplain PreparableReloadListener PreparableReloadListeners} to streamline extra (basic) operations, such as
 * syncing data to the client automatically and/or specifying which side a reload listener should be registered on.
 *
 * @param listenerPackType The {@link PackType} that the listener will be registered to (resource/data).
 * @param shouldSyncToClient Whether this configurator should mark its associated listener as syncable to the client.
 *                           This will only be effective if {@code listenerPackType} is {@link PackType#SERVER_DATA}.
 * @param dataMapGetter A {@link Function} to specify the data map that should be synced to the client (if the associated
 *                      listener can be synced).
 *                      <br></br>
 *                      The {@link Map} itself is usually comprised of {@linkplain ResourceLocation ResourceLocations}
 *                      (for each file within the listener's target directory) mapped to data deserialized from said
 *                      files. If this is not present, then syncing  will be skipped for the associated listener.
 *                      <br></br>
 *                      Nullity is most likely going to cause crashes from the packet itself since {@code null} cannot
 *                      be de/serialized (obviously), so ensure that this at least outputs an empty {@link Map} by default.
 * @param dataCodecMapper The {@link Codec} used to encode/decode data from the scanned files for the associated listener,
 *                        only really used during syncing. If {@code dataMapGetter}'s presence check (and its preceding
 *                        checks) passes and this is not present, an {@link IllegalArgumentException} will be thrown
 *                        from within the {@link DatapackEntriesSyncPacket} itself.
 * @param resourceSyncOperation A side-safe operation to run on the client once data is received. This should usually
 *                              be a method that updates the client-side data of the associated listener. If this is not
 *                              present, then nothing will be done on the target side.
 *
 * @param <PRL> The type of {@linkplain PreparableReloadListener PreparableReloadListener} that this configurator is
 *              associated with.
 *
 * @see Registrar#registerReloadListener(ResourceLocation, PreparableReloadListener, ResourceReloadListenerConfig)
 * @see DatapackEntriesSyncPacket
 */
public record ResourceReloadListenerConfig<PRL extends PreparableReloadListener>(PackType listenerPackType, boolean shouldSyncToClient, Optional<Function<PRL, Map<ResourceLocation, ?>>> dataMapGetter, Optional<Function<PRL, Codec<?>>> dataCodecMapper, Optional<BiConsumer<PRL, Map<ResourceLocation, ?>>> resourceSyncOperation) {

    public static <T> ResourceReloadListenerConfig<DefaultedCodecResourceReloadListener<T>> createForDefaultable(PackType listenerPackType, boolean shouldSyncToClient) {
        return new ResourceReloadListenerConfig<>(listenerPackType, shouldSyncToClient, Optional.of(DefaultedCodecResourceReloadListener::getMappedObjectData), Optional.of(DefaultedCodecResourceReloadListener::getObjectCodec), Optional.of((targetListener, updatedObjectData) -> targetListener.updateSyncedObjectData((Map<ResourceLocation, T>) updatedObjectData)));
    }

    public static <PRL extends PreparableReloadListener> ResourceReloadListenerConfig<PRL> createDefaultSided(PackType listenerPackType) {
        return new ResourceReloadListenerConfig<>(listenerPackType, false, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static <PRL extends PreparableReloadListener> ResourceReloadListenerConfig<PRL> createSyncable(Function<PRL, Map<ResourceLocation, ?>> dataMapGetter, Function<PRL, Codec<?>> dataCodecMapper, BiConsumer<PRL, Map<ResourceLocation, ?>> resourceSyncOperation) {
        return new ResourceReloadListenerConfig<>(PackType.SERVER_DATA, true, Optional.of(dataMapGetter), Optional.of(dataCodecMapper), Optional.of(resourceSyncOperation));
    }
}
