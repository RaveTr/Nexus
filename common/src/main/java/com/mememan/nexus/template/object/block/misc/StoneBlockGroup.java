package com.mememan.nexus.template.object.block.misc;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Set;
import java.util.function.Supplier;

public record StoneBlockGroup(BlockSetType stoneBlockSetType, Set<Supplier<? extends Block>> stoneBlockFamily) {

    public StoneBlockGroup chain(StoneBlockGroup other) {
        Set<Supplier<? extends Block>> newStoneBlockFamily = new ObjectOpenHashSet<>(this.stoneBlockFamily);

        newStoneBlockFamily.addAll(other.stoneBlockFamily);

        return new StoneBlockGroup(stoneBlockSetType, newStoneBlockFamily);
    }
}
