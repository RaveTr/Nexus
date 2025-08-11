package com.mememan.nexus.block.data;

import com.mememan.nexus.item.data.ItemModelDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A wrapper-builder {@code class} primarily used to hold and group block model data for more convenient model
 * definitions.
 */
public class BlockModelDefinition {
    @NotNull
    private final ModelTemplate parentModel;
    private boolean ambientOcclusion = true;
    @Nullable
    private ResourceLocation blockRenderType;
    @Nullable
    private TextureMapping blockModelTextureMapping;
    @Nullable
    private String customModelName;
    @Nullable
    private ItemModelDefinition customItemModel;
    @Nullable
    private BlockModel.GuiLight blockModelGuiLight;

    private BlockModelDefinition(ModelTemplate parentModel) {
        this.parentModel = parentModel;
    }

    /**
     * Creates a new {@link BlockModelDefinition} with the given {@link ModelTemplate} as its parent model.
     *
     * @param parentModel The parent {@link ModelTemplate} from which base model data will be serialized during datagen.
     *
     * @return A new {@link BlockModelDefinition} with the given {@link ModelTemplate} as its parent model.
     *
     * @apiNote Do keep in mind that depending on the template you choose, you may need to define additional properties
     * (e.g. Additional mapped {@linkplain TextureSlot TextureSlots} via {@link #withTextureMapping(TextureMapping)}).
     */
    public static BlockModelDefinition of(ModelTemplate parentModel) {
        return new BlockModelDefinition(parentModel);
    }

    /**
     * Whether the resulting block model should exude ambient occlusion. Defaults to {@code true}.
     *
     * @param ambientOcclusion Whether the resulting block model should exude ambient occlusion.
     *
     * @return {@code this} (builder method)
     */
    public BlockModelDefinition withAmbientOcclusion(boolean ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
        return this;
    }

    /**
     * A {@link ResourceLocation} representing existing render types by which the parent {@link Block} will be rendered.
     * <p></p>
     * <b>List of built-in block-specific render types:</b>
     * <ul>
     *  <li>{@code minecraft:solid} -> Default, all solid/fully opaque pixels in block textures.</li>
     *  <li>{@code minecraft:cutout} -> For fully solid or fully transparent pixels in block textures.
     *  No translucency.</li>
     *  <li>{@code minecraft:cutout_mipped} -> Same as above, but applies mipmapping (basically LODs). Does not apply
     *  to block item model.</li>
     *  <li>{@code minecraft:cutout_mipped_all} -> Same as above, but applies to block item model.</li>
     *  <li>{@code minecraft:translucent} -> For blocks with translucent (I.E. Not fully opaque or fully transparent)
     *  pixels in block textures.</li>
     *  <li>{@code minecraft:tripwire} -> Refers to the specific shader that renders tripwires.</li>
     * </ul>
     *
     * You can also find these out by heading to the {@link RenderType} {@code class} and looking for {@code static}
     * methods that don't take any parameters.
     *
     * @param blockRenderType The target {@linkplain RenderType render type's} {@link ResourceLocation}
     *                        (e.g. {@code "minecraft:cutout"}).
     *
     * @return {@code this} (builder method)
     *
     * @apiNote Nexus API writes whatever the chosen render type here is (if any) to the block's model JSON(s) and
     * handles actually applying said render type accordingly. Forge automatically reads and applies the render type
     * from the model JSON file, whereas Nexus API handles implementing similar functionality on Fabric.
     *
     * @see RenderType
     */
    public BlockModelDefinition withBlockRenderType(ResourceLocation blockRenderType) {
        this.blockRenderType = blockRenderType;
        return this;
    }

    /**
     * Sets the texture map, conforming to the parent {@linkplain ModelTemplate ModelTemplate's} required
     * {@linkplain TextureSlot TextureSlot(s)}. Any additional slots that have nothing to do with the parent
     * {@linkplain ModelTemplate ModelTemplate's} required {@linkplain TextureSlot TextureSlot(s)} will be ignored.
     * However, any missing slot mappings (as per the parent {@linkplain ModelTemplate ModelTemplate's} required
     * {@linkplain TextureSlot TextureSlot(s)}) will end up throwing an {@link IllegalStateException}.
     *
     * @param blockModelTextureMapping The object representation of
     *                                 texture {@linkplain ResourceLocation ResourceLocation(s)}
     *                                 to the required {@linkplain TextureSlot TextureSlot(s)}.
     *
     * @return {@code this} (builder method)
     *
     * @see ModelTemplate#requiredSlots
     * @see TexturedModel
     * @see ModelTemplates
     * @see TextureSlot
     */
    public BlockModelDefinition withTextureMapping(TextureMapping blockModelTextureMapping) {
        this.blockModelTextureMapping = blockModelTextureMapping;
        return this;
    }

    /**
     * Defines a custom {@link ItemModelDefinition} for the parent {@linkplain Block Block's} generated item model
     * (the one in {@code "model/item"}). Defaults to the parent {@linkplain Block Block's} block model location. Can
     * be used to override the default generated block item model.
     *
     * @param customItemModelDef The custom {@link ItemModelDefinition} for the parent {@linkplain Block Block's}
     *                           {@linkplain ItemModelDefinition item model}.
     *
     * @return {@code this} (builder method)
     *
     * @see ItemModelDefinition
     */
    public BlockModelDefinition withCustomItemModel(ItemModelDefinition customItemModelDef) {
        this.customItemModel = customItemModelDef;
        return this;
    }

    /**
     * Defines a custom model name with which the generated block model file based on this definition will be named.
     * <br></br>
     * By default, model files generated through BMDs are named based on the parent {@linkplain Block Block's} registry
     * name.
     *
     * @param customModelName The custom model name.
     *
     * @return {@code this} (builder method)
     */
    public BlockModelDefinition withCustomModelName(String customModelName) {
        this.customModelName = customModelName;
        return this;
    }

    /**
     * Defines a custom {@link BlockModel.GuiLight} to render this block model with. Primarily used for perspective
     * -aware/block models. Contrary to the name of the superclass in which GuiLight is nested, this is applicable to
     * item models, too.
     *
     * @param targetGuiLight The {@link BlockModel.GuiLight} to render this block model with. May be {@code null}.
     *
     * @return {@code this} (builder method).
     */
    public BlockModelDefinition withGuiLight(BlockModel.GuiLight targetGuiLight) {
        this.blockModelGuiLight = targetGuiLight;
        return this;
    }

    /**
     * Gets the parent {@link ModelTemplate} from which base model data will be serialized during datagen.
     *
     * @return The parent {@link ModelTemplate}.
     *
     * @see ModelTemplates
     */
    public @NotNull ModelTemplate getParentModel() {
        return parentModel;
    }

    /**
     * Whether the resulting block model exudes ambient occlusion.
     *
     * @return Whether the resulting block model exudes ambient occlusion.
     */
    public boolean hasAmbientOcclusion() {
        return ambientOcclusion;
    }

    /**
     * A mapped representation of model transformations applied to the parent {@linkplain Block Block's} item model
     * based on its key {@link ItemDisplayContext}. May be empty if the parent {@link ItemModelDefinition} is
     * {@code null} or its transforms are empty.
     *
     * @return The mapped item model transformations.
     *
     * @see ItemTransforms
     */
    public Map<ItemDisplayContext, ItemTransform> getItemModelTransforms() {
        return customItemModel != null ? customItemModel.getItemModelTransforms() : Map.of();
    }

    /**
     * A {@link ResourceLocation} representing existing render types by which the parent {@link Block} will be rendered.
     *
     * @return The target render type's {@link ResourceLocation} (E.G. {@code "minecraft:cutout"}).
     *
     * @see RenderType
     */
    @Nullable
    public ResourceLocation getBlockModelRenderType() {
        return blockRenderType;
    }

    /**
     * Gets the {@link ItemModelDefinition} associated with this BMD. May be {@code null}.
     *
     * @return The {@link ItemModelDefinition} associated with this BMD, or {@code null} if left undefined.
     */
    @Nullable
    public ItemModelDefinition getCustomItemModel() {
        return customItemModel;
    }

    /**
     * Gets this definition's {@link TextureMapping}. Should conform to the parent
     * {@linkplain ModelTemplate ModelTemplate's} required {@linkplain TextureSlot TextureSlots}.
     *
     * @return This definition's {@link TextureMapping}.
     *
     * @see ModelTemplate#requiredSlots
     * @see TexturedModel
     * @see ModelTemplates
     * @see TextureSlot
     */
    @Nullable
    public TextureMapping getTextureMapping() {
        return blockModelTextureMapping;
    }

    /**
     * Gets the parent {@link ModelTemplate} from which base item model data will be serialized during datagen.
     * Stored in {@link #customItemModel}. May be {@code null} if {@link #customItemModel} is left undefined.
     *
     * @return The parent {@link ModelTemplate}.
     *
     * @see ModelTemplates
     * @see ItemModelDefinition#getParentModel()
     */
    @Nullable
    public ModelTemplate getItemParentModel() {
        return customItemModel != null ? customItemModel.getParentModel() : null;
    }

    /**
     * Gets the {@link ResourceLocation} of the parent {@link Block}'s item model (The one in {@code "model/item"}).
     *
     * @return The {@link ResourceLocation} of the parent {@link Block}'s item model. {@code null} if the
     * {@link #customItemModel} is left undefined.
     */
    @Nullable
    public ResourceLocation getItemParentModelLocation() {
        return customItemModel != null ? customItemModel.getParentModelLocation() : null;
    }

    /**
     * Gets the custom map of {@link TextureMapping} representing the {@code "textures"} array within the parent
     * {@linkplain Block Block's} {@link ItemModelDefinition}.
     *
     * @return The block item model's {@link TextureMapping} definition. {@code null} if {@link #customItemModel} is
     * {@code null} or it's undefined.
     *
     * @see ItemModelDefinition#withTextureMapping(TextureMapping)
     */
    @Nullable
    public TextureMapping getItemModelTextureMapping() {
        return customItemModel != null ? customItemModel.getTextureMapping() : null;
    }

    /**
     * Gets the custom map of texture overrides for the parent {@linkplain Block Block's} {@link ItemModelDefinition}.
     *
     * @return The texture overrides {@link Map}. Empty if left undefined. {@code null} if {@link #customItemModel} is
     * {@code null} or it's undefined.
     *
     * @see ItemModelDefinition#withItemModelTextureOverrides(Map)
     */
    public Map<Map<ResourceLocation, Float>, ResourceLocation> getItemModelTextureOverrides() {
        return customItemModel != null ? customItemModel.getItemModelTextureOverrides() : Map.of();
    }

    /**
     * Returns the custom model name with which the generated block model file based on this definition will be named.
     * May be {@code null} if left undefined.
     *
     * @return The custom model name, or {@code null} if left undefined.
     */
    @Nullable
    public String getCustomModelName() {
        return customModelName;
    }

    /**
     * Gets the {@link BlockModel.GuiLight} with which this block model is rendered. May be {@code null}.
     *
     * @return The stored {@link BlockModel.GuiLight}. May be {@code null}.
     */
    @Nullable
    public BlockModel.GuiLight getBlockModelGuiLight() {
        return blockModelGuiLight;
    }

    /**
     * Gets the {@link ResourceLocation} of the parent model/{@link ModelTemplate}, or {@link ModelTemplates#CUBE} if
     * the parent model is not explicitly defined.
     *
     * @return The {@link ResourceLocation} of the parent model/{@link ModelTemplate} || {@link ModelTemplates#CUBE} if
     * the parent model is not explicitly defined.
     */
    public ResourceLocation getParentModelLocation() {
        return parentModel.model.orElseGet(ModelTemplates.CUBE.model::get);
    }
}
