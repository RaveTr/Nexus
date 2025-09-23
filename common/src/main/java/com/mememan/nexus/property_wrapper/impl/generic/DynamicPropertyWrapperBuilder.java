package com.mememan.nexus.property_wrapper.impl.generic;

import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.tag.DefaultableTagBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Dynamic extension of {@link BaseDataGenPropertyWrapperBuilder} that wraps the parent object type in a {@link ResourceKey}.
 * Primarily intended for dynamic objects, such as damage types. Implements composite specialized wrapper builders for
 * language and tag support.
 *
 * @apiNote {@link LanguageBasedPropertyWrapper#bypassesDefaultTranslation()} is enabled by default for this builder,
 * since the majority of dynamic objects are not directly localized. This can be disabled/adjusted if needed via
 * {@link #bypassDefaultTranslation(boolean)}.
 *
 * @implNote {@link #withTag(Supplier)} and its overloads/variants are deprecated for this builder in favour of
 * {@link #withAdditionalTag(Supplier)} and its overloads/variants. You may view their javadocs here for details on
 * why that is.
 *
 * @see DynamicPropertyWrapper
 * @see #withTag(Supplier)
 */
public class DynamicPropertyWrapperBuilder<T, SELF extends DynamicPropertyWrapperBuilder<T, SELF, DPW>, DPW extends DynamicPropertyWrapper<T, DPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW> implements DefaultableLanguageBasedPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW>, DefaultableTagBasedPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW> {
    protected final SpecializedLanguagePropertyWrapperBuilder<ResourceKey<T>, SELF, DPW> compositeLanguageBuilder;
    protected final SpecializedTagPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW> compositeTagBuilder;

    public DynamicPropertyWrapperBuilder(@NotNull DPW ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<ResourceKey<T>, SELF, DPW>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get()
                .bypassDefaultTranslation(); // Enabled by default for convenience + determinism of how dynamic objects are meant to be translated
        this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
    }

    @Override
    public SELF copyFrom(DPW propertyWrapper) {
        DefaultableLanguageBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        DefaultableTagBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper);
    }

    /**
     * @deprecated Use {@link #withAdditionalTag(Supplier)} instead.
     *
     * @implNote Due to generic type constraints imposed by the fact that dynamic objects are always wrapped within
     * {@linkplain ResourceKey ResourceKeys} by nature, this method functionally does nothing.
     * <br></br>
     * If you absolutely require the base functionality of this method (for whatever reason - seriously, how would
     * {@link ResourceKey} {@linkplain TagKey TagKeys} even work?), you may override this method and return
     * {@code DefaultableTagBasedPropertyWrapperBuilder.super.withTag(targetTag)}.
     */
    @Deprecated
    @Override
    public SELF withTag(Supplier<TagKey<? super ResourceKey<T>>> targetTag) {
        return self();
    }

    /**
     * @deprecated Use {@link #withAdditionalTags(List)} instead.
     *
     * @implNote Due to generic type constraints imposed by the fact that dynamic objects are always wrapped within
     * {@linkplain ResourceKey ResourceKeys} by nature, this method functionally does nothing.
     * <br></br>
     * If you absolutely require the base functionality of this method (for whatever reason - seriously, how would
     * {@link ResourceKey} {@linkplain TagKey TagKeys} even work?), you may override this method and return
     * {@code DefaultableTagBasedPropertyWrapperBuilder.super.withTags(targetTags)}.
     */
    @Deprecated
    @Override
    public SELF withTags(List<Supplier<TagKey<? super ResourceKey<T>>>> targetTags) {
        return self();
    }

    /**
     * @deprecated Use {@link #withAdditionalTags(Supplier[])} instead.
     *
     * @implNote Due to generic type constraints imposed by the fact that dynamic objects are always wrapped within
     * {@linkplain ResourceKey ResourceKeys} by nature, this method functionally does nothing.
     * <br></br>
     * If you absolutely require the base functionality of this method (for whatever reason - seriously, how would
     * {@link ResourceKey} {@linkplain TagKey TagKeys} even work?), you may override this method and return
     * {@code DefaultableTagBasedPropertyWrapperBuilder.super.withTags(targetTags)}.
     */
    @Deprecated
    @Override
    public SELF withTags(Supplier<TagKey<? super ResourceKey<T>>>... targetTags) {
        return self();
    }

    /**
     * @deprecated Use {@link #setAdditionalTags(List)} instead.
     *
     * @implNote Due to generic type constraints imposed by the fact that dynamic objects are always wrapped within
     * {@linkplain ResourceKey ResourceKeys} by nature, this method functionally does nothing.
     * <br></br>
     * If you absolutely require the base functionality of this method (for whatever reason - seriously, how would
     * {@link ResourceKey} {@linkplain TagKey TagKeys} even work?), you may override this method and return
     * {@code DefaultableTagBasedPropertyWrapperBuilder.super.setTags(targetTags)}.
     */
    @Deprecated
    @Override
    public SELF setTags(List<Supplier<TagKey<? super ResourceKey<T>>>> targetTags) {
        return self();
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<ResourceKey<T>, SELF, DPW>> getSpecializedLanguageBuilder() {
        return Optional.of(compositeLanguageBuilder);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW>> getSpecializedTagBuilder() {
        return Optional.of(compositeTagBuilder);
    }
}
