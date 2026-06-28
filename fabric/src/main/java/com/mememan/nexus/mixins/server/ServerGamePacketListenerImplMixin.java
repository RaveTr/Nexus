package com.mememan.nexus.mixins.server;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin {@code class} that modifies the reach distance check on the server when an attack interaction packet is
 * received and dispatched, replacing it with Nexus' own hooks/fallback entity reach attribute.
 *
 * @see NexusAttributes#ENTITY_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    private ServerGamePacketListenerImplMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (ServerGamePacketListenerImplMixin)");
    }

    @Definition(id = "aABB", local = @Local(type = AABB.class))
    @Definition(id = "distanceToSqr", method = "Lnet/minecraft/world/phys/AABB;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")
    @Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
    @Definition(id = "getEyePosition", method = "Lnet/minecraft/server/level/ServerPlayer;getEyePosition()Lnet/minecraft/world/phys/Vec3;")
    @Definition(id = "MAX_INTERACTION_DISTANCE", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;MAX_INTERACTION_DISTANCE:D")
    @Expression("aABB.distanceToSqr(this.player.getEyePosition()) < MAX_INTERACTION_DISTANCE")
    @ModifyExpressionValue(method = "handleInteract", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyHardcodedVanillaEntityReachRequirement(boolean original, @Local(ordinal = 0) AABB targetEntityAABB) {
        return targetEntityAABB.distanceToSqr(player.getEyePosition()) < Math.pow(player.getAttributeValue(NexusAttributes.ENTITY_REACH.get()) + 3.0D, 2.0D);
    }

    @Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
    @Definition(id = "getEyePosition", method = "Lnet/minecraft/server/level/ServerPlayer;getEyePosition()Lnet/minecraft/world/phys/Vec3;")
    @Definition(id = "distanceToSqr", method = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")
    @Definition(id = "vec32", local = @Local(type = Vec3.class, ordinal = 1))
    @Definition(id = "MAX_INTERACTION_DISTANCE", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;MAX_INTERACTION_DISTANCE:D")
    @Expression("this.player.getEyePosition().distanceToSqr(vec32) > MAX_INTERACTION_DISTANCE")
    @ModifyExpressionValue(method = "handleUseItemOn", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyHardcodedVanillaBlockReachRequirement(boolean original, @Local(ordinal = 1) Vec3 targetPickPos) {
        return player.getEyePosition().distanceToSqr(targetPickPos) > Math.pow(player.getAttributeValue(NexusAttributes.BLOCK_REACH.get()) + 1.5D, 2.0D);
    }
}
