package com.mememan.nexus.property_wrapper.def.block;

import com.mememan.nexus.client.block.BlockStateDefinition;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.DefaultableVanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
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

    @Override
    public Optional<SpecializedVanillaPropertyWrapper<B, ?, ?>> getSpecializedVanillaWrapper() {
        return Optional.of(compositeVanillaWrapper);
    }
}