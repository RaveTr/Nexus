package com.mememan.nexus.property_wrapper.base;

import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards handling tags for the object being wrapped.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link TagBasedPropertyWrapperBuilder}.
 *
 * @see TagBasedPropertyWrapperBuilder
 */
public interface TagBasedPropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> extends DataGenPropertyWrapper<T, SELF, BUILDER> {

    /**
     * Gets a {@link List} of {@link TagKey} entries for the object being wrapped, and whose generic types are {@code T}.
     * May be empty.
     *
     * @return A {@link List} of {@link TagKey} entries for the object being wrapped, or an empty {@link List} if no
     * tags are present.
     *
     * @see TagBasedPropertyWrapperBuilder#withTag(TagKey)
     * @see TagBasedPropertyWrapperBuilder#withTags(List)
     * @see TagBasedPropertyWrapperBuilder#withTags(TagKey...)
     */
    List<TagKey<T>> getObjectTags();

    /**
     * Gets a {@link List} of additional {@link TagKey} entries for the object being wrapped, and whose generic types are
     * ambiguous.
     * <br></br>
     * Useful for cases like adding tags to blocks, where they may require item tags to be added to them for proper
     * functionality too.
     *
     * @return A {@link List} of additional {@link TagKey} entries for the object being wrapped, or an empty {@link List}
     * if no tags are present.
     *
     * @implNote Some PW types don't have a need for implementing this. Thus, they may always return an empty {@link List}.
     *
     * @see TagBasedPropertyWrapperBuilder#withAdditionalTag(TagKey)
     * @see TagBasedPropertyWrapperBuilder#withAdditionalTags(List)
     * @see TagBasedPropertyWrapperBuilder#withAdditionalTags(TagKey...)
     */
    List<TagKey<?>> getAdditionalTags();
}
