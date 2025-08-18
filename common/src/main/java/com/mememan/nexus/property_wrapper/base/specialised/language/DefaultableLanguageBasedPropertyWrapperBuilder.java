package com.mememan.nexus.property_wrapper.base.specialised.language;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Delegate extension for {@link LanguageBasedPropertyWrapperBuilder} that adds default method implementations using
 * {@link DefaultablePropertyWrapperBuilder}.
 */
public interface DefaultableLanguageBasedPropertyWrapperBuilder<T, SELF extends LanguageBasedPropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends LanguageBasedPropertyWrapper<T, LBPW, SELF>> extends LanguageBasedPropertyWrapperBuilder<T, SELF, LBPW>, DefaultablePropertyWrapperBuilder<T, SELF, LBPW, SpecializedLanguagePropertyWrapperBuilder<T, SELF, LBPW>> {

    @Override
    default SELF withCustomName(String manuallyLocalizedObjectName) {
        getSpecializedBuilder().ifPresent(builder -> builder.withCustomName(manuallyLocalizedObjectName));
        return self();
    }

    @Override
    default SELF literalTranslation(boolean literalTranslation) {
        getSpecializedBuilder().ifPresent(builder -> builder.literalTranslation(literalTranslation));
        return self();
    }

    @Override
    default SELF withLocalization(Function<String, String> objectTranslationFunc) {
        getSpecializedBuilder().ifPresent(builder -> builder.withLocalization(objectTranslationFunc));
        return self();
    }

    @Override
    default SELF bypassDefaultTranslation(boolean bypassDefaultTranslation) {
        getSpecializedBuilder().ifPresent(builder -> builder.bypassDefaultTranslation(bypassDefaultTranslation));
        return self();
    }

    @Override
    default SELF withCustomSeparatorWord(String customSeparatorWord) {
        getSpecializedBuilder().ifPresent(builder -> builder.withCustomSeparatorWord(customSeparatorWord));
        return self();
    }

    @Override
    default SELF withCustomSeparatorWords(List<String> definedSeparatorWords) {
        getSpecializedBuilder().ifPresent(builder -> builder.withCustomSeparatorWords(definedSeparatorWords));
        return self();
    }

    @Override
    default SELF setCustomSeparatorWords(List<String> definedSeparatorWords) {
        getSpecializedBuilder().ifPresent(builder -> builder.setCustomSeparatorWords(definedSeparatorWords));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKey(String localizationKey) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKey(localizationKey));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKeys(List<String> localizationKeys) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKeys(localizationKeys));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKey(String localizationKey, String localizedValue) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKey(localizationKey, localizedValue));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKeys(String[] localizationKeys, String[] localizedValues) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKeys(localizationKeys, localizedValues));
        return self();
    }

    @Override
    default SELF withAdditionalLocalizationKey(String localizationKey, Function<String, String> localizedValueMapper) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalLocalizationKey(localizationKey, localizedValueMapper));
        return self();
    }

    @Override
    default SELF setAdditionalLocalizationKeys(Map<String, Function<String, String>> localizationKeys) {
        getSpecializedBuilder().ifPresent(builder -> builder.setAdditionalLocalizationKeys(localizationKeys));
        return self();
    }
}
