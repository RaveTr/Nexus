package com.mememan.nexus.template.object.block.vegetation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Helper {@code interface} that abstracts simple configurations (such as placement) for different plant block types.
 */
public interface ConfigurablePlant {

    /**
     * Gets the pre-determined {@link Set} of tags on which the implementing tag block may be placed on/survive.
     *
     * @return The {@link Set} of tags on which the implementing tag block may be placed on/survive. May be empty.
     */
    Set<Supplier<TagKey<Block>>> getValidPlacementTags();

    /**
     * Abstracted variant of {@link BushBlock#mayPlaceOn(BlockState, BlockGetter, BlockPos)}, with a default implementation
     * that matches the provided {@code targetState} with any of the tags in {@link #getValidPlacementTags()} for placement
     * validation.
     *
     * @param targetState The {@link BlockState} on which the implementor plant block is being placed.
     * @param curLevel The current {@linkplain BlockGetter level}.
     * @param targetPos The {@linkplain BlockPos position} at which the implementor plant block is being placed.
     *
     * @return {@code true} if the implementor plant block may be placed on the provided {@code targetState},
     * {@code false} otherwise.
     */
    default boolean allowPlacementOn(BlockState targetState, BlockGetter curLevel, BlockPos targetPos) {
        return getValidPlacementTags().stream()
                .map(Supplier::get)
                .anyMatch(targetState::is);
    }
}
