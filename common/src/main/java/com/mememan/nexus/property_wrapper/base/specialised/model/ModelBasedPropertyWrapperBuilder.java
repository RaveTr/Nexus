package com.mememan.nexus.property_wrapper.base.specialised.model;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapperBuilder} with builder methods tailored towards handling models for the
 * object being wrapped. Parent objects of implementors may have multiple associated model definitions.
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link ModelBasedPropertyWrapper}.
 *
 * @see ModelBasedPropertyWrapper
 */
public interface ModelBasedPropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends PropertyWrapper<T, MBPW, SELF>> extends DataGenPropertyWrapperBuilder<T, SELF, MBPW> {

    /**
     * Defines a custom mapping function representing the parent object's model definition, where the input is the parent
     * object.
     *
     * @param modelDefinition The model definition to associate with the parent object.
     *
     * @return {@link #self()} (builder method)
     */
    SELF withModelDefinition(Function<Supplier<T>, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition);
}
