package com.mememan.nexus.property_wrapper.impl.specialised.model;

import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized implementation of {@link ModelBasedPropertyWrapperBuilder}. Implements all model-related builder methods,
 * generic types, and default behaviour for model-based property wrapper handling.
 *
 * @see SpecializedModelPropertyWrapper
 */
public class SpecializedModelPropertyWrapperBuilder<T, SELF extends ModelBasedPropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends ModelBasedPropertyWrapper<T, MBPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, MBPW> implements ModelBasedPropertyWrapperBuilder<T, SELF, MBPW> {
    protected Function<Supplier<T>, ModelBasedPropertyWrapper.ModelDefinition> modelDefinitionsMapper;

    public SpecializedModelPropertyWrapperBuilder(@NotNull MBPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(MBPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .withModelDefinition(propertyWrapper.getModelDefinition().orElse(null));
    }

    @Override
    public SELF withModelDefinition(Function<Supplier<T>, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition) {
        this.modelDefinitionsMapper = modelDefinition;
        return self();
    }
}
