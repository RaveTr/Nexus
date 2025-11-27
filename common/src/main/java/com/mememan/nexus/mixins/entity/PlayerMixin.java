package com.mememan.nexus.mixins.entity;

import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin {@code class} responsible for firing and handling player tick and misc. event hooks with as little intrusion as
 * possible.
 *
 * @see TickEventBlueprint.PlayerTickEventBlueprint
 * @see TickEvent.PlayerTickEvent
 * @see EntityMixin
 * @see LivingEntityMixin
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    private PlayerMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (PlayerMixin)");
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nexus$handlePrePlayerTickEventHook(CallbackInfo ci) {
        TickEvent.PlayerTickEvent playerTickEventHook = new TickEvent.PlayerTickEvent(TickEvent.Phase.START, (Player) (Object) this);
        EventResult<TickEvent.PlayerTickEvent> playerTickEventResult = TickEventBlueprint.PLAYER_TICK.fireEvent(playerTickEventHook);

        if (playerTickEventResult.cancelled()) ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nexus$handlePostPlayerTickEventHook(CallbackInfo ci) {
        TickEvent.PlayerTickEvent playerTickEventHook = new TickEvent.PlayerTickEvent(TickEvent.Phase.END, (Player) (Object) this);
        TickEventBlueprint.PLAYER_TICK.fireEvent(playerTickEventHook);
    }
}
