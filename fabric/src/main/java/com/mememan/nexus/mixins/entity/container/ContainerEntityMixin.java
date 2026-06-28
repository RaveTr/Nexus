package com.mememan.nexus.mixins.entity.container;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Mixin {@code interface} mainly intended to substitute Vanilla's hardcoded entity reach limit for container entities
 * with Nexus' own hook/fallback entity reach attribute.
 *
 * @see NexusAttributes#ENTITY_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(ContainerEntity.class)
public interface ContainerEntityMixin {

    @ModifyArg(method = "isChestVehicleStillValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z"), index = 1)
    default double nexus$modifyHardcodedVanillaEntityReachRequirement(double distance, @Local(argsOnly = true) Player player) {
        return player.getAttributeValue(NexusAttributes.ENTITY_REACH.get()) + 5.0F;
    }
}
