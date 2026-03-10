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

    /**
     * Whether this side properly pertains to the provided physical equivalent {@code targetSide}.
     *
     * @param targetSide The physical {@link EnvironmentSide} to check against.
     *
     * @return Whether this side properly pertains to the provided physical equivalent {@code targetSide}.
     */
    public boolean pertainsTo(EnvironmentSide targetSide) {
        return this == COMMON
                || (targetSide == EnvironmentSide.CLIENT && this == CLIENT)
                || (targetSide == EnvironmentSide.DEDICATED_SERVER && this == SERVER);
    }
}
