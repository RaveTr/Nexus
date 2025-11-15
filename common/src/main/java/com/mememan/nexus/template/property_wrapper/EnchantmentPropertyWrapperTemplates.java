package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.property_wrapper.def.enchantment.EnchantmentPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.enchantment.EnchantmentPropertyWrapperBuilder;
import net.minecraft.core.Registry;
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

    /**
     * Registers and returns the provided {@link Enchantment}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param enchSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantment(ResourceLocation enchId, Supplier<E> enchSup, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = NexusServices.REGISTRAR.registerObject(enchId, enchSup, BuiltInRegistries.ENCHANTMENT);

        if (enchSupCol != null) enchSupCol.add((Supplier<Enchantment>) registeredEnchantment);

        return registeredEnchantment;
    }

    /**
     * Overloaded variant of {@link #registerEnchantment(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantment(ResourceLocation enchId, Supplier<E> enchSup) {
        return registerEnchantment(enchId, enchSup, null);
    }

    /**
     * Registers and returns the provided {@link Enchantment}, mapping it to a new {@link EnchantmentPropertyWrapper} inheriting
     * from the provided {@link EnchantmentPropertyWrapper} template. Optionally tracks the registered {@link Enchantment} to a
     * custom {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     * @param enchSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}, mapped to its own {@link EnchantmentPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantmentFromTemplate(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = registerEnchantment(enchId, enchSup, enchSupCol);

        return new EnchantmentPropertyWrapper<>(registeredEnchantment, enchId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerEnchantmentFromTemplate(ResourceLocation, Supplier, EnchantmentPropertyWrapper, Collection)} that does not track the
     * registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}, mapped to its own {@link EnchantmentPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantmentFromTemplate(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW) {
        return registerEnchantmentFromTemplate(enchId, enchSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Enchantment} and returns its {@link EnchantmentPropertyWrapperBuilder} inheriting from the
     * provided {@link EnchantmentPropertyWrapper} template. Optionally tracks the registered {@link Enchantment} to a custom
     * {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     * @param enchSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> EnchantmentPropertyWrapperBuilder<E> registerAndChain(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = registerEnchantment(enchId, enchSup, enchSupCol);

        return new EnchantmentPropertyWrapper<>(registeredEnchantment, enchId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, EnchantmentPropertyWrapper, Collection)} that does not track the
     * registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> EnchantmentPropertyWrapperBuilder<E> registerAndChain(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW) {
        return registerAndChain(enchId, enchSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Enchantment} and returns its {@link EnchantmentPropertyWrapperBuilder}. Optionally tracks the
     * registered {@link Enchantment} to a custom {@link Collection}.
     *
     * @param enchantmentId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchantmentSup The {@link Enchantment} object to register.
     * @param enchantmentSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily
     *                          useful if you want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, chaining from its own
     * {@link EnchantmentPropertyWrapperBuilder}.
     *
     * @param <B> Any {@link Enchantment} type.
     */
    public static <B extends Enchantment> EnchantmentPropertyWrapperBuilder<B> registerAndChain(ResourceLocation enchantmentId, Supplier<B> enchantmentSup, @Nullable Collection<Supplier<Enchantment>> enchantmentSupCol) {
        Supplier<B> registeredEnchantment = registerEnchantment(enchantmentId, enchantmentSup, enchantmentSupCol);

        return new EnchantmentPropertyWrapper<>(registeredEnchantment, enchantmentId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, EnchantmentPropertyWrapper, Collection)}
     * that does not track the registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchantmentId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchantmentSup The {@link Enchantment} object to register.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, chaining from its own
     * {@link EnchantmentPropertyWrapperBuilder}.
     *
     * @param <B> Any {@link Enchantment} type.
     */
    public static <B extends Enchantment> EnchantmentPropertyWrapperBuilder<B> registerAndChain(ResourceLocation enchantmentId, Supplier<B> enchantmentSup) {
        return registerAndChain(enchantmentId, enchantmentSup, (Collection<Supplier<Enchantment>>) null);
    }

    /**
     * Registers and returns the provided {@link Enchantment}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param enchSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantmentAndReflect(ResourceLocation enchId, Supplier<E> enchSup, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = NexusServices.REGISTRAR.registerObjectAndReflect(enchId, enchSup, BuiltInRegistries.ENCHANTMENT);

        if (enchSupCol != null) enchSupCol.add((Supplier<Enchantment>) registeredEnchantment);

        return registeredEnchantment;
    }

    /**
     * Overloaded variant of {@link #registerEnchantmentAndReflect(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantmentAndReflect(ResourceLocation enchId, Supplier<E> enchSup) {
        return registerEnchantmentAndReflect(enchId, enchSup, null);
    }

    /**
     * Registers and returns the provided {@link Enchantment}, mapping it to a new {@link EnchantmentPropertyWrapper} inheriting
     * from the provided {@link EnchantmentPropertyWrapper} template. Optionally tracks the registered {@link Enchantment} to a
     * custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     * @param enchSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}, mapped to its own {@link EnchantmentPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantmentFromTemplateAndReflect(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = registerEnchantmentAndReflect(enchId, enchSup, enchSupCol);

        return new EnchantmentPropertyWrapper<>(registeredEnchantment, enchId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerEnchantmentFromTemplateAndReflect(ResourceLocation, Supplier, EnchantmentPropertyWrapper, Collection)} that does not track the
     * registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link Enchantment}, mapped to its own {@link EnchantmentPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> Supplier<E> registerEnchantmentFromTemplateAndReflect(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW) {
        return registerEnchantmentFromTemplateAndReflect(enchId, enchSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Enchantment} and returns its {@link EnchantmentPropertyWrapperBuilder} inheriting from the
     * provided {@link EnchantmentPropertyWrapper} template. Optionally tracks the registered {@link Enchantment} to a custom
     * {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     * @param enchSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> EnchantmentPropertyWrapperBuilder<E> registerAndReflectAndChain(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW, @Nullable Collection<Supplier<Enchantment>> enchSupCol) {
        Supplier<E> registeredEnchantment = registerEnchantmentAndReflect(enchId, enchSup, enchSupCol);

        return new EnchantmentPropertyWrapper<>(registeredEnchantment, enchId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, EnchantmentPropertyWrapper, Collection)} that does not track the
     * registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchSup The {@link Enchantment} object to register.
     * @param templateBPW The {@link EnchantmentPropertyWrapper} template to inherit from.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link Enchantment} type.
     */
    public static <E extends Enchantment> EnchantmentPropertyWrapperBuilder<E> registerAndReflectAndChain(ResourceLocation enchId, Supplier<E> enchSup, EnchantmentPropertyWrapper<Enchantment> templateBPW) {
        return registerAndReflectAndChain(enchId, enchSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Enchantment} and returns its {@link EnchantmentPropertyWrapperBuilder}. Optionally tracks the
     * registered {@link Enchantment} to a custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param enchantmentId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchantmentSup The {@link Enchantment} object to register.
     * @param enchantmentSupCol An optional {@link Collection} to track the registered {@link Enchantment}. Primarily
     *                          useful if you want a shorthand method of tracking your own registered enchantments.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, chaining from its own
     * {@link EnchantmentPropertyWrapperBuilder}.
     *
     * @param <B> Any {@link Enchantment} type.
     */
    public static <B extends Enchantment> EnchantmentPropertyWrapperBuilder<B> registerAndReflectAndChain(ResourceLocation enchantmentId, Supplier<B> enchantmentSup, @Nullable Collection<Supplier<Enchantment>> enchantmentSupCol) {
        Supplier<B> registeredEnchantment = registerEnchantmentAndReflect(enchantmentId, enchantmentSup, enchantmentSupCol);

        return new EnchantmentPropertyWrapper<>(registeredEnchantment, enchantmentId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, Collection)}
     * that does not track the registered {@link Enchantment} to any custom {@link Collection}.
     *
     * @param enchantmentId The target {@linkplain Enchantment Enchantment's} {@linkplain ResourceLocation registry ID}.
     * @param enchantmentSup The {@link Enchantment} object to register.
     *
     * @return The {@link EnchantmentPropertyWrapperBuilder} of the registered {@link Enchantment}, chaining from its own
     * {@link EnchantmentPropertyWrapperBuilder}.
     *
     * @param <B> Any {@link Enchantment} type.
     */
    public static <B extends Enchantment> EnchantmentPropertyWrapperBuilder<B> registerAndReflectAndChain(ResourceLocation enchantmentId, Supplier<B> enchantmentSup) {
        return registerAndReflectAndChain(enchantmentId, enchantmentSup, (Collection<Supplier<Enchantment>>) null);
    }
}
