package com.mememan.nexus.internal.event.common;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.internal.ForgeVanillaCompat;
import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncPacket;
import com.mememan.nexus.internal.services.ForgeRegistrar;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapper;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
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
    private static final Object2ObjectOpenHashMap<ToolAction, Object2ObjectOpenHashMap<Block, Function<Supplier<Block>, BlockState>>> CACHED_BLOCK_TOOL_ACTIONS = Util.make(new Object2ObjectOpenHashMap<>(), toolActionMap -> {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(BlockPropertyWrapper.class)
                .stream()
                .map(curPW -> (BlockPropertyWrapper<Block>) curPW)
                .forEach(curBPW -> {
                    Supplier<Block> parentBlockSup = curBPW.getParentObject();
                    Block parentBlock = parentBlockSup.get();

                    curBPW.getBlockStrippingMapper().ifPresent(strippedBlockMapper -> toolActionMap
                            .computeIfAbsent(ToolActions.AXE_STRIP, axeStripAction -> new Object2ObjectOpenHashMap<>())
                            .put(parentBlock, strippedBlockMapper));
                    curBPW.getBlockFlatteningMapper().ifPresent(flatteningMapper -> toolActionMap
                            .computeIfAbsent(ToolActions.SHOVEL_FLATTEN, shovelFlattenAction -> new Object2ObjectOpenHashMap<>())
                            .put(parentBlock, flatteningMapper));
                });
    });
    private static final Object2IntOpenHashMap<Item> CACHED_FUEL_TIME = Util.make(new Object2IntOpenHashMap<>(), fuelTimeMap -> {
        // ItemLikes (General)
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class)
                .stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<ItemLike, ?, ?>) curPW)
                .forEach(curPW -> {
                    curPW.getFuelMapper().ifPresent(fuelMapper -> {
                        Supplier<ItemLike> parentItemLikeSup = curPW.getParentObject();

                        fuelTimeMap.put(parentItemLikeSup.get().asItem(), Math.abs(fuelMapper.apply(parentItemLikeSup)));
                    });
                });

        // Tags (Items)
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(TagPropertyWrapper.class)
                .stream()
                .map(curPW -> (TagPropertyWrapper<?, ? extends TagKey<?>>) curPW)
                .filter(curPW -> curPW.getParentObject().get().isFor(Registries.ITEM) && curPW.getCookTime().isPresent())
                .map(curPW -> (TagPropertyWrapper<Item, TagKey<Item>>) curPW)
                .forEach(curPW -> {
                    TagKey<Item> parentTagKeySup = curPW.getParentObject().get();

                    curPW.getCookTime().ifPresent(cookTime -> // Functional-style expression (we can already guarantee that it's present but meh, whatever)
                            BuiltInRegistries.ITEM.getTagOrEmpty(parentTagKeySup)
                                    .forEach(curItemHolder -> fuelTimeMap.put(curItemHolder.value(), Math.abs(cookTime)))
                    );
                });
    });
    private static final ObjectArrayList<PreparableReloadListener> CACHED_RESOURCE_RELOAD_LISTENERS = Util.make(new ObjectArrayList<>(), resourceReloadListenerList -> {
        ForgeRegistrar.getCachedResourceReloadListeners().values().stream()
                .filter(curListenerPair -> curListenerPair.second()
                        .map(curListenerConfig -> curListenerConfig.listenerPackType() == PackType.SERVER_DATA)
                        .orElse(false))
                .map(Pair::first)
                .forEach(resourceReloadListenerList::add);
    });
    private static final Object2ObjectOpenHashMap<ResourceLocation, Pair<PreparableReloadListener, ResourceReloadListenerConfig<PreparableReloadListener>>> CACHED_SYNCABLE_RESOURCE_RELOAD_LISTENERS = Util.make(new Object2ObjectOpenHashMap<>(), syncableResourceReloadListenerMap -> {
        ForgeRegistrar.getCachedResourceReloadListeners().entrySet().stream()
                .filter(curListenerEntry -> curListenerEntry.getValue().second()
                        .map(curListenerConfig -> curListenerConfig.listenerPackType() == PackType.SERVER_DATA && curListenerConfig.shouldSyncToClient())
                        .orElse(false))
                .forEach(curListenerEntry -> syncableResourceReloadListenerMap.putIfAbsent(curListenerEntry.getKey(), ObjectObjectImmutablePair.of(curListenerEntry.getValue().first(), curListenerEntry.getValue().second().get())));
    });
    public static final Object2ObjectOpenHashMap<Block, Function<Supplier<Block>, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>> CACHED_BLOCK_TILLING_BEHAVIOURS = Util.make(new Object2ObjectOpenHashMap<>(), tillingBehaviourMap -> {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(BlockPropertyWrapper.class)
                .stream()
                .map(curPW -> (BlockPropertyWrapper<Block>) curPW)
                .forEach(curBPW -> {
                    Supplier<Block> parentBlockSup = curBPW.getParentObject();
                    Block parentBlock = parentBlockSup.get();

                    curBPW.getBlockTillingMapper().ifPresent(tillingMapper -> tillingBehaviourMap.put(parentBlock, tillingMapper));
                });
    }); // Separate cause hoe tilling is handled differently

    @SubscribeEvent
    public static void onBlockToolModificationEvent(BlockEvent.BlockToolModificationEvent event) { // Handling tool actions (stripping, tilling, flattening) with as little performance overhead and intrusion as possible
        ToolAction stateTransformationAction = event.getToolAction();

        if (stateTransformationAction == null) return; // JIC

        Object2ObjectOpenHashMap<Block, Function<Supplier<Block>, BlockState>> toolActionMap = CACHED_BLOCK_TOOL_ACTIONS.get(stateTransformationAction);

        if (toolActionMap != null && !toolActionMap.isEmpty()) {
            Block initialBlock = event.getState().getBlock();
            Function<Supplier<Block>, BlockState> stateTransformer = toolActionMap.get(initialBlock);

            if (stateTransformer != null) {
                BlockState transformedState = stateTransformer.apply(() -> initialBlock); // Not using Suppliers#ofInstance since this could probably change

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