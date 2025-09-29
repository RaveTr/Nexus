package com.mememan.nexus.internal.event.client;

import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.client.item.WrappedClampedItemPropertyFunction;
import com.mememan.nexus.internal.services.ForgeRegistrar;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.item.ItemPropertyWrapper;
import com.mememan.nexus.util.ClientUtil;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Internal event {@code class} responsible for handling client-side registration of different {@code PropertyWrapper}
 * implementations on startup.
 */
public class NexusForgeClientSetupEvents {

    @SubscribeEvent
    public static void onFMLClientSetupEvent(FMLClientSetupEvent event) {
        // Item Model Properties
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(ItemPropertyWrapper.class)
                .stream()
                .map(curPW -> (ItemPropertyWrapper<?>) curPW)
                .forEach(curPW -> {
                    Supplier<? extends Item> itemSupEntry = curPW.getParentObject();
                    Map<ResourceLocation, WrappedClampedItemPropertyFunction> targetItemModelPredicates = curPW.getItemModelPredicates();

                    if (!targetItemModelPredicates.isEmpty()) {
                        targetItemModelPredicates.forEach((curName, curFunc) -> {
                            ClampedItemPropertyFunction clampedPropertyFunc = ClientUtil.toClampedItemPropertyFunction(curFunc);

                            if (clampedPropertyFunc != null) ItemProperties.register(itemSupEntry.get(), curName, clampedPropertyFunc);
                        });
                    }
                });
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(EntityTypePropertyWrapper.class).stream()
                .map(curPW -> (EntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> curPW.getEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> registerEntityRenderers(curPW, event));
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitionsEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(EntityTypePropertyWrapper.class).stream()
                .map(curPW -> (EntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> curPW.getEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> registerEntityModelLayerDefinitions(curPW, event));
    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlersEvent(RegisterColorHandlersEvent.Block event) {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(BlockPropertyWrapper.class)
                .stream()
                .map(curPW -> (BlockPropertyWrapper<Block>) curPW)
                .forEach(curPW -> {
                    Supplier<Block> parentBlockSup = curPW.getParentObject();
                    Block parentBlock = parentBlockSup.get();

                    curPW.getBlockColorMapper().ifPresent(curMapper -> {
                        BlockColor mappedBlockColor = ClientUtil.toBlockColor(curMapper.apply(parentBlockSup));

                        if (mappedBlockColor != null) event.register(mappedBlockColor, parentBlock);
                    });
                });
    }

    @SubscribeEvent
    public static void onRegisterItemColorHandlersEvent(RegisterColorHandlersEvent.Item event) {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class)
                .stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<?, ?, ?>) curPW)
                .forEach(curPW -> {
                    if (curPW instanceof BlockPropertyWrapper<?> curBPW) registerBlockItemColorProvider(curBPW, event);
                    if (curPW instanceof ItemPropertyWrapper<?> curIPW) registerItemColorProvider(curIPW, event);
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

    private static <B extends Block> void registerBlockItemColorProvider(BlockPropertyWrapper<B> targetBPW, RegisterColorHandlersEvent.Item itemRegEvent) {
        Supplier<B> parentBlockSup = targetBPW.getParentObject();
        B parentBlock = parentBlockSup.get();

        targetBPW.getBlockColorMapper().ifPresent(curMapper -> {
            BlockColor mappedBlockColor = ClientUtil.toBlockColor(curMapper.apply(parentBlockSup));

            if (mappedBlockColor != null) itemRegEvent.register((curStack, tintIdx) -> itemRegEvent.getBlockColors().getColor(parentBlock.defaultBlockState(), null, null, tintIdx), parentBlock);
        });
    }

    private static <I extends Item> void registerItemColorProvider(ItemPropertyWrapper<I> targetIPW, RegisterColorHandlersEvent.Item itemRegEvent) {
        Supplier<I> parentItemSup = targetIPW.getParentObject();
        I parentItem = parentItemSup.get();

        targetIPW.getItemColorMapper().ifPresent(curMapper -> {
            ItemColor mappedItemColor = ClientUtil.toItemColor(curMapper.apply(parentItemSup));

            if (mappedItemColor != null) itemRegEvent.register(mappedItemColor, parentItem);
        });
    }

    private static <E extends Entity> void registerEntityRenderers(EntityTypePropertyWrapper<E> targetETPW, EntityRenderersEvent.RegisterRenderers event) {
        targetETPW.getEntityClientData().ifPresent(curClientData -> {
            EntityClientData<E> clientData = curClientData.get();

            if (clientData != null) {
                Function<EntityRendererProvider.Context, EntityRenderer<E>> entityRendererMapper = clientData.entityRendererMapper();

                if (entityRendererMapper != null) event.registerEntityRenderer(targetETPW.getParentObject().get(), entityRendererMapper::apply);
            }
        });
    }

    private static <E extends Entity> void registerEntityModelLayerDefinitions(EntityTypePropertyWrapper<E> targetETPW, EntityRenderersEvent.RegisterLayerDefinitions event) {
        targetETPW.getEntityClientData().ifPresent(curClientData -> {
            EntityClientData<E> clientData = curClientData.get();

            if (clientData != null) {
                Supplier<Pair<ModelLayerLocation, LayerDefinition>> mappedLayerDefSup = clientData.mappedModelLayerDefinition();

                if (mappedLayerDefSup != null && mappedLayerDefSup.get() != null) {
                    Pair<ModelLayerLocation, LayerDefinition> mappedModelLayerDef = mappedLayerDefSup.get();
                    ModelLayerLocation layerLoc = mappedModelLayerDef.left();
                    LayerDefinition layerDef = mappedModelLayerDef.right();

                    if (layerLoc != null && layerDef != null) event.registerLayerDefinition(layerLoc, () -> layerDef);
                }
            }
        });
    }
}