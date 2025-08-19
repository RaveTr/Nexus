package com.mememan.nexus.property_wrapper.base.specialised.recipe;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapperBuilder;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link RecipeBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedBuilder()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableRecipeBasedPropertyWrapperBuilder<T, SELF extends RecipeBasedPropertyWrapperBuilder<T, SELF, RBPW>, RBPW extends RecipeBasedPropertyWrapper<T, RBPW, SELF>> extends RecipeBasedPropertyWrapperBuilder<T, SELF, RBPW>, DefaultablePropertyWrapperBuilder<T, SELF, RBPW, SpecializedRecipePropertyWrapperBuilder<T, SELF, RBPW>> {

    @Override
    default SELF withRecipe(Function<Supplier<T>, Consumer<FinishedRecipe>> recipeConsumerFunc) {
        getSpecializedBuilder().ifPresent(specializedRecipeBuilder -> specializedRecipeBuilder.withRecipe(recipeConsumerFunc));
        return self();
    }
}
