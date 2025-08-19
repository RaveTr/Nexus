package com.mememan.nexus.property_wrapper.base.specialised.recipe;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapperBuilder;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link RecipeBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link RecipeBasedPropertyWrapperBuilder}) using {@link #getSpecializedWrapper()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableRecipeBasedPropertyWrapper<T, SELF extends RecipeBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedRecipePropertyWrapperBuilder<T, BUILDER, SELF>> extends RecipeBasedPropertyWrapper<T, SELF, BUILDER>, DefaultablePropertyWrapper<T, SELF, BUILDER, SpecializedRecipePropertyWrapper<T, SELF, BUILDER>> {

    @Override
    default Optional<Function<Supplier<T>, Consumer<FinishedRecipe>>> getRecipeConsumer() {
        return getSpecializedWrapper().flatMap(SpecializedRecipePropertyWrapper::getRecipeConsumer);
    }
}
