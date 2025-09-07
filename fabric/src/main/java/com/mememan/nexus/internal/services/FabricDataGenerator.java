package com.mememan.nexus.internal.services;

import com.mememan.nexus.Nexus;
import com.mememan.nexus.NexusFabric;
import com.mememan.nexus.asm.annotations.DatagenRegistrarEntry;
import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.DataGenerator;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * Fabric-specific implementation of {@link DataGenerator}. Doubles as the primary datagen entrypoint for Nexus API on
 * Fabric.
 */
public class FabricDataGenerator implements DataGenerator, DataGeneratorEntrypoint {
    private static boolean hasConsumedGenerators = false;
    private static final AtomicReference<net.minecraft.data.DataGenerator> GLOBAL_DATA_GENERATOR_INSTANCE = new AtomicReference<>();
    private static final Queue<ObjectObjectImmutablePair<String, BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends Pair<Boolean, ? extends DataProvider>>>> ENQUEUED_PROVIDERS = new ConcurrentLinkedQueue<>();
    private static final Queue<ObjectObjectImmutablePair<String, BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends ModDataProvider>>> ENQUEUED_MOD_PROVIDERS = new ConcurrentLinkedQueue<>();
    private static final ObjectOpenCustomHashSet<ModDatagenConfig> MOD_DATAGEN_CONFIGS = new ObjectOpenCustomHashSet<>(new Hash.Strategy<>() {
        @Override
        public int hashCode(ModDatagenConfig o) {
            return Objects.hash(o.modId());
        }

        @Override
        public boolean equals(ModDatagenConfig a, ModDatagenConfig b) {
            return a != null && b != null && Objects.equals(a.modId(), b.modId());
        }
    });

    /**
     * @implNote The Fabric implementation of this method does nothing in favour of
     * {@link #onInitializeDataGenerator(net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator)}, since datagen
     * initialization should be handled within the appropriate entrypoint method, whereas this is called in the
     * primary mod initialization entrypoint.
     *
     * @see Nexus
     * @see NexusFabric
     */
    @Override
    public void setupDataGenerator() {
        // NO-OP
    }

    @Override
    public void onInitializeDataGenerator(net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator fabricDataGenerator) {
        NexusServices.PLATFORM_MANAGER.discoverAnnotatedClasses(DatagenRegistrarEntry.class); // Early discovery to ensure presence of configs and/or external data providers

        if (!hasConsumedGenerators()) {
            GLOBAL_DATA_GENERATOR_INSTANCE.set(fabricDataGenerator);

            net.minecraft.data.DataGenerator.PackGenerator a = fabricDataGenerator.createPack();

            hasConsumedGenerators = true;
        }
    }

    @Override
    public <DP extends DataProvider> void registerDataProvider(String modId, BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, Pair<Boolean, DP>> dataProvider) {
        ENQUEUED_PROVIDERS.add(ObjectObjectImmutablePair.of(modId, dataProvider));
    }

    @Override
    public <MDP extends ModDataProvider> void registerModDataProvider(String modId, BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, MDP> modDataProvider) {
        ENQUEUED_MOD_PROVIDERS.add(ObjectObjectImmutablePair.of(modId, modDataProvider));
    }

    @Override
    public ModDatagenConfig registerConfigForMod(ModDatagenConfig modDatagenConfig) {
        MOD_DATAGEN_CONFIGS.add(modDatagenConfig);
        return modDatagenConfig;
    }

    @Override
    public Set<ModDatagenConfig> getModDatagenConfigs() {
        return MOD_DATAGEN_CONFIGS;
    }

    @Override
    public @Nullable net.minecraft.data.DataGenerator getDataGenerator() {
        return GLOBAL_DATA_GENERATOR_INSTANCE.get();
    }

    public static boolean hasConsumedGenerators() {
        return hasConsumedGenerators;
    }
}
