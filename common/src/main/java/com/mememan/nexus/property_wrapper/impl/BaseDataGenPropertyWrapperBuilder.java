package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.DataGenPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class BaseDataGenPropertyWrapperBuilder<T, SELF extends DataGenPropertyWrapperBuilder<T, SELF, DGPW>, DGPW extends DataGenPropertyWrapper<T, DGPW, SELF>> extends BasePropertyWrapperBuilder<T, SELF, DGPW> implements DataGenPropertyWrapperBuilder<T, SELF, DGPW> {
    protected boolean excludeFromNativeDatagen = false;
    protected final Map<ProviderType, Boolean> providerTypeRequisites = new Object2BooleanOpenHashMap<>();

    public BaseDataGenPropertyWrapperBuilder(@NotNull DGPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public DataGenPropertyWrapperBuilder<T, SELF, DGPW> copyFrom(DGPW propertyWrapper) {
        return this
                .excludeFromNativeDatagen(propertyWrapper.isExcludedFromDataGen())
                .requiresSetDatagenEntries(propertyWrapper.getProviderTypeRequisites());
    }

    @Override
    public DataGenPropertyWrapperBuilder<T, SELF, DGPW> excludeFromNativeDatagen(boolean excludeFromNativeDatagen) {
        this.excludeFromNativeDatagen = excludeFromNativeDatagen;
        return this;
    }

    @Override
    public DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresDatagenEntry(ProviderType targetProviderType, boolean requiresDatagenEntry) {
        this.providerTypeRequisites.put(targetProviderType, requiresDatagenEntry);
        return this;
    }

    @Override
    public DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
        if (!targetProviderTypes.isEmpty()) targetProviderTypes.forEach(type -> providerTypeRequisites.put(type, requiresDatagenEntry));
        return this;
    }

    @Override
    public DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresSetDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
        this.providerTypeRequisites.clear();
        if (!targetProviderTypes.isEmpty()) targetProviderTypes.forEach(type -> providerTypeRequisites.put(type, requiresDatagenEntry));
        return this;
    }

    @Override
    public DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresSetDatagenEntries(Map<ProviderType, Boolean> mappedProviderRequisites) {
        this.providerTypeRequisites.clear();
        this.providerTypeRequisites.putAll(mappedProviderRequisites);
        return this;
    }
}
