package com.mememan.nexus.property_wrapper.base.specialised.tag;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Delegate extension for {@link TagBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link TagBasedPropertyWrapperBuilder}) using {@link #getSpecializedWrapper()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableTagBasedPropertyWrapper<T, SELF extends TagBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedTagPropertyWrapperBuilder<T, BUILDER, SELF>> extends TagBasedPropertyWrapper<T, SELF, BUILDER>, DefaultablePropertyWrapper<T, SELF, BUILDER, SpecializedTagPropertyWrapper<T, SELF, BUILDER>> {

    @Override
    default List<TagKey<T>> getObjectTags() {
        return getSpecializedWrapper().map(SpecializedTagPropertyWrapper::getObjectTags).orElse(ObjectArrayList.of());
    }

    @Override
    default List<TagKey<?>> getAdditionalTags() {
        return getSpecializedWrapper().map(SpecializedTagPropertyWrapper::getAdditionalTags).orElse(ObjectArrayList.of());
    }
}
