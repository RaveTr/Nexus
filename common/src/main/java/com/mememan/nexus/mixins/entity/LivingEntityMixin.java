package com.mememan.nexus.mixins.entity;

import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin {@code class} responsible for firing and handling living entity tick and misc. event hooks with as little intrusion as
 * possible.
 *
 * @see TickEventBlueprint.EntityTickEventBlueprint
 * @see TickEvent.EntityTickEvent
 * @see EntityMixin
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private LivingEntityMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (LivingEntityMixin)");
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nexus$handlePreLivingEntityTickEventHook(CallbackInfo ci) {
        TickEvent.LivingEntityTickEvent livingEntityTickEventHook = new TickEvent.LivingEntityTickEvent(TickEvent.Phase.START, (LivingEntity) (Object) this);
        EventResult<TickEvent.LivingEntityTickEvent> livingEntityTickEventResult = TickEventBlueprint.LIVING_ENTITY_TICK.fireEvent(livingEntityTickEventHook);

        if (livingEntityTickEventResult.cancelled()) ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nexus$handlePostLivingEntityTickEventHook(CallbackInfo ci) {
        TickEvent.LivingEntityTickEvent livingEntityTickEventHook = new TickEvent.LivingEntityTickEvent(TickEvent.Phase.END, (LivingEntity) (Object) this);
        TickEventBlueprint.LIVING_ENTITY_TICK.fireEvent(livingEntityTickEventHook);
    }
}
