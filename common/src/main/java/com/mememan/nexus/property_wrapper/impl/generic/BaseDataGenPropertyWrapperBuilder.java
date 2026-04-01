package com.mememan.nexus.property_wrapper.impl.generic;

import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Base implementation of {@link DataGenPropertyWrapperBuilder}. Extends from {@link BasePropertyWrapperBuilder} for
 * default behavior. Includes datagen-oriented builder methods.
 *
 * @see BaseDataGenPropertyWrapper
 */
public class BaseDataGenPropertyWrapperBuilder<T, SELF extends DataGenPropertyWrapperBuilder<T, SELF, DGPW>, DGPW extends DataGenPropertyWrapper<T, DGPW, SELF>> extends BasePropertyWrapperBuilder<T, SELF, DGPW> implements DataGenPropertyWrapperBuilder<T, SELF, DGPW> {
    protected boolean excludeFromNativeDatagen = false;
    protected final Map<ProviderType, Boolean> providerTypeRequisites = new Object2BooleanOpenHashMap<>();

    public BaseDataGenPropertyWrapperBuilder(@NotNull DGPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(DGPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .excludeFromNativeDatagen(propertyWrapper.isExcludedFromDataGen())
                .requiresSetDatagenEntries(propertyWrapper.getProviderTypeRequisites());
    }

    @Override
    public SELF excludeFromNativeDatagen(boolean excludeFromNativeDatagen) {
        this.excludeFromNativeDatagen = excludeFromNativeDatagen;
        return self();
    }

    @Override
    public SELF requiresDatagenEntry(ProviderType targetProviderType, boolean requiresDatagenEntry) {
        this.providerTypeRequisites.put(targetProviderType, requiresDatagenEntry);
        return self();
    }

    @Override
    public SELF requiresDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
        if (!targetProviderTypes.isEmpty()) targetProviderTypes.forEach(type -> providerTypeRequisites.put(type, requiresDatagenEntry));
        return self();
    }

    @Override
    public SELF requiresSetDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
        this.providerTypeRequisites.clear();
        if (!targetProviderTypes.isEmpty()) targetProviderTypes.forEach(type -> providerTypeRequisites.put(type, requiresDatagenEntry));
        return self();
    }

    @Override
    public SELF requiresSetDatagenEntries(Map<ProviderType, Boolean> mappedProviderRequisites) {
        this.providerTypeRequisites.clear();
        this.providerTypeRequisites.putAll(mappedProviderRequisites);
        return self();
    }
}
