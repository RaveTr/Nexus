package com.mememan.nexus.client.model.general;

import com.mememan.nexus.client.model.block.BlockModelDefinition;
import com.mememan.nexus.client.model.item.ItemModelDefinition;
import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Core implementation of {@link ModelBasedPropertyWrapper.ModelDefinition}.
 * <br></br>
 * Specialised implementations should be used (primarily for backing directories) when it comes to blocks and items, etc.
 *
 * @param <SELF> Self type, primarily used for builder method chaining.
 *
 * @see BlockModelDefinition
 * @see ItemModelDefinition
 */
public class BaseModelDefinition<SELF extends BaseModelDefinition<SELF>> implements ModelBasedPropertyWrapper.ModelDefinition {
    @NotNull
    protected final ModelTemplate parentModel;
    @NotNull
    protected String modelCustomBackingDirectory;
    @Nullable
    protected TextureMapping modelTextureMapping;
    @Nullable
    protected ResourceLocation modelRenderType;
    @Nullable
    protected ModelGuiLight modelGuiLight;
    @Nullable
    protected String modelCustomName;
    protected boolean hasAO = true;
    protected final Map<ItemDisplayContext, ModelTransform> modelTransforms = new Object2ObjectOpenHashMap<>();
    protected final List<ModelElement> modelElements = new ObjectArrayList<>();
    protected final List<ModelBasedPropertyWrapper.ModelDefinition> ordinalModelDefinitions = new ObjectArrayList<>();

    public BaseModelDefinition(@NotNull ModelTemplate parentModel, @NotNull String customBackingDirectory) {
        this.parentModel = parentModel;
        this.modelCustomBackingDirectory = customBackingDirectory;
    }

    @Override
    public SELF withOrdinalModelDefinition(ModelBasedPropertyWrapper.ModelDefinition modelDefinition) {
        this.ordinalModelDefinitions.add(modelDefinition);
        return self();
    }

    @Override
    public SELF withOrdinalModelDefinitions(Collection<ModelBasedPropertyWrapper.ModelDefinition> modelDefinitions) {
        this.ordinalModelDefinitions.addAll(modelDefinitions);
        return self();
    }

    @Override
    public SELF setOrdinalModelDefinitions(Collection<ModelBasedPropertyWrapper.ModelDefinition> modelDefinitions) {
        this.ordinalModelDefinitions.clear();
        this.ordinalModelDefinitions.addAll(modelDefinitions);
        return self();
    }

    @Override
    public SELF withOrdinalModelDefinitions(ModelBasedPropertyWrapper.ModelDefinition... modelDefinition) { // Overridden for convenience when chaining (avoid annoying compile-time errors)
        return withOrdinalModelDefinitions(ObjectArrayList.of(modelDefinition));
    }

    @Override
    public SELF withCustomName(String customName) {
        this.modelCustomName = customName;
        return self();
    }

    @Override
    public SELF appendToBackingDirectory(String appendedDir) {
        this.modelCustomBackingDirectory += appendedDir;
        return self();
    }

    @Override
    public SELF withTextureMapping(TextureMapping texMapping) {
        this.modelTextureMapping = texMapping;
        return self();
    }

    @Override
    public ModelBasedPropertyWrapper.ModelDefinition withRenderType(ResourceLocation renderType) {
        this.modelRenderType = renderType;
        return self();
    }

    @Override
    public SELF withGuiLight(ModelGuiLight guiLight) {
        this.modelGuiLight = guiLight;
        return self();
    }

    @Override
    public SELF withAmbientOcclusion(boolean ambientOcclusion) {
        this.hasAO = ambientOcclusion;
        return self();
    }

    @Override
    public SELF withTransform(ItemDisplayContext perspectiveContext, ModelTransform applicableTransform) {
        this.modelTransforms.put(perspectiveContext, applicableTransform);
        return self();
    }

    @Override
    public SELF withTransforms(Map<ItemDisplayContext, ModelTransform> transforms) {
        this.modelTransforms.putAll(transforms);
        return self();
    }

    @Override
    public SELF setTransforms(Map<ItemDisplayContext, ModelTransform> transforms) {
        this.modelTransforms.clear();
        this.modelTransforms.putAll(transforms);
        return self();
    }

    @Override
    public SELF withElement(ModelElement element) {
        this.modelElements.add(element);
        return self();
    }

    @Override
    public SELF withElements(Collection<ModelElement> elements) {
        this.modelElements.addAll(elements);
        return self();
    }

    @Override
    public SELF withElements(ModelElement... elements) {
        this.modelElements.addAll(new ObjectArrayList<>(elements));
        return self();
    }

    @Override
    public SELF setElements(Collection<ModelElement> elements) {
        this.modelElements.clear();
        this.modelElements.addAll(elements);
        return self();
    }

    @Override
    public @NotNull ModelTemplate getParentModel() {
        return parentModel;
    }

    @Override
    public Optional<TextureMapping> getTextureMapping() {
        return Optional.ofNullable(modelTextureMapping);
    }

    @Override
    public Optional<String> getCustomModelName() {
        return Optional.ofNullable(modelCustomName);
    }

    @Override
    public Optional<String> getBackingDirectory() {
        return Optional.of(modelCustomBackingDirectory);
    }

    @Override
    public Optional<ResourceLocation> getRenderType() {
        return Optional.ofNullable(modelRenderType);
    }

    @Override
    public Optional<ModelGuiLight> getGuiLight() {
        return Optional.ofNullable(modelGuiLight);
    }

    @Override
    public boolean hasAmbientOcclusion() {
        return hasAO;
    }

    @Override
    public Map<ItemDisplayContext, ModelTransform> getModelTransforms() {
        return modelTransforms;
    }

    @Override
    public List<ModelElement> getModelElements() {
        return modelElements;
    }

    @Override
    public List<ModelBasedPropertyWrapper.ModelDefinition> getOrdinalModelDefinitions() {
        return ordinalModelDefinitions;
    }

    /**
     * Generic helper method used to bypass builder method return types.
     *
     * @return {@code (SELF) this}.
     */
    public SELF self() {
        return (SELF) this;
    }
}
