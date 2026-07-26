package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.property_wrapper.def.block_entity.BlockEntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block_entity.BlockEntityTypePropertyWrapperBuilder;
import com.mememan.nexus.template.object.client.ClientDataEntryTemplates;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link BlockEntityTypePropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class BlockEntityTypePropertyWrapperTemplates {
    public static final BlockEntityTypePropertyWrapper<SignBlockEntity> SIGN = new BlockEntityTypePropertyWrapper<SignBlockEntity>()
            .builder()
            .withClientData(() -> ClientDataEntryTemplates.SIGN_CLIENT_DATA)
            .build();
    public static final BlockEntityTypePropertyWrapper<SignBlockEntity> HANGING_SIGN = new BlockEntityTypePropertyWrapper<SignBlockEntity>()
            .builder()
            .withClientData(() -> ClientDataEntryTemplates.HANGING_SIGN_CLIENT_DATA)
            .build();

    private BlockEntityTypePropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (BlockEntityTypePropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link BlockEntityType}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity types.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityType(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = NexusServices.REGISTRAR.registerObject(blockEntityId, blockEntityTypeSup, BuiltInRegistries.BLOCK_ENTITY_TYPE);

        if (blockEntityTypeSupCol != null) blockEntityTypeSupCol.add(() -> (BlockEntityType<BlockEntity>) registeredBlockEntityType.get());

        return registeredBlockEntityType;
    }

    /**
     * Overloaded variant of {@link #registerBlockEntityType(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link BlockEntityType} to any custom {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityType(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup) {
        return registerBlockEntityType(blockEntityId, blockEntityTypeSup, null);
    }

    /**
     * Registers and returns the provided {@link BlockEntityType}, mapping it to a new {@link BlockEntityTypePropertyWrapper} inheriting
     * from the provided {@link BlockEntityTypePropertyWrapper} template. Optionally tracks the registered {@link BlockEntityType} to a
     * custom {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity types.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}, mapped to its own {@link BlockEntityTypePropertyWrapper}
     * inheriting from the provided {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityTypeFromTemplate(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW, @Nullable Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = registerBlockEntityType(blockEntityId, blockEntityTypeSup, blockEntityTypeSupCol);

        return new BlockEntityTypePropertyWrapper<>(registeredBlockEntityType, blockEntityId.getNamespace())
                .builder()
                .copyFrom((BlockEntityTypePropertyWrapper<BE>) templateBEPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockEntityTypeFromTemplate(ResourceLocation, Supplier, BlockEntityTypePropertyWrapper, Collection)}
     * that does not track the registered {@link BlockEntityType} to any custom {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}, mapped to its own {@link BlockEntityTypePropertyWrapper}
     * inheriting from the provided {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityTypeFromTemplate(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW) {
        return registerBlockEntityTypeFromTemplate(blockEntityId, blockEntityTypeSup, templateBEPW, null);
    }

    /**
     * Registers the provided {@link BlockEntityType} and returns its {@link BlockEntityTypePropertyWrapperBuilder} inheriting from the
     * provided {@link BlockEntityTypePropertyWrapper} template. Optionally tracks the registered {@link BlockEntityType} to a custom
     * {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity types.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}, inheriting from the provided
     * {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW, @Nullable Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = registerBlockEntityType(blockEntityId, blockEntityTypeSup, blockEntityTypeSupCol);

        return new BlockEntityTypePropertyWrapper<>(registeredBlockEntityType, blockEntityId.getNamespace())
                .builder()
                .copyFrom((BlockEntityTypePropertyWrapper<BE>) templateBEPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, BlockEntityTypePropertyWrapper, Collection)} that does not track the
     * registered {@link BlockEntityType} to any custom {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}, inheriting from the provided
     * {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW) {
        return registerAndChain(blockEntityId, blockEntityTypeSup, templateBEPW, null);
    }

    /**
     * Registers the provided {@link BlockEntityType} and returns its {@link BlockEntityTypePropertyWrapperBuilder} inheriting from the
     * provided {@link BlockEntityTypePropertyWrapper} template. Optionally tracks the registered {@link BlockEntityType} to a custom
     * {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity
     *                              types.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, @Nullable Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = registerBlockEntityType(blockEntityId, blockEntityTypeSup, blockEntityTypeSupCol);

        return new BlockEntityTypePropertyWrapper<>(registeredBlockEntityType, blockEntityId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link BlockEntityType}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup) {
        return registerAndChain(blockEntityId, blockEntityTypeSup, (Collection<Supplier<BlockEntityType<BlockEntity>>>) null);
    }

    /**
     * Registers and returns the provided {@link BlockEntityType}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity types.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityTypeAndReflect(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = NexusServices.REGISTRAR.registerObjectAndReflect(blockEntityId, blockEntityTypeSup, BuiltInRegistries.BLOCK_ENTITY_TYPE);

        if (blockEntityTypeSupCol != null) blockEntityTypeSupCol.add(() -> (BlockEntityType<BlockEntity>) registeredBlockEntityType.get());

        return registeredBlockEntityType;
    }

    /**
     * Overloaded variant of {@link #registerBlockEntityTypeAndReflect(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link BlockEntityType} to any custom {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityTypeAndReflect(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup) {
        return registerBlockEntityTypeAndReflect(blockEntityId, blockEntityTypeSup, null);
    }

    /**
     * Registers and returns the provided {@link BlockEntityType}, mapping it to a new {@link BlockEntityTypePropertyWrapper} inheriting
     * from the provided {@link BlockEntityTypePropertyWrapper} template. Optionally tracks the registered {@link BlockEntityType} to a
     * custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity types.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}, mapped to its own {@link BlockEntityTypePropertyWrapper}
     * inheriting from the provided {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityTypeFromTemplateAndReflect(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW, @Nullable Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = registerBlockEntityTypeAndReflect(blockEntityId, blockEntityTypeSup, blockEntityTypeSupCol);

        return new BlockEntityTypePropertyWrapper<>(registeredBlockEntityType, blockEntityId.getNamespace())
                .builder()
                .copyFrom((BlockEntityTypePropertyWrapper<BE>) templateBEPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockEntityTypeFromTemplateAndReflect(ResourceLocation, Supplier, BlockEntityTypePropertyWrapper, Collection)}
     * that does not track the registered {@link BlockEntityType} to any custom {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link BlockEntityType}, mapped to its own {@link BlockEntityTypePropertyWrapper}
     * inheriting from the provided {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> registerBlockEntityTypeFromTemplateAndReflect(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW) {
        return registerBlockEntityTypeFromTemplateAndReflect(blockEntityId, blockEntityTypeSup, templateBEPW, null);
    }

    /**
     * Registers the provided {@link BlockEntityType} and returns its {@link BlockEntityTypePropertyWrapperBuilder} inheriting from the
     * provided {@link BlockEntityTypePropertyWrapper} template. Optionally tracks the registered {@link BlockEntityType} to a custom
     * {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity types.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}, inheriting from the provided
     * {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndReflectAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW, @Nullable Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = registerBlockEntityTypeAndReflect(blockEntityId, blockEntityTypeSup, blockEntityTypeSupCol);

        return new BlockEntityTypePropertyWrapper<>(registeredBlockEntityType, blockEntityId.getNamespace())
                .builder()
                .copyFrom((BlockEntityTypePropertyWrapper<BE>) templateBEPW);
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, BlockEntityTypePropertyWrapper, Collection)} that does not track the
     * registered {@link BlockEntityType} to any custom {@link Collection}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param templateBEPW The {@link BlockEntityTypePropertyWrapper} template to inherit from.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}, inheriting from the provided
     * {@code templateBEPW}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndReflectAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, BlockEntityTypePropertyWrapper<? super BE> templateBEPW) {
        return registerAndReflectAndChain(blockEntityId, blockEntityTypeSup, templateBEPW, null);
    }

    /**
     * Registers the provided {@link BlockEntityType} and returns its {@link BlockEntityTypePropertyWrapperBuilder}. Optionally tracks the
     * registered {@link BlockEntityType} to a custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     * @param blockEntityTypeSupCol An optional {@link Collection} to track the registered {@link BlockEntityType}. Primarily
     *                              useful if you want a shorthand method of tracking your own registered block entity
     *                              types.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndReflectAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup, @Nullable Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol) {
        Supplier<BlockEntityType<BE>> registeredBlockEntityType = registerBlockEntityTypeAndReflect(blockEntityId, blockEntityTypeSup, blockEntityTypeSupCol);

        return new BlockEntityTypePropertyWrapper<>(registeredBlockEntityType, blockEntityId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link BlockEntityType}.
     *
     * @param blockEntityId The target {@linkplain BlockEntityType BlockEntityType's} {@linkplain ResourceLocation registry ID}.
     * @param blockEntityTypeSup The {@link BlockEntityType} object to register.
     *
     * @return The {@link BlockEntityTypePropertyWrapperBuilder} of the registered {@link BlockEntityType}.
     *
     * @param <BE> Any {@link BlockEntity} type.
     */
    public static <BE extends BlockEntity> BlockEntityTypePropertyWrapperBuilder<BE> registerAndReflectAndChain(ResourceLocation blockEntityId, Supplier<BlockEntityType<BE>> blockEntityTypeSup) {
        return registerAndReflectAndChain(blockEntityId, blockEntityTypeSup, (Collection<Supplier<BlockEntityType<BlockEntity>>>) null);
    }
}
