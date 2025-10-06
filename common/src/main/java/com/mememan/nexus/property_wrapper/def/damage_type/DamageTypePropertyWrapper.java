package com.mememan.nexus.property_wrapper.def.damage_type;

import com.mememan.nexus.property_wrapper.impl.generic.DynamicPropertyWrapper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DamageTypePropertyWrapper<DT extends DamageType> extends DynamicPropertyWrapper<DT, DamageTypePropertyWrapper<DT>, DamageTypePropertyWrapperBuilder<DT>> {

    public DamageTypePropertyWrapper(Supplier<ResourceKey<DT>> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, DamageTypePropertyWrapperBuilder::new, modId);
    }

    public DamageTypePropertyWrapper(@NotNull Supplier<ResourceKey<DT>> parentObject, String modId) {
        super(parentObject, DamageTypePropertyWrapperBuilder::new, modId);
    }

    public DamageTypePropertyWrapper() {
        super(DamageTypePropertyWrapperBuilder::new);
    }
}
