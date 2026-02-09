package com.mememan.nexus.internal.registry;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.loader.ModLoader;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.template.event.blueprint.common.LevelDataEventBlueprint;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Internal handler {@code class} responsible for agnostically-handling registry data per-save via queried data from
 * {@link NexusServices#REGISTRAR}.
 *
 * @see Registrar#appellate(ResourceLocation, ResourceLocation, ResourceKey)
 */
@PostInit
public final class NexusRegistryDataManager {
    public static final LevelResource LEVEL_DATA_DIR = new LevelResource("data");
    public static final LevelResource REGISTRY_DATA_LOCK = new LevelResource("RegistryDataLock.dat");

    static {
        handleLevelRegistryData();
    }

    private static void handleLevelRegistryData() {
        NexusServices.REGISTRAR.appellate(new ResourceLocation("dirt"), new ResourceLocation("soil"), Registries.BLOCK);
        NexusServices.REGISTRAR.appellate(new ResourceLocation("stone"), new ResourceLocation("rock"), Registries.BLOCK);

        // Platform-agnostic listeners for Nexus API level data n stuff
        LevelDataEventBlueprint.SAVE_LEVEL_DATA.onEvent(event -> {
            LevelStorageSource.LevelDirectory rootLevelDir = event.getLevelDirectory();
            File dataLevelDirectory = rootLevelDir.resourcePath(LEVEL_DATA_DIR).toFile();
            CompoundTag regBackupDataTag = new CompoundTag();
            Map<ResourceKey<? extends Registry<?>>, Int2ObjectMap<? extends List<ResourceLocation>>> appellations = NexusServices.REGISTRAR.getAppellations();

            if (!appellations.isEmpty()) {
                CompoundTag appellationDataTag = new CompoundTag();
                CompoundTag mappedAppellationsTag = new CompoundTag();

                appellations.forEach((regKey, appellatedIds) -> {
                    if (!appellatedIds.isEmpty()) {
                        appellatedIds.forEach((numericalId, aliases) -> {
                            CompoundTag entryAppellationsTag = new CompoundTag();
                            ResourceLocation baseRegEntryId = aliases.get(0); // The first entry is always guaranteed to be the base ID passed in, as per the impl spec for Registrar#appellate

                            entryAppellationsTag.putInt("NumericalId", numericalId);

                            ListTag aliasListTag = new ListTag();

                            aliases.forEach(aliasId -> {
                                if (!Objects.equals(baseRegEntryId, aliasId)) {
                                    CompoundTag aliasTag = new CompoundTag();

                                    aliasTag.putString("Alias", aliasId.toString());
                                    aliasListTag.add(aliasTag);
                                }
                            });

                            entryAppellationsTag.put("Aliases", aliasListTag);
                            mappedAppellationsTag.put(baseRegEntryId.toString(), entryAppellationsTag);
                        });
                    }

                    appellationDataTag.put(regKey.location().toString(), mappedAppellationsTag);
                });

                regBackupDataTag.put("Appellations", appellationDataTag);
            }

            try {
                File regDataLockFile = new File(dataLevelDirectory, REGISTRY_DATA_LOCK.getId());

                try (FileOutputStream fileOutputStream = new FileOutputStream(regDataLockFile)) {
                    NbtIo.writeCompressed(regBackupDataTag, fileOutputStream);
                }
            } catch (IOException e) {
                NexusConstants.LOGGER.warn("Failed to create RegistryDataLock.dat for level '{}'. Any missing entries from registries, regardless of whether they persist or are synced, may not be recoverable if a bug prevents your mod-loader's ({}) registry mechanism from properly caching orphaned registry entries or blocking previous IDs associated with missing entries from being used by new ones.", rootLevelDir.directoryName(), NexusServices.PLATFORM_MANAGER.getPlatform().getPlatformName(), e);
            }
        });

        LevelDataEventBlueprint.LOAD_LEVEL_DATA.onEvent(event -> {

        });

        if (NexusServices.PLATFORM_MANAGER.getPlatform().equals(ModLoader.FORGE)) {
            LevelDataEventBlueprint.SAVE_LEVEL_DATA.onEvent(event -> {
                CompoundTag levelDataTag = event.getLevelDataTag();
                CompoundTag rootFMLTag = levelDataTag.getCompound("fml");
                CompoundTag serializedRegistriesTag = rootFMLTag.getCompound("Registries");

                event.getRegistriesView().registries()
                        .map(curRegEntry -> event.getRegistriesView().registry(curRegEntry.key()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(curRegistry -> {
                            CompoundTag curRegTag = serializedRegistriesTag.getCompound(curRegistry.key().location().toString()); // .registry() here refers to the root registry
                            ListTag aliasesTag = curRegTag.getList("aliases", Tag.TAG_COMPOUND);


                        });
            });
        }

        if (NexusServices.PLATFORM_MANAGER.getPlatform().equals(ModLoader.FABRIC)) {
            LevelDataEventBlueprint.SAVE_LEVEL_DATA.onEvent(event -> {
                CompoundTag levelDataTag = event.getLevelDataTag();

                levelDataTag.putString("Test", "ZAMN");
            });
        }
    }
}
