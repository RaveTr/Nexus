package com.mememan.nexus.mixins.registries;

import com.mememan.nexus.internal.loader.FabricRegistryHookManager;
import com.mememan.nexus.loader.RegistryHookManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Accessor {@code interface} exposing some internal fields in {@link MappedRegistry} for querying purposes. Primarily
 * used by Nexus to handle registry state synchronization and migration.
 *
 * @see FabricRegistryHookManager#updateActiveRegistryState(ResourceKey, RegistryHookManager.ActiveRegistryMapper)
 * @see MappedRegistryMixin
 */
@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor {

    @Accessor("unregisteredIntrusiveHolders")
    Map<Object, Holder.Reference<Object>> nexus$getUnregisteredIntrusiveHolders();

    @Accessor("byId")
    ObjectList<Holder.Reference<Object>> nexus$getIdList();

    @Accessor("toId")
    Object2IntMap<Object> nexus$getToId();

    @Accessor("byLocation")
    Map<ResourceLocation, Holder.Reference<Object>> nexus$getByLocation();

    @Accessor("byKey")
    Map<ResourceKey<Object>, Holder.Reference<Object>> nexus$getByKey();

    @Accessor("byValue")
    Map<Object, Holder.Reference<Object>> nexus$getByValue();

    @Accessor("frozen")
    @Mutable
    void nexus$setFrozen(boolean frozen);

    @Accessor("nextId")
    int nexus$getNextId();

    @Accessor("nextId")
    @Mutable
    void nexus$setNextId(int nextId);
}
