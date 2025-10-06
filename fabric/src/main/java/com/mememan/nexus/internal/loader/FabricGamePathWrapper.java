package com.mememan.nexus.internal.loader;

import com.google.common.base.Suppliers;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.loader.GamePathWrapper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fabric-specific implementation of {@link GamePathWrapper}. Effectively mirrors/abstracts Forge's {@code FMLPaths}
 * implementations in Fabric, with a little refactoring.
 */
public class FabricGamePathWrapper extends GamePathWrapper {

    public FabricGamePathWrapper() {
        super(Suppliers.ofInstance(FabricLoader.getInstance().getGameDir())); // Cache it using Suppliers#ofInstance since it's constant anyway
    }

    @Override
    public Path getOrCreatePath(Path nestedPath) {
        Path gameFolderPath = getGameDir().resolve(nestedPath);

        if (!Files.isDirectory(gameFolderPath)) {
            try {
                Files.createDirectories(gameFolderPath);
            } catch (IOException e) {
                NexusConstants.LOGGER.error("Failed to create path: {}", gameFolderPath, e);
            }
        }

        return gameFolderPath;
    }

    @Override
    public void deletePath(Path targetNestedPath) {
        Path targetPath = getGameDir().resolve(targetNestedPath);

        try {
            if (Files.deleteIfExists(targetPath)) NexusConstants.LOGGER.debug("Successfully deleted path: {}", targetPath);
        } catch (IOException e) {
            NexusConstants.LOGGER.error("Failed to delete path: {}", targetPath, e);
        }
    }
}
