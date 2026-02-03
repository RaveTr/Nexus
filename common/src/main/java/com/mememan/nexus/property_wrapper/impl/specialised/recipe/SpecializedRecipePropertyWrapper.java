package com.mememan.nexus.property_wrapper.impl.specialised.recipe;

import com.mememan.nexus.property_wrapper.base.specialised.recipe.RecipeBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import net.minecraft.data.recipes.FinishedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized implementation of {@link RecipeBasedPropertyWrapper}. Implements all recipe-related getter methods,
 * generic types, and default behaviour for recipe-based property wrapper handling.
 *
 * @see SpecializedRecipePropertyWrapperBuilder
 */
public class SpecializedRecipePropertyWrapper<T, SELF extends RecipeBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedRecipePropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements RecipeBasedPropertyWrapper<T, SELF, BUILDER> {

    public SpecializedRecipePropertyWrapper(Supplier<T> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, SpecializedRecipePropertyWrapperBuilder::new, modId);
    }

    public SpecializedRecipePropertyWrapper(@NotNull Supplier<T> parentObject, @NotNull String modId) {
        super(parentObject, SpecializedRecipePropertyWrapperBuilder::new, modId);
    }

    public SpecializedRecipePropertyWrapper() {
        super(SpecializedRecipePropertyWrapperBuilder::new);
    }

    @Override
    public Optional<Function<Consumer<FinishedRecipe>, Consumer<Supplier<T>>>> getRecipeConsumer() {
        return rawBuilder().map(builder -> builder.recipeConsumerFunc);
    }
}
