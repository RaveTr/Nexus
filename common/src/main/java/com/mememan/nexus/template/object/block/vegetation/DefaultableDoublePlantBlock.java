package com.mememan.nexus.template.object.block.vegetation;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Set;
import java.util.function.Supplier;

public class DefaultableDoublePlantBlock extends DoublePlantBlock implements ConfigurablePlant {
    protected final Set<Supplier<TagKey<Block>>> validPlacementTags;

    public DefaultableDoublePlantBlock(Properties properties, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        super(properties);

        this.validPlacementTags = validPlacementTags;
    }

    public DefaultableDoublePlantBlock(Properties properties) {
        this(properties, ObjectOpenHashSet.of(() -> BlockTags.DIRT));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return allowPlacementOn(state, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState targetState, LevelReader curLevel, BlockPos targetPos) {
        return targetState.getValue(HALF) != DoubleBlockHalf.UPPER
                ? mayPlaceOn(curLevel.getBlockState(targetPos.below()), curLevel, targetPos.below())
                : curLevel.getBlockState(targetPos.below()).is(this);
    }

    @Override
    public Set<Supplier<TagKey<Block>>> getValidPlacementTags() {
        return validPlacementTags;
    }
}
