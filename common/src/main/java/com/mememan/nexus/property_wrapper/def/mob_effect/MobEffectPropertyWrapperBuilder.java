package com.mememan.nexus.property_wrapper.def.mob_effect;

import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapperBuilder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public class MobEffectPropertyWrapperBuilder<ME extends MobEffect> extends BaseDefaultableBareDataGenPropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>> {

    public MobEffectPropertyWrapperBuilder(@NotNull MobEffectPropertyWrapper<ME> ownerWrapper) {
        super(ownerWrapper);
    }
}
