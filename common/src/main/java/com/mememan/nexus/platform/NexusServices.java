package com.mememan.nexus.platform;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.platform.services.DataGenerator;
import com.mememan.nexus.platform.services.NetworkManager;
import com.mememan.nexus.platform.services.PlatformManager;
import com.mememan.nexus.platform.services.Registrar;

import java.util.ServiceLoader;

/**
 * Centralized service loader for Nexus API. This is the primary access point for all platform-agnostic services.
 */
public class NexusServices {
    /**
     * The central service for managing platform-specific tasks handled on a per-loader basis.
     */
    public static final PlatformManager PLATFORM_MANAGER = loadService(PlatformManager.class);
    /**
     * The service responsible for handling all types of object registration (save for packet registration, which is
     * handled by {@link #NETWORK_MANAGER}).
     */
    public static final Registrar REGISTRAR = loadService(Registrar.class);
    /**
     * The service responsible for handling packet registration and interaction across sides (Client/Server).
     */
    public static final NetworkManager NETWORK_MANAGER = loadService(NetworkManager.class);
    /**
     * The service responsible for registering and handling all data generation providers/tasks for dependant mods.
     */
    public static final DataGenerator DATA_GENERATOR = loadService(DataGenerator.class);

    /**
     * Internal service loader method for loading platform-agnostic services.
     *
     * @param clazz The platform {@code class} (usually an {@code interface}) to load.
     *
     * @return The loaded service instance.
     *
     * @param <T> The object type of the service to load.
     */
    private static <T> T loadService(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        NexusConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}