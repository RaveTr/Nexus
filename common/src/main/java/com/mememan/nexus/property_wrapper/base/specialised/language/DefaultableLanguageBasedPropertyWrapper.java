package com.mememan.nexus.property_wrapper.base.specialised.language;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Delegate extension for {@link LanguageBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link LanguageBasedPropertyWrapperBuilder}) using {@link #getSpecializedWrapper()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableLanguageBasedPropertyWrapper<T, SELF extends LanguageBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedLanguagePropertyWrapperBuilder<T, BUILDER, SELF>> extends LanguageBasedPropertyWrapper<T, SELF, BUILDER>, DefaultablePropertyWrapper<T, SELF, BUILDER, SpecializedLanguagePropertyWrapper<T, SELF, BUILDER>> {

    @Override
    default Optional<String> getCustomName() {
        return getSpecializedWrapper().flatMap(SpecializedLanguagePropertyWrapper::getCustomName);
    }

    @Override
    default Optional<Function<String, String>> getObjectPostTranslationMapper() {
        return getSpecializedWrapper().flatMap(SpecializedLanguagePropertyWrapper::getObjectPostTranslationMapper);
    }

    @Override
    default List<String> getCustomSeparatorWords() {
        return getSpecializedWrapper().map(SpecializedLanguagePropertyWrapper::getCustomSeparatorWords).orElse(LanguageBasedPropertyWrapperBuilder.DEFAULT_SEPARATOR_WORDS);
    }

    @Override
    default boolean hasLiteralTranslation() {
        return getSpecializedWrapper().map(SpecializedLanguagePropertyWrapper::hasLiteralTranslation).orElse(false);
    }

    @Override
    default boolean bypassesDefaultTranslation() {
        return getSpecializedWrapper().map(SpecializedLanguagePropertyWrapper::bypassesDefaultTranslation).orElse(false);
    }

    @Override
    default Map<String, Function<String, String>> getAdditionalLocalizationKeys() {
        return getSpecializedWrapper().map(SpecializedLanguagePropertyWrapper::getAdditionalLocalizationKeys).orElse(Map.of());
    }
}
