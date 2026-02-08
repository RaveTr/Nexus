package com.mememan.nexus.template.object.block.vegetation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.function.Supplier;

public class DefaultableMutiLayerFlowerBlock extends DefaultableMultiLayerPlantBlock {

    public DefaultableMutiLayerFlowerBlock(Properties properties, int maxLevel, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        super(properties, maxLevel, validPlacementTags);
    }

    public DefaultableMutiLayerFlowerBlock(Properties properties, int maxLevel) {
        super(properties, maxLevel);
    }

    public DefaultableMutiLayerFlowerBlock(Properties properties, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        super(properties, validPlacementTags);
    }

    public DefaultableMutiLayerFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBonemealSuccess(Level curLevel, RandomSource rand, BlockPos targetPos, BlockState targetState) {
        return true;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader curLevel, BlockPos targetPos, BlockState targetState, boolean onClient) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel curServerLevel, RandomSource randSrc, BlockPos targetPos, BlockState targetState) {
        popResource(curServerLevel, targetPos, asItem().getDefaultInstance());
    }
}
