package com.mememan.nexus.datagen.standard.loot;

import com.mememan.nexus.datagen.standard.ModDataProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Loader-agnostic mod-oriented version of {@link LootTableSubProvider} designed for mod-specific loot table data
 * generation tasks in Nexus API.
 * <br></br>
 * This {@code interface} ensures that loot table data generation tasks are contextually segregated and
 * tied to a specific mod ID.
 */
public interface ModLootTableSubProvider extends LootTableSubProvider, ModDataProvider {

    /**
     * Overridden to throw an {@link UnsupportedOperationException} as this method should not be called in
     * ModLootTableSubProvider, because it isn't a standalone data provider.
     *
     * @param cachedOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return Nothing, as this method should not be called in ModLootTableSubProvider.
     *
     * @throws UnsupportedOperationException This method should not be called in ModLootTableSubProvider.
     */
    @Override
    @NotNull
    default CompletableFuture<?> run(CachedOutput cachedOutput) {
        throw new UnsupportedOperationException("run() should not be called in ModLootTableSubProvider.");
    }

    /**
     * The method responsible for generating loot table data and mapping it to specific
     * {@linkplain ResourceLocation ResourceLocations}.
     * <br></br>
     * Unlike the default {@link #run(CachedOutput)} method present in primary data providers, this method is a
     * {@code void} simply maps the data to generate rather than generating and saving the data itself.
     *
     * @param lootTableMapper The {@link BiConsumer} used to map the generated data to specific
     *                        {@linkplain ResourceLocation ResourceLocations}.
     *
     * @see StandardLootProvider
     */
    @Override
    void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> lootTableMapper);
}
