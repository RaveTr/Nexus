package com.mememan.nexus.property_wrapper.base.specialised.recipe;

import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapperBuilder;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link RecipeBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedRecipeBuilder()}.
 *
 * @see DefaultableRecipeBasedPropertyWrapper
 */
public interface DefaultableRecipeBasedPropertyWrapperBuilder<T, SELF extends RecipeBasedPropertyWrapperBuilder<T, SELF, RBPW>, RBPW extends RecipeBasedPropertyWrapper<T, RBPW, SELF>> extends RecipeBasedPropertyWrapperBuilder<T, SELF, RBPW> {

    Optional<SpecializedRecipePropertyWrapperBuilder<T, SELF, RBPW>> getSpecializedRecipeBuilder();

    @Override
    default SELF withRecipe(Function<Consumer<FinishedRecipe>, Consumer<Supplier<T>>> recipeConsumerFunc) {
        getSpecializedRecipeBuilder().ifPresent(specializedRecipeBuilder -> specializedRecipeBuilder.withRecipe(recipeConsumerFunc));
        return self();
    }

    @Override
    default SELF copyFrom(RBPW propertyWrapper) {
        getSpecializedRecipeBuilder().ifPresent(builder -> builder.copyFrom(propertyWrapper));
        return self();
    }
}
