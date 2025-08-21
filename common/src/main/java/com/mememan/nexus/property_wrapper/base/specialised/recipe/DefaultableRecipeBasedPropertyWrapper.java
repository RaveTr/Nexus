package com.mememan.nexus.property_wrapper.base.specialised.recipe;

import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapper;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link RecipeBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link RecipeBasedPropertyWrapperBuilder}) using {@link #getSpecializedRecipeWrapper()}.
 *
 * @see DefaultableRecipeBasedPropertyWrapperBuilder
 */
public interface DefaultableRecipeBasedPropertyWrapper<T, SELF extends RecipeBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends RecipeBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends RecipeBasedPropertyWrapper<T, SELF, BUILDER> {

    Optional<SpecializedRecipePropertyWrapper<T, ?, ?>> getSpecializedRecipeWrapper();

    @Override
    default Optional<Function<Supplier<T>, Consumer<FinishedRecipe>>> getRecipeConsumer() {
        return getSpecializedRecipeWrapper().flatMap(SpecializedRecipePropertyWrapper::getRecipeConsumer);
    }
}
