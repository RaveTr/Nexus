package com.mememan.nexus.template;

import com.google.common.collect.ImmutableList;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.damage_type.DamageTypePropertyWrapper;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.platform.NexusServices;
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

    // Block
    public static final Supplier<ResourceKey<DamageType>> THORNY_SUN = DamageTypePropertyWrapper.create(registerDamageType("thorny_sun", () -> new DamageType("thorny_sun", 0.1F)))
            .builder()
            .withLocalizedDeathMessageComponent("%1$s was pricked to death by a Thorny Sun")
            .withTag(() -> DamageTypeTags.BYPASSES_ARMOR)
            .build()
            .getOwnerDamageType();

    public static final Supplier<ResourceKey<DamageType>> BIG_CARNIVOROUS_PLANT = DamageTypePropertyWrapper.create(registerDamageType("big_carnivorous_plant", () -> new DamageType("big_carnivorous_plant", 0.1F)))
            .builder()
            .withLocalizedDeathMessageComponent("%1$s was bitten to death by a Big Carnivorous Plant")
            .build()
            .getOwnerDamageType();


    private static Supplier<ResourceKey<DamageType>> registerConfiguredFeature(ResourceLocation id, Supplier<DamageType> actualDamageTypeSup) {
        Supplier<ResourceKey<DamageType>> damageTypeSup = NexusServices.REGISTRAR.registerDatapackObject(id, b -> actualDamageTypeSup, Registries.DAMAGE_TYPE);
        DAMAGE_TYPES.add(damageTypeSup);
        return damageTypeSup;
    }

    private static Supplier<ResourceKey<DamageType>> registerDamageType(String id, Supplier<DamageType> actualDamageTypeSup) {
        return registerConfiguredFeature(NexusConstants.prefix(id), actualDamageTypeSup);
    }

    public static ImmutableList<Supplier<ResourceKey<DamageType>>> getDamageTypes() {
        return ImmutableList.copyOf(DAMAGE_TYPES);
    }
}
