package com.mememan.nexus.mixins.server;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.blueprint.server.ServerLifeCycleEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import com.mememan.nexus.template.event.def.server.ServerLifeCycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

/**
 * Mixin {@code class} that fires server-related event hooks with as little intrusion as possible. Handles both server
 * lifecycle and server ticking events.
 *
 * @see ServerLifeCycleEventBlueprint
 * @see ServerLifeCycleEvent
 * @see TickEventBlueprint.ServerTickEventBlueprint
 * @see TickEvent.ServerTickEvent
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    private MinecraftServerMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (MinecraftServerMixin)");
    }

    @Shadow
    public abstract ResourceManager getResourceManager();

    @Definition(id = "initServer", method = "Lnet/minecraft/server/MinecraftServer;initServer()Z")
    @Expression("this.initServer()")
    @ModifyExpressionValue(method = "runServer", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$handleServerStartingEventHook(boolean original) {
        ServerLifeCycleEvent.ServerStartingEvent serverStartingEventHook = new ServerLifeCycleEvent.ServerStartingEvent((MinecraftServer) (Object) this);
        EventResult<ServerLifeCycleEvent.ServerStartingEvent> serverStartingEventResult = ServerLifeCycleEventBlueprint.SERVER_STARTING.fireEvent(serverStartingEventHook);

        return original && !serverStartingEventResult.cancelled();
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;", shift = At.Shift.AFTER))
    private void nexus$handleServerStartEventHook(CallbackInfo ci) {
        ServerLifeCycleEvent.ServerStartedEvent serverStartedEventHook = new ServerLifeCycleEvent.ServerStartedEvent((MinecraftServer) (Object) this);
        ServerLifeCycleEventBlueprint.SERVER_STARTED.fireEvent(serverStartedEventHook);
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void nexus$handleServerStoppingEventHook(CallbackInfo ci) {
        ServerLifeCycleEvent.ServerStoppingEvent serverStoppingEventHook = new ServerLifeCycleEvent.ServerStoppingEvent((MinecraftServer) (Object) this);
        ServerLifeCycleEventBlueprint.SERVER_STOPPING.fireEvent(serverStoppingEventHook);
    }

    @Inject(method = "stopServer", at = @At("TAIL"))
    private void nexus$handleServerStopEventHook(CallbackInfo ci) {
        ServerLifeCycleEvent.ServerStoppedEvent serverStoppedEventHook = new ServerLifeCycleEvent.ServerStoppedEvent((MinecraftServer) (Object) this);
        ServerLifeCycleEventBlueprint.SERVER_STOPPED.fireEvent(serverStoppedEventHook);
    }

    @Inject(method = "tickServer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/Util;getNanos()J", ordinal = 0), cancellable = true)
    private void nexus$handlePreServerTickEventHook(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        TickEvent.ServerTickEvent serverTickEventHook = new TickEvent.ServerTickEvent(TickEvent.Phase.START, (MinecraftServer) (Object) this, hasTimeLeft);
        EventResult<TickEvent.ServerTickEvent> serverTickEventEventResult = TickEventBlueprint.SERVER_TICK.fireEvent(serverTickEventHook);

        if (serverTickEventEventResult.cancelled()) ci.cancel();
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void nexus$handlePostServerTickEventHook(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        TickEvent.ServerTickEvent serverTickEventHook = new TickEvent.ServerTickEvent(TickEvent.Phase.END, (MinecraftServer) (Object) this, hasTimeLeft);
        TickEventBlueprint.SERVER_TICK.fireEvent(serverTickEventHook);
    }

    @Inject(method = "reloadResources", at = @At("HEAD"), cancellable = true)
    private void nexus$handleDataPackReloadStartEventHook(Collection<String> selectedIds, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ServerLifeCycleEvent.DataPackReloadStartEvent dataPackReloadStartEventHook = new ServerLifeCycleEvent.DataPackReloadStartEvent((MinecraftServer) (Object) this, getResourceManager());
        EventResult<ServerLifeCycleEvent.DataPackReloadStartEvent> dataPackReloadStartEventResult = ServerLifeCycleEventBlueprint.DATAPACK_RELOAD_START.fireEvent(dataPackReloadStartEventHook);

        if (dataPackReloadStartEventResult.cancelled()) cir.setReturnValue(CompletableFuture.allOf());
    }

    @Inject(method = "reloadResources", at = @At("TAIL"), cancellable = true)
    private void nexus$handleDataPackReloadEndEventHook(Collection<String> selectedIds, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().handleAsync((value, throwable) -> {
            ServerLifeCycleEvent.DataPackReloadEndEvent dataPackReloadEndEventHook = new ServerLifeCycleEvent.DataPackReloadEndEvent((MinecraftServer) (Object) this, getResourceManager(), throwable == null);
            ServerLifeCycleEventBlueprint.DATAPACK_RELOAD_END.fireEvent(dataPackReloadEndEventHook);

            return value;
        }, (MinecraftServer) (Object) this);
    }
}
