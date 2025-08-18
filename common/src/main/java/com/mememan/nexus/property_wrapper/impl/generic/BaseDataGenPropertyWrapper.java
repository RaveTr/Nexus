package com.mememan.nexus.property_wrapper.impl.generic;

import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base implementation of {@link DataGenPropertyWrapper}, extending from {@link BasePropertyWrapper} for default behavior.
 *
 * @see BaseDataGenPropertyWrapperBuilder
 */
public class BaseDataGenPropertyWrapper<T, SELF extends DataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends BaseDataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends BasePropertyWrapper<T, SELF, BUILDER> implements DataGenPropertyWrapper<T, SELF, BUILDER> {

    public BaseDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        super(parentObject, isTemplate, builderFactory);
    }

    public BaseDataGenPropertyWrapper(@NotNull Supplier<T> parentObject, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        super(parentObject, builderFactory);
    }

    public BaseDataGenPropertyWrapper(Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        super(builderFactory);
    }

    public BaseDataGenPropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        this(parentObject, isTemplate, BaseDataGenPropertyWrapperBuilder::new);
    }

    public BaseDataGenPropertyWrapper(@NotNull Supplier<T> parentObject) {
        this(parentObject, BaseDataGenPropertyWrapperBuilder::new);
    }

    public BaseDataGenPropertyWrapper() {
        this(BaseDataGenPropertyWrapperBuilder::new);
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
