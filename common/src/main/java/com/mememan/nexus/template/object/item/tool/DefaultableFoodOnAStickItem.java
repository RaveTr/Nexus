package com.mememan.nexus.template.object.item.tool;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FoodOnAStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DefaultableFoodOnAStickItem<EIS extends Entity & ItemSteerable> extends FoodOnAStickItem<EIS> {
    protected final Supplier<EntityType<EIS>> interactableEntityType;

    public DefaultableFoodOnAStickItem(Properties properties, Supplier<EntityType<EIS>> interactableEntityType, int consumeItemDamage) {
        super(properties, null, consumeItemDamage);
        
        this.interactableEntityType = interactableEntityType;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            Entity curVehicle = player.getControlledVehicle();

            if (player.isPassenger() && curVehicle instanceof ItemSteerable steerableVehicleEntity) {
                if (curVehicle.getType() == interactableEntityType.get() && steerableVehicleEntity.boost()) {
                    heldStack.hurtAndBreak(consumeItemDamage, player, (ownerPlayer) -> ownerPlayer.broadcastBreakEvent(hand));

                    if (heldStack.isEmpty()) {
                        ItemStack fishingRodStack = Items.FISHING_ROD.getDefaultInstance();

                        fishingRodStack.setTag(heldStack.getTag());
                        return InteractionResultHolder.success(fishingRodStack);
                    }

                    return InteractionResultHolder.success(heldStack);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.pass(heldStack);
        }

        return InteractionResultHolder.pass(heldStack);
    }
}
