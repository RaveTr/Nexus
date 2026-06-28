package com.mememan.nexus.internal.logic;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncPacket;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import com.mememan.nexus.template.event.blueprint.server.ServerLifeCycleEventBlueprint;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Internal handler {@code class} that handles miscellaneous server-based logic tasks for different parts of Nexus API
 * (primarily via event hooks).
 *
 * @apiNote An assumption is made by this {@code class} that all event handlers routed (registered) through Nexus API
 * that all {@linkplain PreparableReloadListener PreparableReloadListeners} have been registered before post-load
 * state kicks in (i.e. the timing of {@link PostInit}).
 *
 * @see ServerLifeCycleEventBlueprint
 */
@PostInit
public final class NexusServerLifeCycleManager {
    private static final Object2ObjectOpenHashMap<ResourceLocation, Pair<PreparableReloadListener, ResourceReloadListenerConfig<PreparableReloadListener>>> CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS = Util.make(new Object2ObjectOpenHashMap<>(), syncableResourceReloadListenerMap -> {
        NexusServices.REGISTRAR.getMappedResourceReloadListeners().entrySet().stream()
                .filter(curListenerEntry -> curListenerEntry.getValue().second()
                        .map(curListenerConfig -> curListenerConfig.listenerPackType() == PackType.SERVER_DATA && curListenerConfig.shouldSyncToClient())
                        .orElse(false))
                .forEach(curListenerEntry -> syncableResourceReloadListenerMap.putIfAbsent(curListenerEntry.getKey(), ObjectObjectImmutablePair.of(curListenerEntry.getValue().first(), curListenerEntry.getValue().second().get())));
    });

    private NexusServerLifeCycleManager() {

    }

    static {
        handleServerResourceReloadListeners();
    }

    private static void handleServerResourceReloadListeners() {
        ServerLifeCycleEventBlueprint.DATAPACK_INDIVIDUAL_SYNC.onEvent(event -> {
            ServerPlayer targetPlayer = event.getTargetPlayer();

            CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS.forEach((curListenerId, curListenerPair) -> {
                PreparableReloadListener listener = curListenerPair.first();
                ResourceReloadListenerConfig<PreparableReloadListener> listenerConfig = curListenerPair.second();
                Optional<Function<PreparableReloadListener, Map<ResourceLocation, ?>>> listenerDataMapper = listenerConfig.dataMapGetter();

                listenerDataMapper.ifPresentOrElse(dataMapper -> {
                    DatapackEntriesSyncPacket<?> syncPacket = new DatapackEntriesSyncPacket<>(curListenerId, dataMapper.apply(listener));

                    NexusServices.NETWORK_MANAGER.sendToClient(syncPacket, targetPlayer);
                }, () -> NexusConstants.LOGGER.warn("Skipping syncable resource reload listener of id '{}' due to missing data mapper.", curListenerId));
            });
        });
    }
}
