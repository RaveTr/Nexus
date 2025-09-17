package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.mob_effect.MobEffectPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.function.Supplier;

@RegistrarEntry
public class TestMobEffectRegistrar {
    private static final ObjectArrayList<Supplier<? extends MobEffect>> MOB_EFFECTS = new ObjectArrayList<>();

    public static final Supplier<MobEffect> TEST_MOB_EFFECT = new MobEffectPropertyWrapper<>(registerMobEffect(NexusConstants.prefix("test_mob_effect"), () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0)), NexusConstants.MOD_ID)
            .builder()
            .buildAndGet();

    private static <ME extends MobEffect> Supplier<ME> registerMobEffect(ResourceLocation name, Supplier<ME> mobEffect) {
        Supplier<ME> registeredMobEffect = NexusServices.REGISTRAR.registerObject(name, mobEffect, BuiltInRegistries.MOB_EFFECT);
        MOB_EFFECTS.add(registeredMobEffect);
        return registeredMobEffect;
    }
}
