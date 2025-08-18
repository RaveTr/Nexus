package com.mememan.nexus.property_wrapper.base.generic;

import java.util.Optional;

/**
 * Optional extension for {@link PropertyWrapperBuilder} that adds default method implementations via delegates to
 * {@link #getSpecializedBuilder()}. Primarily used to reduce boilerplate across PWB implementations that may share
 * some, but not all, of the same methods/properties.
 *
 * @param <SPEC> Separate generic type that extends from the same type as {@code SELF} to allow for concrete/specialized
 *              implementation definition for use in {@link #getSpecializedBuilder()}.
 */
public interface DefaultablePropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, PW>, PW extends PropertyWrapper<T, PW, SELF>, SPEC extends PropertyWrapperBuilder<T, SELF, PW>> extends PropertyWrapperBuilder<T, SELF, PW> {

    /**
     * Gets the optional default implementation to be used for concrete delegates of builder methods for this PWB.
     *
     * @return The optional default implementation to be used for concrete delegates of builder methods for this PWB.
     * May be empty.
     *
     * @apiNote Inheritors/implementors should use this functionally (via {@link Optional} mapping methods) such that
     * methods are only called if the value is present.
     */
    Optional<SPEC> getSpecializedBuilder();
}
