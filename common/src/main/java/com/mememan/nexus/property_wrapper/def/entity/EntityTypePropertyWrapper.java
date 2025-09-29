package com.mememan.nexus.property_wrapper.def.entity;

import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class EntityTypePropertyWrapper<E extends Entity> extends BaseDefaultableBareDataGenPropertyWrapper<EntityType<E>, EntityTypePropertyWrapper<E>, EntityTypePropertyWrapperBuilder<E>> implements DefaultableLootBasedPropertyWrapper<EntityType<E>, EntityTypePropertyWrapper<E>, EntityTypePropertyWrapperBuilder<E>> {
    protected final SpecializedLootPropertyWrapper<EntityType<E>, ?, ?> compositeLootWrapper;

    public EntityTypePropertyWrapper(Supplier<EntityType<E>> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, EntityTypePropertyWrapperBuilder::new, modId);

        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public EntityTypePropertyWrapper(@NotNull Supplier<EntityType<E>> parentObject, String modId) {
        super(parentObject, EntityTypePropertyWrapperBuilder::new, modId);

        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, modId);
    }

    public EntityTypePropertyWrapper() {
        super(EntityTypePropertyWrapperBuilder::new);

        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>();
    }

    /**
     * Gets the {@link Supplier} describing the parent entity type's default attributes.
     *
     * @return The parent entity type's {@link AttributeSupplier.Builder}. May be empty.
     *
     * @see EntityTypePropertyWrapperBuilder#withAttributes(Supplier)
     */
    public Optional<Supplier<AttributeSupplier.Builder>> getEntityTypeAttributes() {
        return rawBuilder().flatMap(builder -> builder.entityTypeAttributes);
    }

    /**
     * Gets the side-safe {@link Supplier} for the parent entity type's client-side data.
     *
     * @return The parent entity type's {@link EntityClientData}. May be empty.
     *
     * @see EntityTypePropertyWrapperBuilder#withClientData(Supplier)
     */
    public Optional<Supplier<EntityClientData<E>>> getEntityClientData() {
        return rawBuilder().flatMap(builder -> builder.clientData);
    }

    @Override
    public Optional<String> getDescriptionIdPrefix() {
        return Optional.of("entity");
    }

    @Override
    public @NotNull String getLootTableDir() {
        return "entities";
    }

    @Override
    public Optional<SpecializedLootPropertyWrapper<EntityType<E>, ?, ?>> getSpecializedLootWrapper() {
        return Optional.of(compositeLootWrapper);
    }
}
