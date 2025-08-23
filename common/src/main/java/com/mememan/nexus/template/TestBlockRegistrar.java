package com.mememan.nexus.template;

import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.platform.NexusServices;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;

import java.util.function.Supplier;

@RegistrarEntry
public class TestBlockRegistrar {
    private static final ObjectArrayList<Supplier<? extends Block>> BLOCKS = new ObjectArrayList<>();

    public static final Supplier<Block> TEST_ITEM_2 = BlockPropertyWrapper.create(registerBlock(new ResourceLocation("nexus", "test_block"), () -> new Block(BlockBehaviour.Properties.of())))
            .builder()
            .asCompostable(I -> 20.0F)
            .asFuel(I -> 200)
            .withParentCreativeModeTab(() -> CreativeModeTabs.allTabs().get(3))
            .withTag(() -> ItemTags.ACACIA_LOGS)
            .withRecipe(r -> result -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get())
                    .requires(Items.ACACIA_BOAT)
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(Items.ACACIA_BOAT).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(Items.ACACIA_BOAT))
                    .save(r, new ResourceLocation("nexus", "test_block")))
            .withLootTable(parent -> LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(Items.ACACIA_BOAT))))
            .build()
            .getParentBlock();

    public static final Supplier<SlabBlock> BLAH = new com.mememan.nexus.property_wrapper.BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_block"), () -> new SlabBlock(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .literalTranslation()
            .excludeFromNativeDatagen()
            .withTag(BlockTags.ACACIA_LOGS)
            .withRecipe(r -> result -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get())
                    .requires(Items.ACACIA_BOAT)
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(Items.ACACIA_BOAT).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(Items.ACACIA_BOAT))
                    .save(r, new ResourceLocation("nexus", "test_block")))
            .withLootTable(parent -> LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(Items.ACACIA_BOAT))))
            .buildAndGet();

    private static <B extends Block> Supplier<B> registerBlock(ResourceLocation name, Supplier<B> block) {
        Supplier<B> registeredItem = NexusServices.REGISTRAR.registerObject(name, block, BuiltInRegistries.BLOCK);
        NexusServices.REGISTRAR.registerObject(name, () -> new BlockItem(registeredItem.get(), new Item.Properties()), BuiltInRegistries.ITEM);
        BLOCKS.add(registeredItem);
        return registeredItem;
    }
}
