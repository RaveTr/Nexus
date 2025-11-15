package com.mememan.nexus.template.object.item.entity.boat;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableBoat;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableBoatType;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableChestBoat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DefaultableBoatItem extends BoatItem {
    protected final BoatType defaultableBoatType;

    public DefaultableBoatItem(boolean hasChest, BoatType defaultableBoatType, Properties properties) {
        super(hasChest, null, properties);

        this.defaultableBoatType = defaultableBoatType;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (Objects.equals(hitResult.getType(), HitResult.Type.BLOCK)) {
            Boat validBoat = getBoat(level, hitResult);

            if (validBoat instanceof DefaultableBoatType validDefaultableBoat) validDefaultableBoat.setBoatType(defaultableBoatType);

            validBoat.setYRot(player.getYRot());

            if (!level.noCollision(validBoat, validBoat.getBoundingBox())) return InteractionResultHolder.fail(heldStack);
            else {
                if (!level.isClientSide) {
                    level.addFreshEntity(validBoat);
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation());

                    if (!player.getAbilities().instabuild) heldStack.shrink(1);
                }

                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(heldStack, level.isClientSide());
            }
        } else return super.use(level, player, hand);
    }

    @Override
    public @NotNull Boat getBoat(Level level, HitResult hitResult) {
        return hasChest
                ? new DefaultableChestBoat(() -> (EntityType<? extends Boat>) BuiltInRegistries.ENTITY_TYPE.getOptional(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(this).withPath("chest_boat")).orElseThrow(), level, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z())
                : new DefaultableBoat(() -> (EntityType<? extends Boat>) BuiltInRegistries.ENTITY_TYPE.getOptional(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(this).withPath("boat")).orElseThrow(), level, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z());
    }

    public BoatType getBoatType() {
        return defaultableBoatType;
    }
}
