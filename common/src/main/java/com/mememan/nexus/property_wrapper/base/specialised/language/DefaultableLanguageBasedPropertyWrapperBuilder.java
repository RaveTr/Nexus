package com.mememan.nexus.property_wrapper.base.specialised.language;

import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Delegate extension for {@link LanguageBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedLanguageBuilder()}.
 *
 * @see DefaultableLanguageBasedPropertyWrapper
 */
public interface DefaultableLanguageBasedPropertyWrapperBuilder<T, SELF extends LanguageBasedPropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends LanguageBasedPropertyWrapper<T, LBPW, SELF>> extends LanguageBasedPropertyWrapperBuilder<T, SELF, LBPW> {

    /**
     * The specialized builder to which all builder methods should delegate.
     *
     * @return The specialized builder to which all builder methods should delegate. May be empty.
     */
    Optional<SpecializedLanguagePropertyWrapperBuilder<T, SELF, LBPW>> getSpecializedLanguageBuilder();

    @Override
    default SELF withCustomName(String manuallyLocalizedObjectName) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withCustomName(manuallyLocalizedObjectName));
        return self();
    }

    @Override
    default SELF literalTranslation(boolean literalTranslation) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.literalTranslation(literalTranslation));
        return self();
    }

    @Override
    default SELF withLocalization(Function<String, String> objectTranslationFunc) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withLocalization(objectTranslationFunc));
        return self();
    }

    @Override
    default SELF bypassDefaultTranslation(boolean bypassDefaultTranslation) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.bypassDefaultTranslation(bypassDefaultTranslation));
        return self();
    }

    @Override
    default SELF withCustomSeparatorWord(String customSeparatorWord) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withCustomSeparatorWord(customSeparatorWord));
        return self();
    }

    @Override
    default SELF withCustomSeparatorWords(List<String> definedSeparatorWords) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withCustomSeparatorWords(definedSeparatorWords));
        return self();
    }

    @Override
    default SELF setCustomSeparatorWords(List<String> definedSeparatorWords) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.setCustomSeparatorWords(definedSeparatorWords));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKey(String localizationKey) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKey(localizationKey));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKeys(List<String> localizationKeys) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKeys(localizationKeys));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKey(String localizationKey, String localizedValue) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKey(localizationKey, localizedValue));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKeys(String[] localizationKeys, String[] localizedValues) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKeys(localizationKeys, localizedValues));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKey(String localizationKey, Function<String, String> localizedValueMapper) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKey(localizationKey, localizedValueMapper));
        return self();
    }

    @Override
    default SELF setAdditionalLocalizationKeys(Map<String, Function<String, String>> localizationKeys) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.setAdditionalLocalizationKeys(localizationKeys));
        return self();
    }

    /**
     * @implNote Implementations of this method for language wrappers usually copy
     * {@link LanguageBasedPropertyWrapper#getAdditionalLocalizationKeys()}. This may result in duplicate language keys
     * if direct copies are made (usually from non-templates). As such, if you're calling this on an LBPWB instance that
     * is not a template, make sure that there are no collusions with the original additional localization keys.
     */
    @Override
    default SELF copyFrom(LBPW propertyWrapper) {
        getSpecializedLanguageBuilder().ifPresent(builder -> builder.copyFrom(propertyWrapper));
        return self();
    }
}
