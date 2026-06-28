package com.mememan.nexus.mixins.entity.container;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin {@code class} that mainly adds compatibility with Nexus' fallback entity reach attribute/hook.
 *
 * @see NexusAttributes
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin {
    @Shadow
    @Final
    private AbstractHorse horse;

    private HorseInventoryMenuMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (HorseInventoryMenuMixin)");
    }

    @Definition(id = "horse", field = "Lnet/minecraft/world/inventory/HorseInventoryMenu;horse:Lnet/minecraft/world/entity/animal/horse/AbstractHorse;")
    @Definition(id = "distanceTo", method = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;distanceTo(Lnet/minecraft/world/entity/Entity;)F")
    @Definition(id = "player", local = @Local(type = Player.class, argsOnly = true))
    @Expression("this.horse.distanceTo(player) < 8.0")
    @ModifyExpressionValue(method = "stillValid", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyHardcodedVanillaEntityReachRequirement(boolean original, Player player) {
        return horse.distanceTo(player) < player.getAttributeValue(NexusAttributes.ENTITY_REACH.get()) + 5.0F;
    }
}
