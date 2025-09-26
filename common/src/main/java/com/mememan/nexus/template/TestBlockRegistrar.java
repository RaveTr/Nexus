package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.template.property_wrapper.BlockPropertyWrapperTemplates;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Locale;
import java.util.function.Supplier;

@RegistrarEntry
public class TestBlockRegistrar {
    private static final ObjectArrayList<Supplier<Block>> BLOCKS = new ObjectArrayList<>();

    public static final Supplier<Block> BLAH = new BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_block"), () -> new Block(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .copyFromType(BlockPropertyWrapperTemplates.BASIC)
            .asCompostable(parentBlock -> 45.0F)
            .asFuel(parentBlock -> 12000)
            .withBlockTilling(parentBlock -> Pair.of((ctx) -> true, (ctx) -> ctx.getLevel().setBlock(ctx.getClickedPos(), Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL)))
            .withParentTab(() -> CreativeModeTabs.allTabs().get(3))
            .withTag(() -> BlockTags.MINEABLE_WITH_AXE)
            .buildAndGet();

    public static final Supplier<Block> BLAH_SLAB = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("test_block_slab"), () -> new SlabBlock(BlockBehaviour.Properties.of()), BlockPropertyWrapperTemplates.SLAB);
    public static final Supplier<Block> BLAH_STAIRS = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("test_stairs"), () -> new StairBlock(BLAH.get().defaultBlockState(), BlockBehaviour.Properties.copy(BLAH.get())), BlockPropertyWrapperTemplates.STAIRS);
    public static final Supplier<Block> BLAH_WALL = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("test_block_wall"), () -> new WallBlock(BlockBehaviour.Properties.copy(BLAH.get())), BlockPropertyWrapperTemplates.WALL);
    public static final Supplier<Block> BLAH_BUTTON = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("test_button"), () -> new ButtonBlock(BlockBehaviour.Properties.copy(BLAH.get()).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), BlockSetType.STONE, 20, false), BlockPropertyWrapperTemplates.BUTTON);
    public static final Supplier<Block> BLAH_PRESSURE_PLATE = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("test_block_pressure_plate"), () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, BlockBehaviour.Properties.copy(BLAH.get()).mapColor(MapColor.STONE).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), BlockSetType.STONE), BlockPropertyWrapperTemplates.PRESSURE_PLATE);
    public static final Supplier<Block> BLAH_DOOR = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(NexusConstants.prefix("test_door"), () -> new DoorBlock(BlockBehaviour.Properties.of().mapColor(BLAH.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(5.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY), BlockSetType.STONE), BlockPropertyWrapperTemplates.DOOR);

    public static final Supplier<SlabBlock> BLAH_2 = new BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_able_block"), () -> new SlabBlock(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .literalTranslation()
            .withLocalization(v -> v.toUpperCase(Locale.ROOT))
            .buildAndGet();

    public static final Supplier<Block> BLAH_3 = BlockPropertyWrapperTemplates.registerWithItemAndChain(NexusConstants.prefix("test_block_3"), () -> new Block(BlockBehaviour.Properties.of()), BlockPropertyWrapperTemplates.BASIC)
            .withTags(TestBlockTags.TEST::get, () -> BlockTags.MINEABLE_WITH_AXE)
            .withBlockFlattening(parentBlock -> BLAH.get().defaultBlockState())
            .asCompostable(parentBlock -> 45.0F)
            .asFuel(parentBlock -> 12000)
            .withBlockTilling(parentBlock -> Pair.of((ctx) -> true, (ctx) -> ctx.getLevel().setBlock(ctx.getClickedPos(), Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL)))
            .withParentTabs(() -> CreativeModeTabs.allTabs().get(3), () -> CreativeModeTabs.allTabs().get(2))
            .buildAndGet();

    private static <B extends Block> Supplier<B> registerBlock(ResourceLocation name, Supplier<B> block) {
        Supplier<B> registeredItem = NexusServices.REGISTRAR.registerObject(name, block, BuiltInRegistries.BLOCK);
        NexusServices.REGISTRAR.registerObject(name, () -> new BlockItem(registeredItem.get(), new Item.Properties()), BuiltInRegistries.ITEM);
        BLOCKS.add((Supplier<Block>) registeredItem);
        return registeredItem;
    }
}
