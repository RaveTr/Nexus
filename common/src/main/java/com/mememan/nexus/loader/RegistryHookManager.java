package com.mememan.nexus.loader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.template.event.blueprint.common.LevelDataEventBlueprint;
import com.mememan.nexus.template.event.def.common.RegistryEvent;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * @apiNote This method more specifically looks the numerical ID of the provided {@code registryEntryId} and blocks
     * it from being re-used within saves.
     * <br></br>
     * It should be noted that both methods are typically used to preserve the last known state of missing registry
     * entries within saves in order to provide some leeway for them to be re-introduced (e.g. readding a removed mod)
     * without permanently losing data.
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

    /**
     * Method responsible for updating the active registry state for the specified registry key.
     * <br></br>
     * Mod-loaders have their own methods of tracking registry states. Forge has a whole system for capturing snapshots
     * of global registry state at different points in time:
     * <ul>
     *     <li>{@code RegistryManager#FROZEN} - Captures final state after registries have been frozen, including modded
     *     entries. Acts as the fallback state that subs all data into {@code RegistryManager#ACTIVE} whenever the user
     *     leaves a world/server.</li>
     *     <li>{@code RegistryManager#VANILLA} - Captures Vanilla registry state post-bootstrap, before modded entries
     *     are registered.</li>
     *     <li>{@code RegistryManager#ACTIVE} - Contains the current state of registries, depending on when/where they're
     *     being used. For instance, loading into a local world will update this state to reflect entries from that
     *     world, which allows for keeping track of entries that are updated to fire events for listening/use later on
     *     (e.g. missing registry entries, ID re-maps).</li>
     *     <li>{@code RegistryManager#STAGING} - Temp state created whenever the user loads a world or joins a server. Used
     *     as a buffer for validating and updating registries and their entries before pooling all changes into
     *     {@code RegistryManager#ACTIVE}.</li>
     * </ul>
     * Meanwhile on Fabric, registry state is managed by the {@code fabric-registry-sync} API and is effectively split
     * into 2 states:
     * <ul>
     *     <li>{@code fabric_prevIndexedEntries} / {@code fabric_prevEntries} - Contains the original registry state
     *     after all mods have loaded and all registries have been frozen/finalized. This is only populated the first
     *     time the user attempts to load a world or join a server and acts as the equivalent to
     *     {@code RegistryManager#FROZEN}.</li>
     *     <li><b>Current Registry</b> - Registry state is updated directly via their own mixins without any buffers
     *     in-between.</li>
     * </ul>
     *
     * Nexus API provides this method as a sort of safe post-processing "buffer" to update the active registry state once
     * more, particularly during world load. Trying to call this method at any other point in time may result in
     * indeterministic behavior. It's preferred that you call this in {@link LevelDataEventBlueprint#LOAD_LEVEL_DATA_POST_LOADER},
     * though any loading stage works.
     * <br></br>
     * It's also important to note that this method is primarily intended for <b>INTERNAL USE ONLY</b>. Do <b>NOT</b>
     * call or use this yourself unless you absolutely know what you're doing.
     *
     * @param targetRegistryKey The registry key pertaining to the registry for which the active registry state should
     *                          be updated.
     * @param idPool The ID pool to query against the specified registry for re-mapping.
     * @param mapper The {@link ActiveRegistryMapper} instance responsible for remapping registry entries.
     *
     * @param <T> The registry object type.
     *
     * @apiNote This is only effective when loading from a save (e.g. on the dedicated server, or when joining a local
     * world). Attempting to call this method otherwise will likely lead to an exception being thrown, or nothing
     * happening.
     *
     * @implNote If {@code idPool} is {@code null}, it is assumed that the end-developer intends to query the entire
     * registry in its current state for re-mapping.
     */
    @ApiStatus.Internal
    <T> void updateActiveRegistry(ResourceKey<Registry<T>> targetRegistryKey, @Nullable Object2IntMap<ResourceLocation> idPool, ActiveRegistryMapper<T> mapper);

    @ApiStatus.Internal
    default <T> void updateActiveRegistry(ResourceKey<Registry<T>> targetRegistryKey, ActiveRegistryMapper<T> mapper) {
        updateActiveRegistry(targetRegistryKey, null, mapper);
    }

    Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getBlockedIds();

    default BiMap<Integer, ResourceLocation> getBlockedIds(ResourceKey<? extends Registry<?>> targetRegistryKey) {
        return getBlockedIds().getOrDefault(targetRegistryKey, HashBiMap.create());
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
     * in saves from session to session based on the user's mod configuration.
     *
     * @return A {@link Map} of all registry appellations pertaining to existing registry entries.
     *
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
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
     * @see #getUpdatedAppellations()
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
     */
    default Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getAppellations() {
        return getAppellations(true);
    }

    default Object2ObjectMap<ResourceLocation, List<ResourceLocation>> getAppellations(ResourceKey<? extends Registry<?>> targetRegistryKey) {
        return getAppellations().getOrDefault(targetRegistryKey, new Object2ObjectLinkedOpenHashMap<>());
    }

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

    default Object2ObjectMap<ResourceLocation, List<ResourceLocation>> getUpdatedAppellations(ResourceKey<? extends Registry<?>> targetRegistryKey) {
        return getUpdatedAppellations().getOrDefault(targetRegistryKey, new Object2ObjectLinkedOpenHashMap<>());
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
     * @see #updateActiveRegistry(ResourceKey, Object2IntMap, ActiveRegistryMapper)
     */
    @FunctionalInterface
    interface ActiveRegistryMapper<T> {

        /**
         * Remaps the provided {@link RawRegistryEntry} for the specified {@link Registry}.
         * <br></br>
         * If the remapped entry is equal to the original entry, remapping is skipped. Returning {@code null} or an
         * invalid entry will also skip remapping.
         *
         * @param targetRegistry The {@link Registry} for which the entry should be remapped. Should ONLY ever be used to
         *                       query registry state, not for modifying registry entries.
         * @param rawEntry The {@link RawRegistryEntry} representing the registry entry to (potentially) remap.
         *
         * @return An object to remap the original entry to, preserving the original metadata (numerical ID, name, etc.).
         */
        T map(Registry<T> targetRegistry, RawRegistryEntry<T> rawEntry);
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
     *     <li>Registry entries are re-mappable in terms of {@link #objValue}. Implementors should consider stable
     *     re-mapping implementations with respect to said data.</li>
     *     <li>Registry entries can <b>ONLY</b> be re-mapped for the current "active" registry state (see references
     *     below).</li>
     *     <li>Registry entries <b>CANNOT</b> be unmapped. If an instance of this {@code class} happens to contain any
     *     invalid/{@code null} references, it should be ignored.</li>
     *     <li>If multiple {@link ActiveRegistryMapper} instances target the same {@link RawRegistryEntry}, then the
     *     result of the last mapper to be called should be used (note: composition is highly-discouraged and not natively
     *     supported, as it may lead to indeterministic behavior).</li>
     *     <li>Implementors should provide a method to consistently track re-mapped entries, whether that be through
     *     persistence to disk or written to memory.</li>
     * </ul>
     *
     * @see #updateActiveRegistry(ResourceKey, Object2IntMap, ActiveRegistryMapper)
     * @see NexusRegistryDataManager
     */
    record RawRegistryEntry<T>(ResourceKey<Registry<T>> registryKey, ResourceLocation objId, T objValue, int numericalId, boolean missing) {

        public RawRegistryEntry(ResourceKey<Registry<T>> registryKey, ResourceLocation objId, T objValue, int numericalId) {
            this(registryKey, objId, objValue, numericalId, false);
        }

        /**
         * Checks whether the registry entry is missing based on the provided numerical ID or object value.
         *
         * @return {@code true} if the entry is missing, {@code false} otherwise.
         */
        public boolean isMissing() {
            return missing || numericalId == -1 || objValue == null;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RawRegistryEntry<?> other)) return false;

            return Objects.equals(registryKey, other.registryKey)
                    && Objects.equals(objId, other.objId)
                    && Objects.equals(objValue, other.objValue)
                    && numericalId == other.numericalId
                    && missing == other.missing;
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
     * Basic data-holding {@code class} representing a singular registry entry with immutable identification data but a
     * mutable object value.
     * <br></br>
     * Primarily used to dynamically associate a substitute (or dummy) object value with registry entries in local saves
     * that may have existed before but are no longer part of the active mod configuration for any reason, but can technically
     * be used to replace/re-map object entries regardless of presence.
     *
     * @param <T> The registry entry's object type.
     *
     * @implSpec There are a number of things to note regarding how any implementors of this {@code record} should behave,
     * including the default contract imposed by Nexus API itself:
     * <ul>
     *     <li>Registry entries are <b>NOT</b> re-mappable across registries.</li>
     *     <li>Registry entries are <b>NOT</b> re-mappable in terms of their IDs through Nexus API. It's much less brittle
     *     and more stable to leave the re-mapping to each mod-loader's respective registry API rather than trying to
     *     hijack them, as there'd be much more to keep track of for an unproportionally small gain.</li>
     *     <li>Registry entries are re-mappable in terms of {@link #objValue}. Implementors should consider stable
     *     re-mapping implementations with respect to said data.</li>
     *     <li>Registry entries can <b>ONLY</b> be re-mapped for the current "active" registry state (see references
     *     below).</li>
     *     <li>If multiple {@link ActiveRegistryMapper} instances target the same {@link RawRegistryEntry}, then the
     *     result of the last mapper to be called should be used (note: composition is highly-discouraged and not natively
     *     supported, as it may lead to indeterministic behavior).</li>
     * </ul>
     *
     * @see #updateActiveRegistry(ResourceKey, ActiveRegistryMapper)
     * @see NexusRegistryDataManager
     */
    class RawRegistryEntryB<T> {
        private final ResourceKey<Registry<T>> registryKey;
        private final ResourceLocation objId;
        private final int numericalId;
        private T objValue;

        public RawRegistryEntryB(ResourceKey<Registry<T>> registryKey, ResourceLocation objId, int numericalId, T objValue) {
            this.registryKey = registryKey;
            this.objId = objId;
            this.numericalId = numericalId;
            this.objValue = objValue;
        }

        public ResourceKey<Registry<T>> getRegistryKey() {
            return registryKey;
        }

        public ResourceLocation getObjId() {
            return objId;
        }

        public int getNumericalId() {
            return numericalId;
        }

        public T getObjValue() {
            return objValue;
        }

        public void setObjValue(T objValue) {
            this.objValue = objValue;
        }
    }
}
