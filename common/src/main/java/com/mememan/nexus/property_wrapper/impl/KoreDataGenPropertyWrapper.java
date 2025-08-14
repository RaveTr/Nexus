package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.LanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.LanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.ModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.TagBasedPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Core implementation for a generic Property Wrapper that covers most datagen-based use cases for a standard object.
 *
 * @see KoreDataGenPropertyWrapperBuilder
 */
public class KoreDataGenPropertyWrapper<T, SELF extends KoreDataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends KoreDataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements LanguageBasedPropertyWrapper<T, SELF, BUILDER>, ModelBasedPropertyWrapper<T, SELF, BUILDER>, TagBasedPropertyWrapper<T, SELF, BUILDER> {

    public KoreDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        super(parentObject, isTemplate);
    }

    public KoreDataGenPropertyWrapper(@NotNull Supplier<T> parentObject) {
        super(parentObject);
    }

    @Override
    public @NotNull String getObjectDescriptionId() { //TODO Abstract
        return "";
    }

    @Override
    public Optional<Function<String, String>> getObjectPostTranslationMapper() {
        return rawBuilder().flatMap(b -> b.objectPostTranslationMapper);
    }

    @Override
    public Optional<String> getCustomName() {
        return rawBuilder().flatMap(b -> b.customName);
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

    @Override
    public Optional<Function<T, ModelDefinition>> getModelDefinition() {
        return rawBuilder().flatMap(b -> b.modelDefinitionsMapper);
    }

    @Override
    public List<TagKey<T>> getObjectTags() {
        return rawBuilder().map(b -> b.objectTagKeys).orElse(ObjectArrayList.of());
    }

    @Override
    public List<TagKey<?>> getAdditionalTags() {
        return rawBuilder().map(b -> b.additionalTagKeys).orElse(ObjectArrayList.of());
    }
}
