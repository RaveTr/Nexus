package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.client.model.item.ItemModelDefinition;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@RegistrarEntry
public class TestBlockRegistrar {
    private static final ObjectArrayList<Supplier<? extends Block>> BLOCKS = new ObjectArrayList<>();

    public static final Supplier<SlabBlock> BLAH = new BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_block"), () -> new SlabBlock(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .withRecipe(r -> result -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get())
                    .requires(Items.ACACIA_BOAT)
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(Items.ACACIA_BOAT).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(Items.ACACIA_BOAT))
                    .save(r, new ResourceLocation("nexus", result.get().getDescriptionId().substring(result.get().getDescriptionId().lastIndexOf(".") + 1))))
            .withLootTable(parent -> LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(Items.ACACIA_BOAT))))
            .buildAndGet();

    public static final Supplier<SlabBlock> BLAH_2 = new BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_able_block"), () -> new SlabBlock(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .copyFrom(BLAH)
            .literalTranslation()
            .withModelDefinition(suh -> new ItemModelDefinition(ModelTemplates.CUBE_ALL)
                    .withTextureMapping(TextureMapping.cube(NexusConstants.prefix("test_able_block")))
                    .withItemModelTextureOverride(Map.of(NexusConstants.prefix("blud"), 0.614F), NexusConstants.prefix("zamn_texture_path/zamn")))
            .withLocalization(v -> v.toUpperCase(Locale.ROOT))
            .buildAndGet();

    public static final Supplier<Block> BLAH_3 = new BlockPropertyWrapper<>(registerBlock(new ResourceLocation("nexus", "test_block_3"), () -> new Block(BlockBehaviour.Properties.of())), "nexus")
            .builder()
            .withTags(TestBlockTags.TEST::get)
            .buildAndGet();

    private static <B extends Block> Supplier<B> registerBlock(ResourceLocation name, Supplier<B> block) {
        Supplier<B> registeredItem = NexusServices.REGISTRAR.registerObject(name, block, BuiltInRegistries.BLOCK);
        NexusServices.REGISTRAR.registerObject(name, () -> new BlockItem(registeredItem.get(), new Item.Properties()), BuiltInRegistries.ITEM);
        BLOCKS.add(registeredItem);
        return registeredItem;
    }
}
