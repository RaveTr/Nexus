package com.mememan.nexus.property_wrapper.def.enchantment;

import com.mememan.nexus.property_wrapper.base.specialised.misc.DefaultableBareDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnchantmentPropertyWrapperBuilder<E extends Enchantment> extends BaseDataGenPropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>> implements DefaultableBareDataGenPropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>> {
    protected final SpecializedLanguagePropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>> compositeLanguageBuilder;
    protected final SpecializedTagPropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>> compositeTagBuilder;

    public EnchantmentPropertyWrapperBuilder(@NotNull EnchantmentPropertyWrapper<E> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get();
        this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
    }

    @Override
    public EnchantmentPropertyWrapperBuilder<E> copyFrom(EnchantmentPropertyWrapper<E> propertyWrapper) {
        DefaultableBareDataGenPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>>> getSpecializedLanguageBuilder() {
        return Optional.empty();
    }

    @Override
    public Optional<SpecializedTagPropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>>> getSpecializedTagBuilder() {
        return Optional.empty();
    }
}
