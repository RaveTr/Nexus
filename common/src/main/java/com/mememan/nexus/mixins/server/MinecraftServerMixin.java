package com.mememan.nexus.mixins.server;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.server.ServerLifeCycleEventBlueprint;
import com.mememan.nexus.template.event.def.server.ServerLifeCycleEvent;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin {@code class} that fires server-related event hooks with as little intrusion as possible. Handles both server
 * lifecycle and server ticking/misc. events.
 *
 * @see ServerLifeCycleEventBlueprint
 * @see ServerLifeCycleEvent
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    private MinecraftServerMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (MinecraftServerMixin)");
    }

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
}
