package com.mememan.nexus.property_wrapper.impl.specialised.language;

import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized implementation of {@link LanguageBasedPropertyWrapper}. Implements all language-related getter methods,
 * generic types, and default behaviour for language-based property wrapper handling.
 *
 * @see SpecializedLanguagePropertyWrapperBuilder
 */
public class SpecializedLanguagePropertyWrapper<T, SELF extends LanguageBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedLanguagePropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements LanguageBasedPropertyWrapper<T, SELF, BUILDER> {

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
    public Optional<String> getCustomName() {
        return rawBuilder().map(b -> b.customName);
    }

    @Override
    public Optional<Function<String, String>> getObjectPostTranslationMapper() {
        return rawBuilder().map(b -> b.objectPostTranslationMapper);
    }

    @Override
    public List<String> getCustomSeparatorWords() {
        return rawBuilder().map(b -> new ObjectArrayList<>(b.customSeparatorWords)).orElse(LanguageBasedPropertyWrapperBuilder.DEFAULT_SEPARATOR_WORDS);
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
        return rawBuilder().map(b -> new Object2ObjectOpenHashMap<>(b.additionalLocalizations)).orElse(new Object2ObjectOpenHashMap<>());
    }
}
