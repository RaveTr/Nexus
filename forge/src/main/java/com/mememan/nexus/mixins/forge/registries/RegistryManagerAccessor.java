package com.mememan.nexus.mixins.forge.registries;

import com.google.common.collect.BiMap;
import com.mememan.nexus.internal.loader.ForgeRegistryHookManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

/**
 * Accessor {@code interface} that exposes all registry state information for Forge's {@code RegistryManager}. Used
 * internally by Nexus to handle registry state synchronization and migration.
 *
 * @see RegistryManager
 * @see ForgeRegistryHookManager
 */
@Mixin(value = RegistryManager.class, remap = false)
public interface RegistryManagerAccessor {

    @Accessor("registries")
    BiMap<ResourceLocation, ForgeRegistry<?>> nexus$getRegistries();

    @Accessor("persisted")
    Set<ResourceLocation> nexus$getPersisted();

    @Accessor("synced")
    Set<ResourceLocation> nexus$getSynced();

    @Accessor("legacyNames")
    Map<ResourceLocation, ResourceLocation> nexus$getLegacyNames();
}
