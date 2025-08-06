package com.mememan.nexus.loader;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.PlatformManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Wrapper {@code class} that effectively streamlines calling loader-specific implementations of common {@link Path}-related
 * methods/operations within the game's root directory.
 *
 * @see NexusServices#PLATFORM_MANAGER
 * @see PlatformManager
 */
public abstract class GamePathWrapper {
    private final Supplier<Path> gameDir;

    public GamePathWrapper(Supplier<Path> gameDir) {
        this.gameDir = gameDir;
    }

    /**
     * Directly gets the game's root directory, as specified by the loader-specific implementation within this
     * instance's constructor.
     *
     * @return The game's root directory (Where "mods", "config", etc. live).
     */
    @NotNull
    public Path getGameDir() {
        return gameDir.get();
    }

    /**
     * Shortcut getter method for the "mods" directory within the game's root directory.
     *
     * @return The "mods" directory within the game's root directory. Should not be {@code null}.
     */
    public Path getModsDir() {
        return getGameDir().resolve("mods");
    }

    /**
     * Shortcut getter method for the "config" directory within the game's root directory.
     *
     * @return The "config" directory within the game's root directory. Should not be {@code null}.
     */
    public Path getConfigDir() {
        return getGameDir().resolve("config");
    }

    /**
     * Gets a {@link Path} from the game's root directory, as specified by the given loader-specific implementation.
     * If no such directory (as per the one passed into this method) exists, this method (as per impl-standard) will
     * create a {@link Path} matching the given {@code nestedPath} within the game's root directory.
     *
     * @param nestedPath Nested directory within the game's root directory to check for.
     *
     * @return The specified {@code nestedPath} from the game's root directory, created if it did not exist.
     */
    public abstract Path getOrCreatePath(Path nestedPath);

    /**
     * Deletes a path from the game's root directory (if it exists), as specified by the given loader-specific
     * implementation. Effectively does nothing otherwise.
     * <br></br>
     * Note that this method may still fail if the path points to an existing file/directory that cannot be deleted
     * (insufficient system perms, currently running in any program, etc.).
     *
     * @param targetNestedPath The {@link Path} to delete from the game's root directory. May point to a file or a
     *                         directory.
     */
    public abstract void deletePath(Path targetNestedPath);
}