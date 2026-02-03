package com.mememan.nexus.template.object.block.vegetation;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;

public class DefaultableFlowerPotBlock extends FlowerPotBlock {

    private DefaultableFlowerPotBlock(Block content, Properties properties) {
        super(content, properties);
        throw new UnsupportedOperationException("Attempted to use default constructor for DefaultableFlowerPotBlock. This is not permitted due to the fact that a direct Block reference is passed in as a parameter for the super constructor to consume. Please use any of the other constructors instead.");
    }


}
