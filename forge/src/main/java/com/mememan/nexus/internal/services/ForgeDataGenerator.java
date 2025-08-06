package com.mememan.nexus.internal.services;

import com.mememan.nexus.asm.annotations.DatagenRegistrarEntry;
import com.mememan.nexus.datagen.*;
import com.mememan.nexus.datagen.standard.*;
import com.mememan.nexus.datagen.standard.loot.StandardLootProvider;
import com.mememan.nexus.datagen.standard.tag.*;
import com.mememan.nexus.loader.ModData;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.DataGenerator;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

public class ForgeDataGenerator implements DataGenerator {
    private static boolean HAS_CONSUMED_GENERATORS = false;
    @Nullable
    private static net.minecraft.data.DataGenerator GLOBAL_DATA_GENERATOR_INSTANCE;
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

    @Override
    public void setupDataGenerator() {
        NexusServices.PLATFORM_MANAGER.discoverAnnotatedClasses(DatagenRegistrarEntry.class); // Early discovery to ensure presence of configs and/or external data providers

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(ForgeDataGenerator::onGatherDataEvent);
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
        return GLOBAL_DATA_GENERATOR_INSTANCE;
    }

    public static boolean hasConsumedGenerators() {
        return HAS_CONSUMED_GENERATORS;
    }

    private static void onGatherDataEvent(final GatherDataEvent event) {
        net.minecraft.data.DataGenerator primaryGen = event.getGenerator();
        PackOutput rootPackOutput = primaryGen.getPackOutput();
        Path formattedOutputPath = rootPackOutput.getOutputFolder();
        CompletableFuture<HolderLookup.Provider> regLookupProvider = event.getLookupProvider();

        boolean onClient = event.includeClient();
        boolean onServer = event.includeServer();

        if (!hasConsumedGenerators()) { // Safeguard against potentially running this more than once for any reason
            GLOBAL_DATA_GENERATOR_INSTANCE = primaryGen;

            // ModDataProvider types
            if (!ENQUEUED_MOD_PROVIDERS.isEmpty()) {
                ObjectObjectImmutablePair<String, BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends ModDataProvider>> modProviderMapper;

                while ((modProviderMapper = ENQUEUED_MOD_PROVIDERS.poll()) != null) {
                    ModData targetMod = NexusServices.PLATFORM_MANAGER.getModDataById(modProviderMapper.left());
                    String targetModId = targetMod.getModMetadata().modId();
                    ModDatagenConfig targetModConfig = NexusServices.DATA_GENERATOR.getConfigForMod(targetModId);
                    boolean allowDatagenForMod = targetModConfig != null && targetModConfig.enableDatagen();
                    ModSpecificPackOutput modSpecificPackOutput = new ModSpecificPackOutput(formattedOutputPath, targetMod, allowDatagenForMod);

                    if (allowDatagenForMod) {
                        ModDataProvider mappedModProvider = modProviderMapper.right().apply(modSpecificPackOutput, regLookupProvider);
                        ProviderType mappedModProviderType = mappedModProvider.getProviderType();
                        boolean modProviderOnClient = mappedModProviderType.getSide() == ModSide.CLIENT;
                        boolean allowDatagenForProviderType = targetModConfig == null || !targetModConfig.disabledProviderTypes().contains(mappedModProviderType);

                        if (allowDatagenForProviderType) primaryGen.addProvider(modProviderOnClient ? onClient : onServer, mappedModProvider);
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
                    ModSpecificPackOutput modSpecificPackOutput = new ModSpecificPackOutput(formattedOutputPath, targetMod, allowDatagenForMod);

                    if (allowDatagenForMod) {
                        Pair<Boolean, DataProvider> mappedDataProvider = (Pair<Boolean, DataProvider>) providerMapper.right().apply(modSpecificPackOutput, regLookupProvider);

                        primaryGen.addProvider(mappedDataProvider.left() ? onClient : onServer, mappedDataProvider.right());
                    }
                }
            }

            // Native Nexus datagen
            NexusServices.PLATFORM_MANAGER.getModData().forEach(curModData -> {
                String modId = curModData.getModMetadata().modId();
                ModDatagenConfig modDatagenConfig = NexusServices.DATA_GENERATOR.getConfigForMod(modId);
                boolean allowDatagenForMod = modDatagenConfig != null && modDatagenConfig.enableDatagen();
                ModSpecificPackOutput modSpecificPackOutput = new ModSpecificPackOutput(formattedOutputPath, curModData, allowDatagenForMod);

                if (allowDatagenForMod) {
                    Set<ProviderType> providersToValidate = modDatagenConfig == null || modDatagenConfig.providerTypesToFullyValidate() == null ? Set.of() : modDatagenConfig.providerTypesToFullyValidate();
                    Set<ProviderType> disabledProviders = modDatagenConfig == null || modDatagenConfig.disabledProviderTypes() == null ? Set.of() : modDatagenConfig.disabledProviderTypes();
                    Map<ProviderType, DuplicateDataPolicy> mappedDupeStrats = modDatagenConfig == null || modDatagenConfig.mappedDupeStrats() == null ? Map.of() : modDatagenConfig.mappedDupeStrats();

                    // Client
                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.LANGUAGE_PROVIDER) && onClient, new StandardLanguageProvider(modSpecificPackOutput, modId, "en_us", providersToValidate.contains(NexusProviderTypes.LANGUAGE_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.LANGUAGE_PROVIDER, DuplicateDataPolicy.CRASH)));

                    // Server
                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.DYNAMIC_REGISTRY_PROVIDER) && onServer, new StandardDatapackRegistryProvider(modSpecificPackOutput, regLookupProvider, NexusServices.REGISTRAR.getRegistrySetBuilder(), modId, providersToValidate.contains(NexusProviderTypes.DYNAMIC_REGISTRY_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.DYNAMIC_REGISTRY_PROVIDER, DuplicateDataPolicy.CRASH)));

                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.BLOCK_TAGS_PROVIDER) && onServer, new StandardBlockTagProvider(modSpecificPackOutput, regLookupProvider, modId, providersToValidate.contains(NexusProviderTypes.BLOCK_TAGS_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.BLOCK_TAGS_PROVIDER, DuplicateDataPolicy.CRASH)));
                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.ITEM_TAGS_PROVIDER) && onServer, new StandardItemTagProvider(modSpecificPackOutput, regLookupProvider, modId, providersToValidate.contains(NexusProviderTypes.ITEM_TAGS_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.ITEM_TAGS_PROVIDER, DuplicateDataPolicy.CRASH)));
                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.ENTITY_TYPE_TAGS_PROVIDER) && onServer, new StandardEntityTypeTagProvider(modSpecificPackOutput, regLookupProvider, modId, providersToValidate.contains(NexusProviderTypes.ENTITY_TYPE_TAGS_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.ENTITY_TYPE_TAGS_PROVIDER, DuplicateDataPolicy.CRASH)));
                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.ENCHANTMENT_TAGS_PROVIDER) && onServer, new StandardEnchantmentTagProvider(modSpecificPackOutput, regLookupProvider, modId, providersToValidate.contains(NexusProviderTypes.ENCHANTMENT_TAGS_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.ENCHANTMENT_TAGS_PROVIDER, DuplicateDataPolicy.CRASH)));
                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.MOB_EFFECT_TAGS_PROVIDER) && onServer, new StandardMobEffectTagProvider(modSpecificPackOutput, regLookupProvider, modId, providersToValidate.contains(NexusProviderTypes.MOB_EFFECT_TAGS_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.MOB_EFFECT_TAGS_PROVIDER, DuplicateDataPolicy.CRASH)));

                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.DAMAGE_TYPE_TAGS_PROVIDER) && onServer, new StandardDamageTypeTagProvider(modSpecificPackOutput, regLookupProvider, modId, providersToValidate.contains(NexusProviderTypes.DAMAGE_TYPE_TAGS_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.DAMAGE_TYPE_TAGS_PROVIDER, DuplicateDataPolicy.CRASH)));

                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.RECIPE_PROVIDER) && onServer, new StandardRecipeProvider(modSpecificPackOutput, modId, providersToValidate.contains(NexusProviderTypes.RECIPE_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.RECIPE_PROVIDER, DuplicateDataPolicy.CRASH)));
                    primaryGen.addProvider(!disabledProviders.contains(NexusProviderTypes.LOOT_TABLE_PROVIDER) && onServer, new StandardLootProvider(modSpecificPackOutput, modId, providersToValidate.contains(NexusProviderTypes.LOOT_TABLE_PROVIDER), mappedDupeStrats.getOrDefault(NexusProviderTypes.LOOT_TABLE_PROVIDER, null), mappedDupeStrats, providersToValidate, disabledProviders));
                }
            });

            HAS_CONSUMED_GENERATORS = true;
        }
    }
}
