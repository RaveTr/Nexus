package com.mememan.nexus.property_wrapper.base.generic;

import java.util.Optional;

/**
 * Optional extension for {@link PropertyWrapper} that adds default method implementations via delegates to
 * {@link #getSpecializedWrapper()}. Primarily used to reduce boilerplate across PW implementations that may share
 * some, but not all, of the same methods/properties.
 *
 * @param <SPEC> Separate generic type that extends from the same type as {@code SELF} to allow for concrete/specialized
 *              implementation definition for use in {@link #getSpecializedWrapper()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultablePropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>, SPEC extends PropertyWrapper<T, SELF, BUILDER>> {

    /**
     * Gets the optional default implementation to be used for concrete delegates of wrapper methods for this PW.
     *
     * @return The optional default implementation to be used for concrete delegates of wrapper methods for this PW.
     * May be empty.
     *
     * @apiNote Inheritors/implementors should use this functionally (via {@link Optional} mapping methods) such that
     * methods are only called if the value is present.
     */
    Optional<SPEC> getSpecializedWrapper();
}
