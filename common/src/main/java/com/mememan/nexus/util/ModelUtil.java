package com.mememan.nexus.util;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.client.model.block.BlockModelDefinition;
import com.mememan.nexus.client.model.item.ItemModelDefinition;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
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
        ResourceLocation baseSlabId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
                        .with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE)
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
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#INNER_LEFT} or {@link StairsShape#INNER_RIGHT} -> {@code RegistryUtil.pickBlockPrefix(innerStairsModel)}</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#OUTER_LEFT} or {@link StairsShape#OUTER_RIGHT} -> {@code RegistryUtil.pickBlockPrefix(outerStairsModel)}</li>
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
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#INNER_LEFT} or {@link StairsShape#INNER_RIGHT}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_inner")}</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#OUTER_LEFT} or {@link StairsShape#OUTER_RIGHT}
     *         -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_outer")}</li>
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
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
        ResourceLocation baseWallId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
        ResourceLocation baseButtonId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
        ResourceLocation baseButtonId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
        ResourceLocation basePressurePlateId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));

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
     * Creates a {@link ItemModelDefinition} with the {@link ModelTemplates#FLAT_ITEM} template (for "generated" item
     * models).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#LAYER0} -> {@code texLoc}</li>
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
}
