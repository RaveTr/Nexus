package com.mememan.nexus.template.object.block_entity.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DefaultableHangingSignBlockEntity extends HangingSignBlockEntity {
    protected final Supplier<BlockEntityType<?>> wrappedType;

    public DefaultableHangingSignBlockEntity(Supplier<BlockEntityType<?>> wrappedType, BlockPos pos, BlockState blockstate) {
        super(pos, blockstate);

        this.wrappedType = wrappedType;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return wrappedType.get();
    }
}
