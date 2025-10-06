package com.mememan.nexus.property_wrapper.impl.generic.misc;

import com.mememan.nexus.property_wrapper.base.generic.misc.DefaultableDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.misc.DefaultableDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BaseDefaultableDataGenPropertyWrapperBuilder<T, SELF extends DefaultableDataGenPropertyWrapperBuilder<T, SELF, BDDGPW>, BDDGPW extends DefaultableDataGenPropertyWrapper<T, BDDGPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, BDDGPW> implements DefaultableDataGenPropertyWrapperBuilder<T, SELF, BDDGPW> {
    protected final SpecializedLanguagePropertyWrapperBuilder<T, SELF, BDDGPW> compositeLanguageBuilder;
    protected final SpecializedLootPropertyWrapperBuilder<T, SELF, BDDGPW> compositeLootBuilder;
    protected final SpecializedModelPropertyWrapperBuilder<T, SELF, BDDGPW> compositeModelBuilder;
    protected final SpecializedRecipePropertyWrapperBuilder<T, SELF, BDDGPW> compositeRecipeBuilder;
    protected final SpecializedTagPropertyWrapperBuilder<T, SELF, BDDGPW> compositeTagBuilder;

    public BaseDefaultableDataGenPropertyWrapperBuilder(@NotNull BDDGPW ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<T, SELF, BDDGPW>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get();
        this.compositeLootBuilder = (SpecializedLootPropertyWrapperBuilder<T, SELF, BDDGPW>) ownerWrapper.getSpecializedLootWrapper().map(SpecializedLootPropertyWrapper::builder).get();
        this.compositeModelBuilder = (SpecializedModelPropertyWrapperBuilder<T, SELF, BDDGPW>) ownerWrapper.getSpecializedModelWrapper().map(SpecializedModelPropertyWrapper::builder).get();
        this.compositeRecipeBuilder = (SpecializedRecipePropertyWrapperBuilder<T, SELF, BDDGPW>) ownerWrapper.getSpecializedRecipeWrapper().map(SpecializedRecipePropertyWrapper::builder).get();
        this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<T, SELF, BDDGPW>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
    }

    @Override
    public SELF copyFrom(BDDGPW propertyWrapper) {
        DefaultableDataGenPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<T, SELF, BDDGPW>> getSpecializedLanguageBuilder() {
        return Optional.of(compositeLanguageBuilder);
    }

    @Override
    public Optional<SpecializedLootPropertyWrapperBuilder<T, SELF, BDDGPW>> getSpecializedLootBuilder() {
        return Optional.of(compositeLootBuilder);
    }

    @Override
    public Optional<SpecializedModelPropertyWrapperBuilder<T, SELF, BDDGPW>> getSpecializedModelBuilder() {
        return Optional.of(compositeModelBuilder);
    }

    @Override
    public Optional<SpecializedRecipePropertyWrapperBuilder<T, SELF, BDDGPW>> getSpecializedRecipeBuilder() {
        return Optional.of(compositeRecipeBuilder);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapperBuilder<T, SELF, BDDGPW>> getSpecializedTagBuilder() {
        return Optional.of(compositeTagBuilder);
    }
}
