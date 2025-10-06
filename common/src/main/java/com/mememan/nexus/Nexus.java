package com.mememan.nexus;

import com.mememan.nexus.loader.ModLoader;
import com.mememan.nexus.platform.NexusServices;

/**
 * Core class for initializing Nexus API.
 */
public class Nexus {

    /**
     * Central initialization method for Nexus API.
     */
    public static void initialize() {
        NexusServices.REGISTRAR.setupRegistrar();

        if (NexusServices.PLATFORM_MANAGER.getPlatform().equals(ModLoader.FABRIC)) NexusServices.NETWORK_MANAGER.setupNetworkManager();
        if (NexusServices.PLATFORM_MANAGER.isRunningDataGen()) NexusServices.DATA_GENERATOR.setupDataGenerator();
    }

    /**
     * Initialization method for startup tasks that need to be deferred to a later mod-loading stage, primarily on
     * Neo/Forge.
     */
    public static void initializeDeferred() {
        NexusServices.NETWORK_MANAGER.setupNetworkManager();
    }
}