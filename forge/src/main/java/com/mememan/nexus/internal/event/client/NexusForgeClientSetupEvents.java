package com.mememan.nexus.internal.event.client;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.client.item.WrappedClampedItemPropertyFunction;
import com.mememan.nexus.internal.services.ForgeRegistrar;
import com.mememan.nexus.item.standard.ItemPropertyWrapper;
import com.mememan.nexus.util.ClientUtil;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Supplier;

/**
 * Internal event {@code class} responsible for handling client-side registration of different {@code PropertyWrapper}
 * implementations on startup.
 */
@Mod.EventBusSubscriber(modid = NexusConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NexusForgeClientSetupEvents {

    @SubscribeEvent
    public static void onFMLClientSetupEvent(FMLClientSetupEvent event) {
        // Item Model Properties
        ItemPropertyWrapper.getMappedIpws().entrySet().stream().filter(curEntry -> !curEntry.getValue().getItemModelPredicates().isEmpty()).forEach(curEntry -> {
            Supplier<Item> itemSupEntry = curEntry.getKey();
            Object2ObjectOpenHashMap<ResourceLocation, WrappedClampedItemPropertyFunction> definedModelPredicateFunctions = curEntry.getValue().getItemModelPredicates();

            definedModelPredicateFunctions.forEach((curName, curFunc) -> ItemProperties.register(itemSupEntry.get(), curName, ClientUtil.toClampedItemPropertyFunction(curFunc)));
        });
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {

    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitionsEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlersEvent(RegisterColorHandlersEvent.Block event) {
        BlockPropertyWrapper.getMappedBpws().entrySet().stream().filter(curBwpEntry -> curBwpEntry.getValue().getBlockColorMappingFunc() != null).forEach(curBwpEntry -> {
            Supplier<Block> blockSupEntry = curBwpEntry.getKey();
            BlockColor curMappedBlockColor = ClientUtil.toBlockColor(curBwpEntry.getValue().getBlockColorMappingFunc().apply(blockSupEntry));

            event.register(curMappedBlockColor, blockSupEntry.get());
        });
    }

    @SubscribeEvent
    public static void onRegisterItemColorHandlersEvent(RegisterColorHandlersEvent.Item event) {
        BlockPropertyWrapper.getMappedBpws().entrySet().stream().filter(curBwpEntry -> curBwpEntry.getValue().getBlockColorMappingFunc() != null).forEach(curBwpEntry -> {
            Supplier<Block> blockSupEntry = curBwpEntry.getKey();

            event.register((curStack, tintIdx) -> event.getBlockColors().getColor(blockSupEntry.get().defaultBlockState(), null, null, tintIdx), blockSupEntry.get());
        });
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListenersEvent(RegisterClientReloadListenersEvent event) {
        ForgeRegistrar.getCachedResourceReloadListeners().values().stream()
                .filter(curListenerPair -> curListenerPair.second()
                        .map(curListenerConfig -> curListenerConfig.listenerPackType() == PackType.CLIENT_RESOURCES)
                        .orElse(false))
                .map(Pair::first)
                .forEach(event::registerReloadListener);
    }
}