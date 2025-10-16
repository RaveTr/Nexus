package com.mememan.nexus.util;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.client.model.block.BlockModelDefinition;
import com.mememan.nexus.client.model.item.ItemModelDefinition;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.*;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utility {@code class} containing helpful model shortcut/delegator helper methods, as well as some re-used constants
 * related to models in general.
 */
public final class ModelUtil {
    public static final ModelTemplate EMPTY_MODEL_TEMPLATE = new ModelTemplate(Optional.empty(), Optional.empty());
    public static final TextureMapping EMPTY_TEXTURE_MAPPING = new TextureMapping();
    public static final ResourceLocation SOLID_RENDER_TYPE = new ResourceLocation("solid");
    public static final ResourceLocation CUTOUT_MIPPED_RENDER_TYPE = new ResourceLocation("cutout_mipped");
    public static final ResourceLocation CUTOUT_RENDER_TYPE = new ResourceLocation("cutout");
    public static final ResourceLocation TRANSLUCENT_RENDER_TYPE = new ResourceLocation("translucent");
    public static final ResourceLocation TRANSLUCENT_MOVING_BLOCK_RENDER_TYPE = new ResourceLocation("translucent_moving_block");

    private ModelUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (ModelUtil)");
    }

    /**
     * Shortcut method to allow for the definition of a single parent {@link ModelTemplate} without any additional
     * parameters (textures, etc.).
     *
     * @param parentModelLoc The {@link ResourceLocation} pointing towards the parent model.
     *
     * @return A {@link ModelTemplate} with the specified parent model.
     */
    public static ModelTemplate fromLocation(ResourceLocation parentModelLoc) {
        return new ModelTemplate(Optional.of(parentModelLoc), Optional.empty());
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template and prompty generates
     * an {@link ItemModelDefinition} using the {@code ownerBlockSup} to grab the prompt model location.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#ALL} -> {@code RegistryUtil.pickBlockPrefix(blockTexLoc)}</li>
     *     </ul>
     *
     * @param ownerBlockSup The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                      automatic model location resolution.
     * @param blockTexLoc The {@link ResourceLocation} pointing towards the block texture for the block model.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template.
     *
     * @see #simpleBlockState(Supplier)
     */
    public static BlockModelDefinition cubeAll(Supplier<Block> ownerBlockSup, ResourceLocation blockTexLoc) {
        return new BlockModelDefinition(ModelTemplates.CUBE_ALL)
                .withTextureMapping(TextureMapping.cube(RegistryUtil.pickBlockPrefix(blockTexLoc)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(ownerBlockSup.get()))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template and prompty generates
     * an {@link ItemModelDefinition} using the {@code ownerBlockSup} to grab the prompt model location.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#ALL} -> {@code RegistryUtil.getTextureLocationOrDefault(ownerBlockSup)}</li>
     *     </ul>
     *
     * @param ownerBlockSup The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                      automatic texture location resolution (as shown above).
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template.
     *
     * @see #simpleBlockState(Supplier)
     */
    public static BlockModelDefinition cubeAll(Supplier<Block> ownerBlockSup) {
        return cubeAll(ownerBlockSup, RegistryUtil.getTextureLocationOrDefault(ownerBlockSup));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_BOTTOM_TOP} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     </ul>
     *
     * @param ownerBlockSup The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                      automatic model location resolution.
     * @param sideTexture The {@link ResourceLocation} representing the texture of the 4 horizontal faces of a standard
     *                    cube block (N, S, E, W).
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the bottom vertical face of a
     *                      standard cube block (D).
     * @param topTexture The {@link ResourceLocation} representing the texture of the top vertical face of a standard
     *                   cube block (U).
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_BOTTOM_TOP} template.
     *
     * @see #simpleBlockState(Supplier)
     * @see #cubeBottomTop(Supplier)
     */
    public static BlockModelDefinition cubeBottomTop(Supplier<Block> ownerBlockSup, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
        return new BlockModelDefinition(ModelTemplates.CUBE_BOTTOM_TOP)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.SIDE, RegistryUtil.pickBlockPrefix(sideTexture))
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(ownerBlockSup.get()))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_BOTTOM_TOP} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(ownerBlockSup, "_side")}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(ownerBlockSup, "_bottom")}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(ownerBlockSup, "_top")}</li>
     *     </ul>
     *
     * @param ownerBlockSup The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                      automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_BOTTOM_TOP} template.
     *
     * @see #simpleBlockState(Supplier)
     * @see #cubeBottomTop(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition cubeBottomTop(Supplier<Block> ownerBlockSup) {
        return cubeBottomTop(
                ownerBlockSup,
                RegistryUtil.getTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_side"),
                RegistryUtil.getTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_bottom"),
                RegistryUtil.getTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_top")
        );
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template for leaves blocks.
     * This method automatically sets the render type to {@link #CUTOUT_MIPPED_RENDER_TYPE} for proper transparency
     * handling of leaf textures. The model will be automatically named using the leaves block's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#ALL} -> {@code RegistryUtil.pickBlockPrefix(leavesTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the leaves {@link Block} to be used for
     *                    automatic model location resolution.
     * @param leavesTexture The {@link ResourceLocation} representing the texture of the leaves.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template and {@link #CUTOUT_MIPPED_RENDER_TYPE}.
     *
     * @see #leaves(Supplier)
     * @see #cubeAll(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition leaves(Supplier<Block> targetBlock, ResourceLocation leavesTexture) {
        return cubeAll(targetBlock, leavesTexture)
                .withRenderType(CUTOUT_MIPPED_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #leaves(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#CUBE_ALL} template for leaves blocks using automatic texture resolution.
     * This method automatically sets the render type to {@link #CUTOUT_MIPPED_RENDER_TYPE} for proper transparency
     * handling of leaf textures. Automatically determines the leaves texture based on the leaves block's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#ALL} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the leaves {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template and {@link #CUTOUT_MIPPED_RENDER_TYPE}.
     *
     * @see #leaves(Supplier, ResourceLocation)
     * @see #cubeAll(Supplier)
     */
    public static BlockModelDefinition leaves(Supplier<Block> targetBlock) {
        return leaves(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CARPET} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WOOL} -> {@code RegistryUtil.pickBlockPrefix(carpetTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the carpet {@link Block} to be used for
     *                    automatic model location resolution.
     * @param carpetTexture The {@link ResourceLocation} representing the texture of the carpet.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CARPET} template.
     *
     * @see #simpleBlockState(Supplier)
     * @see #carpet(Supplier)
     */
    public static BlockModelDefinition carpet(Supplier<Block> targetBlock, ResourceLocation carpetTexture) {
        return new BlockModelDefinition(ModelTemplates.CARPET)
                .withTextureMapping(TextureMapping.wool(RegistryUtil.pickBlockPrefix(carpetTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Overloaded variant of {@link #carpet(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#CARPET} template using automatic texture resolution.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WOOL} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the carpet {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CARPET} template.
     *
     * @see #simpleBlockState(Supplier)
     * @see #carpet(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition carpet(Supplier<Block> targetBlock) {
        return carpet(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CROSS} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(crossTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the cross {@link Block} to be used for
     *                    automatic model location resolution.
     * @param crossTexture The {@link ResourceLocation} representing the texture of the cross. Item model uses the same
     *                     texture.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CROSS} template.
     *
     * @see #cross(Supplier)
     */
    public static BlockModelDefinition cross(Supplier<Block> targetBlock, ResourceLocation crossTexture) {
        return new BlockModelDefinition(ModelTemplates.CROSS)
                .withTextureMapping(TextureMapping.cross(RegistryUtil.pickBlockPrefix(crossTexture)))
                .withOrdinalModelDefinition(generatedBlock(crossTexture));
    }

    /**
     * Overloaded variant of {@link #cross(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#CROSS} template using automatic texture resolution.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the cross {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CROSS} template.
     *
     * @see #cross(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition cross(Supplier<Block> targetBlock) {
        return cross(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CROSS} template and sets the render type
     * to {@link #CUTOUT_RENDER_TYPE} for proper transparency handling.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(crossTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the cross {@link Block} to be used for
     *                    automatic model location resolution.
     * @param crossTexture The {@link ResourceLocation} representing the texture of the cross.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CROSS} template and {@link #CUTOUT_RENDER_TYPE}.
     *
     * @see #crossCutout(Supplier)
     * @see #cross(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition crossCutout(Supplier<Block> targetBlock, ResourceLocation crossTexture) {
        return cross(targetBlock, crossTexture)
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #crossCutout(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#CROSS} template using automatic texture resolution and sets the render type
     * to {@link #CUTOUT_RENDER_TYPE} for proper transparency handling.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the cross {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CROSS} template and {@link #CUTOUT_RENDER_TYPE}.
     *
     * @see #crossCutout(Supplier, ResourceLocation)
     * @see #cross(Supplier)
     */
    public static BlockModelDefinition crossCutout(Supplier<Block> targetBlock) {
        return crossCutout(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#TINTED_CROSS} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(tintedCrossTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the tinted cross {@link Block} to be used for
     *                    automatic model location resolution.
     * @param tintedCrossTexture The {@link ResourceLocation} representing the texture of the tinted cross. Item model
     *                           uses the same texture.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TINTED_CROSS} template.
     *
     * @see #tintedCross(Supplier)
     */
    public static BlockModelDefinition tintedCross(Supplier<Block> targetBlock, ResourceLocation tintedCrossTexture) {
        return new BlockModelDefinition(ModelTemplates.TINTED_CROSS)
                .withTextureMapping(TextureMapping.cross(RegistryUtil.pickBlockPrefix(tintedCrossTexture)))
                .withOrdinalModelDefinition(generatedBlock(tintedCrossTexture));
    }

    /**
     * Overloaded variant of {@link #tintedCross(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#TINTED_CROSS} template using automatic texture resolution.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the tinted cross {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TINTED_CROSS} template.
     *
     * @see #tintedCross(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition tintedCross(Supplier<Block> targetBlock) {
        return tintedCross(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#TINTED_CROSS} template and sets the render type
     * to {@link #CUTOUT_RENDER_TYPE} for proper transparency handling.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(tintedCrossTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the tinted cross {@link Block} to be used for
     *                    automatic model location resolution.
     * @param tintedCrossTexture The {@link ResourceLocation} representing the texture of the tinted cross.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TINTED_CROSS} template and {@link #CUTOUT_RENDER_TYPE}.
     *
     * @see #tintedCrossCutout(Supplier)
     * @see #tintedCross(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition tintedCrossCutout(Supplier<Block> targetBlock, ResourceLocation tintedCrossTexture) {
        return tintedCross(targetBlock, tintedCrossTexture)
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #tintedCrossCutout(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#TINTED_CROSS} template using automatic texture resolution and sets the render type
     * to {@link #CUTOUT_RENDER_TYPE} for proper transparency handling.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the tinted cross {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TINTED_CROSS} template and {@link #CUTOUT_RENDER_TYPE}.
     *
     * @see #tintedCrossCutout(Supplier, ResourceLocation)
     * @see #tintedCross(Supplier)
     */
    public static BlockModelDefinition tintedCrossCutout(Supplier<Block> targetBlock) {
        return tintedCrossCutout(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FLOWER_POT_CROSS} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#PLANT} -> {@code RegistryUtil.pickBlockPrefix(flowerPotTexture)}</li>
     *     </ul>
     *
     * @param flowerPotTexture The {@link ResourceLocation} representing the texture of the potted flower.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FLOWER_POT_CROSS} template.
     *
     * @see #flowerPotCross(Supplier)
     */
    public static BlockModelDefinition flowerPotCross(ResourceLocation flowerPotTexture) {
        return new BlockModelDefinition(ModelTemplates.FLOWER_POT_CROSS)
                .withTextureMapping(TextureMapping.plant(RegistryUtil.pickBlockPrefix(flowerPotTexture)));
    }

    /**
     * Overloaded variant of {@link #flowerPotCross(ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FLOWER_POT_CROSS} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#PLANT} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the potted flower {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FLOWER_POT_CROSS} template.
     *
     * @see #flowerPotCross(ResourceLocation)
     */
    public static BlockModelDefinition flowerPotCross(Supplier<Block> targetBlock) {
        return flowerPotCross(RegistryUtil.getTextureLocationOrDefault(RegistryUtil.pickBlockId(
                        targetBlock,
                        parentBlockPath ->
                                parentBlockPath.startsWith("potted_")
                                        ? parentBlockPath.substring("potted_".length())
                                        : parentBlockPath)
                )
        );
    }

    /**
     * Creates a {@link BlockStateDefinition}, using {@link MultiVariantGenerator} with the {@link VariantProperties#MODEL}
     * property set to the supplied {@linkplain Block Block's} default model location.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link VariantProperties#MODEL} -> {@link ModelLocationUtils#getModelLocation(Block)}</li>
     *     </ul>
     *
     * @param targetBlock The {@linkplain Block Block} to use as the base for the {@link BlockStateDefinition}.
     *
     * @return A new {@link BlockStateDefinition} with a {@code simpleBlock} template.
     *
     * @see #cubeAll(Supplier)
     */
    public static BlockStateDefinition simpleBlockState(Supplier<Block> targetBlock) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get(), Variant.variant()
                        .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Creates two {@link BlockModelDefinition}s for a double plant block, with separate top and bottom {@link ModelTemplates#CROSS}
     * models using {@link #CUTOUT_RENDER_TYPE}. Generates a single item model from the bottom model.
     * <p>
     *     <h3>Required Texture Slots (Top Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     </ul>
     *     <h3>Required Texture Slots (Bottom Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the double plant {@link Block} to be used for
     *                    automatic model location resolution.
     * @param topTexture The {@link ResourceLocation} representing the texture of the upper half.
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the lower half.
     *
     * @return A {@link BlockModelDefinition} with nested top and bottom {@link ModelTemplates#CROSS} models.
     *
     * @see #doublePlant(Supplier)
     * @see #doublePlantBlockState(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition doublePlant(Supplier<Block> targetBlock, ResourceLocation topTexture, ResourceLocation bottomTexture) {
        return crossCutout(targetBlock, topTexture)
                .withOrdinalModelDefinition(crossCutout(targetBlock, bottomTexture)
                        .setOrdinalModelDefinitions(ObjectArrayList.of())); // Ensure generation of 1 item model, not 2
    }

    /**
     * Overloaded variant of {@link #doublePlant(Supplier, ResourceLocation, ResourceLocation)}. Creates two
     * {@link BlockModelDefinition}s for a double plant block using automatic texture resolution with {@code _top} and
     * {@code _bottom} suffixes.
     * <p>
     *     <h3>Required Texture Slots (Top Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top")}</li>
     *     </ul>
     *     <h3>Required Texture Slots (Bottom Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom")}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the double plant {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with nested top and bottom {@link ModelTemplates#CROSS} models.
     *
     * @see #doublePlant(Supplier, ResourceLocation, ResourceLocation)
     * @see #doublePlantBlockState(Supplier)
     */
    public static BlockModelDefinition doublePlant(Supplier<Block> targetBlock) {
        return doublePlant(targetBlock, RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top"), RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom"));
    }

    /**
     * Creates two {@link BlockModelDefinition}s for a tinted double plant block, with separate top and bottom
     * {@link ModelTemplates#TINTED_CROSS} models using {@link #CUTOUT_RENDER_TYPE}. Generates a single item model from
     * the bottom model.
     * <p>
     *     <h3>Required Texture Slots (Top Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     </ul>
     *     <h3>Required Texture Slots (Bottom Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the tinted double plant {@link Block} to be used for
     *                    automatic model location resolution.
     * @param topTexture The {@link ResourceLocation} representing the texture of the upper half.
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the lower half.
     *
     * @return A {@link BlockModelDefinition} with nested top and bottom {@link ModelTemplates#TINTED_CROSS} models.
     *
     * @see #tintedDoublePlant(Supplier)
     * @see #doublePlant(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition tintedDoublePlant(Supplier<Block> targetBlock, ResourceLocation topTexture, ResourceLocation bottomTexture) {
        return tintedCrossCutout(targetBlock, topTexture)
                .withOrdinalModelDefinition(tintedCrossCutout(targetBlock, bottomTexture)
                        .setOrdinalModelDefinitions(ObjectArrayList.of())); // Ensure generation of 1 item model, not 2
    }

    /**
     * Overloaded variant of {@link #tintedDoublePlant(Supplier, ResourceLocation, ResourceLocation)}. Creates two
     * {@link BlockModelDefinition}s for a tinted double plant block using automatic texture resolution with {@code _top}
     * and {@code _bottom} suffixes.
     * <p>
     *     <h3>Required Texture Slots (Top Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top")}</li>
     *     </ul>
     *     <h3>Required Texture Slots (Bottom Model)</h3>
     *     <ul>
     *         <li>{@link TextureSlot#CROSS} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom")}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the tinted double plant {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with nested top and bottom {@link ModelTemplates#TINTED_CROSS} models.
     *
     * @see #tintedDoublePlant(Supplier, ResourceLocation, ResourceLocation)
     * @see #doublePlant(Supplier)
     */
    public static BlockModelDefinition tintedDoublePlant(Supplier<Block> targetBlock) {
        return tintedDoublePlant(targetBlock, RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top"), RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom"));
    }

    /**
     * Creates a {@link BlockStateDefinition} for a double plant block using {@link MultiVariantGenerator} with
     * {@link BlockStateProperties#DOUBLE_BLOCK_HALF} property dispatch. Maps {@link DoubleBlockHalf#LOWER} to the
     * bottom model and {@link DoubleBlockHalf#UPPER} to the top model.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link DoubleBlockHalf#LOWER} -> {@link VariantProperties#MODEL} = {@code bottomModel}</li>
     *         <li>{@link DoubleBlockHalf#UPPER} -> {@link VariantProperties#MODEL} = {@code topModel}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the double plant {@link Block}.
     * @param topModel The {@link ResourceLocation} pointing to the model for the upper half.
     * @param bottomModel The {@link ResourceLocation} pointing to the model for the lower half.
     *
     * @return A {@link BlockStateDefinition} with {@link BlockStateProperties#DOUBLE_BLOCK_HALF} property dispatch.
     *
     * @see #doublePlantBlockState(Supplier)
     * @see #doublePlant(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockStateDefinition doublePlantBlockState(Supplier<Block> targetBlock, ResourceLocation topModel, ResourceLocation bottomModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF)
                                .select(DoubleBlockHalf.LOWER, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomModel))
                                .select(DoubleBlockHalf.UPPER, Variant.variant()
                                        .with(VariantProperties.MODEL, topModel))));
    }

    /**
     * Overloaded variant of {@link #doublePlantBlockState(Supplier, ResourceLocation, ResourceLocation)}. Creates a
     * {@link BlockStateDefinition} for a double plant block using automatic model location resolution with {@code _top}
     * and {@code _bottom} suffixes.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link DoubleBlockHalf#LOWER} -> {@link VariantProperties#MODEL} = {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom")}</li>
     *         <li>{@link DoubleBlockHalf#UPPER} -> {@link VariantProperties#MODEL} = {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top")}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the double plant {@link Block} to be used for
     *                    automatic model location resolution.
     *
     * @return A {@link BlockStateDefinition} with {@link BlockStateProperties#DOUBLE_BLOCK_HALF} property dispatch.
     *
     * @see #doublePlantBlockState(Supplier, ResourceLocation, ResourceLocation)
     * @see #doublePlant(Supplier)
     */
    public static BlockStateDefinition doublePlantBlockState(Supplier<Block> targetBlock) {
        return doublePlantBlockState(targetBlock, RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top"), RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_BOTTOM} template. Promptly generates
     * a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                    automatic model location resolution.
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the bottom face of the slab.
     * @param topTexture The {@link ResourceLocation} representing the texture of the top face of the slab.
     * @param sideTexture The {@link ResourceLocation} representing the texture of the side faces of the slab.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_BOTTOM} template.
     *
     * @see #slabTop(Supplier)
     * @see #slab(Supplier)
     */
    public static BlockModelDefinition slabBottom(Supplier<Block> targetBlock, ResourceLocation bottomTexture, ResourceLocation topTexture, ResourceLocation sideTexture) {
        return new BlockModelDefinition(ModelTemplates.SLAB_BOTTOM)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topTexture))
                        .put(TextureSlot.SIDE, RegistryUtil.pickBlockPrefix(sideTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_BOTTOM} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom")}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top")}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_BOTTOM} template.
     *
     * @see #slabBottom(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabTop(Supplier)
     * @see #slab(Supplier)
     */
    public static BlockModelDefinition slabBottom(Supplier<Block> targetBlock) {
        return slabBottom(targetBlock, RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom"), RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top"), RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_TOP} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                    automatic model location resolution.
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the bottom face of the slab.
     * @param topTexture The {@link ResourceLocation} representing the texture of the top face of the slab.
     * @param sideTexture The {@link ResourceLocation} representing the texture of the side faces of the slab.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_TOP} template.
     *
     * @see #slabBottom(Supplier)
     * @see #slab(Supplier)
     */
    public static BlockModelDefinition slabTop(Supplier<Block> targetBlock, ResourceLocation bottomTexture, ResourceLocation topTexture, ResourceLocation sideTexture) {
        ResourceLocation baseSlabId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.SLAB_TOP)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topTexture))
                        .put(TextureSlot.SIDE, RegistryUtil.pickBlockPrefix(sideTexture)))
                .withCustomName(baseSlabId.getPath().concat("_top"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_TOP} template.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom")}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top")}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#SLAB_TOP} template.
     *
     * @see #slabTop(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabBottom(Supplier)
     * @see #slab(Supplier)
     */
    public static BlockModelDefinition slabTop(Supplier<Block> targetBlock) {
        return slabTop(targetBlock, RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_bottom"), RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_top"), RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with both {@link ModelTemplates#SLAB_BOTTOM} and {@link ModelTemplates#SLAB_TOP}
     * templates.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                    automatic model location resolution.
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the bottom face of the slab.
     * @param topTexture The {@link ResourceLocation} representing the texture of the top face of the slab.
     * @param sideTexture The {@link ResourceLocation} representing the texture of the side faces of the slab.
     *
     * @return A {@link BlockModelDefinition} with both {@link ModelTemplates#SLAB_BOTTOM} and {@link ModelTemplates#SLAB_TOP} templates.
     *
     * @see #slabBottom(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabTop(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slab(Supplier)
     */
    public static BlockModelDefinition slab(Supplier<Block> targetBlock, ResourceLocation bottomTexture, ResourceLocation topTexture, ResourceLocation sideTexture) {
        return slabBottom(targetBlock, bottomTexture, topTexture, sideTexture)
                .withOrdinalModelDefinition(slabTop(targetBlock, bottomTexture, topTexture, sideTexture));
    }

    /**
     * Creates a {@link BlockModelDefinition} with both {@link ModelTemplates#SLAB_BOTTOM} and {@link ModelTemplates#SLAB_TOP}
     * templates. Automatically determines the slab textures based on the slab's registry ID ({@code chosenDoubleBlockId}).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with both {@link ModelTemplates#SLAB_BOTTOM} and {@link ModelTemplates#SLAB_TOP} templates.
     *
     * @see #slab(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabBottom(Supplier)
     * @see #slabTop(Supplier)
     */
    public static BlockModelDefinition slab(Supplier<Block> targetBlock) {
        ResourceLocation defaultedTexLoc = RegistryUtil.pickBlockTexture(targetBlock);

        return slab(targetBlock, defaultedTexLoc, defaultedTexLoc, defaultedTexLoc);
    }

    /**
     * Creates a {@link BlockStateDefinition} for a slab block using {@link MultiVariantGenerator} with different models
     * for each {@link SlabType}.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link SlabType#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomModel)}</li>
     *         <li>{@link SlabType#TOP} -> {@code RegistryUtil.pickBlockPrefix(topModel)}</li>
     *         <li>{@link SlabType#DOUBLE} -> {@code RegistryUtil.pickBlockPrefix(doubleSlabModel)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the slab {@link Block} to create the blockstate for.
     * @param bottomModel The {@link ResourceLocation} of the model to use for the bottom slab variant.
     * @param topModel The {@link ResourceLocation} of the model to use for the top slab variant.
     * @param doubleSlabModel The {@link ResourceLocation} of the model to use for the double slab variant.
     *
     * @return A {@link BlockStateDefinition} with slab-specific variants.
     *
     * @see #slabBlockState(Supplier, ResourceLocation)
     * @see #slabBlockState(Supplier)
     */
    public static BlockStateDefinition slabBlockState(Supplier<Block> targetBlock, ResourceLocation bottomModel, ResourceLocation topModel, ResourceLocation doubleSlabModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch
                                .property(BlockStateProperties.SLAB_TYPE)
                                .select(SlabType.BOTTOM, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(bottomModel)))
                                .select(SlabType.TOP, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(topModel)))
                                .select(SlabType.DOUBLE, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(doubleSlabModel)))));
    }

    /**
     * Overloaded variant of {@link #slabBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for a slab block using {@link MultiVariantGenerator} with different models
     * for each {@link SlabType}.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link SlabType#BOTTOM} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())}</li>
     *         <li>{@link SlabType#TOP} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_top")}</li>
     *         <li>{@link SlabType#DOUBLE} -> {@code doubleBlockModel}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the slab {@link Block} to create the blockstate for.
     * @param doubleBlockModel The {@link ResourceLocation} of the model to use for the double slab variant.
     *
     * @return A {@link BlockStateDefinition} with slab-specific variants.
     *
     * @see #slabBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabBlockState(Supplier)
     */
    public static BlockStateDefinition slabBlockState(Supplier<Block> targetBlock, ResourceLocation doubleBlockModel) {
        return slabBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get()), ModelLocationUtils.getModelLocation(targetBlock.get(), "_top"), doubleBlockModel);
    }

    /**
     * Overloaded variant of {@link #slabBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for a slab block using {@link MultiVariantGenerator} with different models
     * for each {@link SlabType}. Automatically determines the double slab model based on the slab's registry ID.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link SlabType#BOTTOM} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())}</li>
     *         <li>{@link SlabType#TOP} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_top")}</li>
     *         <li>{@link SlabType#DOUBLE} -> {@code RegistryUtil.pickBlockId(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the slab {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with slab-specific variants.
     *
     * @see #slabBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabBlockState(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition slabBlockState(Supplier<Block> targetBlock) {
        return slabBlockState(targetBlock, RegistryUtil.pickBlockId(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template. Promptly generates
     * a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     * @param sideTexture The side texture location for the stairs model.
     * @param bottomTexture The bottom texture location for the stairs model.
     * @param topTexture The top texture location for the stairs model.
     *
     * @return A new {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template.
     *
     * @see #stairsStraight(Supplier)
     */
    public static BlockModelDefinition stairsStraight(Supplier<Block> targetBlock, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
        return new BlockModelDefinition(ModelTemplates.STAIRS_STRAIGHT)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.SIDE, RegistryUtil.pickBlockPrefix(sideTexture))
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template. Automatically
     * determines the stairs textures based on the stairs' registry ID ({@code chosenBlockId}). Promptly generates
     * a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     *
     * @return A new {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template.
     *
     * @see #stairsStraight(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition stairsStraight(Supplier<Block> targetBlock) {
        ResourceLocation defaultedTexLoc = RegistryUtil.pickBlockTexture(targetBlock);

        return stairsStraight(targetBlock, defaultedTexLoc, defaultedTexLoc, defaultedTexLoc);
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for the inner
     * corner variant. Automatically determines the stairs textures based on the stairs' registry ID ({@code chosenBlockId}).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     * @param sideTexture The side texture location for the stairs inner model.
     * @param bottomTexture The bottom texture location for the stairs inner model.
     * @param topTexture The top texture location for the stairs inner model.
     *
     * @return A new {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for inner corners.
     *
     * @see #stairsInner(Supplier)
     */
    public static BlockModelDefinition stairsInner(Supplier<Block> targetBlock, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.STAIRS_INNER)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.SIDE, RegistryUtil.pickBlockPrefix(sideTexture))
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topTexture)))
                .withCustomName(baseStairsId.getPath().concat("_inner"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for the inner
     * corner variant. Automatically determines the stairs textures based on the stairs' registry ID ({@code chosenBlockId}).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     *
     * @return A new {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for inner corners.
     *
     * @see #stairsInner(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition stairsInner(Supplier<Block> targetBlock) {
        ResourceLocation defaultedTexLoc = RegistryUtil.pickBlockTexture(targetBlock);

        return stairsInner(targetBlock, defaultedTexLoc, defaultedTexLoc, defaultedTexLoc);
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for the outer
     * corner variant. Automatically determines the stairs textures based on the stairs' registry ID ({@code chosenBlockId}).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     * @param sideTexture The side texture location for the stairs outer model.
     * @param bottomTexture The bottom texture location for the stairs outer model.
     * @param topTexture The top texture location for the stairs outer model.
     *
     * @return A new {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for outer corners.
     *
     * @see #stairsOuter(Supplier)
     */
    public static BlockModelDefinition stairsOuter(Supplier<Block> targetBlock, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.STAIRS_OUTER)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.SIDE, RegistryUtil.pickBlockPrefix(sideTexture))
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topTexture)))
                .withCustomName(baseStairsId.getPath().concat("_outer"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for the outer
     * corner variant. Automatically determines the stairs textures based on the stairs' registry ID ({@code chosenBlockId}).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     *
     * @return A new {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for outer corners.
     *
     * @see #stairsOuter(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition stairsOuter(Supplier<Block> targetBlock) {
        ResourceLocation defaultedTexLoc = RegistryUtil.pickBlockTexture(targetBlock);

        return stairsOuter(targetBlock, defaultedTexLoc, defaultedTexLoc, defaultedTexLoc);
    }

    /**
     * Creates a {@link BlockModelDefinition} with all three stairs variants (straight, inner, outer) using the
     * {@link ModelTemplates#STAIRS_STRAIGHT} template. The straight variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockPrefix(sideTexture)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     * @param sideTexture The side texture location for all stairs models.
     * @param bottomTexture The bottom texture location for all stairs models.
     * @param topTexture The top texture location for all stairs models.
     *
     * @return A new {@link BlockModelDefinition} with all stairs variants.
     *
     * @see #stairs(Supplier)
     */
    public static BlockModelDefinition stairs(Supplier<Block> targetBlock, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
        return stairsStraight(targetBlock, sideTexture, bottomTexture, topTexture)
                .withOrdinalModelDefinitions(stairsInner(targetBlock, sideTexture, bottomTexture, topTexture), stairsOuter(targetBlock, sideTexture, bottomTexture, topTexture));
    }

    /**
     * Creates a {@link BlockModelDefinition} with all three stairs variants (straight, inner, outer) using the
     * {@link ModelTemplates#STAIRS_STRAIGHT} template. Automatically determines the stairs textures based on the
     * stairs' registry ID ({@code chosenBlockId}). The straight variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     *
     * @return A new {@link BlockModelDefinition} with all stairs variants.
     *
     * @see #stairs(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition stairs(Supplier<Block> targetBlock) {
        ResourceLocation defaultedTexLoc = RegistryUtil.pickBlockTexture(targetBlock);

        return stairs(targetBlock, defaultedTexLoc, defaultedTexLoc, defaultedTexLoc);
    }

    /**
     * Creates a {@link BlockStateDefinition} for stairs blocks using {@link MultiVariantGenerator} with different models
     * for each combination of facing, half, and shape.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#STRAIGHT} -> {@code RegistryUtil.pickBlockPrefix(straightStairsModel)}</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X rotation + UV lock</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X + 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X + 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X + 270° Y rotation + UV lock</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#OUTER_RIGHT} or {@link StairsShape#OUTER_LEFT} -> {@code RegistryUtil.pickBlockPrefix(outerStairsModel)}</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 0° rotation</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#INNER_RIGHT} or {@link StairsShape#INNER_LEFT} -> {@code RegistryUtil.pickBlockPrefix(innerStairsModel)}</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 0° rotation</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 180° Y rotation + UV lock</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to create the blockstate for.
     * @param straightStairsModel The model location for straight stairs.
     * @param innerStairsModel The model location for inner corner stairs.
     * @param outerStairsModel The model location for outer corner stairs.
     *
     * @return A {@link BlockStateDefinition} with comprehensive stairs blockstate variants.
     *
     * @see #stairsBlockState(Supplier)
     */
    public static BlockStateDefinition stairsBlockState(Supplier<Block> targetBlock, ResourceLocation straightStairsModel, ResourceLocation innerStairsModel, ResourceLocation outerStairsModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch
                                .properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE)
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel)))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel)))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel)))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel)))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel)))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(straightStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(outerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(innerStairsModel))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))));
    }

    /**
     * Overloaded variant of {@link #stairsBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for stairs blocks using {@link MultiVariantGenerator} with different models
     * for each combination of facing, half, and shape. Automatically determines model locations using standard naming
     * convention (base model, "_inner", "_outer" suffixes).
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#STRAIGHT} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())}</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#STRAIGHT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X rotation + UV lock</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X + 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X + 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@link StairsShape#STRAIGHT} -> 180° X + 270° Y rotation + UV lock</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#OUTER_RIGHT} or {@link StairsShape#OUTER_LEFT} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_outer")}</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_RIGHT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 0° rotation</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#OUTER_LEFT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#INNER_RIGHT} or {@link StairsShape#INNER_LEFT} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_inner")}</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 180° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_RIGHT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 270° Y rotation + UV lock</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 90° Y rotation + UV lock</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 0° rotation</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@link StairsShape#INNER_LEFT} -> 180° Y rotation + UV lock</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the stairs {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with comprehensive stairs blockstate variants.
     *
     * @see #stairsBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockStateDefinition stairsBlockState(Supplier<Block> targetBlock) {
        return stairsBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get()), ModelLocationUtils.getModelLocation(targetBlock.get(), "_inner"), ModelLocationUtils.getModelLocation(targetBlock.get(), "_outer"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_INVENTORY} template for wall blocks.
     * This model is specifically designed for the inventory representation of wall blocks and promptly generates
     * a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WALL} -> {@code RegistryUtil.pickBlockPrefix(wallTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param wallTexture The {@link ResourceLocation} representing the texture of the wall block.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_INVENTORY} template.
     *
     * @see #wallPost(Supplier, ResourceLocation)
     * @see #wallSide(Supplier, ResourceLocation)
     * @see #wallSideTall(Supplier, ResourceLocation)
     * @see #wall(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition wallInventory(Supplier<Block> targetBlock, ResourceLocation wallTexture) {
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.WALL_INVENTORY)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.WALL, RegistryUtil.pickBlockPrefix(wallTexture)))
                .withCustomName(baseWallId.getPath().concat("_inventory"))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get(), "_inventory"))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_POST} template for wall blocks.
     * This model is specifically designed for the center post/pillar component of wall blocks.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WALL} -> {@code RegistryUtil.pickBlockPrefix(wallTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param wallTexture The {@link ResourceLocation} representing the texture of the wall block.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_POST} template.
     *
     * @see #wallInventory(Supplier, ResourceLocation)
     * @see #wallSide(Supplier, ResourceLocation)
     * @see #wallSideTall(Supplier, ResourceLocation)
     * @see #wall(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition wallPost(Supplier<Block> targetBlock, ResourceLocation wallTexture) {
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.WALL_POST)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.WALL, RegistryUtil.pickBlockPrefix(wallTexture)))
                .withCustomName(baseWallId.getPath().concat("_post"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_LOW_SIDE} template for wall blocks.
     * This model is specifically designed for the low side components of wall blocks that connect to adjacent blocks
     * at the standard wall height.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WALL} -> {@code RegistryUtil.pickBlockPrefix(wallTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param wallTexture The {@link ResourceLocation} representing the texture of the wall block.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_LOW_SIDE} template.
     *
     * @see #wallInventory(Supplier, ResourceLocation)
     * @see #wallPost(Supplier, ResourceLocation)
     * @see #wallSideTall(Supplier, ResourceLocation)
     * @see #wall(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition wallSide(Supplier<Block> targetBlock, ResourceLocation wallTexture) {
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.WALL_LOW_SIDE)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.WALL, RegistryUtil.pickBlockPrefix(wallTexture)))
                .withCustomName(baseWallId.getPath().concat("_side"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_TALL_SIDE} template for wall blocks.
     * This model is specifically designed for the tall side components of wall blocks that connect to adjacent blocks
     * at an elevated wall height, typically used when walls connect to taller blocks.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WALL} -> {@code RegistryUtil.pickBlockPrefix(wallTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param wallTexture The {@link ResourceLocation} representing the texture of the wall block.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#WALL_TALL_SIDE} template.
     *
     * @see #wallInventory(Supplier, ResourceLocation)
     * @see #wallPost(Supplier, ResourceLocation)
     * @see #wallSide(Supplier, ResourceLocation)
     * @see #wall(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition wallSideTall(Supplier<Block> targetBlock, ResourceLocation wallTexture) {
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.WALL_TALL_SIDE)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.WALL, RegistryUtil.pickBlockPrefix(wallTexture)))
                .withCustomName(baseWallId.getPath().concat("_side_tall"));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with all three wall variants (inventory, post, side, side_tall)
     * using the {@link ModelTemplates#WALL_INVENTORY}, {@link ModelTemplates#WALL_POST}, {@link ModelTemplates#WALL_LOW_SIDE},
     * and {@link ModelTemplates#WALL_TALL_SIDE} templates. The inventory variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WALL} -> {@code RegistryUtil.pickBlockPrefix(wallTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param wallTexture The {@link ResourceLocation} representing the texture of the wall block.
     *
     * @return A {@link BlockModelDefinition} with all wall variants.
     *
     * @see #wallInventory(Supplier, ResourceLocation)
     * @see #wallPost(Supplier, ResourceLocation)
     * @see #wallSide(Supplier, ResourceLocation)
     * @see #wallSideTall(Supplier, ResourceLocation)
     * @see #wall(Supplier)
     */
    public static BlockModelDefinition wall(Supplier<Block> targetBlock, ResourceLocation wallTexture) {
        return wallInventory(targetBlock, wallTexture)
                .withOrdinalModelDefinitions(wallPost(targetBlock, wallTexture), wallSide(targetBlock, wallTexture), wallSideTall(targetBlock, wallTexture));
    }

    /**
     * Overloaded variant of {@link #wall(Supplier, ResourceLocation)}. Creates a comprehensive {@link BlockModelDefinition}
     * with all three wall variants using automatic texture resolution. Automatically determines the wall texture based
     * on the wall's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#WALL} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with all wall variants using automatic texture resolution.
     *
     * @see #wallInventory(Supplier, ResourceLocation)
     * @see #wallPost(Supplier, ResourceLocation)
     * @see #wallSide(Supplier, ResourceLocation)
     * @see #wallSideTall(Supplier, ResourceLocation)
     * @see #wall(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition wall(Supplier<Block> targetBlock) {
        return wall(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockStateDefinition} for wall blocks using {@link MultiPartGenerator} with different models
     * for each combination of wall connections and heights. This method handles the complex multipart system used
     * by Minecraft walls to connect properly with adjacent blocks.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#UP} -> {@code wallPostModel} (when wall has a post)</li>
     *         <li>{@link BlockStateProperties#NORTH_WALL} + {@link WallSide#LOW} -> {@code wallSideModel}</li>
     *         <li>{@link BlockStateProperties#EAST_WALL} + {@link WallSide#LOW} -> {@code wallSideModel} (90° rotation)</li>
     *         <li>{@link BlockStateProperties#SOUTH_WALL} + {@link WallSide#LOW} -> {@code wallSideModel} (180° rotation)</li>
     *         <li>{@link BlockStateProperties#WEST_WALL} + {@link WallSide#LOW} -> {@code wallSideModel} (270° rotation)</li>
     *         <li>{@link BlockStateProperties#NORTH_WALL} + {@link WallSide#TALL} -> {@code wallSideTallModel}</li>
     *         <li>{@link BlockStateProperties#EAST_WALL} + {@link WallSide#TALL} -> {@code wallSideTallModel} (90° rotation)</li>
     *         <li>{@link BlockStateProperties#SOUTH_WALL} + {@link WallSide#TALL} -> {@code wallSideTallModel} (180° rotation)</li>
     *         <li>{@link BlockStateProperties#WEST_WALL} + {@link WallSide#TALL} -> {@code wallSideTallModel} (270° rotation)</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to create the blockstate for.
     * @param wallPostModel The {@link ResourceLocation} of the model to use for the wall post (center pillar).
     * @param wallSideModel The {@link ResourceLocation} of the model to use for low wall sides.
     * @param wallSideTallModel The {@link ResourceLocation} of the model to use for tall wall sides.
     *
     * @return A {@link BlockStateDefinition} with comprehensive wall blockstate variants using multipart system.
     *
     * @see #wallBlockState(Supplier)
     * @see #wallInventory(Supplier, ResourceLocation)
     * @see #wallPost(Supplier, ResourceLocation)
     * @see #wallSide(Supplier, ResourceLocation)
     * @see #wallSideTall(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition wallBlockState(Supplier<Block> targetBlock, ResourceLocation wallPostModel, ResourceLocation wallSideModel, ResourceLocation wallSideTallModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiPartGenerator.multiPart(targetBlock.get())
                        .with(Condition.condition()
                                .term(BlockStateProperties.UP, true), Variant.variant()
                                .with(VariantProperties.MODEL, wallPostModel))
                        .with(Condition.condition()
                                .term(BlockStateProperties.NORTH_WALL, WallSide.LOW), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideModel)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.EAST_WALL, WallSide.LOW), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.SOUTH_WALL, WallSide.LOW), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.WEST_WALL, WallSide.LOW), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.NORTH_WALL, WallSide.TALL), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideTallModel)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.EAST_WALL, WallSide.TALL), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideTallModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.SOUTH_WALL, WallSide.TALL), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideTallModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.WEST_WALL, WallSide.TALL), Variant.variant()
                                .with(VariantProperties.MODEL, wallSideTallModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.UV_LOCK, true)));
    }

    /**
     * Overloaded variant of {@link #wallBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for wall blocks using {@link MultiPartGenerator} with different models
     * for each combination of wall connections and heights. Automatically determines model locations using standard
     * naming convention (base model with "_post", "_side", "_side_tall" suffixes).
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#UP} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_post")}</li>
     *         <li>{@link BlockStateProperties#NORTH_WALL} + {@link WallSide#LOW} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")}</li>
     *         <li>{@link BlockStateProperties#EAST_WALL} + {@link WallSide#LOW} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")} (90° rotation)</li>
     *         <li>{@link BlockStateProperties#SOUTH_WALL} + {@link WallSide#LOW} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")} (180° rotation)</li>
     *         <li>{@link BlockStateProperties#WEST_WALL} + {@link WallSide#LOW} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")} (270° rotation)</li>
     *         <li>{@link BlockStateProperties#NORTH_WALL} + {@link WallSide#TALL} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side_tall")}</li>
     *         <li>{@link BlockStateProperties#EAST_WALL} + {@link WallSide#TALL} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side_tall")} (90° rotation)</li>
     *         <li>{@link BlockStateProperties#SOUTH_WALL} + {@link WallSide#TALL} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side_tall")} (180° rotation)</li>
     *         <li>{@link BlockStateProperties#WEST_WALL} + {@link WallSide#TALL} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side_tall")} (270° rotation)</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wall {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with comprehensive wall blockstate variants using automatic model resolution.
     *
     * @see #wallBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #wallInventory(Supplier, ResourceLocation)
     * @see #wallPost(Supplier, ResourceLocation)
     * @see #wallSide(Supplier, ResourceLocation)
     * @see #wallSideTall(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition wallBlockState(Supplier<Block> targetBlock) {
        return wallBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get(), "_post"), ModelLocationUtils.getModelLocation(targetBlock.get(), "_side"), ModelLocationUtils.getModelLocation(targetBlock.get(), "_side_tall"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#BUTTON} template for button blocks.
     * This model is specifically designed for the default button component.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(buttonTexture)}</li>
     *     </ul>
     *
     * @param buttonTexture The {@link ResourceLocation} representing the texture of the button.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#BUTTON} template.
     *
     * @see #buttonInventory(Supplier, ResourceLocation)
     * @see #buttonPressed(Supplier, ResourceLocation)
     * @see #button(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition buttonDefault(ResourceLocation buttonTexture) {
        return new BlockModelDefinition(ModelTemplates.BUTTON)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.TEXTURE, RegistryUtil.pickBlockPrefix(buttonTexture)));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#BUTTON_INVENTORY} template for button blocks.
     * This model is specifically designed for the inventory representation of button blocks and promptly generates
     * a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(buttonTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the button {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param buttonTexture The {@link ResourceLocation} representing the texture of the button.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#BUTTON_INVENTORY} template.
     *
     * @see #buttonDefault(ResourceLocation)
     * @see #buttonPressed(Supplier, ResourceLocation)
     * @see #button(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition buttonInventory(Supplier<Block> targetBlock, ResourceLocation buttonTexture) {
        ResourceLocation baseButtonId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.BUTTON_INVENTORY)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.TEXTURE, RegistryUtil.pickBlockPrefix(buttonTexture)))
                .withCustomName(baseButtonId.getPath().concat("_inventory"))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get(), "_inventory"))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#BUTTON_PRESSED} template for button blocks.
     * This model is specifically designed for the pressed state of button blocks.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(buttonTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the button {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param buttonTexture The {@link ResourceLocation} representing the texture of the button.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#BUTTON_PRESSED} template.
     *
     * @see #buttonDefault(ResourceLocation)
     * @see #buttonInventory(Supplier, ResourceLocation)
     * @see #button(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition buttonPressed(Supplier<Block> targetBlock, ResourceLocation buttonTexture) {
        ResourceLocation baseButtonId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.BUTTON_PRESSED)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.TEXTURE, RegistryUtil.pickBlockPrefix(buttonTexture)))
                .withCustomName(baseButtonId.getPath().concat("_pressed"));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with all three button variants (default, inventory, pressed)
     * using the {@link ModelTemplates#BUTTON}, {@link ModelTemplates#BUTTON_INVENTORY}, and {@link ModelTemplates#BUTTON_PRESSED}
     * templates. The inventory variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code buttonTexture}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the button {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param buttonTexture The {@link ResourceLocation} representing the texture of the button.
     *
     * @return A {@link BlockModelDefinition} with all button variants.
     *
     * @see #buttonDefault(ResourceLocation)
     * @see #buttonInventory(Supplier, ResourceLocation)
     * @see #buttonPressed(Supplier, ResourceLocation)
     * @see #button(Supplier)
     */
    public static BlockModelDefinition button(Supplier<Block> targetBlock, ResourceLocation buttonTexture) {
        return buttonDefault(buttonTexture)
                .withOrdinalModelDefinitions(buttonInventory(targetBlock, buttonTexture), buttonPressed(targetBlock, buttonTexture));
    }

    /**
     * Overloaded variant of {@link #button(Supplier, ResourceLocation)}. Creates a comprehensive {@link BlockModelDefinition}
     * with all three button variants using automatic texture resolution. Automatically determines the button texture based
     * on the button's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the button {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with all button variants using automatic texture resolution.
     *
     * @see #buttonDefault(ResourceLocation)
     * @see #buttonInventory(Supplier, ResourceLocation)
     * @see #buttonPressed(Supplier, ResourceLocation)
     * @see #button(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition button(Supplier<Block> targetBlock) {
        return button(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockStateDefinition} for button blocks using {@link MultiVariantGenerator} with different models
     * for each combination of power state, attach face, and facing direction. This method handles the complex variant system
     * used by Minecraft buttons to show pressed/unpressed states and proper orientations based on placement.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#POWERED} = false -> {@code RegistryUtil.pickBlockPrefix(buttonModel)}</li>
     *         <li>{@link BlockStateProperties#POWERED} = true -> {@code RegistryUtil.pickBlockPrefix(buttonPressedModel)}</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#EAST} -> 90° Y rotation</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#WEST} -> 270° Y rotation</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#SOUTH} -> 180° Y rotation</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#NORTH} -> no rotation</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#EAST} -> 90° Y + 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#WEST} -> 270° Y + 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#SOUTH} -> 180° Y + 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#NORTH} -> 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#EAST} -> 270° Y + 180° X rotation</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#WEST} -> 90° Y + 180° X rotation</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#SOUTH} -> 180° X rotation</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#NORTH} -> 180° Y + 180° X rotation</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the button {@link Block} to create the blockstate for.
     * @param buttonModel The {@link ResourceLocation} of the model to use for the unpressed button state.
     * @param buttonPressedModel The {@link ResourceLocation} of the model to use for the pressed button state.
     *
     * @return A {@link BlockStateDefinition} with comprehensive button blockstate variants.
     *
     * @see #buttonBlockState(Supplier)
     * @see #buttonDefault(ResourceLocation)
     * @see #buttonInventory(Supplier, ResourceLocation)
     * @see #buttonPressed(Supplier, ResourceLocation)
     * @see #button(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition buttonBlockState(Supplier<Block> targetBlock, ResourceLocation buttonModel, ResourceLocation buttonPressedModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch
                                .property(BlockStateProperties.POWERED)
                                .select(false, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(buttonModel)))
                                .select(true, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(buttonPressedModel))))
                        .with(PropertyDispatch
                                .properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                                .select(AttachFace.FLOOR, Direction.EAST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(AttachFace.FLOOR, Direction.WEST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.FLOOR, Direction.NORTH, Variant.variant())
                                .select(AttachFace.WALL, Direction.EAST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(AttachFace.WALL, Direction.WEST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(AttachFace.WALL, Direction.SOUTH, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(AttachFace.WALL, Direction.NORTH, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(AttachFace.CEILING, Direction.EAST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.CEILING, Direction.WEST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.CEILING, Direction.SOUTH, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.CEILING, Direction.NORTH, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))));
    }

    /**
     * Overloaded variant of {@link #buttonBlockState(Supplier, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for button blocks using {@link MultiVariantGenerator} with different models
     * for each combination of power state, attach face, and facing direction. Automatically determines model locations
     * using standard naming convention (base model and "_pressed" suffix).
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#POWERED} = false -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())}</li>
     *         <li>{@link BlockStateProperties#POWERED} = true -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_pressed")}</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#EAST} -> 90° Y rotation</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#WEST} -> 270° Y rotation</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#SOUTH} -> 180° Y rotation</li>
     *         <li>{@link AttachFace#FLOOR} + {@link Direction#NORTH} -> no rotation</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#EAST} -> 90° Y + 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#WEST} -> 270° Y + 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#SOUTH} -> 180° Y + 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#WALL} + {@link Direction#NORTH} -> 90° X rotation + UV lock</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#EAST} -> 270° Y + 180° X rotation</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#WEST} -> 90° Y + 180° X rotation</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#SOUTH} -> 180° X rotation</li>
     *         <li>{@link AttachFace#CEILING} + {@link Direction#NORTH} -> 180° Y + 180° X rotation</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the button {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with comprehensive button blockstate variants using automatic model resolution.
     *
     * @see #buttonBlockState(Supplier, ResourceLocation, ResourceLocation)
     * @see #buttonDefault(ResourceLocation)
     * @see #buttonInventory(Supplier, ResourceLocation)
     * @see #buttonPressed(Supplier, ResourceLocation)
     * @see #button(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition buttonBlockState(Supplier<Block> targetBlock) {
        return buttonBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get()), ModelLocationUtils.getModelLocation(targetBlock.get(), "_pressed"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#PRESSURE_PLATE_UP} template for pressure plate blocks.
     * This model is specifically designed for the unpressed (up) state of pressure plate blocks and promptly generates
     * a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(pressurePlateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the pressure plate {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param pressurePlateTexture The {@link ResourceLocation} representing the texture of the pressure plate.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#PRESSURE_PLATE_UP} template.
     *
     * @see #pressurePlateDown(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier)
     */
    public static BlockModelDefinition pressurePlateUp(Supplier<Block> targetBlock, ResourceLocation pressurePlateTexture) {
        return new BlockModelDefinition(ModelTemplates.PRESSURE_PLATE_UP)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(pressurePlateTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#PRESSURE_PLATE_DOWN} template for pressure plate blocks.
     * This model is specifically designed for the pressed (down) state of pressure plate blocks.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(pressurePlateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the pressure plate {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param pressurePlateTexture The {@link ResourceLocation} representing the texture of the pressure plate.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#PRESSURE_PLATE_DOWN} template.
     *
     * @see #pressurePlateUp(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier)
     */
    public static BlockModelDefinition pressurePlateDown(Supplier<Block> targetBlock, ResourceLocation pressurePlateTexture) {
        ResourceLocation basePressurePlateId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.PRESSURE_PLATE_DOWN)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(pressurePlateTexture)))
                .withCustomName(basePressurePlateId.getPath().concat("_down"));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with both pressure plate variants (up and down)
     * using the {@link ModelTemplates#PRESSURE_PLATE_UP} and {@link ModelTemplates#PRESSURE_PLATE_DOWN} templates.
     * The up variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(pressurePlateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the pressure plate {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param pressurePlateTexture The {@link ResourceLocation} representing the texture of the pressure plate.
     *
     * @return A {@link BlockModelDefinition} with both pressure plate variants.
     *
     * @see #pressurePlateUp(Supplier, ResourceLocation)
     * @see #pressurePlateDown(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier)
     */
    public static BlockModelDefinition pressurePlate(Supplier<Block> targetBlock, ResourceLocation pressurePlateTexture) {
        return pressurePlateUp(targetBlock, pressurePlateTexture)
                .withOrdinalModelDefinition(pressurePlateDown(targetBlock, pressurePlateTexture));
    }

    /**
     * Overloaded variant of {@link #pressurePlate(Supplier, ResourceLocation)}. Creates a comprehensive {@link BlockModelDefinition}
     * with both pressure plate variants using automatic texture resolution. Automatically determines the pressure plate texture based
     * on the pressure plate's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the pressure plate {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with both pressure plate variants using automatic texture resolution.
     *
     * @see #pressurePlateUp(Supplier, ResourceLocation)
     * @see #pressurePlateDown(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition pressurePlate(Supplier<Block> targetBlock) {
        return pressurePlate(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockStateDefinition} for pressure plate blocks using {@link MultiVariantGenerator} with different models
     * for each power state. This method handles the simple binary system used by Minecraft pressure plates to show
     * pressed/unpressed states based on whether entities are standing on them.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#POWERED} = {@code false} -> {@code RegistryUtil.pickBlockPrefix(pressurePlateUpModel)}</li>
     *         <li>{@link BlockStateProperties#POWERED} = {@code true} -> {@code RegistryUtil.pickBlockPrefix(pressurePlateDownModel)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the pressure plate {@link Block} to create the blockstate for.
     * @param pressurePlateDownModel The {@link ResourceLocation} of the model to use for the pressed (down) pressure plate state.
     * @param pressurePlateUpModel The {@link ResourceLocation} of the model to use for the unpressed (up) pressure plate state.
     *
     * @return A {@link BlockStateDefinition} with pressure plate blockstate variants using binary power states.
     *
     * @see #pressurePlateBlockState(Supplier)
     * @see #pressurePlateUp(Supplier, ResourceLocation)
     * @see #pressurePlateDown(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition pressurePlateBlockState(Supplier<Block> targetBlock, ResourceLocation pressurePlateDownModel, ResourceLocation pressurePlateUpModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch
                                .property(BlockStateProperties.POWERED)
                                .select(true, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(pressurePlateDownModel)))
                                .select(false, Variant.variant()
                                        .with(VariantProperties.MODEL, RegistryUtil.pickBlockPrefix(pressurePlateUpModel)))));
    }

    /**
     * Overloaded variant of {@link #pressurePlateBlockState(Supplier, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for pressure plate blocks using {@link MultiVariantGenerator} with different models
     * for each power state. Automatically determines model locations using standard naming convention (base model and "_down" suffix).
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#POWERED} = {@code false} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())}</li>
     *         <li>{@link BlockStateProperties#POWERED} = {@code true} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_down")}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the pressure plate {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with pressure plate blockstate variants using automatic model resolution.
     *
     * @see #pressurePlateBlockState(Supplier, ResourceLocation, ResourceLocation)
     * @see #pressurePlateUp(Supplier, ResourceLocation)
     * @see #pressurePlateDown(Supplier, ResourceLocation)
     * @see #pressurePlate(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition pressurePlateBlockState(Supplier<Block> targetBlock) {
        return pressurePlateBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get(), "_down"), ModelLocationUtils.getModelLocation(targetBlock.get()));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_LEFT} template for door blocks.
     * This model represents the bottom-left portion of a closed door and is used for the lower half of doors
     * positioned on the left side when viewed from the front. The model will be automatically named
     * {@code baseDoorId.getPath() + "_bottom_left"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_LEFT} template.
     *
     * @see #doorBottomLeft(Supplier)
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomLeft(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_BOTTOM_LEFT)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_bottom_left"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_BOTTOM_LEFT} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_LEFT} template.
     *
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeftOpen(Supplier)
     * @see #doorBottomRight(Supplier)
     * @see #doorTopLeft(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomLeft(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorBottomLeft(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_LEFT_OPEN} template for door blocks.
     * This model represents the bottom-left portion of an open door and is used for the lower half of doors
     * positioned on the left side when viewed from the front, rotated 90 degrees to show the open state.
     * The model will be automatically named {@code baseDoorId.getPath() + "_bottom_left_open"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_LEFT_OPEN} template.
     *
     * @see #doorBottomLeftOpen(Supplier)
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomLeftOpen(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_BOTTOM_LEFT_OPEN)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_bottom_left_open"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_BOTTOM_LEFT_OPEN} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_LEFT_OPEN} template.
     *
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeft(Supplier)
     * @see #doorBottomRightOpen(Supplier)
     * @see #doorTopLeftOpen(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomLeftOpen(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorBottomLeftOpen(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT} template for door blocks.
     * This model represents the bottom-right portion of a closed door and is used for the lower half of doors
     * positioned on the right side when viewed from the front (mirror of the left variant).
     * The model will be automatically named {@code baseDoorId.getPath() + "_bottom_right"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT} template.
     *
     * @see #doorBottomRight(Supplier)
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomRight(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_BOTTOM_RIGHT)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_bottom_right"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT} template.
     *
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeft(Supplier)
     * @see #doorBottomRightOpen(Supplier)
     * @see #doorTopRight(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomRight(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorBottomRight(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT_OPEN} template for door blocks.
     * This model represents the bottom-right portion of an open door and is used for the lower half of doors
     * positioned on the right side when viewed from the front, rotated 270 degrees to show the open state.
     * The model will be automatically named {@code baseDoorId.getPath() + "_bottom_right_open"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT_OPEN} template.
     *
     * @see #doorBottomRightOpen(Supplier)
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomRightOpen(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_bottom_right_open"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT_OPEN} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_BOTTOM_RIGHT_OPEN} template.
     *
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRight(Supplier)
     * @see #doorBottomLeftOpen(Supplier)
     * @see #doorTopRightOpen(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorBottomRightOpen(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorBottomRightOpen(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_LEFT} template for door blocks.
     * This model represents the top-left portion of a closed door and is used for the upper half of doors
     * positioned on the left side when viewed from the front.
     * The model will be automatically named {@code baseDoorId.getPath() + "_top_left"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_LEFT} template.
     *
     * @see #doorTopLeft(Supplier)
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopLeft(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_TOP_LEFT)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_top_left"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_TOP_LEFT} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_LEFT} template.
     *
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeft(Supplier)
     * @see #doorTopLeftOpen(Supplier)
     * @see #doorTopRight(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopLeft(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorTopLeft(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_LEFT_OPEN} template for door blocks.
     * This model represents the top-left portion of an open door and is used for the upper half of doors
     * positioned on the left side when viewed from the front, rotated 90 degrees to show the open state.
     * The model will be automatically named {@code baseDoorId.getPath() + "_top_left_open"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_LEFT_OPEN} template.
     *
     * @see #doorTopLeftOpen(Supplier)
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopLeftOpen(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_TOP_LEFT_OPEN)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_top_left_open"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_TOP_LEFT_OPEN} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_LEFT_OPEN} template.
     *
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeft(Supplier)
     * @see #doorBottomLeftOpen(Supplier)
     * @see #doorTopRightOpen(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopLeftOpen(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorTopLeftOpen(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_RIGHT} template for door blocks.
     * This model represents the top-right portion of a closed door and is used for the upper half of doors
     * positioned on the right side when viewed from the front (mirror of the left variant).
     * The model will be automatically named {@code baseDoorId.getPath() + "_top_right"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_RIGHT} template.
     *
     * @see #doorTopRight(Supplier)
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopRight(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_TOP_RIGHT)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_top_right"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorTopRight(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_TOP_RIGHT} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_RIGHT} template.
     *
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeft(Supplier)
     * @see #doorBottomRight(Supplier)
     * @see #doorTopRightOpen(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopRight(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorTopRight(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_RIGHT_OPEN} template for door blocks.
     * This model represents the top-right portion of an open door and is used for the upper half of doors
     * positioned on the right side when viewed from the front, rotated 270 degrees to show the open state.
     * The model will be automatically named {@code baseDoorId.getPath() + "_top_right_open"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomDoorTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topDoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model naming.
     * @param bottomDoorTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topDoorTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_RIGHT_OPEN} template.
     *
     * @see #doorTopRightOpen(Supplier)
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopRightOpen(Supplier<Block> targetBlock, ResourceLocation bottomDoorTexture, ResourceLocation topDoorTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.DOOR_TOP_RIGHT_OPEN)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.BOTTOM, RegistryUtil.pickBlockPrefix(bottomDoorTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(topDoorTexture)))
                .withCustomName(baseDoorId.getPath().concat("_top_right_open"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#DOOR_TOP_RIGHT_OPEN} template for door blocks using automatic texture resolution.
     * Automatically determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#DOOR_TOP_RIGHT_OPEN} template.
     *
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRight(Supplier)
     * @see #doorBottomRightOpen(Supplier)
     * @see #doorTopLeftOpen(Supplier)
     * @see #door(Supplier)
     */
    public static BlockModelDefinition doorTopRightOpen(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return doorTopRightOpen(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates an {@link ItemModelDefinition} for door items using the {@link ModelTemplates#FLAT_ITEM} template.
     * This method generates the item model for door blocks used in inventory and item rendering.
     * Automatically determines the item texture based on the door's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic item texture location resolution.
     *
     * @return An {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template for door items.
     *
     * @see #basicGenerated(ResourceLocation)
     * @see #door(Supplier)
     * @see #door(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static ItemModelDefinition doorItem(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return basicGenerated(RegistryUtil.getTextureLocationOrDefault(baseDoorId));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with all eight door variants (bottom-left, bottom-left-open,
     * bottom-right, bottom-right-open, top-left, top-left-open, top-right, top-right-open) using the respective
     * {@link ModelTemplates#DOOR_BOTTOM_LEFT}, {@link ModelTemplates#DOOR_BOTTOM_LEFT_OPEN}, {@link ModelTemplates#DOOR_BOTTOM_RIGHT},
     * {@link ModelTemplates#DOOR_BOTTOM_RIGHT_OPEN}, {@link ModelTemplates#DOOR_TOP_LEFT}, {@link ModelTemplates#DOOR_TOP_LEFT_OPEN},
     * {@link ModelTemplates#DOOR_TOP_RIGHT}, and {@link ModelTemplates#DOOR_TOP_RIGHT_OPEN} templates. Also includes
     * a corresponding item model for inventory rendering.
     * <p>
     * <h3>Required Texture Slots</h3>
     * <ul>
     *     <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *     <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *     <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.pickItemPrefix(itemTexture)}</li>
     * </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topTexture    The {@link ResourceLocation} representing the texture of the top portion of the door.
     * @param itemTexture   The {@link ResourceLocation} representing the texture for the door item model.
     *
     * @return A {@link BlockModelDefinition} with all door variants and item model.
     *
     * @see #door(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorItem(Supplier)
     */
    public static BlockModelDefinition door(Supplier<Block> targetBlock, ResourceLocation bottomTexture, ResourceLocation topTexture, ResourceLocation itemTexture) {
        return doorBottomLeft(targetBlock, bottomTexture, topTexture)
                .withOrdinalModelDefinitions(
                        doorBottomLeftOpen(targetBlock, bottomTexture, topTexture),
                        doorBottomRight(targetBlock, bottomTexture, topTexture),
                        doorBottomRightOpen(targetBlock, bottomTexture, topTexture),
                        doorTopLeft(targetBlock, bottomTexture, topTexture),
                        doorTopLeftOpen(targetBlock, bottomTexture, topTexture),
                        doorTopRight(targetBlock, bottomTexture, topTexture),
                        doorTopRightOpen(targetBlock, bottomTexture, topTexture),
                        basicGenerated(itemTexture)
                );
    }

    /**
     * Overloaded variant of {@link #door(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}. Creates a comprehensive
     * {@link BlockModelDefinition} with all eight door variants using automatic item texture resolution. Automatically
     * determines the item texture via {@link RegistryUtil#getTextureLocation(ResourceLocation, String)} under the
     * "item" directory.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.pickBlockPrefix(bottomTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(topTexture)}</li>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.pickItemPrefix(derivedItemTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     * @param bottomTexture The {@link ResourceLocation} representing the texture of the bottom portion of the door.
     * @param topTexture The {@link ResourceLocation} representing the texture of the top portion of the door.
     *
     * @return A {@link BlockModelDefinition} with all door variants using automatic item texture resolution.
     *
     * @see #door(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorItem(Supplier)
     */
    public static BlockModelDefinition door(Supplier<Block> targetBlock, ResourceLocation bottomTexture, ResourceLocation topTexture) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return door(targetBlock, bottomTexture, topTexture, RegistryUtil.getTextureLocation(baseDoorId, "item").orElse(baseDoorId));
    }

    /**
     * Overloaded variant of {@link #door(Supplier, ResourceLocation, ResourceLocation)}. Creates a comprehensive
     * {@link BlockModelDefinition} with all eight door variants using automatic texture resolution. Automatically
     * determines the door textures based on the door's registry ID using "_bottom" and "_top" suffixes, and
     * derives the item texture automatically.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom"))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top"))}</li>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.pickItemPrefix(derivedItemTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with all door variants using automatic texture resolution.
     *
     * @see #door(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #door(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeft(Supplier)
     * @see #doorBottomLeftOpen(Supplier)
     * @see #doorBottomRight(Supplier)
     * @see #doorBottomRightOpen(Supplier)
     * @see #doorTopLeft(Supplier)
     * @see #doorTopLeftOpen(Supplier)
     * @see #doorTopRight(Supplier)
     * @see #doorTopRightOpen(Supplier)
     * @see #doorItem(Supplier)
     */
    public static BlockModelDefinition door(Supplier<Block> targetBlock) {
        ResourceLocation baseDoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return door(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_bottom")), RegistryUtil.getTextureLocationOrDefault(baseDoorId.withSuffix("_top")));
    }

    /**
     * Creates a {@link BlockStateDefinition} for door blocks using {@link MultiVariantGenerator} with models selected by
     * {@link BlockStateProperties#HORIZONTAL_FACING}, {@link BlockStateProperties#DOUBLE_BLOCK_HALF},
     * {@link BlockStateProperties#DOOR_HINGE}, and {@link BlockStateProperties#OPEN}.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#DOUBLE_BLOCK_HALF}
     *         + {@link BlockStateProperties#DOOR_HINGE} + {@link BlockStateProperties#OPEN} -> Various model combinations</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#LEFT} + {@code false} -> {@code bottomLeftDoorModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#RIGHT} + {@code false} -> {@code bottomRightDoorModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#LEFT} + {@code true} -> {@code bottomLeftDoorOpenModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 90°, {@link Direction#SOUTH}: 180°, {@link Direction#WEST}: 270°, {@link Direction#NORTH}: 0°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#RIGHT} + {@code true} -> {@code bottomRightDoorOpenModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 270°, {@link Direction#SOUTH}: 0°, {@link Direction#WEST}: 90°, {@link Direction#NORTH}: 180°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#LEFT} + {@code false} -> {@code topLeftDoorModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#RIGHT} + {@code false} -> {@code topRightDoorModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#LEFT} + {@code true} -> {@code topLeftDoorOpenModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 90°, {@link Direction#SOUTH}: 180°, {@link Direction#WEST}: 270°, {@link Direction#NORTH}: 0°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#RIGHT} + {@code true} -> {@code topRightDoorOpenModel} with Y-rotations by facing:
     *         {@link Direction#EAST}: 270°, {@link Direction#SOUTH}: 0°, {@link Direction#WEST}: 90°, {@link Direction#NORTH}: 180°</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to create the blockstate for.
     * @param topLeftDoorModel Model for the top-left closed door variant.
     * @param topLeftDoorOpenModel Model for the top-left open door variant.
     * @param bottomLeftDoorModel Model for the bottom-left closed door variant.
     * @param bottomLeftDoorOpenModel Model for the bottom-left open door variant.
     * @param topRightDoorModel Model for the top-right closed door variant.
     * @param topRightDoorOpenModel Model for the top-right open door variant.
     * @param bottomRightDoorModel Model for the bottom-right closed door variant.
     * @param bottomRightDoorOpenModel Model for the bottom-right open door variant.
     *
     * @return A {@link BlockStateDefinition} with door variants mapped across facing, half, hinge, and open properties.
     *
     * @see #doorBlockState(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBlockState(Supplier)
     * @see #door(Supplier)
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockStateDefinition doorBlockState(Supplier<Block> targetBlock, ResourceLocation topLeftDoorModel, ResourceLocation topLeftDoorOpenModel, ResourceLocation bottomLeftDoorModel, ResourceLocation bottomLeftDoorOpenModel, ResourceLocation topRightDoorModel, ResourceLocation topRightDoorOpenModel, ResourceLocation bottomRightDoorModel, ResourceLocation bottomRightDoorOpenModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get()).with(
                        PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.DOOR_HINGE, BlockStateProperties.OPEN)
                                .select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorModel))
                                .select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorModel))
                                .select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomLeftDoorOpenModel))
                                .select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorOpenModel))
                                .select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomRightDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorModel))
                                .select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorModel))
                                .select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topLeftDoorOpenModel))
                                .select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorOpenModel))
                                .select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, Variant.variant()
                                        .with(VariantProperties.MODEL, topRightDoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                ));
    }

    /**
     * Overload that derives the eight door-part models from two base models using the naming scheme
     * {@code topDoorModel}_{left|left_open|right|right_open} and
     * {@code bottomDoorModel}_{left|left_open|right|right_open}, then applies the same variant mapping as the
     * full-parameter overload.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#DOUBLE_BLOCK_HALF}
     *         + {@link BlockStateProperties#DOOR_HINGE} + {@link BlockStateProperties#OPEN} -> Derived model combinations</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#LEFT} + {@code false} -> {@code topDoorModel + "_left"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#RIGHT} + {@code false} -> {@code bottomDoorModel + "_right"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#LEFT} + {@code true} -> {@code bottomDoorModel + "_left_open"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 90°, {@link Direction#SOUTH}: 180°, {@link Direction#WEST}: 270°, {@link Direction#NORTH}: 0°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#RIGHT} + {@code true} -> {@code bottomDoorModel + "_right_open"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 270°, {@link Direction#SOUTH}: 0°, {@link Direction#WEST}: 90°, {@link Direction#NORTH}: 180°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#LEFT} + {@code false} -> {@code topDoorModel + "_left"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#RIGHT} + {@code false} -> {@code topDoorModel + "_right"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#LEFT} + {@code true} -> {@code topDoorModel + "_left_open"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 90°, {@link Direction#SOUTH}: 180°, {@link Direction#WEST}: 270°, {@link Direction#NORTH}: 0°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#RIGHT} + {@code true} -> {@code topDoorModel + "_right_open"} with Y-rotations by facing:
     *         {@link Direction#EAST}: 270°, {@link Direction#SOUTH}: 0°, {@link Direction#WEST}: 90°, {@link Direction#NORTH}: 180°</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to create the blockstate for.
     * @param topDoorModel Base model for top door halves; "_left", "_left_open", "_right", "_right_open" are appended.
     * @param bottomDoorModel Base model for bottom door halves; "_left", "_left_open", "_right", "_right_open" are appended.
     *
     * @return A {@link BlockStateDefinition} with door variants using automatically derived models.
     *
     * @see #doorBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #doorBlockState(Supplier)
     * @see #door(Supplier)
     * @see #doorBottomLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorBottomRightOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeft(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopLeftOpen(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRight(Supplier, ResourceLocation, ResourceLocation)
     * @see #doorTopRightOpen(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockStateDefinition doorBlockState(Supplier<Block> targetBlock, ResourceLocation topDoorModel, ResourceLocation bottomDoorModel) {
        return doorBlockState(targetBlock, topDoorModel.withSuffix("_left"), topDoorModel.withSuffix("_left_open"), bottomDoorModel.withSuffix("_left"), bottomDoorModel.withSuffix("_left_open"), topDoorModel.withSuffix("_right"), topDoorModel.withSuffix("_right_open"), bottomDoorModel.withSuffix("_right"), bottomDoorModel.withSuffix("_right_open"));
    }

    /**
     * Overload that fully derives all door-part models from the block's registry-based model locations using the
     * standard suffix scheme: top = "_top", bottom = "_bottom", then "_left", "_left_open", "_right",
     * "_right_open". Applies the same variant mapping and rotations as the full-parameter overload.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#DOUBLE_BLOCK_HALF}
     *         + {@link BlockStateProperties#DOOR_HINGE} + {@link BlockStateProperties#OPEN} -> Registry-derived model combinations</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#LEFT} + {@code false} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_bottom_left"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#RIGHT} + {@code false} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_bottom_right"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#LEFT} + {@code true} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_bottom_left_open"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 90°, {@link Direction#SOUTH}: 180°, {@link Direction#WEST}: 270°, {@link Direction#NORTH}: 0°</li>
     *         <li>{@link DoubleBlockHalf#LOWER} + {@link DoorHingeSide#RIGHT} + {@code true} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_bottom_right_open"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 270°, {@link Direction#SOUTH}: 0°, {@link Direction#WEST}: 90°, {@link Direction#NORTH}: 180°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#LEFT} + {@code false} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_top_left"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#RIGHT} + {@code false} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_top_right"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 0°, {@link Direction#SOUTH}: 90°, {@link Direction#WEST}: 180°, {@link Direction#NORTH}: 270°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#LEFT} + {@code true} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_top_left_open"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 90°, {@link Direction#SOUTH}: 180°, {@link Direction#WEST}: 270°, {@link Direction#NORTH}: 0°</li>
     *         <li>{@link DoubleBlockHalf#UPPER} + {@link DoorHingeSide#RIGHT} + {@code true} ->
     *             {@link ModelLocationUtils}.getModelLocation({@code targetBlock.get()}, {@code "_top_right_open"}) with Y-rotations by facing:
     *         {@link Direction#EAST}: 270°, {@link Direction#SOUTH}: 0°, {@link Direction#WEST}: 90°, {@link Direction#NORTH}: 180°</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the door {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with door variants using fully automatic model resolution.
     *
     * @see #doorBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #doorBlockState(Supplier, ResourceLocation, ResourceLocation)
     * @see #door(Supplier)
     * @see #doorBottomLeft(Supplier)
     * @see #doorBottomLeftOpen(Supplier)
     * @see #doorBottomRight(Supplier)
     * @see #doorBottomRightOpen(Supplier)
     * @see #doorTopLeft(Supplier)
     * @see #doorTopLeftOpen(Supplier)
     * @see #doorTopRight(Supplier)
     * @see #doorTopRightOpen(Supplier)
     */
    public static BlockStateDefinition doorBlockState(Supplier<Block> targetBlock) {
        return doorBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get(), "_top"), ModelLocationUtils.getModelLocation(targetBlock.get(), "_bottom"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_BOTTOM} template for trapdoor blocks.
     * This model is specifically designed for the bottom half of closed trapdoors and promptly generates
     * a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(trapdoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param trapdoorTexture The {@link ResourceLocation} representing the texture of the trapdoor.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_BOTTOM} template.
     *
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition trapdoorBottom(Supplier<Block> targetBlock, ResourceLocation trapdoorTexture) {
        ResourceLocation targetTrapdoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.TRAPDOOR_BOTTOM)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(trapdoorTexture)))
                .withOrdinalModelDefinitions(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get(), "_bottom"))))
                .withCustomName(targetTrapdoorId.getPath().concat("_bottom"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #trapdoorBottom(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#TRAPDOOR_BOTTOM} template for trapdoor blocks using automatic texture resolution.
     * Automatically determines the trapdoor texture based on the trapdoor's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_BOTTOM} template.
     *
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorOpen(Supplier)
     * @see #trapdoorTop(Supplier)
     * @see #trapdoor(Supplier)
     */
    public static BlockModelDefinition trapdoorBottom(Supplier<Block> targetBlock) {
        ResourceLocation baseTrapdoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return trapdoorBottom(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseTrapdoorId));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_OPEN} template for trapdoor blocks.
     * This model is specifically designed for the open state of trapdoors.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(trapdoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param trapdoorTexture The {@link ResourceLocation} representing the texture of the trapdoor.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_OPEN} template.
     *
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition trapdoorOpen(Supplier<Block> targetBlock, ResourceLocation trapdoorTexture) {
        ResourceLocation targetTrapdoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.TRAPDOOR_OPEN)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(trapdoorTexture)))
                .withCustomName(targetTrapdoorId.getPath().concat("_open"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #trapdoorOpen(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#TRAPDOOR_OPEN} template for trapdoor blocks using automatic texture resolution.
     * Automatically determines the trapdoor texture based on the trapdoor's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_OPEN} template.
     *
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoorBottom(Supplier)
     * @see #trapdoorTop(Supplier)
     * @see #trapdoor(Supplier)
     */
    public static BlockModelDefinition trapdoorOpen(Supplier<Block> targetBlock) {
        ResourceLocation baseTrapdoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return trapdoorOpen(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseTrapdoorId));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_TOP} template for trapdoor blocks.
     * This model is specifically designed for the top half of closed trapdoors.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(trapdoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param trapdoorTexture The {@link ResourceLocation} representing the texture of the trapdoor.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_TOP} template.
     *
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition trapdoorTop(Supplier<Block> targetBlock, ResourceLocation trapdoorTexture) {
        ResourceLocation targetTrapdoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.TRAPDOOR_TOP)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(trapdoorTexture)))
                .withCustomName(targetTrapdoorId.getPath().concat("_top"))
                .withRenderType(CUTOUT_RENDER_TYPE);
    }

    /**
     * Overloaded variant of {@link #trapdoorTop(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#TRAPDOOR_TOP} template for trapdoor blocks using automatic texture resolution.
     * Automatically determines the trapdoor texture based on the trapdoor's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#TRAPDOOR_TOP} template.
     *
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoorBottom(Supplier)
     * @see #trapdoorOpen(Supplier)
     * @see #trapdoor(Supplier)
     */
    public static BlockModelDefinition trapdoorTop(Supplier<Block> targetBlock) {
        ResourceLocation baseTrapdoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return trapdoorTop(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseTrapdoorId));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with all three trapdoor variants (bottom, open, top)
     * using the {@link ModelTemplates#TRAPDOOR_BOTTOM}, {@link ModelTemplates#TRAPDOOR_OPEN}, and {@link ModelTemplates#TRAPDOOR_TOP}
     * templates. The bottom variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(trapdoorTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param trapdoorTexture The {@link ResourceLocation} representing the texture of the trapdoor.
     *
     * @return A {@link BlockModelDefinition} with all trapdoor variants.
     *
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier)
     */
    public static BlockModelDefinition trapdoor(Supplier<Block> targetBlock, ResourceLocation trapdoorTexture) {
        return trapdoorBottom(targetBlock, trapdoorTexture)
                .withOrdinalModelDefinitions(trapdoorOpen(targetBlock, trapdoorTexture), trapdoorTop(targetBlock, trapdoorTexture));
    }

    /**
     * Overloaded variant of {@link #trapdoor(Supplier, ResourceLocation)}. Creates a comprehensive {@link BlockModelDefinition}
     * with all three trapdoor variants using automatic texture resolution. Automatically determines the trapdoor texture based
     * on the trapdoor's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with all trapdoor variants using automatic texture resolution.
     *
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition trapdoor(Supplier<Block> targetBlock) {
        ResourceLocation baseTrapdoorId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return trapdoor(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseTrapdoorId));
    }

    /**
     * Creates a {@link BlockStateDefinition} for trapdoor blocks using {@link MultiVariantGenerator} with different models
     * for each combination of facing, half, and open state. This method handles the complex variant system used
     * by Minecraft trapdoors to show open/closed states and proper orientations based on placement.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#OPEN} = {@code false} + {@link BlockStateProperties#HALF} = {@link Half#BOTTOM}
     *         -> {@code trapdoorBottomModel} (all facings use same model for bottom-closed)</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = {@code false} + {@link BlockStateProperties#HALF} = {@link Half#TOP}
     *         -> {@code trapdoorTopModel} (all facings use same model for top-closed)</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = {@code true} + {@link BlockStateProperties#HALF} = {@link Half#BOTTOM}
     *         -> {@code trapdoorOpenModel} with Y-rotations by facing</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@code true} -> 270° Y rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = {@code true} + {@link BlockStateProperties#HALF} = {@link Half#TOP}
     *         -> {@code trapdoorOpenModel} with Y-rotations by facing</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@code true} -> 270° Y rotation</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to create the blockstate for.
     * @param trapdoorOpenModel The {@link ResourceLocation} of the model to use for open trapdoor states.
     * @param trapdoorTopModel The {@link ResourceLocation} of the model to use for top half closed trapdoor states.
     * @param trapdoorBottomModel The {@link ResourceLocation} of the model to use for bottom half closed trapdoor states.
     *
     * @return A {@link BlockStateDefinition} with comprehensive trapdoor blockstate variants.
     *
     * @see #trapdoorBlockState(Supplier, ResourceLocation)
     * @see #trapdoorBlockState(Supplier)
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition trapdoorBlockState(Supplier<Block> targetBlock, ResourceLocation trapdoorOpenModel, ResourceLocation trapdoorTopModel, ResourceLocation trapdoorBottomModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch
                                .properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN)
                                .select(Direction.NORTH, Half.BOTTOM, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorBottomModel))
                                .select(Direction.SOUTH, Half.BOTTOM, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorBottomModel))
                                .select(Direction.EAST, Half.BOTTOM, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorBottomModel))
                                .select(Direction.WEST, Half.BOTTOM, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorBottomModel))
                                .select(Direction.NORTH, Half.TOP, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorTopModel))
                                .select(Direction.SOUTH, Half.TOP, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorTopModel))
                                .select(Direction.EAST, Half.TOP, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorTopModel))
                                .select(Direction.WEST, Half.TOP, false, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorTopModel))
                                .select(Direction.NORTH, Half.BOTTOM, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel))
                                .select(Direction.SOUTH, Half.BOTTOM, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.EAST, Half.BOTTOM, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, Half.BOTTOM, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.NORTH, Half.TOP, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel))
                                .select(Direction.SOUTH, Half.TOP, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.EAST, Half.TOP, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, Half.TOP, true, Variant.variant()
                                        .with(VariantProperties.MODEL, trapdoorOpenModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
    }

    /**
     * Overloaded variant of {@link #trapdoorBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for trapdoor blocks using {@link MultiVariantGenerator} with different models
     * for each combination of facing, half, and open state. Automatically determines model locations using standard
     * naming convention (base model with "_open", "_top", "_bottom" suffixes).
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#OPEN} = {@code false} + {@link BlockStateProperties#HALF} = {@link Half#BOTTOM}
     *         -> {@code baseTrapdoorId + "_bottom"} (all facings use same model for bottom-closed)</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = {@code false} + {@link BlockStateProperties#HALF} = {@link Half#TOP}
     *         -> {@code baseTrapdoorId + "_top"} (all facings use same model for top-closed)</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = {@code true} + {@link BlockStateProperties#HALF} = {@link Half#BOTTOM}
     *         -> {@code baseTrapdoorId + "_open"} with Y-rotations by facing</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@code true} -> 270° Y rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = {@code true} + {@link BlockStateProperties#HALF} = {@link Half#TOP}
     *         -> {@code baseTrapdoorId + "_open"} with Y-rotations by facing</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@code true} -> 270° Y rotation</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to create the blockstate for.
     * @param baseTrapdoorId Base model location; "_open", "_top", "_bottom" are appended automatically.
     *
     * @return A {@link BlockStateDefinition} with comprehensive trapdoor blockstate variants using automatic model resolution.
     *
     * @see #trapdoorBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #trapdoorBlockState(Supplier)
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition trapdoorBlockState(Supplier<Block> targetBlock, ResourceLocation baseTrapdoorId) {
        return trapdoorBlockState(targetBlock, baseTrapdoorId.withSuffix("_open"), baseTrapdoorId.withSuffix("_top"), baseTrapdoorId.withSuffix("_bottom"));
    }

    /**
     * Overloaded variant of {@link #trapdoorBlockState(Supplier, ResourceLocation)}. Creates a {@link BlockStateDefinition}
     * for trapdoor blocks using {@link MultiVariantGenerator} with different models for each combination of facing, half,
     * and open state. Uses fully automatic model resolution based on the trapdoor's registry ID.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#OPEN} = false + {@link BlockStateProperties#HALF} = {@link Half#BOTTOM}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_bottom")} (all facings use same model for bottom-closed)</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@code false} -> 0° rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = false + {@link BlockStateProperties#HALF} = {@link Half#TOP}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_top")} (all facings use same model for top-closed)</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@code false} -> 0° rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = true + {@link BlockStateProperties#HALF} = {@link Half#BOTTOM}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_open")} with Y-rotations by facing</li>
     *         <li>{@link Direction#NORTH} + {@link Half#BOTTOM} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#BOTTOM} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#BOTTOM} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#BOTTOM} + {@code true} -> 270° Y rotation</li>
     *         <li>{@link BlockStateProperties#OPEN} = true + {@link BlockStateProperties#HALF} = {@link Half#TOP}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_open")} with Y-rotations by facing</li>
     *         <li>{@link Direction#NORTH} + {@link Half#TOP} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#SOUTH} + {@link Half#TOP} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@link Half#TOP} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#WEST} + {@link Half#TOP} + {@code true} -> 270° Y rotation</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the trapdoor {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with comprehensive trapdoor blockstate variants using fully automatic model resolution.
     *
     * @see #trapdoorBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #trapdoorBlockState(Supplier, ResourceLocation)
     * @see #trapdoorBottom(Supplier, ResourceLocation)
     * @see #trapdoorOpen(Supplier, ResourceLocation)
     * @see #trapdoorTop(Supplier, ResourceLocation)
     * @see #trapdoor(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition trapdoorBlockState(Supplier<Block> targetBlock) {
        return trapdoorBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get()));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_INVENTORY} template for fence blocks.
     * This model represents the inventory appearance of fence blocks and includes a corresponding item model.
     * The model will be automatically named {@code targetFenceId.getPath() + "_inventory"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceTexture The {@link ResourceLocation} representing the texture of the fence.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_INVENTORY} template.
     *
     * @see #fenceInventory(Supplier)
     * @see #fencePost(Supplier, ResourceLocation)
     * @see #fenceSide(Supplier, ResourceLocation)
     * @see #fence(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fenceInventory(Supplier<Block> targetBlock, ResourceLocation fenceTexture) {
        ResourceLocation targetFenceId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.FENCE_INVENTORY)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(fenceTexture)))
                .withOrdinalModelDefinitions(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get(), "_inventory"))))
                .withCustomName(targetFenceId.getPath().concat("_inventory"));
    }

    /**
     * Overloaded variant of {@link #fenceInventory(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FENCE_INVENTORY} template for fence blocks using automatic texture resolution.
     * Automatically determines the fence texture based on the fence's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_INVENTORY} template.
     *
     * @see #fenceInventory(Supplier, ResourceLocation)
     * @see #fencePost(Supplier)
     * @see #fenceSide(Supplier)
     * @see #fence(Supplier)
     */
    public static BlockModelDefinition fenceInventory(Supplier<Block> targetBlock) {
        return fenceInventory(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_POST} template for fence blocks.
     * This model represents the central post of fence blocks. The model will be automatically named
     * {@code targetFenceId.getPath() + "_post"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceTexture The {@link ResourceLocation} representing the texture of the fence.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_POST} template.
     *
     * @see #fencePost(Supplier)
     * @see #fenceInventory(Supplier, ResourceLocation)
     * @see #fenceSide(Supplier, ResourceLocation)
     * @see #fence(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fencePost(Supplier<Block> targetBlock, ResourceLocation fenceTexture) {
        ResourceLocation targetFenceId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.FENCE_POST)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(fenceTexture)))
                .withCustomName(targetFenceId.getPath().concat("_post"));
    }

    /**
     * Overloaded variant of {@link #fencePost(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FENCE_POST} template for fence blocks using automatic texture resolution.
     * Automatically determines the fence texture based on the fence's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_POST} template.
     *
     * @see #fencePost(Supplier, ResourceLocation)
     * @see #fenceInventory(Supplier)
     * @see #fenceSide(Supplier)
     * @see #fence(Supplier)
     */
    public static BlockModelDefinition fencePost(Supplier<Block> targetBlock) {
        return fencePost(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_SIDE} template for fence blocks.
     * This model represents the side connections of fence blocks. Note that this method uses the same template
     * as fence posts but with different naming. The model will be automatically named
     * {@code targetFenceId.getPath() + "_side"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceTexture The {@link ResourceLocation} representing the texture of the fence.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_POST} template.
     *
     * @see #fenceSide(Supplier)
     * @see #fenceInventory(Supplier, ResourceLocation)
     * @see #fencePost(Supplier, ResourceLocation)
     * @see #fence(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fenceSide(Supplier<Block> targetBlock, ResourceLocation fenceTexture) {
        ResourceLocation targetFenceId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.FENCE_SIDE)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(fenceTexture)))
                .withCustomName(targetFenceId.getPath().concat("_side"));
    }

    /**
     * Overloaded variant of {@link #fenceSide(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FENCE_POST} template for fence blocks using automatic texture resolution.
     * This model represents the side connections of fence blocks. Automatically determines the fence texture
     * based on the fence's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_POST} template.
     *
     * @see #fenceSide(Supplier, ResourceLocation)
     * @see #fenceInventory(Supplier)
     * @see #fencePost(Supplier)
     * @see #fence(Supplier)
     */
    public static BlockModelDefinition fenceSide(Supplier<Block> targetBlock) {
        return fenceSide(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with all three fence variants (inventory, post, side)
     * using the {@link ModelTemplates#FENCE_INVENTORY}, {@link ModelTemplates#FENCE_POST}, and {@link ModelTemplates#FENCE_POST}
     * templates. The inventory variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceTexture The {@link ResourceLocation} representing the texture of the fence.
     *
     * @return A {@link BlockModelDefinition} with all fence variants.
     *
     * @see #fence(Supplier)
     * @see #fenceInventory(Supplier, ResourceLocation)
     * @see #fencePost(Supplier, ResourceLocation)
     * @see #fenceSide(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fence(Supplier<Block> targetBlock, ResourceLocation fenceTexture) {
        return fenceInventory(targetBlock, fenceTexture)
                .withOrdinalModelDefinitions(fencePost(targetBlock, fenceTexture), fenceSide(targetBlock, fenceTexture));
    }

    /**
     * Overloaded variant of {@link #fence(Supplier, ResourceLocation)}. Creates a comprehensive {@link BlockModelDefinition}
     * with all three fence variants using automatic texture resolution. Automatically determines the fence texture
     * based on the fence's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with all fence variants using automatic texture resolution.
     *
     * @see #fence(Supplier, ResourceLocation)
     * @see #fenceInventory(Supplier)
     * @see #fencePost(Supplier)
     * @see #fenceSide(Supplier)
     */
    public static BlockModelDefinition fence(Supplier<Block> targetBlock) {
        return fence(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockStateDefinition} for fence blocks using {@link MultiPartGenerator} with different models
     * for the post and side connections based on block state properties. This method handles the multipart system
     * used by Minecraft fences to show connections to adjacent blocks.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>Base fence post model (always present)</li>
     *         <li>North connection: {@code fenceSideModel} with 0° rotation and UV lock</li>
     *         <li>East connection: {@code fenceSideModel} with 90° Y rotation and UV lock</li>
     *         <li>South connection: {@code fenceSideModel} with 180° Y rotation and UV lock</li>
     *         <li>West connection: {@code fenceSideModel} with 270° Y rotation and UV lock</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to create the blockstate for.
     * @param fencePostModel The {@link ResourceLocation} of the model to use for the fence post.
     * @param fenceSideModel The {@link ResourceLocation} of the model to use for fence side connections.
     *
     * @return A {@link BlockStateDefinition} with fence multipart variants.
     *
     * @see #fenceBlockState(Supplier)
     * @see #fence(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition fenceBlockState(Supplier<Block> targetBlock, ResourceLocation fencePostModel, ResourceLocation fenceSideModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiPartGenerator.multiPart(targetBlock.get())
                        .with(Variant.variant()
                                .with(VariantProperties.MODEL, fencePostModel))
                        .with(Condition.condition()
                                .term(BlockStateProperties.NORTH, true), Variant.variant()
                                .with(VariantProperties.MODEL, fenceSideModel)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.EAST, true), Variant.variant()
                                .with(VariantProperties.MODEL, fenceSideModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.SOUTH, true), Variant.variant()
                                .with(VariantProperties.MODEL, fenceSideModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                .with(VariantProperties.UV_LOCK, true))
                        .with(Condition.condition()
                                .term(BlockStateProperties.WEST, true), Variant.variant()
                                .with(VariantProperties.MODEL, fenceSideModel)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.UV_LOCK, true)));
    }

    /**
     * Overloaded variant of {@link #fenceBlockState(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockStateDefinition}
     * for fence blocks using {@link MultiPartGenerator} with automatic model resolution. Uses fully automatic model
     * resolution based on the fence's registry ID with "_post" and "_side" suffixes.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>Base fence post model: {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_post")}</li>
     *         <li>North connection: {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")} with 0° rotation and UV lock</li>
     *         <li>East connection: {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")} with 90° Y rotation and UV lock</li>
     *         <li>South connection: {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")} with 180° Y rotation and UV lock</li>
     *         <li>West connection: {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_side")} with 270° Y rotation and UV lock</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with fence multipart variants using fully automatic model resolution.
     *
     * @see #fenceBlockState(Supplier, ResourceLocation, ResourceLocation)
     * @see #fence(Supplier)
     */
    public static BlockStateDefinition fenceBlockState(Supplier<Block> targetBlock) {
        return fenceBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get(), "_post"), ModelLocationUtils.getModelLocation(targetBlock.get(), "_side"));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_CLOSED} template for fence gate blocks.
     * This model represents the closed state of fence gates and includes a corresponding item model.
     * The model will be automatically named using the fence gate's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceGateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model location resolution.
     * @param fenceGateTexture The {@link ResourceLocation} representing the texture of the fence gate.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_CLOSED} template.
     *
     * @see #fenceGateClosed(Supplier)
     * @see #fenceGateOpen(Supplier, ResourceLocation)
     * @see #fenceGateWallClosed(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fenceGateClosed(Supplier<Block> targetBlock, ResourceLocation fenceGateTexture) {
        return new BlockModelDefinition(ModelTemplates.FENCE_GATE_CLOSED)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(fenceGateTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Overloaded variant of {@link #fenceGateClosed(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FENCE_GATE_CLOSED} template for fence gate blocks using automatic texture resolution.
     * Automatically determines the fence gate texture based on the fence gate's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_CLOSED} template.
     *
     * @see #fenceGateClosed(Supplier, ResourceLocation)
     * @see #fenceGateOpen(Supplier)
     * @see #fenceGateWallClosed(Supplier)
     */
    public static BlockModelDefinition fenceGateClosed(Supplier<Block> targetBlock) {
        return fenceGateClosed(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_OPEN} template for fence gate blocks.
     * This model represents the open state of fence gates. The model will be automatically named
     * {@code targetFenceGateId.getPath() + "_open"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceGateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceGateTexture The {@link ResourceLocation} representing the texture of the fence gate.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_OPEN} template.
     *
     * @see #fenceGateOpen(Supplier)
     * @see #fenceGateClosed(Supplier, ResourceLocation)
     * @see #fenceGateWallOpen(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fenceGateOpen(Supplier<Block> targetBlock, ResourceLocation fenceGateTexture) {
        ResourceLocation targetFenceGateId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.FENCE_GATE_OPEN)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(fenceGateTexture)))
                .withCustomName(targetFenceGateId.getPath().concat("_open"));
    }

    /**
     * Overloaded variant of {@link #fenceGateOpen(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FENCE_GATE_OPEN} template for fence gate blocks using automatic texture resolution.
     * Automatically determines the fence gate texture based on the fence gate's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_OPEN} template.
     *
     * @see #fenceGateOpen(Supplier, ResourceLocation)
     * @see #fenceGateClosed(Supplier)
     * @see #fenceGateWallOpen(Supplier)
     */
    public static BlockModelDefinition fenceGateOpen(Supplier<Block> targetBlock) {
        return fenceGateOpen(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_WALL_CLOSED} template for fence gate blocks.
     * This model represents the closed state of fence gates when attached to walls. The model will be automatically named
     * {@code targetFenceGateId.getPath() + "_wall"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceGateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceGateTexture The {@link ResourceLocation} representing the texture of the fence gate.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_WALL_CLOSED} template.
     *
     * @see #fenceGateWallClosed(Supplier)
     * @see #fenceGateClosed(Supplier, ResourceLocation)
     * @see #fenceGateWallOpen(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fenceGateWallClosed(Supplier<Block> targetBlock, ResourceLocation fenceGateTexture) {
        ResourceLocation targetFenceGateId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.FENCE_GATE_WALL_CLOSED)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(fenceGateTexture)))
                .withCustomName(targetFenceGateId.getPath().concat("_wall"));
    }

    /**
     * Overloaded variant of {@link #fenceGateWallClosed(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FENCE_GATE_WALL_CLOSED} template for fence gate blocks using automatic texture resolution.
     * This model represents the closed state of fence gates when attached to walls. Automatically determines the fence gate texture
     * based on the fence gate's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_WALL_CLOSED} template.
     *
     * @see #fenceGateWallClosed(Supplier, ResourceLocation)
     * @see #fenceGateClosed(Supplier)
     * @see #fenceGateWallOpen(Supplier)
     */
    public static BlockModelDefinition fenceGateWallClosed(Supplier<Block> targetBlock) {
        return fenceGateWallClosed(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_WALL_OPEN} template for fence gate blocks.
     * This model represents the open state of fence gates when attached to walls. The model will be automatically named
     * {@code targetFenceGateId.getPath() + "_wall_open"}.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceGateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceGateTexture The {@link ResourceLocation} representing the texture of the fence gate.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_WALL_OPEN} template.
     *
     * @see #fenceGateWallOpen(Supplier)
     * @see #fenceGateOpen(Supplier, ResourceLocation)
     * @see #fenceGateWallClosed(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fenceGateWallOpen(Supplier<Block> targetBlock, ResourceLocation fenceGateTexture) {
        ResourceLocation targetFenceGateId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

        return new BlockModelDefinition(ModelTemplates.FENCE_GATE_WALL_OPEN)
                .withTextureMapping(TextureMapping.defaultTexture(RegistryUtil.pickBlockPrefix(fenceGateTexture)))
                .withCustomName(targetFenceGateId.getPath().concat("_wall_open"));
    }

    /**
     * Overloaded variant of {@link #fenceGateWallOpen(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FENCE_GATE_WALL_OPEN} template for fence gate blocks using automatic texture resolution.
     * This model represents the open state of fence gates when attached to walls. Automatically determines the fence gate texture
     * based on the fence gate's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FENCE_GATE_WALL_OPEN} template.
     *
     * @see #fenceGateWallOpen(Supplier, ResourceLocation)
     * @see #fenceGateOpen(Supplier)
     * @see #fenceGateWallClosed(Supplier)
     */
    public static BlockModelDefinition fenceGateWallOpen(Supplier<Block> targetBlock) {
        return fenceGateWallOpen(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with all four fence gate variants (closed, open, wall closed, wall open)
     * using the {@link ModelTemplates#FENCE_GATE_CLOSED}, {@link ModelTemplates#FENCE_GATE_OPEN}, {@link ModelTemplates#FENCE_GATE_WALL_CLOSED},
     * and {@link ModelTemplates#FENCE_GATE_WALL_OPEN} templates. The closed variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockPrefix(fenceGateTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param fenceGateTexture The {@link ResourceLocation} representing the texture of the fence gate.
     *
     * @return A {@link BlockModelDefinition} with all fence gate variants.
     *
     * @see #fenceGate(Supplier)
     * @see #fenceGateClosed(Supplier, ResourceLocation)
     * @see #fenceGateOpen(Supplier, ResourceLocation)
     * @see #fenceGateWallClosed(Supplier, ResourceLocation)
     * @see #fenceGateWallOpen(Supplier, ResourceLocation)
     */
    public static BlockModelDefinition fenceGate(Supplier<Block> targetBlock, ResourceLocation fenceGateTexture) {
        return fenceGateClosed(targetBlock, fenceGateTexture)
                .withOrdinalModelDefinitions(fenceGateOpen(targetBlock, fenceGateTexture), fenceGateWallClosed(targetBlock, fenceGateTexture), fenceGateWallOpen(targetBlock, fenceGateTexture));
    }

    /**
     * Overloaded variant of {@link #fenceGate(Supplier, ResourceLocation)}. Creates a comprehensive {@link BlockModelDefinition}
     * with all four fence gate variants using automatic texture resolution. Automatically determines the fence gate texture
     * based on the fence gate's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TEXTURE} -> {@code RegistryUtil.pickBlockTexture(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with all fence gate variants using automatic texture resolution.
     *
     * @see #fenceGate(Supplier, ResourceLocation)
     * @see #fenceGateClosed(Supplier)
     * @see #fenceGateOpen(Supplier)
     * @see #fenceGateWallClosed(Supplier)
     * @see #fenceGateWallOpen(Supplier)
     */
    public static BlockModelDefinition fenceGate(Supplier<Block> targetBlock) {
        return fenceGate(targetBlock, RegistryUtil.pickBlockTexture(targetBlock));
    }

    /**
     * Creates a {@link BlockStateDefinition} for fence gate blocks using {@link MultiVariantGenerator} with different models
     * for each combination of facing, wall attachment, and open state. This method handles the complex variant system
     * used by Minecraft fence gates to show open/closed states and proper orientations based on placement.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code fenceGateModel} with Y-rotations by facing</li>
     *         <li>{@link Direction#SOUTH} + {@code false} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@code false} + {@code false} -> 90° Y rotation</li>
     *         <li>{@link Direction#NORTH} + {@code false} + {@code false} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@code false} + {@code false} -> 270° Y rotation</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code fenceGateWallModel} with Y-rotations by facing</li>
     *         <li>{@link Direction#SOUTH} + {@code true} + {@code false} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@code true} + {@code false} -> 90° Y rotation</li>
     *         <li>{@link Direction#NORTH} + {@code true} + {@code false} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@code true} + {@code false} -> 270° Y rotation</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code fenceGateOpenModel} with Y-rotations by facing</li>
     *         <li>{@link Direction#SOUTH} + {@code false} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@code false} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#NORTH} + {@code false} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@code false} + {@code true} -> 270° Y rotation</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code fenceGateWallOpenModel} with Y-rotations by facing</li>
     *         <li>{@link Direction#SOUTH} + {@code true} + {@code true} -> 0° rotation</li>
     *         <li>{@link Direction#WEST} + {@code true} + {@code true} -> 90° Y rotation</li>
     *         <li>{@link Direction#NORTH} + {@code true} + {@code true} -> 180° Y rotation</li>
     *         <li>{@link Direction#EAST} + {@code true} + {@code true} -> 270° Y rotation</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to create the blockstate for.
     * @param fenceGateModel The {@link ResourceLocation} of the model to use for closed fence gates not attached to walls.
     * @param fenceGateOpenModel The {@link ResourceLocation} of the model to use for open fence gates not attached to walls.
     * @param fenceGateWallModel The {@link ResourceLocation} of the model to use for closed fence gates attached to walls.
     * @param fenceGateWallOpenModel The {@link ResourceLocation} of the model to use for open fence gates attached to walls.
     * @param uvLock Whether to lock UV coordinates for proper texture alignment.
     *
     * @return A {@link BlockStateDefinition} with comprehensive fence gate blockstate variants.
     *
     * @see #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #fenceGateBlockState(Supplier, ResourceLocation)
     * @see #fenceGateBlockState(Supplier)
     */
    public static BlockStateDefinition fenceGateBlockState(Supplier<Block> targetBlock, ResourceLocation fenceGateModel, ResourceLocation fenceGateOpenModel, ResourceLocation fenceGateWallModel, ResourceLocation fenceGateWallOpenModel, boolean uvLock) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get(), Variant.variant()
                                .with(VariantProperties.UV_LOCK, uvLock))
                        .with(PropertyDispatch
                                .property(BlockStateProperties.HORIZONTAL_FACING)
                                .select(Direction.SOUTH, Variant.variant())
                                .select(Direction.WEST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.NORTH, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.EAST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))
                        .with(PropertyDispatch
                                .properties(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN)
                                .select(false, false, Variant.variant()
                                        .with(VariantProperties.MODEL, fenceGateModel))
                                .select(true, false, Variant.variant()
                                        .with(VariantProperties.MODEL, fenceGateWallModel))
                                .select(false, true, Variant.variant()
                                        .with(VariantProperties.MODEL, fenceGateOpenModel))
                                .select(true, true, Variant.variant()
                                        .with(VariantProperties.MODEL, fenceGateWallOpenModel))));
    }

    /**
     * Overloaded variant of {@link #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, boolean)}.
     * Creates a {@link BlockStateDefinition} for fence gate blocks using {@link MultiVariantGenerator} with different models
     * for each combination of facing, wall attachment, and open state, with UV locking enabled by default.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code fenceGateModel} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code fenceGateWallModel} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code fenceGateOpenModel} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code fenceGateWallOpenModel} with Y-rotations by facing</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to create the blockstate for.
     * @param fenceGateModel The {@link ResourceLocation} of the model to use for closed fence gates not attached to walls.
     * @param fenceGateOpenModel The {@link ResourceLocation} of the model to use for open fence gates not attached to walls.
     * @param fenceGateWallModel The {@link ResourceLocation} of the model to use for closed fence gates attached to walls.
     * @param fenceGateWallOpenModel The {@link ResourceLocation} of the model to use for open fence gates attached to walls.
     *
     * @return A {@link BlockStateDefinition} with comprehensive fence gate blockstate variants (UV lock enabled by default).
     *
     * @see #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, boolean)
     * @see #fenceGateBlockState(Supplier, ResourceLocation)
     * @see #fenceGateBlockState(Supplier)
     */
    public static BlockStateDefinition fenceGateBlockState(Supplier<Block> targetBlock, ResourceLocation fenceGateModel, ResourceLocation fenceGateOpenModel, ResourceLocation fenceGateWallModel, ResourceLocation fenceGateWallOpenModel) {
        return fenceGateBlockState(targetBlock, fenceGateModel, fenceGateOpenModel, fenceGateWallModel, fenceGateWallOpenModel, true);
    }

    /**
     * Overloaded variant of {@link #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for fence gate blocks using {@link MultiVariantGenerator} with automatic model
     * resolution. Automatically determines all fence gate model locations using standard naming convention with
     * base model location plus "_open", "_wall", "_wall_open" suffixes.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code fenceGateModel} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code fenceGateModel + "_wall"} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code fenceGateModel + "_open"} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code fenceGateModel + "_wall_open"} with Y-rotations by facing</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to create the blockstate for.
     * @param fenceGateModel Base model location; "_open", "_wall", "_wall_open" are appended automatically.
     *
     * @return A {@link BlockStateDefinition} with comprehensive fence gate blockstate variants using automatic model resolution.
     *
     * @see #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, boolean)
     * @see #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #fenceGateBlockState(Supplier)
     */
    public static BlockStateDefinition fenceGateBlockState(Supplier<Block> targetBlock, ResourceLocation fenceGateModel) {
        return fenceGateBlockState(targetBlock, fenceGateModel, fenceGateModel.withSuffix("_open"), fenceGateModel.withSuffix("_wall"), fenceGateModel.withSuffix("_wall_open"));
    }

    /**
     * Overloaded variant of {@link #fenceGateBlockState(Supplier, ResourceLocation)}. Creates a {@link BlockStateDefinition}
     * for fence gate blocks using {@link MultiVariantGenerator} with fully automatic model resolution. Uses fully
     * automatic model resolution based on the fence gate's registry ID.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code false}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_wall")} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code false} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_open")} with Y-rotations by facing</li>
     *         <li>{@link BlockStateProperties#IN_WALL} = {@code true} + {@link BlockStateProperties#OPEN} = {@code true}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_wall_open")} with Y-rotations by facing</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the fence gate {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with comprehensive fence gate blockstate variants using fully automatic model resolution.
     *
     * @see #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, boolean)
     * @see #fenceGateBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #fenceGateBlockState(Supplier, ResourceLocation)
     */
    public static BlockStateDefinition fenceGateBlockState(Supplier<Block> targetBlock) {
        return fenceGateBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get()));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template for farmland blocks.
     * This model represents the dry farmland top and includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#DIRT} -> {@code RegistryUtil.pickBlockPrefix(dirtTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(dryFarmlandTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model location resolution.
     * @param dryFarmlandTexture The {@link ResourceLocation} representing the texture of the dry farmland top.
     * @param dirtTexture The {@link ResourceLocation} representing the texture of the dirt sides.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template.
     *
     * @see #farmlandDry(Supplier, ResourceLocation)
     * @see #farmlandDry(Supplier)
     * @see #farmlandMoist(Supplier, ResourceLocation, ResourceLocation)
     * @see #farmland(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition farmlandDry(Supplier<Block> targetBlock, ResourceLocation dryFarmlandTexture, ResourceLocation dirtTexture) {
        return new BlockModelDefinition(ModelTemplates.FARMLAND)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.DIRT, RegistryUtil.pickBlockPrefix(dirtTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(dryFarmlandTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Overloaded variant of {@link #farmlandDry(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FARMLAND} template for farmland blocks using automatic texture resolution for the dirt sides.
     * This model represents the dry farmland top and includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(dryFarmlandTexture)}</li>
     *         <li>{@link TextureSlot#DIRT} -> resolved automatically</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model location resolution.
     * @param dryFarmlandTexture The {@link ResourceLocation} representing the texture of the dry farmland top.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template.
     *
     * @see #farmlandDry(Supplier, ResourceLocation, ResourceLocation)
     * @see #farmlandDry(Supplier)
     */
    public static BlockModelDefinition farmlandDry(Supplier<Block> targetBlock, ResourceLocation dryFarmlandTexture) {
        return farmlandDry(targetBlock, dryFarmlandTexture, RegistryUtil.getTextureLocationOrDefault(
                dryFarmlandTexture.withPath(dryFarmlandTexture.getPath().replace("_farmland", "_dirt")),
                RegistryUtil.getTextureLocationOrDefault(dryFarmlandTexture.withPath(dryFarmlandTexture.getPath().replace("_farmland", "")))));
    }

    /**
     * Overloaded variant of {@link #farmlandDry(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FARMLAND} template for farmland blocks using automatic texture resolution.
     * This model represents the dry farmland top and includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(RegistryUtil.getTextureLocationOrDefault(targetBlock))}</li>
     *         <li>{@link TextureSlot#DIRT} -> resolved automatically</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template.
     *
     * @see #farmlandDry(Supplier, ResourceLocation)
     * @see #farmlandDry(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition farmlandDry(Supplier<Block> targetBlock) {
        return farmlandDry(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template for farmland blocks.
     * This model represents the moist farmland top and includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#DIRT} -> {@code RegistryUtil.pickBlockPrefix(dirtTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(moistFarmlandTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model location resolution.
     * @param moistFarmlandTexture The {@link ResourceLocation} representing the texture of the moist farmland top.
     * @param dirtTexture The {@link ResourceLocation} representing the texture of the dirt sides.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template.
     *
     * @see #farmlandMoist(Supplier, ResourceLocation)
     * @see #farmlandMoist(Supplier)
     * @see #farmlandDry(Supplier, ResourceLocation, ResourceLocation)
     * @see #farmland(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition farmlandMoist(Supplier<Block> targetBlock, ResourceLocation moistFarmlandTexture, ResourceLocation dirtTexture) {
        return new BlockModelDefinition(ModelTemplates.FARMLAND)
                .withTextureMapping(new TextureMapping()
                        .put(TextureSlot.DIRT, RegistryUtil.pickBlockPrefix(dirtTexture))
                        .put(TextureSlot.TOP, RegistryUtil.pickBlockPrefix(moistFarmlandTexture)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    /**
     * Overloaded variant of {@link #farmlandMoist(Supplier, ResourceLocation, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FARMLAND} template for farmland blocks using automatic texture resolution for the dirt sides.
     * This model represents the moist farmland top and includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(moistFarmlandTexture)}</li>
     *         <li>{@link TextureSlot#DIRT} -> resolved automatically</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model location resolution.
     * @param moistFarmlandTexture The {@link ResourceLocation} representing the texture of the moist farmland top.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template.
     *
     * @see #farmlandMoist(Supplier, ResourceLocation, ResourceLocation)
     * @see #farmlandMoist(Supplier)
     */
    public static BlockModelDefinition farmlandMoist(Supplier<Block> targetBlock, ResourceLocation moistFarmlandTexture) {
        return farmlandMoist(targetBlock, moistFarmlandTexture, RegistryUtil.getTextureLocationOrDefault(
                moistFarmlandTexture.withPath(moistFarmlandTexture.getPath().replace("_farmland_moist", "_dirt")),
                RegistryUtil.getTextureLocationOrDefault(moistFarmlandTexture.withPath(moistFarmlandTexture.getPath().replace("_farmland_moist", "")))));
    }

    /**
     * Overloaded variant of {@link #farmlandMoist(Supplier, ResourceLocation)}. Creates a {@link BlockModelDefinition}
     * with the {@link ModelTemplates#FARMLAND} template for farmland blocks using automatic texture resolution.
     * This model represents the moist farmland top and includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_moist"))}</li>
     *         <li>{@link TextureSlot#DIRT} -> resolved automatically</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#FARMLAND} template.
     *
     * @see #farmlandMoist(Supplier, ResourceLocation)
     * @see #farmlandMoist(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition farmlandMoist(Supplier<Block> targetBlock) {
        return farmlandMoist(targetBlock, RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_moist"));
    }

    /**
     * Creates a comprehensive {@link BlockModelDefinition} with both dry and moist farmland variants using the
     * {@link ModelTemplates#FARMLAND} template. Each variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#DIRT} -> {@code RegistryUtil.pickBlockPrefix(dirtTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(dryFarmlandTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(moistFarmlandTexture)}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param dryFarmlandTexture The {@link ResourceLocation} representing the texture of the dry farmland top.
     * @param moistFarmlandTexture The {@link ResourceLocation} representing the texture of the moist farmland top.
     * @param dirtTexture The {@link ResourceLocation} representing the texture of the dirt sides.
     *
     * @return A {@link BlockModelDefinition} with both dry and moist farmland variants.
     *
     * @see #farmland(Supplier, ResourceLocation, ResourceLocation)
     * @see #farmland(Supplier)
     * @see #farmlandDry(Supplier, ResourceLocation, ResourceLocation)
     * @see #farmlandMoist(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition farmland(Supplier<Block> targetBlock, ResourceLocation dryFarmlandTexture, ResourceLocation moistFarmlandTexture, ResourceLocation dirtTexture) {
        return farmlandDry(targetBlock, dryFarmlandTexture, dirtTexture)
                .withOrdinalModelDefinition(farmlandMoist(targetBlock, moistFarmlandTexture, dirtTexture));
    }

    /**
     * Overloaded variant of {@link #farmland(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a comprehensive {@link BlockModelDefinition} with both dry and moist farmland variants using automatic
     * texture resolution for the dirt sides.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(dryFarmlandTexture)}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(moistFarmlandTexture)}</li>
     *         <li>{@link TextureSlot#DIRT} -> resolved automatically</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model location resolution and custom naming.
     * @param dryFarmlandTexture The {@link ResourceLocation} representing the texture of the dry farmland top.
     * @param moistFarmlandTexture The {@link ResourceLocation} representing the texture of the moist farmland top.
     *
     * @return A {@link BlockModelDefinition} with both dry and moist farmland variants.
     *
     * @see #farmland(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #farmland(Supplier)
     */
    public static BlockModelDefinition farmland(Supplier<Block> targetBlock, ResourceLocation dryFarmlandTexture, ResourceLocation moistFarmlandTexture) {
        return farmlandDry(targetBlock, dryFarmlandTexture)
                .withOrdinalModelDefinition(farmlandMoist(targetBlock, moistFarmlandTexture));
    }

    /**
     * Overloaded variant of {@link #farmland(Supplier, ResourceLocation, ResourceLocation)}. Creates a comprehensive
     * {@link BlockModelDefinition} with both dry and moist farmland variants using automatic texture resolution.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(RegistryUtil.getTextureLocationOrDefault(targetBlock))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.pickBlockPrefix(RegistryUtil.getTextureLocationOrDefaultWithSuffix(targetBlock, "_moist"))}</li>
     *         <li>{@link TextureSlot#DIRT} -> resolved automatically</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to be used for
     *                    automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with both dry and moist farmland variants using automatic texture resolution.
     *
     * @see #farmland(Supplier, ResourceLocation, ResourceLocation)
     * @see #farmland(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition farmland(Supplier<Block> targetBlock) {
        return farmlandDry(targetBlock)
                .withOrdinalModelDefinition(farmlandMoist(targetBlock));
    }

    /**
     * Creates a {@link BlockStateDefinition} for farmland blocks using {@link MultiVariantGenerator} with different models
     * based on moisture level.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@code MOISTURE >= 7} -> {@code moistFarmlandModel}</li>
     *         <li>{@code MOISTURE < 7} -> {@code dryFarmlandModel}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to create the blockstate for.
     * @param dryFarmlandModel The {@link ResourceLocation} of the model to use for the dry farmland variant.
     * @param moistFarmlandModel The {@link ResourceLocation} of the model to use for the moist farmland variant.
     *
     * @return A {@link BlockStateDefinition} with farmland moisture variants.
     *
     * @see #farmlandBlockState(Supplier)
     */
    public static BlockStateDefinition farmlandBlockState(Supplier<Block> targetBlock, ResourceLocation dryFarmlandModel, ResourceLocation moistFarmlandModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch.property(BlockStateProperties.MOISTURE)
                                .generate(moistureLevel -> moistureLevel.compareTo(7) >= 0
                                        ? Variant.variant().with(VariantProperties.MODEL, moistFarmlandModel)
                                        : Variant.variant().with(VariantProperties.MODEL, dryFarmlandModel))));
    }

    /**
     * Overloaded variant of {@link #farmlandBlockState(Supplier, ResourceLocation, ResourceLocation)}. Creates a
     * {@link BlockStateDefinition} for farmland blocks using {@link MultiVariantGenerator} with fully automatic model
     * resolution based on the farmland block's registry ID ("_moist" suffix for the moist variant).
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@code MOISTURE >= 7} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get()).withSuffix("_moist")}</li>
     *         <li>{@code MOISTURE < 7} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the farmland {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with farmland moisture variants using fully automatic model resolution.
     *
     * @see #farmlandBlockState(Supplier, ResourceLocation, ResourceLocation)
     */
    public static BlockStateDefinition farmlandBlockState(Supplier<Block> targetBlock) {
        return farmlandBlockState(targetBlock, ModelLocationUtils.getModelLocation(targetBlock.get()), ModelLocationUtils.getModelLocation(targetBlock.get()).withSuffix("_moist"));
    }

    /**
     * Creates a {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template (for "generated" item
     * models).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.pickItemPrefix(texLoc)}</li>
     *     </ul>
     *
     * @param texLoc The location to use for the {@code layer0} texture slot.
     *
     * @return A new {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template.
     *
     * @see #basicGenerated(Supplier)
     */
    public static ItemModelDefinition basicGenerated(ResourceLocation texLoc) {
        return new ItemModelDefinition(ModelTemplates.FLAT_ITEM)
                .withTextureMapping(TextureMapping.layer0(RegistryUtil.pickItemPrefix(texLoc)));
    }

    /**
     * Overloaded variant of {@link #basicGenerated(ResourceLocation)}. Creates a {@link ItemModelDefinition} with the
     * {@link ModelTemplates#FLAT_ITEM} template (for "generated" item models).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.getTextureLocationOrDefault(targetItem)}</li>
     *     </ul>
     *
     * @param targetItem The {@linkplain Item Item} to use as the base for the {@link ItemModelDefinition} {@code layer0}
     *                   texture lookup.
     *
     * @return A new {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template.
     *
     * @see #basicGenerated(ResourceLocation)
     */
    public static ItemModelDefinition basicGenerated(Supplier<Item> targetItem) {
        return basicGenerated(RegistryUtil.getTextureLocationOrDefault(targetItem));
    }

    /**
     * Creates a {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template (for "generated" item
     * models).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.pickBlockPrefix(texLoc)}</li>
     *     </ul>
     *
     * @param texLoc The location to use for the {@code layer0} texture slot.
     *
     * @return A new {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template.
     *
     * @see #generatedBlock(Supplier)
     */
    public static ItemModelDefinition generatedBlock(ResourceLocation texLoc) {
        return new ItemModelDefinition(ModelTemplates.FLAT_ITEM)
                .withTextureMapping(TextureMapping.layer0(RegistryUtil.pickBlockPrefix(texLoc)));
    }

    /**
     * Overloaded variant of {@link #generatedBlock(ResourceLocation)}. Creates a {@link ItemModelDefinition} with the
     * {@link ModelTemplates#FLAT_ITEM} template (for "generated" item models).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock)}</li>
     *     </ul>
     *
     * @param targetBlock The {@linkplain Block Block} to use as the base for the {@link ItemModelDefinition} {@code layer0}
     *                    texture lookup.
     *
     * @return A new {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template.
     *
     * @see #generatedBlock(ResourceLocation)
     */
    public static ItemModelDefinition generatedBlock(Supplier<Block> targetBlock) {
        return generatedBlock(RegistryUtil.getTextureLocationOrDefault(targetBlock));
    }

    /**
     * Creates a {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_HANDHELD_ITEM} template (for "handheld"
     * item models).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code texLoc}</li>
     *     </ul>
     *
     * @param texLoc The location to use for the {@code layer0} texture slot.
     *
     * @return A new {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_HANDHELD_ITEM} template.
     *
     * @see #basicHandheld(Supplier)
     */
    public static ItemModelDefinition basicHandheld(ResourceLocation texLoc) {
        return new ItemModelDefinition(ModelTemplates.FLAT_HANDHELD_ITEM)
                .withTextureMapping(TextureMapping.layer0(RegistryUtil.pickItemPrefix(texLoc)));
    }

    /**
     * Overloaded variant of {@link #basicHandheld(ResourceLocation)}. Creates a {@link ItemModelDefinition} with the
     * {@link ModelTemplates#FLAT_HANDHELD_ITEM} template (for "handheld" item models).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.getTextureLocationOrDefault(targetItem)}</li>
     *     </ul>
     *
     * @param targetItem The {@linkplain Item Item} to use as the base for the {@link ItemModelDefinition} {@code layer0}
     *                   texture lookup.
     *
     * @return A new {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_HANDHELD_ITEM} template.
     *
     * @see #basicGenerated(ResourceLocation)
     */
    public static ItemModelDefinition basicHandheld(Supplier<Item> targetItem) {
        return basicHandheld(RegistryUtil.getTextureLocationOrDefault(targetItem));
    }

    /**
     * Creates a handheld rod item model definition with the specified texture location.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.pickItemPrefix(texLoc)}</li>
     *     </ul>
     *
     * @param texLoc The {@link ResourceLocation} of the texture to be used for the rod item.
     *
     * @return An {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_HANDHELD_ROD_ITEM} template.
     *
     * @see ModelTemplates#FLAT_HANDHELD_ROD_ITEM
     * @see TextureMapping#layer0(ResourceLocation)
     */
    public static ItemModelDefinition handheldRod(ResourceLocation texLoc) {
        return new ItemModelDefinition(ModelTemplates.FLAT_HANDHELD_ROD_ITEM)
                .withTextureMapping(TextureMapping.layer0(RegistryUtil.pickItemPrefix(texLoc)));
    }

    /**
     * Overloaded variant of {@link #handheldRod(ResourceLocation)}. Creates a handheld rod item model definition using
     * the texture location derived from the provided item.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code RegistryUtil.pickItemPrefix(RegistryUtil.getTextureLocationOrDefault(targetItem))}</li>
     *     </ul>
     *
     * @param targetItem The {@code Supplier<Item>} used to automatically resolve the texture location.
     *
     * @return An {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_HANDHELD_ROD_ITEM} template.
     *
     * @see #handheldRod(ResourceLocation)
     * @see RegistryUtil#getTextureLocationOrDefault(Supplier)
     */
    public static ItemModelDefinition handheldRod(Supplier<Item> targetItem) {
        return handheldRod(RegistryUtil.getTextureLocationOrDefault(targetItem));
    }
}
