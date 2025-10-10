package com.mememan.nexus.template.object.block.vegetation;

import com.google.common.base.Suppliers;
import com.mememan.nexus.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public class DefaultableSpreadingSnowyDirtBlock extends SpreadingSnowyDirtBlock {
    protected final boolean allowSnowyVariant;
    protected final Set<Supplier<TagKey<Block>>> validSnowyTags;
    protected final Supplier<Block> baseDirtBlock;

    public DefaultableSpreadingSnowyDirtBlock(Properties properties, boolean allowSnowyVariant, Set<Supplier<TagKey<Block>>> validSnowyTags, Supplier<Block> baseDirtBlock) {
        super(properties);

        this.allowSnowyVariant = allowSnowyVariant;
        this.validSnowyTags = validSnowyTags;
        this.baseDirtBlock = baseDirtBlock;
    }

    public DefaultableSpreadingSnowyDirtBlock(Properties properties, boolean allowSnowyVariant, Set<Supplier<TagKey<Block>>> validSnowyTags) {
        super(properties);

        this.allowSnowyVariant = allowSnowyVariant;
        this.validSnowyTags = validSnowyTags;

        Supplier<Block> ownerRefSup = () -> this; // Peak Java generic type inference
        this.baseDirtBlock = RegistryUtil.getSuppliedObjectFrom(ownerRefSup, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_grass_block", "_dirt")))
                .orElse(RegistryUtil.getSuppliedObjectFrom(ownerRefSup, parentBlockId -> RegistryUtil.pickSuffix(parentBlockId, "_dirt"))
                        .orElse(Suppliers.ofInstance(null)));
    }

    public DefaultableSpreadingSnowyDirtBlock(Properties properties, Set<Supplier<TagKey<Block>>> validSnowyTags, Supplier<Block> baseDirtBlock) {
        this(properties, true, validSnowyTags, baseDirtBlock);
    }

    public DefaultableSpreadingSnowyDirtBlock(Properties properties, boolean allowSnowyVariant, Supplier<Block> baseDirtBlock) {
        this(properties, allowSnowyVariant, ObjectOpenHashSet.of(() -> BlockTags.SNOW), baseDirtBlock);
    }

    public DefaultableSpreadingSnowyDirtBlock(Properties properties, boolean allowSnowyVariant) {
        this(properties, allowSnowyVariant, ObjectOpenHashSet.of(() -> BlockTags.SNOW));
    }

    public DefaultableSpreadingSnowyDirtBlock(Properties properties, Set<Supplier<TagKey<Block>>> validSnowyTags) {
        this(properties, true, validSnowyTags);
    }

    public DefaultableSpreadingSnowyDirtBlock(Properties properties, Supplier<Block> baseDirtBlock) {
        this(properties, false, baseDirtBlock);
    }

    public DefaultableSpreadingSnowyDirtBlock(Properties properties) {
        this(properties, false);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState targetState, Direction facingDir, BlockState facingState, LevelAccessor curLevel, BlockPos targetPos, BlockPos facingPos) {
        return facingDir == Direction.UP && allowSnowyVariant
                ? targetState.setValue(SNOWY, allowSnowyVariant(targetState))
                : super.updateShape(targetState, facingDir, facingState, curLevel, targetPos, facingPos);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(SNOWY, allowSnowyVariant(ctx.getLevel().getBlockState(ctx.getClickedPos().above())));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (baseDirtBlock != null && baseDirtBlock.get() != null) {
            if (!allowGrassBlock(level, state, pos, random)) level.setBlockAndUpdate(pos, baseDirtBlock.get().defaultBlockState());
            else if (allowSpread(level, state, pos, random)) performSpread(level, state, pos, random);
        }
    }

    public boolean allowSnowyVariant() {
        return allowSnowyVariant;
    }

    public boolean allowSnowyVariant(BlockState state) {
        return allowSnowyVariant() && validSnowyTags.stream().map(Supplier::get).anyMatch(state::is);
    }

    public Set<Supplier<TagKey<Block>>> getValidSnowyTags() {
        return validSnowyTags;
    }

    public Supplier<Block> getBaseDirtBlock() {
        return baseDirtBlock;
    }

    public boolean allowGrassBlock(LevelAccessor curLevel, BlockState targetState, BlockPos targetPos, RandomSource randSrc) {
        BlockPos aboveTargetPos = targetPos.above();
        BlockState aboveTargetState = curLevel.getBlockState(aboveTargetPos);

        boolean snowSpreadGrass = allowSnowyVariant(targetState) && aboveTargetState.is(BlockTags.SNOW) && aboveTargetState.hasProperty(SnowLayerBlock.LAYERS) && aboveTargetState.getValue(SnowLayerBlock.LAYERS) == 1;
        boolean lightSpreadGrass = LightEngine.getLightBlockInto(curLevel, targetState, targetPos, aboveTargetState, aboveTargetPos, Direction.UP, aboveTargetState.getLightBlock(curLevel, aboveTargetPos)) < curLevel.getMaxLightLevel();

        return snowSpreadGrass || (lightSpreadGrass && aboveTargetState.getFluidState().getAmount() != 8);
    }

    public boolean allowSpread(LevelAccessor curServerLevel, BlockState targetState, BlockPos targetPos, RandomSource randSrc) {
        return curServerLevel.getMaxLocalRawBrightness(targetPos.above()) > 9 && baseDirtBlock != null && baseDirtBlock.get() != null;
    }

    public void performSpread(LevelAccessor serverLevel, BlockState targetState, BlockPos targetPos, RandomSource randSrc) {
        if (baseDirtBlock != null && baseDirtBlock.get() != null) { // JIC (Should never be reached, tho)
            for (int attempt = 0; attempt < 4; attempt++) {
                BlockPos offsetPos = targetPos.offset(randSrc.nextInt(3) - 1, randSrc.nextInt(5) - 3, randSrc.nextInt(3) - 1);

                if (serverLevel instanceof ServerLevel curServerLevel && curServerLevel.getBlockState(offsetPos).is(baseDirtBlock.get())) {
                    curServerLevel.setBlockAndUpdate(offsetPos, targetState.setValue(SnowyDirtBlock.SNOWY, allowSnowyVariant(targetState)));
                }
            }
        }
    }
}
