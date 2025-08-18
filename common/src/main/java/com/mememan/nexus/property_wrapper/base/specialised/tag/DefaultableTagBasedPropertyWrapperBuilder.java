package com.mememan.nexus.property_wrapper.base.specialised.tag;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Delegate extension for {@link TagBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedBuilder()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableTagBasedPropertyWrapperBuilder<T, SELF extends TagBasedPropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends TagBasedPropertyWrapper<T, MBPW, SELF>> extends TagBasedPropertyWrapperBuilder<T, SELF, MBPW>, DefaultablePropertyWrapperBuilder<T, SELF, MBPW, SpecializedTagPropertyWrapperBuilder<T, SELF, MBPW>> {

    @Override
    default SELF withTag(TagKey<T> targetTag) {
        getSpecializedBuilder().ifPresent(builder -> builder.withTag(targetTag));
        return self();
    }

    @Override
    default SELF withTags(List<TagKey<T>> targetTags) {
        getSpecializedBuilder().ifPresent(builder -> builder.withTags(targetTags));
        return self();
    }

    @Override
    default SELF withTags(TagKey<T>... targetTags) {
        getSpecializedBuilder().ifPresent(builder -> builder.withTags(targetTags));
        return self();
    }

    @Override
    default SELF setTags(List<TagKey<T>> targetTags) {
        getSpecializedBuilder().ifPresent(builder -> builder.setTags(targetTags));
        return self();
    }

    @Override
    default SELF withAdditionalTag(TagKey<?> targetTag) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalTag(targetTag));
        return self();
    }

    @Override
    default SELF withAdditionalTags(List<TagKey<?>> targetTags) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalTags(targetTags));
        return self();
    }

    @Override
    default SELF withAdditionalTags(TagKey<?>... targetTags) {
        getSpecializedBuilder().ifPresent(builder -> builder.withAdditionalTags(targetTags));
        return self();
    }
}
