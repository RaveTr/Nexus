package com.mememan.nexus.internal.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.ClassFinder;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.internal.loader.FabricRegistryHookManager;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.loader.StandardRegistryBuilder;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.resource.config.ResourceReloadListenerConfig;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fabric-specific implementation of {@link Registrar}.
 */
public class FabricRegistrar implements Registrar {
    private static final Multimap<ResourceKey<? extends Registry<?>>, ObjectObjectMutablePair<ResourceKey<?>, Function<? extends BootstapContext<?>, ? extends Supplier<?>>>> CACHED_DATAPACK_OBJECT_ENTRIES = ArrayListMultimap.create(); // Slower put() than HashMultiMap, but we need to allow duplicates for leniency
    private static final Map<ResourceLocation, Pair<? extends PreparableReloadListener, Optional<ResourceReloadListenerConfig<? extends PreparableReloadListener>>>> CACHED_RESOURCE_RELOAD_LISTENERS = new Object2ObjectOpenHashMap<>();
    private static final FabricRegistryHookManager REGISTRY_HOOK_MANAGER = new FabricRegistryHookManager();
    private static RegistrySetBuilder DATAPACK_REGISTRY_SET_BUILDER;

    @Override
    @ApiStatus.Internal
    public void setupRegistrar() {
        long startTime = System.currentTimeMillis();

        NexusServices.PLATFORM_MANAGER.discoverAnnotatedClasses(RegistrarEntry.class, (classA, classB) -> {
            Class<?> uninitializedClassA = ClassFinder.forNameNoInit(classA);
            Class<?> uninitializedClassB = ClassFinder.forNameNoInit(classB);
            RegistrarEntry annotA = uninitializedClassA.getAnnotation(RegistrarEntry.class); // We don't care about initializing the annotation itself since it doesn't do anything
            RegistrarEntry annotB = uninitializedClassB.getAnnotation(RegistrarEntry.class);
            int priorityA = annotA.priority();
            int priorityB = annotB.priority();

            return priorityA > priorityB
                    ? -1
                    : priorityA == priorityB
                    ? classA.compareTo(classB)
                    : 1;
        }, (sortedClassName) -> {
            Class<?> uninitializedTargetClass = ClassFinder.forNameNoInit(sortedClassName);
            RegistrarEntry targetAnnotation = uninitializedTargetClass.getAnnotation(RegistrarEntry.class);
            Class<?>[] dependencies = targetAnnotation.dependencies();

            if (dependencies != null) {
                for (Class<?> dependency : dependencies) {
                    if (dependency == null || dependency.getName().equals(sortedClassName)) continue;

                    ClassFinder.forName(dependency.getName());
                }
            }
        }, (sortedClassName) -> {
            Class<?> uninitializedTargetClass = ClassFinder.forNameNoInit(sortedClassName);
            RegistrarEntry targetAnnotation = uninitializedTargetClass.getAnnotation(RegistrarEntry.class);
            ModSide targetInitSide = targetAnnotation.initSide(); // Defaults to ModSide#COMMON anyway soooo...

            return NexusServices.PLATFORM_MANAGER.getEnvironmentSide().pertainsTo(targetInitSide);
        });

        CACHED_DATAPACK_OBJECT_ENTRIES.asMap().forEach((registryKey, objSupMappingFuncs) -> {
            getDatapackRegistrySetBuilder().add(tCastRegistryKey(registryKey), b -> objSupMappingFuncs.forEach(mappedObjKey -> {
                b.register(tCastObjectKey(mappedObjKey.left()), tCastObjSupMappingFunc(mappedObjKey.right(), b).get());
            }));
        });

        long endTime = System.currentTimeMillis();
        NexusConstants.LOGGER.info("Registrar setup took {} ms", endTime - startTime);
    }

    @Override
    public <V, T extends V> Supplier<T> registerObject(ResourceLocation objId, Supplier<T> objSup, Registry<V> targetRegistry) {
        T targetObject = Registry.register(targetRegistry, objId, objSup.get()); // Must store in a local field beforehand cuz... it's null if inlined
        return () -> targetObject;
    }

    @Override
    public <V, T extends V> Supplier<T> registerObjectAndReflect(ResourceLocation objId, Supplier<T> objSup, Registry<V> targetRegistry) {
        return registerObject(objId, objSup, targetRegistry);
    }

    @Override
    public <V, T extends V> Supplier<ResourceKey<T>> registerDatapackObject(ResourceLocation objId, Function<BootstapContext<T>, Supplier<T>> objSupMappingFunc, ResourceKey<Registry<V>> targetDatapackRegistry) {
        ResourceKey<T> targetObject = (ResourceKey<T>) ResourceKey.create(targetDatapackRegistry, objId);

        if (objSupMappingFunc != null) {
            CACHED_DATAPACK_OBJECT_ENTRIES.put(targetDatapackRegistry, ObjectObjectMutablePair.of(targetObject, objSupMappingFunc)); // Need to use this approach since RSBs don't support stacking registration calls
        }

        return () -> targetObject;
    }

    @Override
    public <T> Registry<T> registerStandardRegistry(StandardRegistryBuilder<T, Registry<T>> registryBuilder) {
        Registry<T> builtReg = registryBuilder.buildAndGetRegistry();

        if (!(builtReg instanceof WritableRegistry<T>)) throw new IllegalArgumentException(String.format("Registry %s is not of type WritableRegistry. FabricRegistryBuilder requires registries to implement WritableRegistry. Nexus may update around this generic type constraint at a later point, but for now, ensure your custom registry type implements WritableRegistry.", registryBuilder.getRegistryKey()));

        FabricRegistryBuilder<T, ? extends WritableRegistry<T>> fabricRegBuilder = FabricRegistryBuilder.from((WritableRegistry<T>) builtReg);

        if (registryBuilder.isSynced()) fabricRegBuilder = fabricRegBuilder.attribute(RegistryAttribute.SYNCED);
        if (registryBuilder.isPersistent()) fabricRegBuilder = fabricRegBuilder.attribute(RegistryAttribute.PERSISTED);

        builtReg = fabricRegBuilder.buildAndRegister();

        return builtReg;
    }

    @Override
    public <T> ResourceKey<Registry<T>> registerDatapackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> registryCodec, @Nullable Codec<T> networkCodec) {
        if (networkCodec != null) DynamicRegistries.registerSynced(registryKey, registryCodec, networkCodec);
        else DynamicRegistries.register(registryKey, registryCodec);

        return registryKey;
    }

    @Override
    public <PRL extends PreparableReloadListener> PRL registerReloadListener(ResourceLocation listenerId, PRL listener, ResourceReloadListenerConfig<PRL> config) {
        CACHED_RESOURCE_RELOAD_LISTENERS.putIfAbsent(listenerId, ObjectObjectImmutablePair.of(listener, Optional.ofNullable(config)));
        return listener;
    }

    @Override
    public FabricRegistryHookManager getRegistryHookManager() {
        return REGISTRY_HOOK_MANAGER;
    }

    @Override
    public @Nullable RegistrySetBuilder getRegistrySetBuilder() {
        return getDatapackRegistrySetBuilder();
    }

    @Override
    public List<RegistryDataLoader.RegistryData<?>> getDynamicRegistries() {
        return DynamicRegistries.getDynamicRegistries();
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> getSyncedDynamicRegistries() {
        return ImmutableMap.copyOf(RegistrySynchronization.NETWORKABLE_REGISTRIES);
    }

    @Override
    public <PRL extends PreparableReloadListener> Map<ResourceLocation, Pair<PRL, Optional<ResourceReloadListenerConfig<PRL>>>> getMappedResourceReloadListeners() {
        return getCachedResourceReloadListeners();
    }

    protected <T> Supplier<T> tCastObjSupMappingFunc(Function<? extends BootstapContext<?>, ? extends Supplier<?>> objSupMappingFunc, BootstapContext<T> bootstapContext) { // I love wildcard casts
        return ((Function<BootstapContext<T>, Supplier<T>>) objSupMappingFunc).apply(bootstapContext);
    }

    protected <T> ResourceKey<T> tCastObjectKey(ResourceKey<?> objectKey) {
        return (ResourceKey<T>) objectKey;
    }

    protected <T> ResourceKey<Registry<T>> tCastRegistryKey(ResourceKey<? extends Registry<?>> registryKey) {
        return (ResourceKey<Registry<T>>) registryKey;
    }

    public static RegistrySetBuilder getDatapackRegistrySetBuilder() {
        return DATAPACK_REGISTRY_SET_BUILDER == null ? DATAPACK_REGISTRY_SET_BUILDER = new RegistrySetBuilder() : DATAPACK_REGISTRY_SET_BUILDER;
    }

    public static <PRL extends PreparableReloadListener> ImmutableMap<ResourceLocation, Pair<PRL, Optional<ResourceReloadListenerConfig<PRL>>>> getCachedResourceReloadListeners() {
        Map<ResourceLocation, Pair<PRL, Optional<ResourceReloadListenerConfig<PRL>>>> result = new Object2ObjectOpenHashMap<>();

        CACHED_RESOURCE_RELOAD_LISTENERS.forEach((key, value) -> // Avoid generic type invariance screwing us at compile-time
                result.put(key, ObjectObjectImmutablePair.of((PRL) value.left(), value.right().flatMap(config -> Optional.of((ResourceReloadListenerConfig<PRL>) config))))
        );

        return ImmutableMap.copyOf(result);
    }
}
