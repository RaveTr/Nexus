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
        return rawBuilder().flatMap(builder -> builder.blockStateDefMapperFunc);
    }

    public Optional<Function<Supplier<B>, WrappedBlockColor>> getBlockColorMapper() {
        return rawBuilder().flatMap(builder -> builder.blockColorMappingFunc);
    }

    public Optional<Function<Supplier<B>, IntIntMutablePair>> getFlammabilityMapper() {
        return rawBuilder().flatMap(builder -> builder.flammabilityMappingFunc);
    }

    public Optional<Function<Supplier<B>, BlockState>> getBlockStrippingMapper() {
        return rawBuilder().flatMap(builder -> builder.blockStrippingMappingFunc);
    }

    public Optional<Function<Supplier<B>, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>> getBlockTillingMapper() {
        return rawBuilder().flatMap(builder -> builder.blockTillingMappingFunc);
    }

    public Optional<Function<Supplier<B>, BlockState>> getBlockFlatteningMapper() {
        return rawBuilder().flatMap(builder -> builder.blockFlatteningMappingFunc);
    }

    public Optional<Function<Supplier<B>, Supplier<Block>>> getBlockOxidizationMapper() {
        return rawBuilder().flatMap(builder -> builder.blockOxidizationMappingFunc);
    }

    public Optional<Function<Supplier<B>, Supplier<Block>>> getBlockWaxingMapper() {
        return rawBuilder().flatMap(builder -> builder.blockWaxingMappingFunc);
    }

    @Override
    public Optional<SpecializedVanillaPropertyWrapper<B, ?, ?>> getSpecializedVanillaWrapper() {
        return Optional.of(compositeVanillaWrapper);
    }
}