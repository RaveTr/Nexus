package com.mememan.nexus.property_wrapper.base.specialised.language;

import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Delegate extension for {@link LanguageBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link LanguageBasedPropertyWrapperBuilder}) using {@link #getSpecializedLanguageWrapper()}.
 *
 * @see DefaultableLanguageBasedPropertyWrapperBuilder
 */
public interface DefaultableLanguageBasedPropertyWrapper<T, SELF extends LanguageBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends LanguageBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends LanguageBasedPropertyWrapper<T, SELF, BUILDER> {

    /**
     * The specialized wrapper to which all getters should delegate.
     *
     * @return The specialized wrapper to which all getters should delegate. May be empty.
     */
    Optional<SpecializedLanguagePropertyWrapper<T, ?, ?>> getSpecializedLanguageWrapper();

    @Override
    default Optional<String> getCustomName() {
        return getSpecializedLanguageWrapper().flatMap(SpecializedLanguagePropertyWrapper::getCustomName);
    }

    @Override
    default Optional<Function<String, String>> getObjectPostTranslationMapper() {
        return getSpecializedLanguageWrapper().flatMap(SpecializedLanguagePropertyWrapper::getObjectPostTranslationMapper);
    }

    @Override
    default List<String> getCustomSeparatorWords() {
        return getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::getCustomSeparatorWords).orElse(LanguageBasedPropertyWrapperBuilder.DEFAULT_SEPARATOR_WORDS);
    }

    @Override
    default boolean hasLiteralTranslation() {
        return getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::hasLiteralTranslation).orElse(false);
    }

    @Override
    default boolean bypassesDefaultTranslation() {
        return getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::bypassesDefaultTranslation).orElse(false);
    }

    @Override
    default Map<String, Function<String, String>> getAdditionalLocalizationKeys() {
        return getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::getAdditionalLocalizationKeys).orElse(Map.of());
    }
}
