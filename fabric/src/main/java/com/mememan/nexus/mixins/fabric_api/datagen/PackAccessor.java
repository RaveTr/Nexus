package com.mememan.nexus.mixins.fabric_api.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Accessor {@code interface} to allow for {@link FabricDataGenerator.Pack} object construction from within
 * {@link FabricDataGenerator}.
 *
 * @see com.mememan.nexus.internal.services.FabricDataGenerator#onInitializeDataGenerator(FabricDataGenerator)
 */
@Mixin(value = FabricDataGenerator.Pack.class, remap = false)
public interface PackAccessor {

    @Invoker("<init>")
    static FabricDataGenerator.Pack create(FabricDataGenerator outerGen, boolean shouldRun, String name, FabricDataOutput output) {
        throw new AssertionError();
    }
}
