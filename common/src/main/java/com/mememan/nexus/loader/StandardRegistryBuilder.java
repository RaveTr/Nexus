package com.mememan.nexus.loader;

import com.mememan.nexus.platform.services.Registrar;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper-builder {@code class} that provides a generalized implementation for loader-specific methods centered around
 * creating new standard {@linkplain Registry Registries}.
 * <br></br>
 * Allows for native configuration of a Registry's attributes, such as sync, persistence, and default entry. More may
 * be added as Nexus API updates.
 *
 * @param <T> The object type within the {@link Registry} (e.g. {@link Item}).
 * @param <R> The {@linkplain Registry Registry's} generic type itself (e.g. {@code Registry<Item>}).
 */
public class StandardRegistryBuilder<T, R extends Registry<T>> {
    private final ResourceKey<R> registryKey;
    private boolean synced = false;
    private boolean persistent = false;
    @Nullable
    private ResourceLocation defaultRegistryEntryLocation;
    @Nullable
    private R potentialBuiltRegistry;
    private boolean builtAsCustomRegistry = false;

    private StandardRegistryBuilder(ResourceKey<R> registryKey) {
        this.registryKey = registryKey;
    }

    /**
     * {@code static} factory method for creating a new {@code StandardRegistryBuilder} from a
     * {@linkplain ResourceKey RegistryKey}.
     * <br></br>
     * <b>IMPORTANT:</b> Make sure the key you're passing in is initialized via
     * {@link ResourceKey#createRegistryKey(ResourceLocation)}.
     *
     * @param registryKey The registry's {@linkplain ResourceKey identifier key}.
     * @param regTypeClazz Dummy placeholder for the object type within the registry (e.g. {@link Item}) to bypass
     *                     Java's generic type invariance.
     *
     * @return A new {@code StandardRegistryBuilder}, ready for configuration.
     *
     * @param <T> The object type within the {@link Registry} (e.g. {@link Item}).
     * @param <R> The {@linkplain Registry Registry's} generic type itself (e.g. {@code Registry<Item>}).
     *
     * @see #of(ResourceLocation, Class)
     */
    public static <T, R extends Registry<T>> StandardRegistryBuilder<T, R> of(ResourceKey<R> registryKey, Class<T> regTypeClazz) {
        return new StandardRegistryBuilder<>(registryKey);
    }

    /**
     * {@code static} factory method for creating a new {@code StandardRegistryBuilder} from a
     * {@linkplain ResourceLocation registry identifier}. Wraps the {@link ResourceLocation} passed in with
     * {@link ResourceKey#createRegistryKey(ResourceLocation)}.
     *
     * @param registryId The registry's {@linkplain ResourceLocation identifier}.
     * @param regTypeClazz Dummy placeholder for the object type within the registry (e.g. {@link Item}) to bypass
     *                     Java's generic type invariance.
     *
     * @return A new {@code StandardRegistryBuilder}, ready for configuration.
     *
     * @param <T> The object type within the {@link Registry} (e.g. {@link Item}).
     * @param <R> The {@linkplain Registry Registry's} generic type itself (e.g. {@code Registry<Item>}).
     *
     * @see #of(ResourceKey, Class)
     */
    public static <T, R extends Registry<T>> StandardRegistryBuilder<T, R> of(ResourceLocation registryId, Class<T> regTypeClazz) {
        return new StandardRegistryBuilder<>((ResourceKey<R>) ResourceKey.createRegistryKey(registryId));
    }

    /**
     * Configures whether the wrapped registry should be synced to the client. Defaults to {@code false}.
     *
     * @param synced Whether the wrapped registry should be synced to the client.
     *
     * @return {@code this} (builder method).
     */
    public StandardRegistryBuilder<T, R> synced(boolean synced) {
        this.synced = synced;
        return this;
    }

    /**
     * Configures whether the wrapped registry should be persisted to disk. Defaults to {@code false}.
     *
     * @param persistent Whether the wrapped registry should be saved to disk.
     *
     * @return {@code this} (builder method).
     */
    public StandardRegistryBuilder<T, R> persistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    /**
     * Configures the default entry for the wrapped registry, such that a {@link DefaultedMappedRegistry} is built.
     * Defaults to {@code null}.
     *
     * @param defaultRegistryEntryLocation The default entry for the wrapped registry (i.e. if a {@code get} method call
     *                                     has no corresponding entry in the registry, the wrapped registry will return
     *                                     this location).
     *
     * @return {@code this} (builder method).
     */
    public StandardRegistryBuilder<T, R> defaultRegistryEntryLocation(ResourceLocation defaultRegistryEntryLocation) {
        this.defaultRegistryEntryLocation = defaultRegistryEntryLocation;
        return this;
    }

    /**
     * Allows for passing in a custom registry object of type {@link Registry} to build from.
     *
     * @param customRegistryObject The custom registry object to build from. Functionally does nothing if {@code null}.
     *
     * @return {@code this} (builder method).
     *
     * @see #buildRegistry()
     */
    public StandardRegistryBuilder<T, R> buildFromCustomRegistryType(R customRegistryObject) {
        this.potentialBuiltRegistry = customRegistryObject;
        this.builtAsCustomRegistry = potentialBuiltRegistry != null;
        return this;
    }

    /**
     * Builds the wrapped registry and returns the builder for inlined use in
     * {@link Registrar#registerStandardRegistry(StandardRegistryBuilder)}.
     * <br></br>
     * If {@link #buildFromCustomRegistryType(Registry)} has been called and the {@link #potentialBuiltRegistry} has
     * been assigned a non-{@code null} type value, this method will return {@code this} instead of building the
     * wrapped registry based on other stored builder data (specifically {@link #defaultRegistryEntryLocation}).
     * <br></br>
     * Otherwise, this method will build the wrapped registry and return {@code this} based on all other stored builder
     * data. If {@link #defaultRegistryEntryLocation} is non-{@code null}, a {@link DefaultedMappedRegistry} will be built
     * instead of a {@link MappedRegistry}.
     *
     * @return {@code this} (builder method).
     *
     * @apiNote {@link #synced} and {@link #persistent} are universally applied to the built registry, regardless of its
     * object type.
     */
    public StandardRegistryBuilder<T, R> buildRegistry() {
        if (!builtAsCustomRegistry) {
            this.potentialBuiltRegistry = defaultRegistryEntryLocation == null
                    ? (R) new MappedRegistry<T>(registryKey, Lifecycle.stable())
                    : (R) new DefaultedMappedRegistry<T>(defaultRegistryEntryLocation.toString(), registryKey, Lifecycle.stable(), false);
        }
        return this;
    }

    /**
     * Builds the wrapped registry and returns it. Useful shortcut for advanced use-cases where you don't necessarily
     * want to use {@link Registrar#registerStandardRegistry(StandardRegistryBuilder)} on this builder.
     * <br></br>
     * <b>NOTE:</b> Will only build the registry if it hasn't already been built without actually registering it. Modders
     * should always conventionally prefer using {@link Registrar#registerStandardRegistry(StandardRegistryBuilder)}.
     *
     * @return The built wrapped {@link Registry}.
     *
     * @see #buildRegistry()
     * @see Registrar#registerStandardRegistry(StandardRegistryBuilder)
     */
    public R buildAndGetRegistry() {
        buildRegistry();
        return getRegistry();
    }

    /**
     * Gets the {@link ResourceKey} of the wrapped registry.
     *
     * @return The wrapped registry's {@link ResourceKey}.
     */
    public ResourceKey<R> getRegistryKey() {
        return registryKey;
    }

    /**
     * Gets the default entry for the wrapped registry.
     *
     * @return The default entry for the wrapped registry. May be {@code null}.
     */
    @Nullable
    public ResourceLocation getDefaultRegistryEntryLocation() {
        return defaultRegistryEntryLocation;
    }

    /**
     * Gets whether the wrapped registry is synced to the client.
     *
     * @return Whether the wrapped registry is synced to the client.
     */
    public boolean isSynced() {
        return synced;
    }

    /**
     * Gets whether the wrapped registry is persistent.
     *
     * @return Whether the wrapped registry is persistent/saved to disk.
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Gets the wrapped registry. May be {@code null} if it hasn't been built yet. Note that non-nullity does not
     * guarantee that the registry has been registered.
     *
     * @return The wrapped registry.
     */
    @Nullable
    public R getRegistry() {
        return potentialBuiltRegistry;
    }

    /**
     * Gets whether the wrapped registry was built as a custom registry object type.
     *
     * @return Whether the wrapped registry was built as a custom registry object type.
     */
    public boolean isBuiltAsCustomRegistry() {
        return builtAsCustomRegistry;
    }
}
