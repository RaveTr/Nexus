package com.mememan.nexus.mixins.entity.container;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin {@code class} primarily intended to replace the general {@linkplain ItemCombinerMenu ItemCombinerMenu's} hardcoded
 * block reach distance check(s) with Nexus' own hook/fallback block reach attribute.
 *
 * @see NexusAttributes#BLOCK_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMenuMixin {

    private ItemCombinerMenuMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (ItemCombinerMenuMixin)");
    }

    @Definition(id = "player", local = @Local(type = Player.class, argsOnly = true))
    @Definition(id = "distanceToSqr", method = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(DDD)D")
    @Definition(id = "blockPos", local = @Local(type = BlockPos.class, argsOnly = true))
    @Definition(id = "getX", method = "Lnet/minecraft/core/BlockPos;getX()I")
    @Definition(id = "getY", method = "Lnet/minecraft/core/BlockPos;getY()I")
    @Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
    @Expression("player.distanceToSqr((double) blockPos.getX() + 0.5, (double) blockPos.getY() + 0.5, (double) blockPos.getZ() + 0.5) <= 64.0")
    @ModifyExpressionValue(method = "method_24924", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyHardcodedVanillaBlockReachRequirement(boolean original, @Local(argsOnly = true) Player player, @Local(argsOnly = true) BlockPos targetPos) {
        return player.distanceToSqr(targetPos.getX() + 0.5D, targetPos.getY() + 0.5D, targetPos.getZ() + 0.5D) <= Math.pow(player.getAttributeValue(NexusAttributes.BLOCK_REACH.get()) + 3.5D, 2.0D);
    }
}
