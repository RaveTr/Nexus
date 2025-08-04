package com.mememan.nexus;

import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.client.item.WrappedClampedItemPropertyFunction;
import com.mememan.nexus.item.standard.ItemPropertyWrapper;
import com.mememan.nexus.util.ClientUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Needed for some loader-specific optimizations/implementations. Handles client-side mod initialization for Nexus on
 * Fabric.
 */
public class NexusClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerBlockColorProviders();
        registerItemColorProviders();

        registerItemModelPredicates();
    }

    private static void registerBlockColorProviders() {
        BlockPropertyWrapper.getMappedBpws().entrySet().stream().filter(curBwpEntry -> curBwpEntry.getValue().getBlockColorMappingFunc() != null).forEach(curBwpEntry -> {
            Supplier<Block> blockSupEntry = curBwpEntry.getKey();
            BlockColor curMappedBlockColor = ClientUtil.toBlockColor(curBwpEntry.getValue().getBlockColorMappingFunc().apply(blockSupEntry));

            ColorProviderRegistry.BLOCK.register(curMappedBlockColor, blockSupEntry.get());
        });
    }

    private static void registerItemColorProviders() {
        BlockPropertyWrapper.getMappedBpws().entrySet().stream().filter(curBwpEntry -> curBwpEntry.getValue().getBlockColorMappingFunc() != null).forEach(curBwpEntry -> { // Need to isolate both methods (in terms of blocks), since otherwise block is null within the same method's scope when retrieved from the ColorProviderRegistry
            Supplier<Block> blockSupEntry = curBwpEntry.getKey();
            BlockColor curMappedBlockColor = ClientUtil.toBlockColor(curBwpEntry.getValue().getBlockColorMappingFunc().apply(blockSupEntry));

            if (curMappedBlockColor == null) return; // Failsafe for initial nullity (how)

            ColorProviderRegistry.ITEM.register((curStack, tintIdx) -> curMappedBlockColor.getColor(blockSupEntry.get().defaultBlockState(), null, null, tintIdx), blockSupEntry.get());
        });
    }

    private static void registerItemModelPredicates() {
        ItemPropertyWrapper.getMappedIpws().entrySet().stream().filter(curEntry -> !curEntry.getValue().getItemModelPredicates().isEmpty()).forEach(curEntry -> {
            Supplier<Item> itemSupEntry = curEntry.getKey();
            Object2ObjectOpenHashMap<ResourceLocation, WrappedClampedItemPropertyFunction> definedModelPredicateFunctions = curEntry.getValue().getItemModelPredicates();

            definedModelPredicateFunctions.forEach((curName, curFunc) -> ItemProperties.register(itemSupEntry.get(), curName, ClientUtil.toClampedItemPropertyFunction(curFunc)));
        });
    }
}
