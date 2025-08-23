package com.mememan.nexus.property_wrapper.impl.specialised.recipe;

import com.mememan.nexus.property_wrapper.base.specialised.recipe.RecipeBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.recipe.RecipeBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpecializedRecipePropertyWrapperBuilder<T, SELF extends RecipeBasedPropertyWrapperBuilder<T, SELF, RBPW>, RBPW extends RecipeBasedPropertyWrapper<T, RBPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, RBPW> implements RecipeBasedPropertyWrapperBuilder<T, SELF, RBPW> {
    protected Optional<Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>>> recipeConsumerFunc = Optional.empty();

    public SpecializedRecipePropertyWrapperBuilder(@NotNull RBPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(RBPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .withRecipe(propertyWrapper.getRecipeConsumer().orElse(null));
    }

    @Override
    public SELF withRecipe(Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>> lootTableBuilderFunc) {
        this.recipeConsumerFunc = Optional.ofNullable(lootTableBuilderFunc);
        return self();
    }
}
