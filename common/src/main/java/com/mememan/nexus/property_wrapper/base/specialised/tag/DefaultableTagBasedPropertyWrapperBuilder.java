package com.mememan.nexus.property_wrapper.base.specialised.tag;

import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link TagBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedTagBuilder()}.
 *
 * @see DefaultableTagBasedPropertyWrapper
 */
public interface DefaultableTagBasedPropertyWrapperBuilder<T, SELF extends TagBasedPropertyWrapperBuilder<T, SELF, TBPW>, TBPW extends TagBasedPropertyWrapper<T, TBPW, SELF>> extends TagBasedPropertyWrapperBuilder<T, SELF, TBPW> {

    /**
     * The specialized builder to which all builder methods should delegate.
     *
     * @return The specialized builder to which all builder methods should delegate. May be empty.
     */
    Optional<SpecializedTagPropertyWrapperBuilder<T, SELF, TBPW>> getSpecializedTagBuilder();

    @Override
    default SELF withTag(Supplier<TagKey<? super T>> targetTag) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withTag(targetTag));
        return self();
    }

    @Override
    default SELF withTags(List<Supplier<TagKey<? super T>>> targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withTags(targetTags));
        return self();
    }

    @Override
    default SELF withTags(Supplier<TagKey<? super T>>... targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withTags(targetTags));
        return self();
    }

    @Override
    default SELF setTags(List<Supplier<TagKey<? super T>>> targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.setTags(targetTags));
        return self();
    }

    @Override
    default SELF withAdditionalTag(Supplier<TagKey<?>> targetTag) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withAdditionalTag(targetTag));
        return self();
    }

    @Override
    default SELF withAdditionalTags(List<Supplier<TagKey<?>>> targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withAdditionalTags(targetTags));
        return self();
    }

    @Override
    default SELF withAdditionalTags(Supplier<TagKey<?>>... targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.withAdditionalTags(targetTags));
        return self();
    }

    @Override
    default SELF setAdditionalTags(List<Supplier<TagKey<?>>> targetTags) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.setAdditionalTags(targetTags));
        return self();
    }

    @Override
    default SELF copyFrom(TBPW propertyWrapper) {
        getSpecializedTagBuilder().ifPresent(builder -> builder.copyFrom(propertyWrapper));
        return self();
    }
}
