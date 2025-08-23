package com.mememan.nexus.property_wrapper.base.specialised.tag;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Extension of {@link DataGenPropertyWrapperBuilder} with builder methods tailored towards handling tags for the object
 * being wrapped.
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link TagBasedPropertyWrapper}.
 *
 * @see TagBasedPropertyWrapper
 */
public interface TagBasedPropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, TBPW>, TBPW extends PropertyWrapper<T, TBPW, SELF>> extends DataGenPropertyWrapperBuilder<T, SELF, TBPW> {

    /**
     * Appends a tag to the object being wrapped.
     *
     * @param targetTag The tag to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withTags(List)
     * @see #withTags(TagKey...)
     * @see #setTags(List)
     */
    SELF withTag(TagKey<? super T> targetTag);

    /**
     * Appends multiple tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withTag(TagKey)
     * @see #withTags(TagKey...)
     * @see #setTags(List)
     */
    SELF withTags(List<TagKey<? super T>> targetTags);

    /**
     * Appends multiple tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withTag(TagKey)
     * @see #withTags(List)
     * @see #setTags(List)
     */
    default SELF withTags(TagKey<? super T>... targetTags) {
        return withTags(ObjectArrayList.of(targetTags));
    }

    /**
     * Overrides the tags of the object being wrapped with the given tags.
     *
     * @param targetTags The tags to set.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withTag(TagKey)
     * @see #withTags(List)
     * @see #withTags(TagKey...)
     */
    SELF setTags(List<TagKey<? super T>> targetTags);

    /**
     * Appends an additional tag to the object being wrapped.
     *
     * @param targetTag The tag to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTags(List)
     * @see #withAdditionalTags(TagKey...)
     * @see #setAdditionalTags(List)
     */
    SELF withAdditionalTag(TagKey<?> targetTag);

    /**
     * Appends multiple additional tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTag(TagKey)
     * @see #withAdditionalTags(TagKey...)
     * @see #setAdditionalTags(List)
     */
    SELF withAdditionalTags(List<TagKey<?>> targetTags);

    /**
     * Appends multiple additional tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTag(TagKey)
     * @see #withAdditionalTags(List)
     * @see #setAdditionalTags(List)
     */
    default SELF withAdditionalTags(TagKey<?>... targetTags) {
        return withAdditionalTags(ObjectArrayList.of(targetTags));
    }

    /**
     * Overrides the additional tags of the object being wrapped with the given tags.
     *
     * @param targetTags The tags to set.
     *
     * @return {@link #self()} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTag(TagKey)
     * @see #withAdditionalTags(List)
     * @see #withAdditionalTags(TagKey...)
     */
    SELF setAdditionalTags(List<TagKey<?>> targetTags);
}
