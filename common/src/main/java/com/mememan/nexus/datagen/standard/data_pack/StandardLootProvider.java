package com.mememan.nexus.datagen.standard.data_pack;

import com.google.common.collect.Multimap;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.loot.LootBasedPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Loader-agnostic mod-specific loot table provider in Nexus API. Instanced based on the provided mod ID. Handles
 * the generation of mod-specific loot tables, with additional modifications to allow for standard Nexus configurability.
 * <br></br>
 * Unlike the original {@link LootTableProvider}, this provider processes loot table entries directly via
 * {@link LootBasedPropertyWrapper} rather than taking an input of sub-providers and using those to generate loot
 * table JSONs.
 */
public class StandardLootProvider extends LootTableProvider implements ModDataProvider {
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final List<LootBasedPropertyWrapper<?, ?, ?>> mappedLootPWs;

    public StandardLootProvider(PackOutput targetOutput, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetOutput, Set.of(), List.of());

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedLootPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(LootBasedPropertyWrapper.class, modId);
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput output) {
        final Object2ObjectOpenHashMap<ResourceLocation, LootTable> mappedLootTables = new Object2ObjectOpenHashMap<>();
        final Object2ObjectOpenHashMap<RandomSupport.Seed128bit, ResourceLocation> hashedLootTableLocs = new Object2ObjectOpenHashMap<>();
        final ObjectOpenHashSet<ResourceLocation> trackedTables = new ObjectOpenHashSet<>();
        final ObjectOpenHashSet<ResourceLocation> requiredTables = new ObjectOpenHashSet<>();

        ValidationContext lootMiscValidationCtx = new ValidationContext(LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
            @Nullable
            public <T> T getElement(LootDataId<T> lootDataId) {
                return (T) (lootDataId.type() == LootDataType.TABLE ? mappedLootTables.get(lootDataId.location()) : null);
            }
        });

        populateLootTables(mappedLootTables, trackedTables, requiredTables, hashedLootTableLocs);

        for (ResourceLocation curLootTableLoc : requiredTables) {
            lootMiscValidationCtx.reportProblem(String.format("Missing loot table: %s (required by mod of ID: %s), either because validateAllEntries is set to true for this provider or the object itself requires validation through DataGenBasedPropertyWrapper#getProviderTypeRequisites().", curLootTableLoc, modId));
        }

        mappedLootTables.forEach((mappedLootTableLoc, mappedLootTable) -> mappedLootTable.validate(lootMiscValidationCtx.setParams(mappedLootTable.getParamSet()).enterElement("{" + mappedLootTableLoc + "}", new LootDataId<>(LootDataType.TABLE, mappedLootTableLoc))));

        Multimap<String, String> lootTableValidationProblems = lootMiscValidationCtx.getProblems();

        if (!lootTableValidationProblems.isEmpty()) {
            lootTableValidationProblems.forEach((problematicLootTable, validationProblem) -> LootTableProvider.LOGGER.warn("Found validation problem: {}", validationProblem));
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

    protected <T> void populateLootTables(Map<ResourceLocation, LootTable> mappedLootTables, Set<ResourceLocation> trackedTables, Set<ResourceLocation> requiredLootTables, Map<RandomSupport.Seed128bit, ResourceLocation> hashedLootTableLocs) {
        this.mappedLootPWs.stream()
                .map(curPW -> (LootBasedPropertyWrapper<T, ?, ?>) curPW)
                .forEach(curPW -> {
                    Supplier<T> parentObjSup = curPW.getParentObject();
                    T parentObj = parentObjSup.get();
                    String objectDescId = curPW.getObjectDescriptionId();
                    String objectClassName = parentObj.getClass().getSimpleName();
                    ResourceLocation finalizedLootTableLoc = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentObj)
                            .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for object of type %s: %s", objectClassName, objectDescId)))
                            .withPrefix(curPW.getLootTableDir());

                    curPW.getLootTableBuilder().ifPresentOrElse(lootTableBuilder -> {
                        Runnable lootTableMapper = () -> {
                            ResourceLocation hashedLootTableLoc = hashedLootTableLocs.put(RandomSequence.seedForKey(finalizedLootTableLoc), finalizedLootTableLoc);

                            if (hashedLootTableLoc != null) Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + hashedLootTableLoc + " and " + finalizedLootTableLoc);

                            NexusConstants.LOGGER.debug("[{}] [Generating Loot Table]: {} (for {} '{}')", getModId(), finalizedLootTableLoc, objectClassName, objectDescId);

                            mappedLootTables.put(finalizedLootTableLoc, lootTableBuilder.apply(parentObjSup).build());
                        };

                        handeDuplicateLootTables(finalizedLootTableLoc, trackedTables, objectClassName, objectDescId, lootTableMapper);
                    }, () -> {
                        // Nullity validation is handled later appropriately in this specific provider's case
                        if (validateAllEntries() || curPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) requiredLootTables.add(finalizedLootTableLoc);
                    });
                });
    }

    protected void handeDuplicateLootTables(ResourceLocation targetLootTable, Set<ResourceLocation> trackedTables, String objectClassName, String objectName, Runnable lootTableMapper) {
        if (!trackedTables.add(targetLootTable)) {
            switch (getDuplicateDataPolicy()) {
                case CRASH -> throw new IllegalStateException(String.format("Attempted to generate duplicate loot table %s for %s '%s' (from mod of ID %s), specified DuplicateDataPolicy is CRASH.", targetLootTable, objectClassName, objectName, getModId()));
                case EXCLUDE_WARN -> NexusConstants.LOGGER.warn("Attempted to generate duplicate loot table {} for {} '{}' (from mod of ID {}), specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", targetLootTable, objectClassName, objectName, getModId());
                case EXCLUDE_SILENT -> {}
                case OVERRIDE_WARN -> {
                    NexusConstants.LOGGER.warn("Overriding duplicate loot table {} for {} '{}' (from mod of ID {}), specified DuplicateDataPolicy is OVERRIDE_WARN.", targetLootTable, objectClassName, objectName, getModId());
                    lootTableMapper.run();
                }
                case OVERRIDE_SILENT -> lootTableMapper.run();
            }
        } else lootTableMapper.run();
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
