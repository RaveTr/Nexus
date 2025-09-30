package com.mememan.nexus.mixins.fabric_api.resource_loader;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.internal.services.FabricRegistrar;
import com.mememan.nexus.mixins.server.PlayerListMixin;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This mixin {@code class} exists solely due to the fact that Fabric wants to be different instead of allowing you to
 * register a {@link PreparableReloadListener} of your own choosing and mapping that to a {@link ResourceLocation}.
 * <br></br>
 * In other words, this {@code class} handles resource reload listener registration for Fabric.
 *
 * @see PlayerListMixin
 */
@Mixin(value = ResourceManagerHelperImpl.class, remap = false)
public abstract class ResourceManagerHelperImplMixin {

    private ResourceManagerHelperImplMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (ResourceManagerHelperImplMixin)");
    }

    @Inject(method = "sort(Lnet/minecraft/server/packs/PackType;Ljava/util/List;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/resource/loader/ResourceManagerHelperImpl;sort(Ljava/util/List;)V", shift = At.Shift.AFTER))
    private static void nexus$registerSidedResourceReloadListeners(PackType type, List<PreparableReloadListener> listeners, CallbackInfoReturnable<List<PreparableReloadListener>> cir, @Local(name = "mutable") List<PreparableReloadListener> mutable) {
        Object2ObjectOpenHashMap<ResourceLocation, Pair<PreparableReloadListener, Optional<ResourceReloadListenerConfig<PreparableReloadListener>>>> cachedReloadListeners = FabricRegistrar.getCachedResourceReloadListeners().entrySet().stream()
                .filter(curEntry -> curEntry.getValue().second().map(curConfig -> Objects.equals(curConfig.listenerPackType(), type)).orElse(false))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, Object2ObjectOpenHashMap::new));

        if (!cachedReloadListeners.isEmpty()) {
            cachedReloadListeners.forEach((curListenerId, curListenerPair) -> mutable.add(curListenerPair.first()));
        }
    }
}
