package com.mememan.nexus.property_wrapper.def.block_entity;

import com.mememan.nexus.client.block_entity.BlockEntityClientData;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockEntityTypePropertyWrapper<BE extends BlockEntity> extends BaseDefaultableBareDataGenPropertyWrapper<BlockEntityType<BE>, BlockEntityTypePropertyWrapper<BE>, BlockEntityTypePropertyWrapperBuilder<BE>> {

    public BlockEntityTypePropertyWrapper(Supplier<BlockEntityType<BE>> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BlockEntityTypePropertyWrapperBuilder::new, modId);
    }

    public BlockEntityTypePropertyWrapper(@NotNull Supplier<BlockEntityType<BE>> parentObject, String modId) {
        super(parentObject, BlockEntityTypePropertyWrapperBuilder::new, modId);
    }

    public BlockEntityTypePropertyWrapper() {
        super(BlockEntityTypePropertyWrapperBuilder::new);
    }

    /**
     * Gets the side-safe {@link Supplier} for the parent block entity type's client-side data.
     *
     * @return The parent block entity type's {@link BlockEntityClientData}. May be empty.
     *
     * @see BlockEntityTypePropertyWrapperBuilder#withClientData(Supplier)
     */
    public Optional<Supplier<BlockEntityClientData<BE>>> getBlockEntityClientData() {
        return rawBuilder().flatMap(builder -> builder.clientData);
    }
}
