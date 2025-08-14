package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.LanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.ModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.ModelBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.TagBasedPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Core implementation for a generic Property Wrapper Builder that covers most datagen-based use cases for a standard object.
 *
 * @see KoreDataGenPropertyWrapper
 */
public class KoreDataGenPropertyWrapperBuilder<T, SELF extends KoreDataGenPropertyWrapperBuilder<T, SELF, SPW>, SPW extends KoreDataGenPropertyWrapper<T, SPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, SPW> implements LanguageBasedPropertyWrapperBuilder<T, SELF, SPW>, ModelBasedPropertyWrapperBuilder<T, SELF, SPW>, TagBasedPropertyWrapperBuilder<T, SELF, SPW> {
    protected Optional<String> customName = Optional.empty();
    protected boolean literalTranslation = false;
    protected Optional<Function<String, String>> objectPostTranslationMapper = Optional.empty();
    protected boolean bypassDefaultTranslation = false;
    protected final List<String> customSeparatorWords = ObjectArrayList.of(DEFAULT_SEPARATOR_WORDS.toArray(String[]::new));
    protected final Map<String, Function<String, String>> additionalLocalizations = new Object2ObjectOpenHashMap<>();
    protected Optional<Function<T, ModelBasedPropertyWrapper.ModelDefinition>> modelDefinitionsMapper = Optional.empty();
    protected final List<TagKey<T>> objectTagKeys = ObjectArrayList.of();
    protected final List<TagKey<?>> additionalTagKeys = ObjectArrayList.of();

    public KoreDataGenPropertyWrapperBuilder(@NotNull SPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(SPW propertyWrapper) { // Allow gen of (either builder or specific property)
        return super.copyFrom(propertyWrapper)
                .withCustomName(propertyWrapper.getCustomName().orElse(null))
                .literalTranslation(propertyWrapper.hasLiteralTranslation())
                .withLocalization(propertyWrapper.getObjectPostTranslationMapper().orElse(null))
                .bypassDefaultTranslation(propertyWrapper.bypassesDefaultTranslation())
                .setCustomSeparatorWords(propertyWrapper.getCustomSeparatorWords())
                .setAdditionalLocalizationKeys(propertyWrapper.getAdditionalLocalizationKeys())
                .withModelDefinition(propertyWrapper.getModelDefinition().orElse(null))
                .setTags(propertyWrapper.getObjectTags())
                .setAdditionalTags(propertyWrapper.getAdditionalTags());
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

    @Override
    public SELF withModelDefinition(Function<T, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition) {
        this.modelDefinitionsMapper = Optional.ofNullable(modelDefinition); // JIC
        return self();
    }

    @Override
    public SELF withTag(TagKey<T> targetTag) {
        this.objectTagKeys.add(targetTag);
        return self();
    }

    @Override
    public SELF withTags(List<TagKey<T>> targetTags) {
        this.objectTagKeys.addAll(targetTags);
        return self();
    }

    @Override
    public SELF setTags(List<TagKey<T>> targetTags) {
        this.objectTagKeys.clear();
        this.objectTagKeys.addAll(targetTags);
        return self();
    }

    @Override
    public SELF withAdditionalTag(TagKey<?> targetTag) {
        this.additionalTagKeys.add(targetTag);
        return self();
    }

    @Override
    public SELF withAdditionalTags(List<TagKey<?>> targetTags) {
        this.additionalTagKeys.addAll(targetTags);
        return self();
    }

    @Override
    public SELF setAdditionalTags(List<TagKey<?>> targetTags) {
        this.additionalTagKeys.clear();
        this.additionalTagKeys.addAll(targetTags);
        return self();
    }
}
