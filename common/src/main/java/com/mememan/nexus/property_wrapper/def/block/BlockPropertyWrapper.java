package com.mememan.nexus.property_wrapper.def.block;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.client.block.WrappedBlockColor;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.DefaultableVanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Definite wrapper implementation for {@link Block} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}.
 *
 * @param <B> Any {@link Block} type.
 *
 * @see BlockPropertyWrapperBuilder
 */
public class BlockPropertyWrapper<B extends Block> extends BaseDefaultableDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapperBuilder<B>> implements DefaultableVanillaBasedPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapperBuilder<B>> {
    protected final SpecializedVanillaPropertyWrapper<B, ?, ?> compositeVanillaWrapper;

    public BlockPropertyWrapper(Supplier<B> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BlockPropertyWrapperBuilder::new, modId);

        this.compositeVanillaWrapper = new SpecializedVanillaPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BlockPropertyWrapper(@NotNull Supplier<B> parentObject, String modId) {
        super(parentObject, BlockPropertyWrapperBuilder::new, modId);

        this.compositeVanillaWrapper = new SpecializedVanillaPropertyWrapper<>(parentObject, modId);
    }

    public BlockPropertyWrapper() {
        super(BlockPropertyWrapperBuilder::new);

        this.compositeVanillaWrapper = new SpecializedVanillaPropertyWrapper<>();
    }

    /**
     * Gets the {@link BlockStateDefinition} mapping function used to build this BlockPropertyWrapper's parent block's
     * blockstate in datagen, with the parent block as the input.
     *
     * @return The {@link BlockStateDefinition} mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withBlockStateDefinition(Function)
     */
    public Optional<Function<Supplier<B>, BlockStateDefinition>> getBlockStateDefinition() {
        return rawBuilder().map(builder -> builder.blockStateDefMapperFunc);
    }

    /**
     * Gets the {@link WrappedBlockColor} mapping function used to assign a custom color overlay to this
     * BlockPropertyWrapper's parent block at startup on the client. This is useful for cases where blocks should
     * have a custom color applied to them, such as grass blocks.
     *
     * @return The {@link WrappedBlockColor} mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withBlockColor(Function)
     */
    public Optional<Function<Supplier<B>, WrappedBlockColor>> getBlockColorMapper() {
        return rawBuilder().map(builder -> builder.blockColorMappingFunc);
    }

    /**
     * Gets the flammability mapping function used to define the fire-related properties of this BlockPropertyWrapper's
     * parent block, where the left {@code int} represents the encouragement (ignition chance) and the right {@code int}
     * represents the spread (burn chance).
     *
     * @return The flammability mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withFlammability(Function)
     */
    public Optional<Function<Supplier<B>, IntIntMutablePair>> getFlammabilityMapper() {
        return rawBuilder().map(builder -> builder.flammabilityMappingFunc);
    }

    /**
     * Gets the block stripping mapping function used to define the behavior when this BlockPropertyWrapper's parent
     * block is right-clicked with an axe, returning the resultant {@link BlockState}.
     *
     * @return The block stripping mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withBlockStripping(Function)
     */
    public Optional<Function<BlockState, BlockState>> getBlockStrippingMapper() {
        return rawBuilder().map(builder -> builder.blockStrippingMappingFunc);
    }

    /**
     * Gets the block tilling mapping function used to define the behavior when this BlockPropertyWrapper's parent
     * block is right-clicked with a hoe. Returns a {@link Pair} containing a {@link Predicate} that determines if
     * tilling can occur and a {@link Consumer} that handles the tilling action.
     *
     * @return The block tilling mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withBlockTilling(Function)
     */
    public Optional<Function<Supplier<B>, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>> getBlockTillingMapper() {
        return rawBuilder().map(builder -> builder.blockTillingMappingFunc);
    }

    /**
     * Gets the block flattening mapping function used to define the behavior when this BlockPropertyWrapper's parent
     * block is right-clicked with a shovel, returning the resultant {@link BlockState}.
     *
     * @return The block flattening mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withBlockFlattening(Function)
     */
    public Optional<Function<BlockState, BlockState>> getBlockFlatteningMapper() {
        return rawBuilder().map(builder -> builder.blockFlatteningMappingFunc);
    }

    /**
     * Gets the block oxidization mapping function used to define the oxidized state of this BlockPropertyWrapper's
     * parent block. This is typically used for blocks that can weather or oxidize over time, such as copper blocks.
     * Returns a {@link Supplier} of the oxidized block.
     *
     * @return The block oxidization mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withBlockOxidization(Function)
     */
    public Optional<Function<Supplier<B>, Supplier<Block>>> getBlockOxidizationMapper() {
        return rawBuilder().map(builder -> builder.blockOxidizationMappingFunc);
    }

    /**
     * Gets the block waxing mapping function used to define the waxed state of this BlockPropertyWrapper's parent
     * block. This is typically used for blocks that can be waxed to prevent oxidation, such as copper blocks.
     * Returns a {@link Supplier} of the waxed block.
     *
     * @return The block waxing mapping function. May be empty.
     *
     * @see BlockPropertyWrapperBuilder#withBlockWaxing(Function)
     */
    public Optional<Function<Supplier<B>, Supplier<Block>>> getBlockWaxingMapper() {
        return rawBuilder().map(builder -> builder.blockWaxingMappingFunc);
    }

    /**
     * Gets the minimum mining level required to mine this BlockPropertyWrapper's parent block. 0 represents none
     * (i.e. this property won't have any effect on its own).
     *
     * @return The minimum mining level required to mine this BlockPropertyWrapper's parent block.
     *
     * @see BlockPropertyWrapperBuilder#minimumMiningLevel(int)
     */
    public int getMinMiningLevel() {
        return rawBuilder().map(builder -> builder.minMiningLevel).orElse(0);
    }

    @Override
    public Optional<SpecializedVanillaPropertyWrapper<B, ?, ?>> getSpecializedVanillaWrapper() {
        return Optional.of(compositeVanillaWrapper);
    }
}