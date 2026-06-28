package com.mememan.nexus.mixins.entity.container;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Mixin {@code interface} mainly intended to substitute Vanilla's hardcoded block reach limit for containers with Nexus'
 * own hook/fallback block reach attribute.
 *
 * @see NexusAttributes#BLOCK_REACH
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(Container.class)
public interface ContainerMixin {

    @ModifyArg(method = "stillValidBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/player/Player;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;stillValidBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/player/Player;I)Z"), index = 2)
    private static int nexus$modifyHardcodedVanillaBlockReachRequirement(int maxDistance, @Local(argsOnly = true) Player player) {
        return (int) (player.getAttributeValue(NexusAttributes.BLOCK_REACH.get()) + 3.5D);
    }
}
