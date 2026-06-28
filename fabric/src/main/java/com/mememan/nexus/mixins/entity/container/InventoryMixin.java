package com.mememan.nexus.mixins.entity.container;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin {@code class} intended to replace Vanilla's hardcoded entity reach limits for inventories with Nexus' own
 * hook/fallback entity reach attribute.
 *
 * @see NexusAttributes#ENTITY_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    private InventoryMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (InventoryMixin)");
    }

    @Definition(id = "localPlayer", local = @Local(type = Player.class, argsOnly = true))
    @Definition(id = "distanceToSqr", method = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D")
    @Definition(id = "player", field = "Lnet/minecraft/world/entity/player/Inventory;player:Lnet/minecraft/world/entity/player/Player;")
    @Expression("localPlayer.distanceToSqr(this.player) > 64.0")
    @ModifyExpressionValue(method = "stillValid", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyHardcodedVanillaEntityReachRequirement(boolean original, Player player) {
        return player.distanceToSqr(this.player) > Math.pow(player.getAttributeValue(NexusAttributes.ENTITY_REACH.get()) + 5.0D, 2);
    }
}
