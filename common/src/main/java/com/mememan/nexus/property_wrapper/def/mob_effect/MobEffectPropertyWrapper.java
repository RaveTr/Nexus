package com.mememan.nexus.property_wrapper.def.mob_effect;

import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapper;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Definite wrapper implementation for {@link MobEffect} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}. Mainly serves as a convenient type wrapper.
 *
 * @param <ME> Any {@link MobEffect} type.
 *
 * @see MobEffectPropertyWrapperBuilder
 */
public class MobEffectPropertyWrapper<ME extends MobEffect> extends BaseDefaultableBareDataGenPropertyWrapper<ME, MobEffectPropertyWrapper<ME>, MobEffectPropertyWrapperBuilder<ME>> {

    public MobEffectPropertyWrapper(Supplier<ME> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, MobEffectPropertyWrapperBuilder::new, modId);
    }

    public MobEffectPropertyWrapper(@NotNull Supplier<ME> parentObject, String modId) {
        super(parentObject, MobEffectPropertyWrapperBuilder::new, modId);
    }

    public MobEffectPropertyWrapper() {
        super(MobEffectPropertyWrapperBuilder::new);
    }

    @Override
    public Optional<String> getDescriptionIdPrefix() {
        return Optional.of("effect");
    }
}
