package com.mememan.nexus.resource.config;

import com.mememan.nexus.resource.reload_listeners.DefaultedCodecResourceReloadListener;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
