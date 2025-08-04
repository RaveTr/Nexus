package com.mememan.nexus.datagen.standard;

import com.google.gson.JsonObject;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.item.standard.ItemPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Standard loader-agnostic mod-specific recipe provider in Nexus API. Instanced based on the provided mod ID. Handles
 * block and item recipes of all types.
 */
public class StandardRecipeProvider extends RecipeProvider implements ModDataProvider {
    protected final String modId;
    protected final Object2ObjectOpenHashMap<Supplier<Block>, BlockPropertyWrapper> mappedModBPWs;
    protected final Object2ObjectOpenHashMap<Supplier<Item>, ItemPropertyWrapper> mappedModIPWs;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;

    public StandardRecipeProvider(PackOutput targetPackOutput, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetPackOutput);

        this.modId = modId;

        this.mappedModBPWs = BlockPropertyWrapper.getMappedBpws().entrySet()
                .stream()
                .filter(curEntry -> BuiltInRegistries.BLOCK.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen())
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
        this.mappedModIPWs = ItemPropertyWrapper.getMappedIpws().entrySet()
                .stream()
                .filter(curEntry -> BuiltInRegistries.ITEM.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen())
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);

        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;
    }

    /**
     * Automatically generates all block and item recipes for the specified mod id, if any. Handles {@code null}/missing
     * recipes.
     *
     * @param recipeActionConsumer The recipe action consumer used to serialize recipes.
     *
     * @see #run(CachedOutput)
     */
    @Override
    public void buildRecipes(Consumer<FinishedRecipe> recipeActionConsumer) {
        if (!mappedModBPWs.isEmpty()) {
            mappedModBPWs.forEach((blockSupEntry, bpwEntry) -> {
                Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>> mappedRecipe = bpwEntry.getRecipeMappingFunction();

                if (mappedRecipe != null) {
                    NexusConstants.LOGGER.debug("[{}] [Generating Block Recipe]: {}", modId, blockSupEntry.get().getDescriptionId());

                    mappedRecipe.apply(recipeActionConsumer).accept(blockSupEntry);
                } else if (validateAllEntries() || bpwEntry.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) throw new NullPointerException(String.format("Missing recipe for block: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the block itself requires validation through BlockPropertyWrapper#getProviderTypeRequisites().", blockSupEntry.get().getDescriptionId(), modId));
            });
        }

        if (!mappedModIPWs.isEmpty()) {
            mappedModIPWs.forEach((itemSupEntry, ipwEntry) -> {
                Function<Consumer<FinishedRecipe>, Consumer<Supplier<Item>>> mappedRecipe = ipwEntry.getRecipeMappingFunction();

                if (mappedRecipe != null) {
                    NexusConstants.LOGGER.debug("[{}] [Generating Item Recipe]: {}", modId, itemSupEntry.get().getDescriptionId());

                    mappedRecipe.apply(recipeActionConsumer).accept(itemSupEntry);
                } else if (validateAllEntries() || ipwEntry.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) throw new NullPointerException(String.format("Missing recipe for item: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the item itself requires validation through ItemPropertyWrapper#getProviderTypeRequisites().", itemSupEntry.get().getDescriptionId(), modId));
            });
        }
    }

    /**
     * Handles recipe serialization and saving. Also handles duplicate cases based on {@link #getDuplicateDataPolicy()}.
     *
     * @param cachedOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return A {@link CompletableFuture} that completes when the data generation is complete.
     */
    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput cachedOutput) {
        ObjectOpenHashSet<ResourceLocation> recipeLocations = new ObjectOpenHashSet<>();
        ObjectArrayList<CompletableFuture<?>> recipesAndAdvancements = new ObjectArrayList<>();

        buildRecipes((finishedRecipe) -> {
            ResourceLocation finishedRecipeId = finishedRecipe.getId();
            boolean wasAlreadyAdded = !recipeLocations.add(finishedRecipeId);
            Runnable recipeAndAdvancementSerializer = () -> {
                recipesAndAdvancements.add(DataProvider.saveStable(cachedOutput, finishedRecipe.serializeRecipe(), this.recipePathProvider.json(finishedRecipeId)));

                JsonObject serializedRecipeAdvancement = finishedRecipe.serializeAdvancement();

                if (serializedRecipeAdvancement != null) {
                    recipesAndAdvancements.add(DataProvider.saveStable(cachedOutput, serializedRecipeAdvancement, this.advancementPathProvider.json(finishedRecipe.getAdvancementId())));
                }
            };

            if (wasAlreadyAdded) {
                switch (getDuplicateDataPolicy()) {
                    case CRASH -> throw new IllegalStateException(String.format("Attempted to generate duplicate recipe %s (from mod of ID %s), specified DuplicateDataPolicy is CRASH.", finishedRecipeId, getModId()));
                    case EXCLUDE_WARN -> NexusConstants.LOGGER.warn("Attempted to generate duplicate recipe {} (from mod of ID {}), specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", finishedRecipeId, getModId());
                    case EXCLUDE_SILENT -> {}
                    case OVERRIDE_WARN -> {
                        NexusConstants.LOGGER.warn("Overriding duplicate recipe {} (from mod of ID {}), specified DuplicateDataPolicy is OVERRIDE_WARN.", finishedRecipeId, getModId());

                        recipeAndAdvancementSerializer.run();
                    }
                    case OVERRIDE_SILENT -> recipeAndAdvancementSerializer.run();
                }
            } else recipeAndAdvancementSerializer.run();
        });

        return CompletableFuture.allOf(recipesAndAdvancements.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return String.format("%s [%s]", super.getName(), getModId());
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
        return NexusProviderTypes.RECIPE_PROVIDER;
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }
}
