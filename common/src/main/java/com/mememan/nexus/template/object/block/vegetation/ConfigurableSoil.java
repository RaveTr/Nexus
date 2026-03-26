package com.mememan.nexus.template.object.block.vegetation;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Helper {@code interface} that abstracts basic configuration for soil-based blocks, providing some common getters
 * for shared data among other things.
 */
public interface ConfigurableSoil {

    /**
     * Gets the pre-determined {@link Supplier} of the mapped dirt block for the implementing soil block.
     *
     * @return The {@link Supplier} of the mapped dirt block for the implementing soil block.
     */
    @NotNull
    Supplier<Block> getMappedDirtBlock();
}
