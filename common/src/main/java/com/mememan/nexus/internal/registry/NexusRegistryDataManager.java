package com.mememan.nexus.internal.registry;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.loader.ModLoader;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.template.event.blueprint.common.LevelDataEventBlueprint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Internal handler {@code class} responsible for agnostically-handling registry data per-save via queried data from
 * {@link NexusServices#REGISTRAR}.
 *
 * @see Registrar#appellate(ResourceLocation, ResourceLocation, ResourceKey)
 */
@PostInit
public final class NexusRegistryDataManager {

    static {
        handleLevelRegistryData();
    }

    private static void handleLevelRegistryData() {
        // Platform-agnostic listeners for Nexus API level data n stuff
        LevelDataEventBlueprint.SAVE_LEVEL_DATA.onEvent(event -> {
            File rootLevelDirectory = event.getLevelDirectory().path().toFile();
            File dataLevelDirectory = new File(rootLevelDirectory, "data");

            try {
                File regDataLockFile = File.createTempFile("RegistryDataLock", ".dat", dataLevelDirectory);

            } catch (IOException e) {
                NexusConstants.LOGGER.warn("Failed to create RegistryDataLock.dat for level '{}'. Any missing entries from registries, regardless of whether they persist or are synced, may not be recoverable if a bug prevents your mod-loader's ({}) registry mechanism from properly caching orphaned registry entries or blocking previous IDs associated with missing entries from being used by new ones.", event.getLevelDirectory().directoryName(), NexusServices.PLATFORM_MANAGER.getPlatform().getPlatformName());
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
