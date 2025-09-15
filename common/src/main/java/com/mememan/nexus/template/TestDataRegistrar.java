package com.mememan.nexus.template;

import com.google.common.collect.ImmutableList;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.damage_type.DamageTypePropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.function.Supplier;

@RegistrarEntry
public class TestDataRegistrar {
    private static final ObjectArrayList<Supplier<ResourceKey<DamageType>>> DAMAGE_TYPES = new ObjectArrayList<>();

    // Damage Types
    public static final Supplier<ResourceKey<DamageType>> THORNY_SUN = new DamageTypePropertyWrapper<>(registerDamageType("thorny_sun", () -> new DamageType("thorny_sun", 0.1F)), NexusConstants.MOD_ID)
            .builder()
            .withAdditionalTag(() -> DamageTypeTags.BYPASSES_ARMOR)
            .buildAndGet();

    public static final Supplier<ResourceKey<DamageType>> BIG_CARNIVOROUS_PLANT = new DamageTypePropertyWrapper<>(registerDamageType("big_carnivorous_plant", () -> new DamageType("big_carnivorous_plant", 0.1F)), NexusConstants.MOD_ID)
            .builder()
            .copyFrom(THORNY_SUN)
            .withAdditionalTag(() -> DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL)
            .buildAndGet();


    private static Supplier<ResourceKey<DamageType>> registerDamageType(ResourceLocation id, Supplier<DamageType> actualDamageTypeSup) {
        Supplier<ResourceKey<DamageType>> damageTypeSup = NexusServices.REGISTRAR.registerDatapackObject(id, b -> actualDamageTypeSup, Registries.DAMAGE_TYPE);
        DAMAGE_TYPES.add(damageTypeSup);
        return damageTypeSup;
    }

    private static Supplier<ResourceKey<DamageType>> registerDamageType(String id, Supplier<DamageType> actualDamageTypeSup) {
        return registerDamageType(NexusConstants.prefix(id), actualDamageTypeSup);
    }

    public static ImmutableList<Supplier<ResourceKey<DamageType>>> getDamageTypes() {
        return ImmutableList.copyOf(DAMAGE_TYPES);
    }
}
