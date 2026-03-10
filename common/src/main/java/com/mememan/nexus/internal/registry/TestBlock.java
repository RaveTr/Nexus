package com.mememan.nexus.internal.registry;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.template.property_wrapper.BlockPropertyWrapperTemplates;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

@RegistrarEntry
public class TestBlock {

    public static final Supplier<Block> TEST_BLOCK = BlockPropertyWrapperTemplates.registerBlockWithItem(NexusConstants.prefix("test_block"), () -> new Block(BlockBehaviour.Properties.of()));
    public static final Supplier<Block> TEST_BLOCK_2 = BlockPropertyWrapperTemplates.registerBlockWithItem(NexusConstants.prefix("test_block_2"), () -> new Block(BlockBehaviour.Properties.of()));

}
