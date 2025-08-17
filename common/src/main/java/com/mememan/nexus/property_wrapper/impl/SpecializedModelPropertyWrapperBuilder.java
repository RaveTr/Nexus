package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.ModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.ModelBasedPropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SpecializedModelPropertyWrapperBuilder<T, SELF extends ModelBasedPropertyWrapperBuilder<T, SELF, MPW>, MPW extends ModelBasedPropertyWrapper<T, MPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, MPW> implements ModelBasedPropertyWrapperBuilder<T, SELF, MPW> {

    public SpecializedModelPropertyWrapperBuilder(@NotNull MPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF withModelDefinition(Function<T, ModelBasedPropertyWrapper.ModelDefinition> modelDefinition) {
        return self();
    }
}
