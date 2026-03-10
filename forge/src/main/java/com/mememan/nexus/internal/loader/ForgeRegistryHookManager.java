package com.mememan.nexus.internal.loader;

import com.google.common.collect.BiMap;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.ClassFinder;
import com.mememan.nexus.loader.RegistryHookManager;
import com.mememan.nexus.mixins.forge.registries.ForgeRegistryAccessor;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ForgeRegistryHookManager implements RegistryHookManager {
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> GLOBAL_APPELLATIONS_VIEW = Collections.unmodifiableMap(GLOBAL_APPELLATIONS);
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> GLOBAL_BLOCKED_IDS = new Object2ObjectLinkedOpenHashMap<>();

    public ForgeRegistryHookManager() {

    }

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
    public <T> void updateActiveRegistry(ResourceKey<Registry<T>> targetRegistryKey, Object2IntMap<ResourceLocation> idPool, ActiveRegistryMapper<T> mapper) {
        // TODO Add prelim check to determine whether we're actually loading into a world or not

        RegistryManager intermediary = new RegistryManager("INTERMEDIARY");
        RegistryManager active = RegistryManager.ACTIVE;
        ForgeRegistry<T> activeForgeReg = active.getRegistry(targetRegistryKey.location());
        ForgeRegistry<T> targetForgeReg = intermediary.getRegistry(targetRegistryKey.location(), active); // Initially empty (this only copies registry configuration, not state)
        Object2IntMap<ResourceLocation> usedIdPool = idPool;

        if (!(activeForgeReg instanceof ForgeRegistryAccessor activeAccessor) || !(targetForgeReg instanceof ForgeRegistryAccessor targetAccessor)) return; // JIC

        ResourceLocation wrapperId = new ResourceLocation("forge", "registry_defaulted_wrapper");
        boolean hasDefault = targetForgeReg.getDefaultKey() != null;
        Class<?> chosenWrapperClazz = hasDefault // FIXME This is EXTREMELY fragile
                ? ClassFinder.forNameNoInit("net.minecraftforge.registries.NamespacedDefaultedWrapper")
                : ClassFinder.forNameNoInit("net.minecraftforge.registries.NamespacedWrapper");
        Registry<T> wrappedRegistry = (Registry<T>) targetForgeReg.getSlaveMap(wrapperId, chosenWrapperClazz);
        Registry<T> wrappedActiveRegistry = (Registry<T>) activeForgeReg.getSlaveMap(wrapperId, chosenWrapperClazz);

        if (wrappedRegistry == null) throw new IllegalStateException(String.format("Cannot update registry '%s', as it does not have a wrapper/mapped registry.", targetRegistryKey));

        if (usedIdPool == null) {
            NexusConstants.LOGGER.info("Calling updateActiveRegistry for registry '{}' with idPool being substituted with the entire registry. This may lead to breakages and/or indeterministic behaviour if used incorrectly. To the modder: proceed with caution.", targetRegistryKey);
            usedIdPool = activeForgeReg.getKeys().stream()
                    .collect(Collectors.toMap(Function.identity(), activeForgeReg::getID, (a, b) -> {
                        throw new IllegalArgumentException("Somehow encountered duplicate registry entry numerical IDs when pooling to update active registry state. This shouldn't be possible due to preliminary checks done when validating registry entries. Problematic ID: %s".formatted(a));
                    }, Object2IntLinkedOpenHashMap::new));
        }

        // Load things into the intermediary manager, cuz it's about to (potentially) get messy
        targetForgeReg.loadIds(usedIdPool, activeAccessor.nexus$getOverrideOwners(), new Object2IntLinkedOpenHashMap<>(), new Object2ObjectLinkedOpenHashMap<>(), activeForgeReg, targetRegistryKey.location());

        usedIdPool.forEach((objId, numericalId) -> {
            RawRegistryEntry<T> rawEntry = new RawRegistryEntry<>(targetRegistryKey, objId, activeForgeReg.getRaw(objId), numericalId);
            T remappedEntry = mapper.map(wrappedActiveRegistry, rawEntry);

            if (remappedEntry != null && !Objects.equals(rawEntry.objValue(), remappedEntry)) {
                // Basically, hijack the target ID only if it was mapped to the target entry's ID, if necessary
                targetAccessor.nexus$getAvailabilityMap().clear(rawEntry.numericalId());
                targetForgeReg.register(rawEntry.numericalId(), rawEntry.objId(), remappedEntry);
            }
        });

        resolveRegistryState(activeForgeReg, targetForgeReg); // Resolve differences in state (if any) after querying usedIdPool b4 we actually attempt to sync changes
   //     activeAccessor.nexus$sync(targetRegistryKey.location(), targetForgeReg);
   //     activeForgeReg.bake(); // Needed to fire callbacks that populate whatever slave map(s) the target registry may have (looking at you, blockstatetoid map)
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> getBlockedIds() {
        return Map.of();
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getAppellations(boolean computeFromLoaderApi) {
        if (computeFromLoaderApi) return GLOBAL_APPELLATIONS_VIEW;
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

    private static <T> void resolveRegistryState(ForgeRegistry<T> from, ForgeRegistry<T> to) {
        if (from == to) throw new IllegalArgumentException(String.format("Attempted to resolve registry state for the same registry! Registry: %s", from == null ? "null" : from.getRegistryKey()));
        if (!(from instanceof ForgeRegistryAccessor fromAccessor) || !(to instanceof ForgeRegistryAccessor toAccessor)) return;

        toAccessor.nexus$setModifiable(true);

        /*
         * TODO:
         *  - Resolve differences in names (ids and such effectively pertain to it soooo...)
         *  - Registry data migration ('to' takes precedence in id conflicts)
         *  - Migrate rest of registry state (aliases, blocked ids, etc.)
         */



        toAccessor.nexus$setModifiable(false);
    }
}
