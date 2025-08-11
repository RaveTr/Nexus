package com.mememan.nexus.property_wrapper.base;

import java.util.List;
import java.util.function.Function;

/**
 * Extension of {@link DataGenPropertyWrapperBuilder} with builder methods tailored towards handling models for the
 * object being wrapped.
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link ModelBasedPropertyWrapper}.
 *
 * @see ModelBasedPropertyWrapper
 */
public interface ModelBasedPropertyWrapperBuilder<T, SELF extends ModelBasedPropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends ModelBasedPropertyWrapper<T, MBPW, SELF>> extends DataGenPropertyWrapperBuilder<T, SELF, MBPW> {

    /**
     * Defines a custom mapping function representing the parent object's model definition, where the input is the parent
     * object.
     *
     * @param modelDefinition The model definition to associate with the parent object.
     *
     * @return {@code this} (builder method)
     */
    ModelBasedPropertyWrapperBuilder<T, SELF, MBPW> withModelDefinition(Function<T, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition);

    /**
     * Defines a custom mapping function representing the parent object's model definitions, where the input is the
     * parent object.
     *
     * @param modelDefinitions The model definitions to associate with the parent object.
     *
     * @return {@code this} (builder method)
     */
    ModelBasedPropertyWrapperBuilder<T, SELF, MBPW> withModelDefinitions(Function<T, List<ModelBasedPropertyWrapper.ModelDefinition>> modelDefinitions);

    /**
     * Sets the parent object's model definition, where the input is the parent object.
     *
     * @param modelDefinitions The model definition to associate with the parent object.
     *
     * @return {@code this} (builder method)
     */
    ModelBasedPropertyWrapperBuilder<T, SELF, MBPW> setModelDefinitions(Function<T, List<ModelBasedPropertyWrapper.ModelDefinition>> modelDefinitions);
}
