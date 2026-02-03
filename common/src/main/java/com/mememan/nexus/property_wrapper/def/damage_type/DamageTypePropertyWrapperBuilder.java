package com.mememan.nexus.property_wrapper.def.damage_type;

import com.mememan.nexus.property_wrapper.impl.generic.DynamicPropertyWrapperBuilder;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

/**
 * Definite builder implementation for {@link DamageType} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}.
 *
 * @param <DT> Any {@link DamageType} type.
 *
 * @see DamageTypePropertyWrapper
 */
public class DamageTypePropertyWrapperBuilder<DT extends DamageType> extends DynamicPropertyWrapperBuilder<DT, DamageTypePropertyWrapperBuilder<DT>, DamageTypePropertyWrapper<DT>> {

    public DamageTypePropertyWrapperBuilder(@NotNull DamageTypePropertyWrapper<DT> ownerWrapper) {
        super(ownerWrapper);
    }
}
