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
     * Creates a {@link BlockModelDefinition} with the {@link ModelTemplates#CUBE_ALL} template.
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
        return new BlockModelDefinition(ModelTemplates.CUBE_ALL)
                .withTextureMapping(TextureMapping.cube(RegistryUtil.pickBlockPrefix(RegistryUtil.getBlockTextureLocationOrDefault(ownerBlockSup))))
                .withOrdinalModelDefinition(new ItemModelDefinition(fromLocation(ModelLocationUtils.getModelLocation(ownerBlockSup.get()))));
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
     * @see #cubeBottomTop(ResourceLocation)
     * @see #cubeBottomTop(ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #cubeBottomTop(ResourceLocation, ResourceLocation, ResourceLocation)
     * @see #cross(ResourceLocation)
     * @see #crossCutout(ResourceLocation)
     * @see #sign(ResourceLocation, ResourceLocation)
     */
    public static BlockStateDefinition simpleBlock(Supplier<Block> targetBlock) {
        return new BlockStateDefinition(targetBlock)
                .withBlockStateSupplier(MultiVariantGenerator.multiVariant(targetBlock.get(), Variant.variant()
                        .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(targetBlock.get()))));
    }

    public static ModelTemplate fromLocation(ResourceLocation parentModelLoc) {
        return new ModelTemplate(Optional.of(parentModelLoc), Optional.empty());
    }
}
