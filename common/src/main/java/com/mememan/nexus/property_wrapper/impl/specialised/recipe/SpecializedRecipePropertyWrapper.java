package com.mememan.nexus.property_wrapper.impl.specialised.recipe;

import com.mememan.nexus.property_wrapper.base.specialised.recipe.RecipeBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import net.minecraft.data.recipes.FinishedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public Optional<Function<Supplier<T>, Consumer<FinishedRecipe>>> getRecipeConsumer() {
        return Optional.empty();
    }
}
