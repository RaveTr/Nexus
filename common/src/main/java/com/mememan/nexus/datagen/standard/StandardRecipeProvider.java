package com.mememan.nexus.datagen.standard;

import com.google.gson.JsonObject;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.recipe.RecipeBasedPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
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
    protected final List<? extends RecipeBasedPropertyWrapper<?, ?, ?>> mappedRecipePWs;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;

    public StandardRecipeProvider(PackOutput targetPackOutput, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetPackOutput);

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedRecipePWs = PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(RecipeBasedPropertyWrapper.class, modId);
    }

    /**
     * Automatically generates all block and item recipes for the specified mod id, if any. Handles {@code null}/missing
     * recipes.
     *
     * @param recipeActionConsumer The recipe action consumer used to serialize recipes.
     *
     * @see #run(CachedOutput)
     *
     * @see #processRecipesGenerically(Consumer)
     */
    @Override
    public void buildRecipes(Consumer<FinishedRecipe> recipeActionConsumer) {
        processRecipesGenerically(recipeActionConsumer);
    }

    /**
     * Helper delegate method to work around Java's generic type testing (especially for wildcards). Actually does all
     * the recipe processing work. Additionally, handles missing recipe entries appropriately.
     *
     * @param recipeActionConsumer The recipe action consumer used to serialize recipes.
     *
     * @param <T> The object type for each recipe-based property wrapper.
     */
    protected <T> void processRecipesGenerically(Consumer<FinishedRecipe> recipeActionConsumer) {
        if (!mappedRecipePWs.isEmpty()) {
            mappedRecipePWs.stream()
                    .map(curPW -> (RecipeBasedPropertyWrapper<T, ?, ?>) curPW)
                    .forEach(curPW -> {
                        Optional<Function<Consumer<FinishedRecipe>, Consumer<Supplier<T>>>> mappedRecipe = curPW.getRecipeConsumer();
                        String objectClassName = curPW.getParentObject().get().getClass().getSimpleName();

                        mappedRecipe.ifPresentOrElse(recipeMapperFunc -> {
                            NexusConstants.LOGGER.debug("[{}] [Generating Recipe for {}]: {}", modId, objectClassName, curPW.getObjectDescriptionId());

                            recipeMapperFunc.apply(recipeActionConsumer).accept(curPW.getParentObject());
                        }, () -> {
                            if (validateAllEntries() || curPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                                throw new NullPointerException(String.format("Missing recipe mapper for %s: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the object itself requires validation through DataGenBasedPropertyWrapper#getProviderTypeRequisites().", objectClassName, curPW.getObjectDescriptionId(), modId));
                            }
                        });
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
