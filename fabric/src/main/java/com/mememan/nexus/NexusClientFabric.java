package com.mememan.nexus;

import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.internal.services.FabricNetworkManager;
import com.mememan.nexus.network.BasePacket;
import com.mememan.nexus.network.NetworkSide;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.item.ItemPropertyWrapper;
import com.mememan.nexus.util.ClientUtil;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Needed for some loader-specific optimizations/implementations. Handles client-side mod initialization for Nexus on
 * Fabric.
 */
public class NexusClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerClientNetworkReceivers();

        registerColorProviders();
        registerItemModelPredicates();

        registerEntityRenderersAndModelLayerDefinitions();
    }

    private static <MSGT> void registerClientNetworkReceivers() {
        FabricNetworkManager.getMappedPackets().values().stream()
                .filter(curPacket -> curPacket.targetSide() == NetworkSide.S2C)
                .map(curPacket -> (BasePacket<MSGT>) curPacket)
                .forEach(packet -> {
                    ClientPlayNetworking.registerGlobalReceiver(packet.packetId(), ((targetClient, clientPacketListener, buf, fabricPacketSender) -> {
                        buf.readByte();

                        packet.packetHandler().apply(packet.packetDecoder().apply(buf)).handlePacket(ClientUtil.getClientPlayer(), ClientUtil.getClientLevel(), clientPacketListener.getConnection(), NetworkSide.S2C);
                    }));
                });
    }

    private static <IL extends ItemLike> void registerColorProviders() {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class).stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<IL, ?, ?>) curPW)
                .forEach(curPW -> {
                    if (curPW instanceof BlockPropertyWrapper<?> curBPW) registerBlockColorProvider(curBPW);
                    if (curPW instanceof ItemPropertyWrapper<?> curIPW) registerItemColorProvider(curIPW);
                });
    }

    private static <B extends Block> void registerBlockColorProvider(BlockPropertyWrapper<B> targetBPW) {
        targetBPW.getBlockColorMapper().ifPresent(curMapper -> {
            Supplier<B> parentBlockSup = targetBPW.getParentObject();
            B parentBlock = parentBlockSup.get();

            BlockColor resultBlockColor = ClientUtil.toBlockColor(curMapper.apply(parentBlockSup));

            if (resultBlockColor != null) {
                ColorProviderRegistry.BLOCK.register(resultBlockColor, parentBlock);
                ColorProviderRegistry.ITEM.register((curStack, tintIdx) -> resultBlockColor.getColor(parentBlock.defaultBlockState(), null, null, tintIdx),parentBlock);
            }
        });
    }

    private static <I extends Item> void registerItemColorProvider(ItemPropertyWrapper<I> targetIPW) {
        targetIPW.getItemColorMapper().ifPresent(curMapper -> {
            Supplier<I> parentItemSup = targetIPW.getParentObject();
            I parentItem = parentItemSup.get();

            ItemColor resultItemColor = ClientUtil.toItemColor(curMapper.apply(parentItemSup));

            if (resultItemColor != null) ColorProviderRegistry.ITEM.register(resultItemColor, parentItem);
        });
    }

    private static <I extends Item> void registerItemModelPredicates() {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(ItemPropertyWrapper.class).stream()
                .map(curPW -> (ItemPropertyWrapper<I>) curPW)
                .filter(curPW -> !curPW.getItemModelPredicates().isEmpty())
                .forEach(curPW -> curPW.getItemModelPredicates().forEach((predicateId, curItemPropertyFunc) -> ItemProperties.register(curPW.getParentObject().get(), predicateId, ClientUtil.toClampedItemPropertyFunction(curItemPropertyFunc))));
    }

    private static <E extends Entity> void registerEntityRenderersAndModelLayerDefinitions() {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(EntityTypePropertyWrapper.class).stream()
                .map(curPW -> (EntityTypePropertyWrapper<E>) curPW)
                .filter(curPW -> curPW.getEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> {
                    curPW.getEntityClientData().ifPresent(curClientData -> {
                        EntityClientData<E> clientData = curClientData.get();

                        if (clientData != null) {
                            Function<EntityRendererProvider.Context, EntityRenderer<E>> entityRendererMapper = clientData.entityRendererMapper();
                            Supplier<Pair<ModelLayerLocation, LayerDefinition>> mappedLayerDefSup = clientData.mappedModelLayerDefinition();

                            if (entityRendererMapper != null) EntityRendererRegistry.register(curPW.getParentObject().get(), entityRendererMapper::apply);

                            if (mappedLayerDefSup != null && mappedLayerDefSup.get() != null) {
                                Pair<ModelLayerLocation, LayerDefinition> mappedModelLayerDef = mappedLayerDefSup.get();
                                ModelLayerLocation layerLoc = mappedModelLayerDef.left();
                                LayerDefinition layerDef = mappedModelLayerDef.right();

                                if (layerLoc != null && layerDef != null) EntityModelLayerRegistry.registerModelLayer(layerLoc, () -> layerDef);
                            }
                        }
                    });
                });
    }
}
