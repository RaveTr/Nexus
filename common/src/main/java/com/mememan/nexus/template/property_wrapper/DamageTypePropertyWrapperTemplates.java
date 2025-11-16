package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.damage_type.DamageTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.damage_type.DamageTypePropertyWrapperBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link DamageTypePropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class DamageTypePropertyWrapperTemplates {

    private DamageTypePropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (DamageTypePropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link DamageType} as a datapack object.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     * @param damageTypeKeySupCol An optional {@link Collection} to track the registered {@link ResourceKey} of the {@link DamageType}.
     *                            Primarily useful if you want a shorthand method of tracking your own registered damage types.
     *
     * @return The {@link Supplier} of the registered {@link ResourceKey} mapped to the {@link DamageType}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> Supplier<ResourceKey<DT>> registerDamageType(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup, @Nullable Collection<Supplier<ResourceKey<DamageType>>> damageTypeKeySupCol) {
        Supplier<ResourceKey<DT>> registeredDamageType = NexusServices.REGISTRAR.registerDatapackObject(damageTypeId, ctx -> damageTypeObjSup, Registries.DAMAGE_TYPE);

        if (damageTypeKeySupCol != null) damageTypeKeySupCol.add((Supplier<ResourceKey<DamageType>>) (Supplier<?>) registeredDamageType);

        return registeredDamageType;
    }

    /**
     * Overloaded variant of {@link #registerDamageType(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link ResourceKey} to any custom {@link Collection}.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     *
     * @return The {@link Supplier} of the registered {@link ResourceKey} mapped to the {@link DamageType}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> Supplier<ResourceKey<DT>> registerDamageType(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup) {
        return registerDamageType(damageTypeId, damageTypeObjSup, null);
    }

    /**
     * Registers and returns the provided {@link DamageType} as a datapack object, mapping it to a new {@link DamageTypePropertyWrapper}
     * inheriting from the provided {@link DamageTypePropertyWrapper} template. Optionally tracks the registered {@link ResourceKey}
     * to a custom {@link Collection}.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     * @param templateDTW The {@link DamageTypePropertyWrapper} template to inherit from.
     * @param damageTypeKeySupCol An optional {@link Collection} to track the registered {@link ResourceKey} of the {@link DamageType}.
     *                            Primarily useful if you want a shorthand method of tracking your own registered damage types.
     *
     * @return The {@link Supplier} of the registered {@link ResourceKey} mapped to the {@link DamageType}, inheriting from the provided {@code templateDTW}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> Supplier<ResourceKey<DT>> registerDamageTypeFromTemplate(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup, DamageTypePropertyWrapper<DamageType> templateDTW, @Nullable Collection<Supplier<ResourceKey<DamageType>>> damageTypeKeySupCol) {
        Supplier<ResourceKey<DT>> registeredDamageType = registerDamageType(damageTypeId, damageTypeObjSup, damageTypeKeySupCol);

        return new DamageTypePropertyWrapper<>(registeredDamageType, damageTypeId.getNamespace())
                .builder()
                .copyFrom((DamageTypePropertyWrapper<DT>) templateDTW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerDamageTypeFromTemplate(ResourceLocation, Supplier, DamageTypePropertyWrapper, Collection)} that does not track the
     * registered {@link ResourceKey} to any custom {@link Collection}.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     * @param templateDTW The {@link DamageTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link ResourceKey} mapped to the {@link DamageType}, inheriting from the provided {@code templateDTW}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> Supplier<ResourceKey<DT>> registerDamageTypeFromTemplate(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup, DamageTypePropertyWrapper<DamageType> templateDTW) {
        return registerDamageTypeFromTemplate(damageTypeId, damageTypeObjSup, templateDTW, null);
    }

    /**
     * Registers the provided {@link DamageType} as a datapack object and returns its {@link DamageTypePropertyWrapperBuilder}
     * inheriting from the provided {@link DamageTypePropertyWrapper} template. Optionally tracks the registered {@link ResourceKey}
     * to a custom {@link Collection}.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     * @param templateDTW The {@link DamageTypePropertyWrapper} template to inherit from.
     * @param damageTypeKeySupCol An optional {@link Collection} to track the registered {@link ResourceKey} of the {@link DamageType}.
     *                            Primarily useful if you want a shorthand method of tracking your own registered damage types.
     *
     * @return The {@link DamageTypePropertyWrapperBuilder} of the registered {@link DamageType}, inheriting from the provided {@code templateDTW}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> DamageTypePropertyWrapperBuilder<DT> registerAndChain(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup, DamageTypePropertyWrapper<DamageType> templateDTW, @Nullable Collection<Supplier<ResourceKey<DamageType>>> damageTypeKeySupCol) {
        Supplier<ResourceKey<DT>> registeredDamageType = registerDamageType(damageTypeId, damageTypeObjSup, damageTypeKeySupCol);

        return new DamageTypePropertyWrapper<>(registeredDamageType, damageTypeId.getNamespace())
                .builder()
                .copyFrom((DamageTypePropertyWrapper<DT>) templateDTW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, DamageTypePropertyWrapper, Collection)} that does not track the
     * registered {@link ResourceKey} to any custom {@link Collection}.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     * @param templateDTW The {@link DamageTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link DamageTypePropertyWrapperBuilder} of the registered {@link DamageType}, inheriting from the provided {@code templateDTW}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> DamageTypePropertyWrapperBuilder<DT> registerAndChain(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup, DamageTypePropertyWrapper<DamageType> templateDTW) {
        return registerAndChain(damageTypeId, damageTypeObjSup, templateDTW, null);
    }

    /**
     * Registers the provided {@link DamageType} as a datapack object and returns its {@link DamageTypePropertyWrapperBuilder}.
     * Optionally tracks the registered {@link ResourceKey} to a custom {@link Collection}.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     * @param damageTypeKeySupCol An optional {@link Collection} to track the registered {@link ResourceKey} of the {@link DamageType}.
     *                            Primarily useful if you want a shorthand method of tracking your own registered damage types.
     *
     * @return The {@link DamageTypePropertyWrapperBuilder} of the registered {@link DamageType}, chaining from its own {@link DamageTypePropertyWrapperBuilder}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> DamageTypePropertyWrapperBuilder<DT> registerAndChain(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup, @Nullable Collection<Supplier<ResourceKey<DamageType>>> damageTypeKeySupCol) {
        Supplier<ResourceKey<DT>> registeredDamageType = registerDamageType(damageTypeId, damageTypeObjSup, damageTypeKeySupCol);

        return new DamageTypePropertyWrapper<>(registeredDamageType, damageTypeId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link ResourceKey} to any custom {@link Collection}.
     *
     * @param damageTypeId The target {@linkplain DamageType DamageType's} {@linkplain ResourceLocation registry ID}.
     * @param damageTypeObjSup The {@link DamageType} object to register.
     *
     * @return The {@link DamageTypePropertyWrapperBuilder} of the registered {@link DamageType}, chaining from its own {@link DamageTypePropertyWrapperBuilder}.
     *
     * @param <DT> Any {@link DamageType} type.
     */
    public static <DT extends DamageType> DamageTypePropertyWrapperBuilder<DT> registerAndChain(ResourceLocation damageTypeId, Supplier<DT> damageTypeObjSup) {
        return registerAndChain(damageTypeId, damageTypeObjSup, (Collection<Supplier<ResourceKey<DamageType>>>) null);
    }
}
