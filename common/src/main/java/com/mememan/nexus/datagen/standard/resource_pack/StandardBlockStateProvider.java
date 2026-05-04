package com.mememan.nexus.datagen.standard.resource_pack;

import com.google.gson.JsonElement;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Loader-agnostic mod-specific blockstate provider in Nexus API. Instanced based on the provided mod ID. Handles
 * generation of blockstates from {@linkplain BlockPropertyWrapper BlockPropertyWrappers} based on their defined
 * {@linkplain BlockPropertyWrapper#getBlockStateDefinition() BlockStateDefinitions}.
 */
public class StandardBlockStateProvider implements ModDataProvider {
    protected final PackOutput.PathProvider blockStatePathProvider;
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final List<BlockPropertyWrapper<? extends Block>> mappedBlockPWs;
    protected final ObjectOpenHashSet<ResourceLocation> trackedBlockStates = new ObjectOpenHashSet<>();

    public StandardBlockStateProvider(PackOutput targetOutput, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        this.blockStatePathProvider = targetOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedBlockPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(BlockPropertyWrapper.class, modId);
    }

    /**
     * Entrypoint for generating all known BPW-based blockstates. Streams through {@link #mappedBlockPWs}, maps them
     * to their serialized counterparts (where applicable), and gathers the stream of futures into a single array.
     *
     * @param cachedOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return A {@link CompletableFuture} that completes when all blockstates have been generated.
     *
     * @see #generateBlockState(CachedOutput, BlockPropertyWrapper, ObjectArrayList)
     */
    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput cachedOutput) {
        ObjectArrayList<CompletableFuture<?>> savedStateFutures = new ObjectArrayList<>();

        return CompletableFuture.allOf(mappedBlockPWs.stream()
                .peek(curPW -> generateBlockState(cachedOutput, curPW, savedStateFutures))
                .flatMap(curPW -> savedStateFutures.stream())
                .toArray(CompletableFuture[]::new));
    }

    /**
     * Generates a blockstate for the provided {@link BlockPropertyWrapper}. Handles missing/{@code null} blockstates
     * accordingly. Defers duplicate handling and serializable entry population (see references below).
     *
     * @param targetOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     * @param targetBPW The {@link BlockPropertyWrapper} to generate a blockstate for.
     * @param savedStateFutures The {@link ObjectArrayList} to add the generated blockstate future to for saving.
     *
     * @param <B> Any {@link Block} type.
     *
     * @see #handleDuplicateState(ResourceLocation, String, Runnable)
     * @see #serializeBlockStateJson(BlockStateDefinition, String, String)
     * @see #run(CachedOutput)
     */
    protected <B extends Block> void generateBlockState(CachedOutput targetOutput, BlockPropertyWrapper<B> targetBPW, ObjectArrayList<CompletableFuture<?>> savedStateFutures) {
        Supplier<B> parentObjSup = targetBPW.getParentObject();
        String blockClassName = parentObjSup.get().getClass().getSimpleName();
        String blockName = targetBPW.getObjectDescriptionId();
        ResourceLocation blockStateRL = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentObjSup.get());

        targetBPW.getBlockStateDefinition().ifPresentOrElse(bsdMappingFunc -> {
            BlockStateDefinition mappedBSD = bsdMappingFunc.apply(parentObjSup);
            JsonElement generatedState = serializeBlockStateJson(mappedBSD, blockClassName, blockName);
            Runnable stateSaveTask = () -> {
                NexusConstants.LOGGER.debug("[{}] [Generating Block State for {}]: {}", getModId(), blockClassName, blockStateRL);

                savedStateFutures.add(DataProvider.saveStable(targetOutput, generatedState, blockStatePathProvider.json(blockStateRL)));
            };

            handleDuplicateState(blockStateRL, blockClassName, stateSaveTask);
        }, () -> {
            if (validateAllEntries() || targetBPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                throw new NullPointerException(String.format("Missing block state mapper for %s: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the object itself requires validation through DataGenBasedPropertyWrapper#getProviderTypeRequisites().", blockClassName, blockName, modId));
            }
        });
    }

    /**
     * Handles duplicate blockstate entries for the given {@code targetBlockId} based on {@link #dupeStrat} before running
     * the provided {@code stateSaveTask} callback.
     *
     * @param targetBlockId The block {@linkplain ResourceLocation id} used to validate presence of duplicates.
     * @param blockClassName The {@code class} name of the target {@link Block} associated with the provided
     *                       {@code targetBlockId}. Primarily used for logging.
     * @param stateSaveTask The callback to run if the blockstate is not a duplicate (or if it is a duplicate and the
     *                      specified {@link #dupeStrat} is {@link DuplicateDataPolicy#OVERRIDE_WARN} or
     *                      {@link DuplicateDataPolicy#OVERRIDE_SILENT}).
     */
    protected void handleDuplicateState(ResourceLocation targetBlockId, String blockClassName, Runnable stateSaveTask) {
        if (!trackedBlockStates.add(targetBlockId)) {
            switch (getDuplicateDataPolicy()) {
                case CRASH -> throw new IllegalArgumentException(String.format("Duplicate block state entry for: %s", targetBlockId));
                case EXCLUDE_WARN -> NexusConstants.LOGGER.warn("Attempted to generate duplicate block state for {} {} (from mod of ID {}), specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", blockClassName, targetBlockId, getModId());
                case EXCLUDE_SILENT -> {}
                case OVERRIDE_WARN -> {
                    NexusConstants.LOGGER.warn("Overriding duplicate block state for {} from mod of ID {}, specified DuplicateDataPolicy is OVERRIDE_WARN.", targetBlockId, getModId());
                    stateSaveTask.run();
                }
                case OVERRIDE_SILENT -> stateSaveTask.run();
            }
        } else stateSaveTask.run();
    }

    @Override
    public @NotNull String getName() {
        return String.format("BlockStates [%s]", getModId());
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
        return NexusProviderTypes.BLOCK_STATE_PROVIDER;
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }

    /**
     * Shortcut delegator method responsible for serializing the provided {@code mappedBSD} based on its
     * {@linkplain BlockStateDefinition#getBlockStateSupplier() BlockStateGenerator}. Handles registry-related
     * exceptions accordingly.
     *
     * @param mappedBSD The {@link BlockStateDefinition} whose generator should be serialized (via
     *                  {@link BlockStateGenerator#get()}).
     * @param blockClassName The {@code class} name associated with the mapped {@link Block} whose block state is being
     *                       generated. Primarily used for logging.
     * @param blockName The name (typically description ID) of the associated {@link Block} whose block state is being
     *                  generated. Primarily used for logging.
     *
     * @return A {@link JsonElement} representing the serialized blockstate JSON.
     *
     * @see BlockStateGenerator
     */
    protected static JsonElement serializeBlockStateJson(BlockStateDefinition mappedBSD, String blockClassName, String blockName) {
        BlockStateGenerator stateGen = Optional.ofNullable(mappedBSD.getBlockStateSupplier())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Failed to retrieve block state generator for %s: %s (No generator present via %s#getBlockStateSupplier())", blockClassName, blockName, mappedBSD.getClass().getSimpleName())));
        return Optional.ofNullable(stateGen.get())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Failed to serialize block state JSON for %s: %s (%s#get() returned null)", blockClassName, blockName, stateGen.getClass().getSimpleName())));
    }
}
