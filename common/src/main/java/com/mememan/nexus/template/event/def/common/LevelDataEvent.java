package com.mememan.nexus.template.event.def.common;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelDataEvent extends BaseEvent {
    protected final LevelStorageSource.LevelDirectory levelDirectory;
    @NotNull
    protected final CompoundTag levelDataTag;

    public LevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, @NotNull CompoundTag levelDataTag) {
        super(NexusServices.PLATFORM_MANAGER.getEnvironmentSide().isDedicatedServer() ? ModSide.SERVER : ModSide.COMMON);

        this.levelDirectory = levelDirectory;
        this.levelDataTag = levelDataTag;
    }

    public LevelStorageSource.LevelDirectory getLevelDirectory() {
        return levelDirectory;
    }

    @NotNull
    public CompoundTag getLevelDataTag() {
        return levelDataTag;
    }

    public static class SaveLevelDataEvent extends LevelDataEvent {
        protected final RegistryAccess registriesView;
        protected final WorldData serverConfiguration;
        @Nullable
        protected final CompoundTag hostPlayerNBT;

        public SaveLevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, @NotNull CompoundTag levelDataTag, RegistryAccess registriesView, WorldData serverConfiguration, @Nullable CompoundTag hostPlayerNBT) {
            super(levelDirectory, levelDataTag);

            this.registriesView = registriesView;
            this.serverConfiguration = serverConfiguration;
            this.hostPlayerNBT = hostPlayerNBT;
        }

        public SaveLevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, @NotNull CompoundTag levelDataTag, RegistryAccess registriesView, WorldData serverConfiguration) {
            this(levelDirectory, levelDataTag, registriesView, serverConfiguration, null);
        }

        public RegistryAccess getRegistriesView() {
            return registriesView;
        }

        public WorldData getServerConfiguration() {
            return serverConfiguration;
        }

        @Nullable
        public CompoundTag getHostPlayerNBT() {
            return hostPlayerNBT;
        }
    }

    public static class LoadLevelDataEvent extends LevelDataEvent {

        public LoadLevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag) {
            super(levelDirectory, levelDataTag);
        }
    }
}
