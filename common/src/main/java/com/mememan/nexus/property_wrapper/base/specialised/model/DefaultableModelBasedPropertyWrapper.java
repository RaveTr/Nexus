package com.mememan.nexus.property_wrapper.base.specialised.model;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapperBuilder;

import java.util.Optional;
import java.util.function.Function;

/**
 * Delegate extension for {@link ModelBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link ModelBasedPropertyWrapperBuilder}) using {@link #getSpecializedWrapper()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableModelBasedPropertyWrapper<T, SELF extends ModelBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedModelPropertyWrapperBuilder<T, BUILDER, SELF>> extends ModelBasedPropertyWrapper<T, SELF, BUILDER>, DefaultablePropertyWrapper<T, SELF, BUILDER, SpecializedModelPropertyWrapper<T, SELF, BUILDER>> {

    @Override
    default Optional<Function<T, ModelDefinition>> getModelDefinition() {
        return getSpecializedWrapper().flatMap(SpecializedModelPropertyWrapper::getModelDefinition);
    }
}
