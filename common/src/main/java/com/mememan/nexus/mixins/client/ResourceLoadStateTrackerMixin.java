package com.mememan.nexus.mixins.client;

import com.mememan.nexus.template.event.blueprint.client.ClientLifeCycleEventBlueprint;
import com.mememan.nexus.template.event.def.client.ClientLifeCycleEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Client-side Mixin {@code class} primarily responsible for handling firing resource pack reload event hooks (see
 * references below).
 *
 * @see ClientLifeCycleEvent.ResourcePackReloadStartEvent
 * @see ClientLifeCycleEvent.ResourcePackReloadEndEvent
 * @see ClientLifeCycleEventBlueprint.ResourcePackReloadStartEventBlueprint
 * @see ClientLifeCycleEventBlueprint.ResourcePackReloadEndEventBlueprint
 * @see MinecraftMixin#nexus$handleResourcePackReloadStartEventPrePhaseHook(boolean, CallbackInfoReturnable)
 */
@Mixin(ResourceLoadStateTracker.class)
public abstract class ResourceLoadStateTrackerMixin {
    @Shadow
    public ResourceLoadStateTracker.ReloadState reloadState;
    @Shadow
    public int reloadCount;

    private ResourceLoadStateTrackerMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (ResourceLoadStateTrackerMixin)");
    }

    @Inject(method = "startReload", at = @At("TAIL"))
    private void nexus$handleResourcePackReloadStartEventHook(ResourceLoadStateTracker.ReloadReason reloadReason, List<PackResources> packs, CallbackInfo ci) {
        ClientLifeCycleEvent.ResourcePackReloadStartEvent resourcePackReloadStartEventHook = new ClientLifeCycleEvent.ResourcePackReloadStartEvent(
                reloadReason,
                reloadState,
                reloadState == null ? null : reloadState.recoveryReloadInfo, // JIC
                reloadCount,
                packs,
                reloadState == null || reloadState.recoveryReloadInfo == null ? null : reloadState.recoveryReloadInfo.error,
                reloadState != null && !reloadState.finished
        );

        ClientLifeCycleEventBlueprint.RESOURCEPACK_RELOAD_START.fireEvent(resourcePackReloadStartEventHook);
    }

    @Inject(method = "finishReload", at = @At("TAIL"))
    private void nexus$handleResourcePackReloadEndEventHook(CallbackInfo ci) {
        ClientLifeCycleEvent.ResourcePackReloadEndEvent resourcePackReloadEndEventHook = new ClientLifeCycleEvent.ResourcePackReloadEndEvent(
                reloadState == null ? ResourceLoadStateTracker.ReloadReason.UNKNOWN : reloadState.reloadReason,
                reloadState,
                reloadState == null ? null : reloadState.recoveryReloadInfo,
                reloadCount,
                Minecraft.getInstance().getResourcePackRepository().openAllSelected(),
                reloadState == null || reloadState.recoveryReloadInfo == null ? null : reloadState.recoveryReloadInfo.error,
                false
        );

        ClientLifeCycleEventBlueprint.RESOURCEPACK_RELOAD_END.fireEvent(resourcePackReloadEndEventHook);
    }
}
