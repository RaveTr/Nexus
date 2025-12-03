package com.mememan.nexus.internal.event.client;

import com.mememan.nexus.client.block_entity.BlockEntityClientData;
import com.mememan.nexus.client.block_entity.BlockEntitySheetData;
import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.client.item.WrappedClampedItemPropertyFunction;
import com.mememan.nexus.internal.services.ForgeRegistrar;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block_entity.BlockEntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.item.ItemPropertyWrapper;
import com.mememan.nexus.util.ClientUtil;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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

        // BlockEntity Sheet Data
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(BlockEntityTypePropertyWrapper.class).stream()
                .map(curPW -> (BlockEntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> curPW.getBlockEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(NexusForgeClientSetupEvents::registerBlockEntitySheetData);
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(EntityTypePropertyWrapper.class).stream()
                .map(curPW -> (EntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> curPW.getEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> registerEntityRenderer(curPW, event));

        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(BlockEntityTypePropertyWrapper.class).stream()
                .map(curPW -> (BlockEntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> curPW.getBlockEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> registerBlockEntityRenderer(curPW, event));
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitionsEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(EntityTypePropertyWrapper.class).stream()
                .map(curPW -> (EntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> curPW.getEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> registerEntityModelLayerDefinitions(curPW, event));

        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(BlockEntityTypePropertyWrapper.class).stream()
                .map(curPW -> (BlockEntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> curPW.getBlockEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> registerBlockEntityModelLayerDefinitions(curPW, event));
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

    private static <E extends Entity> void registerEntityRenderer(EntityTypePropertyWrapper<E> targetETPW, EntityRenderersEvent.RegisterRenderers event) {
        targetETPW.getEntityClientData().ifPresent(curClientData -> {
            EntityClientData<E> clientData = curClientData.get();

            if (clientData != null) {
                Function<EntityRendererProvider.Context, EntityRenderer<E>> entityRendererMapper = clientData.entityRendererMapper();

                if (entityRendererMapper != null) event.registerEntityRenderer(targetETPW.getParentObject().get(), entityRendererMapper::apply);
            }
        });
    }

    private static <BE extends BlockEntity> void registerBlockEntityRenderer(BlockEntityTypePropertyWrapper<BE> targetBEPW, EntityRenderersEvent.RegisterRenderers event) {
        targetBEPW.getBlockEntityClientData().ifPresent(curClientData -> {
            BlockEntityClientData<BE> clientData = curClientData.get();

            if (clientData != null) {
                Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> blockEntityRendererMapper = clientData.blockEntityRendererMapper();

                if (blockEntityRendererMapper != null) event.registerBlockEntityRenderer(targetBEPW.getParentObject().get(), blockEntityRendererMapper::apply);
            }
        });
    }

    private static <BE extends BlockEntity> void registerBlockEntitySheetData(BlockEntityTypePropertyWrapper<BE> targetBEPW) {
        targetBEPW.getBlockEntityClientData().ifPresent(curClientData -> {
            BlockEntityClientData<BE> clientData = curClientData.get();

            if (clientData != null) {
                Supplier<BlockEntityType<BE>> parentObj = targetBEPW.getParentObject();
                @Nullable Function<Supplier<BlockEntityType<BE>>, Collection<BlockEntitySheetData>> blockEntitySheetDataMapper = clientData.blockEntitySheetDataMapper();

                if (blockEntitySheetDataMapper != null) {
                    Collection<BlockEntitySheetData> collectedMappedSheetData = blockEntitySheetDataMapper.apply(parentObj);

                    collectedMappedSheetData.forEach(mappedSheetData -> {
                        WoodType signWoodType = mappedSheetData.signWoodType();
                        WoodType hangingSignWoodType = mappedSheetData.hangingSignWoodType();
                        ResourceKey<BannerPattern> bannerPatternKey = mappedSheetData.bannerPatternKey();
                        ResourceKey<BannerPattern> shieldPatternKey = mappedSheetData.shieldPatternKey();
                        ResourceKey<String> decoratedPotMaterialName = mappedSheetData.decoratedPotMaterialName();

                        if (signWoodType != null) Sheets.SIGN_MATERIALS.put(signWoodType, ClientUtil.createSignMaterial(signWoodType));
                        if (hangingSignWoodType != null) Sheets.HANGING_SIGN_MATERIALS.put(hangingSignWoodType, ClientUtil.createHangingSignMaterial(hangingSignWoodType));
                        if (bannerPatternKey != null) Sheets.BANNER_MATERIALS.put(bannerPatternKey, ClientUtil.createBannerMaterial(bannerPatternKey));
                        if (shieldPatternKey != null) Sheets.SHIELD_MATERIALS.put(shieldPatternKey, ClientUtil.createShieldMaterial(shieldPatternKey));
                        if (decoratedPotMaterialName != null) Sheets.DECORATED_POT_MATERIALS.put(decoratedPotMaterialName, ClientUtil.createDecoratedPotMaterial(decoratedPotMaterialName));
                    });
                }
            }
        });
    }

    private static <E extends Entity> void registerEntityModelLayerDefinitions(EntityTypePropertyWrapper<E> targetETPW, EntityRenderersEvent.RegisterLayerDefinitions event) {
        targetETPW.getEntityClientData().ifPresent(curClientData -> {
            EntityClientData<E> clientData = curClientData.get();

            if (clientData != null) {
                Function<Supplier<EntityType<E>>, Pair<Collection<ModelLayerLocation>, LayerDefinition>> mappedLayerDefSup = clientData.modelLayerDefinitionMapper();

                if (mappedLayerDefSup != null) {
                    Pair<Collection<ModelLayerLocation>, LayerDefinition> mappedModelLayerDef = mappedLayerDefSup.apply(targetETPW.getParentObject());

                    if (mappedModelLayerDef != null) {
                        Collection<ModelLayerLocation> layerLocs = mappedModelLayerDef.left();
                        LayerDefinition layerDef = mappedModelLayerDef.right();

                        if (layerLocs != null && layerDef != null && !layerLocs.isEmpty()){
                            layerLocs.forEach(layerLoc -> event.registerLayerDefinition(layerLoc, () -> layerDef));
                        }
                    }
                }
            }
        });
    }

    private static <BE extends BlockEntity> void registerBlockEntityModelLayerDefinitions(BlockEntityTypePropertyWrapper<BE> targetBETPW, EntityRenderersEvent.RegisterLayerDefinitions event) {
        targetBETPW.getBlockEntityClientData().ifPresent(curClientData -> {
            BlockEntityClientData<BE> clientData = curClientData.get();

            if (clientData != null) {
                Function<Supplier<BlockEntityType<BE>>, Pair<Collection<ModelLayerLocation>, LayerDefinition>> layerDefMapper = clientData.mappedModelLayerDefinitions();

                if (layerDefMapper != null) {
                    Pair<Collection<ModelLayerLocation>, LayerDefinition> mappedModelLayerDef = layerDefMapper.apply(targetBETPW.getParentObject());

                    if (mappedModelLayerDef != null) {
                        Collection<ModelLayerLocation> layerLocs = mappedModelLayerDef.left();
                        LayerDefinition layerDef = mappedModelLayerDef.right();

                        if (layerLocs != null && layerDef != null && !layerLocs.isEmpty()) {
                            layerLocs.forEach(layerLoc -> event.registerLayerDefinition(layerLoc, () -> layerDef));
                        }
                    }
                }
            }
        });
    }
}