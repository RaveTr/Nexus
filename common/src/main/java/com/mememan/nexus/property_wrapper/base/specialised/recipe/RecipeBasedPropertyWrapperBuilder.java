package com.mememan.nexus.property_wrapper.base.specialised.recipe;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapperBuilder} with builder methods towards object loot tables.
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link RecipeBasedPropertyWrapper}.
 *
 * @see RecipeBasedPropertyWrapper
 */
public interface RecipeBasedPropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, RBPW>, RBPW extends RecipeBasedPropertyWrapper<T, RBPW, SELF>> extends DataGenPropertyWrapperBuilder<T, SELF, RBPW> {

    /**
     * Specifies the recipe consumer to be used in data generation, using the parent object as an input.
     *
     * @param recipeConsumerFunc The mapping {@code Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>>} used
     *                           to build the parent object's recipe in datagen.
     *
     * @return {@link #self()} (builder method).
     */
    SELF withRecipe(Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>> recipeConsumerFunc);
}
