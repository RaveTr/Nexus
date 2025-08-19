package com.mememan.nexus.property_wrapper.impl.specialised.language;

import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SpecializedLanguagePropertyWrapper<T, SELF extends LanguageBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedLanguagePropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements LanguageBasedPropertyWrapper<T, SELF, BUILDER> {

    public SpecializedLanguagePropertyWrapper(Supplier<T> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, SpecializedLanguagePropertyWrapperBuilder::new, modId);
    }

    public SpecializedLanguagePropertyWrapper(@NotNull Supplier<T> parentObject, @NotNull String modId) {
        super(parentObject, SpecializedLanguagePropertyWrapperBuilder::new, modId);
    }

    public SpecializedLanguagePropertyWrapper() {
        super(SpecializedLanguagePropertyWrapperBuilder::new);
    }

    @Override
    public abstract @NotNull String getObjectDescriptionId();

    @Override
    public Optional<String> getCustomName() {
        return rawBuilder().flatMap(b -> b.customName);
    }

    @Override
    public Optional<Function<String, String>> getObjectPostTranslationMapper() {
        return rawBuilder().flatMap(b -> b.objectPostTranslationMapper);
    }

    @Override
    public List<String> getCustomSeparatorWords() {
        return rawBuilder().map(b -> b.customSeparatorWords).orElse(LanguageBasedPropertyWrapperBuilder.DEFAULT_SEPARATOR_WORDS);
    }

    @Override
    public boolean hasLiteralTranslation() {
        return rawBuilder().map(b -> b.literalTranslation).orElse(false);
    }

    @Override
    public boolean bypassesDefaultTranslation() {
        return rawBuilder().map(b -> b.bypassDefaultTranslation).orElse(false);
    }

    @Override
    public Map<String, Function<String, String>> getAdditionalLocalizationKeys() {
        return rawBuilder().map(b -> b.additionalLocalizations).orElse(Map.of());
    }
}
