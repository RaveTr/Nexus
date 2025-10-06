package com.mememan.nexus.datagen;

import com.mememan.nexus.loader.ModSide;
import org.jetbrains.annotations.NotNull;

/**
 * Base {@code interface} representing different data provider types used by Nexus API for classification/identification.
 */
public interface ProviderType {

    /**
     * The {@link ModSide} this type runs on. {@link ModSide#COMMON} is run on both sides.
     *
     * @return The {@link ModSide} this provider type runs on.
     */
    @NotNull
    ModSide getSide();
}
