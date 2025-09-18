package com.mememan.nexus.property_wrapper.def.enchantment;

import com.mememan.nexus.property_wrapper.base.generic.misc.DefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class EnchantmentPropertyWrapper<E extends Enchantment> extends BaseDefaultableBareDataGenPropertyWrapper<E, EnchantmentPropertyWrapper<E>, EnchantmentPropertyWrapperBuilder<E>> {

    public EnchantmentPropertyWrapper(Supplier<E> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, EnchantmentPropertyWrapperBuilder::new, modId);
    }

    public EnchantmentPropertyWrapper(@NotNull Supplier<E> parentObject, String modId) {
        super(parentObject, EnchantmentPropertyWrapperBuilder::new, modId);
    }

    public EnchantmentPropertyWrapper() {
        super(EnchantmentPropertyWrapperBuilder::new);
    }
}
