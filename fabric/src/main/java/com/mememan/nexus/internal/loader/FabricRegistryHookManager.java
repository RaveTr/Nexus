package com.mememan.nexus.internal.loader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.graph.MutableNetwork;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import com.mememan.nexus.loader.RegistryHookManager;
import com.mememan.nexus.mixins.registries.MappedRegistryAccessor;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.impl.registry.sync.ListenableRegistry;
import net.fabricmc.fabric.impl.registry.sync.RemapStateImpl;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FabricRegistryHookManager implements RegistryHookManager {
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS_VIEW = Collections.unmodifiableMap(GLOBAL_APPELLATIONS);
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> GLOBAL_BLOCKED_IDS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> GLOBAL_BLOCKED_IDS_VIEW = Collections.unmodifiableMap(GLOBAL_BLOCKED_IDS);

    @Override
    public <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, int id) {
        GLOBAL_BLOCKED_IDS
                .computeIfAbsent(targetRegistryKey, k -> HashBiMap.create())
                .put(id, NUMERICALLY_BLOCKED);
    }

    @Override
    public <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, ResourceLocation registryEntryId) {
        GLOBAL_BLOCKED_IDS
                .computeIfAbsent(targetRegistryKey, k -> HashBiMap.create())
                .inverse()
                .put(registryEntryId, RL_BLOCKED);
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
    public <T> void updateActiveRegistryState(ResourceKey<Registry<T>> targetRegistryKey, ActiveRegistryMapper<T> mapper) {
        Registry<T> originalRegistry = BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) targetRegistryKey);
        Map<RawRegistryEntry<T>, RawRegistryEntry<T>> potentialRemaps = RegistryHookManager.gatherPotentialRemaps(originalRegistry, mapper);

        BiConsumer<RawRegistryEntry<T>, MappedRegistryAccessor> regEntryRemover = (entry, accessor) -> {
            ResourceKey<T> entryKey = ResourceKey.create(entry.registryKey(), entry.objId());
            MappedRegistry<T> registry = (MappedRegistry<T>) accessor;

            if (registry.getOptional(entryKey.location()).isEmpty()) return; // Entry already removed, skip

            Holder.Reference<T> holder = registry.getHolderOrThrow(entryKey);

            accessor.nexus$getByLocation().remove(entryKey.location());
            accessor.nexus$getByKey().remove(entryKey);
            accessor.nexus$getByValue().remove(holder.value());

            ObjectList<Holder.Reference<Object>> idList = accessor.nexus$getIdList();
            int numId = entry.numericalId();

            if (numId >= 0 && numId < idList.size()) {
                idList.set(numId, null); // Set to null instead of removing to avoid index shifting
            }

            accessor.nexus$getToId().removeInt(holder.value());

            if (accessor.nexus$getUnregisteredIntrusiveHolders() != null) {
                accessor.nexus$getUnregisteredIntrusiveHolders().put(holder.value(), (Holder.Reference<Object>) holder);
            }
        };

        // TODO Check for whether we're updating at the correct time or not

        if (potentialRemaps.isEmpty()) return; // Nothing to do
        if (!(originalRegistry instanceof MappedRegistry<T> originalMappedRegistry) || !(originalMappedRegistry instanceof MappedRegistryAccessor originalAccessor)) return;

        boolean originallyAllowedIntrusiveHolders = originalAccessor.nexus$getUnregisteredIntrusiveHolders() != null;

        // Bit of a dirty trick: just do all mutations on a copy, then sync everything after validating registry state
        Int2ObjectMap<ResourceLocation> prevIdMap = getIdMap(originalMappedRegistry);
        MappedRegistry<T> tempCopy = new MappedRegistry<>(originalRegistry.key(), Lifecycle.stable(), originallyAllowedIntrusiveHolders);

        if (!(tempCopy instanceof MappedRegistryAccessor tempAccessor)) return;

        // Validate and load registry state
        BitSet availableIds = new BitSet();

        validateRegistryState(originalMappedRegistry);
        loadRegistryState(originalMappedRegistry, tempCopy, availableIds);

        // Resolve remaps
        MutableNetwork<RawRegistryEntry<T>, RemapTarget> remapNetwork = RegistryHookManager.constructRemapNetwork(potentialRemaps, originalMappedRegistry, RemapConflictResolution.SWAP);
        Map<RawRegistryEntry<T>, RawRegistryEntry<T>> resolvedRemaps = RegistryHookManager.resolveRemapTargets(remapNetwork, potentialRemaps);

        // Apply remaps
        for (Map.Entry<RawRegistryEntry<T>, RawRegistryEntry<T>> resolvedRemap : resolvedRemaps.entrySet()) {
            RawRegistryEntry<T> originalEntry = resolvedRemap.getKey();
            RawRegistryEntry<T> remappedEntry = resolvedRemap.getValue();

            ResourceKey<T> remappedEntryKey = ResourceKey.create(remappedEntry.registryKey(), remappedEntry.objId());
            int targetId = remappedEntry.numericalId();

            regEntryRemover.accept(originalEntry, tempAccessor); // Remove the original entry first

            if (tempCopy.byId(targetId) != null) { // Clear target slot if occupied (this might be the same as originalEntry in some cases)
                regEntryRemover.accept(new RawRegistryEntry<>(remappedEntry.registryKey(), tempCopy.getKey(tempCopy.byId(targetId)), tempCopy.byId(targetId), targetId), tempAccessor);
            }

            if (targetId != -1 && availableIds.get(targetId)) availableIds.clear(targetId);
            if (targetId == -1) targetId = availableIds.nextClearBit(0);

            tempCopy.registerMapping(targetId, remappedEntryKey, remappedEntry.objValue(), Lifecycle.stable());
            availableIds.set(targetId);
        }

        ResourceLocation defaultKey = originalMappedRegistry instanceof DefaultedRegistry<?> defReg
                ? defReg.getDefaultKey()
                : null;

        availableIds.stream()
                .filter(id -> tempCopy.byId(id) == null || tempCopy.getId(tempCopy.byId(id)) == -1 || (!Objects.equals(tempCopy.getKey(tempCopy.byId(id)), defaultKey) && tempCopy.getId(tempCopy.byId(id)) == tempCopy.getId(tempCopy.get(defaultKey))))
                .forEach(availableIds::clear);

        // Validate and sync registry state
        try {
            validateRegistryState(tempCopy);
            syncRegistryState(tempCopy, originalMappedRegistry);
        } catch (Exception e) {
            NexusConstants.LOGGER.error("Failed to validate intermediary registry for active registry state '{}'. Skipping state update.", targetRegistryKey.location(), e);
            return;
        }

        if (originalRegistry instanceof ListenableRegistry) { // FAPI compat: Track registry remaps for any listeners that may use them to update internal data (e.g. blockstate to ID)
            Int2IntMap curIdMap = getIdUpdates(originalMappedRegistry, prevIdMap);

            ListenableRegistry.get(originalRegistry).fabric_getRemapEvent().invoker().onRemap(new RemapStateImpl<>(originalRegistry, prevIdMap, curIdMap));
        }

        NexusRegistryDataManager.markRegistryDataDirty();
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getBlockedIds(boolean computeFromLoaderApi) {
        return GLOBAL_BLOCKED_IDS_VIEW; // Cuz FAPI has no ID blocking system or similar in their registry sync API
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getAppellations(boolean computeFromLoaderApi) {
        return GLOBAL_APPELLATIONS_VIEW; // Cuz FAPI has no alias system or similar in their registry sync API
    }

    private static <T> void validateRegistryState(Registry<T> targetRegistry) {
        if (!(targetRegistry instanceof MappedRegistry<T> targetMappedRegistry)) return;
        if (!(targetMappedRegistry instanceof MappedRegistryAccessor mappedRegistryAccessor)) return;

        ResourceLocation defaultKey = targetRegistry instanceof DefaultedRegistry<T> defReg
                ? defReg.getDefaultKey()
                : null;
        Function<T, ResourceLocation> regEntryIdResolver = regEntry -> {
            ResourceLocation id = targetMappedRegistry.getKey(regEntry);

            return Objects.equals(id, defaultKey) ? null : id;
        };
        Function<T, Integer> regEntryNumIdResolver = regEntry -> {
            int id = targetMappedRegistry.getId(regEntry);

            return id == targetMappedRegistry.getId(targetMappedRegistry.get(defaultKey)) ? -1 : id;
        };

        for (T regEntry : targetMappedRegistry) {
            ResourceLocation regEntryID = regEntryIdResolver.apply(regEntry);
            int regEntryNumId = regEntryNumIdResolver.apply(regEntry);

            if (defaultKey != null && Objects.equals(targetMappedRegistry.getKey(regEntry), defaultKey)) continue; // No need to validate default entry

            if (regEntry == null) {
                throw new IllegalStateException(String.format("Registry entry for registry %s, id %d, name %s, is null.", targetRegistry.key(), regEntryNumId, regEntryID));
            }

            if (regEntryNumId == -1) {
                throw new IllegalStateException(String.format("Registry entry for registry %s, name %s, isn't properly associated with a numerical ID.", targetRegistry.key(), regEntryID));
            }

            if (!Objects.equals(regEntry, targetMappedRegistry.get(regEntryID))) {
                throw new IllegalStateException(String.format("Registry entry for registry %s, name %s, id %d, doesn't yield the expected value. Expected: %s, Got: %s", targetRegistry.key(), regEntryID, regEntryNumId, regEntry, targetMappedRegistry.get(regEntryID)));
            }

            if (!Objects.equals(regEntry, targetMappedRegistry.byId(regEntryNumId))) {
                throw new IllegalStateException(String.format("Registry entry for registry %s, name %s, id %d, doesn't yield the expected value. Expected: %s, Got: %s", targetRegistry.key(), regEntryID, regEntryNumId, regEntry, targetMappedRegistry.byId(regEntryNumId)));
            }

            if (!Objects.equals(regEntryID, targetMappedRegistry.getKey(regEntry))) {
                throw new IllegalStateException(String.format("Registry entry for registry %s, id %d, doesn't yield the expected name. Expected: %s, Got: %s", targetRegistry.key(), regEntryNumId, regEntryID, targetMappedRegistry.getKey(regEntry)));
            }
        }
    }

    private static <T> void syncRegistryState(Registry<T> from, Registry<T> to) {
        if (!(from instanceof MappedRegistry<T> fromMappedRegistry) || !(fromMappedRegistry instanceof MappedRegistryAccessor fromAccessor)) return;
        if (!(to instanceof MappedRegistry<T> toMappedRegistry) || !(toMappedRegistry instanceof MappedRegistryAccessor toAccessor)) return;

        Map<ResourceLocation, Holder.Reference<T>> toByLocation = (Map) toAccessor.nexus$getByLocation();
        Map<ResourceKey<T>, Holder.Reference<T>> toByKey = (Map) toAccessor.nexus$getByKey();
        Map<T, Holder.Reference<T>> toByValue = (Map) toAccessor.nexus$getByValue();
        ObjectList<Holder.Reference<T>> toIdList = (ObjectList) toAccessor.nexus$getIdList();
        Object2IntMap<T> toToId = (Object2IntMap) toAccessor.nexus$getToId();

        toAccessor.nexus$setFrozen(false);

        toByLocation.clear();
        toByKey.clear();
        toByValue.clear();
        toIdList.clear();
        toToId.clear();

        for (Holder.Reference<T> holder : from.holders().toList()) {
            int id = from.getId(holder.value());

            toMappedRegistry.registerMapping(id, holder.key(), holder.value(), Lifecycle.stable());
        }

        toMappedRegistry.freeze();
    }

    private static <T> void loadRegistryState(Registry<T> from, Registry<T> to, BitSet availableIds) {
        if (!(from instanceof MappedRegistry<T> fromMappedRegistry) || !(fromMappedRegistry instanceof MappedRegistryAccessor fromAccessor)) return;
        if (!(to instanceof MappedRegistry<T> toMappedRegistry) || !(toMappedRegistry instanceof MappedRegistryAccessor toAccessor)) return;

        for (Holder.Reference<T> holder : from.holders().toList()) {
            int id = from.getId(holder.value());

            toMappedRegistry.registerMapping(id, holder.key(), holder.value(), Lifecycle.stable());
            availableIds.set(id);
        }
    }

    private static <T> Int2ObjectMap<ResourceLocation> getIdMap(Registry<T> targetRegistry) {
        Int2ObjectMap<ResourceLocation> result = new Int2ObjectLinkedOpenHashMap<>();

        for (T regEntry : targetRegistry) {
            result.put(targetRegistry.getId(regEntry), targetRegistry.getKey(regEntry));
        }

        return result;
    }

    private static <T> Int2IntMap getIdUpdates(Registry<T> targetRegistry, Int2ObjectMap<ResourceLocation> prevIdMap) {
        Int2IntMap result = new Int2IntOpenHashMap();
        Object2IntMap<ResourceLocation> prevIdToIdxMap = new Object2IntOpenHashMap<>(HashBiMap.create(prevIdMap).inverse());

        if (!(targetRegistry instanceof MappedRegistryAccessor targetMappedAccessor)) return result;

        for (int curIdx = 0; curIdx < targetMappedAccessor.nexus$getIdList().size(); curIdx++) {
            Holder.Reference<T> holder = (Holder.Reference<T>) targetMappedAccessor.nexus$getIdList().get(curIdx);

            if (holder == null) continue;

            int newId = targetRegistry.getId(holder.value());

            if (prevIdToIdxMap.containsKey(holder.key().location())) result.put(prevIdToIdxMap.getInt(holder.key().location()), newId);
        }

        return result;
    }
}
