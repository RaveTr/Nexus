package com.mememan.nexus.template.event.def.client;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.server.packs.PackResources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ClientLifeCycleEvent extends BaseEvent {

    public ClientLifeCycleEvent() {
        super(ModSide.CLIENT);
    }

    public static class ResourcePackEvent extends ClientLifeCycleEvent {
        @NotNull
        private final ResourceLoadStateTracker.ReloadReason reloadReason;
        @Nullable
        private final ResourceLoadStateTracker.ReloadState currentReloadState;
        @Nullable
        private final ResourceLoadStateTracker.RecoveryInfo recoveryInfo;
        private final int sessionReloadCount;
        private final List<PackResources> packsToReload;
        @Nullable
        private final Throwable reloadException;

        public ResourcePackEvent(@NotNull ResourceLoadStateTracker.ReloadReason reloadReason, @Nullable ResourceLoadStateTracker.ReloadState currentReloadState, @Nullable ResourceLoadStateTracker.RecoveryInfo recoveryInfo, int sessionReloadCount, List<PackResources> packsToReload, @Nullable Throwable reloadException) {
            this.reloadReason = reloadReason;
            this.currentReloadState = currentReloadState;
            this.recoveryInfo = recoveryInfo;
            this.sessionReloadCount = sessionReloadCount;
            this.packsToReload = packsToReload;
            this.reloadException = reloadException;
        }

        @NotNull
        public ResourceLoadStateTracker.ReloadReason getReloadReason() {
            return reloadReason;
        }

        @Nullable
        public ResourceLoadStateTracker.ReloadState getReloadState() {
            return currentReloadState;
        }

        @Nullable
        public ResourceLoadStateTracker.RecoveryInfo getRecoveryInfo() {
            return recoveryInfo;
        }

        public List<PackResources> getPacksToReload() {
            return packsToReload;
        }

        public int getSessionReloadCount() {
            return sessionReloadCount;
        }

        @Nullable
        public Throwable getReloadException() {
            return reloadException;
        }

        public boolean isRecovering() {
            return recoveryInfo != null;
        }
    }

    public static class ResourcePackReloadPreStartEvent extends ResourcePackEvent {
        private final boolean wasAlreadyReloading;

        public ResourcePackReloadPreStartEvent(@NotNull ResourceLoadStateTracker.ReloadReason reloadReason, @Nullable ResourceLoadStateTracker.ReloadState currentReloadState, @Nullable ResourceLoadStateTracker.RecoveryInfo recoveryInfo, int sessionReloadCount, List<PackResources> packsToReload, @Nullable Throwable reloadException, boolean wasAlreadyReloading) {
            super(reloadReason, currentReloadState, recoveryInfo, sessionReloadCount, packsToReload, reloadException);

            this.wasAlreadyReloading = wasAlreadyReloading;
        }

        public boolean wasAlreadyReloading() {
            return wasAlreadyReloading;
        }
    }

    public static class ResourcePackReloadStartEvent extends ResourcePackReloadPreStartEvent {

        public ResourcePackReloadStartEvent(@NotNull ResourceLoadStateTracker.ReloadReason reloadReason, @Nullable ResourceLoadStateTracker.ReloadState currentReloadState, @Nullable ResourceLoadStateTracker.RecoveryInfo recoveryInfo, int sessionReloadCount, List<PackResources> packsToReload, @Nullable Throwable reloadException, boolean wasAlreadyReloading) {
            super(reloadReason, currentReloadState, recoveryInfo, sessionReloadCount, packsToReload, reloadException, wasAlreadyReloading);
        }
    }

    public static class ResourcePackReloadEndEvent extends ResourcePackEvent {
        private final boolean wasAborted;

        public ResourcePackReloadEndEvent(@NotNull ResourceLoadStateTracker.ReloadReason reloadReason, @Nullable ResourceLoadStateTracker.ReloadState currentReloadState, @Nullable ResourceLoadStateTracker.RecoveryInfo recoveryInfo, int sessionReloadCount, List<PackResources> packsToReload, @Nullable Throwable reloadException, boolean wasAborted) {
            super(reloadReason, currentReloadState, recoveryInfo, sessionReloadCount, packsToReload, reloadException);

            this.wasAborted = wasAborted;
        }

        public boolean wasAborted() {
            return wasAborted;
        }
    }
}
