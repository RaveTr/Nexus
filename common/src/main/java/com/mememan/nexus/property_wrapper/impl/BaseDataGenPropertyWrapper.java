package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.DataGenPropertyWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class BaseDataGenPropertyWrapper<T, SELF extends DataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends BaseDataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends BasePropertyWrapper<T, SELF, BUILDER> implements DataGenPropertyWrapper<T, SELF, BUILDER> {

    public BaseDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        super(parentObject, isTemplate);
    }

    public BaseDataGenPropertyWrapper(@NotNull Supplier<T> parentObject) {
        super(parentObject);
    }

    @Override
    public boolean isExcludedFromDataGen() {
        return builder().excludeFromNativeDatagen;
    }

    @Override
    public Map<ProviderType, Boolean> getProviderTypeRequisites() {
        return builder().providerTypeRequisites;
    }
}
