package com.mememan.nexus.property_wrapper.base.specialised.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mememan.nexus.client.model.general.ModelElement;
import com.mememan.nexus.client.model.general.ModelGuiLight;
import com.mememan.nexus.client.model.general.ModelTransform;
import com.mememan.nexus.datagen.standard.resource_pack.StandardModelProvider;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.util.JsonUtil;
import com.mememan.nexus.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards handling models for the object being wrapped.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link ModelBasedPropertyWrapperBuilder}.
 *
 * @see ModelBasedPropertyWrapperBuilder
 */
public interface ModelBasedPropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> extends DataGenPropertyWrapper<T, SELF, BUILDER> {

    /**
     * Gets the {@link ModelDefinition} for the parent block, represented as a {@link Function} taking the parent object
     * as input and returning a {@link ModelDefinition}.
     *
     * @return A model definition for the object being wrapped. May be empty.
     *
     * @see ModelBasedPropertyWrapperBuilder#withModelDefinition(Function)
     */
    Optional<Function<Supplier<T>, ModelDefinition>> getModelDefinition();

    /**
     * Nested data {@code interface} representing models for different types of objects.
     * <br></br>
     * Different implementations for different object types may contain additional data used to properly generate
     * an object type's model (like how blocks should also have item models, thus storing item model definitions inside
     * their own block model definitions).
     */
    interface ModelDefinition {

        /**
         * Defines another model definition to be used after this one as a child/contained definition.
         *
         * @param modelDefinition The model definition to add and apply after this one.
         *
         * @return {@code this} (builder method)
         *
         * @see #withOrdinalModelDefinitions(Collection)
         * @see #withOrdinalModelDefinitions(ModelDefinition...)
         * @see #setOrdinalModelDefinitions(Collection)
         */
        ModelDefinition withOrdinalModelDefinition(ModelDefinition modelDefinition);

        /**
         * Defines a list of model definitions to be used after this one as children/contained definitions.
         *
         * @param modelDefinitions The model definitions to add and apply after this one.
         *
         * @return {@code this} (builder method)
         *
         * @see #withOrdinalModelDefinition(ModelDefinition)
         * @see #withOrdinalModelDefinitions(ModelDefinition...)
         * @see #setOrdinalModelDefinitions(Collection)
         */
        ModelDefinition withOrdinalModelDefinitions(Collection<ModelDefinition> modelDefinitions);

        /**
         * Sets the list of model definitions to be used after this one as children/contained definitions.
         *
         * @param modelDefinitions The model definitions to set.
         *
         * @return {@code this} (builder method)
         *
         * @see #withOrdinalModelDefinitions(Collection)
         * @see #withOrdinalModelDefinitions(ModelDefinition...)
         * @see #withOrdinalModelDefinition(ModelDefinition)
         */
        ModelDefinition setOrdinalModelDefinitions(Collection<ModelDefinition> modelDefinitions);

        /**
         * Overloaded variant of {@link #withOrdinalModelDefinitions(Collection)}. Defines an array of model definitions to be
         * used after this one as children/contained definitions.
         *
         * @param modelDefinition The model definitions to add and apply after this one.
         *
         * @return {@code this} (builder method)
         *
         * @see #withOrdinalModelDefinition(ModelDefinition)
         * @see #withOrdinalModelDefinitions(Collection)
         * @see #setOrdinalModelDefinitions(Collection)
         */
        default ModelDefinition withOrdinalModelDefinitions(ModelDefinition... modelDefinition) {
            return withOrdinalModelDefinitions(ObjectArrayList.of(modelDefinition));
        }

        /**
         * Sets a custom file name to use for this definition rather than defaulting to the owner object's formatted
         * description ID.
         *
         * @param customName The custom file name to use for this model definition.
         *
         * @return {@code this} (builder method)
         */
        ModelDefinition withCustomName(String customName);

        /**
         * Appends an additional path to the {@linkplain #getBackingDirectory() backing directory} for this definition
         * type.
         * <br></br>
         * This method does not typically perform any formatting of its own. As such, the {@code appendedDir} should
         * be formatted like any normal {@link ResourceLocation} (see param list below). The resulting path will
         * look something like {@code "assets/<mod_id>/models/<backing_directory>/<appended_directory>/"}.
         *
         * @param appendedDir The directory to append to the root {@linkplain #getBackingDirectory() backing directory}
         *                    (e.g {@code "some/additional/set/of/paths"}).
         *
         * @return {@code this} (builder method)
         *
         * @see #getBackingDirectory()
         */
        ModelDefinition appendToBackingDirectory(String appendedDir);

        /**
         * Sets the texture map, conforming to the parent {@linkplain ModelTemplate ModelTemplate's} required
         * {@linkplain TextureSlot TextureSlot(s)}. Any additional slots that have nothing to do with the parent
         * {@linkplain ModelTemplate ModelTemplate's} required {@linkplain TextureSlot TextureSlot(s)} will be ignored.
         * However, any missing slot mappings (as per the parent {@linkplain ModelTemplate ModelTemplate's} required
         * {@linkplain TextureSlot TextureSlot(s)}) will end up throwing an {@link IllegalStateException}.
         *
         * @param texMapping The object representation of texture {@linkplain ResourceLocation ResourceLocation(s)} to
         *                   the required {@linkplain TextureSlot TextureSlot(s)}.
         *
         * @return {@code this} (builder method)
         *
         * @see ModelTemplate#requiredSlots
         * @see TexturedModel
         * @see ModelTemplates
         * @see TextureSlot
         */
        ModelDefinition withTextureMapping(TextureMapping texMapping);

        /**
         * Defines a render type to use for this model definition.
         *
         * @param renderType The {@link ResourceLocation} of the target {@link net.minecraft.client.renderer.RenderType}.
         *
         * @return {@code this} (builder method)
         *
         * @see #getRenderType()
         */
        ModelDefinition withRenderType(ResourceLocation renderType);

        /**
         * Defines the GUI lighting to use for this model definition.
         *
         * @param guiLight The {@link ModelGuiLight} to use for this model definition.
         *
         * @return {@code this} (builder method)
         *
         * @see #getGuiLight()
         */
        ModelDefinition withGuiLight(ModelGuiLight guiLight);

        /**
         * Whether the resulting object model should exude ambient occlusion. Defaults to {@code true}.
         *
         * @param ambientOcclusion Whether the resulting block model should exude ambient occlusion.
         *
         * @return {@code this} (builder method)
         */
        ModelDefinition withAmbientOcclusion(boolean ambientOcclusion);

        /**
         * Defines the model transform to apply to this model definition, mapped to the provided {@code perspectiveContext}.
         *
         * @param perspectiveContext The perspective context to map the {@code applicableTransform} to.
         * @param applicableTransform The model transform to apply to this model definition. Will not be applied if
         *                            {@code null} or equivalent to {@link ModelTransform#defaultTransform()}.
         *
         * @return {@code this} (builder method)
         *
         * @see #getModelTransforms()
         * @see #withTransforms(Map)
         * @see #setTransforms(Map)
         */
        ModelDefinition withTransform(ItemDisplayContext perspectiveContext, ModelTransform applicableTransform);

        /**
         * Defines a {@link Map} of different perspective-aware transformations to apply to this model definition.
         *
         * @param transforms The {@link Map} describing a set of {@linkplain ItemDisplayContext ItemDisplayContexts}
         *                   mapped to their respective {@linkplain ModelTransform ModelTransforms}.
         *
         * @return {@code this} (builder method)
         *
         * @see #withTransform(ItemDisplayContext, ModelTransform)
         * @see #setTransforms(Map)
         */
        ModelDefinition withTransforms(Map<ItemDisplayContext, ModelTransform> transforms);

        /**
         * Sets the model transforms to apply to this model definition based on perspective context.
         *
         * @param transforms The {@link Map} describing a set of {@linkplain ItemDisplayContext ItemDisplayContexts}
         *                   mapped to their respective {@linkplain ModelTransform ModelTransforms}.
         *
         * @return {@code this} (builder method)
         *
         * @see #withTransform(ItemDisplayContext, ModelTransform)
         * @see #withTransforms(Map)
         */
        ModelDefinition setTransforms(Map<ItemDisplayContext, ModelTransform> transforms);

        /**
         * Defines a {@link ModelElement} to add to this model definition.
         *
         * @param element The {@link ModelElement} to add.
         *
         * @return {@code this} (builder method)
         *
         * @see #withElements(Collection)
         * @see #withElements(ModelElement...)
         * @see #setElements(Collection)
         */
        ModelDefinition withElement(ModelElement element);

        /**
         * Defines a {@link Iterable} of {@link ModelElement ModelElements} to add to this model definition.
         *
         * @param elements The {@link Iterable} of {@link ModelElement ModelElements} to add.
         *
         * @return {@code this} (builder method)
         *
         * @see #withElement(ModelElement)
         * @see #withElements(ModelElement...)
         * @see #setElements(Collection)
         */
        ModelDefinition withElements(Collection<ModelElement> elements);

        /**
         * Defines an array of {@link ModelElement ModelElements} to add to this model definition.
         *
         * @param elements The array of {@link ModelElement ModelElements} to add to this model definition.
         *
         * @return {@code this} (builder method)
         *
         * @see #withElement(ModelElement)
         * @see #withElements(Collection)
         * @see #setElements(Collection)
         */
        ModelDefinition withElements(ModelElement... elements);

        /**
         * Sets the element data stored in this model definition.
         *
         * @param elements The {@link Iterable} of {@linkplain ModelElement ModelElements} to set the current ones to.
         *
         * @return {@code this} (builder method)
         *
         * @see #withElement(ModelElement)
         * @see #withElements(Collection)
         * @see #withElements(ModelElement...)
         */
        ModelDefinition setElements(Collection<ModelElement> elements);

        /**
         * Gets the parent model to use as a template for this definition's model.
         *
         * @return The parent model.
         */
        @NotNull
        ModelTemplate getParentModel();

        /**
         * Gets the texture mapping to use for this definition's model (i.e. the texture locations mapped to each
         * {@link TextureSlot} required by the parent model).
         *
         * @return The texture mapping to use for this definition's model, influenced by the parent model.
         */
        Optional<TextureMapping> getTextureMapping();

        /**
         * Gets the custom model name to use for this definition's model file name.
         *
         * @return The custom model name. May be empty.
         *
         * @see #getBackingDirectory()
         */
        Optional<String> getCustomModelName();

        /**
         * Gets the backing directory for this definition type.
         * <br></br>
         * By default, during model generation, objects are placed inside of
         * {@code "assets/<mod_id>/models/"}. If this method returns a non-empty value, it will look something like
         * {@code "assets/<mod_id>/models/<backing_directory>/"}.
         * <br></br>
         * This method should be overridden for custom implementations to allow for proper object segregation during
         * model generation.
         *
         * @return The backing directory for this definition type. May be empty.
         *
         * @see #getCustomModelName()
         */
        Optional<String> getBackingDirectory();

        /**
         * Gets the custom {@link net.minecraft.client.renderer.RenderType} to be passed into this model's JSON for
         * custom rendering masks.
         * <br></br>
         * <b>List of built-in render types:</b>
         * <ul>
         *  <li>{@code minecraft:solid} -> Default, all solid/fully opaque pixels in textures.</li>
         *  <li>{@code minecraft:cutout} -> For fully solid, or fully transparent pixels in textures. No translucency.</li>
         *  <li>{@code minecraft:cutout_mipped} -> Same as above, but applies mipmapping (basically LODs). Does not apply to block item model for blocks.</li>
         *  <li>{@code minecraft:cutout_mipped_all} -> Same as above, but applies to block item model for blocks.</li>
         *  <li>{@code minecraft:translucent} -> For textures with translucent (i.e. Not fully opaque nor fully transparent) pixels.</li>
         *  <li>{@code minecraft:tripwire} -> Uses a custom shader to render a tripwire.</li>
         * </ul>
         *
         * @return The custom render type location. May be empty.
         *
         * @see net.minecraft.client.renderer.RenderType
         */
        Optional<ResourceLocation> getRenderType();

        /**
         * Gets the custom GUI lighting to use for this model definition.
         *
         * @return The custom GUI lighting to use for this model definition. May be empty.
         */
        Optional<ModelGuiLight> getGuiLight();

        /**
         * Gets whether this model definition should exude ambient occlusion. Defaults to {@code true}.
         *
         * @return Whether this model definition should exude ambient occlusion.
         */
        boolean hasAmbientOcclusion();

        /**
         * Gets the model transforms to apply to this model definition based on perspective context.
         *
         * @return A {@link Map} of model transforms to apply to this model definition based on perspective context. May
         * be empty.
         *
         * @apiNote This is primarily used for item model transformations, including item-representable models (e.g.
         * block items).
         */
        Map<ItemDisplayContext, ModelTransform> getModelTransforms();

        /**
         * Gets the element data stored in this model definition.
         *
         * @return A {@link List} of all element data stored in this model definition.
         */
        List<ModelElement> getModelElements();

        /**
         * A {@link List} of all contained model definitions within this definition. Does not traverse down contained
         * definitions' children.
         *
         * @return A {@link List} of all contained model definitions within this definition.
         *
         * @see #getFlattenedModelDefinitions()
         */
        List<ModelDefinition> getOrdinalModelDefinitions();

        /**
         * Gets a flattened view of all contained model definitions within this definition and their children, all the
         * way down the logical hierarchy. Prunes duplicates from the resultant {@link List}.
         *
         * @return A flattened list of all contained model definitions.
         *
         * @see #getOrdinalModelDefinitions()
         */
        default List<ModelDefinition> getFlattenedModelDefinitions() {
            if (getOrdinalModelDefinitions().isEmpty()) return ObjectArrayList.of();

            List<ModelDefinition> flattenedDefinitions = new ObjectArrayList<>();
            Queue<ModelDefinition> toProcess = new LinkedList<>(getOrdinalModelDefinitions());
            Set<ModelDefinition> processed = new ObjectOpenHashSet<>();

            while (!toProcess.isEmpty()) { // Recursively flatten definitions all the way down
                ModelDefinition next = toProcess.poll();
                
                if (!processed.add(next)) continue;
                
                flattenedDefinitions.add(next);

                if (!next.getOrdinalModelDefinitions().isEmpty()) {
                    toProcess.addAll(next.getOrdinalModelDefinitions());
                }
            }

            return flattenedDefinitions;
        }

        /**
         * Serializes all data within this model definition into a {@link JsonObject} based on the provided base model
         * JSON. May be overridden to attach or override properties as needed.
         *
         * @param finalizedModelLoc The full {@link ResourceLocation} in which this definition will be saved.
         * @param baseModelJson The base model JSON, typically created by
         *                      {@link ModelTemplate#createBaseTemplate(ResourceLocation, Map)}.
         *
         * @return The full serialized model JSON.
         *
         * @see ModelTemplate#create(ResourceLocation, TextureMapping, BiConsumer)
         * @see StandardModelProvider#constructModelJson(String, ModelDefinition)
         */
        default JsonObject constructJson(ResourceLocation finalizedModelLoc, Supplier<JsonElement> baseModelJson) {
            JsonObject baseSerializedModelJson = baseModelJson.get().getAsJsonObject(); // Should always be a JsonObject based on ModelTemplate#createBaseTemplate

            boolean hasAO = hasAmbientOcclusion();

            if (!hasAO) baseSerializedModelJson.addProperty("ambientocclusion", hasAO);

            getGuiLight().ifPresent(guiLight -> baseSerializedModelJson.addProperty("gui_light", guiLight.getSerializedName()));
            getRenderType().ifPresent(renderType -> baseSerializedModelJson.addProperty("render_type", renderType.toString()));

            Map<ItemDisplayContext, ModelTransform> modelTransforms = getModelTransforms();
            List<ModelElement> modelElements = getModelElements();

            if (!modelTransforms.isEmpty()) {
                JsonObject modelTransformsMap = new JsonObject();

                modelTransforms.forEach((transformName, transform) -> {
                    JsonObject transformJsonObj = new JsonObject();

                    if (transform.hasRotation()) transformJsonObj.add("rotation", JsonUtil.createVec3fArray(transform.rotation()));
                    if (transform.hasTranslation()) transformJsonObj.add("translation", JsonUtil.createVec3fArray(transform.translation()));
                    if (transform.hasScale()) transformJsonObj.add("scale", JsonUtil.createVec3fArray(transform.scale()));

                    modelTransformsMap.add(transformName.getSerializedName(), transformJsonObj);
                });

                baseSerializedModelJson.add("display", modelTransformsMap);
            }

            if (!modelElements.isEmpty()) {
                JsonArray modelElementsArray = new JsonArray();

                modelElements.forEach(curElement -> {
                    JsonObject elementObj = new JsonObject();

                    Vector3f from = curElement.from();
                    Vector3f to = curElement.to();
                    boolean hasShade = curElement.shade();

                    if (from != null) elementObj.add("from", JsonUtil.createVec3fArray(from));
                    if (to != null) elementObj.add("to", JsonUtil.createVec3fArray(to));
                    if (!hasShade) elementObj.addProperty("shade", hasShade);

                    ModelElement.ElementRotationData rotationData = curElement.rotation();
                    Map<Direction, ModelElement.ElementFaceData> elementFaces = curElement.faces();

                    if (rotationData != null) {
                        JsonObject rotationDataObj = new JsonObject();

                        Vector3f origin = rotationData.origin();
                        Direction.Axis axis = rotationData.axis();
                        float angle = rotationData.angle();
                        boolean shouldRescale = rotationData.rescale();

                        if (origin != null) rotationDataObj.add("origin", JsonUtil.createVec3fArray(origin));
                        if (axis != null) rotationDataObj.addProperty("axis", axis.getSerializedName());
                        if (rotationData.hasValidAngle()) rotationDataObj.addProperty("angle", angle);
                        if (shouldRescale) rotationDataObj.addProperty("rescale", shouldRescale);

                        elementObj.add("rotation", rotationDataObj);
                    }

                    if (elementFaces != null && !elementFaces.isEmpty()) {
                        JsonObject facesObj = new JsonObject();

                        elementFaces.forEach((faceDir, faceData) -> {
                            JsonObject faceDataObj = new JsonObject();

                            ModelElement.FaceUVData uvData = faceData.uv();
                            String targetTextureOrKey = faceData.formattedTextureKey();
                            Direction cullFaceDir = faceData.cullFace();

                            if (uvData != null) {
                                Vector2f uvFrom = uvData.from();
                                Vector2f uvTo = uvData.to();

                                if (uvFrom != null && uvTo != null) faceDataObj.add("uv", JsonUtil.flatConcatVec2fArrays(uvFrom, uvTo));
                            }

                            if (targetTextureOrKey != null) faceDataObj.addProperty("texture", ResourceLocationUtil.formatModelUVTexture(targetTextureOrKey));
                            if (cullFaceDir != null) faceDataObj.addProperty("cullface", cullFaceDir.getSerializedName());
                            if (faceData.hasTintIndex()) faceDataObj.addProperty("tintindex", faceData.tintIndex());

                            facesObj.add(faceDir.getSerializedName(), faceDataObj);
                        });

                        elementObj.add("faces", facesObj);
                    }

                    modelElementsArray.add(elementObj);
                });

                baseSerializedModelJson.add("elements", modelElementsArray);
            }

            return baseSerializedModelJson;
        }
    }
}
