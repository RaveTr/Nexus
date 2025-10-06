package com.mememan.nexus.property_wrapper.def.entity;

import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class EntityTypePropertyWrapperBuilder<E extends Entity> extends BaseDefaultableBareDataGenPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>> implements DefaultableLootBasedPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>> {
    protected final SpecializedLootPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>> compositeLootBuilder;
    protected Optional<Supplier<AttributeSupplier.Builder>> entityTypeAttributes = Optional.empty();
    protected Optional<Supplier<EntityClientData<E>>> clientData = Optional.empty();

    public EntityTypePropertyWrapperBuilder(@NotNull EntityTypePropertyWrapper<E> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLootBuilder = (SpecializedLootPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>>) ownerWrapper.getSpecializedLootWrapper().map(SpecializedLootPropertyWrapper::builder).get();
    }

    @Override
    public EntityTypePropertyWrapperBuilder<E> copyFrom(EntityTypePropertyWrapper<E> propertyWrapper) {
        DefaultableLootBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper)
                .withAttributes(propertyWrapper.getEntityTypeAttributes().orElse(null))
                .withClientData(propertyWrapper.getEntityClientData().orElse(null));
    }

    /**
     * Defines the {@link Supplier} for the parent entity type's default attributes. Usually a functional method reference
     * (e.g. {@code SomeEntity::createAttributes}).
     *
     * @param entityTypeAttributes The {@link Supplier} for the parent entity type's default attributes.
     *
     * @return {@link #self()} (builder method).
     */
    public EntityTypePropertyWrapperBuilder<E> withAttributes(Supplier<AttributeSupplier.Builder> entityTypeAttributes) {
        this.entityTypeAttributes = Optional.ofNullable(entityTypeAttributes);
        return self();
    }

    /**
     * Defines the client-side data for the parent entity type. Should be hidden behind a lambda expression (see
     * references below).
     *
     * @param clientData The {@link EntityClientData} {@link Supplier} to associate with the parent entity type.
     *
     * @return {@link #self()} (builder method).
     *
     * @see EntityClientData
     */
    public EntityTypePropertyWrapperBuilder<E> withClientData(Supplier<EntityClientData<E>> clientData) {
        this.clientData = Optional.ofNullable(clientData);
        return self();
    }

    @Override
    public Optional<SpecializedLootPropertyWrapperBuilder<EntityType<E>, EntityTypePropertyWrapperBuilder<E>, EntityTypePropertyWrapper<E>>> getSpecializedLootBuilder() {
        return Optional.of(compositeLootBuilder);
    }
}
