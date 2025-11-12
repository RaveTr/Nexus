package com.mememan.nexus.template.object.block_entity.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DefaultableSignBlockEntity extends SignBlockEntity {
    protected final Supplier<BlockEntityType<?>> wrappedType;

    public DefaultableSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);

        this.wrappedType = () -> BlockEntityType.SIGN;
    }

    public DefaultableSignBlockEntity(Supplier<BlockEntityType<?>> wrappedType, BlockPos pos, BlockState blockState) {
        super(wrappedType.get(), pos, blockState);

        this.wrappedType = wrappedType;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return wrappedType.get();
    }
}
