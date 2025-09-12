package com.mememan.nexus.mixins.fabric_api.resource_loader;

import com.google.common.collect.ImmutableMap;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.internal.services.FabricRegistrar;
import com.mememan.nexus.mixins.server.PlayerListMixin;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

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

    @Inject(method = "sort(Ljava/util/List;)V", at = @At("TAIL"))
    private void nexus$registerResourceReloadListeners(List<PreparableReloadListener> listeners, CallbackInfo ci) {
        ImmutableMap<ResourceLocation, Pair<PreparableReloadListener, Optional<ResourceReloadListenerConfig<PreparableReloadListener>>>> cachedReloadListeners = FabricRegistrar.getCachedResourceReloadListeners();

        if (!cachedReloadListeners.isEmpty()) {
            cachedReloadListeners.forEach((curListenerId, curListenerPair) -> {
                Optional<ResourceReloadListenerConfig<PreparableReloadListener>> listenerConfig = curListenerPair.second();

                listenerConfig.ifPresentOrElse(curListenerConfig -> {
                    listeners.add(curListenerPair.first());
                }, () -> NexusConstants.LOGGER.warn("Skipping registration for resource reload listener of id '{}' due to missing listener config.", curListenerId));
            });
        }
    }
}
