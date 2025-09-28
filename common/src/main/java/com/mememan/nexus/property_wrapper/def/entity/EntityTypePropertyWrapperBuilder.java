package com.mememan.nexus.property_wrapper.def.entity;

import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EntityTypePropertyWrapperBuilder<E extends Entity> extends BaseDefaultableBareDataGenPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>> implements DefaultableLootBasedPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>> {
    protected final SpecializedLootPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>> compositeLootBuilder;
    protected Optional<AttributeSupplier.Builder> entityTypeAttributes = Optional.empty();

    public EntityTypePropertyWrapperBuilder(@NotNull EntityTypePropertyWrapper<E> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLootBuilder = (SpecializedLootPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>>) ownerWrapper.getSpecializedLootWrapper().map(SpecializedLootPropertyWrapper::builder).get();
    }

    @Override
    public EntityTypePropertyWrapperBuilder<E> copyFrom(EntityTypePropertyWrapper<E> propertyWrapper) {
        DefaultableLootBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper)
                .withAttributes(propertyWrapper.getEntityTypeAttributes().orElse(null));
    }

    public EntityTypePropertyWrapperBuilder<E> withAttributes(AttributeSupplier.Builder entityTypeAttributes) {
        this.entityTypeAttributes = Optional.ofNullable(entityTypeAttributes);
        return self();
    }



    @Override
    public Optional<SpecializedLootPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>>> getSpecializedLootBuilder() {
        return Optional.of(compositeLootBuilder);
    }
}
