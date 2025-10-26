package com.mememan.nexus.mixins.fabric_api.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

/**
 * Accessor {@code interface} to allow for {@code registryLookup} access from Fabric's {@link FabricDataGenerator} to be
 * used for context during datagen initialization.
 *
 * @see com.mememan.nexus.internal.services.FabricDataGenerator#onInitializeDataGenerator(FabricDataGenerator)
 */
@Mixin(value = FabricDataGenerator.class, remap = false)
public interface FabricDataGeneratorAccessor {

    @Accessor("fabricOutput")
    FabricDataOutput getFabricOutput();

    @Accessor("registriesFuture")
    CompletableFuture<HolderLookup.Provider> getRegistriesFuture();
}
