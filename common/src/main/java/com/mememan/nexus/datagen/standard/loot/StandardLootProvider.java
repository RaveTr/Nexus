package com.mememan.nexus.datagen.standard.loot;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Loader-agnostic mod-specific loot table provider in Nexus API. Instanced based on the provided mod ID. Handles
 * the generation of mod-specific loot tables, with additional modifications to allow for standard Nexus configurability.
 * <br></br>
 * Configurations made to this provider's type ({@link NexusProviderTypes#LOOT_TABLE_PROVIDER}) take precedence over
 * any configurations made to the individual sub-providers.
 */
public class StandardLootProvider extends LootTableProvider implements ModDataProvider {
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;

    public StandardLootProvider(PackOutput targetOutput, String modId, boolean validateAllEntries, @Nullable DuplicateDataPolicy dupeStrat) {
        super(targetOutput, Set.of(), Util.make(new ObjectArrayList<>(), curList -> {

        }));

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput output) {
        final Object2ObjectOpenHashMap<ResourceLocation, LootTable> mappedLootTables = new Object2ObjectOpenHashMap<>();
        final Object2ObjectOpenHashMap<RandomSupport.Seed128bit, ResourceLocation> hashedLootTableLocs = new Object2ObjectOpenHashMap<>();
        final ObjectOpenHashSet<ResourceLocation> requiredTables = new ObjectOpenHashSet<>();

        ValidationContext lootMiscValidationCtx = new ValidationContext(LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
            @Nullable
            public <T> T getElement(LootDataId<T> lootDataId) {
                return (T) (lootDataId.type() == LootDataType.TABLE ? mappedLootTables.get(lootDataId.location()) : null);
            }
        });

        this.subProviders.stream()
                .filter(curSubProviderEntry -> curSubProviderEntry.provider().get() instanceof ModLootTableSubProvider)
                .forEach((curSubProviderEntry) -> {
                    ModLootTableSubProvider modLootTableSubProvider = (ModLootTableSubProvider) curSubProviderEntry.provider().get();

                    NexusConstants.LOGGER.debug("Generating loot tables for sub-provider: {}", modLootTableSubProvider.getName());

                    curSubProviderEntry.provider().get().generate((curLootTableLoc, curLootTableBuilder) -> {
                        ResourceLocation mappedHashedLootTable = hashedLootTableLocs.put(RandomSequence.seedForKey(curLootTableLoc), curLootTableLoc);

                        if (mappedHashedLootTable != null) Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + mappedHashedLootTable + " and " + curLootTableLoc);

                        LootTable curBuiltLootTable = curLootTableBuilder
                                .setRandomSequence(curLootTableLoc)
                                .setParamSet(curSubProviderEntry.paramSet())
                                .build();

                        Consumer<Object2ObjectOpenHashMap<ResourceLocation, LootTable>> tableMapper = (mappedTables) -> mappedTables.put(curLootTableLoc, curBuiltLootTable);

                        if (mappedLootTables.get(curLootTableLoc) != null) {
                            DuplicateDataPolicy chosenDupeStrat = dupeStrat == null ? modLootTableSubProvider.getDuplicateDataPolicy() : dupeStrat;

                            switch (chosenDupeStrat) {
                                case CRASH -> throw new IllegalStateException(String.format("Duplicate loot table %s from mod of ID %s, specified DuplicateDataPolicy is CRASH.", curLootTableLoc, modLootTableSubProvider.getModId()));
                                case EXCLUDE_WARN -> NexusConstants.LOGGER.warn("Duplicate loot table {} from mod of ID {}, specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", curLootTableLoc, modLootTableSubProvider.getModId());
                                case EXCLUDE_SILENT -> {}
                                case OVERRIDE_WARN -> {
                                    NexusConstants.LOGGER.warn("Overriding duplicate loot table {} (mod of ID {}) with new data.", curLootTableLoc, modLootTableSubProvider.getModId());
                                    tableMapper.accept(mappedLootTables);
                                }
                                case OVERRIDE_SILENT -> tableMapper.accept(mappedLootTables);
                            }
                        } else tableMapper.accept(mappedLootTables);

                        if (validateAllEntries() || modLootTableSubProvider.validateAllEntries()) requiredTables.add(curLootTableLoc);
                    });
                });

        for (ResourceLocation curLootTableLoc : Sets.difference(requiredTables, mappedLootTables.keySet())) { // This should literally never be reached (as in run (duh)), but in case it somehow is, we can still handle it ig
            lootMiscValidationCtx.reportProblem(String.format("Missing loot table: %s (required by mod of ID: %s)", curLootTableLoc, modId));
        }

        mappedLootTables.forEach((mappedLootTableLoc, mappedLootTable) -> mappedLootTable.validate(lootMiscValidationCtx.setParams(mappedLootTable.getParamSet()).enterElement("{" + mappedLootTableLoc + "}", new LootDataId<>(LootDataType.TABLE, mappedLootTableLoc))));

        Multimap<String, String> lootTableValidationProblems = lootMiscValidationCtx.getProblems();

        if (!lootTableValidationProblems.isEmpty()) {
            lootTableValidationProblems.forEach((problematicLootTable, validationProblem) -> LootTableProvider.LOGGER.warn("Found validation problem in {}: {}", problematicLootTable, validationProblem));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        } else {
            return CompletableFuture.allOf(mappedLootTables.entrySet().stream().map((lootEntry) -> {
                ResourceLocation lootTableLoc = lootEntry.getKey();
                LootTable lootTable = lootEntry.getValue();
                Path targetPath = this.pathProvider.json(lootTableLoc);

                return DataProvider.saveStable(output, LootDataType.TABLE.parser().toJsonTree(lootTable), targetPath);
            }).toArray(CompletableFuture[]::new));
        }
    }

    @Override
    public @NotNull String getModId() {
        return modId;
    }

    @Override
    public boolean validateAllEntries() {
        return validateAllEntries;
    }

    @Override
    public @NotNull ProviderType getProviderType() {
        return NexusProviderTypes.LOOT_TABLE_PROVIDER;
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }

    @Override
    public @NotNull String getName() {
        return String.format("Loot Tables [%s]", getModId());
    }
}
