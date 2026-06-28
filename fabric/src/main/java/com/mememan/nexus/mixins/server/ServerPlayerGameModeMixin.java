package com.mememan.nexus.mixins.server;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin {@code class} primarily intended to replace Vanilla's hardcoded block pick distance with Nexus' own
 * hook/fallback block reach attribute.
 *
 * @see NexusAttributes#BLOCK_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    private ServerPlayerGameModeMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (ServerPlayerGameModeMixin)");
    }

    @Definition(id = "player", field = "Lnet/minecraft/server/level/ServerPlayerGameMode;player:Lnet/minecraft/server/level/ServerPlayer;")
    @Definition(id = "getEyePosition", method = "Lnet/minecraft/server/level/ServerPlayer;getEyePosition()Lnet/minecraft/world/phys/Vec3;")
    @Definition(id = "distanceToSqr", method = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")
    @Definition(id = "atCenterOf", method = "Lnet/minecraft/world/phys/Vec3;atCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;")
    @Definition(id = "pos", local = @Local(type = BlockPos.class, argsOnly = true))
    @Definition(id = "MAX_INTERACTION_DISTANCE", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;MAX_INTERACTION_DISTANCE:D")
    @Expression("this.player.getEyePosition().distanceToSqr(atCenterOf(pos)) > MAX_INTERACTION_DISTANCE")
    @ModifyExpressionValue(method = "handleBlockBreakAction", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyHardcodedVanillaBlockReachRequirement(boolean original, BlockPos targetPos) {
        return player.getEyePosition().distanceToSqr(Vec3.atCenterOf(targetPos)) > Math.pow(player.getAttributeValue(NexusAttributes.BLOCK_REACH.get()) + 1.5D, 2.0D);
    }
}
