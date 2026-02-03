package com.mememan.nexus.property_wrapper.def.enchantment;

import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapperBuilder;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

/**
 * Definite builder implementation for {@link Enchantment} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}.
 *
 * @param <E> Any {@link Enchantment} type.
 *
 * @see EnchantmentPropertyWrapper
 */
public class EnchantmentPropertyWrapperBuilder<E extends Enchantment> extends BaseDefaultableBareDataGenPropertyWrapperBuilder<E, EnchantmentPropertyWrapperBuilder<E>, EnchantmentPropertyWrapper<E>> {

    public EnchantmentPropertyWrapperBuilder(@NotNull EnchantmentPropertyWrapper<E> ownerWrapper) {
        super(ownerWrapper);
    }
}
