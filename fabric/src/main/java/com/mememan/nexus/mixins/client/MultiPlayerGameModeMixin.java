package com.mememan.nexus.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
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
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private GameType localPlayerMode;

    private MultiPlayerGameModeMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (MultiPlayerGameModeMixin)");
    }

    @ModifyReturnValue(method = "getPickRange", at = @At("RETURN"))
    private float nexus$modifyHardcodedVanillaBlockReachRequirement(float original) {
        if (minecraft.player == null) return original;

        return (float) minecraft.player.getAttributeValue(NexusAttributes.BLOCK_REACH.get()) + (localPlayerMode.isCreative() ? 0.5F : 0.0F);
    }
}
