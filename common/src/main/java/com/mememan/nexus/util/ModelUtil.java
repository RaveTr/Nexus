package com.mememan.nexus.util;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.client.model.block.BlockModelDefinition;
import com.mememan.nexus.client.model.item.ItemModelDefinition;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.apache.commons.lang3.StringUtils;

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
     *         <li>{@link TextureSlot#ALL} -> {@code blockTexLoc}</li>
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
     *         <li>{@link TextureSlot#SIDE} -> {@code sideTexture}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code bottomTexture}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code topTexture}</li>
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
     *         <li>{@link TextureSlot#BOTTOM} -> {@code bottomTexture}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code topTexture}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code sideTexture}</li>
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
     *         <li>{@link TextureSlot#BOTTOM} -> {@code bottomTexture}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code topTexture}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code sideTexture}</li>
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
     *         <li>{@link TextureSlot#BOTTOM} -> {@code bottomTexture}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code topTexture}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code sideTexture}</li>
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
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(chosenDoubleBlockId)))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(chosenDoubleBlockId)))}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(chosenDoubleBlockId)))}</li>
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
        ResourceLocation baseSlabId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String baseSlabPath = baseSlabId.getPath();
        String targetDoubleBlockId = StringUtils.substringBefore(baseSlabId.getPath(), "_slab");
        String chosenDoubleBlockId = baseSlabPath.contains("_brick_")
                ? targetDoubleBlockId.concat("_bricks")
                : baseSlabPath.endsWith("_block")
                ? targetDoubleBlockId
                : targetDoubleBlockId.concat("_block");

        return slab(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(chosenDoubleBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(chosenDoubleBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(chosenDoubleBlockId))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with both {@link ModelTemplates#SLAB_BOTTOM} and {@link ModelTemplates#SLAB_TOP}
     * templates, specifically designed for wooden slabs. Automatically determines the slab textures to use the
     * corresponding "_planks" variant from the slab's registry ID.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(targetDoubleBlockId)))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(targetDoubleBlockId)))}</li>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(targetDoubleBlockId)))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wooden slab {@link Block} to be used for
     *                    texture lookup and model creation.
     *
     * @return A new {@link BlockModelDefinition} with slab templates using the "_planks" texture variant.
     *
     * @see #slab(Supplier)
     * @see #woodenSlabBlockState(Supplier)
     */
    public static BlockModelDefinition woodenSlab(Supplier<Block> targetBlock) {
        ResourceLocation baseSlabId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String targetDoubleBlockId = StringUtils.substringBefore(baseSlabId.getPath(), "_slab").concat("_planks");

        return slab(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(targetDoubleBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(targetDoubleBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseSlabId.withPath(targetDoubleBlockId))));
    }

    /**
     * Creates a {@link BlockStateDefinition} for a slab block using {@link MultiVariantGenerator} with different models
     * for each {@link SlabType}.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link SlabType#BOTTOM} -> {@code bottomModel}</li>
     *         <li>{@link SlabType#TOP} -> {@code topModel}</li>
     *         <li>{@link SlabType#DOUBLE} -> {@code doubleSlabModel}</li>
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
     * @see #woodenSlabBlockState(Supplier)
     */
    public static BlockStateDefinition slabBlockState(Supplier<Block> targetBlock, ResourceLocation bottomModel, ResourceLocation topModel, ResourceLocation doubleSlabModel) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get())
                        .with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE)
                                .select(SlabType.BOTTOM, Variant.variant()
                                        .with(VariantProperties.MODEL, bottomModel))
                                .select(SlabType.TOP, Variant.variant()
                                        .with(VariantProperties.MODEL, topModel))
                                .select(SlabType.DOUBLE, Variant.variant()
                                        .with(VariantProperties.MODEL, doubleSlabModel))));
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
     * @see #woodenSlabBlockState(Supplier)
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
     *         <li>{@link SlabType#DOUBLE} -> {@code chosenDoubleBlockModelPath} (derived from slab registry ID)</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the slab {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with slab-specific variants.
     *
     * @see #slabBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabBlockState(Supplier, ResourceLocation)
     * @see #woodenSlabBlockState(Supplier)
     */
    public static BlockStateDefinition slabBlockState(Supplier<Block> targetBlock) {
        ResourceLocation baseSlabId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String baseSlabPath = baseSlabId.getPath();
        String targetDoubleBlockModelPath = StringUtils.substringBefore(ModelLocationUtils.getModelLocation(targetBlock.get()).getPath(), "_slab");
        String chosenDoubleBlockModelPath = baseSlabPath.contains("_brick_")
                ? targetDoubleBlockModelPath.concat("_bricks")
                : baseSlabPath.endsWith("_block")
                ? targetDoubleBlockModelPath
                : targetDoubleBlockModelPath.concat("_block");

        return slabBlockState(targetBlock, baseSlabId.withPath(chosenDoubleBlockModelPath));
    }

    /**
     * Overloaded variant of {@link #slabBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)}.
     * Creates a {@link BlockStateDefinition} for a wooden slab block using {@link MultiVariantGenerator} with different models
     * for each {@link SlabType}. Automatically determines the double slab model to use the "_planks" variant.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link SlabType#BOTTOM} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get())}</li>
     *         <li>{@link SlabType#TOP} -> {@code ModelLocationUtils.getModelLocation(targetBlock.get(), "_top")}</li>
     *         <li>{@link SlabType#DOUBLE} -> {@code targetDoubleBlockModelPath + "_planks"} (derived from slab registry ID)</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wooden slab {@link Block} to create the blockstate for.
     *
     * @return A {@link BlockStateDefinition} with slab-specific variants using the "_planks" double slab model.
     *
     * @see #slabBlockState(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #slabBlockState(Supplier, ResourceLocation)
     * @see #slabBlockState(Supplier)
     */
    public static BlockStateDefinition woodenSlabBlockState(Supplier<Block> targetBlock) {
        ResourceLocation baseSlabId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String targetDoubleBlockModelPath = StringUtils.substringBefore(ModelLocationUtils.getModelLocation(targetBlock.get()).getPath(), "_slab").concat(baseSlabId.getPath().contains("_plank_") ? "s" : "_planks");

        return slabBlockState(targetBlock, baseSlabId.withPath(targetDoubleBlockModelPath));
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
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
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
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String baseStairsPath = baseStairsId.getPath();
        String targetBlockId = StringUtils.substringBefore(baseStairsId.getPath(), "_stairs");
        String chosenBlockId = baseStairsPath.contains("_brick_")
                ? targetBlockId.concat("_bricks")
                : targetBlockId.endsWith("_block")
                ? targetBlockId
                : targetBlockId.concat("_block");

        return stairsStraight(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for the inner
     * corner variant. Automatically determines the stairs textures based on the stairs' registry ID ({@code chosenBlockId}).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
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
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
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
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String baseStairsPath = baseStairsId.getPath();
        String targetBlockId = StringUtils.substringBefore(baseStairsId.getPath(), "_stairs");
        String chosenBlockId = baseStairsPath.contains("_brick_")
                ? targetBlockId.concat("_bricks")
                : targetBlockId.endsWith("_block")
                ? targetBlockId
                : targetBlockId.concat("_block");

        return stairsInner(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#STAIRS_STRAIGHT} template for the outer
     * corner variant. Automatically determines the stairs textures based on the stairs' registry ID ({@code chosenBlockId}).
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
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
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
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
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String baseStairsPath = baseStairsId.getPath();
        String targetBlockId = StringUtils.substringBefore(baseStairsId.getPath(), "_stairs");
        String chosenBlockId = baseStairsPath.contains("_brick_")
                ? targetBlockId.concat("_bricks")
                : targetBlockId.endsWith("_block")
                ? targetBlockId
                : targetBlockId.concat("_block");

        return stairsOuter(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))));
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
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(chosenBlockId))}</li>
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
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String baseStairsPath = baseStairsId.getPath();
        String targetBlockId = StringUtils.substringBefore(baseStairsId.getPath(), "_stairs");
        String chosenBlockId = baseStairsPath.contains("_brick_")
                ? targetBlockId.concat("_bricks")
                : targetBlockId.endsWith("_block")
                ? targetBlockId
                : targetBlockId.concat("_block");

        return stairs(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(chosenBlockId))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with all three stairs variants (straight, inner, outer) specifically
     * designed for wooden stairs. Uses the {@link ModelTemplates#STAIRS_STRAIGHT} template with the "_planks"
     * texture variant. The straight variant includes a corresponding item model.
     * <p>
     *     <h3>Required Texture Slots</h3>
     *     <ul>
     *         <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(targetBlockId))}</li>
     *         <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(targetBlockId))}</li>
     *         <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getTextureLocationOrDefault(targetBlock, baseStairsId.withPath(targetBlockId))}</li>
     *     </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the wooden stairs {@link Block} to be used for
     *                    texture lookup and model creation.
     *
     * @return A new {@link BlockModelDefinition} with all wooden stairs variants.
     *
     * @see #stairs(Supplier)
     */
    public static BlockModelDefinition woodenStairs(Supplier<Block> targetBlock) {
        ResourceLocation baseStairsId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetBlock.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for Block of type %s: %s", targetBlock.getClass().getSimpleName(), targetBlock)));
        String targetBlockId = StringUtils.substringBefore(baseStairsId.getPath(), "_stairs").concat(baseStairsId.getPath().contains("_plank_") ? "s" : "_planks");

        return stairs(targetBlock, RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(targetBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(targetBlockId))), RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(baseStairsId.withPath(targetBlockId))));
    }

    /**
     * Creates a {@link BlockStateDefinition} for stairs blocks using {@link MultiVariantGenerator} with different models
     * for each combination of facing, half, and shape.
     * <p>
     *     <h3>Variants</h3>
     *     <ul>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#STRAIGHT} -> {@code straightStairsModel}</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#INNER_LEFT} or {@link StairsShape#INNER_RIGHT} -> {@code innerStairsModel}</li>
     *         <li>{@link BlockStateProperties#HORIZONTAL_FACING} + {@link BlockStateProperties#HALF}
     *         + {@link StairsShape#OUTER_LEFT} or {@link StairsShape#OUTER_RIGHT} -> {@code outerStairsModel}</li>
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
                                        .with(VariantProperties.MODEL, straightStairsModel))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, straightStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, straightStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, straightStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel))
                                .select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, straightStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, straightStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, straightStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, straightStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, outerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.UV_LOCK, true))
                                .select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant()
                                        .with(VariantProperties.MODEL, innerStairsModel)
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
