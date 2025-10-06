package com.mememan.nexus.property_wrapper.base.specialised.tag;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.function.Supplier;

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
     * @apiNote If you're getting compile-time errors due to the oddities of Java generic type invariance, consider
     * using {@link #withAdditionalTag(Supplier)} (and/or its overloads) instead.
     *
     * @see #withTags(List)
     * @see #withTags(Supplier[])
     * @see #setTags(List)
     */
    SELF withTag(Supplier<TagKey<? super T>> targetTag);

    /**
     * Appends multiple tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote If you're getting compile-time errors due to the oddities of Java generic type invariance, consider
     * using {@link #withAdditionalTags(List)} (and/or its overloads) instead.
     *
     * @see #withTag(Supplier)
     * @see #withTags(Supplier[])
     * @see #setTags(List)
     */
    SELF withTags(List<Supplier<TagKey<? super T>>> targetTags);

    /**
     * Appends multiple tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote If you're getting compile-time errors due to the oddities of Java generic type invariance, consider
     * using {@link #withAdditionalTags(Supplier[])} (and/or its overloads) instead.
     *
     * @see #withTag(Supplier)
     * @see #withTags(List)
     * @see #setTags(List)
     */
    default SELF withTags(Supplier<TagKey<? super T>>... targetTags) {
        return withTags(ObjectArrayList.of(targetTags));
    }

    /**
     * Overrides the tags of the object being wrapped with the given tags.
     *
     * @param targetTags The tags to set.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote If you're getting compile-time errors due to the oddities of Java generic type invariance, consider
     * using {@link #setAdditionalTags(List)} (and/or its overloads) instead.
     *
     * @see #withTag(Supplier)
     * @see #withTags(List)
     * @see #withTags(Supplier[])
     */
    SELF setTags(List<Supplier<TagKey<? super T>>> targetTags);

    /**
     * Appends an additional tag to the object being wrapped.
     *
     * @param targetTag The tag to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types may not have a need for implementing this.
     *
     * @see #withAdditionalTags(List)
     * @see #withAdditionalTags(Supplier[])
     * @see #setAdditionalTags(List)
     */
    SELF withAdditionalTag(Supplier<TagKey<?>> targetTag);

    /**
     * Appends multiple additional tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types may not have a need for implementing this.
     *
     * @see #withAdditionalTag(Supplier)
     * @see #withAdditionalTags(Supplier[])
     * @see #setAdditionalTags(List)
     */
    SELF withAdditionalTags(List<Supplier<TagKey<?>>> targetTags);

    /**
     * Appends multiple additional tags to the object being wrapped.
     *
     * @param targetTags The tags to append.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types may not have a need for implementing this.
     *
     * @see #withAdditionalTag(Supplier)
     * @see #withAdditionalTags(List)
     * @see #setAdditionalTags(List)
     */
    default SELF withAdditionalTags(Supplier<TagKey<?>>... targetTags) {
        return withAdditionalTags(ObjectArrayList.of(targetTags));
    }

    /**
     * Overrides the additional tags of the object being wrapped with the given tags.
     *
     * @param targetTags The tags to set.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote Additional tags are used for cases like adding tags to blocks, where they may require item tags to be
     * added to them for proper functionality too. Some PW types may not have a need for implementing this.
     *
     * @see #withAdditionalTag(Supplier)
     * @see #withAdditionalTags(List)
     * @see #withAdditionalTags(Supplier[])
     */
    SELF setAdditionalTags(List<Supplier<TagKey<?>>> targetTags);
}
