package com.mememan.nexus.property_wrapper.base.specialised.tag;

import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Optional;

/**
 * Delegate extension for {@link TagBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link TagBasedPropertyWrapperBuilder}) using {@link #getSpecializedTagWrapper()}.
 *
 * @see DefaultableTagBasedPropertyWrapperBuilder
 */
public interface DefaultableTagBasedPropertyWrapper<T, SELF extends TagBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends TagBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends TagBasedPropertyWrapper<T, SELF, BUILDER> {

    Optional<SpecializedTagPropertyWrapper<T, ?, ?>> getSpecializedTagWrapper();

    @Override
    default List<TagKey<? super T>> getObjectTags() {
        return getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::getObjectTags).orElse(ObjectArrayList.of());
    }

    @Override
    default List<TagKey<?>> getAdditionalTags() {
        return getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::getAdditionalTags).orElse(ObjectArrayList.of());
    }
}
