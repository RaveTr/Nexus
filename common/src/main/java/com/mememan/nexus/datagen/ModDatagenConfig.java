package com.mememan.nexus.datagen;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Basic data-holding {@code record} used to store information regarding the general parameters under which data generation
 * should take place for a given mod by its mod ID.
 * <br></br>
 * Only useful for Nexus API datagen. External implementations utilising this object are left to the end-developer.
 * Needed in order to allow Nexus API to generate data for any given mod.
 *
 * @param modId The parent mod ID under which the specified data should be configured/stored.
 * @param enableDatagen Whether Nexus API should generate any data for the specified mod ID. If disabled, both native
 *                      Nexus datagen (for the specified {@code modId}) and providers added through Nexus API under the
 *                      specified modId are disabled.
 * @param mappedDupeStrats A {@link Map} of {@link DuplicateDataPolicy} to abide by when generating data. See the {@code enum}
 *                         itself for more info. Defaults to {@link DuplicateDataPolicy#CRASH} for all providers.
 * @param providerTypesToFullyValidate A {@link Set} of {@linkplain ProviderType ProviderTypes} for which all entries under
 *                                     the parent mod ID should be validated (i.e. checked for presence).
 * @param disabledProviderTypes A {@link Set} of {@linkplain ProviderType ProviderTypes} to be excluded by Nexus API
 *                              from datagen.
 */
public record ModDatagenConfig(@NotNull String modId, boolean enableDatagen, Map<ProviderType, DuplicateDataPolicy> mappedDupeStrats, Set<ProviderType> providerTypesToFullyValidate, Set<ProviderType> disabledProviderTypes) {

    /**
     * Helper factory method for creating a {@link ModDatagenConfig} object that runs all provider types for the
     * specified {@code modId} using default configurations.
     *
     * @param modId The parent mod ID under which the specified data should be configured/stored.
     *
     * @return A {@link ModDatagenConfig} object that runs all provider types for the specified {@code modId} using
     * default configurations.
     */
    public static ModDatagenConfig defaultConfig(String modId) {
        return new ModDatagenConfig(modId, true, Map.of(), Set.of(), Set.of());
    }
}
