package com.mememan.nexus.mixins.item;

import com.mememan.nexus.internal.event.common.NexusForgeCommonMiscEvents;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Mixin {@code class} for properly applying custom hoe tilling behaviour set by BPWs.
 *
 * @see NexusForgeCommonMiscEvents#CACHED_BLOCK_TILLING_BEHAVIOURS
 */
@Mixin(HoeItem.class)
public abstract class HoeItemMixin {

    private HoeItemMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (HoeItemMixin)");
    }

    @ModifyVariable(method = "useOn", at = @At(value = "STORE", ordinal = 0))
    private Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> nexus$remapToolModifierState(Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> originalPair, UseOnContext ctx) {
        Block targetBlock = ctx.getLevel().getBlockState(ctx.getClickedPos()).getBlock();
        Function<Supplier<Block>, it.unimi.dsi.fastutil.Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> tillingBehaviourFunc = NexusForgeCommonMiscEvents.CACHED_BLOCK_TILLING_BEHAVIOURS.get(targetBlock);

        if (tillingBehaviourFunc != null) {
            it.unimi.dsi.fastutil.Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> tillingBehaviourPair = tillingBehaviourFunc.apply(() -> targetBlock);

            if (tillingBehaviourPair != null) {
                Predicate<UseOnContext> ctxTillingPredicate = tillingBehaviourPair.left();
                Consumer<UseOnContext> ctxTillingAction = tillingBehaviourPair.right();

                return ctxTillingPredicate != null && ctxTillingAction != null ? Pair.of(ctxTillingPredicate, ctxTillingAction) : originalPair;
            }
        }

        return originalPair;
    }
}
