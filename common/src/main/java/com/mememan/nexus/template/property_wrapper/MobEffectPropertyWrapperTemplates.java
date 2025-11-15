package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.property_wrapper.def.mob_effect.MobEffectPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.mob_effect.MobEffectPropertyWrapperBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link MobEffectPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class MobEffectPropertyWrapperTemplates {

    private MobEffectPropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (MobEffectPropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link MobEffect}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param effectSupCol An optional {@link Collection} to track the registered {@link MobEffect}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered mob effects.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffect(ResourceLocation effectId, Supplier<E> effectSup, @Nullable Collection<Supplier<MobEffect>> effectSupCol) {
        Supplier<E> registeredMobEffect = NexusServices.REGISTRAR.registerObject(effectId, effectSup, BuiltInRegistries.MOB_EFFECT);

        if (effectSupCol != null) effectSupCol.add((Supplier<MobEffect>) registeredMobEffect);

        return registeredMobEffect;
    }

    /**
     * Overloaded variant of {@link #registerMobEffect(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link MobEffect} to any custom {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffect(ResourceLocation effectId, Supplier<E> effectSup) {
        return registerMobEffect(effectId, effectSup, null);
    }

    /**
     * Registers and returns the provided {@link MobEffect}, mapping it to a new {@link MobEffectPropertyWrapper} inheriting
     * from the provided {@link MobEffectPropertyWrapper} template. Optionally tracks the registered {@link MobEffect} to a
     * custom {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     * @param effectSupCol An optional {@link Collection} to track the registered {@link MobEffect}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered mob effects.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}, mapped to its own {@link MobEffectPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffectFromTemplate(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW, @Nullable Collection<Supplier<MobEffect>> effectSupCol) {
        Supplier<E> registeredMobEffect = registerMobEffect(effectId, effectSup, effectSupCol);

        return new MobEffectPropertyWrapper<>(registeredMobEffect, effectId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerMobEffectFromTemplate(ResourceLocation, Supplier, MobEffectPropertyWrapper, Collection)} that does not track the
     * registered {@link MobEffect} to any custom {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}, mapped to its own {@link MobEffectPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffectFromTemplate(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW) {
        return registerMobEffectFromTemplate(effectId, effectSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link MobEffect} and returns its {@link MobEffectPropertyWrapperBuilder} inheriting from the
     * provided {@link MobEffectPropertyWrapper} template. Optionally tracks the registered {@link MobEffect} to a custom
     * {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     * @param effectSupCol An optional {@link Collection} to track the registered {@link MobEffect}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered mob effects.
     *
     * @return The {@link MobEffectPropertyWrapperBuilder} of the registered {@link MobEffect}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> MobEffectPropertyWrapperBuilder<E> registerAndChain(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW, @Nullable Collection<Supplier<MobEffect>> effectSupCol) {
        Supplier<E> registeredMobEffect = registerMobEffect(effectId, effectSup, effectSupCol);

        return new MobEffectPropertyWrapper<>(registeredMobEffect, effectId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, MobEffectPropertyWrapper, Collection)} that does not track the
     * registered {@link MobEffect} to any custom {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     *
     * @return The {@link MobEffectPropertyWrapperBuilder} of the registered {@link MobEffect}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> MobEffectPropertyWrapperBuilder<E> registerAndChain(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW) {
        return registerAndChain(effectId, effectSup, templateBPW, null);
    }

    /**
     * Registers and returns the provided {@link MobEffect}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param effectSupCol An optional {@link Collection} to track the registered {@link MobEffect}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered mob effects.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffectAndReflect(ResourceLocation effectId, Supplier<E> effectSup, @Nullable Collection<Supplier<MobEffect>> effectSupCol) {
        Supplier<E> registeredMobEffect = NexusServices.REGISTRAR.registerObjectAndReflect(effectId, effectSup, BuiltInRegistries.MOB_EFFECT);

        if (effectSupCol != null) effectSupCol.add((Supplier<MobEffect>) registeredMobEffect);

        return registeredMobEffect;
    }

    /**
     * Overloaded variant of {@link #registerMobEffectAndReflect(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link MobEffect} to any custom {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffectAndReflect(ResourceLocation effectId, Supplier<E> effectSup) {
        return registerMobEffectAndReflect(effectId, effectSup, null);
    }

    /**
     * Registers and returns the provided {@link MobEffect}, mapping it to a new {@link MobEffectPropertyWrapper} inheriting
     * from the provided {@link MobEffectPropertyWrapper} template. Optionally tracks the registered {@link MobEffect} to a
     * custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     * @param effectSupCol An optional {@link Collection} to track the registered {@link MobEffect}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered mob effects.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}, mapped to its own {@link MobEffectPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffectFromTemplateAndReflect(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW, @Nullable Collection<Supplier<MobEffect>> effectSupCol) {
        Supplier<E> registeredMobEffect = registerMobEffectAndReflect(effectId, effectSup, effectSupCol);

        return new MobEffectPropertyWrapper<>(registeredMobEffect, effectId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerMobEffectFromTemplateAndReflect(ResourceLocation, Supplier, MobEffectPropertyWrapper, Collection)} that does not track the
     * registered {@link MobEffect} to any custom {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link MobEffect}, mapped to its own {@link MobEffectPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> Supplier<E> registerMobEffectFromTemplateAndReflect(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW) {
        return registerMobEffectFromTemplateAndReflect(effectId, effectSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link MobEffect} and returns its {@link MobEffectPropertyWrapperBuilder} inheriting from the
     * provided {@link MobEffectPropertyWrapper} template. Optionally tracks the registered {@link MobEffect} to a custom
     * {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     * @param effectSupCol An optional {@link Collection} to track the registered {@link MobEffect}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered mob effects.
     *
     * @return The {@link MobEffectPropertyWrapperBuilder} of the registered {@link MobEffect}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> MobEffectPropertyWrapperBuilder<E> registerAndReflectAndChain(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW, @Nullable Collection<Supplier<MobEffect>> effectSupCol) {
        Supplier<E> registeredMobEffect = registerMobEffectAndReflect(effectId, effectSup, effectSupCol);

        return new MobEffectPropertyWrapper<>(registeredMobEffect, effectId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, MobEffectPropertyWrapper, Collection)} that does not track the
     * registered {@link MobEffect} to any custom {@link Collection}.
     *
     * @param effectId The target {@linkplain MobEffect MobEffect's} {@linkplain ResourceLocation registry ID}.
     * @param effectSup The {@link MobEffect} object to register.
     * @param templateBPW The {@link MobEffectPropertyWrapper} template to inherit from.
     *
     * @return The {@link MobEffectPropertyWrapperBuilder} of the registered {@link MobEffect}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link MobEffect} type.
     */
    public static <E extends MobEffect> MobEffectPropertyWrapperBuilder<E> registerAndReflectAndChain(ResourceLocation effectId, Supplier<E> effectSup, MobEffectPropertyWrapper<MobEffect> templateBPW) {
        return registerAndReflectAndChain(effectId, effectSup, templateBPW, null);
    }
}
