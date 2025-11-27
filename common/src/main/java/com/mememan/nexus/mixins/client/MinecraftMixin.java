package com.mememan.nexus.mixins.client;

import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin {@code class} responsible for firing and handling client tick event hooks with as little intrusion as
 * possible.
 *
 * @see TickEventBlueprint.ClientTickEventBlueprint
 * @see TickEvent.ClientTickEvent
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

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
}
