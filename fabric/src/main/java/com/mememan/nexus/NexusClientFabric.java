package com.mememan.nexus;

import com.mememan.nexus.client.block_entity.BlockEntityClientData;
import com.mememan.nexus.client.block_entity.BlockEntitySheetData;
import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.internal.services.FabricNetworkManager;
import com.mememan.nexus.network.BasePacket;
import com.mememan.nexus.network.NetworkSide;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block_entity.BlockEntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.item.ItemPropertyWrapper;
import com.mememan.nexus.util.ClientUtil;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Needed for some loader-specific optimizations/implementations. Handles client-side mod initialization for Nexus on
 * Fabric.
 */
public class NexusClientFabric implements ClientModInitializer {
    private static final Object2ObjectOpenHashMap<ResourceLocation, RenderType> RENDER_TYPE_LOOKUP = Util.make(new Object2ObjectOpenHashMap<>(), typeMap -> {
        // Basic render types
        typeMap.put(new ResourceLocation("solid"), RenderType.solid());
        typeMap.put(new ResourceLocation("cutout_mipped"), RenderType.cutoutMipped());
        typeMap.put(new ResourceLocation("cutout"), RenderType.cutout());
        typeMap.put(new ResourceLocation("translucent"), RenderType.translucent());
        typeMap.put(new ResourceLocation("translucent_moving_block"), RenderType.translucentMovingBlock());
        typeMap.put(new ResourceLocation("translucent_no_crumbling"), RenderType.translucentNoCrumbling());
        
        // Special render types [Most things under here are a big fat TBD/are unused for now]
        typeMap.put(new ResourceLocation("leash"), RenderType.leash());
        typeMap.put(new ResourceLocation("water_mask"), RenderType.waterMask());
        typeMap.put(new ResourceLocation("armor_glint"), RenderType.armorGlint());
        typeMap.put(new ResourceLocation("armor_entity_glint"), RenderType.armorEntityGlint());
        typeMap.put(new ResourceLocation("glint_translucent"), RenderType.glintTranslucent());
        typeMap.put(new ResourceLocation("glint"), RenderType.glint());
        typeMap.put(new ResourceLocation("glint_direct"), RenderType.glintDirect());
        typeMap.put(new ResourceLocation("entity_glint"), RenderType.entityGlint());
        typeMap.put(new ResourceLocation("entity_glint_direct"), RenderType.entityGlintDirect());
        typeMap.put(new ResourceLocation("lightning"), RenderType.lightning());
        typeMap.put(new ResourceLocation("tripwire"), RenderType.tripwire());
        typeMap.put(new ResourceLocation("end_portal"), RenderType.endPortal());
        typeMap.put(new ResourceLocation("end_gateway"), RenderType.endGateway());
        
        // Line render types
        typeMap.put(new ResourceLocation("lines"), RenderType.lines());
        typeMap.put(new ResourceLocation("line_strip"), RenderType.lineStrip());
        typeMap.put(new ResourceLocation("debug_line_strip"), RenderType.debugLineStrip(1.0));
        typeMap.put(new ResourceLocation("debug_filled_box"), RenderType.debugFilledBox());
        typeMap.put(new ResourceLocation("debug_quads"), RenderType.debugQuads());
        typeMap.put(new ResourceLocation("debug_section_quads"), RenderType.debugSectionQuads());
        
        // GUI render types
        typeMap.put(new ResourceLocation("gui"), RenderType.gui());
        typeMap.put(new ResourceLocation("gui_overlay"), RenderType.guiOverlay());
        typeMap.put(new ResourceLocation("gui_text_highlight"), RenderType.guiTextHighlight());
        typeMap.put(new ResourceLocation("gui_ghost_recipe_overlay"), RenderType.guiGhostRecipeOverlay());
        
        // Text render types
        typeMap.put(new ResourceLocation("text"), RenderType.text(new ResourceLocation("textures/font/ascii.png")));
        typeMap.put(new ResourceLocation("text_background"), RenderType.textBackground());
        typeMap.put(new ResourceLocation("text_intensity"), RenderType.textIntensity(new ResourceLocation("textures/font/ascii.png")));
        typeMap.put(new ResourceLocation("text_polygon_offset"), RenderType.textPolygonOffset(new ResourceLocation("textures/font/ascii.png")));
        typeMap.put(new ResourceLocation("text_intensity_polygon_offset"), RenderType.textIntensityPolygonOffset(new ResourceLocation("textures/font/ascii.png")));
        typeMap.put(new ResourceLocation("text_see_through"), RenderType.textSeeThrough(new ResourceLocation("textures/font/ascii.png")));
        typeMap.put(new ResourceLocation("text_background_see_through"), RenderType.textBackgroundSeeThrough());
        typeMap.put(new ResourceLocation("text_intensity_see_through"), RenderType.textIntensitySeeThrough(new ResourceLocation("textures/font/ascii.png")));
    });

    @Override
    public void onInitializeClient() {
        registerClientNetworkReceivers();

        registerColorProviders();
        registerItemModelPredicates();

        registerBlockEntityRenderersAndSheetDataAndModelLayerDefinitions(); // Mmmmmm peak Java method naming
        registerEntityRenderersAndModelLayerDefinitions();

        registerRenderTypes();
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

    private static <BE extends BlockEntity> void registerBlockEntityRenderersAndSheetDataAndModelLayerDefinitions() {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(BlockEntityTypePropertyWrapper.class).stream()
                .map(curPW -> (BlockEntityTypePropertyWrapper<BE>) curPW)
                .filter(curPW -> curPW.getBlockEntityClientData().filter(curClientData -> curClientData.get() != null).isPresent())
                .forEach(curPW -> {
                    curPW.getBlockEntityClientData().ifPresent(curClientData -> {
                        BlockEntityClientData<BE> clientData = curClientData.get();

                        if (clientData != null) {
                            Supplier<BlockEntityType<BE>> parentObj = curPW.getParentObject();
                            Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> blockEntityRendererMapper = clientData.blockEntityRendererMapper();
                            Function<Supplier<BlockEntityType<BE>>, Collection<BlockEntitySheetData>> blockEntitySheetDataMapper = clientData.blockEntitySheetDataMapper();
                            Supplier<Pair<Collection<ModelLayerLocation>, LayerDefinition>> mappedLayerDefSup = clientData.mappedModelLayerDefinitions();

                            if (blockEntityRendererMapper != null) BlockEntityRenderers.register(parentObj.get(), blockEntityRendererMapper::apply);
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
                            if (mappedLayerDefSup != null && mappedLayerDefSup.get() != null) {
                                Pair<Collection<ModelLayerLocation>, LayerDefinition> mappedModelLayerDef = mappedLayerDefSup.get();
                                Collection<ModelLayerLocation> layerLocs = mappedModelLayerDef.left();
                                LayerDefinition layerDef = mappedModelLayerDef.right();

                                if (layerLocs != null && layerDef != null && !layerLocs.isEmpty()) {
                                    layerLocs.forEach(layerLoc -> EntityModelLayerRegistry.registerModelLayer(layerLoc, () -> layerDef));
                                }
                            }
                        }
                    });
                });
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
                            Function<Supplier<EntityType<E>>, Pair<Collection<ModelLayerLocation>, LayerDefinition>> layerDefMapper = clientData.modelLayerDefinitionMapper();

                            if (entityRendererMapper != null) EntityRendererRegistry.register(curPW.getParentObject().get(), entityRendererMapper::apply);

                            if (layerDefMapper != null) {
                                Pair<Collection<ModelLayerLocation>, LayerDefinition> mappedModelLayerDefs = layerDefMapper.apply(curPW.getParentObject());
                                Collection<ModelLayerLocation> layerLocs = mappedModelLayerDefs.left();
                                LayerDefinition layerDef = mappedModelLayerDefs.right();

                                if (layerLocs != null && layerDef != null && !layerLocs.isEmpty()) {
                                    layerLocs.forEach(layerLoc -> EntityModelLayerRegistry.registerModelLayer(layerLoc, () -> layerDef));
                                }
                            }
                        }
                    });
                });
    }

    private static <T> void registerRenderTypes() {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(ModelBasedPropertyWrapper.class).stream()
                .map(curPW -> (ModelBasedPropertyWrapper<T, ?, ?>) curPW)
                .filter(curPW -> curPW.getModelDefinition()
                        .map(curDef -> curDef.apply(curPW.getParentObject()).getRenderType().isPresent())
                        .orElse(false))
                .forEach(curPW -> {
                    Supplier<T> parentObjectSup = curPW.getParentObject();

                    // JIC + Functional style go brrr
                    curPW.getModelDefinition().flatMap(curDef -> curDef.apply(parentObjectSup).getRenderType()).ifPresent(curRenderType -> {
                        T parentObject = parentObjectSup.get();

                        if (parentObject instanceof Block parentBlock && RENDER_TYPE_LOOKUP.containsKey(curRenderType)) BlockRenderLayerMap.INSTANCE.putBlock(parentBlock, RENDER_TYPE_LOOKUP.get(curRenderType));
                        if (parentObject instanceof Fluid parentFluid && RENDER_TYPE_LOOKUP.containsKey(curRenderType)) BlockRenderLayerMap.INSTANCE.putFluid(parentFluid, RENDER_TYPE_LOOKUP.get(curRenderType));
                    });
                });
    }
}
