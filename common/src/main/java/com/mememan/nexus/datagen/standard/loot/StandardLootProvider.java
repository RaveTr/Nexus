package com.mememan.nexus.datagen.standard.loot;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mememan.nexus.NexusConstants;
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

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Loader-agnostic mod-specific loot table provider in Nexus API. Instanced based on the provided mod ID. Handles
 * the generation of mod-specific loot tables, with additional modifications to allow for standard Nexus configurability.
 */
public class StandardLootProvider extends LootTableProvider {
    protected final String modId;

    public StandardLootProvider(PackOutput targetOutput, String modId) {
        super(targetOutput, Set.of(), Util.make(new ObjectArrayList<>(), curList -> {
            
        }));

        this.modId = modId;
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
        this.subProviders.forEach((curSubProviderEntry) -> curSubProviderEntry.provider().get().generate((curLootTableLoc, curLootTableBuilder) -> {
            if (curSubProviderEntry.provider().get() instanceof ModLootTableSubProvider modLootTableSubProvider) {
                ResourceLocation mappedHashedLootTable = hashedLootTableLocs.put(RandomSequence.seedForKey(curLootTableLoc), curLootTableLoc);

                if (mappedHashedLootTable != null) Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + mappedHashedLootTable + " and " + curLootTableLoc);

                LootTable curBuiltLootTable = curLootTableBuilder
                        .setRandomSequence(curLootTableLoc)
                        .setParamSet(curSubProviderEntry.paramSet())
                        .build();

                Consumer<Object2ObjectOpenHashMap<ResourceLocation, LootTable>> tableMapper = (mappedTables) -> mappedTables.put(curLootTableLoc, curBuiltLootTable);

                if (mappedLootTables.get(curLootTableLoc) != null) {
                    switch (modLootTableSubProvider.getDuplicateDataPolicy()) {
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

                if (modLootTableSubProvider.validateAllEntries()) requiredTables.add(curLootTableLoc);
            }
        }));

        for (ResourceLocation curLootTableLoc : Sets.difference(requiredTables, mappedLootTables.keySet())) {
            lootMiscValidationCtx.reportProblem(String.format("Missing loot table: %s (required by mod of ID: %s)", curLootTableLoc, modId));
        }

        mappedLootTables.forEach((mappedLootTableLoc, mappedLootTable) -> mappedLootTable.validate(lootMiscValidationCtx.setParams(mappedLootTable.getParamSet()).enterElement("{" + mappedLootTableLoc + "}", new LootDataId<>(LootDataType.TABLE, mappedLootTableLoc))));

        Multimap<String, String> lootTableValidationProblems = lootMiscValidationCtx.getProblems();

        if (!lootTableValidationProblems.isEmpty()) {
            lootTableValidationProblems.forEach((problematicLootTable, validationProblem) -> LOGGER.warn("Found validation problem in {}: {}", problematicLootTable, validationProblem));
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
}
