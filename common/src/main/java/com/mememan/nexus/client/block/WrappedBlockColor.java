package com.mememan.nexus.client.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Side-safe variant of {@link net.minecraft.client.color.block.BlockColor} to avoid accidental classloading via imports
 * or other means. Mainly used for determining and registering different dynamic block colors.
 */
@FunctionalInterface
public interface WrappedBlockColor {

    /**
     * Computes the packed RGB color of a given {@link BlockState} at a given {@link BlockPos}.
     *
     * @param targetState The {@link BlockState} to compute the color of.
     * @param curLevel The current {@linkplain BlockAndTintGetter level}, if applicable. May be {@code null}.
     * @param targetPos The {@link BlockPos} of the {@link BlockState} to compute the color of. May be {@code null}.
     * @param tintIndex The computed tintIndex that represents the current tint of the block.
     *
     * @return The packed RGB color of the given {@link BlockState} at the given {@link BlockPos}.
     *
     * @see net.minecraft.client.color.block.BlockColor#getColor(BlockState, BlockAndTintGetter, BlockPos, int)
     * @see net.minecraft.client.color.block.BlockColors
     */
    int getColor(BlockState targetState, @Nullable BlockAndTintGetter curLevel, @Nullable BlockPos targetPos, int tintIndex);
}
