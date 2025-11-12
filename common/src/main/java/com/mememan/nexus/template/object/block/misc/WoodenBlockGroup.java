package com.mememan.nexus.template.object.block.misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Set;
import java.util.function.Supplier;

public record WoodenBlockGroup(BlockSetType blockSetType, WoodType woodType, Set<Supplier<? extends Block>> woodBlockFamily, Set<Supplier<? extends Item>> woodItemFamily, Set<Supplier<? extends BlockEntityType<? extends BlockEntity>>> woodBlockEntityFamily, Set<Supplier<? extends EntityType<? extends Entity>>> woodEntityTypeFamily) {
}
