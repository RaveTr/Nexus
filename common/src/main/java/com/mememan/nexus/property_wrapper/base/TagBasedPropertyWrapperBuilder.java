package com.mememan.nexus.property_wrapper.base;

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
public interface TagBasedPropertyWrapperBuilder<T, SELF extends TagBasedPropertyWrapperBuilder<T, SELF, TBPW>, TBPW extends TagBasedPropertyWrapper<T, TBPW, SELF>> extends DataGenPropertyWrapperBuilder<T, SELF, TBPW> {

    /**
     * Appends a tag to the object being wrapped.
     *
     * @param targetTag The tag to append.
     *
     * @return {@code this} (builder method).
     *
     * @see #withTags(List)
     * @see #withTags(TagKey...)
     * @see #setTags(List)
     */
    TagBasedPropertyWrapperBuilder<T, SELF, TBPW> withTag(TagKey<T> targetTag);

    /**
     * Appends multiple tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@code this} (builder method).
     *
     * @see #withTag(TagKey)
     * @see #withTags(TagKey...)
     * @see #setTags(List)
     */
    TagBasedPropertyWrapperBuilder<T, SELF, TBPW> withTags(List<TagKey<T>> targetTags);

    /**
     * Appends multiple tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@code this} (builder method).
     *
     * @see #withTag(TagKey)
     * @see #withTags(List)
     * @see #setTags(List)
     */
    default TagBasedPropertyWrapperBuilder<T, SELF, TBPW> withTags(TagKey<T>... targetTags) {
        return withTags(ObjectArrayList.of(targetTags));
    }

    /**
     * Overrides the tags of the object being wrapped with the given tags.
     *
     * @param targetTags The tags to set.
     *
     * @return {@code this} (builder method).
     *
     * @see #withTag(TagKey)
     * @see #withTags(List)
     * @see #withTags(TagKey...)
     */
    TagBasedPropertyWrapperBuilder<T, SELF, TBPW> setTags(List<TagKey<T>> targetTags);

    /**
     * Appends an additional tag to the object being wrapped.
     *
     * @param targetTag The tag to append.
     *
     * @return {@code this} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTags(List)
     * @see #withAdditionalTags(TagKey...)
     * @see #setAdditionalTags(List)
     */
    TagBasedPropertyWrapperBuilder<T, SELF, TBPW> withAdditionalTag(TagKey<?> targetTag);

    /**
     * Appends multiple additional tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@code this} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTag(TagKey)
     * @see #withAdditionalTags(TagKey...)
     * @see #setAdditionalTags(List)
     */
    TagBasedPropertyWrapperBuilder<T, SELF, TBPW> withAdditionalTags(List<TagKey<?>> targetTags);

    /**
     * Appends multiple additional tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@code this} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTag(TagKey)
     * @see #withAdditionalTags(List)
     * @see #setAdditionalTags(List)
     */
    default TagBasedPropertyWrapperBuilder<T, SELF, TBPW> withAdditionalTags(TagKey<?>... targetTags) {
        return withAdditionalTags(ObjectArrayList.of(targetTags));
    }

    /**
     * Overrides the additional tags of the object being wrapped with the given tags.
     *
     * @param targetTags The tags to set.
     *
     * @return {@code this} (builder method).
     *
     * @implNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types don't have a need for implementing this.
     *
     * @see #withAdditionalTag(TagKey)
     * @see #withAdditionalTags(List)
     * @see #withAdditionalTags(TagKey...)
     */
    TagBasedPropertyWrapperBuilder<T, SELF, TBPW> setAdditionalTags(List<TagKey<?>> targetTags);
}
