package com.mememan.nexus.property_wrapper.impl.specialised.language;

import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class SpecializedLanguagePropertyWrapperBuilder<T, SELF extends LanguageBasedPropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends LanguageBasedPropertyWrapper<T, LBPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, LBPW> implements LanguageBasedPropertyWrapperBuilder<T, SELF, LBPW> {
    protected Optional<String> customName = Optional.empty();
    protected boolean literalTranslation = false;
    protected Optional<Function<String, String>> objectPostTranslationMapper = Optional.empty();
    protected boolean bypassDefaultTranslation = false;
    protected final List<String> customSeparatorWords = ObjectArrayList.of(DEFAULT_SEPARATOR_WORDS.toArray(String[]::new));
    protected final Map<String, Function<String, String>> additionalLocalizations = new Object2ObjectOpenHashMap<>();

    public SpecializedLanguagePropertyWrapperBuilder(@NotNull LBPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(LBPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .withCustomName(propertyWrapper.getCustomName().orElse(null))
                .literalTranslation(propertyWrapper.hasLiteralTranslation())
                .withLocalization(propertyWrapper.getObjectPostTranslationMapper().orElse(null))
                .bypassDefaultTranslation(propertyWrapper.bypassesDefaultTranslation())
                .setCustomSeparatorWords(List.copyOf(propertyWrapper.getCustomSeparatorWords()))
                .setAdditionalLocalizationKeys(Map.copyOf(propertyWrapper.getAdditionalLocalizationKeys()));
    }

    @Override
    public SELF withCustomName(String manuallyLocalizedObjectName) {
        this.customName = Optional.ofNullable(manuallyLocalizedObjectName); // Allow blanks cuz why not + JIC
        return self();
    }

    @Override
    public SELF literalTranslation(boolean literalTranslation) {
        this.literalTranslation = literalTranslation;
        return self();
    }

    @Override
    public SELF withLocalization(Function<String, String> objectTranslationFunc) {
        this.objectPostTranslationMapper = Optional.ofNullable(objectTranslationFunc); // JIC
        return self();
    }

    @Override
    public SELF bypassDefaultTranslation(boolean bypassDefaultTranslation) {
        this.bypassDefaultTranslation = bypassDefaultTranslation;
        return self();
    }

    @Override
    public SELF withCustomSeparatorWord(String customSeparatorWord) {
        this.customSeparatorWords.add(customSeparatorWord);
        return self();
    }

    @Override
    public SELF withCustomSeparatorWords(List<String> definedSeparatorWords) {
        this.customSeparatorWords.addAll(definedSeparatorWords);
        return self();
    }

    @Override
    public SELF setCustomSeparatorWords(List<String> definedSeparatorWords) {
        this.customSeparatorWords.clear();
        this.customSeparatorWords.addAll(DEFAULT_SEPARATOR_WORDS);
        this.customSeparatorWords.addAll(definedSeparatorWords);
        return self();
    }

    @Override
    public SELF withAdditionalLocalizationKey(String localizationKey) {
        this.additionalLocalizations.put(localizationKey, null); // Allow for overrides + null keys
        return self();
    }

    @Override
    public SELF withAdditionalLocalizationKeys(List<String> localizationKeys) {
        if (!localizationKeys.isEmpty()) localizationKeys.forEach(this::withAdditionalLocalizationKey);
        return self();
    }

    @Override
    public SELF withAdditionalLocalizationKey(String localizationKey, String localizedValue) {
        this.additionalLocalizations.put(localizationKey, resultKey -> localizedValue);
        return self();
    }

    @Override
    public SELF withAdditionalLocalizationKeys(String[] localizationKeys, String[] localizedValues) {
        int chosenLength = Math.min(localizationKeys.length, localizedValues.length);

        if (chosenLength > 0) {
            if (chosenLength == 1) return withAdditionalLocalizationKey(localizationKeys[0], localizedValues[0]); // Don't unnecessarily run a loop (micro-optimization)

            for (int i = 0; i < chosenLength; i++) {
                withAdditionalLocalizationKey(localizationKeys[i], localizedValues[i]);
            }
        }

        return self();
    }

    @Override
    public SELF withAdditionalLocalizationKey(String localizationKey, Function<String, String> localizedValueMapper) {
        this.additionalLocalizations.put(localizationKey, localizedValueMapper);
        return self();
    }

    @Override
    public SELF setAdditionalLocalizationKeys(Map<String, Function<String, String>> localizationKeys) {
        this.additionalLocalizations.clear();
        this.additionalLocalizations.putAll(localizationKeys);
        return self();
    }
}
