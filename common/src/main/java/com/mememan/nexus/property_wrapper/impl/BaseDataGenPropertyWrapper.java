package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.DataGenPropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Base implementation of {@link DataGenPropertyWrapper}, extending from {@link BasePropertyWrapper} for default behavior.
 *
 * @see BaseDataGenPropertyWrapperBuilder
 */
public class BaseDataGenPropertyWrapper<T, SELF extends DataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends DataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends BasePropertyWrapper<T, SELF, BUILDER> implements DataGenPropertyWrapper<T, SELF, BUILDER> {

    public BaseDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        super(parentObject, isTemplate);
    }

    public BaseDataGenPropertyWrapper(@NotNull Supplier<T> parentObject) {
        super(parentObject);
    }

    public BaseDataGenPropertyWrapper() {
        super();
    }

    @Override
    public boolean isExcludedFromDataGen() {
        return rawBuilder().map(b -> b.excludeFromNativeDatagen).orElse(false);
    }

    @Override
    public Map<ProviderType, Boolean> getProviderTypeRequisites() {
        return rawBuilder().map(b -> b.providerTypeRequisites).orElse(Map.of());
    }
}
