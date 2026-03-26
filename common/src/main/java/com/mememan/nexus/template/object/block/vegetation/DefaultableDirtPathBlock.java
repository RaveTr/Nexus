package com.mememan.nexus.template.object.block.vegetation;

import com.google.common.base.Suppliers;
import com.mememan.nexus.util.RegistryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DefaultableDirtPathBlock extends DirtPathBlock implements ConfigurableSoil {
    protected final Supplier<Block> mappedDirtBlock;

    public DefaultableDirtPathBlock(Properties properties, Supplier<Block> mappedDirtBlock) {
        super(properties);

        this.mappedDirtBlock = mappedDirtBlock == null ? Suppliers.ofInstance(null) : mappedDirtBlock;
    }

    public DefaultableDirtPathBlock(Properties properties) {
        super(properties);

        Supplier<Block> ownerRefSup = () -> this; // Peak Java generic type inference
        this.mappedDirtBlock = () -> RegistryUtil.getObjectFrom(ownerRefSup, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_path", "_dirt")))
                .orElse(RegistryUtil.getObjectFrom(ownerRefSup, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_path", "")))
                        .orElse(null));
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return !defaultBlockState().canSurvive(ctx.getLevel(), ctx.getClickedPos()) && mappedDirtBlock.get() != null
                ? Block.pushEntitiesUp(defaultBlockState(), mappedDirtBlock.get().defaultBlockState(), ctx.getLevel(), ctx.getClickedPos())
                : super.getStateForPlacement(ctx);
    }

    @Override
    public void tick(BlockState targetState, ServerLevel curServerLevel, BlockPos targetPos, RandomSource randSrc) {
        DefaultableFarmBlock.convertToMappedDirt(null, targetState, curServerLevel, targetPos);
    }

    @Override
    public @NotNull Supplier<Block> getMappedDirtBlock() {
        return mappedDirtBlock;
    }
}
