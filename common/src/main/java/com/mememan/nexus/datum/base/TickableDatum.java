package com.mememan.nexus.datum.base;

import com.mememan.nexus.loader.ModSide;

/**
 * Base extension of {@link Datum} that provides tick-related functionality.
 */
public interface TickableDatum extends Datum {

    /**
     *
     */
    void tick(ModSide logicalSide, long totalTickCount);
}
