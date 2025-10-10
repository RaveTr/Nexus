package com.mememan.nexus.template.object.block.vegetation;

import com.google.common.base.Suppliers;
import com.mememan.nexus.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.function.Supplier;

public class DefaultableTallGrassBlock extends TallGrassBlock implements ConfigurablePlant {
    protected final Set<Supplier<TagKey<Block>>> validPlacementTags;
    protected final Supplier<DoublePlantBlock> tallPlantBlock;

    public DefaultableTallGrassBlock(Properties properties, Set<Supplier<TagKey<Block>>> validPlacementTags, Supplier<DoublePlantBlock> tallPlantBlock) {
        super(properties);

        this.validPlacementTags = validPlacementTags;
        this.tallPlantBlock = tallPlantBlock == null ? Suppliers.ofInstance(null) : tallPlantBlock;
    }

    public DefaultableTallGrassBlock(Properties properties, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        super(properties);

        this.validPlacementTags = validPlacementTags;

        Block ownerRef = this; // Peak Java generic type inference
        this.tallPlantBlock = () -> RegistryUtil.getObjectFrom(() -> ownerRef, null)
                .filter(ownerBlock -> ownerBlock instanceof DoublePlantBlock)
                .map(ownerBlock -> (DoublePlantBlock) ownerBlock)
                .orElse(null);
    }

    public DefaultableTallGrassBlock(Properties properties, Supplier<DoublePlantBlock> tallPlantBlock) {
        this(properties, ObjectOpenHashSet.of(() -> BlockTags.DIRT), tallPlantBlock);
    }

    public DefaultableTallGrassBlock(Properties properties) {
        this(properties, ObjectOpenHashSet.of(() -> BlockTags.DIRT));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return allowPlacementOn(state, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState targetState, LevelReader curLevel, BlockPos targetPos) { // Needed because Forge patch go brr
        return mayPlaceOn(curLevel.getBlockState(targetPos.below()), curLevel, targetPos.below());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return tallPlantBlock != null && tallPlantBlock.get() != null;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return tallPlantBlock != null && tallPlantBlock.get() != null;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (tallPlantBlock != null && tallPlantBlock.get() != null) { // JIC (Should never be reached tho)
            DoublePlantBlock existingTallPlantBlock = tallPlantBlock.get();

            if (existingTallPlantBlock.defaultBlockState().canSurvive(level, pos) && level.isEmptyBlock(pos.above())) {
                DoublePlantBlock.placeAt(level, existingTallPlantBlock.defaultBlockState(), pos, Block.UPDATE_CLIENTS);
            }
        } else super.performBonemeal(level, random, pos, state);
    }

    @Override
    public Set<Supplier<TagKey<Block>>> getValidPlacementTags() {
        return validPlacementTags;
    }
}
