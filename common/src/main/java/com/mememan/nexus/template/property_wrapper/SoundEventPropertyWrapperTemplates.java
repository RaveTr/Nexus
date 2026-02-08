package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.property_wrapper.def.sound_event.SoundEventPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.sound_event.SoundEventPropertyWrapperBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link SoundEventPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class SoundEventPropertyWrapperTemplates {

    private SoundEventPropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (SoundEventPropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link SoundEvent}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param soundSupCol An optional {@link Collection} to track the registered {@link SoundEvent}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered sound events.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEvent(ResourceLocation soundId, Supplier<SE> soundSup, @Nullable Collection<Supplier<SoundEvent>> soundSupCol) {
        Supplier<SE> registeredSoundEvent = NexusServices.REGISTRAR.registerObject(soundId, soundSup, BuiltInRegistries.SOUND_EVENT);

        if (soundSupCol != null) soundSupCol.add((Supplier<SoundEvent>) registeredSoundEvent);

        return registeredSoundEvent;
    }

    /**
     * Overloaded variant of {@link #registerSoundEvent(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link SoundEvent} to any custom {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEvent(ResourceLocation soundId, Supplier<SE> soundSup) {
        return registerSoundEvent(soundId, soundSup, null);
    }

    /**
     * Registers and returns the provided {@link SoundEvent}, mapping it to a new {@link SoundEventPropertyWrapper} inheriting
     * from the provided {@link SoundEventPropertyWrapper} template. Optionally tracks the registered {@link SoundEvent} to a
     * custom {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     * @param soundSupCol An optional {@link Collection} to track the registered {@link SoundEvent}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered sound events.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}, mapped to its own {@link SoundEventPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEventFromTemplate(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW, @Nullable Collection<Supplier<SoundEvent>> soundSupCol) {
        Supplier<SE> registeredSoundEvent = registerSoundEvent(soundId, soundSup, soundSupCol);

        return new SoundEventPropertyWrapper<>(registeredSoundEvent, soundId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerSoundEventFromTemplate(ResourceLocation, Supplier, SoundEventPropertyWrapper, Collection)} that does not track the
     * registered {@link SoundEvent} to any custom {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}, mapped to its own {@link SoundEventPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEventFromTemplate(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW) {
        return registerSoundEventFromTemplate(soundId, soundSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link SoundEvent} and returns its {@link SoundEventPropertyWrapperBuilder} inheriting from the
     * provided {@link SoundEventPropertyWrapper} template. Optionally tracks the registered {@link SoundEvent} to a custom
     * {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     * @param soundSupCol An optional {@link Collection} to track the registered {@link SoundEvent}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered sound events.
     *
     * @return The {@link SoundEventPropertyWrapperBuilder} of the registered {@link SoundEvent}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> SoundEventPropertyWrapperBuilder<SE> registerAndChain(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW, @Nullable Collection<Supplier<SoundEvent>> soundSupCol) {
        Supplier<SE> registeredSoundEvent = registerSoundEvent(soundId, soundSup, soundSupCol);

        return new SoundEventPropertyWrapper<>(registeredSoundEvent, soundId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, SoundEventPropertyWrapper, Collection)} that does not track the
     * registered {@link SoundEvent} to any custom {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     *
     * @return The {@link SoundEventPropertyWrapperBuilder} of the registered {@link SoundEvent}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> SoundEventPropertyWrapperBuilder<SE> registerAndChain(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW) {
        return registerAndChain(soundId, soundSup, templateBPW, null);
    }

    /**
     * Registers and returns the provided {@link SoundEvent}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param soundSupCol An optional {@link Collection} to track the registered {@link SoundEvent}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered sound events.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEventAndReflect(ResourceLocation soundId, Supplier<SE> soundSup, @Nullable Collection<Supplier<SoundEvent>> soundSupCol) {
        Supplier<SE> registeredSoundEvent = NexusServices.REGISTRAR.registerObjectAndReflect(soundId, soundSup, BuiltInRegistries.SOUND_EVENT);

        if (soundSupCol != null) soundSupCol.add((Supplier<SoundEvent>) registeredSoundEvent);

        return registeredSoundEvent;
    }

    /**
     * Overloaded variant of {@link #registerSoundEventAndReflect(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link SoundEvent} to any custom {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEventAndReflect(ResourceLocation soundId, Supplier<SE> soundSup) {
        return registerSoundEventAndReflect(soundId, soundSup, null);
    }

    /**
     * Registers and returns the provided {@link SoundEvent}, mapping it to a new {@link SoundEventPropertyWrapper} inheriting
     * from the provided {@link SoundEventPropertyWrapper} template. Optionally tracks the registered {@link SoundEvent} to a
     * custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     * @param soundSupCol An optional {@link Collection} to track the registered {@link SoundEvent}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered sound events.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}, mapped to its own {@link SoundEventPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEventFromTemplateAndReflect(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW, @Nullable Collection<Supplier<SoundEvent>> soundSupCol) {
        Supplier<SE> registeredSoundEvent = registerSoundEventAndReflect(soundId, soundSup, soundSupCol);

        return new SoundEventPropertyWrapper<>(registeredSoundEvent, soundId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerSoundEventFromTemplateAndReflect(ResourceLocation, Supplier, SoundEventPropertyWrapper, Collection)} that does not track the
     * registered {@link SoundEvent} to any custom {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link SoundEvent}, mapped to its own {@link SoundEventPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> Supplier<SE> registerSoundEventFromTemplateAndReflect(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW) {
        return registerSoundEventFromTemplateAndReflect(soundId, soundSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link SoundEvent} and returns its {@link SoundEventPropertyWrapperBuilder} inheriting from the
     * provided {@link SoundEventPropertyWrapper} template. Optionally tracks the registered {@link SoundEvent} to a custom
     * {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     * @param soundSupCol An optional {@link Collection} to track the registered {@link SoundEvent}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered sound events.
     *
     * @return The {@link SoundEventPropertyWrapperBuilder} of the registered {@link SoundEvent}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> SoundEventPropertyWrapperBuilder<SE> registerAndReflectAndChain(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW, @Nullable Collection<Supplier<SoundEvent>> soundSupCol) {
        Supplier<SE> registeredSoundEvent = registerSoundEventAndReflect(soundId, soundSup, soundSupCol);

        return new SoundEventPropertyWrapper<>(registeredSoundEvent, soundId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, SoundEventPropertyWrapper, Collection)} that does not track the
     * registered {@link SoundEvent} to any custom {@link Collection}.
     *
     * @param soundId The target {@linkplain SoundEvent SoundEvent's} {@linkplain ResourceLocation registry ID}.
     * @param soundSup The {@link SoundEvent} object to register.
     * @param templateBPW The {@link SoundEventPropertyWrapper} template to inherit from.
     *
     * @return The {@link SoundEventPropertyWrapperBuilder} of the registered {@link SoundEvent}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <SE> Any {@link SoundEvent} type.
     */
    public static <SE extends SoundEvent> SoundEventPropertyWrapperBuilder<SE> registerAndReflectAndChain(ResourceLocation soundId, Supplier<SE> soundSup, SoundEventPropertyWrapper<SoundEvent> templateBPW) {
        return registerAndReflectAndChain(soundId, soundSup, templateBPW, null);
    }
}
