package com.mememan.nexus.template.object.block.entity.sign;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class DefaultableWallSignBlock extends WallSignBlock {
    protected final Supplier<BlockEntityType<SignBlockEntity>> wrappedType;

    public DefaultableWallSignBlock(Properties properties, WoodType type) {
        super(properties, type);

        this.wrappedType = () -> (BlockEntityType<SignBlockEntity>) BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(this).withPath("sign")).orElse(BlockEntityType.SIGN);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return wrappedType.get().create(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, wrappedType.get(), SignBlockEntity::tick);
    }
}
