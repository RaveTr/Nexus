package com.mememan.nexus.mixins.forge.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.Multimap;
import com.mememan.nexus.internal.loader.ForgeRegistryHookManager;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import com.mememan.nexus.loader.RegistryHookManager;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.BitSet;
import java.util.Map;

/**
 * Accessor {@code interface} that exposes stored aliases for Forge's {@code NameSpacedWrapper} delegates, as well as
 * wrapper checks for internal registry API use within Nexus.
 *
 * @see ForgeRegistry
 * @see ForgeRegistryHookManager#getAppellations(boolean)
 * @see ForgeRegistryHookManager#updateActiveRegistryState(ResourceKey, RegistryHookManager.ActiveRegistryMapper)
 * @see NexusRegistryDataManager
 */
@Mixin(value = ForgeRegistry.class, remap = false)
public interface ForgeRegistryAccessor {

    @Accessor("ids")
    BiMap<Integer, ResourceLocation> nexus$getIds();

    @Accessor("names")
    BiMap<ResourceLocation, Object> nexus$getNames();

    @Accessor("keys")
    BiMap<ResourceKey<Object>, Object> nexus$getKeys();

    @Accessor("aliases")
    Map<ResourceLocation, ResourceLocation> nexus$getAliases();

    @Accessor("blocked")
    IntSet nexus$getBlockedIds();

    @Accessor("availabilityMap")
    BitSet nexus$getAvailabilityMap();

    @Accessor("overrides")
    Multimap<ResourceLocation, Object> nexus$getOverrides();

    @Accessor("min")
    int nexus$getMinId();

    @Accessor("max")
    int nexus$getMaxId();

    @Invoker("getOverrideOwners")
    Map<ResourceLocation, String> nexus$getOverrideOwners();

    @Accessor("isModifiable")
    @Mutable
    void nexus$setModifiable(boolean modifiable);

    @Accessor("allowOverrides")
    @Mutable
    void nexus$allowOverrides(boolean allowOverrides);

    @Accessor("hasWrapper")
    boolean nexus$hasWrapper();

    @Invoker("block")
    void nexus$block(int id);

    @Invoker("sync")
    void nexus$sync(ResourceLocation name, ForgeRegistry<?> from);

    @Invoker("resetDelegates")
    void nexus$resetDelegates();

    @Invoker("validateContent")
    void nexus$validateContent(ResourceLocation registryName);

    @Invoker("dump")
    void nexus$dump(ResourceLocation registryName);

    @Invoker("getIDRaw")
    int nexus$getIDRaw(ResourceLocation key);
}
