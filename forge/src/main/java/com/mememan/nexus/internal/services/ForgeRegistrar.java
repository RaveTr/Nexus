package com.mememan.nexus.internal.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.ClassFinder;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.loader.StandardRegistryBuilder;
import com.mememan.nexus.mixins.forge.DataPackRegistriesHooksAccessor;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Forge-specific implementation of {@link Registrar}.
 */
public class ForgeRegistrar implements Registrar {
    private static final Object2ObjectLinkedOpenHashMap<String, Object2ObjectOpenHashMap<ResourceKey<?>, DeferredRegister<?>>> CACHED_REGISTRIES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Multimap<ResourceKey<? extends Registry<?>>, ObjectObjectMutablePair<ResourceKey<?>, Function<? extends BootstapContext<?>, ? extends Supplier<?>>>> CACHED_DATAPACK_OBJECT_ENTRIES = ArrayListMultimap.create(); // Slower put() than HashMultiMap, but we need to allow duplicates for leniency
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
    public <V, T extends V> RegistryObject<T> registerObject(ResourceLocation objId, Supplier<T> objSup, Registry<V> targetRegistry) {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus(); // Should not be null at the time this method is called

        ResourceKey<? extends Registry<V>> targetRegistryKey = targetRegistry.key();
        String modId = objId.getNamespace();

        DeferredRegister<V> existingDefReg = (DeferredRegister<V>) CACHED_REGISTRIES.computeIfAbsent(modId, oK -> new Object2ObjectOpenHashMap<>())
                .computeIfAbsent(targetRegistryKey, regKey -> { // Need to layer this in order to allow for multiple mods to register to the same registries (duh). MultiMaps don't allow this level of chaining and I cba to rearrange this. FastUtil FTW.
                    DeferredRegister<V> cachedDefReg = DeferredRegister.create(targetRegistryKey, modId);
                    cachedDefReg.register(modBus);
                    return cachedDefReg;
                });
        return existingDefReg.register(objId.getPath(), objSup);
    }

    @Override
    public <T> Supplier<ResourceKey<T>> registerDatapackObject(ResourceLocation objId, Function<BootstapContext<T>, Supplier<T>> objSupMappingFunc, ResourceKey<Registry<T>> targetDatapackRegistry) {
        ResourceKey<T> targetObject = ResourceKey.create(targetDatapackRegistry, objId);

        if (objSupMappingFunc != null) {
            CACHED_DATAPACK_OBJECT_ENTRIES.put(targetDatapackRegistry, ObjectObjectMutablePair.of(targetObject, objSupMappingFunc)); // Need to use this approach since RSBs don't support stacking registration calls
        }

        return () -> targetObject;
    }

    @Override
    public <T> Registry<T> registerStandardRegistry(StandardRegistryBuilder<T, Registry<T>> registryBuilder) {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus(); // Should not be null at the time this method is called
        DeferredRegister<T> defReg = DeferredRegister.create(registryBuilder.getRegistryKey(), registryBuilder.getRegistryKey().location().getNamespace());
        RegistryBuilder<T> forgeRegBuilder = new RegistryBuilder<>();

        if (!registryBuilder.isSynced()) forgeRegBuilder.disableSync();
        if (!registryBuilder.isPersistent()) forgeRegBuilder.disableSaving();
        if (!registryBuilder.isBuiltAsCustomRegistry() && registryBuilder.getDefaultRegistryEntryLocation() != null) forgeRegBuilder.setDefaultKey(registryBuilder.getDefaultRegistryEntryLocation()); // Consistent behaviour across loaders. We can probably implement this some other way later.

        forgeRegBuilder.hasTags(); // No reason not to default to adding the registry to Minecraft's root registry

        defReg.makeRegistry(() -> forgeRegBuilder);
        defReg.register(modBus);

        return registryBuilder.buildAndGetRegistry();
    }

    @Override
    public <T> ResourceKey<Registry<T>> registerDatapackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> registryCodec, @Nullable Codec<T> networkCodec) {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus(); // Should not be null at the time this method is called

        modBus.addListener((DataPackRegistryEvent.NewRegistry event) -> event.dataPackRegistry(registryKey, registryCodec, networkCodec));

        return registryKey;
    }

    @Override
    public @Nullable RegistrySetBuilder getRegistrySetBuilder() {
        return getDatapackRegistrySetBuilder();
    }

    @Override
    public List<RegistryDataLoader.RegistryData<?>> getDynamicRegistries() {
        return DataPackRegistriesHooks.getDataPackRegistries();
    }

    @Override
    public Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> getSyncedDynamicRegistries() {
        return DataPackRegistriesHooksAccessor.getNetworkableRegistries();
    }

    public static RegistrySetBuilder getDatapackRegistrySetBuilder() {
        return DATAPACK_REGISTRY_SET_BUILDER == null ? DATAPACK_REGISTRY_SET_BUILDER = new RegistrySetBuilder() : DATAPACK_REGISTRY_SET_BUILDER;
    }

    public static ImmutableMap<String, Object2ObjectOpenHashMap<ResourceKey<?>, DeferredRegister<?>>> getCachedRegistries() {
        return ImmutableMap.copyOf(CACHED_REGISTRIES);
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

    private static void onAddReloadListenerEvent(AddReloadListenerEvent event) {
        
    }

    private static void onDatapackSyncEvent(OnDatapackSyncEvent event) {

    }
}
