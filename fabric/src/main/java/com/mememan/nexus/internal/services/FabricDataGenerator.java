package com.mememan.nexus.internal.services;

import com.mememan.nexus.Nexus;
import com.mememan.nexus.NexusFabric;
import com.mememan.nexus.asm.annotations.DatagenRegistrarEntry;
import com.mememan.nexus.datagen.*;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.datagen.standard.StandardDatapackRegistryProvider;
import com.mememan.nexus.datagen.standard.StandardLanguageProvider;
import com.mememan.nexus.datagen.standard.StandardRecipeProvider;
import com.mememan.nexus.datagen.standard.loot.StandardLootProvider;
import com.mememan.nexus.datagen.standard.model.StandardModelProvider;
import com.mememan.nexus.loader.ModData;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.mixins.fabric_api.datagen.FabricDataGeneratorAccessor;
import com.mememan.nexus.mixins.fabric_api.datagen.PackAccessor;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.DataGenerator;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
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
    private static final Object2ObjectOpenHashMap<String, net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack> CACHED_PACK_LOOKUP = new Object2ObjectOpenHashMap<>();
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

            if (FabricDataGeneratorAccessor.class.isAssignableFrom(fabricDataGenerator.getClass())) {
                FabricDataGeneratorAccessor dataGenAccessor = (FabricDataGeneratorAccessor) (Object) fabricDataGenerator; // Duck typing FTW
                CompletableFuture<HolderLookup.Provider> regLookupProvider = dataGenAccessor.getRegistriesFuture();
                FabricDataOutput rootPackOutput = dataGenAccessor.getFabricOutput();
                Path rootOutputPath = rootPackOutput.getOutputFolder();
                boolean onClient = true; // Forge uses these flags from the buildscript to filter providers based on side, but it doesn't hurt to have them here (even if they're just constants) JIC (possible future impl)
                boolean onServer = true;

                // ModDataProvider types
                if (!ENQUEUED_MOD_PROVIDERS.isEmpty()) {
                    ObjectObjectImmutablePair<String, BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends ModDataProvider>> modProviderMapper;

                    while ((modProviderMapper = ENQUEUED_MOD_PROVIDERS.poll()) != null) {
                        ModData targetMod = NexusServices.PLATFORM_MANAGER.getModDataById(modProviderMapper.left());
                        String targetModId = targetMod.getModMetadata().modId();
                        ModDatagenConfig targetModConfig = NexusServices.DATA_GENERATOR.getConfigForMod(targetModId);
                        boolean allowDatagenForMod = targetModConfig != null && targetModConfig.enableDatagen();
                        ModSpecificPackOutput modSpecificPackOutput = new ModSpecificPackOutput(rootOutputPath, targetMod, allowDatagenForMod);
                        net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack modSpecificPack = CACHED_PACK_LOOKUP.computeIfAbsent(targetModId, oK -> PackAccessor.create(
                                fabricDataGenerator,
                                allowDatagenForMod,
                                targetModId,
                                rootPackOutput
                        ));

                        if (allowDatagenForMod) {
                            ModDataProvider mappedModProvider = modProviderMapper.right().apply(modSpecificPackOutput, regLookupProvider);
                            ProviderType mappedModProviderType = mappedModProvider.getProviderType();
                            boolean modProviderOnClient = mappedModProviderType.getSide() == ModSide.CLIENT;
                            boolean allowDatagenForProviderType = (targetModConfig == null || !targetModConfig.disabledProviderTypes().contains(mappedModProviderType)) && (mappedModProviderType.getSide() == ModSide.COMMON || (modProviderOnClient ? onClient : onServer));

                            addProvider(modSpecificPack, allowDatagenForProviderType, mappedModProvider);
                        }
                    }
                }

                // Standard DataProvider types
                if (!ENQUEUED_PROVIDERS.isEmpty()) {
                    ObjectObjectImmutablePair<String, BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends Pair<Boolean, ? extends DataProvider>>> providerMapper;

                    while ((providerMapper = ENQUEUED_PROVIDERS.poll()) != null) {
                        ModData targetMod = NexusServices.PLATFORM_MANAGER.getModDataById(providerMapper.left());
                        String targetModId = targetMod.getModMetadata().modId();
                        ModDatagenConfig targetModConfig = NexusServices.DATA_GENERATOR.getConfigForMod(targetModId);
                        boolean allowDatagenForMod = targetModConfig != null && targetModConfig.enableDatagen();
                        ModSpecificPackOutput modSpecificPackOutput = new ModSpecificPackOutput(rootOutputPath, targetMod, allowDatagenForMod);
                        net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack modSpecificPack = CACHED_PACK_LOOKUP.computeIfAbsent(targetModId, oK -> PackAccessor.create(
                                fabricDataGenerator,
                                allowDatagenForMod,
                                targetModId,
                                rootPackOutput
                        ));

                        if (allowDatagenForMod) {
                            Pair<Boolean, DataProvider> mappedDataProvider = (Pair<Boolean, DataProvider>) providerMapper.right().apply(modSpecificPackOutput, regLookupProvider);
                            boolean shouldGenerateForSide = mappedDataProvider.left() ? onClient : onServer;

                            addProvider(modSpecificPack, shouldGenerateForSide, mappedDataProvider.right());
                        }
                    }
                }

                // Native Nexus datagen
                NexusServices.PLATFORM_MANAGER.getModData().forEach(curModData -> {
                    String modId = curModData.getModMetadata().modId();
                    ModDatagenConfig modDatagenConfig = NexusServices.DATA_GENERATOR.getConfigForMod(modId);
                    boolean allowDatagenForMod = modDatagenConfig != null && modDatagenConfig.enableDatagen();
                    ModSpecificPackOutput modSpecificPackOutput = new ModSpecificPackOutput(rootOutputPath, curModData, allowDatagenForMod);
                    net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack modSpecificPack = CACHED_PACK_LOOKUP.computeIfAbsent(modId, oK -> PackAccessor.create(
                            fabricDataGenerator,
                            allowDatagenForMod,
                            modId,
                            rootPackOutput
                    ));

                    if (allowDatagenForMod) {
                        Set<ProviderType> providersToValidate = modDatagenConfig == null || modDatagenConfig.providerTypesToFullyValidate() == null ? Set.of() : modDatagenConfig.providerTypesToFullyValidate();
                        Set<ProviderType> disabledProviders = modDatagenConfig == null || modDatagenConfig.disabledProviderTypes() == null ? Set.of() : modDatagenConfig.disabledProviderTypes();
                        Map<ProviderType, DuplicateDataPolicy> mappedDupeStrats = modDatagenConfig == null || modDatagenConfig.mappedDupeStrats() == null ? Map.of() : modDatagenConfig.mappedDupeStrats();

                        // Client
                        addProvider(modSpecificPack, !disabledProviders.contains(NexusProviderTypes.LANGUAGE_PROVIDER) && onClient, new StandardLanguageProvider(modSpecificPackOutput, modId, "en_us", providersToValidate.contains(NexusProviderTypes.LANGUAGE_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.LANGUAGE_PROVIDER, DuplicateDataPolicy.CRASH)));
                        addProvider(modSpecificPack, !disabledProviders.contains(NexusProviderTypes.MODEL_PROVIDER) && onClient, new StandardModelProvider(modSpecificPackOutput, modId, providersToValidate.contains(NexusProviderTypes.MODEL_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.MODEL_PROVIDER, DuplicateDataPolicy.CRASH)));

                        // Server
                        addProvider(modSpecificPack, !disabledProviders.contains(NexusProviderTypes.DYNAMIC_REGISTRY_PROVIDER) && onServer, new StandardDatapackRegistryProvider(modSpecificPackOutput, regLookupProvider, NexusServices.REGISTRAR.getRegistrySetBuilder(), modId, providersToValidate.contains(NexusProviderTypes.DYNAMIC_REGISTRY_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.DYNAMIC_REGISTRY_PROVIDER, DuplicateDataPolicy.CRASH)));

                        addProvider(modSpecificPack, !disabledProviders.contains(NexusProviderTypes.RECIPE_PROVIDER) && onServer, new StandardRecipeProvider(modSpecificPackOutput, modId, providersToValidate.contains(NexusProviderTypes.RECIPE_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.RECIPE_PROVIDER, DuplicateDataPolicy.CRASH)));
                        addProvider(modSpecificPack, !disabledProviders.contains(NexusProviderTypes.LOOT_TABLE_PROVIDER) && onServer, new StandardLootProvider(modSpecificPackOutput, modId, providersToValidate.contains(NexusProviderTypes.LOOT_TABLE_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.LOOT_TABLE_PROVIDER, null)));
                    }
                });
            }

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

    protected static <DP extends DataProvider> void addProvider(net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack targetPack, boolean shouldRun, DP providerToAdd) {
        if (shouldRun) targetPack.addProvider((FabricDataOutput redundantCtx) -> providerToAdd);
    }
}
