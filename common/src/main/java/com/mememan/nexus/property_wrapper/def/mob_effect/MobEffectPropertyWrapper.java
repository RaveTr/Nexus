package com.mememan.nexus.property_wrapper.def.mob_effect;

import com.mememan.nexus.property_wrapper.base.specialised.misc.DefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class MobEffectPropertyWrapper<ME extends MobEffect> extends BaseDataGenPropertyWrapper<ME, MobEffectPropertyWrapper<ME>, MobEffectPropertyWrapperBuilder<ME>> implements DefaultableBareDataGenPropertyWrapper<ME, MobEffectPropertyWrapper<ME>, MobEffectPropertyWrapperBuilder<ME>> {
    protected final SpecializedLanguagePropertyWrapper<ME, ?, ?> compositeLanguageWrapper;
    protected final SpecializedTagPropertyWrapper<ME, ?, ?> compositeTagWrapper;

    public MobEffectPropertyWrapper(Supplier<ME> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, MobEffectPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public MobEffectPropertyWrapper(@NotNull Supplier<ME> parentObject, String modId) {
        super(parentObject, MobEffectPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public MobEffectPropertyWrapper() {
        super(MobEffectPropertyWrapperBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    @Override
    public Optional<String> getDescriptionIdPrefix() {
        return Optional.of("effect");
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<ME, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapper<ME, ?, ?>> getSpecializedTagWrapper() {
        return Optional.of(compositeTagWrapper);
    }
}
