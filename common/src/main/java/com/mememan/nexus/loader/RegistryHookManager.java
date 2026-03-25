package com.mememan.nexus.loader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.template.event.def.common.RegistryEvent;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Centralized manager {@code interface} with per-loader implementations for dealing with different types of registry
 * hooks such as appellations, ID management, etc.
 * <br></br>
 * Retrieves loader-specific data alongside data registered through Nexus API where appropriate.
 *
 * @see Registrar#getRegistryHookManager()
 * @see RegistryEvent.MissingRegistryEntriesEvent
 */
public interface RegistryHookManager {
    /**
     * Represents the {@link ResourceLocation} used to indicate that a numerical ID is blocked regardless of registry
     * entry mapping for a given registry.
     *
     * @see #blockId(ResourceKey, int)
     */
    ResourceLocation NUMERICALLY_BLOCKED = NexusConstants.prefix("numerically_blocked");
    /**
     * Represents the {@code int} used to indicate that any {@link ResourceLocation} associated with it should have its
     * actual numerical ID blocked regardless of its value for a given registry.
     *
     * @see #blockId(ResourceKey, ResourceLocation)
     */
    int RL_BLOCKED = -2;

    /**
     * Blocks the specified numerical ID from being used by registries.
     * <br></br>
     * This should primarily be used to preserve missing entries within saves, as usages of this method otherwise are
     * brittle and prone to indeterministic behavior across saves (see implementation note below for more info).
     *
     * @param targetRegistryKey The registry key pertaining to the {@link Registry} for which the ID should be blocked.
     * @param id The numerical ID to block.
     *
     * @param <T> The registry object type (e.g. {@link Item}).
     *
     * @apiNote The numerical ID being blocked may not necessarily be tied to the same registry entry all the time, as
     * mappings may change between different local worlds/multiplayer sessions/sessions.
     *
     * @implNote Nexus handles blocking the provided numerical ID appropriately if this is called after registry states
     * have been updated for the current save being loaded/server being joined, if any.
     * <br></br>
     * Otherwise, if this is called in-between (i.e. when the player is not connected to a server or in a local world),
     * subsequent joins will automatically block the ID regardless of association. Take for instance:
     * <pre>
     *     {@code
     *         // Some ID mapping for a block in a given save
     *         some_mod:block_id - 1234
     *     }
     * </pre>
     * If the numerical ID gets blocked AFTER a save's registry state has been loaded into memory (whether that be through
     * server or local world joins), then the ID will ONLY be blocked for that save.
     * <br></br>
     * Of course, siding matters, meaning that for a dedicated server, if this is called on the client, it functionally
     * does nothing. And so, the result for the particular save whose registry state has been loaded will look something
     * like:
     * <pre>
     *     {@code
     *         // List of blocked numerical IDs for some registry
     *         minecraft:blocks - [1234]
     *
     *         // If that same block (or any block, for that matter) happens to still be present in the current mod configuration and was already tied to the same save, it gets remapped to the next available ID
     *         some_mod:block_id - 1235
     *     }
     * </pre>
     * In contrast, let's assume that the ID gets blocked in general instead of being blocked for the currently-loaded
     * save. For ALL loaded saves afterward, the ID will be blocked and any mappings tied to it will be remapped to the
     * next available ID.
     * <pre>
     *     {@code
     *         // Some World
     *         minecraft:blocks - [1234]
     *         some_mod:block_id - 1235
     *
     *         // Some Other World
     *         minecraft:blocks - [1234]
     *         another_mod:another_block_id - 1235
     *     }
     * </pre>
     * This is the reason as to why it's stated above that this method should really only be used for preserving orphaned
     * registry entries to disk in order to prevent the ID from being re-used, which Nexus already handles internally
     * via {@link NexusRegistryDataManager} and {@link RegistryEvent.MissingRegistryEntriesEvent}. The method is still
     * exposed in case dependant mods need to do something weird/special with specific numerical IDs, for whatever reason.
     *
     * @see #blockId(ResourceKey, ResourceLocation)
     * @see #blockIds(ResourceKey, int...)
     */
    <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, int id);

    /**
     * Alternate variant of {@link #blockId(ResourceKey, int)}. Blocks the {@link ResourceLocation} ID associated with
     * the specified registry entry.
     * <br></br>
     * Deterministically works across saves since it targets IDs whose values are conventionally constant (well, duh)
     * for registry entries. This should be preferred over {@link #blockId(ResourceKey, int)} for blocking registry
     * entries in general. However, its purpose isn't to "unregister" registry entries mapped to the associated ID (see
     * implementation note below for more info, preferably after reading the impl note for
     * {@link #blockId(ResourceKey, int)}).
     *
     * @param targetRegistryKey The registry key pertaining to the {@link Registry} for which the specified ID should be
     *                          blocked.
     * @param registryEntryId The {@link ResourceLocation} ID of the registry entry that should be blocked.
     *
     * @param <T> The registry object type (e.g. {@link Item}).
     *
     * @apiNote This method more specifically looks for the numerical ID of the provided {@code registryEntryId} and
     * blocks it from being re-used within saves.
     * <br></br>
     * It should be noted that both methods are typically used to preserve the last known state of missing registry
     * entries within saves in order to provide some leeway for them to be re-introduced (e.g. readding a removed mod)
     * without permanently losing or corrupting data.
     *
     * @implNote Unlike {@link #blockId(ResourceKey, int)}, this method should preferably be called in-between registry
     * state updates made by each world/server join, since it targets numerical IDs tied to the provided
     * {@code registryEntryId} regardless of value. Take for instance:
     * <pre>
     *     {@code
     *         // Some ID mapping for a block in a given save
     *         some_mod:block_id - 1234
     *     }
     * </pre>
     * Blocking "some_mod:block_id" while the save is loaded would only block its numerical ID (1234) from being re-used
     * within that same save. Alternatively, blocking that same {@link ResourceLocation} ID in-between (i.e. no save/server
     * loaded/joined) would block it for all subsequent world/server joins:
     * <pre>
     *     {@code
     *         // Some World
     *         minecraft:blocks - [1234]
     *
     *         some_mod:block_id - 1235 // Reassign to next available ID if the registry entry is still present
     *
     *         // Some Other World
     *         minecraft:blocks - [1235]
     *
     *         another_mod:block_id - 1236 // Assume the entry initially had 1235 mapped to it
     *     }
     * </pre>
     * As per usual, Nexus already handles internally via {@link NexusRegistryDataManager} and
     * {@link RegistryEvent.MissingRegistryEntriesEvent}, and the method's exposed for any other potential use cases
     * dependant mods may have.
     *
     * @see #blockId(ResourceKey, int)
     * @see #blockIds(ResourceKey, int...)
     */
    <T> void blockId(ResourceKey<Registry<T>> targetRegistryKey, ResourceLocation registryEntryId);

    default <T> void blockIds(ResourceKey<Registry<T>> targetRegistryKey, int... ids) {
        for (int id : ids) {
            blockId(targetRegistryKey, id);
        }
    }

    <T> void unblockId(ResourceKey<Registry<T>> targetRegistryKey, int id);

    <T> void unblockId(ResourceKey<Registry<T>> targetRegistryKey, ResourceLocation registryEntryId);

    default <T> void unblockIds(ResourceKey<Registry<T>> targetRegistryKey, int... ids) {
        for (int id : ids) {
            unblockId(targetRegistryKey, id);
        }
    }

    /**
     * Registers an alternative ID by which a registry entry may be identified. Primarily useful in cases where the
     * original ID isn't resolvable on level load for whatever reason.
     * <br></br>
     * Aliases may be chained together, tying to the original {@code objId}. For instance, you can call this method more
     * than once in reference to the original {@code objId} to associate multiple different alias IDs with it.
     *
     * @param objId The target registry entry's original ID.
     * @param aliasId The alternative ID for the object tied to {@code objId}.
     * @param targetRegistryKey The registry pertaining to the ID being remapped.
     *
     * @param <T> The registry object type.
     *
     * @apiNote Nexus assumes that an object tied to the provided {@code aliasId} is registered by default to the specified
     * {@code targetRegistryKey}'s associated {@link Registry}, should the original {@code objId} fail to resolve.
     *
     * @implSpec This method shouldn't check for the existence of registry entries tied to the provided IDs. That functionality
     * should be left to the other parts of Nexus' registrar mechanisms (e.g. {@link RegistryEvent.MissingRegistryEntriesEvent}).
     */
    <T> void appellate(ResourceLocation objId, ResourceLocation aliasId, ResourceKey<Registry<T>> targetRegistryKey);

    <T> void updateActiveRegistryState(ResourceKey<Registry<T>> targetRegistryKey, ActiveRegistryMapper<T> mapper);

    Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getBlockedIds(boolean computeFromLoaderApi);

    default Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getBlockedIds() {
        return getBlockedIds(true);
    }

    default BiMap<Integer, ResourceLocation> getBlockedIds(ResourceKey<? extends Registry<?>> targetRegistryKey) {
        return getBlockedIds().getOrDefault(targetRegistryKey, HashBiMap.create());
    }

    default Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getUpdatedBlockedIds() {
        Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> result = HashBiMap.create(getBlockedIds());


        return result;
    }

    default BiMap<Integer, ResourceLocation> getUpdatedBlockedIds(ResourceKey<? extends Registry<?>> targetRegistryKey) {
        return getUpdatedBlockedIds().getOrDefault(targetRegistryKey, HashBiMap.create());
    }

    /**
     * Retrieves a copy of appellations (i.e. nicknames/aliases) for registry entries per tracked registry. Tracks both
     * aliases added through Nexus and the respective loader's API (if appropriate).
     * <br></br>
     * Primarily intended for use in remapping missing registry entries to the same ID across different versions of mods,
     * which may add or remove content between versions.
     *
     * @param computeFromLoaderApi Whether to compute and add loader-specific API registry data into the result.
     *
     * @implSpec The aliases tied to any given object ID should not permit duplicate entries from any loader-specific
     * API implementations.
     *
     * @apiNote This retrieves a global view of the appellations stored in memory, which may differ from aliases stored
     * in saves from session to session based on the user's mod configuration. See references below for more info.
     *
     * @return A {@link Map} of all registry appellations pertaining to existing registry entries.
     *
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
     * @see #getAppellations()
     * @see #getAppellations(ResourceKey)
     * @see #getUpdatedAppellations()
     */
    Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getAppellations(boolean computeFromLoaderApi);

    /**
     * Overloaded variant of {@link #getAppellations(boolean)}. Retrieves a copy of appellations (i.e. nicknames/aliases)
     * for registry entries per tracked registry. Tracks both aliases added through Nexus and the respective loader's API
     * (if appropriate). Factors loader-specific api registry data into the result (looking at you, Forge).
     * <br></br>
     * Primarily intended for use in remapping missing registry entries to the same ID across different versions of mods,
     * which may add or remove content between versions.
     *
     * @implSpec The aliases tied to any given object ID should not permit duplicate entries from any loader-specific
     * API implementations.
     *
     * @apiNote This retrieves a global view of the appellations stored in memory, which may differ from aliases stored
     * in saves from session to session based on the user's mod configuration.
     *
     * @return A {@link Map} of all registry appellations pertaining to existing registry entries.
     *
     * @see #getAppellations(boolean)
     * @see #getAppellations(ResourceKey)
     * @see #getUpdatedAppellations()
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
     */
    default Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getAppellations() {
        return getAppellations(true);
    }

    /**
     * Retrieves a copy of global appellations associated with the provided {@code targetRegistry}, if any.
     *
     * @param targetRegistryKey The {@link ResourceKey} representing the {@link Registry} for which global appellations
     *                          should be looked up.
     *
     * @return A {@link Map} containing all global appellations associated with the provided {@code targetRegistry}.
     * May be empty.
     *
     * @see #getAppellations(boolean)
     * @see #getAppellations()
     * @see #getUpdatedAppellations()
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
     */
    default Object2ObjectMap<ResourceLocation, List<ResourceLocation>> getAppellations(ResourceKey<? extends Registry<?>> targetRegistryKey) {
        return getAppellations().getOrDefault(targetRegistryKey, new Object2ObjectLinkedOpenHashMap<>());
    }

    /**
     * Retrieves a combined copy of all <b>active</b> registry appellations.
     * <br></br>
     * Differs from {@link #getAppellations(boolean)} and its overloads in that it retrieves active appellations loaded
     * from the currently-active save, if any. Note that for duplicate entries (that is, mapped to a given registry
     * {@link ResourceKey}), appellations from the active save take precedence over global appellations stored in memory.
     *
     * @return A copy of all appellations, both in memory and from the active save/world (if any). Functionally identical
     * to {@link #getAppellations(boolean)} (+ overloads) if no save is currently active.
     *
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
     * @see #getAppellations(boolean)
     * @see #getAppellations()
     * @see #getAppellations(ResourceKey)
     * @see #getUpdatedAppellations(ResourceKey)
     * @see NexusRegistryDataManager#getCurrentAppellations()
     */
    default Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getUpdatedAppellations() {
        Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> result = new Object2ObjectOpenHashMap<>(getAppellations());

        NexusRegistryDataManager.getCurrentAppellations()
                .forEach((regKey, appellations) -> result.merge(regKey, appellations, (globalApellations, curAppellations) -> {
                    if (curAppellations.isEmpty()) return globalApellations;

                    Object2ObjectMap<ResourceLocation, List<ResourceLocation>> mergedAppellations = new Object2ObjectLinkedOpenHashMap<>(globalApellations);

                    curAppellations.forEach((baseId, aliases) -> mergedAppellations.merge(baseId, aliases, (globalAliases, curAliases) -> curAliases.isEmpty() ? curAliases : globalAliases));

                    return mergedAppellations;
                }));

        return result;
    }

    /**
     * Overloaded variant of {@link #getUpdatedAppellations()}. Retrieves a copy of all appellations associated with the
     * provided {@code targetRegistry}, if any.
     *
     * @param targetRegistryKey The {@link ResourceKey} representing the {@link Registry} for which appellations should be
     *                          looked up.
     *
     * @return A {@link Map} containing all appellations associated with the provided {@code targetRegistry}. May be empty.
     *
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
     * @see #getAppellations(boolean)
     * @see #getAppellations()
     * @see #getAppellations(ResourceKey)
     * @see #getUpdatedAppellations()
     */
    default Object2ObjectMap<ResourceLocation, List<ResourceLocation>> getUpdatedAppellations(ResourceKey<? extends Registry<?>> targetRegistryKey) {
        return getUpdatedAppellations().getOrDefault(targetRegistryKey, new Object2ObjectLinkedOpenHashMap<>());
    }

    /**
     * Queries the target registry's entries and applies the provided {@code mapper}, then pools all (validated) remaps
     * and returns the resulting {@link Object2ObjectLinkedOpenHashMap}. Also considers missing entries, if possible.
     * <br></br>
     * The resulting {@link Object2ObjectLinkedOpenHashMap} structures the original mappings (represented as
     * {@link RawRegistryEntry} objects) as keys, and all pertaining remaps as values.
     *
     * @param targetReg The {@link Registry} for which remaps should be applied and pooled.
     *
     * @return An {@link Object2ObjectLinkedOpenHashMap} preserving entry order, containing all mappable entry remaps.
     * May be empty.
     *
     * @param <T> The target registry type (e.g. {@link Item}).
     *
     * @apiNote Validation logic follows the specifications imposed by {@link RawRegistryEntry}, see references below.
     * In short, remaps that are missing or (have no object/invalid numerical ID/invalid target numerical ID/invalid ID)
     * are ignored.
     *
     * @see RawRegistryEntry
     * @see #updateActiveRegistryState(ResourceKey, ActiveRegistryMapper)
     * @see NexusRegistryDataManager#gatherMissingEntries(ResourceKey)
     */
    static <T> Map<RawRegistryEntry<T>, RawRegistryEntry<T>> gatherPotentialRemaps(Registry<T> targetReg, ActiveRegistryMapper<T> mapper) {
        Map<RawRegistryEntry<T>, RawRegistryEntry<T>> result = new Object2ObjectLinkedOpenHashMap<>();

        ResourceKey<Registry<T>> targetRegistryKey = (ResourceKey<Registry<T>>) targetReg.key();
        RawRegistryEntry<T> defaultEntry = representDefaultRegistryEntry(targetReg).orElse(null);
        BiFunction<RawRegistryEntry<T>, RawRegistryEntry<T>, Boolean> remappedEntryValidator = (RawRegistryEntry<T> remappedEntry, RawRegistryEntry<T> originalEntry) -> {
            if (remappedEntry == null || remappedEntry.isMissing()) {
                NexusConstants.LOGGER.warn("Attempted to remap entry {} to invalid entry: {}. Skipping...", originalEntry, remappedEntry);
                return false;
            }
            if (Objects.equals(result.get(originalEntry), remappedEntry)) {
                NexusConstants.LOGGER.warn("Attempted to remap entry {} to the same entry it's already remapped to, {}. Skipping...", originalEntry, remappedEntry);
                return false;
            }
            if (Objects.equals(originalEntry, remappedEntry)) {
                return false;
            }
            if (defaultEntry != null && (remappedEntry.numericalId() == defaultEntry.numericalId() || Objects.equals(remappedEntry.objId(), defaultEntry.objId()))) {
                NexusConstants.LOGGER.warn("Attempted to remap entry {} to ID {} (numerical ID: {}) which is the default entry for registry {}. Skipping... (Original Entry: {}, Remapped Entry: {})", originalEntry.objId(), remappedEntry.objId(), remappedEntry.numericalId(), targetRegistryKey, originalEntry, remappedEntry);
                return false;
            }

            return true;
        };

        for (Map.Entry<ResourceKey<T>, T> regEntry : targetReg.entrySet()) { // Query all existing entries first
            ResourceLocation entryId = regEntry.getKey().location();

            if (Objects.equals(entryId, defaultEntry != null ? defaultEntry.objId() : null)) continue; // Short-circuit, cuz why not

            T entryObj = regEntry.getValue();
            int entryNumId = targetReg.getId(entryObj);

            RawRegistryEntry<T> rawEntry = new RawRegistryEntry<>(targetRegistryKey, entryId, entryObj, entryNumId);
            RawRegistryEntry<T> remappedEntry = mapper.map(targetReg, rawEntry);

            if (!remappedEntryValidator.apply(remappedEntry, rawEntry)) continue;

            result.put(rawEntry, remappedEntry); // If we somehow ever encounter a case of duplicates (which should be impossible, since this is all done locally in a single pass), then let the last entry win
        }

        Map<ResourceLocation, Integer> missingEntries = NexusRegistryDataManager.gatherMissingEntries(targetRegistryKey);

        for (Map.Entry<ResourceLocation, Integer> missingEntry : missingEntries.entrySet()) { // Go through all missing entries (depends on whether we're loading into a save with that type of info or not)
            ResourceLocation missingKey = missingEntry.getKey();
            Integer lastKnownId = missingEntry.getValue();

            RawRegistryEntry<T> missingEntryRaw = new RawRegistryEntry<>(targetRegistryKey, missingKey, null, lastKnownId, true);
            RawRegistryEntry<T> remappedMissingEntry = mapper.map(targetReg, missingEntryRaw);

            if (!remappedEntryValidator.apply(remappedMissingEntry, missingEntryRaw)) continue;

            result.put(missingEntryRaw, remappedMissingEntry);
        }

        return result;
    }

    /**
     * Uses the provided {@code potentialRemaps} to construct a directed {@link Network} that models relationships
     * between existing/original entries and their remap targets. Uses data from each {@link RawRegistryEntry} to model
     * relationships such as chains and cycles between entries and their remaps out via {@link RemapTarget}.
     *
     * @param potentialRemaps The {@link Map} representing all raw entry <-> remap relationships, as conventionally
     *                        specified in {@link #gatherPotentialRemaps(Registry, ActiveRegistryMapper)}.
     * @param targetRegistry The {@link Registry} for which the remap network should be constructed.
     * @param resolution The remap conflict resolution strategy to use for handling cycles and multi-chains.
     *
     * @return A {@link MutableNetwork} representing all entry <-remap-> target relationships.
     *
     * @param <T> The entries' object types (e.g. {@link Item}).
     *
     * @see #gatherPotentialRemaps(Registry, ActiveRegistryMapper)
     */
    static <T> MutableNetwork<RawRegistryEntry<T>, RemapTarget> constructRemapNetwork(Map<RawRegistryEntry<T>, RawRegistryEntry<T>> potentialRemaps, Registry<T> targetRegistry, RemapConflictResolution resolution) {
        MutableNetwork<RawRegistryEntry<T>, RemapTarget> resultNetwork = NetworkBuilder.directed()
                .allowsSelfLoops(false) // Validated input makes this kinda unnecessary, but you never know
                .allowsParallelEdges(false)
                .edgeOrder(ElementOrder.stable()) // Basically allows us to iterate in insertion order
                .nodeOrder(ElementOrder.stable())
                .build();

        for (Map.Entry<RawRegistryEntry<T>, RawRegistryEntry<T>> remapEntry : potentialRemaps.entrySet()) {
            RawRegistryEntry<T> originalEntry = remapEntry.getKey();
            RawRegistryEntry<T> remappedEntry = remapEntry.getValue();

            RawRegistryEntry<T> potentiallyExistingEntry = Optional.ofNullable(targetRegistry.byId(remappedEntry.numericalId()))
                    .filter(obj -> !(targetRegistry instanceof DefaultedRegistry<T> defaultedTargetReg) || !defaultedTargetReg.getDefaultKey().equals(targetRegistry.getKey(obj)))
                    .map(obj -> new RawRegistryEntry<>((ResourceKey) targetRegistry.key(), targetRegistry.getKey(obj), obj, targetRegistry.getId(obj), false))
                    .orElse(null);

            if (potentiallyExistingEntry != null && !Objects.equals(originalEntry, potentiallyExistingEntry)) {
                resultNetwork.addEdge(
                        originalEntry,
                        potentiallyExistingEntry,
                        new RemapTarget(originalEntry, remappedEntry, true, resolution)
                );
            } else resultNetwork.addNode(originalEntry);
        }

        return resultNetwork;
    }

    /**
     * Untangles conflicts that may lead to cycles or cascading in the provided {@code remapNetwork}, then topologically
     * sorts and compiles all remaps into a {@link Map} of original entries to their remapped counterparts.
     *
     * @param remapNetwork The {@link MutableNetwork} representing all entry <-remap-> target relationships.
     * @param potentialRemaps The {@link Map} of initial/potential remaps.
     *
     * @return A {@link Map} of original entries to their remapped counterparts.
     *
     * @param <T> The type of registry entries, e.g. {@link Item}.
     *
     * @see #gatherPotentialRemaps(Registry, ActiveRegistryMapper)
     * @see #constructRemapNetwork(Map, Registry, RemapConflictResolution)
     * @see #resolveConflicts(MutableNetwork, Map, Map)
     * @see #topologicalSort(Network)
     */
    static <T> Map<RawRegistryEntry<T>, RawRegistryEntry<T>> resolveRemapTargets(MutableNetwork<RawRegistryEntry<T>, RemapTarget> remapNetwork, Map<RawRegistryEntry<T>, RawRegistryEntry<T>> potentialRemaps) {
        Map<RawRegistryEntry<T>, RawRegistryEntry<T>> result = new Object2ObjectLinkedOpenHashMap<>();
        Map<RawRegistryEntry<T>, RemapTarget> cachedBreakpoints = new Object2ObjectLinkedOpenHashMap<>();

        // First, resolve conflicts
        resolveConflicts(remapNetwork, cachedBreakpoints, potentialRemaps);

        // Then, topologically sort and apply all remaps
        List<RawRegistryEntry<T>> sortedRemaps = topologicalSort(remapNetwork);

        sortedRemaps.stream()
                .map(originalEntry -> Optional.ofNullable(potentialRemaps.get(originalEntry)).map(remappedEntry -> ObjectObjectImmutablePair.of(originalEntry, remappedEntry)).orElse(null))
                .filter(Objects::nonNull)
                .forEach(pair -> result.put(pair.left(), pair.right()));

        cachedBreakpoints.forEach((breakPoint, remappedBreakPoint) -> result.put(breakPoint, (RawRegistryEntry<T>) remappedBreakPoint.remappedEntry()));

        return result;
    }

    static <T> void resolveConflicts(MutableNetwork<RawRegistryEntry<T>, RemapTarget> remapNetwork, Map<RawRegistryEntry<T>, RemapTarget> breakpoints, Map<RawRegistryEntry<T>, RawRegistryEntry<T>> potentialRemaps) {
        ObjectArrayList<RawRegistryEntry<T>> indecisiveNodes = remapNetwork.nodes().stream()
                .filter(node -> remapNetwork.inDegree(node) > 1)
                .collect(Collectors.toCollection(ObjectArrayList::new));

        // Resolve conflicts at already-occupied points
        for (RawRegistryEntry<T> indecisiveNode : indecisiveNodes) {
            Set<RemapTarget> wantingRemaps = remapNetwork.inEdges(indecisiveNode);

            if (wantingRemaps.isEmpty()) continue; // Shouldn't be possible, but JIC

            RemapTarget winner = wantingRemaps.stream().findFirst().get(); // Winner is essentially first-come-first-served
            Set<RemapTarget> losers = wantingRemaps.stream()
                    .filter(target -> !target.equals(winner))
                    .collect(Collectors.toSet());

            RawRegistryEntry<T> displacedIndecisiveEntry = new RawRegistryEntry<>(
                    indecisiveNode.registryKey(),
                    indecisiveNode.objId(),
                    indecisiveNode.objValue(),
                    -1, // Displaced instead of swapped
                    false
            );

            if (remapNetwork.outDegree(indecisiveNode) == 0) { // Conflicted node has no outgoing edges (it's a terminal node)
                switch (winner.resolution()) {
                    case DISPLACE -> { // Winner gets the slot, indecisiveNode gets displaced to -1 (temp ID)
                        breakpoints.put(indecisiveNode, new RemapTarget(
                                indecisiveNode,
                                displacedIndecisiveEntry,
                                false,
                                RemapConflictResolution.DISPLACE
                        ));

                        remapNetwork.removeNode(indecisiveNode);

                        // Handle losers: displace them to -1 via breakpoints (after all other remaps are done)
                        displaceEntries(remapNetwork, breakpoints, losers);
                    }
                    case SWAP -> { // Winner takes indecisiveNode's ID, indecisiveNode takes winner's original ID
                        RawRegistryEntry<T> winnerOriginal = (RawRegistryEntry<T>) winner.originalEntry();
                        RawRegistryEntry<T> potentialConflictAtWinnerOldPos = remapNetwork.nodes().stream()
                                .filter(node -> !node.equals(winnerOriginal) && remapNetwork.outEdges(node).stream().anyMatch(edge -> edge.remappedEntry().numericalId() == winnerOriginal.numericalId()))
                                .findFirst()
                                .orElse(null);

                        if (potentialConflictAtWinnerOldPos != null) {
                            NexusConstants.LOGGER.warn("SWAP would create cascading conflict at {}. Displacing {} instead.", winnerOriginal.numericalId(), indecisiveNode);

                            breakpoints.put(indecisiveNode, new RemapTarget(
                                    indecisiveNode,
                                    displacedIndecisiveEntry,
                                    false,
                                    RemapConflictResolution.DISPLACE
                            ));
                        } else { // Safe to swap
                            breakpoints.put(indecisiveNode, new RemapTarget(
                                    indecisiveNode,
                                    new RawRegistryEntry<>(
                                            indecisiveNode.registryKey(),
                                            indecisiveNode.objId(),
                                            indecisiveNode.objValue(),
                                            winnerOriginal.numericalId(), // Takes winner's old ID
                                            false
                                    ),
                                    false,
                                    RemapConflictResolution.SWAP
                            ));
                        }

                        remapNetwork.removeNode(indecisiveNode); // Remove indecisiveNode from remapNetwork, since it'll get processed in breakpoints anyway

                        // Handle losers: displace them to -1 via breakpoints (after all other remaps are done)
                        displaceEntries((MutableNetwork<RawRegistryEntry<T>, RemapTarget>) remapNetwork, (Map<RawRegistryEntry<T>, RemapTarget>) breakpoints, (Set<RemapTarget>) losers);
                    }
                    case REJECT -> {
                        // Reject ALL remap attempts to this slot
                        displaceEntries((MutableNetwork<RawRegistryEntry<T>, RemapTarget>) remapNetwork, (Map<RawRegistryEntry<T>, RemapTarget>) breakpoints, (Set<RemapTarget>) wantingRemaps);

                        NexusConstants.LOGGER.error("Rejected remap conflict at {}", indecisiveNode);
                        throw new UnsupportedOperationException("Remap conflict rejected at " + indecisiveNode);
                    }
                    case IGNORE -> {
                        displaceEntries((MutableNetwork<RawRegistryEntry<T>, RemapTarget>) remapNetwork, (Map<RawRegistryEntry<T>, RemapTarget>) breakpoints, (Set<RemapTarget>) wantingRemaps);

                        NexusConstants.LOGGER.warn("Ignored remap conflict at {}", indecisiveNode);
                    }
                }
            } else { // Otherwise, the conflicted node is part of a chain/cycle
                switch (winner.resolution()) {
                    case DISPLACE -> {
                        Set<RawRegistryEntry<T>> indecisiveTargets = remapNetwork.successors(indecisiveNode);

                        if (indecisiveTargets.size() != 1) {
                            throw new IllegalStateException("Expected exactly 1 outgoing edge from %s, found %d".formatted(indecisiveNode, indecisiveTargets.size()));
                        }

                        RawRegistryEntry<T> indecisiveTarget = indecisiveTargets.iterator().next();
                        RemapTarget indecisiveRemapTarget = remapNetwork.edgeConnecting(indecisiveNode, indecisiveTarget).orElseThrow();

                        RawRegistryEntry<T> tempIndecisiveEntry = displacedIndecisiveEntry;

                        breakpoints.put(indecisiveNode, new RemapTarget(
                                indecisiveNode,
                                tempIndecisiveEntry,
                                false,
                                RemapConflictResolution.DISPLACE
                        ));

                        RemapTarget newRemapTarget = new RemapTarget(
                                tempIndecisiveEntry, // New original entry
                                indecisiveRemapTarget.remappedEntry(), // Same target
                                indecisiveRemapTarget.isOccupied(), // Same occupation status
                                indecisiveRemapTarget.resolution() // Same resolution
                        );

                        remapNetwork.addEdge(tempIndecisiveEntry, indecisiveTarget, newRemapTarget); // Reconnect: tempIndecisiveEntry -> indecisiveTarget
                        remapNetwork.removeNode(indecisiveNode); // Remove original indecisiveNode from network

                        displaceEntries(remapNetwork, breakpoints, losers);
                    }
                    case SWAP -> {
                        RawRegistryEntry<T> winnerOriginal = (RawRegistryEntry<T>) winner.originalEntry();
                        Set<RawRegistryEntry<T>> indecisiveTargets = remapNetwork.successors(indecisiveNode); // Get where indecisiveNode wants to go

                        if (indecisiveTargets.size() != 1) {
                            throw new IllegalStateException("Expected exactly 1 outgoing edge from %s, found %d".formatted(indecisiveNode, indecisiveTargets.size()));
                        }

                        RawRegistryEntry<T> indecisiveTarget = indecisiveTargets.iterator().next();
                        RemapTarget indecisiveRemapTarget = remapNetwork.edgeConnecting(indecisiveNode, indecisiveTarget).orElseThrow();

                        RawRegistryEntry<T> potentialConflictAtWinnerOldPos = remapNetwork.nodes().stream()
                                .filter(node -> !node.equals(winnerOriginal) && remapNetwork.outEdges(node).stream().anyMatch(edge -> edge.remappedEntry().numericalId() == winnerOriginal.numericalId()))
                                .findFirst()
                                .orElse(null); // Check if swapping creates yet another conflict at winner's old position (yetanother[word]lib reference [bruh])

                        RawRegistryEntry<T> swappedIndecisiveEntry;

                        if (potentialConflictAtWinnerOldPos != null) {
                            /*
                             * Cascading conflict: some other entry is at winner's old position.
                             * Therefore, we're displacing indecisiveNode to -1 instead of swapping.
                             * ...
                             * (Multi-line comments make me feel smart :drooling_patrick:)
                             */
                            NexusConstants.LOGGER.warn("SWAP would create cascading conflict at {}. Displacing {} instead.", winnerOriginal.numericalId(), indecisiveNode);

                            swappedIndecisiveEntry = displacedIndecisiveEntry;
                        } else { // Safe to swap
                            swappedIndecisiveEntry = new RawRegistryEntry<>(
                                    indecisiveNode.registryKey(),
                                    indecisiveNode.objId(),
                                    indecisiveNode.objValue(),
                                    winnerOriginal.numericalId(), // Takes winner's old ID
                                    false
                            );
                        }

                        breakpoints.put(indecisiveNode, new RemapTarget( // Store the swap/displace in breakpoints
                                indecisiveNode,
                                swappedIndecisiveEntry,
                                false,
                                potentialConflictAtWinnerOldPos != null ? RemapConflictResolution.DISPLACE : RemapConflictResolution.SWAP
                        ));

                        RemapTarget newRemapTarget = new RemapTarget(
                                swappedIndecisiveEntry, // New original entry
                                indecisiveRemapTarget.remappedEntry(), // Same target
                                indecisiveRemapTarget.isOccupied(), // Same occupation status
                                indecisiveRemapTarget.resolution() // Same resolution
                        );

                        remapNetwork.addEdge(swappedIndecisiveEntry, indecisiveTarget, newRemapTarget); // Reconnect: swappedIndecisiveEntry -> indecisiveTarget
                        remapNetwork.removeNode(indecisiveNode); // Remove original indecisiveNode from network

                        displaceEntries(remapNetwork, breakpoints, losers);
                    }
                    case REJECT -> {
                        displaceEntries(remapNetwork, breakpoints, wantingRemaps);

                        NexusConstants.LOGGER.error("Rejected remap conflict at {} (part of chain)", indecisiveNode);
                        throw new UnsupportedOperationException("Remap conflict rejected at %s".formatted(indecisiveNode));
                    }
                    case IGNORE -> { // Js displace everything
                        displaceEntries(remapNetwork, breakpoints, wantingRemaps);

                        NexusConstants.LOGGER.warn("Ignored remap conflict at {} (part of chain)", indecisiveNode);
                    }
                }
            }
        }

        // Take care of conflicts at empty IDs
        Map<Integer, ObjectArrayList<RawRegistryEntry<T>>> remapCompetition = remapNetwork.nodes().stream()
                .map(node -> Map.entry(node, potentialRemaps.get(node)))
                .filter(entry -> entry.getValue() != null) // Nodes not in potentialRemaps
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().numericalId(), // Group by target numerical ID
                        Collectors.mapping(Map.Entry::getKey, Collectors.toCollection(ObjectArrayList::new))
                ));

        for (Map.Entry<Integer, ObjectArrayList<RawRegistryEntry<T>>> competition : remapCompetition.entrySet()) {
            ObjectArrayList<RawRegistryEntry<T>> wanters = competition.getValue();

            if (wanters.size() <= 1) continue; // No conflict

            wanters.sort(Comparator.comparingInt(RawRegistryEntry::numericalId));

            RawRegistryEntry<T> winner = wanters.get(0); // Lowest numerical ID wins
            ObjectArrayList<RawRegistryEntry<T>> losers = new ObjectArrayList<>(wanters.subList(1, wanters.size()));

            NexusConstants.LOGGER.warn("Multiple entries want target ID {}: {} (winner: {} at ID {})",
                    competition.getKey(),
                    wanters.stream()
                            .map(e -> "%s (ID %d)".formatted(e.objId(), e.numericalId()))
                            .collect(Collectors.toList()),
                    winner.objId(),
                    winner.numericalId());

            // Displace all losers
            for (RawRegistryEntry<T> loser : losers) {
                breakpoints.put(loser, new RemapTarget(
                        loser,
                        new RawRegistryEntry<>(
                                loser.registryKey(),
                                loser.objId(),
                                loser.objValue(),
                                -1, // Displaced to temp ID
                                false
                        ),
                        false,
                        RemapConflictResolution.DISPLACE
                ));

                remapNetwork.removeNode(loser);
                potentialRemaps.remove(loser);
            }
        }
    }

    /**
     * Removes entries from the provided {@code remapNetwork} that are marked for displacement, and updates
     * {@code breakpoints} accordingly.
     *
     * @param remapNetwork The remap network.
     * @param breakpoints The map of breakpoints.
     * @param wantingRemaps The {@link Iterable} of remaps that are waiting to be processed.
     *
     * @param <T> The type of registry entries, e.g. {@link Item}.
     *
     * @see #resolveRemapTargets(MutableNetwork, Map)
     * @see #resolveConflicts(MutableNetwork, Map, Map)
     */
    static <T> void displaceEntries(MutableNetwork<RawRegistryEntry<T>, RemapTarget> remapNetwork, Map<RawRegistryEntry<T>, RemapTarget> breakpoints, Iterable<RemapTarget> wantingRemaps) {
        for (RemapTarget target : wantingRemaps) {
            RawRegistryEntry<T> targetEntry = (RawRegistryEntry<T>) target.originalEntry();

            breakpoints.put(targetEntry, new RemapTarget(
                    targetEntry,
                    new RawRegistryEntry<>(
                            targetEntry.registryKey(),
                            targetEntry.objId(),
                            targetEntry.objValue(),
                            -1, // Displaced to temp ID
                            false
                    ),
                    false,
                    RemapConflictResolution.DISPLACE
            ));

            remapNetwork.removeNode(targetEntry);
        }
    }

    /**
     * Performs a topological sort on the provided remap {@link Network} and returns the sorted entries.
     * <br></br>
     * This implementation of Kahn's Algorithm assumes that the provided {@code remapNetwork} does not contain any cycles
     * or conflicts. That is to say; all related entries should be able to reach each other (as per the definition of SCCs),
     * and all nodes should have at most 1 dependency.
     *
     * @param remapNetwork The remap {@link Network} for which a topological sort should be performed.
     *
     * @return A {@link List} containing all entries in topologically-sorted order.
     *
     * @param <T> The entries' object types (e.g. {@link Item}).
     *
     * @see #detectCycles(Network)
     * @see #strongConnect(RawRegistryEntry, Network, Set, Deque, Map, Map, AtomicInteger, List)
     * @see <a href="https://en.wikipedia.org/wiki/Topological_sorting">Wikipedia: Topological Sorting</a>
     * @see <a href="https://www.cs.usfca.edu/~galles/visualization/TopoSortIndegree.html">CS.USF: Topological Sorting Visualization</a>
     */
    static <T> List<RawRegistryEntry<T>> topologicalSort(Network<RawRegistryEntry<T>, RemapTarget> remapNetwork) {
        List<RawRegistryEntry<T>> sorted = new ObjectArrayList<>();
        Map<RawRegistryEntry<T>, Integer> inDegree = new Object2IntLinkedOpenHashMap<>();

        for (RawRegistryEntry<T> node : remapNetwork.nodes()) { // In-degree = number of incoming edges = number of dependencies
            inDegree.put(node, remapNetwork.inDegree(node));
        }

        Queue<RawRegistryEntry<T>> queue = new ArrayDeque<>();

        for (RawRegistryEntry<T> node : remapNetwork.nodes()) { // Enqueue no-dependency nodes first (i.e. independent remaps)
            if (inDegree.get(node) == 0) {
                queue.add(node);  // Layer 0 nodes
            }
        }

        while (!queue.isEmpty()) { // Now we get to the fun part: topologically-traversing and resolving remaps
            RawRegistryEntry<T> node = queue.poll();
            sorted.add(node);

            for (RawRegistryEntry<T> successor : remapNetwork.successors(node)) {
                int newInDegree = inDegree.get(successor) - 1; // In our case, if a node has more than 1 dependency, that means there's a conflict or cycle that wasn't properly resolved

                inDegree.put(successor, newInDegree);

                if (newInDegree == 0) queue.add(successor); // When in-degree == 0, all dependencies are satisfied
            }
        }

        if (sorted.size() != remapNetwork.nodes().size()) {
            throw new IllegalStateException("Conflict detected - not all nodes processed");
        }

        return sorted;
    }

    // TODO This is probably getting removed, useless here anyway due to the nature of how remaps are processed
    private static <T> void breakCycle(List<RawRegistryEntry<T>> cycle, MutableNetwork<RawRegistryEntry<T>, RemapTarget> remapNetwork, Map<RawRegistryEntry<T>, RemapTarget> breakpoints) {
        RawRegistryEntry<T> breakPoint = cycle.get(0);
        RemapTarget remappedBreakPoint = remapNetwork.outEdges(breakPoint).stream()
                .findFirst()
                .orElse(null);

        if (remappedBreakPoint == null) {
            NexusConstants.LOGGER.warn("Attempted to break cycle at node {}, but no remap target was found. Attempting to resolve breakpoint in cycle.", breakPoint);

            breakPoint = cycle.stream()
                    .filter(cycleEntry -> !remapNetwork.outEdges(cycleEntry).isEmpty())
                    .findFirst()
                    .orElse(breakPoint);
            remappedBreakPoint = remapNetwork.outEdges(breakPoint).stream()
                    .findFirst()
                    .orElse(null);

            if (remappedBreakPoint == null) {
                throw new IllegalStateException(String.format("No valid breakpoint found for cycle starting at %s.", cycle.get(0)));
            }
        }

        breakpoints.put(breakPoint, remappedBreakPoint); // Breakpoints are last to be processed throughout the whole chain

        remapNetwork.removeNode(breakPoint); // FIXME This wouldn't logically work anyway sooo,,
        remapNetwork.removeEdge(remappedBreakPoint);
    }

    /**
     * Uses Tarjan's algorithm to detect cycles in a directed {@link Network}.
     *
     * @param remapNetwork The remap {@link Network} for which cycles should be detected.
     *
     * @return An {@link ObjectArrayList} containing all detected cycles, grouped by their root nodes.
     *
     * @param <T> The entries' object types (e.g. {@link Item}).
     *
     * @see #resolveRemapTargets(MutableNetwork, Map)
     * @see #strongConnect(RawRegistryEntry, Network, Set, Deque, Map, Map, AtomicInteger, List)
     * @see <a href="https://www.geeksforgeeks.org/dsa/tarjan-algorithm-find-strongly-connected-components/">Geeks for Geeks: Tarjan's Algorithm for Strongly Connected Components</a>
     * @see <a href="https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm">Wikipedia: Tarjan's Strongly Connected Components Algorithm</a>
     */
    private static <T> List<List<RawRegistryEntry<T>>> detectCycles(Network<RawRegistryEntry<T>, RemapTarget> remapNetwork) {
        List<List<RawRegistryEntry<T>>> cycles = new ObjectArrayList<>();
        Set<RawRegistryEntry<T>> visited = new ObjectOpenHashSet<>();
        Deque<RawRegistryEntry<T>> stack = new ArrayDeque<>();
        Map<RawRegistryEntry<T>, Integer> indices = new Object2IntOpenHashMap<>();
        Map<RawRegistryEntry<T>, Integer> lowLinks = new Object2IntOpenHashMap<>();
        AtomicInteger index = new AtomicInteger(0);

        for (RawRegistryEntry<T> baseEntry : remapNetwork.nodes()) {
            if (!visited.contains(baseEntry)) {
                strongConnect(baseEntry, remapNetwork, visited, stack, indices, lowLinks, index, cycles);
            }
        }

        return cycles.stream()
                .filter(component -> component.size() > 1) // Filter out single-node components (not cycles)
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    /**
     * Finds all strongly connected components (SCCs) in a directed {@link Network} using Tarjan's algorithm and populates
     * the provided {@code components} {@link List} with the results.
     *
     * @param node The starting node for the current SCC search.
     * @param network The directed {@link Network} for which SCCs should be found.
     * @param visited A {@link Set} of visited nodes during the search.
     * @param stack A {@link Deque} of nodes in the current path.
     * @param indices A {@link Map} of node discovery indices.
     * @param lowLinks A {@link Map} of lowest (reachable) indices for each node.
     * @param index An {@link AtomicInteger} for tracking the current discovery index.
     * @param components The {@link List} keeping track of all SCCs found during the search.
     *
     * @param <T> The entries' object types (e.g. {@link Item}).
     *
     * @see <a href="https://www.geeksforgeeks.org/dsa/tarjan-algorithm-find-strongly-connected-components/">Geeks for Geeks: Tarjan's Algorithm for Strongly Connected Components</a>
     * @see <a href="https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm">Wikipedia: Tarjan's Strongly Connected Components Algorithm</a>
     */
    private static <T> void strongConnect(RawRegistryEntry<T> node, Network<RawRegistryEntry<T>, RemapTarget> network, Set<RawRegistryEntry<T>> visited, Deque<RawRegistryEntry<T>> stack, Map<RawRegistryEntry<T>, Integer> indices, Map<RawRegistryEntry<T>, Integer> lowLinks, AtomicInteger index, List<List<RawRegistryEntry<T>>> components) {
        int currentIndex = index.getAndIncrement();

        indices.put(node, currentIndex); // Node discovery index
        lowLinks.put(node, currentIndex); // Lowest (reachable) index (initial value is the same as the discovery index)
        visited.add(node); // Self-explanatory (each node gets visited 1 time, no more)
        stack.push(node); // Nodes in current path (especially goated here since we'll be working with a lot of direct successor branches)

        for (RawRegistryEntry<T> successor : network.successors(node)) { // "successors" here refers to direct successors of a node, not a chain that expands out from it, apparently
            if (!visited.contains(successor)) {
                strongConnect(successor, network, visited, stack, indices, lowLinks, index, components);
                lowLinks.put(node, Math.min(lowLinks.get(node), lowLinks.get(successor)));
            } else if (stack.contains(successor)) lowLinks.put(node, Math.min(lowLinks.get(node), indices.get(successor)));
        }

        if (lowLinks.get(node).equals(indices.get(node))) { // If node is a root node, pop the stack and create an SCC
            List<RawRegistryEntry<T>> component = new ObjectArrayList<>();
            RawRegistryEntry<T> curNode;

            do {
                curNode = stack.pop();
                component.add(curNode);
            } while (!curNode.equals(node));

            components.add(component);
        }
    }

    /**
     * Attempts to represent the information associated with the provided {@linkplain Registry targetRegistry's} default
     * entry, if any.
     *
     * @param targetRegistry The {@link Registry} for which the default entry should be represented.
     *
     * @return An {@link Optional} containing the {@link RawRegistryEntry} representing the default entry, if any. May
     * be {@link Optional#empty()}.
     *
     * @param <T> The registry's object type (e.g. {@link Item}).
     */
    static <T> Optional<RawRegistryEntry<T>> representDefaultRegistryEntry(Registry<T> targetRegistry) {
        if (!(targetRegistry instanceof DefaultedRegistry<T> defaultedReg)) return Optional.empty();

        ResourceLocation defaultEntryId = defaultedReg.getDefaultKey();
        T defaultEntryObj = defaultedReg.get(defaultEntryId);
        int defaultEntryNumId = defaultedReg.getId(defaultEntryObj);

        return Optional.of(new RawRegistryEntry<>((ResourceKey<Registry<T>>) defaultedReg.key(), defaultEntryId, defaultEntryObj, defaultEntryNumId, false));
    }

    /**
     * Functional {@code interface} that takes an input {@link RawRegistryEntry} within the specified
     * {@code targetRegistry} and returns some object to map to it, pertaining to the original object.
     * <br></br>
     * Can be used to safely modify registry entries in-place during the registration window for populating the "active"
     * registry state for any specific world being loaded.
     *
     * @param <T> The target {@link Registry} object type.
     *
     * @see #updateActiveRegistryState(ResourceKey, ActiveRegistryMapper)
     */
    @FunctionalInterface
    interface ActiveRegistryMapper<T> {

        /**
         * Remaps the provided {@link RawRegistryEntry} for the specified {@link Registry}.
         * <br></br>
         * If the remapped entry is equal to the original entry, remapping is skipped. Returning {@code null} or an
         * invalid entry will also skip remapping. Missing entries that aren't remapped are automatically unmapped.
         *
         * @param targetRegistry The {@link Registry} for which the entry should be remapped. Should ONLY ever be used to
         *                       query registry state, not for modifying registry entries.
         * @param rawEntry The {@link RawRegistryEntry} representing the registry entry to (potentially) remap.
         *
         * @return The {@link RawRegistryEntry} to remap the original entry to.
         */
        RawRegistryEntry<T> map(Registry<T> targetRegistry, RawRegistryEntry<T> rawEntry);
    }

    /**
     * Data-holding {@code record} representing a singular registry entry for the express purpose of comparing and
     * remapping registry data efficiently.
     *
     * @param registryKey The {@link ResourceKey} pertaining to the registry for which the entry belongs.
     * @param objId The {@link ResourceLocation} ID of the registry entry.
     * @param objValue The actual object pertaining to the registry entry.
     * @param numericalId The numerical ID of the registry entry.
     *
     * @param <T> The registry entry's object type.
     *
     * @implSpec There are a number of things to note regarding how any implementors of this {@code record} should behave,
     * including the default contract imposed by Nexus API itself:
     * <ul>
     *     <li>Registry entries are <b>NOT</b> re-mappable across registries. For example, changing {@link #registryKey}
     *     should functionally do nothing, as it's only included for comparison purposes.</li>
     *     <li>Registry entries are re-mappable in terms of {@link #objId}, {@link #objValue}, and {@link #numericalId}.
     *     Implementors should consider stable re-mapping implementations with respect to said data.</li>
     *     <li>Registry entries can <b>ONLY</b> be re-mapped for the current "active" registry state (see references
     *     below).</li>
     *     <li>Registry entries <b>CANNOT</b> be unmapped. If an instance of this {@code class} happens to contain any
     *     invalid/{@code null} references, it should be ignored.</li>
     *     <li>If multiple {@link ActiveRegistryMapper} instances target the same {@link RawRegistryEntry}, then the
     *     result of the last mapper to be called should be used (note: composition is highly-discouraged and not natively
     *     supported, as it may lead to indeterministic behavior).</li>
     *     <li>Implementors should provide a method to consistently track re-mapped entries, whether that be through
     *     persistence to disk or written to memory.</li>
     *     <li>For remapped registry entries whose numerical or {@link ResourceLocation} IDs have changed, implementors
     *     should preferably implement a simple swap-in mechanism that takes the remapped entry's original numerical ID
     *     and subs the entry at the target numerical ID in before registering the remapped entry at its target numerical
     *     ID. For example:
     *         <pre>
     *             {@code
     *                 minecraft:stone -> 1 // This is the entry at the target ID
     *                 some_mod:original_entry -> 1005 // This is the original entry pre-remap
     *
     *                 // During active registry state remapping
     *                 mapper.apply(targetRegistry, originalEntry) -> some_mod:original_entry -> 1 // The key can also change, but that doesn't really matter in this example
     *
     *                 if (*some check to see if remapped entry has a different ID from the original*) {
     *                     if (*another check to see if the target numerical ID is taken*) {
     *                         // Also probably a good idea to do all of this in some temp copy of the registry before syncing changes to gracefully handle errors, but you probably already know that if you're tinkering with this anyway
     *                         intermediaryRegistry.remove(whateverObjExistsAtTheTargetId); // Make sure you capture this in a variable or smth
     *                         intermediaryRegistry.register(targetId, remappedObjStuff); // targetId here is 1, cuz that's what it got remapped to earlier
     *                         intermediaryRegistry.register(originalId, whateverObjExistsAtTheTargetId); // originalId here is 1005, because that's what the entry had in the active registry state per last check
     *                     }
     *                 }
     *
     *                 // Result
     *                 minecraft:stone -> 1005
     *                 some_mod:original_entry -> 1
     *             }
     *         </pre>
     *         It's also wise to note that preserving metadata in general ({@link ResourceLocation}/numerical IDs) is
     *         probably a good idea for registries that heavily rely on them for querying data actively (particularly in
     *         the case of numerical IDs, which mob effects make use of, but blocks would not be affected by if registration
     *         is done correctly, for instance).
     *     </li>
     *     <li>Default registry keys (if they exist for a given registry) should <b>NOT</b> be remapped or touched under
     *     any circumstances. Seriously. There's literally no circumstance under which doing so is a plausible, practical,
     *     or scalable idea.</li>
     *     <li>Implementors are fully responsible for handling edge cases, such as chains and/or cycles in potential remaps.</li>
     * </ul>
     *
     * @see #updateActiveRegistryState(ResourceKey, ActiveRegistryMapper)
     * @see NexusRegistryDataManager
     */
    record RawRegistryEntry<T>(ResourceKey<Registry<T>> registryKey, ResourceLocation objId, T objValue, int numericalId, boolean missing) {

        public RawRegistryEntry(ResourceKey<Registry<T>> registryKey, ResourceLocation objId, T objValue, int numericalId) {
            this(registryKey, objId, objValue, numericalId, false);
        }

        /**
         * Checks whether the registry entry is missing based on the provided object value.
         *
         * @return {@code true} if the entry is missing, {@code false} otherwise.
         *
         * @apiNote {@link #numericalId} isn't considered, since -1 can represent a remapped entry looking for the next
         * available ID in a given registry.
         */
        public boolean isMissing() {
            return missing || objValue == null;
        }

        /**
         * Alternative equivocation to {@link #equals(Object)} that checks for full equivalence of all fields, including
         * {@link #objValue}.
         *
         * @param other The {@link RawRegistryEntry} to compare against.
         *
         * @return {@code true} if the entries are equivalent, {@code false} otherwise.
         *
         * @see #equals(Object)
         */
        public boolean isEquivalentTo(RawRegistryEntry<?> other) {
            return Objects.equals(this, other)
                    && Objects.equals(objId, other.objId);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RawRegistryEntry<?> other)) return false;

            return Objects.equals(registryKey, other.registryKey)
                    && Objects.equals(objId, other.objId)
                    && numericalId == other.numericalId
                    && missing == other.missing;
        }

        @Override
        public int hashCode() {
            return Objects.hash(registryKey, objId, objValue, numericalId, missing);
        }

        @Override
        public @NotNull String toString() {
            return "RawRegistryEntry{" +
                    "registryKey=" + registryKey +
                    ", objId=" + objId +
                    ", objValue=" + objValue +
                    ", numericalId=" + numericalId +
                    ", missing=" + missing +
                    '}';
        }
    }

    /**
     * Object-holder {@code record} representing a remap target for a given registry entry, with additional metadata
     * pertaining to the remap's resolution strategy.
     *
     * @param originalEntry The original registry entry.
     * @param remappedEntry The remapped registry entry.
     * @param isOccupied Whether the remapped entry's ID is already occupied.
     * @param resolution The remap conflict resolution strategy. Should only be used if the remapped entry's ID is
     *                   occupied.
     */
    record RemapTarget(RawRegistryEntry<?> originalEntry, RawRegistryEntry<?> remappedEntry, boolean isOccupied, RemapConflictResolution resolution) {

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            RemapTarget that = (RemapTarget) o;

            return isOccupied == that.isOccupied
                    && Objects.equals(originalEntry, that.originalEntry)
                    && Objects.equals(remappedEntry, that.remappedEntry)
                    && resolution == that.resolution;
        }

        @Override
        public int hashCode() {
            return Objects.hash(originalEntry, remappedEntry, isOccupied, resolution);
        }
    }

    /**
     * Object-holder {@code enum} representing different strategies for handling remap conflicts, which can occur
     * under a number of circumstances (e.g. when an entry is being remapped to an occupied ID for which no remap target
     * exists).
     *
     * @see RemapTarget
     * @see RawRegistryEntry
     */
    enum RemapConflictResolution {
        /**
         * Attempts to displace the existing entry at the target ID to the next available registry ID. Fails if no such
         * ID exists, delegating to {@link #SWAP}.
         */
        DISPLACE,
        /**
         * Swaps the remapped entry's ID with the existing entry's ID. Functionally does nothing if the target entry
         * causing conflict has its own remap target.
         */
        SWAP,
        /**
         * Rejects the remap attempt and leaves the original entry intact, throwing an
         * {@link UnsupportedOperationException}.
         */
        REJECT,
        /**
         * Ignores the remap attempt and leaves the original entry intact.
         */
        IGNORE;
    }
}
