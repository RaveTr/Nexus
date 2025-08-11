package com.mememan.nexus.property_wrapper.base;

import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;

import java.util.List;
import java.util.Map;

/**
 * Extension of {@link PropertyWrapperBuilder} that provides additional methods for Nexus data generation.
 * <br></br>
 * Note that this {@code interface} only provides generic builder methods for handling general cases, such as excluding
 * this PWB's owner PW from Nexus data generation, or manipulating how different {@linkplain ProviderType ProviderTypes}
 * interact with it.
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link DataGenPropertyWrapper}.
 *
 * @see DataGenPropertyWrapper
 */
public interface DataGenPropertyWrapperBuilder<T, SELF extends DataGenPropertyWrapperBuilder<T, SELF, DGPW>, DGPW extends DataGenPropertyWrapper<T, DGPW, SELF>> extends PropertyWrapperBuilder<T, SELF, DGPW> {

    /**
     * Determines whether this DGPWB instance should be entirely excluded from Nexus' native datagen.
     * <br></br>
     * Fundamentally, all this does is flag this instance as not needing a data entry to be mapped to it. You may
     * choose to generate data for it yourself if needed, since Nexus won't handle datagen for this particular object.
     * <br></br>
     * If an object-specific data provider has {@link ModDataProvider#validateAllEntries()} set to {@code true}, this
     * instance (and its children, so long as this value isn't modified) will still be excluded from datagen, and thus
     * an exception won't be thrown for it.
     *
     * @param excludeFromNativeDatagen Whether this instance's data should be excluded from Nexus' native datagen for
     *                                 data generation.
     *
     * @return {@code this} (builder method).
     *
     * @see #excludeFromNativeDatagen()
     * @see #requiresDatagenEntry(ProviderType, boolean)
     */
    DataGenPropertyWrapperBuilder<T, SELF, DGPW> excludeFromNativeDatagen(boolean excludeFromNativeDatagen);

    /**
     * Overloaded variant of {@link #excludeFromNativeDatagen(boolean)}. Sets the value of
     * {@link #excludeFromNativeDatagen(boolean)} to {@code true}.
     *
     * @return {@code this} (builder method).
     *
     * @see #excludeFromNativeDatagen(boolean)
     */
    default DataGenPropertyWrapperBuilder<T, SELF, DGPW> excludeFromNativeDatagen() {
        return excludeFromNativeDatagen(true);
    }

    /**
     * Determines whether this DGPWB instance is required to generate necessary object-related data based on the
     * {@link ProviderType} passed in.
     * <br></br>
     * By default, unmapped providers will not require an entry for this DGPWB to be generated unless
     * {@link ModDataProvider#validateAllEntries()} is set to {@code true}.
     * <br></br>
     * Mapping the related provider passed in here to {@code requiresDatagenEntry}, set to {@code true}, will flag
     * this DGPWB instance for requiring related data regardless of what {@link ModDataProvider#validateAllEntries()} is
     * set to.
     *
     * @param targetProviderType The {@link ProviderType} to modify the data entry requirement for.
     * @param requiresDatagenEntry Whether this DGPWB should require data related to each of the specified
     *                             {@code targetProviderTypes} to be present.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(Map)
     * @see #excludeFromNativeDatagen(boolean)
     * @see DataGenPropertyWrapper#getProviderTypeRequisites()
     */
    DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresDatagenEntry(ProviderType targetProviderType, boolean requiresDatagenEntry);

    /**
     * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
     * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}.
     *
     * @param targetProviderTypes The {@link List} of {@linkplain ProviderType ProviderTypes} to modify the data
     *                            entry requirements for.
     * @param requiresDatagenEntry Whether this DGPWB should require data related to each of the specified
     *                             {@code targetProviderTypes} to be present.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntry(ProviderType, boolean)
     * @see #requiresSetDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(Map)
     * @see #excludeFromNativeDatagen(boolean)
     * @see DataGenPropertyWrapper#getProviderTypeRequisites()
     */
    DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry);

    /**
     * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
     * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}. Overrides the existing
     * {@link Map}.
     *
     * @param targetProviderTypes The {@link List} of {@linkplain ProviderType ProviderTypes} to modify the data
     *                            entry requirements for.
     * @param requiresDatagenEntry Whether this DGPWB should require data related to each of the specified
     *                             {@code targetProviderTypes} to be present.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntry(ProviderType, boolean)
     * @see #requiresDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(Map)
     * @see #excludeFromNativeDatagen(boolean)
     * @see DataGenPropertyWrapper#getProviderTypeRequisites()
     */
    DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresSetDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry);

    /**
     * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
     * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}. Overrides the existing
     * {@link Map}.
     *
     * @param mappedProviderRequisites The {@link Map} of provider requisites to override the existing {@link Map}
     *                                 with.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntry(ProviderType, boolean)
     * @see #requiresDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(List, boolean)
     * @see #excludeFromNativeDatagen(boolean)
     * @see DataGenPropertyWrapper#getProviderTypeRequisites()
     */
    DataGenPropertyWrapperBuilder<T, SELF, DGPW> requiresSetDatagenEntries(Map<ProviderType, Boolean> mappedProviderRequisites);
}
