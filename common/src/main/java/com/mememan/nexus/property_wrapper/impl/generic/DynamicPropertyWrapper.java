package com.mememan.nexus.property_wrapper.impl.generic;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.tag.DefaultableTagBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Dynamic extension of {@link BaseDataGenPropertyWrapper} that wraps the parent object type in a {@link ResourceKey}.
 * Primarily intended for dynamic objects, such as damage types. Implements composite specialized wrappers for language
 * and tag support.
 *
 * @see DynamicPropertyWrapperBuilder
 */
public class DynamicPropertyWrapper<T, SELF extends DynamicPropertyWrapper<T, SELF, BUILDER>, BUILDER extends DynamicPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<ResourceKey<T>, SELF, BUILDER> implements DefaultableLanguageBasedPropertyWrapper<ResourceKey<T>, SELF, BUILDER>, DefaultableTagBasedPropertyWrapper<ResourceKey<T>, SELF, BUILDER> {
    protected final SpecializedLanguagePropertyWrapper<ResourceKey<T>, ?, ?> compositeLanguageWrapper;
    protected final SpecializedTagPropertyWrapper<ResourceKey<T>, ?, ?> compositeTagWrapper;

    public DynamicPropertyWrapper(Supplier<ResourceKey<T>> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<ResourceKey<T>, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, isTemplate, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public DynamicPropertyWrapper(@NotNull Supplier<ResourceKey<T>> parentObject, Function<SELF, PropertyWrapperBuilder<ResourceKey<T>, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public DynamicPropertyWrapper(Function<SELF, PropertyWrapperBuilder<ResourceKey<T>, BUILDER, SELF>> builderFactory) {
        super(builderFactory);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    public DynamicPropertyWrapper(Supplier<ResourceKey<T>> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, DynamicPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public DynamicPropertyWrapper(@NotNull Supplier<ResourceKey<T>> parentObject, String modId) {
        super(parentObject, DynamicPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public DynamicPropertyWrapper() {
        super(DynamicPropertyWrapperBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    /**
     * @implNote Use {@link #getAdditionalTags()} for all {@link DynamicPropertyWrapper} types, as object tags are not
     * directly supported due to generic type constraints.
     *
     * @return {@link ObjectArrayList#of()}.
     *
     * @see DynamicPropertyWrapperBuilder#withTag(Supplier)
     */
    @Override
    public List<Supplier<TagKey<? super ResourceKey<T>>>> getObjectTags() {
        return ObjectArrayList.of();
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<ResourceKey<T>, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapper<ResourceKey<T>, ?, ?>> getSpecializedTagWrapper() {
        return Optional.of(compositeTagWrapper);
    }
}
