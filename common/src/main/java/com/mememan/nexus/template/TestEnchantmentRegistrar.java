package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.template.property_wrapper.EnchantmentPropertyWrapperTemplates;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

@RegistrarEntry
public class TestEnchantmentRegistrar {
    private static final ObjectArrayList<Supplier<Enchantment>> ENCHANTMENTS = new ObjectArrayList<>();

    public static final Supplier<DamageEnchantment> TEST_ENCHANTMENT = EnchantmentPropertyWrapperTemplates.registerBasicEnchantment(NexusConstants.prefix("test_enchantment"), () -> new DamageEnchantment(Enchantment.Rarity.VERY_RARE, 0), ENCHANTMENTS);
}
