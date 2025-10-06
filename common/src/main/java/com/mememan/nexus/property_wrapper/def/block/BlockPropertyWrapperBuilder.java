package com.mememan.nexus.property_wrapper.def.block;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.client.block.WrappedBlockColor;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.DefaultableVanillaBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockPropertyWrapperBuilder<B extends Block> extends BaseDefaultableDataGenPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> implements DefaultableVanillaBasedPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> {
    protected final SpecializedVanillaPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> compositeVanillaBuilder;
    protected Optional<Function<Supplier<B>, BlockStateDefinition>> blockStateDefMapperFunc = Optional.empty();
    protected Optional<Function<Supplier<B>, WrappedBlockColor>> blockColorMappingFunc = Optional.empty();
    protected Optional<Function<Supplier<B>, IntIntMutablePair>> flammabilityMappingFunc = Optional.empty();
    protected Optional<Function<Supplier<B>, BlockState>> blockStrippingMappingFunc = Optional.empty();
    protected Optional<Function<Supplier<B>, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>> blockTillingMappingFunc = Optional.empty();
    protected Optional<Function<Supplier<B>, BlockState>> blockFlatteningMappingFunc = Optional.empty();
    protected Optional<Function<Supplier<B>, Supplier<Block>>> blockOxidizationMappingFunc = Optional.empty();
    protected Optional<Function<Supplier<B>, Supplier<Block>>> blockWaxingMappingFunc = Optional.empty();
    protected int minMiningLevel = 0;

    public BlockPropertyWrapperBuilder(@NotNull BlockPropertyWrapper<B> ownerWrapper) {
        super(ownerWrapper);

        this.compositeVanillaBuilder = (SpecializedVanillaPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedVanillaWrapper().map(SpecializedVanillaPropertyWrapper::builder).get();
    }

    @Override
    public BlockPropertyWrapperBuilder<B> copyFrom(BlockPropertyWrapper<B> propertyWrapper) {
        DefaultableVanillaBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper)
                .withBlockStateDefinition(propertyWrapper.getBlockStateDefinition().orElse(null))
                .withBlockColor(propertyWrapper.getBlockColorMapper().orElse(null))
                .withFlammability(propertyWrapper.getFlammabilityMapper().orElse(null))
                .withBlockStripping(propertyWrapper.getBlockStrippingMapper().orElse(null))
                .withBlockTilling(propertyWrapper.getBlockTillingMapper().orElse(null))
                .withBlockFlattening(propertyWrapper.getBlockFlatteningMapper().orElse(null))
                .withBlockOxidization(propertyWrapper.getBlockOxidizationMapper().orElse(null))
                .withBlockWaxing(propertyWrapper.getBlockWaxingMapper().orElse(null));
    }

    /**
     * Defines the {@link BlockStateDefinition} to be used for the parent {@link Block} in datagen.
     *
     * @param bsdMappingFunc The {@link BlockStateDefinition} mapping function used to build the parent block's
     *                       blockstate in datagen, with the parent block as the input. May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockStateDefinition(Function<Supplier<B>, BlockStateDefinition> bsdMappingFunc) {
        this.blockStateDefMapperFunc = Optional.ofNullable(bsdMappingFunc);
        return self();
    }

    /**
     * Defines a {@link Function} that outputs a {@link WrappedBlockColor} to be assigned to the parent {@link Block}
     * at startup on the client. Useful for cases where blocks should have a custom color overlay applied to them, such
     * as grass blocks.
     *
     * @param blockColorMappingFunc The mapping function used to output the parent block's {@link WrappedBlockColor},
     *                              with the parent block as the input. May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockColor(Function<Supplier<B>, WrappedBlockColor> blockColorMappingFunc) {
        this.blockColorMappingFunc = Optional.ofNullable(blockColorMappingFunc);
        return self();
    }

    /**
     * Defines a {@link Function} that outputs a {@link IntIntMutablePair} representing the flammability properties
     * of the parent {@link Block}, where the left {@code int} is the encouragement (i.e. ignition chance) and the right
     * {@code int} is the spread (i.e. burn chance).
     *
     * @param flammabilityMappingFunc The mapping function used to output the property {@link IntIntMutablePair} for the
     *                                parent block, with the parent block as the input. May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withFlammability(Function<Supplier<B>, IntIntMutablePair> flammabilityMappingFunc) {
        this.flammabilityMappingFunc = Optional.ofNullable(flammabilityMappingFunc);
        return self();
    }

    /**
     * Defines a {@link Function} that outputs a {@link BlockState} representing the stripped state of the parent, which
     * occurs when the parent block is right-clicked with an axe.
     *
     * @param blockStrippingMappingFunc The mapping function used to output the resultant {@link BlockState} for the
     *                                  parent block when right-clicked with an axe, with the parent block as the input.
     *                                  May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockStripping(Function<Supplier<B>, BlockState> blockStrippingMappingFunc) {
        this.blockStrippingMappingFunc = Optional.ofNullable(blockStrippingMappingFunc);
        return self();
    }

    /**
     * Defines a {@link Function} that outputs a {@link Pair} containing a {@link Predicate} and {@link Consumer}
     * representing the tilling behavior of the parent {@link Block}, which occurs when the parent block is right-clicked
     * with a hoe. The {@link Predicate} determines if tilling can occur, while the {@link Consumer} handles the
     * tilling action.
     *
     * @param blockTillingMappingFunc The mapping function used to output the tilling behavior {@link Pair} for the
     *                                parent block, with the parent block as the input. May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockTilling(Function<Supplier<B>, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> blockTillingMappingFunc) {
        this.blockTillingMappingFunc = Optional.ofNullable(blockTillingMappingFunc);
        return self();
    }

    /**
     * Defines a {@link Function} that outputs a {@link BlockState} representing the flattened state of the parent
     * {@link Block}, which occurs when the parent block is right-clicked with a shovel.
     *
     * @param blockFlatteningMappingFunc The mapping function used to output the resultant {@link BlockState} for the
     *                                   parent block when right-clicked with a shovel, with the parent block as the input.
     *                                   May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockFlattening(Function<Supplier<B>, BlockState> blockFlatteningMappingFunc) {
        this.blockFlatteningMappingFunc = Optional.ofNullable(blockFlatteningMappingFunc);
        return self();
    }

    /**
     * Defines a {@link Function} that outputs a {@link Supplier} of a {@link Block} representing the oxidized state
     * of the parent {@link Block}. This is typically used for blocks that can weather or oxidize over time, such as
     * copper blocks.
     *
     * @param blockOxidizationMappingFunc The mapping function used to output the oxidized block {@link Supplier} for the
     *                                    parent block, with the parent block as the input. May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockOxidization(Function<Supplier<B>, Supplier<Block>> blockOxidizationMappingFunc) {
        this.blockOxidizationMappingFunc = Optional.ofNullable(blockOxidizationMappingFunc);
        return self();
    }

    /**
     * Defines a {@link Function} that outputs a {@link Supplier} of a {@link Block} representing the waxed state
     * of the parent {@link Block}. This is typically used for blocks that can be waxed to prevent oxidation, such as
     * copper blocks.
     *
     * @param blockWaxingMappingFunc The mapping function used to output the waxed block {@link Supplier} for the
     *                               parent block, with the parent block as the input. May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockWaxing(Function<Supplier<B>, Supplier<Block>> blockWaxingMappingFunc) {
        this.blockWaxingMappingFunc = Optional.ofNullable(blockWaxingMappingFunc);
        return self();
    }

    /**
     * Defines the minimum mining level required to mine the parent {@link Block}. This is compared against a
     * {@linkplain TieredItem TieredItem's} {@linkplain TieredItem#getTier() tier} {@linkplain Tier#getLevel() level}.
     *
     * @param miningLevel The minimum mining level required to mine the parent {@link Block}.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> minimumMiningLevel(int miningLevel) {
        this.minMiningLevel = Math.abs(miningLevel);
        return self();
    }

    @Override
    public Optional<SpecializedVanillaPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedVanillaBuilder() {
        return Optional.of(compositeVanillaBuilder);
    }
}
