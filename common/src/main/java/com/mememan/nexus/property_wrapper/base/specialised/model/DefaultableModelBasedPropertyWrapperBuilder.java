package com.mememan.nexus.property_wrapper.base.specialised.model;

import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapperBuilder;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link ModelBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedModelBuilder()}.
 *
 * @see DefaultableModelBasedPropertyWrapper
 */
public interface DefaultableModelBasedPropertyWrapperBuilder<T, SELF extends ModelBasedPropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends ModelBasedPropertyWrapper<T, MBPW, SELF>> extends ModelBasedPropertyWrapperBuilder<T, SELF, MBPW> {

    /**
     * The specialized builder to which all builder methods should delegate.
     *
     * @return The specialized builder to which all builder methods should delegate. May be empty.
     */
    Optional<SpecializedModelPropertyWrapperBuilder<T, SELF, MBPW>> getSpecializedModelBuilder();

    @Override
    default SELF withModelDefinition(Function<Supplier<T>, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition) {
        getSpecializedModelBuilder().ifPresent(builder -> builder.withModelDefinition(modelDefinition));
        return self();
    }

    @Override
    default SELF copyFrom(MBPW propertyWrapper) {
        getSpecializedModelBuilder().ifPresent(builder -> builder.copyFrom(propertyWrapper));
        return self();
    }
}
