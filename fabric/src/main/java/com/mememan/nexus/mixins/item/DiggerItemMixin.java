package com.mememan.nexus.mixins.item;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin {@code class} that adds a small additional hook to allow for deterministic behaviour of blocks mapped to
 * {@linkplain BlockPropertyWrapper BlockPropertyWrappers} to specify custom mining levels. This is lower in priority
 * than each loader's respective tier API checks.
 * <br></br>
 * Only reason this is done on a loader-specific basis is that Forge has its own hook which effectively ignores the
 * original {@link DiggerItem#isCorrectToolForDrops(BlockState)} method we're mixing into here. As such, it wouldn't make
 * sense to have a redundant mixin that applies to both loaders from common to an unused target.
 *
 * @see BlockPropertyWrapper#getMinMiningLevel()
 */
@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin {

    private DiggerItemMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (DiggerItemMixin)");
    }

    @Definition(id = "getLevel", method = "Lnet/minecraft/world/item/Tier;getLevel()I")
    @Definition(id = "i", local = @Local(type = int.class))
    @Definition(id = "getTier", method = "Lnet/minecraft/world/item/DiggerItem;getTier()Lnet/minecraft/world/item/Tier;")
    @Expression("i = this.getTier().getLevel()")
    @Inject(method = "isCorrectToolForDrops", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER), cancellable = true)
    private void nexus$determineCorrectMiningLevelForDrops(BlockState targetState, CallbackInfoReturnable<Boolean> cir, @Local int curMiningLevel) {
        PropertyWrapper.PropertyWrappersContainer.getWrapperFor(targetState.getBlock())
                .map(curPW -> (BlockPropertyWrapper<Block>) curPW)
                .ifPresent(mappedBPW -> {
                    int minMiningLevel = mappedBPW.getMinMiningLevel();

                    if (Math.abs(minMiningLevel) > 0 && curMiningLevel < minMiningLevel) cir.setReturnValue(false);
                });
    }
}
