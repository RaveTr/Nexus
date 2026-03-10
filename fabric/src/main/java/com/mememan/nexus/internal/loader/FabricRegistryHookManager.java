package com.mememan.nexus.internal.loader;

import com.google.common.collect.BiMap;
import com.mememan.nexus.loader.RegistryHookManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FabricRegistryHookManager implements RegistryHookManager {
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS_VIEW = Collections.unmodifiableMap(GLOBAL_APPELLATIONS);

    @Override
    public <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, int id) {

    }

    @Override
    public <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, ResourceLocation registryEntryId) {

    }

    @Override
    public <T> void unblockId(ResourceKey<Registry<T>> targetRegistryKey, int id) {

    }

    @Override
    public <T> void unblockId(ResourceKey<Registry<T>> targetRegistryKey, ResourceLocation registryEntryId) {

    }

    @Override
    public <T> void appellate(ResourceLocation objId, ResourceLocation aliasId, ResourceKey<Registry<T>> targetRegistryKey) {
        GLOBAL_APPELLATIONS
                .computeIfAbsent(targetRegistryKey, k -> new Object2ObjectLinkedOpenHashMap<>())
                .computeIfAbsent(objId, k -> new ObjectArrayList<>())
                .add(aliasId);
    }

    @Override
    public <T> void updateActiveRegistry(ResourceKey<Registry<T>> targetRegistryKey, @Nullable Object2IntMap<ResourceLocation> idPool, ActiveRegistryMapper<T> mapper) {

    }

    @Override
    public <T> void updateActiveRegistry(ResourceKey<Registry<T>> targetRegistryKey, ActiveRegistryMapper<T> mapper) {

    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getBlockedIds() {
        return Map.of();
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getAppellations(boolean computeFromLoaderApi) {
        return GLOBAL_APPELLATIONS_VIEW; // Cuz FAPI has no alias system or similar in their registry sync API
    }
}
