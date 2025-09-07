package com.mememan.nexus.property_wrapper.base.generic;

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
 * @param <T> The object type being wrapped.
 * @param <SELF> Type reference generic for this PWB {@code interface}. Useful for implementations that implicitly require
 *               different generic types for their property wrappers.
 * @param <PW> The {@link PropertyWrapper} type being built, and whose generic type is {@code T}.
 *
 * @see PropertyWrapper
 */
public interface PropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, PW>, PW extends PropertyWrapper<T, PW, SELF>> extends Cloneable {

    /**
     * Copies data from the provided {@link PropertyWrapper} instance into this builder. Overrides all existing data
     * for this builder.
     *
     * @param propertyWrapper The {@link PropertyWrapper} instance to copy data from.
     *
     * @return {@link #self()} (builder method)
     *
     * @implSpec Implementations should ensure that properties are deep-copied rather than shallow-copied from the
     * provided {@code propertyWrapper} (e.g. instead of {@code #setSomeList(propertyWrapper.getSomeList())}, use
     * {@code #setSomeList(List.copyOf(propertyWrapper.getSomeList()))}).
     */
    SELF copyFrom(PW propertyWrapper);

    /**
     * Overloaded variant of {@link #copyFrom(PropertyWrapper)} that copies data from the {@link PropertyWrapper}
     * mapped to the provided {@link Supplier} (retrieved through {@link PropertyWrapper#getMappedPropertyWrappers()}).
     *
     * @param associatedPWObject The parent object mapped to another PW.
     *
     * @return {@link #copyFrom(PropertyWrapper)}.
     */
    default SELF copyFrom(Supplier<T> associatedPWObject) {
        return copyFrom((PW) PropertyWrapper.getMappedPropertyWrappers().get(associatedPWObject));
    }

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
     * Gets the current owner {@link PropertyWrapper} instance of this builder. Does not perform any extra operations.
     *
     * @return The current owner {@link PropertyWrapper} instance of this builder.
     */
    @NotNull
    PW getCurrentOwnerWrapper();

    /**
     * Safely clones this builder via {@link Object#clone()}. Useful if you need to create a copy of this builder rather
     * than copying from another one, but it is a bit more performance taxing.
     *
     * @return A deep copy of this builder instance (deep copying mostly depends on {@link #copyFrom(PropertyWrapper)}).
     */
    SELF clone();

    /**
     * Shortcut overload for {@link PropertyWrapperBuilder#build()} that returns the parent object {@link Supplier}
     * wrapped by the owner {@link PropertyWrapper} instance of this builder.
     *
     * @return The parent object {@link Supplier} wrapped by the owner {@link PropertyWrapper} instance of this builder.
     *
     * @see PropertyWrapper#getParentObject()
     *
     * @implNote Typically, if this PW is a template, this method will return {@code Suppliers#ofInstance(null)}.
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
