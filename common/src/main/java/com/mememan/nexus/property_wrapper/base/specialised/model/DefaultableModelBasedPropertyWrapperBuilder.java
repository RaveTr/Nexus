package com.mememan.nexus.property_wrapper.base.specialised.model;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapperBuilder;

import java.util.function.Function;

/**
 * Delegate extension for {@link ModelBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedBuilder()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableModelBasedPropertyWrapperBuilder<T, SELF extends ModelBasedPropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends ModelBasedPropertyWrapper<T, MBPW, SELF>> extends ModelBasedPropertyWrapperBuilder<T, SELF, MBPW>, DefaultablePropertyWrapperBuilder<T, SELF, MBPW, SpecializedModelPropertyWrapperBuilder<T, SELF, MBPW>> {

    @Override
    default SELF withModelDefinition(Function<T, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition) {
        getSpecializedBuilder().ifPresent(builder -> builder.withModelDefinition(modelDefinition));
        return self();
    }
}
