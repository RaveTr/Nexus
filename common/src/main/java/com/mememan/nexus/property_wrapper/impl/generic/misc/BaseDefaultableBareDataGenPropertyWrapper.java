package com.mememan.nexus.property_wrapper.impl.generic.misc;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.misc.DefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseDefaultableBareDataGenPropertyWrapper<T, SELF extends DefaultableBareDataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends BaseDefaultableBareDataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements DefaultableBareDataGenPropertyWrapper<T, SELF, BUILDER> {
    protected final SpecializedLanguagePropertyWrapper<T, ?, ?> compositeLanguageWrapper;
    protected final SpecializedTagPropertyWrapper<T, ?, ?> compositeTagWrapper;

    public BaseDefaultableBareDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, isTemplate, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BaseDefaultableBareDataGenPropertyWrapper(@NotNull Supplier<T> parentObject, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public BaseDefaultableBareDataGenPropertyWrapper(Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        super(builderFactory);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    public BaseDefaultableBareDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BaseDefaultableBareDataGenPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BaseDefaultableBareDataGenPropertyWrapper(@NotNull Supplier<T> parentObject, String modId) {
        super(parentObject, BaseDefaultableBareDataGenPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public BaseDefaultableBareDataGenPropertyWrapper() {
        super(BaseDefaultableBareDataGenPropertyWrapperBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<T, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapper<T, ?, ?>> getSpecializedTagWrapper() {
        return Optional.of(compositeTagWrapper);
    }
}
