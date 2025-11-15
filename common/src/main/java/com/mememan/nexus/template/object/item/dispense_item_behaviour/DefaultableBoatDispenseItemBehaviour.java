package com.mememan.nexus.template.object.item.dispense_item_behaviour;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableBoat;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableBoatType;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableChestBoat;
import com.mememan.nexus.template.object.item.entity.boat.BoatType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DefaultableBoatDispenseItemBehaviour extends DefaultDispenseItemBehavior {
    protected final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();
    protected final String baseBoatTypePath;
    protected final BoatType targetBoatType;
    protected final boolean isChestBoat;

    public DefaultableBoatDispenseItemBehaviour(BoatType targetBoatType, boolean isChestBoat) {
        this.baseBoatTypePath = targetBoatType.isRaft() ? (isChestBoat ? "chest_raft" : "raft") : (isChestBoat ? "chest_boat" : "boat");
        this.targetBoatType = targetBoatType;
        this.isChestBoat = isChestBoat;
    }

    public DefaultableBoatDispenseItemBehaviour(BoatType targetBoatType) {
        this(targetBoatType, false);
    }

    @Override
    protected @NotNull ItemStack execute(BlockSource source, ItemStack stack) {
        Direction dispenseDir = source.getBlockState().getValue(DispenserBlock.FACING);
        Level curLevel = source.getLevel();

        double boatSizeOffsetFactor = 0.5625D + EntityType.BOAT.getWidth() / 2.0D;
        double targetX = source.x() + dispenseDir.getStepX() * boatSizeOffsetFactor;
        double targetY = source.y() + dispenseDir.getStepY() * 1.125D;
        double targetZ = source.z() + dispenseDir.getStepZ() * boatSizeOffsetFactor;

        BlockPos adjacentPos = source.getPos().relative(dispenseDir);

        double targetYOffset;

        if (curLevel.getFluidState(adjacentPos).is(FluidTags.WATER)) targetYOffset = 1.0F;
        else {
            if (!curLevel.getBlockState(adjacentPos).isAir() || !curLevel.getFluidState(adjacentPos.below()).is(FluidTags.WATER)) {
                return defaultBehaviour.dispense(source, stack);
            }

            targetYOffset = 0.0F;
        }

        Supplier<EntityType<? extends Boat>> targetBoatEntityType = BuiltInRegistries.ENTITY_TYPE.getOptional(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(stack.getItem()).withPath(baseBoatTypePath))
                .or(() -> BuiltInRegistries.ENTITY_TYPE.getOptional(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(stack.getItem())))
                .map(targetType -> (Supplier<EntityType<? extends Boat>>) () -> (EntityType<Boat>) targetType)
                .orElse(() -> isChestBoat ? EntityType.CHEST_BOAT : EntityType.BOAT);

        Boat targetBoat = isChestBoat ? new DefaultableChestBoat(targetBoatEntityType, curLevel, targetX, targetY + targetYOffset, targetZ) : new DefaultableBoat(targetBoatEntityType, curLevel, targetX, targetY + targetYOffset, targetZ);

        if (targetBoat instanceof DefaultableBoatType defaultableBoat) defaultableBoat.setBoatType(targetBoatType);

        targetBoat.setYRot(dispenseDir.toYRot());

        curLevel.addFreshEntity(targetBoat);

        stack.shrink(1);

        return stack;
    }

    @Override
    protected void playSound(BlockSource source) {
        source.getLevel().levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, source.getPos(), 0);
    }
}
