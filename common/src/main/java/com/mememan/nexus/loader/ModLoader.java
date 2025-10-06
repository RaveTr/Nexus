package com.mememan.nexus.loader;

/**
 * A basic holder {@code enum} representing mod-loaders as objects.
 */
public enum ModLoader {
    FORGE("Forge"),
    NEOFORGE("NeoForge"), // Currently unused in 1.20.1 (duh)
    FABRIC("Fabric");

    private final String platformName;

    ModLoader(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }
}
