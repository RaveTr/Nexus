package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.enchantment.EnchantmentPropertyWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link EnchantmentPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class EnchantmentPropertyWrapperTemplates {

    private EnchantmentPropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (EnchantmentPropertyWrapperTemplates)");
    }

    public static <E extends Enchantment> Supplier<E> registerEnchantment(ResourceLocation enchId, Supplier<E> enchSup, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = NexusServices.REGISTRAR.registerObject(enchId, enchSup, BuiltInRegistries.ENCHANTMENT);

        if (enchSupCol != null) enchSupCol.add((Supplier<Enchantment>) registeredEnchantment);

        return registeredEnchantment;
    }

    public static <E extends Enchantment> Supplier<E> registerEnchantment(ResourceLocation enchId, Supplier<E> enchSup) {
        return registerEnchantment(enchId, enchSup, null);
    }

    public static <E extends Enchantment> Supplier<E> registerBasicEnchantment(ResourceLocation enchId, Supplier<E> enchSup, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = registerEnchantment(enchId, enchSup, enchSupCol);

        return new EnchantmentPropertyWrapper<>(registeredEnchantment, enchId.getNamespace())
                .builder()
                .buildAndGet();
    }

    public static <E extends Enchantment> Supplier<E> registerBasicEnchantment(ResourceLocation enchId, Supplier<E> enchSup) {
        return registerBasicEnchantment(enchId, enchSup, null);
    }
}
