package com.mememan.nexus.property_wrapper.impl.generic.misc;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.misc.DefaultableDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseDefaultableDataGenPropertyWrapper<T, SELF extends DefaultableDataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends BaseDefaultableDataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements DefaultableDataGenPropertyWrapper<T, SELF, BUILDER> {
    protected final SpecializedLanguagePropertyWrapper<T, ?, ?> compositeLanguageWrapper;
    protected final SpecializedLootPropertyWrapper<T, ?, ?> compositeLootWrapper;
    protected final SpecializedModelPropertyWrapper<T, ?, ?> compositeModelWrapper;
    protected final SpecializedRecipePropertyWrapper<T, ?, ?> compositeRecipeWrapper;
    protected final SpecializedTagPropertyWrapper<T, ?, ?> compositeTagWrapper;

    public BaseDefaultableDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, isTemplate, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BaseDefaultableDataGenPropertyWrapper(@NotNull Supplier<T> parentObject, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public BaseDefaultableDataGenPropertyWrapper(Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        super(builderFactory);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>();
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>();
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    public BaseDefaultableDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BaseDefaultableDataGenPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BaseDefaultableDataGenPropertyWrapper(@NotNull Supplier<T> parentObject, String modId) {
        super(parentObject, BaseDefaultableDataGenPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public BaseDefaultableDataGenPropertyWrapper() {
        super(BaseDefaultableDataGenPropertyWrapperBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>();
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>();
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<T, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }

    @Override
    public Optional<SpecializedLootPropertyWrapper<T, ?, ?>> getSpecializedLootWrapper() {
        return Optional.of(compositeLootWrapper);
    }

    @Override
    public Optional<SpecializedModelPropertyWrapper<T, ?, ?>> getSpecializedModelWrapper() {
        return Optional.of(compositeModelWrapper);
    }

    @Override
    public Optional<SpecializedRecipePropertyWrapper<T, ?, ?>> getSpecializedRecipeWrapper() {
        return Optional.of(compositeRecipeWrapper);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapper<T, ?, ?>> getSpecializedTagWrapper() {
        return Optional.of(compositeTagWrapper);
    }
}
