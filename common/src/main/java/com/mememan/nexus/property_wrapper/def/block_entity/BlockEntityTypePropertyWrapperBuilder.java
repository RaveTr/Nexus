package com.mememan.nexus.property_wrapper.def.block_entity;

import com.mememan.nexus.client.block_entity.BlockEntityClientData;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapperBuilder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockEntityTypePropertyWrapperBuilder<BE extends BlockEntity> extends BaseDefaultableBareDataGenPropertyWrapperBuilder<BlockEntityType<BE>, BlockEntityTypePropertyWrapperBuilder<BE>, BlockEntityTypePropertyWrapper<BE>> {
    protected Optional<Supplier<BlockEntityClientData<BE>>> clientData = Optional.empty();

    public BlockEntityTypePropertyWrapperBuilder(@NotNull BlockEntityTypePropertyWrapper<BE> ownerWrapper) {
        super(ownerWrapper);

        bypassDefaultTranslation(true); // Prevents the generation of locale entries for the block entity type cuz blocks handle that
    }

    @Override
    public BlockEntityTypePropertyWrapperBuilder<BE> copyFrom(BlockEntityTypePropertyWrapper<BE> propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .withClientData(propertyWrapper.getBlockEntityClientData().orElse(null));
    }

    /**
     * Defines the client-side data for the parent block entity type. Should be hidden behind a lambda expression (see
     * references below).
     *
     * @param clientData The {@link BlockEntityClientData} {@link Supplier} to associate with the parent block entity type.
     *
     * @return {@link #self()} (builder method).
     *
     * @see BlockEntityClientData
     */
    public BlockEntityTypePropertyWrapperBuilder<BE> withClientData(Supplier<BlockEntityClientData<BE>> clientData) {
        this.clientData = Optional.ofNullable(clientData);
        return self();
    }
}
