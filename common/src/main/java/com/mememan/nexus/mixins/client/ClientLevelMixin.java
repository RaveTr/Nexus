package com.mememan.nexus.mixins.client;

import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * Mixin {@code class} responsible for firing and handling client level tick event hooks with as little intrusion as
 * possible.
 *
 * @see TickEventBlueprint.ClientLevelTickEventBlueprint
 * @see TickEvent.ClientLevelTickEvent
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    private ClientLevelMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (ClientLevelMixin)");
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nexus$handlePreClientLevelTickEventHook(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        TickEvent.ClientLevelTickEvent clientLevelTickEventHook = new TickEvent.ClientLevelTickEvent(TickEvent.Phase.START, (ClientLevel) (Object) this);
        EventResult<TickEvent.ClientLevelTickEvent> clientLevelTickEventResult = TickEventBlueprint.CLIENT_LEVEL_TICK.fireEvent(clientLevelTickEventHook);

        if (clientLevelTickEventResult.cancelled()) ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nexus$handlePostClientLevelTickEventHook(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        TickEvent.ClientLevelTickEvent clientLevelTickEventHook = new TickEvent.ClientLevelTickEvent(TickEvent.Phase.END, (ClientLevel) (Object) this);
        TickEventBlueprint.CLIENT_LEVEL_TICK.fireEvent(clientLevelTickEventHook);
    }
}
