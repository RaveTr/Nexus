package com.mememan.nexus.internal.loader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.graph.MutableNetwork;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import com.mememan.nexus.loader.RegistryHookManager;
import com.mememan.nexus.mixins.forge.registries.ForgeRegistryAccessor;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.RegistryManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ForgeRegistryHookManager implements RegistryHookManager {
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS_VIEW = Collections.unmodifiableMap(GLOBAL_APPELLATIONS);
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> GLOBAL_BLOCKED_IDS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> GLOBAL_BLOCKED_IDS_VIEW = Collections.unmodifiableMap(GLOBAL_BLOCKED_IDS);

    public ForgeRegistryHookManager() {

    }

    @Override
    public <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, int id) {
        GLOBAL_BLOCKED_IDS
                .computeIfAbsent(targetRegistryKey, k -> HashBiMap.create())
                .put(id, NUMERICALLY_BLOCKED); // Numerically-blocking IDs should always take precedence over blocking particular entries' IDs
    }

    @Override
    public <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, ResourceLocation registryEntryId) {
        GLOBAL_BLOCKED_IDS
                .computeIfAbsent(targetRegistryKey, k -> HashBiMap.create())
                .inverse()
                .put(registryEntryId, RL_BLOCKED); // Same goes here (as specified above), but for RLs
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
        MappedRegistry<T> assumedWrappedReg = GameData.getWrapper((ResourceKey) targetRegistryKey, Lifecycle.stable());
        Map<RawRegistryEntry<T>, RawRegistryEntry<T>> potentialRemaps = RegistryHookManager.gatherPotentialRemaps(assumedWrappedReg, mapper); // Original -> Remappings

        if (potentialRemaps.isEmpty()) return; // Nothing to do

        RegistryManager active = RegistryManager.ACTIVE;
        RegistryManager frozen = RegistryManager.FROZEN;

        // TODO Check for whether we're updating at the correct time or not

        RegistryManager intermediary = new RegistryManager("INTERMEDIARY"); // Keeping this local since multiple calls can be made to update the same active registry state, so we want to avoid stale references/states to prevent accidental corruption

        ResourceLocation registryId = targetRegistryKey.location();
        ForgeRegistry<T> activeTargetReg = active.getRegistry(registryId);
        ForgeRegistry<T> intermediaryTargetReg = intermediary.getRegistry(registryId, active);

        if (!(activeTargetReg instanceof ForgeRegistryAccessor activeAccessor) || !(intermediaryTargetReg instanceof ForgeRegistryAccessor intermediaryAccessor)) return;
        if (!activeAccessor.nexus$hasWrapper()) return;

        // First: Load everything from active to intermediary
        activeAccessor.nexus$validateContent(registryId); // Re-do validation + debug logging JIC
        activeAccessor.nexus$dump(registryId);
        activeAccessor.nexus$resetDelegates();

        ForgeRegistry.Snapshot activeSnapshot = activeTargetReg.makeSnapshot();

        activeSnapshot.aliases.forEach(intermediaryTargetReg::addAlias); // Sync misc. data as well, cuz why not
        activeSnapshot.blocked.forEach(intermediaryAccessor::nexus$block);

        intermediaryTargetReg.loadIds(activeSnapshot.ids, activeSnapshot.overrides, new Object2IntOpenHashMap<>(), new Object2ObjectOpenHashMap<>(), activeTargetReg, registryId); // No need to keep track of missing or remapped stuff internally cuz we're js populating an empty registry state anyway

        // Second: Resolve remaps
        MutableNetwork<RawRegistryEntry<T>, RemapTarget> remapNetwork = RegistryHookManager.constructRemapNetwork(potentialRemaps, assumedWrappedReg, RemapConflictResolution.SWAP);

        // Reconcile remaps, resolve cycles, and handle cascading
        Map<RawRegistryEntry<T>, RawRegistryEntry<T>> resolvedRemaps = RegistryHookManager.resolveRemapTargets(remapNetwork, potentialRemaps);

        // Forge compat: support IdRemapEvent (not done in bulk, which was probably the intention behind how it was written, but it doesn't really matter now, does it)
        Map<ResourceLocation, IdMappingEvent.IdRemapping> forgeRemaps = new Object2ObjectOpenHashMap<>();

        intermediaryAccessor.nexus$setModifiable(true);
        intermediaryTargetReg.unfreeze();

        // Third: Apply remaps to intermediary registry state
        for (Map.Entry<RawRegistryEntry<T>, RawRegistryEntry<T>> resolvedRemap : resolvedRemaps.entrySet()) { // Includes swapped/displaced entries
            RawRegistryEntry<T> originalEntry = resolvedRemap.getKey();
            RawRegistryEntry<T> remappedEntry = resolvedRemap.getValue();

            intermediaryTargetReg.remove(originalEntry.objId()); // Remove original entry to avoid stale IDs being reused when the remapped entry is re-inserted

            int targetNumId = remappedEntry.numericalId();

            if (targetNumId != -1) { // Short-circuit (we're forcing the remapped entry into its target ID, since the finalized state should NOT have any missing/dangling references)
                ResourceKey<T> sourceKey = intermediaryTargetReg.getKey(targetNumId);

                if (sourceKey != null && !Objects.equals(sourceKey.location(), intermediaryTargetReg.getDefaultKey())) {
                    intermediaryTargetReg.remove(sourceKey.location());
                    intermediaryAccessor.nexus$getAvailabilityMap().clear(targetNumId);
                }
            }

            intermediaryTargetReg.register(targetNumId, remappedEntry.objId(), remappedEntry.objValue());

            if (originalEntry.numericalId() != targetNumId) forgeRemaps.put(originalEntry.objId(), new IdMappingEvent.IdRemapping(originalEntry.numericalId(), targetNumId));
        }

        // Clean unused IDs up after the fact to avoid expensive n^2 checks within the remap loop
        intermediaryAccessor.nexus$getAvailabilityMap().stream()
                .filter(id -> !intermediaryAccessor.nexus$getIds().containsKey(id))
                .forEach(intermediaryAccessor.nexus$getAvailabilityMap()::clear);

        // Validate stuff
        intermediaryAccessor.nexus$setModifiable(false);
        intermediaryTargetReg.freeze();

        try {
            intermediaryAccessor.nexus$validateContent(registryId);
        } catch (Exception e) {
            NexusConstants.LOGGER.error("Failed to validate intermediary registry for active registry state '{}'. Skipping state update.", registryId, e);
            return;
        }

        intermediaryAccessor.nexus$dump(registryId);

        // Finally: Sync intermediary registry state to active registry state
        activeAccessor.nexus$sync(registryId, intermediaryTargetReg);
        activeTargetReg.bake(); // Need this to run any necessary post-sync operations, such as populating slave maps (looking at you, BLOCKSTATE_TO_ID)

        if (!forgeRemaps.isEmpty()) MinecraftForge.EVENT_BUS.post(new IdMappingEvent(Map.of(registryId, forgeRemaps), true));

        NexusRegistryDataManager.markRegistryDataDirty();
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getBlockedIds(boolean computeFromLoaderApi) {
        if (!computeFromLoaderApi) return GLOBAL_BLOCKED_IDS_VIEW;
        else {
            Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> result = new Object2ObjectLinkedOpenHashMap<>(GLOBAL_BLOCKED_IDS);

            BuiltInRegistries.REGISTRY.entrySet().stream()
                    .filter(curEntry -> RegistryManager.ACTIVE.getRegistry(curEntry.getKey().location()) != null)
                    .forEach(curEntry -> {
                        ResourceKey<? extends Registry<?>> curRegistryKey = curEntry.getKey();
                        ForgeRegistry<?> curForgeRegistry = RegistryManager.ACTIVE.getRegistry(curEntry.getKey().location());

                        if (curForgeRegistry instanceof ForgeRegistryAccessor curForgeRegistryAccessor && !curForgeRegistryAccessor.nexus$getBlockedIds().isEmpty()) {
                            BiMap<Integer, ResourceLocation> blockedIds = result.computeIfAbsent(curRegistryKey, k -> HashBiMap.create());

                            curForgeRegistryAccessor.nexus$getBlockedIds().forEach((blockedIdValue) -> blockedIds.put(blockedIdValue, NUMERICALLY_BLOCKED));
                        }
                    });

            return result;
        }
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getAppellations(boolean computeFromLoaderApi) {
        if (!computeFromLoaderApi) return GLOBAL_APPELLATIONS_VIEW;
        else {
            Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> result = new Object2ObjectLinkedOpenHashMap<>(GLOBAL_APPELLATIONS);

            BuiltInRegistries.REGISTRY.entrySet().stream()
                    .filter(curEntry -> RegistryManager.ACTIVE.getRegistry(curEntry.getKey().location()) != null)
                    .forEach(curEntry -> {
                        ResourceKey<? extends Registry<?>> curRegistryKey = curEntry.getKey();
                        ForgeRegistry<?> curForgeRegistry = RegistryManager.ACTIVE.getRegistry(curEntry.getKey().location());

                        if (curForgeRegistry instanceof ForgeRegistryAccessor curForgeRegistryAccessor && !curForgeRegistryAccessor.nexus$getAliases().isEmpty()) {
                            Object2ObjectMap<ResourceLocation, List<ResourceLocation>> appellations = result.computeIfAbsent(curRegistryKey, k -> new Object2ObjectLinkedOpenHashMap<>());

                            curForgeRegistryAccessor.nexus$getAliases().forEach((aliasKey, aliasValue) -> {
                                List<ResourceLocation> aliases = appellations.computeIfAbsent(aliasKey, k -> new ObjectArrayList<>());

                                if (!aliases.contains(aliasValue)) aliases.add(aliasValue);
                            });
                        }
                    });

            return result;
        }
    }
}
