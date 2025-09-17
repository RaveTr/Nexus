package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.enchantment.EnchantmentPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

@RegistrarEntry
public class TestEnchantmentRegistrar {
    private static final ObjectArrayList<Supplier<? extends Enchantment>> ENCHANTMENTS = new ObjectArrayList<>();

    public static final Supplier<DamageEnchantment> TEST_ENCHANTMENT = new EnchantmentPropertyWrapper<>(registerEnchantment(NexusConstants.prefix("test_enchantment"), () -> new DamageEnchantment(Enchantment.Rarity.VERY_RARE, 0)), NexusConstants.MOD_ID)
            .builder()
            .buildAndGet();

    private static <E extends Enchantment> Supplier<E> registerEnchantment(ResourceLocation name, Supplier<E> enchantment) {
        Supplier<E> registeredEnchantment = NexusServices.REGISTRAR.registerObject(name, enchantment, BuiltInRegistries.ENCHANTMENT);
        ENCHANTMENTS.add(registeredEnchantment);
        return registeredEnchantment;
    }
}
