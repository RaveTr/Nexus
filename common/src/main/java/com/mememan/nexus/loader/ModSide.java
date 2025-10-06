package com.mememan.nexus.loader;

/**
 * A basic holder {@code enum} representing environment sides for mods as objects.
 */
public enum ModSide {
    CLIENT("Client"),
    COMMON("Common"),
    SERVER("Server");

    private final String sideName;

    ModSide(String sideName) {
        this.sideName = sideName;
    }

    public String getSideName() {
        return sideName;
    }
}
