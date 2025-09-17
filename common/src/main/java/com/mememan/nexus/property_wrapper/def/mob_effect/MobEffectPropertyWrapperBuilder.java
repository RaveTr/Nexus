package com.mememan.nexus.property_wrapper.def.mob_effect;

import com.mememan.nexus.property_wrapper.base.specialised.misc.DefaultableBareDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MobEffectPropertyWrapperBuilder<E extends Enchantment> extends BaseDataGenPropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>> implements DefaultableBareDataGenPropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>> {
    protected final SpecializedLanguagePropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>> compositeLanguageBuilder;
    protected final SpecializedTagPropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>> compositeTagBuilder;

    public MobEffectPropertyWrapperBuilder(@NotNull MobEffectPropertyWrapper<E> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get();
        this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
    }

    @Override
    public MobEffectPropertyWrapperBuilder<E> copyFrom(MobEffectPropertyWrapper<E> propertyWrapper) {
        DefaultableBareDataGenPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>>> getSpecializedLanguageBuilder() {
        return Optional.empty();
    }

    @Override
    public Optional<SpecializedTagPropertyWrapperBuilder<E, MobEffectPropertyWrapperBuilder<E>, MobEffectPropertyWrapper<E>>> getSpecializedTagBuilder() {
        return Optional.empty();
    }
}
