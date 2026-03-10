package com.mememan.nexus.internal.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.loader.ModLoader;
import com.mememan.nexus.loader.RegistryHookManager;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.template.event.blueprint.common.LevelDataEventBlueprint;
import com.mememan.nexus.template.event.blueprint.common.RegistryEventBlueprint;
import com.mememan.nexus.template.event.def.common.RegistryEvent;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Internal handler {@code class} responsible for agnostically-handling registry data per-save via queried data from
 * {@link NexusServices#REGISTRAR}.
 * <br></br>
 * Event listeners here only affect saves from their sources. That is to say, they only run on the side that has the
 * level data. For example, if connecting to a dedicated server that has Nexus, these events will only fire on the dedicated
 * server.
 * <br></br>
 * This handler {@code class} serves as a basic registry lifecycle tracker that updates data per-save and has nothing to
 * do with registry data being synced from dedicated servers to the client (as that is handled by the mod loader itself).
 * The pipeline looks something like this:
 * <ul>
 *     <li>
 *         <b>Startup (post-load/after the game starts and all mods have loaded)</b> => Capture initial registry state, including mods.
 *         <ul>
 *             <li>
 *                 {@link #populateRegistryEntriesFromMemory(boolean)}
 *             </li>
 *             <li>
 *                 Add event listeners ({@link #handleLevelRegistryData()})
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>Level load/creation</b> => Capture registry state after mod-loaders have done their thing.
 *         <ul>
 *             <li>
 *                 <b>World load/creation</b>: Update registry data and fire {@link RegistryEvent.MissingRegistryEntriesEvent} events for
 *                 missing entries ({@link #populateRegistryEntriesFromMemory(boolean)},
 *                 {@link #updateRegistryData(LevelStorageSource.LevelDirectory)}).
 *                 Use captured registry state and create a file called 'RegistryDataView.dat' inside the
 *                 level's 'data' directory ({@link #writeOrAppendAppellationData(LevelStorageSource.LevelDirectory, CompoundTag)}).
 *             </li>
 *             <li>
 *                 Use captured registry state and create a file called 'RegistryDataLock.dat' inside the level's 'data'
 *                 directory ({@link #writeOrAppendAppellationData(LevelStorageSource.LevelDirectory, CompoundTag)}
 *                 + {@link #writeOrAppendBlockedIdData(LevelStorageSource.LevelDirectory, CompoundTag)}).
 *                 <br></br>
 *                 The file pretty much serves as a sort of data store that gets updated whenever level data is saved in
 *                 order to keep track of states and allow for the injection of stuff like
 *                 {@link RegistryEvent.MissingRegistryEntriesEvent}, which would allow us to properly block IDs from being
 *                 re-used, register dummy objects, etc.
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @see Registrar#getRegistryHookManager()
 */
@PostInit
public final class NexusRegistryDataManager {
    public static final LevelResource LEVEL_DATA_DIR = new LevelResource("data");
    public static final LevelResource REGISTRY_DATA_LOCK = new LevelResource("RegistryDataLock.dat");
    public static final LevelResource REGISTRY_DATA_VIEW = new LevelResource("RegistryDataView.dat");
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> CURRENT_APPELLATIONS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> CURRENT_APPELLATIONS_VIEW = Collections.unmodifiableMap(CURRENT_APPELLATIONS);
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<ResourceLocation, Integer>> REGISTRY_ENTRIES_FROM_MEMORY = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<ResourceLocation, Integer>> UPDATED_REGISTRY_ENTRIES_FROM_MEMORY = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> CURRENT_BLOCKED_IDS = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> CURRENT_BLOCKED_IDS_VIEW = Collections.unmodifiableMap(CURRENT_BLOCKED_IDS);
    private static final RegistryHookManager.ActiveRegistryMapper<Object> ALIAS_SUBSTITUTOR = ((targetRegistry, rawRegistryEntry) -> {
        return rawRegistryEntry.isMissing() ? targetRegistry.get(NexusConstants.prefix("test_block_2")) : rawRegistryEntry.objValue();
    });
    private static final RegistryHookManager.ActiveRegistryMapper<Object> ID_BLOCKER = ((targetRegistry, rawRegistryEntry) -> {
        return rawRegistryEntry.objValue();
    });

    static {
        populateRegistryEntriesFromMemory(false); // Initial state post-load (not really used for anything atm, but still nice to have access to JIC)
        handleLevelRegistryData();
    }

    private static <T> void populateRegistryEntriesFromMemory(boolean trackingUpdatedState) {
        if (trackingUpdatedState) UPDATED_REGISTRY_ENTRIES_FROM_MEMORY.clear();

        BuiltInRegistries.REGISTRY.entrySet().forEach(curEntry -> {
            Registry<T> curRegistry = (Registry<T>) curEntry.getValue();
            Supplier<HashBiMap<ResourceLocation, Integer>> pooledRegData = () -> curRegistry.entrySet().stream()
                    .sorted(Comparator.comparingInt((mappedRegEntry) -> curRegistry.getId(mappedRegEntry.getValue())))
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey().location(), curRegistry.getId(entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, AbstractMap.SimpleEntry::getValue, (e1, e2) -> e1, HashBiMap::create));

            if (!trackingUpdatedState) REGISTRY_ENTRIES_FROM_MEMORY.computeIfAbsent(curEntry.getKey(), k -> pooledRegData.get());
            else UPDATED_REGISTRY_ENTRIES_FROM_MEMORY.computeIfAbsent(curEntry.getKey(), k -> pooledRegData.get());
        }); // This can serve as our "active" registry instance, from which global registry data is pulled and compared with level registry data
    }

    private static void handleLevelRegistryData() {
/*        NexusServices.REGISTRAR.getRegistryHookManager().appellate(
                NexusConstants.prefix("test_block"),
                NexusConstants.prefix("test_block_2"),
                Registries.BLOCK
        );*/

        // Test
        RegistryEventBlueprint.MISSING_REGISTRY_ENTRIES.onEvent(event -> {
            List<RegistryEvent.MissingRegistryEntriesEvent.WrappedEntry<?>> missingEntries = event.getMissingEntries();

            missingEntries.forEach(entry -> {
                if (entry.getOldKey().equals(NexusConstants.prefix("test_block"))) {
                    entry.attemptRemap(NexusConstants.prefix("test_block_2"));

                    NexusConstants.LOGGER.info("Found missing entry for test_block: {}", entry);
                }
            });

            NexusConstants.LOGGER.info("Missing entries: {}", event.getMissingEntries());
        });

        // Platform-agnostic listeners for Nexus API level data n stuff
        LevelDataEventBlueprint.SAVE_LEVEL_DATA_POST_LOADER.onEvent(event -> {
            LevelStorageSource.LevelDirectory rootLevelDir = event.getLevelDirectory();
            File dataLevelDirectory = rootLevelDir.resourcePath(LEVEL_DATA_DIR).toFile();
            File regDataViewFile = new File(dataLevelDirectory, REGISTRY_DATA_VIEW.getId());
            File regDataLockFile = new File(dataLevelDirectory, REGISTRY_DATA_LOCK.getId());
            CompoundTag regDataViewTag = getOrCreateRegistryDataViewTag(rootLevelDir);
            CompoundTag regBackupDataTag = getOrCreateRegistryDataLockTag(rootLevelDir);

            writeOrAppendRegistryDataView(rootLevelDir, regDataViewTag);
            writeOrAppendAppellationData(rootLevelDir, regBackupDataTag);
            writeOrAppendBlockedIdData(rootLevelDir, regBackupDataTag);

            // Separate for clearer logging
            try {
                try (FileOutputStream fileOutputStream = new FileOutputStream(regDataLockFile)) {
                    NbtIo.writeCompressed(regBackupDataTag, fileOutputStream);
                }
            } catch (IOException e) {
                NexusConstants.LOGGER.warn("Failed to create RegistryDataLock.dat for level '{}'. Any missing entries from registries, regardless of whether they persist or are synced, may not be recoverable if a bug prevents your mod-loader's ({}) registry mechanism from properly caching orphaned registry entries or blocking previous IDs associated with missing entries from being used by new ones.", rootLevelDir.directoryName(), NexusServices.PLATFORM_MANAGER.getPlatform().getPlatformName(), e);
            }

            try {
                try (FileOutputStream fileOutputStream = new FileOutputStream(regDataViewFile)) {
                    NbtIo.writeCompressed(regDataViewTag, fileOutputStream);
                }
            } catch (IOException e) {
                NexusConstants.LOGGER.warn("Failed to create RegistryDataView.dat for level '{}'. Any missing entries from registries, regardless of whether they persist or are synced, may not be recoverable if a bug prevents your mod-loader's ({}) registry mechanism from properly caching orphaned registry entries or blocking previous IDs associated with missing entries from being used by new ones.", rootLevelDir.directoryName(), NexusServices.PLATFORM_MANAGER.getPlatform().getPlatformName(), e);
            }
        });

        LevelDataEventBlueprint.LOAD_LEVEL_DATA_POST_LOADER.onEvent(event -> {
            LevelStorageSource.LevelDirectory rootLevelDir = event.getLevelDirectory();
            CompoundTag regBackupDataTag = getOrCreateRegistryDataLockTag(rootLevelDir);

            if (!regBackupDataTag.isEmpty()) {
                CompoundTag appellationsTag = regBackupDataTag.getCompound("Appellations");
                CompoundTag safeguardedIdsTag = regBackupDataTag.getCompound("SafeguardedIds");

                readAndUpdateAppellationData(appellationsTag);
                readAndUpdateBlockedIdData(safeguardedIdsTag);
            }
        });

        LevelDataEventBlueprint.LOAD_LEVEL_DATA_POST_LOADER.onEvent(event -> {
            LevelStorageSource.LevelDirectory rootLevelDir = event.getLevelDirectory();

            populateRegistryEntriesFromMemory(true);
            updateRegistryData(rootLevelDir);
        }, 1);

        if (NexusServices.PLATFORM_MANAGER.getPlatform().equals(ModLoader.FORGE)) {
            /*
             * In our case, we don't really care whether Forge considers a registry persistent, since appellations are meant
             * to fill in for active registry entries regardless of whether said registries save to disk, because we're
             * using that data as a fallback whenever registry lookups fail.
             *
             * That being said, we're still respecting Forge's rules by only adding our appellations to registries that
             * are already persisted to disk in level.dat.
             */
            LevelDataEventBlueprint.SAVE_LEVEL_DATA_POST_LOADER.onEvent(event -> { // Allow Forge to keep track of aliases added through Nexus API in-memory by saving data to level.dat, which is where they store (serializable) modded registry data
                CompoundTag levelDataTag = event.getLevelDataTag();
                CompoundTag rootFMLTag = levelDataTag.getCompound("fml");
                CompoundTag serializedRegistriesTag = rootFMLTag.getCompound("Registries");

                event.getRegistriesView().registries()
                        .map(curRegEntry -> event.getRegistriesView().registry(curRegEntry.key()))
                        .filter(Optional::isPresent) // JIC
                        .map(Optional::get)
                        .forEach(curRegistry -> {
                            ResourceLocation curRegId = curRegistry.key().location();
                            CompoundTag curRegTag = serializedRegistriesTag.getCompound(curRegId.toString()); // .registry() here refers to the root registry
                            ListTag aliasesTag = curRegTag.getList("aliases", Tag.TAG_COMPOUND);
                            Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> appellations = NexusServices.REGISTRAR.getRegistryHookManager().getAppellations();

                            if (!appellations.isEmpty()) {
                                appellations.entrySet().stream()
                                        .filter(curEntry -> Objects.equals(curEntry.getKey(), curRegistry.key()))
                                        .map(Map.Entry::getValue)
                                        .findFirst()
                                        .ifPresent((appellatedIds) -> {
                                            if (!appellatedIds.isEmpty()) {
                                                appellatedIds.forEach((baseId, aliases) -> {
                                                    aliases.forEach(aliasId -> {
                                                        if (!Objects.equals(baseId, aliasId) && aliasesTag.stream().map(CompoundTag.class::cast).noneMatch(curAliasTag -> Objects.equals(curAliasTag.getString("K"), baseId.toString()) && Objects.equals(curAliasTag.getString("V"), aliasId.toString()))) {
                                                            CompoundTag aliasTag = new CompoundTag();

                                                            aliasTag.putString("K", baseId.toString());
                                                            aliasTag.putString("V", aliasId.toString());

                                                            aliasesTag.add(aliasTag);
                                                        }
                                                    });
                                                });
                                            }
                                        });
                            }
                        });
            });

            LevelDataEventBlueprint.LOAD_LEVEL_DATA_POST_LOADER.onEvent(event -> {
                CompoundTag levelDataTag = event.getLevelDataTag();
                CompoundTag rootFMLTag = levelDataTag.getCompound("fml");
                CompoundTag serializedRegistriesTag = rootFMLTag.getCompound("Registries");

                serializedRegistriesTag.getAllKeys().forEach(curRegKey -> {
                    ResourceKey<Registry<Object>> curRegResourceKey = ResourceKey.createRegistryKey(new ResourceLocation(curRegKey));
                    CompoundTag curRegTag = serializedRegistriesTag.getCompound(curRegKey);
                    ListTag aliasesTag = curRegTag.getList("aliases", Tag.TAG_COMPOUND);

                    aliasesTag.forEach(aliasTag -> {
                        CompoundTag compoundAliasTag = (CompoundTag) aliasTag;

                        ResourceLocation aliasId = new ResourceLocation(compoundAliasTag.getString("V"));
                        ResourceLocation baseId = new ResourceLocation(compoundAliasTag.getString("K"));

                        List<ResourceLocation> mappedAliases = CURRENT_APPELLATIONS
                                .computeIfAbsent(curRegResourceKey, k -> new Object2ObjectLinkedOpenHashMap<>())
                                .computeIfAbsent(baseId, k -> new ObjectArrayList<>());

                        if (!mappedAliases.contains(aliasId)) mappedAliases.add(aliasId);
                    });
                });
            }, 1);
        }
    }

    private static void writeOrAppendRegistryDataView(LevelStorageSource.LevelDirectory rootLevelDir, CompoundTag rootRegistryDataViewTag) {
        if (rootRegistryDataViewTag.isEmpty() || !rootRegistryDataViewTag.contains("RegistryData") || rootRegistryDataViewTag.getCompound("RegistryData").isEmpty()) writeRegistryDataView(rootRegistryDataViewTag);
        else {
            try { // This'll let us keep track of any remapping that happens, since our events fire AFTER mod-loaders have done their thing(s)
                CompoundTag registryDataTag = rootRegistryDataViewTag.getCompound("RegistryData");

                registryDataTag.getAllKeys().forEach(curRegKey -> {
                    CompoundTag curRegDataTag = registryDataTag.getCompound(curRegKey);
                    ResourceKey<Registry<Object>> curRegResourceKey = ResourceKey.createRegistryKey(new ResourceLocation(curRegKey));
                    BiMap<ResourceLocation, Integer> mappedRegEntries = UPDATED_REGISTRY_ENTRIES_FROM_MEMORY.get(curRegResourceKey);

                    if (mappedRegEntries != null && !mappedRegEntries.isEmpty()) {
                        BiMap<ResourceLocation, Integer> regEntriesToAppend = HashBiMap.create(mappedRegEntries);

                        curRegDataTag.getAllKeys().stream()
                                .map(ResourceLocation::new)
                                .filter(regEntryId -> Optional.ofNullable(regEntriesToAppend.get(regEntryId)).orElse(-1) == curRegDataTag.getCompound(regEntryId.toString()).getInt("LastKnownId"))
                                .forEach(regEntriesToAppend::remove); // Only process differences: Either entirely new registry entries, or entries whose IDs have changed

                        if (!regEntriesToAppend.isEmpty()) {
                            regEntriesToAppend.forEach((curRegEntryId, curNumericalId) -> {
                                if (curNumericalId == -1) return; // Don't purge the actual last known ID if the object is actually missing, as we may need that information later on

                                CompoundTag regEntryTag = new CompoundTag();

                                regEntryTag.putInt("LastKnownId", curNumericalId);
                                curRegDataTag.put(curRegEntryId.toString(), regEntryTag);
                            });
                        }
                    }
                });
            } catch (Exception e) {
                NexusConstants.LOGGER.error("Failed to write data to existing RegistryDataView.dat for level '{}' (perhaps the file contains illegal tags/syntax?). Overwriting existing data. Old/corrupted file will be renamed to 'RegistryDataView.dat_old'.", rootLevelDir.directoryName(), e);

                File regDataViewFile = new File(rootLevelDir.resourcePath(LEVEL_DATA_DIR).toFile(), REGISTRY_DATA_VIEW.getId());

                try {
                    Files.move(regDataViewFile.toPath(), regDataViewFile.toPath().resolveSibling("RegistryDataView.dat_old"), StandardCopyOption.REPLACE_EXISTING);
                    NexusConstants.LOGGER.info("Renamed old/corrupted RegistryDataView.dat for level '{}' to 'RegistryDataView.dat_old'", rootLevelDir.directoryName());
                } catch (IOException ioException) {
                    NexusConstants.LOGGER.error("Failed to rename old/corrupted RegistryDataView.dat for level '{}'. Previous registry data may not be recoverable.", rootLevelDir.directoryName(), ioException);
                }

                writeRegistryDataView(rootRegistryDataViewTag);
            }
        }
    }

    private static void writeRegistryDataView(CompoundTag rootRegistryDataViewTag) {
        populateRegistryEntriesFromMemory(true); // Likely our first time loading into this world (i.e. creating it), or we otherwise need to double-check and overwrite everything just to be sure

        if (UPDATED_REGISTRY_ENTRIES_FROM_MEMORY.isEmpty()) throw new IllegalStateException("No registry data found in memory... how'd you even get here?");

        CompoundTag registryDataTag = new CompoundTag();

        UPDATED_REGISTRY_ENTRIES_FROM_MEMORY.forEach((curRegKey, mappedRegEntries) -> {
            CompoundTag curRegDataTag = new CompoundTag();

            writeRegistryEntryData(registryDataTag, curRegDataTag, curRegKey, mappedRegEntries);
        });

        rootRegistryDataViewTag.put("RegistryData", registryDataTag);
    }

    private static void writeRegistryEntryData(CompoundTag regDataTag, CompoundTag regEntryDataTag, ResourceKey<? extends Registry<?>> targetRegKey, BiMap<ResourceLocation, Integer> mappedRegEntries) {
        mappedRegEntries.forEach((curRegEntryId, curNumericalId) -> {
            CompoundTag regEntryTag = new CompoundTag();

            regEntryTag.putInt("LastKnownId", Optional.ofNullable(curNumericalId).orElse(-1));
            regEntryDataTag.put(curRegEntryId.toString(), regEntryTag);
        });

        regDataTag.put(targetRegKey.location().toString(), regEntryDataTag);
    }

    private static CompoundTag getOrCreateRegistryDataViewTag(LevelStorageSource.LevelDirectory rootLevelDir) {
        File dataLevelDirectory = rootLevelDir.resourcePath(LEVEL_DATA_DIR).toFile();
        File regDataViewFile = new File(dataLevelDirectory, REGISTRY_DATA_VIEW.getId());
        CompoundTag regDataViewTag;

        try {
            FileInputStream fileInputStream = new FileInputStream(regDataViewFile);

            regDataViewTag = NbtIo.readCompressed(fileInputStream);

            fileInputStream.close();
        } catch (Exception e) {
            regDataViewTag = new CompoundTag();
        }

        return regDataViewTag;
    }

    private static void writeOrAppendAppellationData(LevelStorageSource.LevelDirectory rootLevelDir, CompoundTag rootRegistryDataTag) {
        if (rootRegistryDataTag.isEmpty() || !rootRegistryDataTag.contains("Appellations") || rootRegistryDataTag.getCompound("Appellations").isEmpty()) writeAppellationData(rootRegistryDataTag);
        else {
            try {
                Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> appellations = new Object2ObjectLinkedOpenHashMap<>(NexusServices.REGISTRAR.getRegistryHookManager().getAppellations()); // Need to wrap cuz modification:tm:

                if (appellations.isEmpty()) return;

                CompoundTag appellationsTag = rootRegistryDataTag.getCompound("Appellations");
                Set<String> potentiallyModifiedRegAppellations = new ObjectLinkedOpenHashSet<>(appellationsTag.getAllKeys());

                potentiallyModifiedRegAppellations.retainAll(appellations.keySet().stream()
                        .map(ResourceKey::location)
                        .map(ResourceLocation::toString)
                        .collect(Collectors.toCollection(ObjectArrayList::new)));

                potentiallyModifiedRegAppellations.forEach(curRegKey -> { // Resolve any differences between appellations in existing registry key entries
                    ResourceKey<Registry<Object>> curRegResourceKey = ResourceKey.createRegistryKey(new ResourceLocation(curRegKey));
                    Object2ObjectMap<ResourceLocation, List<ResourceLocation>> mappedRegistryAppellations = appellations.get(curRegResourceKey);

                    if (!appellations.containsKey(curRegResourceKey) || mappedRegistryAppellations.isEmpty()) return; // JIC

                    CompoundTag curRegAppellationsTag = appellationsTag.getCompound(curRegKey);

                    // TODO Optimise ts
                    mappedRegistryAppellations.forEach((baseRegEntryId, aliases) -> {
                        if (curRegAppellationsTag.contains(baseRegEntryId.toString())) {
                            CompoundTag baseIdTag = curRegAppellationsTag.getCompound(baseRegEntryId.toString());
                            ListTag baseIdAliasesTag = baseIdTag.getList("Aliases", Tag.TAG_COMPOUND);

                            aliases.forEach(aliasId -> {
                                if (!Objects.equals(baseRegEntryId, aliasId) && baseIdAliasesTag.stream().map(CompoundTag.class::cast).noneMatch(curAliasTag -> Objects.equals(curAliasTag.getString("Alias"), aliasId.toString()))) {
                                    CompoundTag aliasTag = new CompoundTag();

                                    aliasTag.putString("Alias", aliasId.toString());
                                    baseIdAliasesTag.add(aliasTag);
                                }
                            });
                        } else {
                            writeRegistryEntryAppellationData(curRegAppellationsTag, new CompoundTag(), baseRegEntryId, aliases);
                        }
                    });
                });

                potentiallyModifiedRegAppellations.stream() // Remove the registries we just resolved the differences in (mapping manually cuz equality checks will pass regardless + lazy:tm:)
                        .map(curRegKey -> ResourceKey.createRegistryKey(new ResourceLocation(curRegKey)))
                        .forEach(appellations.keySet()::remove);

                appellations.forEach((curRegKey, curAppellatedIds) -> {
                    CompoundTag regAppellationDataTag = new CompoundTag();

                    curAppellatedIds.forEach((baseRegEntryId, aliases) -> {
                        CompoundTag entryAppellationDataTag = new CompoundTag();

                        writeRegistryEntryAppellationData(regAppellationDataTag, entryAppellationDataTag, baseRegEntryId, aliases);
                    });

                    appellationsTag.put(curRegKey.location().toString(), regAppellationDataTag);
                });
            } catch (Exception e) {
                NexusConstants.LOGGER.error("Failed to write data to existing RegistryDataLock.dat for level '{}' (perhaps the file contains illegal tags/syntax?). Overwriting existing data. Old/corrupted file will be renamed to 'RegistryDataLock.dat_old'.", rootLevelDir.directoryName(), e);

                File regDataLockFile = new File(rootLevelDir.resourcePath(LEVEL_DATA_DIR).toFile(), REGISTRY_DATA_LOCK.getId());

                try {
                    Files.move(regDataLockFile.toPath(), regDataLockFile.toPath().resolveSibling("RegistryDataLock.dat_old"), StandardCopyOption.REPLACE_EXISTING);
                    NexusConstants.LOGGER.info("Renamed old/corrupted RegistryDataLock.dat for level '{}' to 'RegistryDataLock.dat_old'", rootLevelDir.directoryName());
                } catch (IOException ioException) {
                    NexusConstants.LOGGER.error("Failed to rename old/corrupted RegistryDataLock.dat for level '{}'. Previous registry appellations may not be recoverable.", rootLevelDir.directoryName(), ioException);
                }

                writeAppellationData(rootRegistryDataTag);
            }
        }
    }

    private static void writeAppellationData(CompoundTag rootRegDataTag) {
        Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> appellations = NexusServices.REGISTRAR.getRegistryHookManager().getAppellations();

        if (!appellations.isEmpty()) {
            CompoundTag appellationDataTag = new CompoundTag();
            CompoundTag mappedRegEntriesTag = new CompoundTag();

            appellations.forEach((regKey, appellatedIds) -> {
                if (!appellatedIds.isEmpty()) {
                    appellatedIds.forEach((baseRegEntryId, aliases) -> {
                        CompoundTag entryAppellationDataTag = new CompoundTag();

                        writeRegistryEntryAppellationData(mappedRegEntriesTag, entryAppellationDataTag, baseRegEntryId, aliases);
                    });
                }

                appellationDataTag.put(regKey.location().toString(), mappedRegEntriesTag);
            });

            rootRegDataTag.put("Appellations", appellationDataTag);
        }
    }

    private static void writeRegistryEntryAppellationData(CompoundTag registryAppellationDataTag, CompoundTag individualEntryTag, ResourceLocation baseId, List<ResourceLocation> appellations) {
        ListTag aliasListTag = new ListTag();

        appellations.forEach(aliasId -> {
            if (!Objects.equals(baseId, aliasId)) {
                CompoundTag aliasTag = new CompoundTag();

                aliasTag.putString("Alias", aliasId.toString());
                aliasListTag.add(aliasTag);
            }
        });

        individualEntryTag.put("Aliases", aliasListTag);
        registryAppellationDataTag.put(baseId.toString(), individualEntryTag);
    }

    private static CompoundTag getOrCreateRegistryDataLockTag(LevelStorageSource.LevelDirectory rootLevelDir) {
        File dataLevelDirectory = rootLevelDir.resourcePath(LEVEL_DATA_DIR).toFile();
        File regDataLockFile = new File(dataLevelDirectory, REGISTRY_DATA_LOCK.getId());
        CompoundTag regBackupDataTag;

        try {
            FileInputStream fileInputStream = new FileInputStream(regDataLockFile);

            regBackupDataTag = NbtIo.readCompressed(fileInputStream);

            fileInputStream.close();
        } catch (Exception e) {
            regBackupDataTag = new CompoundTag();
        }

        return regBackupDataTag;
    }

    private static void writeOrAppendBlockedIdData(LevelStorageSource.LevelDirectory rootLevelDir, CompoundTag rootRegistryDataTag) {
        if (rootRegistryDataTag.isEmpty() || !rootRegistryDataTag.contains("SafeguardedIds") || rootRegistryDataTag.getCompound("SafeguardedIds").isEmpty()) blockMissingEntries(rootRegistryDataTag);
        else {
            Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> blockedIds = NexusServices.REGISTRAR.getRegistryHookManager().getBlockedIds();

            if (blockedIds.isEmpty()) return;

            blockedIds.forEach((curRegKey, mappedBlockedIds) -> {

            });
        }
    }

    private static void blockMissingEntries(CompoundTag rootRegistryDataTag) {
        CompoundTag safeguardedIdsTag = new CompoundTag();

        Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> blockedIds = NexusServices.REGISTRAR.getRegistryHookManager().getBlockedIds();

        if (!blockedIds.isEmpty()) {
            blockedIds.forEach((curRegKey, mappedBlockedIds) -> {
                CompoundTag manuallyBlockedIdsTag = new CompoundTag();
                CompoundTag missingBlockedIdsTag = new CompoundTag();

                mappedBlockedIds.forEach((curNumericalId, curRegEntryId) -> {
                    if (curRegEntryId != null) { // Skip over straight-up non-existent entries
                        ResourceLocation curRegId = curRegKey.location();

                        if (curNumericalId != -1) {
                            CompoundTag manuallyBlockedEntryTag = manuallyBlockedIdsTag.contains(curRegId.toString())
                                    ? manuallyBlockedIdsTag.getCompound(curRegId.toString())
                                    : Util.make(new CompoundTag(), tag -> manuallyBlockedIdsTag.put(curRegId.toString(), tag));
                            CompoundTag regEntryTag = new CompoundTag();

                            regEntryTag.putInt("LastKnownId", curNumericalId);

                            manuallyBlockedEntryTag.put(curRegEntryId.toString(), regEntryTag);
                        } else {
                            CompoundTag missingIdTag = missingBlockedIdsTag.contains(curRegId.toString())
                                    ? missingBlockedIdsTag.getCompound(curRegId.toString())
                                    : Util.make(new CompoundTag(), tag -> missingBlockedIdsTag.put(curRegId.toString(), tag));

                            missingIdTag.putString("EntryId", curRegEntryId.toString());
                        }
                    }
                });

                if (!manuallyBlockedIdsTag.isEmpty()) safeguardedIdsTag.put("ManuallyBlockedIds", manuallyBlockedIdsTag);
                if (!missingBlockedIdsTag.isEmpty()) safeguardedIdsTag.put("MissingBlockedIds", missingBlockedIdsTag);
            });

            rootRegistryDataTag.put("SafeguardedIds", safeguardedIdsTag);
        }
    }

    private static void readAndUpdateAppellationData(CompoundTag appellationDataTag) {
        if (!appellationDataTag.isEmpty()) {
            CURRENT_APPELLATIONS.clear(); // Refresh entries if there's actually an update (e.g. loaded into a different world)

            appellationDataTag.getAllKeys().forEach(curRegKey -> {
                ResourceKey<Registry<Object>> curRegResourceKey = ResourceKey.createRegistryKey(new ResourceLocation(curRegKey));
                CompoundTag mappedRegEntriesTag = appellationDataTag.getCompound(curRegKey);
                Object2ObjectMap<ResourceLocation, List<ResourceLocation>> mappedAppellations = CURRENT_APPELLATIONS.computeIfAbsent(curRegResourceKey, k -> new Object2ObjectLinkedOpenHashMap<>());

                mappedRegEntriesTag.getAllKeys().forEach(curEntryKey -> {
                    CompoundTag entryAppellationDataTag = mappedRegEntriesTag.getCompound(curEntryKey);
                    ListTag aliasListTag = entryAppellationDataTag.getList("Aliases", Tag.TAG_COMPOUND);
                    List<ResourceLocation> aliases = aliasListTag.stream().map(CompoundTag.class::cast).map(curAliasTag -> new ResourceLocation(curAliasTag.getString("Alias"))).collect(Collectors.toCollection(ObjectArrayList::new));

                    // Map to base ID
                    List<ResourceLocation> mappedAliases = mappedAppellations.computeIfAbsent(new ResourceLocation(curEntryKey), k -> new ObjectArrayList<>());

                    // JIC
                    if (!aliases.isEmpty()) mappedAliases.addAll(aliases);
                });
            });
        }
    }

    private static void readAndUpdateBlockedIdData(CompoundTag safeguardedIdsTag) {
        if (!safeguardedIdsTag.isEmpty()) {
            CURRENT_BLOCKED_IDS.clear();
        }
    }

    private static <T> void updateRegistryData(LevelStorageSource.LevelDirectory rootLevelDir) {
        CompoundTag rootRegViewTag = getOrCreateRegistryDataViewTag(rootLevelDir).getCompound("RegistryData");
        Map<ResourceKey<Registry<T>>, Object2IntMap<ResourceLocation>> subEntries = new Object2ObjectLinkedOpenHashMap<>();

        if (!rootRegViewTag.isEmpty()) { // First: Handle missing entries in-memory that used to be present within whatever save we're loading
            rootRegViewTag.getAllKeys().forEach(regKey -> {
                ResourceKey<Registry<T>> regResourceKey = ResourceKey.createRegistryKey(new ResourceLocation(regKey));
                Registry<T> curRegistry = (Registry<T>) BuiltInRegistries.REGISTRY.get((ResourceKey) regResourceKey);

                if (curRegistry == null) {
                    NexusConstants.LOGGER.warn("Registry '{}' not found in root registry, skipping registry data update.", regKey);
                    return;
                }

                CompoundTag regEntriesTag = rootRegViewTag.getCompound(regKey);
                List<RegistryEvent.MissingRegistryEntriesEvent.WrappedEntry<T>> missingEntries = new ObjectArrayList<>(); // Supply all missing entries with default values

                regEntriesTag.getAllKeys().forEach(regEntryId -> {
                    CompoundTag regEntryDataTag = regEntriesTag.getCompound(regEntryId);
                    ResourceLocation regEntryRLID = new ResourceLocation(regEntryId);
                    int storedId = regEntryDataTag.getInt("LastKnownId");
                    int fetchedId = getLastKnownId(regResourceKey, regEntryRLID, null);

                    if (storedId != -1 && fetchedId == -1) { // storedId should never be -1, but as always, JIC
                        missingEntries.add(new RegistryEvent.MissingRegistryEntriesEvent.WrappedEntry<>(regResourceKey, storedId, regEntryRLID));
                    }
                });

                RegistryEvent.MissingRegistryEntriesEvent<T> event = new RegistryEvent.MissingRegistryEntriesEvent<>(curRegistry, missingEntries);

                RegistryEventBlueprint.MISSING_REGISTRY_ENTRIES.fireEvent(event); // Remapping is handled internally within the event itself

                event.getMissingEntries().forEach(wrappedEntry -> {
                    subEntries.computeIfAbsent(regResourceKey, k -> new Object2IntOpenHashMap<>())
                            .put(wrappedEntry.getOldKey(), wrappedEntry.getLastKnownId());
                });
            });
        }

        // Next: Update registry data with respect to loader-specific API implementations (appellations, blocked IDs)
        RegistryHookManager globalRegHookManager = NexusServices.REGISTRAR.getRegistryHookManager();
        Map<ResourceKey<? extends Registry<?>>, BiMap<Integer, ResourceLocation>> blockedIds = globalRegHookManager.getBlockedIds();

        if (!subEntries.isEmpty()) {
            subEntries.forEach((curRegKey, missingEntries) -> {

            });
        }

        if (!blockedIds.isEmpty()) {
            blockedIds.forEach((curRegKey, mappedBlockedIds) -> {
                globalRegHookManager.updateActiveRegistry((ResourceKey<Registry<Object>>) curRegKey, ID_BLOCKER);
            });
        }
    }

    private static int getLastKnownId(ResourceKey<? extends Registry<?>> targetRegKey, ResourceLocation targetRegEntry, @Nullable LevelStorageSource.LevelDirectory rootLevelDir) {
        AtomicInteger lastKnownId = new AtomicInteger(-1);

        // First, check registries in memory to see if the entry already exists
        BiMap<ResourceLocation, Integer> mappedRegEntries = UPDATED_REGISTRY_ENTRIES_FROM_MEMORY.get(targetRegKey);

        if (mappedRegEntries != null && mappedRegEntries.containsKey(targetRegEntry)) lastKnownId.set(Optional.ofNullable(mappedRegEntries.get(targetRegEntry)).orElse(-1));

        if (rootLevelDir != null) { // Still no ID: Check RegistryDataView.dat for the last known ID (if you somehow got -1 for an existing mapping: how'd it even reach there? lol)
            CompoundTag registryDataTag = getOrCreateRegistryDataViewTag(rootLevelDir);
            CompoundTag curRegDataTag = registryDataTag.getCompound(targetRegKey.location().toString());
            CompoundTag curRegEntryTag = curRegDataTag.getCompound(targetRegEntry.toString());
            int lastKnownIdParsed = Optional.of(curRegEntryTag.getInt("LastKnownId"))
                    .filter(id -> id != 0) // Assuming the default entry is always the first one to be registered (which, well, it should be)
                    .orElse(-1);

            lastKnownId.set(lastKnownIdParsed);
        }

        return lastKnownId.get();
    }

    public static Map<ResourceKey<? extends Registry<?>>, Object2ObjectMap<ResourceLocation, List<ResourceLocation>>> getCurrentAppellations() {
        return CURRENT_APPELLATIONS_VIEW;
    }
}
