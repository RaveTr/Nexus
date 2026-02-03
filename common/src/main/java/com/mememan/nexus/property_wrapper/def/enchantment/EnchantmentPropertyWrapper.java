package com.mememan.nexus.property_wrapper.def.enchantment;

import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapper;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Definite wrapper implementation for {@link Enchantment} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}.
 *
 * @param <E> Any {@link Enchantment} type.
 *
 * @see EnchantmentPropertyWrapperBuilder
 */
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
