package com.mememan.nexus.property_wrapper.def.mob_effect;

import com.mememan.nexus.property_wrapper.base.specialised.misc.DefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class MobEffectPropertyWrapper<E extends Enchantment> extends BaseDataGenPropertyWrapper<E, MobEffectPropertyWrapper<E>, MobEffectPropertyWrapperBuilder<E>> implements DefaultableBareDataGenPropertyWrapper<E, MobEffectPropertyWrapper<E>, MobEffectPropertyWrapperBuilder<E>> {
    protected final SpecializedLanguagePropertyWrapper<E, ?, ?> compositeLanguageWrapper;
    protected final SpecializedTagPropertyWrapper<E, ?, ?> compositeTagWrapper;

    public MobEffectPropertyWrapper(Supplier<E> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, MobEffectPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public MobEffectPropertyWrapper(@NotNull Supplier<E> parentObject, String modId) {
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
    public Optional<SpecializedLanguagePropertyWrapper<E, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapper<E, ?, ?>> getSpecializedTagWrapper() {
        return Optional.of(compositeTagWrapper);
    }
}
