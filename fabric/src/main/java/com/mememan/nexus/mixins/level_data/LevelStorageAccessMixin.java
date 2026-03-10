package com.mememan.nexus.mixins.level_data;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.template.event.blueprint.common.LevelDataEventBlueprint;
import com.mememan.nexus.template.event.def.common.LevelDataEvent;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.mixin.registry.sync.LevelStorageSessionMixin;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
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
 * For instance, Fabric handles their own serialization of registry data in {@link LevelStorageSessionMixin}, at
 * {@link LevelStorageSource.LevelStorageAccess#saveDataTag(RegistryAccess, WorldData, CompoundTag)}'s head. Forge, on
 * the other hand, handles direct serialization of mod-loader + registry data in the middle of that same method. Thus,
 * to fire our events with deterministic timing across both loaders, we have to specify 2 separate injection points across
 * both loaders, which can most cleanly be done by having 2 separate loader-specific mixins.
 *
 * @apiNote This mixin applies before {@link LevelStorageSessionMixin} (with a priority of 999) in order to account for
 * the injection points of {@link LevelStorageSessionMixin#saveWorld(RegistryAccess, WorldData, CompoundTag, CallbackInfo)}
 * and {@link LevelStorageSessionMixin#readWorldProperties(CallbackInfoReturnable)} and time level data events correctly.
 *
 * @see LevelStorageSessionMixin#saveWorld(RegistryAccess, WorldData, CompoundTag, CallbackInfo)
 * @see LevelStorageSessionMixin#readWorldProperties(CallbackInfoReturnable)
 * @see LevelDataEvent.SaveLevelDataEvent
 * @see LevelDataEventBlueprint.SaveLevelDataEventBlueprint
 */
@Mixin(value = LevelStorageSource.LevelStorageAccess.class, priority = 999)
public abstract class LevelStorageAccessMixin {
    @Shadow
    @Final
    LevelStorageSource.LevelDirectory levelDirectory;

    private LevelStorageAccessMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (LevelStorageAccessMixin)");
    }

    @Inject(method = "saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", shift = At.Shift.BEFORE))
    private void nexus$handlePreVanillaSaveLevelDataEventHook(RegistryAccess registries, WorldData serverConfiguration, CompoundTag hostPlayerNBT, CallbackInfo ci, @Local(ordinal = 2) CompoundTag levelDataTag) {
        LevelDataEvent.SaveLevelDataEvent.PreVanilla preVanillaSaveLevelDataEventHook = new LevelDataEvent.SaveLevelDataEvent.PreVanilla(levelDirectory, levelDataTag, registries, serverConfiguration, hostPlayerNBT);
        LevelDataEventBlueprint.SAVE_LEVEL_DATA_PRE_VANILLA.fireEvent(preVanillaSaveLevelDataEventHook);
    }

    @Inject(method = "saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", shift = At.Shift.AFTER))
    private void nexus$handlePostVanillaSaveLevelDataEventHook(RegistryAccess registries, WorldData serverConfiguration, CompoundTag hostPlayerNBT, CallbackInfo ci, @Local(ordinal = 2) CompoundTag levelDataTag) {
        LevelDataEvent.SaveLevelDataEvent.PostVanilla postVanillaSaveLevelDataEventHook = new LevelDataEvent.SaveLevelDataEvent.PostVanilla(levelDirectory, levelDataTag, registries, serverConfiguration, hostPlayerNBT);
        LevelDataEventBlueprint.SAVE_LEVEL_DATA_POST_VANILLA.fireEvent(postVanillaSaveLevelDataEventHook);
    }

    @Inject(method = "saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    private void nexus$handlePreLoaderSaveLevelDataEventHook(RegistryAccess registries, WorldData serverConfiguration, CompoundTag hostPlayerNBT, CallbackInfo ci) {
        LevelDataEvent.SaveLevelDataEvent.PreLoader preLoaderSaveLevelDataEventHook = new LevelDataEvent.SaveLevelDataEvent.PreLoader(levelDirectory, new CompoundTag(), registries, serverConfiguration, hostPlayerNBT);
        LevelDataEventBlueprint.SAVE_LEVEL_DATA_PRE_LOADER.fireEvent(preLoaderSaveLevelDataEventHook);
    }

    @Inject(method = "saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;toFile()Ljava/io/File;", ordinal = 0, shift = At.Shift.BEFORE))
    private void nexus$handlePostLoaderSaveLevelDataEventHook(RegistryAccess registries, WorldData serverConfiguration, CompoundTag hostPlayerNBT, CallbackInfo ci) {
        LevelDataEvent.SaveLevelDataEvent.PostLoader postLoaderSaveLevelDataEventHook = new LevelDataEvent.SaveLevelDataEvent.PostLoader(levelDirectory, new CompoundTag(), registries, serverConfiguration, hostPlayerNBT);
        LevelDataEventBlueprint.SAVE_LEVEL_DATA_POST_LOADER.fireEvent(postLoaderSaveLevelDataEventHook);
    }

    @ModifyArg(method = "getDataTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource;readLevelData(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelDirectory;Ljava/util/function/BiFunction;)Ljava/lang/Object;"), index = 1)
    private <T> BiFunction<Path, DataFixer, T> nexus$handlePostVanillaLoadLevelDataEventHook(BiFunction<Path, DataFixer, T> levelDatReader) {
        return (pathToLevelDat, curDataFixer) -> {
            try {
                LevelDataEvent.LoadLevelDataEvent.PreVanilla preVanillaLoadLevelDataEventHook = new LevelDataEvent.LoadLevelDataEvent.PreVanilla(levelDirectory, NbtIo.readCompressed(pathToLevelDat.toFile()));
                LevelDataEventBlueprint.LOAD_LEVEL_DATA_PRE_VANILLA.fireEvent(preVanillaLoadLevelDataEventHook);
            } catch (IOException e) {
                NexusConstants.LOGGER.error("Failed to read level data when firing LoadLevelDataEvent.PreVanilla for level '{}':", levelDirectory.directoryName(), e);
            }

            T vanillaLevelData = levelDatReader.apply(pathToLevelDat, curDataFixer);

            try {
                LevelDataEvent.LoadLevelDataEvent.PostVanilla postVanillaLoadLevelDataEventHook = new LevelDataEvent.LoadLevelDataEvent.PostVanilla(levelDirectory, NbtIo.readCompressed(pathToLevelDat.toFile()));
                LevelDataEventBlueprint.LOAD_LEVEL_DATA_POST_VANILLA.fireEvent(postVanillaLoadLevelDataEventHook);
            } catch (IOException e) {
                NexusConstants.LOGGER.error("Failed to read level data when firing LoadLevelDataEvent.PostVanilla for level '{}':", levelDirectory.directoryName(), e);
            }

            return vanillaLevelData;
        };
    }

    @Inject(method = "getDataTag", at = @At("HEAD"))
    private void nexus$handlePreLoaderLoadLevelDataEventHook(DynamicOps<Tag> ops, WorldDataConfiguration dataConfiguration, Registry<LevelStem> levelStemRegistry, Lifecycle lifecycle, CallbackInfoReturnable<Pair<WorldData, WorldDimensions.Complete>> cir) {
        try {
            LevelDataEvent.LoadLevelDataEvent.PreLoader preLoaderLoadLevelDataEventHook = new LevelDataEvent.LoadLevelDataEvent.PreLoader(levelDirectory, NbtIo.readCompressed(new File(levelDirectory.path().toFile(), "level.dat")));
            LevelDataEventBlueprint.LOAD_LEVEL_DATA_PRE_LOADER.fireEvent(preLoaderLoadLevelDataEventHook);
        } catch (IOException e) {
            NexusConstants.LOGGER.error("Failed to read level data when firing LoadLevelDataEvent.PreLoader for level '{}':", levelDirectory.directoryName(), e);
        }
    }

    @Inject(method = "getDataTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;checkLock()V", shift = At.Shift.BEFORE))
    private void nexus$handlePostLoaderLoadLevelDataEventHook(DynamicOps<Tag> ops, WorldDataConfiguration dataConfiguration, Registry<LevelStem> levelStemRegistry, Lifecycle lifecycle, CallbackInfoReturnable<Pair<WorldData, WorldDimensions.Complete>> cir) {
        try {
            LevelDataEvent.LoadLevelDataEvent.PostLoader postLoaderLoadLevelDataEventHook = new LevelDataEvent.LoadLevelDataEvent.PostLoader(levelDirectory, NbtIo.readCompressed(new File(levelDirectory.path().toFile(), "level.dat")));
            LevelDataEventBlueprint.LOAD_LEVEL_DATA_POST_LOADER.fireEvent(postLoaderLoadLevelDataEventHook);
        } catch (IOException e) {
            NexusConstants.LOGGER.error("Failed to read level data when firing LoadLevelDataEvent.PostLoader for level '{}':", levelDirectory.directoryName(), e);
        }
    }
}
