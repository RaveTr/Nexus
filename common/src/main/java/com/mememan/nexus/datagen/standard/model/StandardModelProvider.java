package com.mememan.nexus.datagen.standard.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.client.model.ModelTransform;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import com.mememan.nexus.util.JsonUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class StandardModelProvider extends ModelProvider implements ModDataProvider {
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final List<ModelBasedPropertyWrapper<?, ?, ?>> mappedModelPWs;
    protected final ObjectOpenHashSet<ResourceLocation> trackedModels = new ObjectOpenHashSet<>();

    public StandardModelProvider(PackOutput targetPackOutput, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetPackOutput);

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedModelPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(ModelBasedPropertyWrapper.class, modId);
    }

    /**
     * The primary method responsible for generating all model serialization tasks and populating the provided
     * {@code serializedModelDefinitions}.
     * <br></br> Actual processing is delegated to {@link #populateModelDefinitions(CachedOutput, List)}, which handles
     * both duplicates (based on the specified {@link #dupeStrat}) and non-nullity enforcement (based on
     * {@link #validateAllEntries}/provider requisites).
     *
     * @param cachedOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return A {@link CompletableFuture} representing the completion of all model serialization tasks.
     */
    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput cachedOutput) {
        ObjectArrayList<CompletableFuture<?>> constructedChunkedModelDefinitions = new ObjectArrayList<>();

        populateModelDefinitions(cachedOutput, constructedChunkedModelDefinitions);

        return CompletableFuture.allOf(constructedChunkedModelDefinitions.toArray(CompletableFuture[]::new));
    }

    /**
     * Helper delegate method responsible for generating all model serialization tasks and populating the provided
     * {@code serializedModelDefinitions}. This method is called by {@link #run(CachedOutput)}, only being separate
     * due to generic type inference.
     *
     * @param cachedOutput The primary {@link CachedOutput}, typically the one passed in through {@link #run(CachedOutput)}.
     * @param serializedModelDefinitions The list of {@linkplain CompletableFuture CompletableFutures} to be
     *                                   populated with model serialization tasks (typically
     *                                   {@link DataProvider#saveStable(CachedOutput, JsonElement, Path)} calls).
     *
     * @param <T> The object type for each model-based property wrapper.
     */
    protected <T> void populateModelDefinitions(CachedOutput cachedOutput, List<CompletableFuture<?>> serializedModelDefinitions) {
        mappedModelPWs.stream()
                .map(curPW -> (ModelBasedPropertyWrapper<T, ?, ?>) curPW)
                .forEach(curPW -> {
                    Supplier<T> parentObj = curPW.getParentObject();
                    String objectClassName = parentObj.get().getClass().getSimpleName();

                    curPW.getModelDefinition().ifPresentOrElse(modelDefMapper -> {
                        ModelBasedPropertyWrapper.ModelDefinition convertedDefinition = modelDefMapper.apply(parentObj);
                        List<ModelBasedPropertyWrapper.ModelDefinition> flattenedModelDefinitions = convertedDefinition.getFlattenedModelDefinitions();
                        String descId = curPW.getObjectDescriptionId();
                        ResourceLocation modelRL = formatModelResourceLocation(descId, convertedDefinition);
                        Runnable serializationAction = () -> serializedModelDefinitions.add(DataProvider.saveStable(cachedOutput, constructModelJson(descId, convertedDefinition), modelPathProvider.json(modelRL)));

                        // Primary model definition
                        handleModelGeneration(modelRL, objectClassName, serializationAction);

                        // Rest of the nested model definitions
                        if (!flattenedModelDefinitions.isEmpty()) {
                            flattenedModelDefinitions.stream()
                                    .map(curDef -> formatModelResourceLocation(descId, curDef))
                                    .forEach(curModelRL -> handleModelGeneration(curModelRL, objectClassName, serializationAction));
                        }
                    }, () -> {
                        if (validateAllEntries() || curPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                            throw new NullPointerException(String.format("Missing model mapper for %s: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the object itself requires validation through DataGenBasedPropertyWrapper#getProviderTypeRequisites().", objectClassName, curPW.getObjectDescriptionId(), modId));
                        }
                    });
                });
    }

    /**
     * Processes the given {@code modelRL} and handles duplicate cases based on {@link #dupeStrat}.
     *
     * @param modelRL The finalized {@link ResourceLocation} of the model being serialized.
     * @param objectClassName The object type for the model being serialized. Only really used for logging purposes.
     * @param serializationAction The action to perform if the model is not a duplicate (or if it is a duplicate and the
     *                            specified {@link #dupeStrat} is {@link DuplicateDataPolicy#OVERRIDE_WARN} or
     *                            {@link DuplicateDataPolicy#OVERRIDE_SILENT}).
     */
    protected void handleModelGeneration(ResourceLocation modelRL, String objectClassName, Runnable serializationAction) {
        NexusConstants.LOGGER.debug("[{}] [Generating {} Model]: {}", getModId(), objectClassName, modelRL);

        if (!trackedModels.add(modelRL)) {
            switch (getDuplicateDataPolicy()) {
                case CRASH -> throw new IllegalStateException(String.format("Attempted to generate duplicate %s model %s (from mod of ID %s), specified DuplicateDataPolicy is CRASH.", objectClassName, modelRL, getModId()));
                case EXCLUDE_WARN -> NexusConstants.LOGGER.warn("Attempted to generate duplicate {} model {} (from mod of ID {}), specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", objectClassName, modelRL, getModId());
                case EXCLUDE_SILENT -> {}
                case OVERRIDE_WARN -> {
                    NexusConstants.LOGGER.warn("Overriding duplicate {} model for {} from mod of ID {}, specified DuplicateDataPolicy is OVERRIDE_WARN.", objectClassName, modelRL, getModId());
                    serializationAction.run();
                }
                case OVERRIDE_SILENT -> serializationAction.run();
            }
        } else serializationAction.run();
    }

    /**
     * Processes and returns the {@link ResourceLocation} in which the specified {@code curPW} should be saved,
     * corresponding to {@link #modelPathProvider}.
     * <br></br>
     * The location generated uses this provider's {@link #modId} as the namespace, then generates a path based on
     * both {@link ModelBasedPropertyWrapper.ModelDefinition#getBackingDirectory()} and
     * {@link ModelBasedPropertyWrapper.ModelDefinition#getCustomModelName()}.
     *
     * @param descId The file name to fall back to if no custom name is present for this model. Typically, the parent
     *               object's description ID.
     * @param convertedDefinition The {@link ModelBasedPropertyWrapper.ModelDefinition} whose name should be used to
     *                            generate the appropriate {@link ResourceLocation}.
     *
     * @return The {@link ResourceLocation} in which the specified {@code curPW} should be saved.
     */
    protected @NotNull ResourceLocation formatModelResourceLocation(String descId, ModelBasedPropertyWrapper.ModelDefinition convertedDefinition) {
        String weaklyFormattedDescId = descId.contains(".") ? descId.substring(descId.lastIndexOf(".") + 1) : descId; //TODO Actually replace this with a stronger backing check/formatting method (this will suffice for now, though)

        return new ResourceLocation(modId, convertedDefinition.getBackingDirectory().orElse("").concat(convertedDefinition.getCustomModelName().orElse(weaklyFormattedDescId)));
    }

    /**
     * Leniently serializes the {@code modelDefinition} passed in into a {@link JsonObject} ready for serialization.
     * Child definitions should recursively call this method separately.
     *
     * @param modelDefinition The {@link ModelBasedPropertyWrapper.ModelDefinition} to serialize.
     *
     * @return The {@link JsonObject} ready for serialization.
     */
    protected JsonObject constructModelJson(String defaultedModelDefName, @NotNull ModelBasedPropertyWrapper.ModelDefinition modelDefinition) {
        AtomicReference<JsonObject> modelJson = new AtomicReference<>();

        ModelTemplate parentModel = modelDefinition.getParentModel();

        parentModel.create(
                formatModelResourceLocation(defaultedModelDefName, modelDefinition),
                modelDefinition.getTextureMapping(),
                (finalizedModelLoc, modelJsonFileSup) -> {
                    JsonObject baseSerializedModelJson = modelJsonFileSup.get().getAsJsonObject(); // Should always be a JsonObject based on ModelTemplate#createBaseTemplate

                    boolean hasAO = modelDefinition.hasAmbientOcclusion();

                    if (!hasAO) baseSerializedModelJson.addProperty("ambientocclusion", hasAO);

                    modelDefinition.getGuiLight().ifPresent(guiLight -> baseSerializedModelJson.addProperty("gui_light", guiLight.getSerializedName()));
                    modelDefinition.getRenderType().ifPresent(renderType -> baseSerializedModelJson.addProperty("render_type", renderType.toString()));

                    Map<ItemDisplayContext, ModelTransform> modelTransforms = modelDefinition.getModelTransforms();

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

                    modelJson.set(baseSerializedModelJson);
                }
        );

        return modelJson.get();
    }

    @Override
    public @NotNull String getName() {
        return String.format("Models [%s]", getModId());
    }

    @Override
    public @NotNull String getModId() {
        return modId;
    }

    @Override
    public boolean validateAllEntries() {
        return validateAllEntries;
    }

    @Override
    public @NotNull ProviderType getProviderType() {
        return NexusProviderTypes.MODEL_PROVIDER;
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }
}
