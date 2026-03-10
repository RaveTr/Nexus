package com.mememan.nexus.template.object.block.misc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Map;
import java.util.function.Supplier;

public record StoneBlockGroup(BlockSetType stoneBlockSetType, Map<ResourceLocation, Supplier<? extends Block>> stoneBlockFamily) {

    public StoneBlockGroup chain(StoneBlockGroup other) {
        Map<ResourceLocation, Supplier<? extends Block>> newStoneBlockFamily = new Object2ObjectOpenHashMap<>(this.stoneBlockFamily);

        newStoneBlockFamily.putAll(other.stoneBlockFamily);

        return new StoneBlockGroup(stoneBlockSetType, newStoneBlockFamily);
    }
}
