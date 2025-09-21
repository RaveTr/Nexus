package com.mememan.nexus.util;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.client.model.block.BlockModelDefinition;
import com.mememan.nexus.client.model.item.ItemModelDefinition;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

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
     * <h3>Required Texture Slots</h3>
     * <ul>
     *  <li>{@link TextureSlot#ALL} -> {@code blockTexLoc}</li>
     * </ul>
     *
     * @param ownerBlockSup The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                      automatic model location resolution.
     * @param blockTexLoc The {@link ResourceLocation} pointing towards the block texture for the block model.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template.
     *
     * @see #simpleBlock(Supplier)
     */
    public static <B extends Block> BlockModelDefinition cubeAll(Supplier<B> ownerBlockSup, ResourceLocation blockTexLoc) {
        return new BlockModelDefinition(ModelTemplates.CUBE_ALL)
                .withTextureMapping(TextureMapping.cube(RegistryUtil.pickBlockPrefix(blockTexLoc)))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(ownerBlockSup.get()))));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template and prompty generates
     * an {@link ItemModelDefinition} using the {@code ownerBlockSup} to grab the prompt model location.
     * <p>
     * <h3>Required Texture Slots</h3>
     * <ul>
     *  <li>{@link TextureSlot#ALL} -> {@code RegistryUtil.getBlockTextureLocationOrDefault(ownerBlockSup)}</li>
     * </ul>
     *
     * @param ownerBlockSup The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                      automatic texture location resolution (as shown above).
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template.
     *
     * @see #simpleBlock(Supplier)
     */
    public static <B extends Block> BlockModelDefinition cubeAll(Supplier<B> ownerBlockSup) {
        return cubeAll(ownerBlockSup, RegistryUtil.getBlockTextureLocationOrDefault(ownerBlockSup));
    }

    /**
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_BOTTOM_TOP} template.
     * <p>
     * <h3>Required Texture Slots</h3>
     * <ul>
     *  <li>{@link TextureSlot#SIDE} -> {@code sideTexture}</li>
     *  <li>{@link TextureSlot#BOTTOM} -> {@code bottomTexture}</li>
     *  <li>{@link TextureSlot#TOP} -> {@code topTexture}</li>
     * </ul>
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
     * @see #simpleBlock(Supplier)
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
     * <h3>Required Texture Slots</h3>
     * <ul>
     *  <li>{@link TextureSlot#SIDE} -> {@code RegistryUtil.getBlockTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_side")}</li>
     *  <li>{@link TextureSlot#BOTTOM} -> {@code RegistryUtil.getBlockTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_bottom")}</li>
     *  <li>{@link TextureSlot#TOP} -> {@code RegistryUtil.getBlockTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_top")}</li>
     * </ul>
     *
     * @param ownerBlockSup The {@code Supplier<Block>} representing the owner {@link Block} to be used for
     *                      automatic model and texture location resolution.
     *
     * @return A {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_BOTTOM_TOP} template.
     *
     * @see #simpleBlock(Supplier)
     * @see #cubeBottomTop(Supplier, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockModelDefinition cubeBottomTop(Supplier<Block> ownerBlockSup) {
        return cubeBottomTop(
                ownerBlockSup,
                RegistryUtil.getBlockTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_side"),
                RegistryUtil.getBlockTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_bottom"),
                RegistryUtil.getBlockTextureLocationOrDefaultWithSuffix(ownerBlockSup, "_top")
        );
    }

    /**
     * Creates a {@link BlockStateDefinition}, using {@link MultiVariantGenerator} with the {@link VariantProperties#MODEL}
     * property set to the supplied {@linkplain Block Block's} default model location.
     * <p>
     * <h3>Variants</h3>
     * <ul>
     *  <li>{@link VariantProperties#MODEL} -> {@link ModelLocationUtils#getModelLocation(Block)}</li>
     * </ul>
     *
     * @param targetBlock The {@linkplain Block Block} to use as the base for the {@link BlockStateDefinition}.
     *
     * @return A new {@link BlockStateDefinition} with a {@code simpleBlock} template.
     *
     * @see #cubeAll(Supplier)
     */
    public static BlockStateDefinition simpleBlock(Supplier<Block> targetBlock) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get(), Variant.variant()
                        .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }
}
