package com.mememan.nexus.mixins.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.client.ClientLifeCycleEventBlueprint;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.client.ClientLifeCycleEvent;
import com.mememan.nexus.template.event.def.common.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.client.Timer;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * Mixin {@code class} responsible for firing and handling client tick event hooks with as little intrusion as
 * possible.
 *
 * @see TickEventBlueprint.ClientTickEventBlueprint
 * @see TickEvent.ClientTickEvent
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    private Timer timer;
    @Shadow
    private volatile boolean pause;
    @Shadow
    private float pausePartialTick;
    @Shadow
    @Final
    private ResourceLoadStateTracker reloadStateTracker;
    @Shadow
    @Final
    private PackRepository resourcePackRepository;
    @Shadow
    private CompletableFuture<Void> pendingReload;

    private MinecraftMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (MinecraftMixin)");
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", ordinal = 0), cancellable = true)
    private void nexus$handlePreClientTickEventHook(CallbackInfo ci) {
        TickEvent.ClientTickEvent clientTickEventHook = new TickEvent.ClientTickEvent(TickEvent.Phase.START);
        EventResult<TickEvent.ClientTickEvent> clientTickEventResult = TickEventBlueprint.CLIENT_TICK.fireEvent(clientTickEventHook);

        if (clientTickEventResult.cancelled()) ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nexus$handlePostClientTickEventHook(CallbackInfo ci) {
        TickEvent.ClientTickEvent clientTickEventHook = new TickEvent.ClientTickEvent(TickEvent.Phase.END);
        TickEventBlueprint.CLIENT_TICK.fireEvent(clientTickEventHook);
    }

    @Definition(id = "noRender", field = "Lnet/minecraft/client/Minecraft;noRender:Z")
    @Expression("this.noRender")
    @ModifyExpressionValue(method = "runTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$handlePreRenderTickEventHook(boolean original, @Local(argsOnly = true) boolean renderLevel) {
        float actualPartialTick = this.pause ? this.pausePartialTick : this.timer.partialTick;
        TickEvent.RenderTickEvent renderTickEventHook = new TickEvent.RenderTickEvent(TickEvent.Phase.START, renderLevel, actualPartialTick);
        EventResult<TickEvent.RenderTickEvent> renderTickEventResult = TickEventBlueprint.RENDER_TICK.fireEvent(renderTickEventHook);

        return original || renderTickEventResult.cancelled();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 4, shift = At.Shift.AFTER))
    private void nexus$handlePostRenderTickEventHook(CallbackInfo ci, @Local(argsOnly = true) boolean renderLevel) {
        float actualPartialTick = this.pause ? this.pausePartialTick : this.timer.partialTick;
        TickEvent.RenderTickEvent renderTickEventHook = new TickEvent.RenderTickEvent(TickEvent.Phase.END, renderLevel, actualPartialTick);

        TickEventBlueprint.RENDER_TICK.fireEvent(renderTickEventHook);
    }

    @Inject(method = "reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private void nexus$handleResourcePackReloadPreStartEventHook(boolean error, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ClientLifeCycleEvent.ResourcePackReloadPreStartEvent resourcePackReloadPreStartEventHook = new ClientLifeCycleEvent.ResourcePackReloadPreStartEvent(
                reloadStateTracker.reloadState == null ? ResourceLoadStateTracker.ReloadReason.UNKNOWN : reloadStateTracker.reloadState.reloadReason,
                reloadStateTracker.reloadState,
                reloadStateTracker.reloadState == null ? null : reloadStateTracker.reloadState.recoveryReloadInfo,
                reloadStateTracker.reloadCount,
                resourcePackRepository.openAllSelected(),
                reloadStateTracker.reloadState == null || reloadStateTracker.reloadState.recoveryReloadInfo == null ? null : reloadStateTracker.reloadState.recoveryReloadInfo.error,
                pendingReload != null
        );
        EventResult<ClientLifeCycleEvent.ResourcePackReloadPreStartEvent> resourcePackReloadPreStartEventResult = ClientLifeCycleEventBlueprint.RESOURCEPACK_RELOAD_PRE_START.fireEvent(resourcePackReloadPreStartEventHook);

        resourcePackReloadPreStartEventResult.ifCancelled(event -> cir.cancel());
    }

    @Inject(method = {"method_49293", "lambda$reloadResourcePacks$26(ZLjava/lang/Throwable;)V"}, at = @At("TAIL"), remap = false)
    private void nexus$handleResourcePackReloadEndEventHook(boolean error, Throwable p_272301_, CallbackInfo ci) {
        if (error) {
            ClientLifeCycleEvent.ResourcePackReloadEndEvent resourcePackReloadEndEventHook = new ClientLifeCycleEvent.ResourcePackReloadEndEvent(
                    reloadStateTracker.reloadState == null ? ResourceLoadStateTracker.ReloadReason.UNKNOWN : reloadStateTracker.reloadState.reloadReason,
                    reloadStateTracker.reloadState,
                    reloadStateTracker.reloadState == null ? null : reloadStateTracker.reloadState.recoveryReloadInfo,
                    reloadStateTracker.reloadCount,
                    resourcePackRepository.openAllSelected(),
                    p_272301_,
                    true
            );

            ClientLifeCycleEventBlueprint.RESOURCEPACK_RELOAD_END.fireEvent(resourcePackReloadEndEventHook);
        }
    }
}
