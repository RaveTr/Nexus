package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.DatagenRegistrarEntry;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.template.object.block.vegetation.DefaultableFlowerBlock;
import com.mememan.nexus.template.property_wrapper.BlockPropertyWrapperTemplates;
import com.mememan.nexus.template.property_wrapper.ItemPropertyWrapperTemplates;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

@RegistrarEntry
public class TestBlockRegistrar {

    public static final Supplier<DefaultableFlowerBlock> TEST_FLOWER = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("test_flower"), () -> new DefaultableFlowerBlock(BlockBehaviour.Properties.copy(Blocks.ALLIUM)), BlockPropertyWrapperTemplates.FLOWER);
    public static final Supplier<FlowerPotBlock> POTTED_TEST_FLOWER = BlockPropertyWrapperTemplates.registerBlockFromTemplate(NexusConstants.prefix("potted_test_flower"), () -> new FlowerPotBlock(TEST_FLOWER.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)), BlockPropertyWrapperTemplates.FLOWER_POT);

    public static final Supplier<Item> TEST_FOOD = ItemPropertyWrapperTemplates.registerItemFromTemplate(NexusConstants.prefix("test_food"), () -> new Item(new Item.Properties().food(Foods.CHICKEN)), ItemPropertyWrapperTemplates.BASIC_GENERATED);
    public static final Supplier<Item> COOKED_TEST_FOOD = ItemPropertyWrapperTemplates.registerItemFromTemplate(NexusConstants.prefix("cooked_test_food"), () -> new Item(new Item.Properties().food(Foods.COOKED_CHICKEN)), ItemPropertyWrapperTemplates.COOKED_FOOD);

    @DatagenRegistrarEntry
    public static class Data {
        public static final ModDatagenConfig CONFIG = NexusServices.DATA_GENERATOR.registerConfigForMod(ModDatagenConfig.defaultConfig(NexusConstants.MOD_ID));
    }
}
