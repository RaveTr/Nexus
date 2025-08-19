package com.mememan.nexus.property_wrapper.base.specialised.recipe;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards recipe building and generation.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link RecipeBasedPropertyWrapperBuilder}.
 *
 * @see RecipeBasedPropertyWrapperBuilder
 */
public interface RecipeBasedPropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> extends DataGenPropertyWrapper<T, SELF, BUILDER> {

    /**
     * Gets the {@code Function<Supplier<T>, Consumer<FinishedRecipe>>} responsible for outputting the
     * {@link FinishedRecipe} {@link Consumer} to be used in data generation.
     *
     * @return The loot table builder function for the parent object. May be empty.
     */
    Optional<Function<Supplier<T>, Consumer<FinishedRecipe>>> getRecipeConsumer();
}
