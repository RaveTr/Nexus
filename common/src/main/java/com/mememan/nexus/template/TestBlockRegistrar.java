package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.template.property_wrapper.BlockPropertyWrapperTemplates;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Locale;
import java.util.function.Supplier;

@RegistrarEntry
public class TestBlockRegistrar {
    private static final ObjectArrayList<Supplier<Block>> BLOCKS = new ObjectArrayList<>();

    public static final Supplier<SlabBlock> BLAH = new BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_block"), () -> new SlabBlock(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .copyFromType(BlockPropertyWrapperTemplates.BASIC)
            .withRecipe(r -> result -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get())
                    .requires(Items.ACACIA_BOAT)
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(Items.ACACIA_BOAT).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(Items.ACACIA_BOAT))
                    .save(r, new ResourceLocation("nexus", result.get().getDescriptionId().substring(result.get().getDescriptionId().lastIndexOf(".") + 1))))
            .asCompostable(parentBlock -> 45.0F)
            .asFuel(parentBlock -> 12000)
            .withBlockTilling(parentBlock -> Pair.of((ctx) -> true, (ctx) -> ctx.getLevel().setBlock(ctx.getClickedPos(), Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL)))
            .withParentTab(() -> CreativeModeTabs.allTabs().get(3))
            .withTag(() -> BlockTags.MINEABLE_WITH_AXE)
            .buildAndGet();

    public static final Supplier<SlabBlock> BLAH_2 = new BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_able_block"), () -> new SlabBlock(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .copyFrom(BLAH)
            .literalTranslation()
            .withLocalization(v -> v.toUpperCase(Locale.ROOT))
            .buildAndGet();

    public static final Supplier<Block> BLAH_3 = BlockPropertyWrapperTemplates.registerAndChain(NexusConstants.prefix("test_block_3"), () -> new Block(BlockBehaviour.Properties.of()), BlockPropertyWrapperTemplates.BASIC, BLOCKS)
            .withTags(TestBlockTags.TEST::get, () -> BlockTags.MINEABLE_WITH_AXE)
            .withBlockFlattening(parentBlock -> BLAH.get().defaultBlockState())
            .asCompostable(parentBlock -> 45.0F)
            .asFuel(parentBlock -> 12000)
            .buildAndGet();

    private static <B extends Block> Supplier<B> registerBlock(ResourceLocation name, Supplier<B> block) {
        Supplier<B> registeredItem = NexusServices.REGISTRAR.registerObject(name, block, BuiltInRegistries.BLOCK);
        NexusServices.REGISTRAR.registerObject(name, () -> new BlockItem(registeredItem.get(), new Item.Properties()), BuiltInRegistries.ITEM);
        BLOCKS.add((Supplier<Block>) registeredItem);
        return registeredItem;
    }
}
