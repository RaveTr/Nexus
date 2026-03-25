package com.mememan.nexus.property_wrapper.def.block_entity;

import com.mememan.nexus.client.block_entity.BlockEntityClientData;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapperBuilder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Definite builder implementation for {@link BlockEntityType} objects, with constrained generic types for {@code SELF} and
 * {@code DBDGPW}. Includes relevant properties for rendering and other metadata associated with block entities.
 *
 * @param <BE> Any {@link BlockEntity} type.
 *
 * @see BlockEntityTypePropertyWrapper
 */
public class BlockEntityTypePropertyWrapperBuilder<BE extends BlockEntity> extends BaseDefaultableBareDataGenPropertyWrapperBuilder<BlockEntityType<BE>, BlockEntityTypePropertyWrapperBuilder<BE>, BlockEntityTypePropertyWrapper<BE>> {
    protected Supplier<BlockEntityClientData<BE>> clientData;

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
        this.clientData = clientData;
        return self();
    }
}
