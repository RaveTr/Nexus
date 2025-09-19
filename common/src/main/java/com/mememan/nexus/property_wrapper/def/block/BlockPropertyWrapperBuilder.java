package com.mememan.nexus.property_wrapper.def.block;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.client.block.WrappedBlockColor;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.DefaultableVanillaBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapperBuilder;
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
     * @param bsdMappingFunc The {@link BlockStateDefinition} mapping function used to build this BlockPropertyWrapperBuilder's
     *                       parent block's blockstate in datagen, with the parent block as the input.
     *
     * @return {@link #self()} (builder method).
     */
    public BlockPropertyWrapperBuilder<B> withBlockStateDefinition(Function<Supplier<B>, BlockStateDefinition> bsdMappingFunc) {
        this.blockStateDefMapperFunc = Optional.ofNullable(bsdMappingFunc);
        return self();
    }

    public BlockPropertyWrapperBuilder<B> withBlockColor(Function<Supplier<B>, WrappedBlockColor> blockColorMappingFunc) {
        this.blockColorMappingFunc = Optional.ofNullable(blockColorMappingFunc);
        return self();
    }

    public BlockPropertyWrapperBuilder<B> withFlammability(Function<Supplier<B>, IntIntMutablePair> flammabilityMappingFunc) {
        this.flammabilityMappingFunc = Optional.ofNullable(flammabilityMappingFunc);
        return self();
    }

    public BlockPropertyWrapperBuilder<B> withBlockStripping(Function<Supplier<B>, BlockState> blockStrippingMappingFunc) {
        this.blockStrippingMappingFunc = Optional.ofNullable(blockStrippingMappingFunc);
        return self();
    }

    public BlockPropertyWrapperBuilder<B> withBlockTilling(Function<Supplier<B>, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> blockTillingMappingFunc) {
        this.blockTillingMappingFunc = Optional.ofNullable(blockTillingMappingFunc);
        return self();
    }

    public BlockPropertyWrapperBuilder<B> withBlockFlattening(Function<Supplier<B>, BlockState> blockFlatteningMappingFunc) {
        this.blockFlatteningMappingFunc = Optional.ofNullable(blockFlatteningMappingFunc);
        return self();
    }

    public BlockPropertyWrapperBuilder<B> withBlockOxidization(Function<Supplier<B>, Supplier<Block>> blockOxidizationMappingFunc) {
        this.blockOxidizationMappingFunc = Optional.ofNullable(blockOxidizationMappingFunc);
        return self();
    }

    public BlockPropertyWrapperBuilder<B> withBlockWaxing(Function<Supplier<B>, Supplier<Block>> blockWaxingMappingFunc) {
        this.blockWaxingMappingFunc = Optional.ofNullable(blockWaxingMappingFunc);
        return self();
    }

    @Override
    public Optional<SpecializedVanillaPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedVanillaBuilder() {
        return Optional.of(compositeVanillaBuilder);
    }
}
