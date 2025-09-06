package com.mememan.nexus.property_wrapper.base.generic;

import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Extension of {@link PropertyWrapper} that provides additional getters for Nexus data generation.
 * <br></br>
 * Note that this {@code interface} only provides generic getters for handling general cases, such as excluding this
 * PW from Nexus data generation, or manipulating how different {@linkplain ProviderType ProviderTypes} interact with
 * it.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link DataGenPropertyWrapperBuilder}.
 *
 * @see DataGenPropertyWrapperBuilder
 */
public interface DataGenPropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> extends PropertyWrapper<T, SELF, BUILDER> {

    /**
     * Gets the description ID for the object being wrapped. Used for the object's key during automatic localization,
     * logging, and general identification.
     *
     * @return The description ID for the object being wrapped.
     */
    @NotNull
    String getObjectDescriptionId();

    /**
     * Whether this DGPW instance should be excluded from Nexus data generation entirely.
     * <br></br>
     * This takes precedence over {@link #getProviderTypeRequisites()} in determining whether a DGPW instance contain
     *
     * @return Whether this DGPW instance should be excluded from Nexus data generation entirely.
     *
     * @see #getProviderTypeRequisites()
     * @see DataGenPropertyWrapperBuilder#excludeFromNativeDatagen(boolean)
     */
    boolean isExcludedFromDataGen();

    /**
     * Gets a {@link Map} (usually {@link Object2BooleanOpenHashMap}) specifying the {@linkplain ProviderType ProviderTypes}
     * for which this DGPW instance requires data to present for generation. May be empty if {@link #builder} is {@code null}
     * or the underlying {@link Map} is also empty.
     * <br></br>
     * This method also takes precedence over {@link ModDataProvider#validateAllEntries()}.
     *
     * @return The {@link Map} representing different {@linkplain ProviderType ProviderTypes} and their requirements for
     * datagen. May be empty.
     *
     * @see #isExcludedFromDataGen()
     * @see DataGenPropertyWrapperBuilder#requiresDatagenEntry(ProviderType, boolean)
     */
    Map<ProviderType, Boolean> getProviderTypeRequisites();

    default Optional<ResourceKey<Registry<? super T>>> getObjectRegistryKey() {
        return Optional.empty();
    }

    static <T> Optional<ResourceKey<Registry<? super T>>> ofRegistryKey(ResourceKey<?> registryKey) {
        return Optional.of((ResourceKey<Registry<? super T>>) registryKey);
    }
}
