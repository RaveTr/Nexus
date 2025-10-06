package com.mememan.nexus.property_wrapper.base.specialised.model;

import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapper;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link ModelBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link ModelBasedPropertyWrapperBuilder}) using {@link #getSpecializedModelWrapper()}.
 *
 * @see DefaultableModelBasedPropertyWrapperBuilder
 */
public interface DefaultableModelBasedPropertyWrapper<T, SELF extends ModelBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends ModelBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends ModelBasedPropertyWrapper<T, SELF, BUILDER> {

    /**
     * The specialized wrapper to which all getters should delegate.
     *
     * @return The specialized wrapper to which all getters should delegate. May be empty.
     */
    Optional<SpecializedModelPropertyWrapper<T, ?, ?>> getSpecializedModelWrapper();

    @Override
    default Optional<Function<Supplier<T>, ModelDefinition>> getModelDefinition() {
        return getSpecializedModelWrapper().flatMap(SpecializedModelPropertyWrapper::getModelDefinition);
    }
}
