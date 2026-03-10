package com.mememan.nexus.template.object.block.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Map;
import java.util.function.Supplier;

public record WoodenBlockGroup(BlockSetType blockSetType, WoodType woodType, Map<ResourceLocation, Supplier<? extends Block>> woodBlockFamily, Map<ResourceLocation, Supplier<? extends Item>> woodItemFamily, Map<ResourceLocation, Supplier<? extends BlockEntityType<? extends BlockEntity>>> woodBlockEntityFamily, Map<ResourceLocation, Supplier<? extends EntityType<? extends Entity>>> woodEntityTypeFamily) {
}
