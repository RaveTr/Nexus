package com.mememan.nexus.property_wrapper;

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
 * @param <PW> The {@link PropertyWrapper} type being built, and whose generic type is {@code T}.
 */
public interface PropertyWrapperBuilder<T, PW extends PropertyWrapper<T, PW>> {

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
}
