package com.mememan.nexus.internal.event.common;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.internal.ForgeVanillaCompat;
import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncPacket;
import com.mememan.nexus.internal.services.ForgeRegistrar;
import com.mememan.nexus.item.standard.ItemPropertyWrapper;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import com.mememan.nexus.tag.TagWrapper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Internal event {@code class} responsible for handling {@code static} memory caching of different {@code PropertyWrapper}
 * implementations' properties and retrieving them for Vanilla compatibility features. Also handles some registrar-related
 * operations (particularly for resource reload listeners).
 *
 * @see ForgeVanillaCompat
 */
public class NexusForgeCommonMiscEvents {
    private static final Object2ObjectOpenHashMap<Block, Object2ObjectOpenHashMap<ToolAction, Function<Supplier<Block>, BlockState>>> CACHED_BLOCK_TOOL_ACTIONS = new Object2ObjectOpenHashMap<>();
    private static final Object2IntOpenHashMap<Item> CACHED_FUEL_TIME = new Object2IntOpenHashMap<>();
    private static final ObjectArrayList<PreparableReloadListener> CACHED_RESOURCE_RELOAD_LISTENERS = new ObjectArrayList<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, Pair<PreparableReloadListener, ResourceReloadListenerConfig<PreparableReloadListener>>> CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<Block, Function<Supplier<Block>, ObjectObjectMutablePair<Predicate<UseOnContext>, Consumer<UseOnContext>>>> CACHED_BLOCK_TILLING_BEHAVIOURS = new Object2ObjectOpenHashMap<>(); // Separate cause hoe tilling is handled differently

    static { // Handle action-mapping once on static initialization
        // Blocks
        BlockPropertyWrapper.getMappedBpws().forEach((parentBlockSup, curBpw) -> {
            Function<Supplier<Block>, Supplier<Block>> strippedBlockVariant = curBpw.getBlockStrippingMappingFunc();
            Function<Supplier<Block>, BlockState> flattenedBlockVariant = curBpw.getBlockFlatteningMappingFunc();
            Function<Supplier<Block>, ObjectObjectMutablePair<Predicate<UseOnContext>, Consumer<UseOnContext>>> parentBlockTillingBehaviourPair = curBpw.getBlockTillingMappingFunc();
            Integer cookTime = curBpw.getBlockFuelMappingFunc() == null ? null : Math.abs(curBpw.getBlockFuelMappingFunc().apply(parentBlockSup));

            if (strippedBlockVariant != null) {
                CACHED_BLOCK_TOOL_ACTIONS
                        .computeIfAbsent(parentBlockSup.get(), parentBlock -> new Object2ObjectOpenHashMap<>())
                        .put(ToolActions.AXE_STRIP, parentBlock -> strippedBlockVariant.apply(parentBlock).get().defaultBlockState());
            }

            if (flattenedBlockVariant != null) {
                CACHED_BLOCK_TOOL_ACTIONS
                        .computeIfAbsent(parentBlockSup.get(), parentBlock -> new Object2ObjectOpenHashMap<>())
                        .put(ToolActions.SHOVEL_FLATTEN, flattenedBlockVariant);
            }

            // Gonna hack HoeItem using a quick mixin to totally override Forge's imposed/defaulted behaviour pair
            if (parentBlockTillingBehaviourPair != null) CACHED_BLOCK_TILLING_BEHAVIOURS.put(parentBlockSup.get(), parentBlockTillingBehaviourPair); // Instead of #putIfAbsent cuz we don't mind updates
            if (cookTime != null && cookTime != 0) CACHED_FUEL_TIME.put(parentBlockSup.get().asItem(), (int) cookTime);
        });

        // Items
        ItemPropertyWrapper.getMappedIpws().forEach((parentItemSup, curIpw) -> {
            Integer cookTime = curIpw.getItemFuelMappingFunc() == null ? null : Math.abs(curIpw.getItemFuelMappingFunc().apply(parentItemSup));

            if (cookTime != null && cookTime != 0) CACHED_FUEL_TIME.put(parentItemSup.get(), (int) cookTime);
        });

        // Tags
        TagWrapper.getCachedTWEntries().stream().filter(curTw -> curTw.getParentTag().get().isFor(Registries.ITEM) && curTw.getCookTime() != 0).forEach(curTw -> {
            for (Holder<Item> itemEntryHolder : BuiltInRegistries.ITEM.getTagOrEmpty((TagKey<Item>) curTw.getParentTag().get())) {
                CACHED_FUEL_TIME.put(itemEntryHolder.value(), Math.abs(curTw.getCookTime()));
            }
        });

        // Resource Reload Listeners
        ForgeRegistrar.getCachedResourceReloadListeners().values().stream()
                .filter(curListenerPair -> curListenerPair.second()
                        .map(curListenerConfig -> curListenerConfig.listenerPackType() == PackType.SERVER_DATA)
                        .orElse(false))
                .map(Pair::first)
                .forEach(CACHED_RESOURCE_RELOAD_LISTENERS::add);
        ForgeRegistrar.getCachedResourceReloadListeners().entrySet().stream()
                .filter(curListenerEntry -> curListenerEntry.getValue().second()
                        .map(curListenerConfig -> curListenerConfig.listenerPackType() == PackType.SERVER_DATA && curListenerConfig.shouldSyncToClient())
                        .orElse(false))
                .forEach(curListenerEntry -> CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS.putIfAbsent(curListenerEntry.getKey(), ObjectObjectImmutablePair.of(curListenerEntry.getValue().first(), curListenerEntry.getValue().second().get())));
    }

    @SubscribeEvent
    public static void onBlockToolModificationEvent(BlockEvent.BlockToolModificationEvent event) { // Handling tool actions (stripping, tilling, flattening) with as little performance overhead and intrusion as possible
        Block targetBlock = event.getState().getBlock(); // Initial state btw
        Object2ObjectOpenHashMap<ToolAction, Function<Supplier<Block>, BlockState>> toolActionMap = CACHED_BLOCK_TOOL_ACTIONS.get(targetBlock);

        if (toolActionMap != null) {
            Function<Supplier<Block>, BlockState> stateTransformationAction = toolActionMap.get(event.getToolAction());

            if (stateTransformationAction != null) {
                BlockState transformedState = stateTransformationAction.apply(() -> targetBlock); // Not using Suppliers#ofInstance since this could probably change

                if (transformedState != null) event.setFinalState(transformedState);
            }
        }
    }

    @SubscribeEvent
    public static void onFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        Item targetItem = event.getItemStack().getItem();
        int cookTime = CACHED_FUEL_TIME.getOrDefault(targetItem, 0);

        if (cookTime != 0) event.setBurnTime(cookTime);
    }

    @SubscribeEvent
    public static void onAddReloadListenerEvent(AddReloadListenerEvent event) {
        CACHED_RESOURCE_RELOAD_LISTENERS.forEach(event::addListener);
    }

    @SubscribeEvent
    public static void onDatapackSyncEvent(OnDatapackSyncEvent event) {
        ServerPlayer primaryPlayer = event.getPlayer();

        CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS.forEach((curListenerId, curListenerPair) -> {
            PreparableReloadListener listener = curListenerPair.first();
            ResourceReloadListenerConfig<PreparableReloadListener> listenerConfig = curListenerPair.second();
            Optional<Function<PreparableReloadListener, Map<ResourceLocation, ?>>> listenerDataMapper = listenerConfig.dataMapGetter();

            listenerDataMapper.ifPresentOrElse(dataMapper -> {
                DatapackEntriesSyncPacket<?> syncPacket = new DatapackEntriesSyncPacket<>(curListenerId, dataMapper.apply(listener));

                if (primaryPlayer != null) NexusServices.NETWORK_MANAGER.sendToClient(syncPacket, primaryPlayer);
                else NexusServices.NETWORK_MANAGER.sendToAllClients(syncPacket);
            }, () -> NexusConstants.LOGGER.warn("Skipping syncable resource reload listener of id '{}' due to missing data mapper.", curListenerId));
        });
    }
}