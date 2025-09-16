package com.mememan.nexus.datagen.standard.loot;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.loot.LootBasedPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Loader-agnostic mod-oriented version of {@link BlockLootSubProvider} designed to work based on Nexus API mod-specific
 * datagen configs.
 */
public class StandardBlockLootSubProvider implements ModLootTableSubProvider {
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final Object2ObjectOpenHashMap<Supplier<Block>, BlockPropertyWrapper> mappedModBPWs;
    protected final List<LootBasedPropertyWrapper<?, ?, ?>> mappedLootPWs;

    public StandardBlockLootSubProvider(String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedLootPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(LootBasedPropertyWrapper.class, modId);
        this.mappedModBPWs = BlockPropertyWrapper.getMappedBpws().entrySet()
                .stream()
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen() && BuiltInRegistries.BLOCK.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
    }

    /**
     * Generates the block loot tables for this provider. Handles nullity/missing entry validation. Duplicates and
     * other edge cases are handled in {@link StandardLootProvider}.
     *
     * @param lootTableMapper The {@link BiConsumer} used to map the generated data to specific
     *                        {@linkplain ResourceLocation ResourceLocations}.
     *
     * @see StandardLootProvider#run(CachedOutput)
     */
    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> lootTableMapper) {
        if (!mappedModBPWs.isEmpty()) {
            mappedModBPWs.forEach((blockSupEntry, curBpw) -> {
                boolean shouldValidate = validateAllEntries() || curBpw.getProviderTypeRequisites().getOrDefault(getProviderType(), false);
                Function<Supplier<Block>, LootTable.Builder> blockLootTableBuilder = curBpw.getBlockLootTableMappingFunction();
                ResourceLocation blockLootTableLoc = blockSupEntry.get().getLootTable();

                if (blockLootTableLoc != BuiltInLootTables.EMPTY) { // Atp we don't even need to do any further validation
                    if (blockLootTableBuilder != null) {
                        NexusConstants.LOGGER.debug("[Generating Block Loot Table]: {} -> {}", blockSupEntry.get().getDescriptionId(), blockLootTableLoc);
                        lootTableMapper.accept(blockLootTableLoc, blockLootTableBuilder.apply(blockSupEntry));
                    } else if (shouldValidate) {
                        throw new IllegalStateException(String.format("Block: %s does not have a loot table mapping function, but is required to be validated by mod: %s, either because validateAllEntries is set to true for this provider or the block itself requires validation through BlockPropertyWrapper#getProviderTypeRequisites().", blockSupEntry.get().getDescriptionId(), modId));
                    }
                } else NexusConstants.LOGGER.warn("Skipping block: {} (from mod of ID: {}) because its BlockBehaviour#getLootTable() somehow returned BuiltInLootTables#EMPTY.", blockSupEntry.get().getDescriptionId(), modId);
            });
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
        return String.format("Block Loot Tables [%s]", getModId());
    }
}
