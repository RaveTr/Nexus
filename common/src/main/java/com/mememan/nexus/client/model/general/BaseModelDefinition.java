package com.mememan.nexus.client.model.general;

import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Core implementation of {@link ModelBasedPropertyWrapper.ModelDefinition}.
 * <br></br>
 * Specialised implementations should be used (primarily for backing directories) when it comes to blocks and items, etc.
 */
public class BaseModelDefinition implements ModelBasedPropertyWrapper.ModelDefinition {
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
    protected Map<ItemDisplayContext, ModelTransform> modelTransforms = new Object2ObjectOpenHashMap<>();
    protected List<ModelElement> modelElements = new ObjectArrayList<>();
    protected List<ModelBasedPropertyWrapper.ModelDefinition> ordinalModelDefinitions = new ObjectArrayList<>();

    public BaseModelDefinition(@NotNull ModelTemplate parentModel, @NotNull String customBackingDirectory) {
        this.parentModel = parentModel;
        this.modelCustomBackingDirectory = customBackingDirectory;
    }

    @Override
    public BaseModelDefinition withOrdinalModelDefinition(ModelBasedPropertyWrapper.ModelDefinition modelDefinition) {
        this.ordinalModelDefinitions.add(modelDefinition);
        return this;
    }

    @Override
    public BaseModelDefinition withOrdinalModelDefinitions(Collection<ModelBasedPropertyWrapper.ModelDefinition> modelDefinitions) {
        this.ordinalModelDefinitions.addAll(modelDefinitions);
        return this;
    }

    @Override
    public BaseModelDefinition setOrdinalModelDefinitions(Collection<ModelBasedPropertyWrapper.ModelDefinition> modelDefinitions) {
        this.ordinalModelDefinitions = new ObjectArrayList<>(modelDefinitions);
        return this;
    }

    @Override
    public BaseModelDefinition withCustomName(String customName) {
        this.modelCustomName = customName;
        return this;
    }

    @Override
    public BaseModelDefinition appendToBackingDirectory(String appendedDir) {
        this.modelCustomBackingDirectory += appendedDir;
        return this;
    }

    @Override
    public BaseModelDefinition withTextureMapping(TextureMapping texMapping) {
        this.modelTextureMapping = texMapping;
        return this;
    }

    @Override
    public BaseModelDefinition withGuiLight(ModelGuiLight guiLight) {
        this.modelGuiLight = guiLight;
        return this;
    }

    @Override
    public BaseModelDefinition withAmbientOcclusion(boolean ambientOcclusion) {
        this.hasAO = ambientOcclusion;
        return this;
    }

    @Override
    public BaseModelDefinition withTransform(ItemDisplayContext perspectiveContext, ModelTransform applicableTransform) {
        this.modelTransforms.put(perspectiveContext, applicableTransform);
        return this;
    }

    @Override
    public BaseModelDefinition withTransforms(Map<ItemDisplayContext, ModelTransform> transforms) {
        this.modelTransforms.putAll(transforms);
        return this;
    }

    @Override
    public BaseModelDefinition setTransforms(Map<ItemDisplayContext, ModelTransform> transforms) {
        this.modelTransforms = new Object2ObjectOpenHashMap<>(transforms);
        return this;
    }

    @Override
    public BaseModelDefinition withElement(ModelElement element) {
        this.modelElements.add(element);
        return this;
    }

    @Override
    public BaseModelDefinition withElements(Collection<ModelElement> elements) {
        this.modelElements.addAll(elements);
        return this;
    }

    @Override
    public BaseModelDefinition withElements(ModelElement... elements) {
        this.modelElements.addAll(new ObjectArrayList<>(elements));
        return this;
    }

    @Override
    public BaseModelDefinition setElements(Collection<ModelElement> elements) {
        this.modelElements = new ObjectArrayList<>(elements);
        return this;
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
}
