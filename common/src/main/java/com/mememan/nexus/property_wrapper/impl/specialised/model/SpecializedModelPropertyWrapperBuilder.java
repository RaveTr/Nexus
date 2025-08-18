package com.mememan.nexus.property_wrapper.impl.specialised.model;

import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public class SpecializedModelPropertyWrapperBuilder<T, SELF extends ModelBasedPropertyWrapperBuilder<T, SELF, MBPW>, MBPW extends ModelBasedPropertyWrapper<T, MBPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, MBPW> implements ModelBasedPropertyWrapperBuilder<T, SELF, MBPW> {
    protected Optional<Function<T, ModelBasedPropertyWrapper.ModelDefinition>> modelDefinitionsMapper = Optional.empty();

    public SpecializedModelPropertyWrapperBuilder(@NotNull MBPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(MBPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .withModelDefinition(propertyWrapper.getModelDefinition().orElse(null));
    }

    @Override
    public SELF withModelDefinition(Function<T, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition) {
        this.modelDefinitionsMapper = Optional.ofNullable(modelDefinition);
        return self();
    }
}
