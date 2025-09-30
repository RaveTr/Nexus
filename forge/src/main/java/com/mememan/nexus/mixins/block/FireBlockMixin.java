package com.mememan.nexus.mixins.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mememan.nexus.internal.event.common.NexusForgeCommonMiscEvents;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin {@code class} whose purpose is to safely delegate both ignite and burn odd getter methods to Nexus' own
 * updated collections when possible. This is mainly to prevent stale references to tagged objects that may have been
 * removed from their parent tags, but are still managed by Nexus.
 * <br></br>
 * Such updates happen on each server reload (cuz that's when tags are updated, duh).
 *
 * @see NexusForgeCommonMiscEvents
 */
@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    private FireBlockMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (FireBlockMixin)");
    }

    @ModifyReturnValue(method = "getIgniteOdds(Lnet/minecraft/world/level/block/state/BlockState;)I", at = @At("RETURN"))
    private int nexus$getDelegateIgniteOdds(int originalIgniteOdds, BlockState targetState) {
        return NexusForgeCommonMiscEvents.CACHED_FLAMMABILITY_BY_TAG.getOrDefault(targetState.getBlock(), IntIntMutablePair.of(originalIgniteOdds, 0)).leftInt();
    }

    @ModifyReturnValue(method = "getBurnOdds(Lnet/minecraft/world/level/block/state/BlockState;)I", at = @At("RETURN"))
    private int nexus$getDelegateBurnOdds(int originalBurnOdds, BlockState targetState) {
        return NexusForgeCommonMiscEvents.CACHED_FLAMMABILITY_BY_TAG.getOrDefault(targetState.getBlock(), IntIntMutablePair.of(0, originalBurnOdds)).rightInt();
    }
}
