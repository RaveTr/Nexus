package com.mememan.nexus.mixins.item;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin {@code class} primarily intended to replace the hardcoded Vanilla block reach distance check for item hit results
 * with Nexus' own hook/fallback block reach attribute.
 *
 * @see NexusAttributes#BLOCK_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(Item.class)
public abstract class ItemMixin {

    private ItemMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (ItemMixin)");
    }

    @ModifyVariable(method = "getPlayerPOVHitResult", at = @At("STORE"), ordinal = 0)
    private static double nexus$modifyReachDistance(double baseReach, @Local(argsOnly = true) Player player) {
        return player.getAttributeValue(NexusAttributes.BLOCK_REACH.get()) + 0.5D;
    }
}
