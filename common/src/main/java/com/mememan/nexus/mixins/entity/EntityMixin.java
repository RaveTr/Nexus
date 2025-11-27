package com.mememan.nexus.mixins.entity;

import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin {@code class} responsible for firing and handling base entity tick and misc. event hooks with as little intrusion as
 * possible.
 *
 * @see TickEventBlueprint.EntityTickEventBlueprint
 * @see TickEvent.EntityTickEvent
 * @see LivingEntityMixin
 * @see PlayerMixin
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    private EntityMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (EntityMixin)");
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nexus$handlePreEntityTickEventHook(CallbackInfo ci) {
        TickEvent.EntityTickEvent entityTickEventHook = new TickEvent.EntityTickEvent(TickEvent.Phase.START, (Entity) (Object) this);
        EventResult<TickEvent.EntityTickEvent> entityTickEventResult = TickEventBlueprint.ENTITY_TICK.fireEvent(entityTickEventHook);

        if (entityTickEventResult.cancelled()) ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nexus$handlePostEntityTickEventHook(CallbackInfo ci) {
        TickEvent.EntityTickEvent entityTickEventHook = new TickEvent.EntityTickEvent(TickEvent.Phase.END, (Entity) (Object) this);
        TickEventBlueprint.ENTITY_TICK.fireEvent(entityTickEventHook);
    }
}
