package com.mememan.nexus.template.object.entity.misc.vehicle;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.template.object.item.entity.boat.BoatType;
import com.mememan.nexus.util.RegistryUtil;
import com.mememan.nexus.util.StringUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class DefaultableChestBoat extends ChestBoat {
    private static final EntityDataAccessor<String> BOAT_TYPE_ID = SynchedEntityData.defineId(DefaultableChestBoat.class, EntityDataSerializers.STRING);

    public DefaultableChestBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public DefaultableChestBoat(Supplier<EntityType<? extends Boat>> chestBoatEntityType, Level level, double x, double y, double z) {
        this(chestBoatEntityType.get(), level);

        setPos(x, y, z);

        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(BOAT_TYPE_ID, BoatType.getKnownBoatTypes().stream()
                .map(BoatType::getSerializedName)
                .filter(curTypeName -> Objects.equals(curTypeName.substring(0, curTypeName.contains("-") ? curTypeName.indexOf('-') : curTypeName.length()), StringUtil.subLastToken(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(getType()).getNamespace())))
                .findFirst()
                .orElse(Type.OAK.getSerializedName()));
    }

    protected String getBoatTypeId() {
        return this.entityData.get(BOAT_TYPE_ID);
    }

    public Optional<BoatType> getBoatType() {
        return BoatType.getKnownBoatTypes().stream()
                .filter(curBoatType -> Objects.equals(curBoatType.getSerializedName(), getBoatTypeId()))
                .findFirst();
    }

    protected void setBoatTypeId(String id) {
        this.entityData.set(BOAT_TYPE_ID, id);
    }

    public void setBoatType(BoatType targetBoatType) {
        setBoatTypeId(targetBoatType.getSerializedName());
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        this.lastYd = getDeltaMovement().y;

        if (!isPassenger()) {
            if (onGround) {
                if (this.fallDistance > 3.0F) {
                    if (this.status != Boat.Status.ON_LAND) {
                        resetFallDistance();
                        return;
                    }

                    causeFallDamage(fallDistance, 1.0F, damageSources().fall());

                    if (!level().isClientSide && !isRemoved()) {
                        kill();

                        if (level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            for (int plankCount = 0; plankCount < 3; ++plankCount) {
                                spawnAtLocation(getPlanks().get());
                            }

                            for (int stickCount = 0; stickCount < 2; ++stickCount) {
                                spawnAtLocation(Items.STICK);
                            }
                        }
                    }
                }

                resetFallDistance();
            } else if (!level().getFluidState(blockPosition().below()).is(FluidTags.WATER) && y < 0.0D) {
                this.fallDistance -= (float) y;
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("BoatType", CompoundTag.TAG_STRING)) setBoatTypeId(compound.getString("BoatType"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putString("BoatType", getBoatTypeId());
    }

    @Override
    public @NotNull Item getDropItem() {
        return BuiltInRegistries.ITEM.getOptional(
                DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(getType())
                        .withPrefix(new ResourceLocation(getBoatType().map(BoatType::getResourceFriendlyId).orElse(new ResourceLocation(getBoatTypeId())).toString()).getPath().concat("_"))
        ).orElse(super.getDropItem());
    }

    public Supplier<Block> getPlanks() {
        return () -> BuiltInRegistries.BLOCK.getOptional(RegistryUtil.pickSuffix(
                Optional.ofNullable(ResourceLocation.tryParse(getBoatType().map(BoatType::getResourceFriendlyId).orElse(new ResourceLocation(getBoatTypeId())).toString())).orElse(new ResourceLocation(WoodType.OAK.name())),
                "_planks"
        )).orElse(getVariant().getPlanks());
    }
}
