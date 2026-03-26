package com.mememan.nexus.mixins.block;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.template.object.block.vegetation.DefaultableFlowerPotBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin {@code class} that allows for adding custom plants via {@link DefaultableFlowerPotBlock#getFullPots()}.
 * Loader-agnostic (which matters, since Forge, as per usual, coremods the heck out of {@link FlowerPotBlock}).
 *
 * @see FlowerPotBlock
 * @see DefaultableFlowerPotBlock#getFullPots()
 */
@Mixin(FlowerPotBlock.class)
public abstract class FlowerPotBlockMixin {

    private FlowerPotBlockMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (FlowerPotBlockMixin)");
    }

    @ModifyVariable(method = "use", at = @At("STORE"), ordinal = 1)
    private BlockState nexus$addCustomFlowerBlockLookup(BlockState blockstate, @Local(argsOnly = true) Player player) {
        return blockstate.isAir() && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BlockItem heldBlockItem ? DefaultableFlowerPotBlock.getFullPot(heldBlockItem::getBlock).get().defaultBlockState() : blockstate;
    }
}
