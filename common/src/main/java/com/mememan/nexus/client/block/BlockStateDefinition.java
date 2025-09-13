package com.mememan.nexus.client.block;

import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A wrapper-builder {@code class} primarily used to hold and group blockstate data for more convenient blockstate
 * definitions.
 */
public class BlockStateDefinition {
    protected final Supplier<Block> parentBlock;
    @Nullable
    protected BlockStateGenerator blockStateSupplier;

    protected BlockStateDefinition(Supplier<Block> parentBlock) {
        this.parentBlock = parentBlock;
    }

    /**
     * Creates a new {@link BlockStateDefinition}.
     *
     * @param parentBlock The parent {@code Supplier<Block>} targeted for blockstate datagen,
     *                    stored in the newly-initialized {@link BlockStateDefinition} instance.
     *
     * @return A new {@link BlockStateDefinition}.
     */
    public static BlockStateDefinition of(Supplier<Block> parentBlock) {
        return new BlockStateDefinition(parentBlock);
    }

    /**
     * Sets this BSD's {@link BlockStateGenerator}.
     * <br></br>
     * Generally speaking, there are 2 known implementations of this
     * {@code interface} ({@link MultiPartGenerator} and {@link MultiVariantGenerator}), and they're likely the only
     * implementations you'll ever need. However, since you only need to pass their parent {@code interface} in, you're
     * not conformed to the 2 aforementioned types when working with BSDs.
     *
     * @param blockStateSupplier The {@link BlockStateGenerator} used for blockstate datagen.
     *
     * @return {@code this} (builder method).
     *
     * @see MultiPartGenerator
     * @see MultiVariantGenerator
     */
    public BlockStateDefinition withBlockStateSupplier(BlockStateGenerator blockStateSupplier) {
        this.blockStateSupplier = blockStateSupplier;
        return this;
    }

    /**
     * Gets the parent {@code Supplier<Block>} targeted and used for blockstate datagen.
     *
     * @return The parent {@code Supplier<Block>}.
     */
    public Supplier<Block> getParentBlock() {
        return parentBlock;
    }

    /**
     * Gets the {@link BlockStateGenerator} targeted and parsed/used for blockstate datagen.
     *
     * @return The {@link BlockStateGenerator}. May be {@code null}.
     *
     * @see #withBlockStateSupplier(BlockStateGenerator)
     */
    @Nullable
    public BlockStateGenerator getBlockStateSupplier() {
        return blockStateSupplier;
    }
}