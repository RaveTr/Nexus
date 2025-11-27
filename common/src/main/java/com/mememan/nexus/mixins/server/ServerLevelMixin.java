package com.mememan.nexus.mixins.server;

import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import net.minecraft.server.level.ServerLevel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * Mixin {@code class} responsible for firing and handling server level tick event hooks with as little intrusion as
 * possible.
 *
 * @see TickEventBlueprint.ServerLevelTickEventBlueprint
 * @see TickEvent.ServerLevelTickEvent
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    private ServerLevelMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (ServerLevelMixin)");
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerLevel;handlingTick:Z", opcode = Opcodes.PUTFIELD, ordinal = 0), cancellable = true)
    private void nexus$handlePreServerLevelTickEventHook(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        TickEvent.ServerLevelTickEvent serverLevelTickEventHook = new TickEvent.ServerLevelTickEvent(TickEvent.Phase.START, (ServerLevel) (Object) this, hasTimeLeft);
        EventResult<TickEvent.ServerLevelTickEvent> serverLevelTickEventResult = TickEventBlueprint.SERVER_LEVEL_TICK.fireEvent(serverLevelTickEventHook);

        if (serverLevelTickEventResult.cancelled()) ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nexus$handlePostServerLevelTickEventHook(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        TickEvent.ServerLevelTickEvent serverLevelTickEventHook = new TickEvent.ServerLevelTickEvent(TickEvent.Phase.END, (ServerLevel) (Object) this, hasTimeLeft);
        TickEventBlueprint.SERVER_LEVEL_TICK.fireEvent(serverLevelTickEventHook);
    }
}
