package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.LanguageBasedPropertyWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpecializedLanguagePropertyWrapper<T, SELF extends LanguageBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedLanguagePropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements LanguageBasedPropertyWrapper<T, SELF, BUILDER> {

    public SpecializedLanguagePropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        super(parentObject, isTemplate);
    }

    public SpecializedLanguagePropertyWrapper(@NotNull Supplier<T> parentObject) {
        super(parentObject);
    }

    public SpecializedLanguagePropertyWrapper() {
        super();
    }

    @Override
    public @NotNull String getObjectDescriptionId() {
        return "";
    }

    @Override
    public Optional<String> getCustomName() {
        return Optional.empty();
    }

    @Override
    public Optional<Function<String, String>> getObjectPostTranslationMapper() {
        return Optional.empty();
    }

    @Override
    public List<String> getCustomSeparatorWords() {
        return List.of();
    }

    @Override
    public boolean hasLiteralTranslation() {
        return false;
    }

    @Override
    public boolean bypassesDefaultTranslation() {
        return false;
    }

    @Override
    public Map<String, Function<String, String>> getAdditionalLocalizationKeys() {
        return Map.of();
    }
}
