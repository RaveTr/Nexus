package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapperBuilder;
import com.mememan.nexus.util.LootUtil;
import com.mememan.nexus.util.ModelUtil;
import com.mememan.nexus.util.RecipeUtil;
import com.mememan.nexus.util.VanillaUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link BlockPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class BlockPropertyWrapperTemplates {
    public static final BlockPropertyWrapper<Block> BASIC = new BlockPropertyWrapper<>()
            .builder()
            .withModelDefinition(ModelUtil::cubeAll)
            .withBlockStateDefinition(ModelUtil::simpleBlockState)
            .withLootTable(LootUtil::dropSelf)
            .build();

    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_PICKAXE)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    public static final BlockPropertyWrapper<Block> BASIC_AXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_AXE)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_AXE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_AXE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_AXE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    public static final BlockPropertyWrapper<Block> BASIC_HOE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_HOE)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_HOE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_HOE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_HOE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_SHOVEL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    public static final BlockPropertyWrapper<Block> SLAB = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.SLABS)
            .withModelDefinition(ModelUtil::slab)
            .withBlockStateDefinition(ModelUtil::slabBlockState)
            .withLootTable(LootUtil::dropSlab)
            .withRecipe(RecipeUtil::slabRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> STAIRS = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.STAIRS)
            .withModelDefinition(ModelUtil::stairs)
            .withBlockStateDefinition(ModelUtil::stairsBlockState)
            .withRecipe(RecipeUtil::stairsRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> BUTTON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.BUTTONS)
            .withModelDefinition(ModelUtil::button)
            .withBlockStateDefinition(ModelUtil::buttonBlockState)
            .withRecipe(RecipeUtil::buttonRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> PRESSURE_PLATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.PRESSURE_PLATES)
            .withModelDefinition(ModelUtil::pressurePlate)
            .withBlockStateDefinition(ModelUtil::pressurePlateBlockState)
            .withRecipe(RecipeUtil::pressurePlateRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> DOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.DOORS)
            .withModelDefinition(ModelUtil::door)
            .withBlockStateDefinition(ModelUtil::doorBlockState)
            .withRecipe(RecipeUtil::doorRecipeFrom)
            .withLootTable(LootUtil::dropDoor)
            .build();
    public static final BlockPropertyWrapper<Block> TRAPDOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.TRAPDOORS)
            .withModelDefinition(ModelUtil::trapdoor)
            .withBlockStateDefinition(ModelUtil::trapdoorBlockState)
            .withRecipe(RecipeUtil::trapdoorRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WALL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.WALLS)
            .withModelDefinition(ModelUtil::wall)
            .withBlockStateDefinition(ModelUtil::wallBlockState)
            .withRecipe(RecipeUtil::wallRecipeFrom)
            .build();

    public static final BlockPropertyWrapper<Block> WOODEN_SLAB = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_SLABS)
            .withModelDefinition(ModelUtil::slab)
            .withBlockStateDefinition(ModelUtil::slabBlockState)
            .withLootTable(LootUtil::dropSlab)
            .withRecipe(RecipeUtil::woodenSlabRecipeFrom)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_STAIRS = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_STAIRS)
            .withModelDefinition(ModelUtil::stairs)
            .withBlockStateDefinition(ModelUtil::stairsBlockState)
            .withRecipe(RecipeUtil::woodenStairsRecipeFrom)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_BUTTON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_BUTTONS)
            .withModelDefinition(ModelUtil::button)
            .withBlockStateDefinition(ModelUtil::buttonBlockState)
            .withRecipe(RecipeUtil::buttonRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_PRESSURE_PLATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_PRESSURE_PLATES)
            .withModelDefinition(ModelUtil::pressurePlate)
            .withBlockStateDefinition(ModelUtil::pressurePlateBlockState)
            .withRecipe(RecipeUtil::pressurePlateRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_DOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_DOORS)
            .withModelDefinition(ModelUtil::door)
            .withBlockStateDefinition(ModelUtil::doorBlockState)
            .withLootTable(LootUtil::dropDoor)
            .withRecipe(RecipeUtil::doorRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_TRAPDOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_TRAPDOORS)
            .withModelDefinition(ModelUtil::trapdoor)
            .withBlockStateDefinition(ModelUtil::trapdoorBlockState)
            .withRecipe(RecipeUtil::woodenTrapdoorRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_FENCE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_FENCES)
            .withModelDefinition(ModelUtil::fence)
            .withBlockStateDefinition(ModelUtil::fenceBlockState)
            .withRecipe(RecipeUtil::fenceRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_FENCE_GATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.FENCE_GATES)
            .withModelDefinition(ModelUtil::fenceGate)
            .withBlockStateDefinition(ModelUtil::fenceGateBlockState)
            .withRecipe(RecipeUtil::fenceGateRecipeFrom)
            .build();

    public static final BlockPropertyWrapper<Block> CARPET = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withModelDefinition(ModelUtil::carpet)
            .build();

    public static final BlockPropertyWrapper<Block> WOOL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.WOOL)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> WOOL_CARPET = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(CARPET)
            .withTag(() -> BlockTags.WOOL_CARPETS)
            .withRecipe(RecipeUtil::woolCarpetRecipeFrom)
            .build();

    public static final BlockPropertyWrapper<Block> DIRT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.DIRT)
            .withBlockTilling(VanillaUtil::dirtFarmlandTillingAction)
            .withBlockFlattening(VanillaUtil::dirtPathFlatteningAction)
            .build();
    public static final BlockPropertyWrapper<Block> GRASS_BLOCK = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withBlockTilling(VanillaUtil::grassBlockFarmlandTillingAction)
            .withBlockFlattening(VanillaUtil::grassBlockPathFlatteningAction)
            .withBlockColor(VanillaUtil::standardGrassColor)
            .withLootTable(LootUtil::dropGrassBlock)
            .build();
    public static final BlockPropertyWrapper<Block> FARMLAND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withModelDefinition(ModelUtil::farmland)
            .withBlockStateDefinition(ModelUtil::farmlandBlockState)
            .withLootTable(LootUtil::dropFarmland)
            .build();

    public static final BlockPropertyWrapper<Block> PLANT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withModelDefinition(ModelUtil::tintedCrossCutout)
            .withLootTable(LootUtil::dropSilkTouchOrShears)
            .withBlockColor(VanillaUtil::standardGrassColor)
            .build();
    public static final BlockPropertyWrapper<Block> TALL_PLANT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(PLANT)
            .withModelDefinition(ModelUtil::tintedDoublePlant)
            .withBlockStateDefinition(ModelUtil::doublePlantBlockState)
            .withLootTable(LootUtil::dropDoublePlantShearsOrSilkTouch)
            .build();
    public static final BlockPropertyWrapper<Block> NO_TINT_PLANT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(PLANT)
            .withBlockColor(null)
            .build();
    public static final BlockPropertyWrapper<Block> NO_TINT_TALL_PLANT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(TALL_PLANT)
            .withBlockColor(null)
            .build();

    public static final BlockPropertyWrapper<Block> FLOWER = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(PLANT)
            .withTag(() -> BlockTags.FLOWERS)
            .withModelDefinition(ModelUtil::crossCutout)
            .withLootTable(LootUtil::dropSelf)
            .withBlockColor(null)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> SMALL_FLOWER = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(FLOWER)
            .setTags(ObjectArrayList.of(() -> BlockTags.SMALL_FLOWERS))
            .build();
    public static final BlockPropertyWrapper<Block> TALL_FLOWER = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(FLOWER)
            .setTags(ObjectArrayList.of(() -> BlockTags.TALL_FLOWERS))
            .withModelDefinition(ModelUtil::doublePlant)
            .withBlockStateDefinition(ModelUtil::doublePlantBlockState)
            .withLootTable(LootUtil::dropDoublePlant)
            .build();
    public static final BlockPropertyWrapper<Block> FLOWER_POT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.FLOWER_POTS)
            .withModelDefinition(ModelUtil::flowerPotCross)
            .withLootTable(LootUtil::dropPottedContents)
            .build();

    private BlockPropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (BlockPropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link Block}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlock(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = NexusServices.REGISTRAR.registerObject(blockId, blockSup, BuiltInRegistries.BLOCK);

        if (blockSupCol != null) blockSupCol.add((Supplier<Block>) registeredBlock);

        return registeredBlock;
    }

    /**
     * Overloaded variant of {@link #registerBlock(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlock(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBlock(blockId, blockSup, null);
    }

    /**
     * Registers and returns the provided {@link Block}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     * @param blockItemSupCol An optional {@link Collection} to track the registered {@link BlockItem}. Primarily useful if you
     *                        want a shorthand method of tracking your own registered block items.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockWithItem(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = NexusServices.REGISTRAR.registerObject(blockId, blockSup, BuiltInRegistries.BLOCK);

        if (blockSupCol != null) blockSupCol.add((Supplier<Block>) registeredBlock);

        return registeredBlock;
    }

    /**
     * Overloaded variant of {@link #registerBlockWithItem(ResourceLocation, Supplier, Collection, Collection)} that
     * does not track the registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockWithItem(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBlockWithItem(blockId, blockSup, null, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapping it to a new {@link BlockPropertyWrapper} inheriting
     * from the provided {@link BlockPropertyWrapper} template. Optionally tracks the registered {@link Block} to a
     * custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlock(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockFromTemplate(ResourceLocation, Supplier, BlockPropertyWrapper, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerBlockFromTemplate(blockId, blockSup, templateBPW, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapping it to a new {@link BlockPropertyWrapper} inheriting
     * from the provided {@link BlockPropertyWrapper} template, and automatically creates a {@link BlockItem} for it.
     * Optionally tracks both the registered {@link Block} and its corresponding {@link BlockItem} to custom
     * {@link Collection}s.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     * @param blockItemSupCol An optional {@link Collection} to track the registered {@link BlockItem}. Primarily useful if you
     *                        want a shorthand method of tracking your own registered block items.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockWithItemFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = registerBlockFromTemplate(blockId, blockSup, templateBPW, blockSupCol);

        ItemPropertyWrapperTemplates.registerItem(blockId, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), blockItemSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockWithItemFromTemplate(ResourceLocation, Supplier, BlockPropertyWrapper, Collection, Collection)} that does not track the
     * registered {@link Block} or its corresponding {@link BlockItem} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockWithItemFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerBlockWithItemFromTemplate(blockId, blockSup, templateBPW, null, null);
    }

    /**
     * Registers the provided {@link Block} and returns its {@link BlockPropertyWrapperBuilder} inheriting from the
     * provided {@link BlockPropertyWrapper} template. Optionally tracks the registered {@link Block} to a custom
     * {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlock(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, BlockPropertyWrapper, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerAndChain(blockId, blockSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Block} and returns its {@link BlockPropertyWrapperBuilder}. Optionally tracks the
     * registered {@link Block} to a custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, chaining from its own
     * {@link BlockPropertyWrapperBuilder}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndChain(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlock(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, BlockPropertyWrapper, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, chaining from its own
     * {@link BlockPropertyWrapperBuilder}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndChain(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerAndChain(blockId, blockSup, (Collection<Supplier<Block>>) null);
    }

    /**
     * Registers the provided {@link Block}, automatically creates a {@link BlockItem} for it, and returns its
     * {@link BlockPropertyWrapperBuilder} inheriting from the provided {@link BlockPropertyWrapper} template.
     * Optionally tracks both the registered {@link Block} and its corresponding {@link BlockItem} to custom
     * {@link Collection}s.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     * @param blockItemSupCol An optional {@link Collection} to track the registered {@link BlockItem}. Primarily useful if you
     *                        want a shorthand method of tracking your own registered block items.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerWithItemAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = registerBlock(blockId, blockSup, blockSupCol);

        ItemPropertyWrapperTemplates.registerItem(blockId, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), blockItemSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerWithItemAndChain(ResourceLocation, Supplier, BlockPropertyWrapper, Collection, Collection)} that does not track the
     * registered {@link Block} or its corresponding {@link BlockItem} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerWithItemAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerWithItemAndChain(blockId, blockSup, templateBPW, null, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapped to a new {@link BlockPropertyWrapper} inheriting
     * from the {@link #BASIC} template. Optionally tracks the registered {@link Block} to a custom
     * {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the {@code BASIC} template.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBasicBlock(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        return registerBlockFromTemplate(blockId, blockSup, BASIC, blockSupCol);
    }

    /**
     * Overloaded variant of {@link #registerBasicBlock(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the {@code BASIC} template.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBasicBlock(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBasicBlock(blockId, blockSup, null);
    }

    /**
     * Registers a new basic {@link Block} with default properties and returns it, mapped to a new
     * {@link BlockPropertyWrapper} inheriting from the {@link #BASIC} template.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the {@code BASIC} template.
     */
    public static Supplier<Block> registerBasicBlock(ResourceLocation blockId) {
        return registerBasicBlock(blockId, () -> new Block(BlockBehaviour.Properties.of()));
    }
}
