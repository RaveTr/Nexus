package com.mememan.nexus.template.object.block.misc;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Set;
import java.util.function.Supplier;

public record WoodenBlockGroup(BlockSetType blockSetType, WoodType woodType, Set<Supplier<? extends Block>> woodBlockFamily) {
}
