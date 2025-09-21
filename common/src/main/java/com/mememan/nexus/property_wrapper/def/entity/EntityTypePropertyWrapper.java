package com.mememan.nexus.property_wrapper.def.entity;

import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    @Override
    public Optional<SpecializedLootPropertyWrapper<EntityType<E>, ?, ?>> getSpecializedLootWrapper() {
        return Optional.of(compositeLootWrapper);
    }
}
