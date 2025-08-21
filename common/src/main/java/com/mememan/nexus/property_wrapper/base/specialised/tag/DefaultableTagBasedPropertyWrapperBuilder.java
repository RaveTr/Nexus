package com.mememan.nexus.property_wrapper.base.specialised.tag;

import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Optional;

/**
 * Delegate extension for {@link TagBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedTagBuilder()}.
 *
 * @see DefaultableTagBasedPropertyWrapper
 */
public interface DefaultableTagBasedPropertyWrapperBuilder<T, SELF extends TagBasedPropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends TagBasedPropertyWrapper<T, MBPW, SELF>> extends TagBasedPropertyWrapperBuilder<T, SELF, MBPW> {

    Optional<SpecializedTagPropertyWrapperBuilder<T, SELF, MBPW>> getSpecializedTagBuilder();

    @Override
    default SELF withTag(TagKey<T> targetTag) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withTag(targetTag));
        return self();
    }

    @Override
    default SELF withTags(List<TagKey<T>> targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withTags(targetTags));
        return self();
    }

    @Override
    default SELF withTags(TagKey<T>... targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withTags(targetTags));
        return self();
    }

    @Override
    default SELF setTags(List<TagKey<T>> targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.setTags(targetTags));
        return self();
    }

    @Override
    default SELF withAdditionalTag(TagKey<?> targetTag) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withAdditionalTag(targetTag));
        return self();
    }

    @Override
    default SELF withAdditionalTags(List<TagKey<?>> targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withAdditionalTags(targetTags));
        return self();
    }

    @Override
    default SELF withAdditionalTags(TagKey<?>... targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withAdditionalTags(targetTags));
        return self();
    }
}
