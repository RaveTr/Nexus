package com.mememan.nexus.platform.services;

import com.mememan.nexus.Nexus;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.loader.StandardRegistryBuilder;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A loader-agnostic {@code interface} used for dynamically delegating object registration without needing multiple
 * separate methods, classes, or redundant loader-specific setup.
 * <br></br>
 * Supports standard, datapack, and special vanilla registry types. Additionally covers custom registry types extending
 * from any of the 3 aforementioned types.
 * <br></br>
 * Dependant mods are responsible for storing their registered objects within their own collections. The average
 * registrar {@code class} should look something like this:
 * <pre>
 *     {@code
 *          @RegistrarEntry // Optional, you can use bootstrap methods or some other way to statically initialize this class
 *          public class MyModBlocks {
 *              private static final ObjectArrayList<Supplier<Block>> BLOCKS = new ObjectArrayList<>(); // Collection type can vary based on your use-case, but this is generally how you'd do it for a standard registry. This is totally optional
 *              private static final ObjectArrayList<Supplier<Item>> BLOCK_ITEMS = new ObjectArrayList<>(); // If your blocks are going to have their own items, you should also store those separately
 *
 *              // Can be combined to automatically register a block item and add it to the appropriate collection via other methods within the same class, your own custom methods, etc. This is just a verbose example to showcase the versatility of what the template classes enable you to do when registering objects
 *              public static final Supplier<Block> EXAMPLE_BLOCK = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("example_block"), () -> ..., BlockPropertyWrapperTemplates.BASIC, BLOCKS, BLOCK_ITEMS);
 *
 *              // You would typically want others to have read-only access to your registered objects
 *              // Note that others modifying your custom collections won't actually affect objects you've registered to the game (i.e. If they, for instance, try BLOCKS.remove(EXAMPLE_BLOCK), it won't actually remove the block from the game)
 *
 *              public static ImmutableList<Supplier<Block>> getBlocks() {
 *                  return ImmutableList.copyOf(BLOCKS);
 *              }
 *
 *              public static ImmutableList<Supplier<Item>> getBlockItems() {
 *                  return ImmutableList.copyOf(BLOCK_ITEMS);
 *              }
 *          }
 *     }
 * </pre>
 *
 * For more information, see the references below.
 *
 * @see <a href="https://github.com/RaveTr/Nexus/wiki/registrars">Nexus Wiki: Registrars</a>
 */
public interface Registrar {

    /**
     * Main method for this service interface, called in {@link Nexus} in order to load it and its loader-specific
     * implementations accordingly.
     * <br></br>
     * Functionally speaking, all this method does is properly load and cache registry information on startup. It also
     * handles loading all classes annotated with {@link RegistrarEntry}.
     * <br></br>
     * Dependant mods may choose to opt out of this auto-loading feature by simply not annotating their classes
     * with {@link RegistrarEntry}. It should, however, be noted that mods not using this annotation will have to
     * statically-initialize their classes in some way (bootstrap methods, custom annotation discovery, etc.) in order
     * for object registration to actually occur.
     * <br></br>
     * Should <b>NOT</b> be called anywhere else!
     */
    @ApiStatus.Internal
    @ApiStatus.OverrideOnly
    void setupRegistrar();

    /**
     * Attempts to register an object to the specified {@linkplain Registry targetRegistry}.
     * <br></br>
     * Generally, any registries available in the {@link BuiltInRegistries} class can be used for this method. This
     * could include custom registry types.
     *
     * @param objId The id of the object to register, following Minecraft's regex naming conventions/constraints
     *              (<code>[a-z0-9_.-]</code>). Duplicate exceptions and other edge-cases are handled accordingly
     *              within the target mod-loader's registry implementation.
     * @param objSup The object to register. Has to be valid (e.g. non-{@code null}, matching the target registry's
     *               type, etc.) for the target registry.
     * @param targetRegistry The target {@link Registry} to register the specified object to.
     *
     * @return The <code>objSup</code> that was registered.
     *
     * @param <V> The parent object type of {@code <T>} (So if {@code targetRegistry} is {@link BuiltInRegistries#ITEM},
     *           {@code <V>} would be of type {@link Item}, which makes {@code <T>} any object type extending
     *           {@link Item}).
     * @param <T> The object type to register (e.g. ({@code extends}) {@link Item} or {@link Attribute}).
     *
     * @see BuiltInRegistries
     */
    <V, T extends V> Supplier<T> registerObject(final ResourceLocation objId, final Supplier<T> objSup, Registry<V> targetRegistry);

    /**
     * Alternate variant of {@link #registerObject(ResourceLocation, Supplier, Registry)} that employs
     * {@link Registry#register(Registry, ResourceLocation, Object)} directly instead of delegating registration to
     * loader-specific APIs.
     * <br></br>
     * This primarily affects Neo/Forge, and is only useful in cases where registered objects are checked against
     * during registration, such that deferred registration may cause logical issues since entries aren't immediately
     * reflected on the {@code targetRegistry}. The standard {@link #registerObject(ResourceLocation, Supplier, Registry)}
     * method is otherwise the preferred standard for most use-cases.
     *
     * @param objId The id of the object to register, following Minecraft's regex naming conventions/constraints
     *              (<code>[a-z0-9_.-]</code>). Duplicate exceptions and other edge-cases are handled accordingly
     *              within the target mod-loader's registry implementation.
     * @param objSup The object to register. Has to be valid (e.g. non-{@code null}, matching the target registry's
     *               type, etc.) for the target registry.
     * @param targetRegistry The target {@link Registry} to register the specified object to.
     *
     * @return The <code>objSup</code> that was registered.
     *
     * @param <V> The parent object type of {@code <T>} (So if {@code targetRegistry} is {@link BuiltInRegistries#ITEM},
     *           {@code <V>} would be of type {@link Item}, which makes {@code <T>} any object type extending
     *           {@link Item}).
     * @param <T> The object type to register (e.g. ({@code extends}) {@link Item} or {@link Attribute}).
     *
     * @implNote Neo/Forge will still track the provided {@code objSup} to its appropriate {@code DeferredRegister} in order
     * to avoid referencing issues later on, similar to {@link #registerObject(ResourceLocation, Supplier, Registry)},
     * with the main difference being that since the object is already registered, any accidentally-instantiated
     * duplicates will be gracefully skipped during deferred registration. This effectively allows for proper wrapping
     * of registered objects.
     *
     * @see #registerObject(ResourceLocation, Supplier, Registry)
     * @see com.mememan.nexus.mixins.forge.registries.NamespacedWrapperMixin
     * @see com.mememan.nexus.mixins.forge.registries.DeferredRegisterMixin
     */
    <V, T extends V> Supplier<T> registerObjectAndReflect(final ResourceLocation objId, final Supplier<T> objSup, Registry<V> targetRegistry);

    /**
     * Attempts to register a datapack object to the specified {@linkplain ResourceKey<Registry<T>> targetRegistry}.
     * <br></br>
     * Generally, any datapack registries available in the {@link Registries} class can be used for this method. This
     * could include custom datapack registries. Datapack registries are {@linkplain Registry Registries} that store any
     * form of CODECs for de/serializing data from/to JSON files pertaining to their respective object types and can be
     * accessed via {@link RegistryAccess} (commonly found in {@link Level} instances).
     *
     * @param objId The id of the object to register, following Minecraft's regex naming conventions/constraints
     *              (<code>[a-z0-9_.-]</code>). Duplicate exceptions and other edge-cases are handled accordingly
     *              within the target mod-loader's registry implementation.
     *              <br></br>
     *              Mind that the {@link ResourceLocation} reference passed in must point to an existing and valid JSON
     *              file within the datapack registry's target directory (except in the case of datagen, in which case
     *              this is used to generate the JSON file itself).
     * @param objSupMappingFunc The actual object pertaining to the registered {@link ResourceKey<Registry<T>>}.
     *                          Is a {@link Function} that takes a {@link BootstapContext} instance as input and outputs
     *                          the object to register.
     * @param targetDatapackRegistry The target datapack registry to register the specified object to.
     *
     * @return The {@link ResourceKey} of the object that was registered.
     *
     * @param <V> The parent object type of {@code <T>}.
     * @param <T> The object type to register.
     *
     * @apiNote The output of {@code objSupMappingFunc} isn't returned since registration fields should store references
     * to the registered {@link ResourceKey<Registry<T>>} for later access utilising {@link RegistryAccess} (commonly
     * found in {@link Level} instances), as per MC's datapack value-storing conventions.
     *
     * @see Registries
     * @see Level#registryAccess()
     */
    <V, T extends V> Supplier<ResourceKey<T>> registerDatapackObject(final ResourceLocation objId, Function<BootstapContext<T>, Supplier<T>> objSupMappingFunc, final ResourceKey<Registry<V>> targetDatapackRegistry);

    /**
     * Registers an alternative ID by which a registry entry may be identified. Primarily useful in cases where the
     * original ID isn't resolvable on level load for whatever reason.
     * <br></br>
     * Aliases may be chained together, tying to the original {@code objId}. For instance, you can call this method more
     * than once in reference to the original {@code objId} to associate multiple different alias IDs with it.
     *
     * @param objId The target registry entry's original ID.
     * @param aliasId The alternative ID for the object tied to {@code objId}.
     * @param targetRegistryKey The registry pertaining to the ID being remapped.
     *
     * @param <T> The registry object type.
     *
     * @apiNote Nexus assumes that an object tied to the provided {@code aliasId} is registered by default to the specified
     * {@code targetRegistryKey}'s associated {@link Registry}, should the original {@code objId} fail to resolve.
     *
     * @implSpec {@code objId} should always be the first element in any iterable containing it and its appellations, as
     * per the impl spec in {@link #getAppellations()}.
     */
    <T> void appellate(final ResourceLocation objId, final ResourceLocation aliasId, ResourceKey<Registry<T>> targetRegistryKey);

    /**
     * Attempts to override an existing object in the specified {@linkplain Registry targetRegistry}.
     * <br></br>
     * <b>Important:</b> Using this on any registered object will result in the corresponding entry for all worlds
     * loaded while this is in effect to be permanently replaced. This is unlike the behaviour of
     * {@link #appellate(ResourceLocation, ResourceLocation, ResourceKey)}, which is backwards-compatible with the
     * reintroduction of mapping(s) tied to the original ID.
     *
     * @param objId The id of the object to override, following Minecraft's regex naming conventions/constraints
     *              (<code>[a-z0-9_.-]</code>). Duplicate exceptions and other edge-cases are handled accordingly
     *              within the target mod-loader's registry implementation.
     * @param objSup The object to override. Has to be valid (e.g. non-{@code null}, matching the target registry's
     *               type, etc.) for the target registry.
     * @param targetRegistry The target {@link Registry} pertaining to the object being overridden.
     *
     * @return The <code>objSup</code> that was overridden/new value associated with the provided {@code objId}.
     *
     * @param <V> The parent object type of {@code <T>} (So if {@code targetRegistry} is {@link BuiltInRegistries#ITEM},
     *           {@code <V>} would be of type {@link Item}, which makes {@code <T>} any object type extending
     *           {@link Item}).
     * @param <T> The object type to override (e.g. ({@code extends}) {@link Item} or {@link Attribute}).
     *
     * @apiNote This method will only override an existing object if it exists in the specified {@code targetRegistry}.
     * If the object doesn't exist, this method will simply return the provided {@code objSup}, similar to
     * {@link #registerObject(ResourceLocation, Supplier, Registry)}.
     * <br></br>
     * Other edge cases (e.g. IDs being resolved against {@code null} values) are handled by each loader's internal
     * registry mechanisms/systems.
     */
    <T, V extends T> Supplier<T> overrideObject(ResourceLocation objId, Supplier<V> objSup, Registry<V> targetRegistry);

    /**
     * Attempts to register a standard {@link Registry} using the {@code registryBuilder} passed in, leveraging additional
     * configurations made within said builder.
     *
     * @param registryBuilder The {@link StandardRegistryBuilder} by which the {@link Registry} to create and register
     *                        should be configured.
     *
     * @return The newly-registered {@link Registry}.
     *
     * @param <T> The object type within the {@link Registry} (e.g. {@link Item}).
     */
    <T> Registry<T> registerStandardRegistry(StandardRegistryBuilder<T, Registry<T>> registryBuilder);

    /**
     * Attempts to register a custom datapack registry and notify Nexus API to add its contents to
     * {@link #getRegistrySetBuilder()}.
     *
     * @param registryKey The registry's {@linkplain ResourceKey identifier key}.
     * @param registryCodec The {@link Codec} used to de/serialize objects from/to JSON files.
     * @param networkCodec The {@link Codec} used to sync this registry's objects to the client. This is usually the
     *                     same as {@code registryCodec}, if needed.
     *
     * @return The {@link ResourceKey} corresponding to the newly-registered datapack registry.
     *
     * @param <T> The object type within the registry (e.g. {@link Item}).
     */
    <T> ResourceKey<Registry<T>> registerDatapackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> registryCodec, @Nullable Codec<T> networkCodec);

    /**
     * Overloaded variant of {@link #registerDatapackRegistry(ResourceKey, Codec, Codec)} that defaults the network
     * codec to {@code null}, such that the datapack registry being registered is only required on the server and isn't
     * synced to the client.
     *
     * @param registryKey The registry's {@linkplain ResourceKey identifier key}.
     * @param registryCodec The {@link Codec} used to de/serialize objects from/to JSON files.
     *
     * @return The {@link ResourceKey} corresponding to the newly-registered datapack registry.
     *
     * @param <T> The object type within the registry (e.g. {@link Item}).
     */
    default <T> ResourceKey<Registry<T>> registerServerDatapackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> registryCodec) {
        return registerDatapackRegistry(registryKey, registryCodec, null);
    }

    /**
     * Overloaded variant of {@link #registerDatapackRegistry(ResourceKey, Codec, Codec)} that defaults the network
     * codec to {@code registryCodec}, such that the datapack registry being registered is synced to the client using
     * the same codec.
     *
     * @param registryKey The registry's {@linkplain ResourceKey identifier key}.
     * @param registryCodec The {@link Codec} used to de/serialize objects from/to JSON files.
     *
     * @return The {@link ResourceKey} corresponding to the newly-registered datapack registry.
     *
     * @param <T> The object type within the registry (e.g. {@link Item}).
     */
    default <T> ResourceKey<Registry<T>> registerSyncedDatapackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> registryCodec) {
        return registerDatapackRegistry(registryKey, registryCodec, registryCodec);
    }

    /**
     * Attempts to register a {@link PreparableReloadListener}.
     * <br></br>
     * Note that this listener will not be synced to the client by default due to the lack of standardized data getters
     * for the aforementioned {@code interface}. See the overloaded methods for more configurable options.
     *
     * @param listenerId The id of the listener, used by Nexus to keep track of different listeners and their
     *                   configurations.
     * @param listener The listener to register.
     * @param config The configurator for the listener to register. May be {@code null}.
     *
     * @return The {@link PreparableReloadListener} that was registered.
     *
     * @param <PRL> A {@link PreparableReloadListener} subtype.
     *
     * @apiNote In actuality, if {@code config} is {@code null}, then the specified {@code listener} will not be registered
     * in order to prevent indeterministic behavior (essentially just so that there's a clear API guideline).
     */
    <PRL extends PreparableReloadListener> PRL registerReloadListener(ResourceLocation listenerId, PRL listener, ResourceReloadListenerConfig<PRL> config);

    /**
     * Overloaded variant of {@link #registerReloadListener(ResourceLocation, PreparableReloadListener, ResourceReloadListenerConfig)}
     * that defaults the listener's configuration to use {@link PackType#SERVER_DATA}.
     *
     * @param listenerId The {@linkplain ResourceLocation ID} to map the {@code listener} to.
     * @param listener The {@link PreparableReloadListener} to register.
     *
     * @return {@link #registerReloadListener(ResourceLocation, PreparableReloadListener, ResourceReloadListenerConfig)}
     *
     * @param <PRL> A {@link PreparableReloadListener} subtype.
     */
    default <PRL extends PreparableReloadListener> PRL registerServerReloadListener(ResourceLocation listenerId, PRL listener) {
        return registerReloadListener(listenerId, listener, ResourceReloadListenerConfig.createDefaultSided(PackType.SERVER_DATA));
    }

    /**
     * Overloaded variant of {@link #registerReloadListener(ResourceLocation, PreparableReloadListener, ResourceReloadListenerConfig)}
     * that defaults the listener's configuration to use {@link PackType#SERVER_DATA} and marks it as syncable using the
     * provided {@code dataMapGetter}.
     *
     * @param listenerId The {@linkplain ResourceLocation ID} to map the {@code listener} to.
     * @param listener The {@link PreparableReloadListener} to register.
     * @param dataMapGetter A {@link Function} that returns a {@link Map} of {@linkplain ResourceLocation ResourceLocations}
     *                      corresponding to the locations of the data that should be synced to the client.
     * @param dataCodecMapper A {@link Function} that returns a {@link Codec} used to de/serialize mapped object data
     *                        from the {@code listener}.
     * @param resourceSyncOperation A side-safe operation to run on the client once data is received. This should usually
     *                              only update data on the client (or some copy of it).
     *
     * @return {@link #registerReloadListener(ResourceLocation, PreparableReloadListener, ResourceReloadListenerConfig)}
     *
     * @param <PRL> A {@link PreparableReloadListener} subtype.
     */
    default <PRL extends PreparableReloadListener> PRL registerSyncableReloadListener(ResourceLocation listenerId, PRL listener, Function<PRL, Map<ResourceLocation, ?>> dataMapGetter, Function<PRL, Codec<?>> dataCodecMapper, BiConsumer<PRL, Map<ResourceLocation, ?>> resourceSyncOperation) {
        return registerReloadListener(listenerId, listener, ResourceReloadListenerConfig.createSyncable(dataMapGetter, dataCodecMapper, resourceSyncOperation));
    }

    /**
     * Overloaded variant of {@link #registerReloadListener(ResourceLocation, PreparableReloadListener, ResourceReloadListenerConfig)}
     * that defaults the listener's configuration to use {@link PackType#CLIENT_RESOURCES}.
     *
     * @param listenerId The {@linkplain ResourceLocation ID} to map the {@code listener} to.
     * @param listener The {@link PreparableReloadListener} to register.
     *
     * @return {@link #registerReloadListener(ResourceLocation, PreparableReloadListener, ResourceReloadListenerConfig)}
     *
     * @param <PRL> A {@link PreparableReloadListener} subtype.
     */
    default <PRL extends PreparableReloadListener> PRL registerClientReloadListener(ResourceLocation listenerId, PRL listener) {
        return registerReloadListener(listenerId, listener, ResourceReloadListenerConfig.createDefaultSided(PackType.CLIENT_RESOURCES));
    }

    /**
     * Gets the current singleton {@link RegistrySetBuilder} responsible for populating datapack entries from registration
     * code. May be {@code null} if accessed too early (i.e. before the first datapack registrar {@code class} is hit).
     *
     * @return The current singleton {@link RegistrySetBuilder} used by Nexus API. May be {@code null}. Usually just a
     * {@code static} method reference that delegates the value-getting to a lazily-initialized RSB (e.g. this would
     * probably just {@code return} {@code getDatapackRegistrySetBuilder()}, which would be a lazy init method for the
     * singleton RSB instance).
     */
    @Nullable
    RegistrySetBuilder getRegistrySetBuilder();

    /**
     * Gets a {@link List} of all registered dynamic registries.
     * <br></br>
     * Each loader has its own implementation when it comes to retrieving a collection of all registered dynamic
     * registries, all of which boiling down to external lists that simply copy Vanilla's {@link RegistryDataLoader}
     * registry data and append to it.
     * <br></br>
     * Hence, Nexus API attempts to group them appropriately, such that even custom registries not registered via Nexus
     * API can still be retrieved.
     *
     * @return A {@link List} of all registered dynamic registries.
     */
    List<RegistryDataLoader.RegistryData<?>> getDynamicRegistries();

    /**
     * Gets a copy of all registered dynamic registries synced to the client.
     * <br></br>
     * Each loader has its own implementation when it comes to retrieving a collection of synced registered dynamic
     * registries, all of which boiling down to modifying {@link RegistrySynchronization#NETWORKABLE_REGISTRIES} (to
     * be more specific, Fabric directly copies and sets, while Forge adds a hook and copies into their own custom
     * {@link Map}).
     * <br></br>
     * Hence, Nexus API attempts to group them appropriately, such that even custom registries not registered via Nexus
     * API can still be retrieved.
     *
     * @return A {@link Map} of all registered dynamic registries synced to the client.
     */
    Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> getSyncedDynamicRegistries();

    /**
     * Retrieves a copy of appellations (i.e. nicknames/aliases) for registry entries per tracked registry. Tracks both
     * aliases added through Nexus and the respective loader's API (if appropriate).
     * <br></br>
     * Primarily intended for use in remapping missing registry entries to the same ID across different versions of mods,
     * which may add or remove content between versions.
     *
     * @implSpec The first element for any object ID {@link Int2ObjectMap} value for each registry key should always be
     * the base/original name of the object ID tied to any aliases (see references below).
     *
     * @return A {@link Map} of all registry appellations pertaining to existing registry entries.
     *
     * @see #appellate(ResourceLocation, ResourceLocation, ResourceKey)
     */
    Map<ResourceKey<? extends Registry<?>>, Int2ObjectMap<? extends List<ResourceLocation>>> getAppellations();

    /**
     * Gets a copy of all {@linkplain PreparableReloadListener PreparableReloadListeners} registered to and tracked by
     * Nexus API.
     *
     * @return A {@link Map} of all registered {@linkplain PreparableReloadListener PreparableReloadListeners}. Usually
     * just a {@code static} getter call of the singular existing {@link Map}.
     *
     * @param <PRL> A {@link PreparableReloadListener} subtype.
     */
    <PRL extends PreparableReloadListener> Map<ResourceLocation, Pair<PRL, Optional<ResourceReloadListenerConfig<PRL>>>> getMappedResourceReloadListeners();
}
