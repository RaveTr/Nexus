package com.mememan.nexus.template.object.block.vegetation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Supplier;

public class DefaultableMultiLayerPlantBlock extends BushBlock implements BonemealableBlock, ConfigurablePlant {
    protected final Set<Supplier<TagKey<Block>>> validPlacementTags;
    protected IntegerProperty level;
    protected final IntOpenHashSet modularLevels = new IntOpenHashSet();
    protected final int maxLevel;

    public DefaultableMultiLayerPlantBlock(Properties properties, int maxLevel, IntOpenHashSet modularLevels, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        super(properties);
        this.maxLevel = maxLevel;
        this.validPlacementTags = validPlacementTags;

        registerDefaultState(getStateDefinition().any().setValue(getLevelProperty(), 0));

        modularLevels.intStream()
                .filter(curPlantLayerLevel -> curPlantLayerLevel > getPossibleLevels().asList().get(0) && curPlantLayerLevel < getPossibleLevels().asList().get(getPossibleLevels().size() - 1))
                .sorted()
                .forEach(this.modularLevels::add);
    }

    public DefaultableMultiLayerPlantBlock(Properties properties, int maxLevel, IntOpenHashSet modularLevels) {
        this(properties, maxLevel, modularLevels, ObjectOpenHashSet.of(() -> BlockTags.DIRT));
    }

    public DefaultableMultiLayerPlantBlock(Properties properties, int maxLevel, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        this(properties, maxLevel, IntOpenHashSet.of(), validPlacementTags);
    }

    public DefaultableMultiLayerPlantBlock(Properties properties, int maxLevel) {
        this(properties, maxLevel, ObjectOpenHashSet.of(() -> BlockTags.DIRT));
    }

    public DefaultableMultiLayerPlantBlock(Properties properties, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        this(properties, 2, validPlacementTags);
    }

    public DefaultableMultiLayerPlantBlock(Properties properties) {
        this(properties, ObjectOpenHashSet.of(() -> BlockTags.DIRT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getLevelProperty());
    }

    @Override
    public void setPlacedBy(Level curLevel, BlockPos targetPos, BlockState targetState, @Nullable LivingEntity placerEntity, ItemStack blockItemStack) {
        placeAt(curLevel, targetState, targetPos, Block.UPDATE_ALL);
    }

    @Override
    public void playerWillDestroy(Level curLevel, BlockPos curPos, BlockState targetState, net.minecraft.world.entity.player.Player responsiblePlayer) {
        if (!curLevel.isClientSide) { // Fake it 'till you make it ahh approach (we're going to depend on the bottom layer entirely for drops to account for any additional loot table predicates that may be associated with this block's loot table)
            int curPlantLayerLevel = getLevelForState(targetState);

            if (curPlantLayerLevel != 0) {
                BlockPos bottomPos = curPos.below(curPlantLayerLevel);
                BlockState bottomState = curLevel.getBlockState(bottomPos);

                if (bottomState.is(this) && getLevelForState(bottomState) == 0) {
                    if (!responsiblePlayer.isCreative()) Block.dropResources(bottomState, curLevel, bottomPos, null, responsiblePlayer, responsiblePlayer.getMainHandItem());

                    curLevel.setBlock(bottomPos, Blocks.AIR.defaultBlockState(), 35); // Remove bottom layer without triggering its loot table again (no explicit update flag cuz we wanna do it silently)
                }
            }
        }

        super.playerWillDestroy(curLevel, curPos, targetState, responsiblePlayer);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level curLevel = ctx.getLevel();
        BlockPos clickedPos = ctx.getClickedPos();
        int maxLevelForBuildHeight = (getModularLevels().isEmpty() ? getPossibleLevels().asList().get(getPossibleLevels().size() - 1) : getModularLevels().stream().findFirst().get()) + 1;

        return clickedPos.getY() < curLevel.getMaxBuildHeight() - maxLevelForBuildHeight && curLevel.getBlockState(clickedPos.above()).canBeReplaced() ? super.getStateForPlacement(ctx) : null;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState targetState, Direction facingDir, BlockState adjacentState, LevelAccessor curLevel, BlockPos curPos, BlockPos adjacentPos) { // Specifically responsible for updating the base of the multi-layer plant
        int curPlantLayerLevel = getLevelForState(targetState);

        if (curPlantLayerLevel != -1 && (facingDir.getAxis() != Direction.Axis.Y || (curPlantLayerLevel == getPossibleLevels().asList().get(getPossibleLevels().size() - 1)) == (facingDir == Direction.UP) || (adjacentState.is(this) && getLevelForState(adjacentState) != curPlantLayerLevel))) {
            return curPlantLayerLevel == 0 && facingDir == Direction.DOWN && !targetState.canSurvive(curLevel, curPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(targetState, facingDir, adjacentState, curLevel, curPos, adjacentPos);
        } else return Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean mayPlaceOn(BlockState targetState, BlockGetter curLevel, BlockPos targetPos) {
        return allowPlacementOn(targetState, curLevel, targetPos);
    }

    @Override
    public boolean canSurvive(BlockState targetState, LevelReader curLevel, BlockPos targetPos) {
        return getLevelForState(targetState) == 0 ? mayPlaceOn(curLevel.getBlockState(targetPos.below()), curLevel, targetPos.below()) : curLevel.getBlockState(targetPos.below()).is(this);
    }

    @Override
    public long getSeed(BlockState targetState, BlockPos targetPos) {
        return Mth.getSeed(targetPos.getX(), targetPos.below(getLevelForState(targetState)).getY(), targetPos.getZ());
    }

    public int getLevelForState(BlockState targetState) {
        return targetState.hasProperty(getLevelProperty()) ? targetState.getValue(getLevelProperty()) : -1;
    }

    public IntegerProperty getLevelProperty() {
        return level == null
                ? level = IntegerProperty.create("level", 0, maxLevel == 0 ? 2 : maxLevel)
                : level != null && ImmutableList.copyOf(level.getPossibleValues()).get(level.getPossibleValues().size() - 1) != maxLevel
                ? level = IntegerProperty.create("level", 0, maxLevel)
                : level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public ImmutableSet<Integer> getPossibleLevels() {
        return ImmutableSet.copyOf(getLevelProperty().getPossibleValues().stream()
                .sorted()
                .toList());
    }

    public ImmutableSet<Integer> getModularLevels() {
        return ImmutableSet.copyOf(modularLevels);
    }

    public static void placeAt(LevelAccessor curLevel, BlockState targetBaseState, BlockPos targetBasePos, int updateFlags) {
        Block targetBaseBlock = targetBaseState.getBlock();

        if (targetBaseBlock instanceof DefaultableMultiLayerPlantBlock targetMultiLayerPlantBaseBlock) {
            int maxPlantLayerLevel = targetMultiLayerPlantBaseBlock.getPossibleLevels().asList().get(targetMultiLayerPlantBaseBlock.getPossibleLevels().size() - 1);
            int curPlantLayerLevel = targetMultiLayerPlantBaseBlock.getLevelForState(targetBaseState);

            if (curPlantLayerLevel == -1) return; // Invalid state (somehow)

            curLevel.setBlock(targetBasePos, DoublePlantBlock.copyWaterloggedFrom(curLevel, targetBasePos, targetBaseState.setValue(targetMultiLayerPlantBaseBlock.getLevelProperty(), 0)), updateFlags);

            if (!targetMultiLayerPlantBaseBlock.getModularLevels().isEmpty()) {
                int minModularLevel = targetMultiLayerPlantBaseBlock.getModularLevels().asList().get(0);

                if (minModularLevel > 0) {
                    for (int plantLayerLevel = 1; plantLayerLevel <= minModularLevel; plantLayerLevel++) {
                        curLevel.setBlock(targetBasePos.above(plantLayerLevel), DoublePlantBlock.copyWaterloggedFrom(curLevel, targetBasePos.above(plantLayerLevel), targetBaseState.setValue(targetMultiLayerPlantBaseBlock.getLevelProperty(), plantLayerLevel)), updateFlags);
                    }
                }
            } else {
                for (int plantLayerLevel = 1; plantLayerLevel <= maxPlantLayerLevel; plantLayerLevel++) {
                    curLevel.setBlock(targetBasePos.above(plantLayerLevel), DoublePlantBlock.copyWaterloggedFrom(curLevel, targetBasePos.above(plantLayerLevel), targetBaseState.setValue(targetMultiLayerPlantBaseBlock.getLevelProperty(), plantLayerLevel)), updateFlags);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader curLevel, BlockPos targetPos, BlockState targetState, boolean onClient) {
        return !modularLevels.isEmpty();
    }

    @Override
    public boolean isBonemealSuccess(Level curLevel, RandomSource rand, BlockPos targetPos, BlockState targetState) {
        return !modularLevels.isEmpty() && getLevelForState(targetState) < getPossibleLevels().asList().get(getPossibleLevels().size() - 1);
    }

    @Override
    public void performBonemeal(ServerLevel curServerLevel, RandomSource randSrc, BlockPos targetPos, BlockState targetState) {
        Block targetBlock = targetState.getBlock();

        if (targetBlock instanceof DefaultableMultiLayerPlantBlock targetMultiLayerPlantBaseBlock && canSurvive(targetState, curServerLevel, targetPos.above())) {
            if (!getModularLevels().isEmpty()) {
                int curLevel = getLevelForState(targetState);
                boolean canGrow = curLevel > -1 && curLevel < maxLevel && getModularLevels().stream().max(Comparator.naturalOrder()).map(maxLevel -> curLevel < maxLevel).orElse(false);

                if (canGrow) {
                    int nextLevel = getModularLevels().stream().filter(potentialNextLevel -> potentialNextLevel > curLevel).findFirst().orElse(curLevel);

                    if (curLevel != nextLevel) { // JIC
                        for (int plantLayerLevel = 1; plantLayerLevel <= nextLevel; plantLayerLevel++) {
                            curServerLevel.setBlock(targetPos.above(plantLayerLevel), DoublePlantBlock.copyWaterloggedFrom(curServerLevel, targetPos.above(plantLayerLevel), targetState.setValue(targetMultiLayerPlantBaseBlock.getLevelProperty(), plantLayerLevel)), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Set<Supplier<TagKey<Block>>> getValidPlacementTags() {
        return validPlacementTags;
    }
}
