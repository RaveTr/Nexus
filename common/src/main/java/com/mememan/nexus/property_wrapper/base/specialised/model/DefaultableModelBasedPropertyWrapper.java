package com.mememan.nexus.property_wrapper.base.specialised.model;

import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapper;

import java.util.Optional;
import java.util.function.Function;

/**
 * Delegate extension for {@link ModelBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link ModelBasedPropertyWrapperBuilder}) using {@link #getSpecializedModelWrapper()}.
 *
 * @see DefaultableModelBasedPropertyWrapperBuilder
 */
public interface DefaultableModelBasedPropertyWrapper<T, SELF extends ModelBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends ModelBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends ModelBasedPropertyWrapper<T, SELF, BUILDER> {

    Optional<SpecializedModelPropertyWrapper<T, ?, ?>> getSpecializedModelWrapper();

    @Override
    default Optional<Function<T, ModelDefinition>> getModelDefinition() {
        return getSpecializedModelWrapper().flatMap(SpecializedModelPropertyWrapper::getModelDefinition);
    }
}
