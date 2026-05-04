package com.mememan.nexus.datum.base;

import net.minecraft.nbt.CompoundTag;

/**
 * Core {@code interface} for all datum types.
 * <br></br>
 * Provides only the bare necessities for datum components to function "properly"; that is, the ability to read/write
 * to a {@link CompoundTag} tied to a particular object in memory. Extensions may provide additional functionality, such
 * as persisting data to disk or syncing data to/from clients.
 *
 * @see <a href="https://github.com/RaveTr/Nexus/wiki/datum">Nexus Wiki: Datum</a>
 */
public interface Datum extends Cloneable {

    /**
     * Writes data tied to this datum to the provided {@code tag}.
     *
     * @param tag The {@link CompoundTag} to serialize this datum's data to.
     */
    void writeTo(CompoundTag tag);

    /**
     * Reads data tied to this datum from the provided {@code tag}.
     *
     * @param tag The {@link CompoundTag} to deserialize this datum's data from.
     */
    void readFrom(CompoundTag tag);

    Datum clone();

    boolean equals(Datum otherDatum);
}
