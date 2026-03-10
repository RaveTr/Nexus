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
    protected final Phase phase;

    public LevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, @NotNull CompoundTag levelDataTag, Phase phase) {
        super(NexusServices.PLATFORM_MANAGER.getEnvironmentSide().isDedicatedServer() ? ModSide.SERVER : ModSide.COMMON);

        this.levelDirectory = levelDirectory;
        this.levelDataTag = levelDataTag;
        this.phase = phase;
    }

    public LevelStorageSource.LevelDirectory getLevelDirectory() {
        return levelDirectory;
    }

    @NotNull
    public CompoundTag getLevelDataTag() {
        return levelDataTag;
    }

    @NotNull
    public Phase getPhase() {
        return phase;
    }

    public static class SaveLevelDataEvent extends LevelDataEvent {
        protected final RegistryAccess registriesView;
        protected final WorldData serverConfiguration;
        @Nullable
        protected final CompoundTag hostPlayerNBT;

        public SaveLevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, @NotNull CompoundTag levelDataTag, Phase phase, RegistryAccess registriesView, WorldData serverConfiguration, @Nullable CompoundTag hostPlayerNBT) {
            super(levelDirectory, levelDataTag, phase);

            this.registriesView = registriesView;
            this.serverConfiguration = serverConfiguration;
            this.hostPlayerNBT = hostPlayerNBT;
        }

        public SaveLevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, @NotNull CompoundTag levelDataTag, Phase phase, RegistryAccess registriesView, WorldData serverConfiguration) {
            this(levelDirectory, levelDataTag, phase, registriesView, serverConfiguration, null);
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

        public static class PreLoader extends SaveLevelDataEvent {

            public PreLoader(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag, RegistryAccess registriesView, WorldData serverConfiguration, @Nullable CompoundTag hostPlayerNBT) {
                super(levelDirectory, levelDataTag, Phase.BEFORE_LOADERS, registriesView, serverConfiguration, hostPlayerNBT);
            }
        }

        public static class PostLoader extends SaveLevelDataEvent {

            public PostLoader(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag, RegistryAccess registriesView, WorldData serverConfiguration, @Nullable CompoundTag hostPlayerNBT) {
                super(levelDirectory, levelDataTag, Phase.AFTER_LOADERS, registriesView, serverConfiguration, hostPlayerNBT);
            }
        }

        public static class PreVanilla extends SaveLevelDataEvent {

            public PreVanilla(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag, RegistryAccess registriesView, WorldData serverConfiguration, @Nullable CompoundTag hostPlayerNBT) {
                super(levelDirectory, levelDataTag, Phase.BEFORE_VANILLA, registriesView, serverConfiguration, hostPlayerNBT);
            }
        }

        public static class PostVanilla extends SaveLevelDataEvent {

            public PostVanilla(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag, RegistryAccess registriesView, WorldData serverConfiguration, @Nullable CompoundTag hostPlayerNBT) {
                super(levelDirectory, levelDataTag, Phase.AFTER_VANILLA, registriesView, serverConfiguration, hostPlayerNBT);
            }
        }
    }

    public static class LoadLevelDataEvent extends LevelDataEvent {

        public LoadLevelDataEvent(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag, Phase phase) {
            super(levelDirectory, levelDataTag, phase);
        }

        public static class PreLoader extends LoadLevelDataEvent {

            public PreLoader(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag) {
                super(levelDirectory, levelDataTag, Phase.BEFORE_LOADERS);
            }
        }

        public static class PostLoader extends LoadLevelDataEvent {

            public PostLoader(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag) {
                super(levelDirectory, levelDataTag, Phase.AFTER_LOADERS);
            }
        }

        public static class PreVanilla extends LoadLevelDataEvent {

            public PreVanilla(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag) {
                super(levelDirectory, levelDataTag, Phase.BEFORE_VANILLA);
            }
        }

        public static class PostVanilla extends LoadLevelDataEvent {

            public PostVanilla(LevelStorageSource.LevelDirectory levelDirectory, CompoundTag levelDataTag) {
                super(levelDirectory, levelDataTag, Phase.AFTER_VANILLA);
            }
        }
    }

    public enum Phase {
        BEFORE_LOADERS,
        AFTER_LOADERS,
        BEFORE_VANILLA,
        AFTER_VANILLA,;
    }
}
