package com.mememan.nexus.property_wrapper.base;

import com.mememan.nexus.damage_type.DamageTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.KoreDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.DynamicPropertyWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Base interface for Property Wrapper Builders.
 * <br></br>
 * PWBs are the objects used to configure and mutate different properties, then store them in their owning
 * {@link PropertyWrapper} instance. Extensions of this {@code interface} provide their own specific methods to allow for
 * different properties to be configured and stored.
 * <br></br>
 * This is only the base {@code interface}, so it isn't going to do much on its own. It only provides the core
 * {@code build()} method (with overloads) and standard generic type constraints.
 *
 * @implNote Some implementations that inherit from base classes share multiple different PWB types
 * (such as {@link KoreDataGenPropertyWrapperBuilder}) may override different builder methods and have them do nothing
 * if they are not applicable. Such methods should be annotated with {@linkplain Deprecated @Deprecated}.
 * <br></br>
 * An example of this would be {@link DamageTypePropertyWrapper}, which extends from {@link DynamicPropertyWrapper} (and
 * by extension, its builder extends from {@link KoreDataGenPropertyWrapperBuilder}). Obviously, damage types do not have
 * models, and thus any methods defined in {@link ModelBasedPropertyWrapperBuilder} are not applicable to it.
 *
 * @param <T> The object type being wrapped.
 * @param <SELF> Type reference generic for this PWB {@code interface}. Useful for implementations that implicitly require
 *               different generic types for their property wrappers.
 * @param <PW> The {@link PropertyWrapper} type being built, and whose generic type is {@code T}.
 *
 * @see PropertyWrapper
 */
public interface PropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, PW>, PW extends PropertyWrapper<T, PW, SELF>> {

    /**
     * Copies data from the provided {@link PropertyWrapper} instance into this builder. Overrides all existing data
     * for this builder.
     *
     * @param propertyWrapper The {@link PropertyWrapper} instance to copy data from.
     *
     * @return {@link #self()} (builder method)
     */
    SELF copyFrom(PW propertyWrapper);

    /**
     * Returns the owner {@link PropertyWrapper} instance of this builder.
     * <br></br>
     * This method also typically performs extra operations, such as mapping the owner PW to the parent object being
     * wrapped if it is not a template.
     *
     * @return The owner {@link PropertyWrapper} instance of this builder.
     *
     * @see PropertyWrapper#isTemplate()
     */
    PW build();

    /**
     * Shortcut overload for {@link PropertyWrapperBuilder#build()} that returns the parent object {@link Supplier}
     * wrapped by the owner {@link PropertyWrapper} instance of this builder.
     *
     * @return The parent object {@link Supplier} wrapped by the owner {@link PropertyWrapper} instance of this builder.
     *
     * @see PropertyWrapper#getParentObject()
     */
    @NotNull
    default Supplier<T> buildAndGet() {
        return build().getParentObject();
    }

    /**
     * Convenience method to return a safe type-casted reference to this builder.
     *
     * @return {@code (SELF) this} (builder method)
     */
    default SELF self() {
        return (SELF) this;
    }
}
