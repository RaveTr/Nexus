package com.mememan.nexus.property_wrapper.def.block;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.DefaultableVanillaBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapperBuilder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockPropertyWrapperBuilder<B extends Block> extends BaseDefaultableDataGenPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> implements DefaultableVanillaBasedPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> {
    protected final SpecializedVanillaPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> compositeVanillaBuilder;
    protected Optional<Function<Supplier<B>, BlockStateDefinition>> blockStateDefMapperFunc = Optional.empty();

    public BlockPropertyWrapperBuilder(@NotNull BlockPropertyWrapper<B> ownerWrapper) {
        super(ownerWrapper);

        this.compositeVanillaBuilder = (SpecializedVanillaPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedVanillaWrapper().map(SpecializedVanillaPropertyWrapper::builder).get();
    }

    @Override
    public BlockPropertyWrapperBuilder<B> copyFrom(BlockPropertyWrapper<B> propertyWrapper) {
        DefaultableVanillaBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper)
                .withBlockStateDefinition(propertyWrapper.getBlockStateDefinition().orElse(null));
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

    @Override
    public Optional<SpecializedVanillaPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedVanillaBuilder() {
        return Optional.of(compositeVanillaBuilder);
    }
}
