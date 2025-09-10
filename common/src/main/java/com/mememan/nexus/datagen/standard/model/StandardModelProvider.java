package com.mememan.nexus.datagen.standard.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ModelProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class StandardModelProvider<JES extends Supplier<JsonElement>> extends ModelProvider implements ModDataProvider {
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final List<ModelBasedPropertyWrapper<?, ?, ?>> mappedModelPWs;

    public StandardModelProvider(PackOutput targetPackOutput, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetPackOutput);

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedModelPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(ModelBasedPropertyWrapper.class, modId);
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput cachedOutput) {
        ObjectArrayList<CompletableFuture<?>> constructedChunkedModelDefinitions = new ObjectArrayList<>();

        return CompletableFuture.allOf(constructedChunkedModelDefinitions.toArray(CompletableFuture[]::new));
    }

    protected JsonObject constructModelJson(ModelBasedPropertyWrapper.ModelDefinition modelDefinition) {
        JsonObject modelJson = new JsonObject();

        return modelJson;
    }

    @Override
    public @NotNull String getName() {
        return String.format("Models & Blockstates [%s]", getModId());
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
        return NexusProviderTypes
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }
}
