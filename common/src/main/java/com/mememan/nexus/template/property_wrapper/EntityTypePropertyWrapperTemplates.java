package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapperBuilder;
import com.mememan.nexus.template.object.client.ClientDataEntryTemplates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link EntityTypePropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class EntityTypePropertyWrapperTemplates {
    public static final EntityTypePropertyWrapper<Boat> BOAT = new EntityTypePropertyWrapper<Boat>()
            .builder()
            .withClientData(() -> ClientDataEntryTemplates.BOAT_CLIENT_DATA)
            .build();
    public static final EntityTypePropertyWrapper<Boat> CHEST_BOAT = new EntityTypePropertyWrapper<Boat>()
            .builder()
            .withClientData(() -> ClientDataEntryTemplates.CHEST_BOAT_CLIENT_DATA)
            .build();

    public static final EntityTypePropertyWrapper<Boat> RAFT = new EntityTypePropertyWrapper<Boat>()
            .builder()
            .withClientData(() -> ClientDataEntryTemplates.RAFT_CLIENT_DATA)
            .build();
    public static final EntityTypePropertyWrapper<Boat> CHEST_RAFT = new EntityTypePropertyWrapper<Boat>()
            .builder()
            .withClientData(() -> ClientDataEntryTemplates.CHEST_RAFT_CLIENT_DATA)
            .build();

    private EntityTypePropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (EntityTypePropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link EntityType}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     * @param entityTypeSupCol An optional {@link Collection} to track the registered {@link EntityType}. Primarily
     *                         useful if you want a shorthand method of tracking your own registered entity types.
     *
     * @return The {@link Supplier} of the registered {@link EntityType}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> Supplier<EntityType<E>> registerEntityType(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup, @Nullable Collection<Supplier<EntityType<Entity>>> entityTypeSupCol) {
        Supplier<EntityType<E>> registeredEntityType = NexusServices.REGISTRAR.registerObject(entityId, entityTypeSup, BuiltInRegistries.ENTITY_TYPE);

        if (entityTypeSupCol != null) entityTypeSupCol.add(() -> (EntityType<Entity>) registeredEntityType.get());

        return registeredEntityType;
    }

    /**
     * Overloaded variant of {@link #registerEntityType(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link EntityType} to any custom {@link Collection}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     *
     * @return The {@link Supplier} of the registered {@link EntityType}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> Supplier<EntityType<E>> registerEntityType(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup) {
        return registerEntityType(entityId, entityTypeSup, null);
    }

    /**
     * Registers and returns the provided {@link EntityType}, mapping it to a new {@link EntityTypePropertyWrapper} inheriting
     * from the provided {@link EntityTypePropertyWrapper} template. Optionally tracks the registered {@link EntityType} to a
     * custom {@link Collection}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     * @param templateBPW The {@link EntityTypePropertyWrapper} template to inherit from.
     * @param entityTypeSupCol An optional {@link Collection} to track the registered {@link EntityType}. Primarily
     *                         useful if you want a shorthand method of tracking your own registered entity types.
     *
     * @return The {@link Supplier} of the registered {@link EntityType}, mapped to its own {@link EntityTypePropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> Supplier<EntityType<E>> registerEntityTypeFromTemplate(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup, EntityTypePropertyWrapper<? super E> templateBPW, @Nullable Collection<Supplier<EntityType<Entity>>> entityTypeSupCol) {
        Supplier<EntityType<E>> registeredEntityType = registerEntityType(entityId, entityTypeSup, entityTypeSupCol);

        return new EntityTypePropertyWrapper<>(registeredEntityType, entityId.getNamespace())
                .builder()
                .copyFrom((EntityTypePropertyWrapper<E>) templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerEntityTypeFromTemplate(ResourceLocation, Supplier, EntityTypePropertyWrapper, Collection)}
     * that does not track the registered {@link EntityType} to any custom {@link Collection}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     * @param templateBPW The {@link EntityTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link EntityType}, mapped to its own {@link EntityTypePropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> Supplier<EntityType<E>> registerEntityTypeFromTemplate(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup, EntityTypePropertyWrapper<? super E> templateBPW) {
        return registerEntityTypeFromTemplate(entityId, entityTypeSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link EntityType} and returns its {@link EntityTypePropertyWrapperBuilder} inheriting from the
     * provided {@link EntityTypePropertyWrapper} template. Optionally tracks the registered {@link EntityType} to a custom
     * {@link Collection}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     * @param templateBPW The {@link EntityTypePropertyWrapper} template to inherit from.
     * @param entityTypeSupCol An optional {@link Collection} to track the registered {@link EntityType}. Primarily
     *                         useful if you want a shorthand method of tracking your own registered entity types.
     *
     * @return The {@link EntityTypePropertyWrapperBuilder} of the registered {@link EntityType}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> EntityTypePropertyWrapperBuilder<E> registerAndChain(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup, EntityTypePropertyWrapper<E> templateBPW, @Nullable Collection<Supplier<EntityType<Entity>>> entityTypeSupCol) {
        Supplier<EntityType<E>> registeredEntityType = registerEntityType(entityId, entityTypeSup, entityTypeSupCol);

        return new EntityTypePropertyWrapper<>(registeredEntityType, entityId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, EntityTypePropertyWrapper, Collection)} that does not track the
     * registered {@link EntityType} to any custom {@link Collection}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     * @param templateBPW The {@link EntityTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link EntityTypePropertyWrapperBuilder} of the registered {@link EntityType}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> EntityTypePropertyWrapperBuilder<E> registerAndChain(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup, EntityTypePropertyWrapper<E> templateBPW) {
        return registerAndChain(entityId, entityTypeSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link EntityType} and returns its {@link EntityTypePropertyWrapperBuilder} inheriting from the
     * provided {@link EntityTypePropertyWrapper} template. Optionally tracks the registered {@link EntityType} to a custom
     * {@link Collection}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     * @param entityTypeSupCol An optional {@link Collection} to track the registered {@link EntityType}. Primarily useful if you
     *                     want a shorthand method of tracking your own registered entity types.
     *
     * @return The {@link EntityTypePropertyWrapperBuilder} of the registered {@link EntityType}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> EntityTypePropertyWrapperBuilder<E> registerAndChain(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup, @Nullable Collection<Supplier<EntityType<Entity>>> entityTypeSupCol) {
        Supplier<EntityType<E>> registeredEntityType = registerEntityType(entityId, entityTypeSup, entityTypeSupCol);

        return new EntityTypePropertyWrapper<>(registeredEntityType, entityId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link EntityType}.
     *
     * @param entityId The target {@linkplain EntityType EntityType's} {@linkplain ResourceLocation registry ID}.
     * @param entityTypeSup The {@link EntityType} object to register.
     *
     * @return The {@link EntityTypePropertyWrapperBuilder} of the registered {@link EntityType}.
     *
     * @param <E> Any {@link Entity} type.
     */
    public static <E extends Entity> EntityTypePropertyWrapperBuilder<E> registerAndChain(ResourceLocation entityId, Supplier<EntityType<E>> entityTypeSup) {
        return registerAndChain(entityId, entityTypeSup, (Collection<Supplier<EntityType<Entity>>>) null);
    }
}
