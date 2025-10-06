package com.mememan.nexus.property_wrapper.def.enchantment;

import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapperBuilder;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantmentPropertyWrapperBuilder<E extends Enchantment> extends BaseDefaultableBareDataGenPropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>> {

    public EnchantmentPropertyWrapperBuilder(@NotNull EnchantmentPropertyWrapper<E> ownerWrapper) {
        super(ownerWrapper);
    }
}
