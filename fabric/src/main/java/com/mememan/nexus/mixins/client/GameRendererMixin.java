package com.mememan.nexus.mixins.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin {@code class} primarily intended to replace Vanilla's hardcoded block pick distance with Nexus' own
 * hook/fallback block reach attribute.
 *
 * @see NexusAttributes#BLOCK_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    private GameRendererMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (GameRendererMixin)");
    }

    @ModifyArg(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;", ordinal = 0))
    private double nexus$modifyHardcodedVanillaEntityReachPickRequirement(double hitDistance) {
        if (minecraft.player == null) return hitDistance;

        return Math.max(hitDistance, minecraft.player.getAttributeValue(NexusAttributes.ENTITY_REACH.get()) + (minecraft.player.isCreative() ? 3.0D : 0.0D));
    }

    @Definition(id = "e", local = @Local(type = double.class, ordinal = 1))
    @Expression("e = e * e")
    @Inject(method = "pick", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.BEFORE))
    private void nexus$modifyHardcodedVanillaEntityReachRequirement(float partialTicks, CallbackInfo ci, @Local(ordinal = 0) LocalDoubleRef d0, @Local(ordinal = 1) LocalDoubleRef d1) {
        if (minecraft.player == null) return;

        d1.set(Math.max(d0.get(), minecraft.player.getAttributeValue(NexusAttributes.ENTITY_REACH.get()) + (minecraft.player.isCreative() ? 3.0D : 0.0D)));
        d0.set(d1.get());
    }

    @Definition(id = "g", local = @Local(type = double.class, ordinal = 2))
    @Expression("g > 9.0")
    @ModifyExpressionValue(method = "pick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyHardcodedVanillaEntityReachPostPickRequirement(boolean original, @Local(ordinal = 2) double g) {
        if (minecraft.player == null) return original;

        return g > Math.pow(minecraft.player.getAttributeValue(NexusAttributes.ENTITY_REACH.get()) + (minecraft.player.isCreative() ? 3.0D : 0.0D), 2.0D);
    }
}
