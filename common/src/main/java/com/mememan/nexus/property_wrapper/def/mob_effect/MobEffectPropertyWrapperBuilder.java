package com.mememan.nexus.property_wrapper.def.mob_effect;

import com.mememan.nexus.property_wrapper.base.specialised.misc.DefaultableBareDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MobEffectPropertyWrapperBuilder<ME extends MobEffect> extends BaseDataGenPropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>> implements DefaultableBareDataGenPropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>> {
    protected final SpecializedLanguagePropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>> compositeLanguageBuilder;
    protected final SpecializedTagPropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>> compositeTagBuilder;

    public MobEffectPropertyWrapperBuilder(@NotNull MobEffectPropertyWrapper<ME> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get();
        this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
    }

    @Override
    public MobEffectPropertyWrapperBuilder<ME> copyFrom(MobEffectPropertyWrapper<ME> propertyWrapper) {
        DefaultableBareDataGenPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>>> getSpecializedLanguageBuilder() {
        return Optional.empty();
    }

    @Override
    public Optional<SpecializedTagPropertyWrapperBuilder<ME, MobEffectPropertyWrapperBuilder<ME>, MobEffectPropertyWrapper<ME>>> getSpecializedTagBuilder() {
        return Optional.empty();
    }
}
