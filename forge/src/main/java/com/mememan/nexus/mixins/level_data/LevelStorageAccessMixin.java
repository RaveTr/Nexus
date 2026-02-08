package com.mememan.nexus.mixins.level_data;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.template.event.blueprint.common.LevelDataEventBlueprint;
import com.mememan.nexus.template.event.def.common.LevelDataEvent;
import com.mojang.datafixers.DataFixer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;

/**
 * Mixin {@code class} responsible for firing and handling different events associated with level data read/write ops.
 * <br></br>
 * The reason as to why this mixin has to exist separately across loaders (as opposed to just one inside the common
 * module) has to do with the fact that Forge injects its own patch for hooking into level data saving/loading in spots
 * that would make it difficult for a single common mixin to predictably fire events at the same time relative to
 * both loaders' level data hooks.
 * <br><br>
 * For instance, Fabric handles their own serialization of registry data in their own mixin, at
 * {@link LevelStorageSource.LevelStorageAccess#saveDataTag(RegistryAccess, WorldData, CompoundTag)}'s head. Forge, on
 * the other hand, handles direct serialization of mod-loader + registry data in the middle of that same method. Thus,
 * to fire our events with deterministic timing across both loaders, we have to specify 2 separate injection points across
 * both loaders, which can most cleanly be done by having 2 separate loader-specific mixins.
 *
 * @see LevelStorageSource.LevelStorageAccess#saveDataTag(RegistryAccess, WorldData, CompoundTag)
 * @see LevelDataEvent.SaveLevelDataEvent
 * @see LevelDataEventBlueprint.SaveLevelDataEventBlueprint
 */
@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class LevelStorageAccessMixin {
    @Shadow
    @Final
    LevelStorageSource.LevelDirectory levelDirectory;

    private LevelStorageAccessMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (LevelStorageAccessMixin)");
    }

    @Inject(method = "saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;writeAdditionalLevelSaveData(Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER, remap = false))
    private void nexus$handleSaveLevelDataEventHook(RegistryAccess registries, WorldData serverConfiguration, CompoundTag hostPlayerNBT, CallbackInfo ci, @Local(ordinal = 2) CompoundTag levelDataTag) {
        LevelDataEvent.SaveLevelDataEvent saveLevelDataEventHook = new LevelDataEvent.SaveLevelDataEvent(levelDirectory, levelDataTag, registries, serverConfiguration, hostPlayerNBT);
        LevelDataEventBlueprint.SAVE_LEVEL_DATA.fireEvent(saveLevelDataEventHook);
    }

    @ModifyArg(method = "getDataConfiguration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource;readLevelData(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelDirectory;Ljava/util/function/BiFunction;)Ljava/lang/Object;"), index = 1)
    private <T> BiFunction<Path, DataFixer, T> nexus$handleLoadLevelDataEventHook(BiFunction<Path, DataFixer, T> levelDatReader) {
        return (pathToLevelDat, curDataFixer) -> {
            try {
                LevelDataEvent.LoadLevelDataEvent loadLevelDataEventHook = new LevelDataEvent.LoadLevelDataEvent(levelDirectory, NbtIo.readCompressed(pathToLevelDat.toFile()));
                LevelDataEventBlueprint.LOAD_LEVEL_DATA.fireEvent(loadLevelDataEventHook);
            } catch (IOException e) {
                NexusConstants.LOGGER.error("Failed to read level data when firing LoadLevelDataEvent for level '{}':", levelDirectory.directoryName(), e);
            }

            return levelDatReader.apply(pathToLevelDat, curDataFixer);
        };
    }
}
