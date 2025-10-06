package com.mememan.nexus.internal.loader;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.loader.GamePathWrapper;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Forge-specific implementation of {@link GamePathWrapper}. Primarily delegates directory-related operations to
 * {@link FMLPaths} with some abstraction layered on top.
 */
public class ForgeGamePathWrapper extends GamePathWrapper {

    public ForgeGamePathWrapper() {
        super(FMLPaths.GAMEDIR::get);
    }

    @Override
    public Path getOrCreatePath(Path nestedPath) {
        return FMLPaths.getOrCreateGameRelativePath(nestedPath);
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
