package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapperBuilder;
import com.mememan.nexus.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
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
    /**
     * Basic BPW template for plain blocks. Cube model, simple blockstate, drops self.
     */
    public static final BlockPropertyWrapper<Block> BASIC = new BlockPropertyWrapper<>()
            .builder()
            .withModelDefinition(ModelUtil::cubeAll)
            .withBlockStateDefinition(ModelUtil::simpleBlockState)
            .withLootTable(LootUtil::dropSelf)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with a pickaxe. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_PICKAXE)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone pickaxe or better. Inherits from {@link #BASIC_PICKAXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron pickaxe or better. Inherits from {@link #BASIC_PICKAXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond pickaxe or better. Inherits from {@link #BASIC_PICKAXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with an axe. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_AXE)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone axe or better. Inherits from {@link #BASIC_AXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron axe or better. Inherits from {@link #BASIC_AXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond axe or better. Inherits from {@link #BASIC_AXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with a hoe. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_HOE)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone hoe or better. Inherits from {@link #BASIC_HOE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron hoe or better. Inherits from {@link #BASIC_HOE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond hoe or better. Inherits from {@link #BASIC_HOE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with a shovel. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_SHOVEL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone shovel or better. Inherits from {@link #BASIC_SHOVEL}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron shovel or better. Inherits from {@link #BASIC_SHOVEL}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond shovel or better. Inherits from {@link #BASIC_SHOVEL}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    /**
     * BPW template for slabs. Inherits from {@link #BASIC_PICKAXE}. Slab loot table, slab blockstate, slab model.
     * Additionally, tags the parent block with {@link BlockTags#SLABS}. Automatically maps a recipe for the parent slab.
     */
    public static final BlockPropertyWrapper<Block> SLAB = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.SLABS)
            .withModelDefinition(ModelUtil::slab)
            .withBlockStateDefinition(ModelUtil::slabBlockState)
            .withLootTable(LootUtil::dropSlab)
            .withRecipe(RecipeUtil::slabRecipeFrom)
            .build();
    /**
     * BPW template for stairs. Inherits from {@link #BASIC_PICKAXE}. Stairs model, stairs blockstate.
     * Additionally, tags the parent block with {@link BlockTags#STAIRS}. Automatically maps a recipe for the parent stairs.
     */
    public static final BlockPropertyWrapper<Block> STAIRS = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.STAIRS)
            .withModelDefinition(ModelUtil::stairs)
            .withBlockStateDefinition(ModelUtil::stairsBlockState)
            .withRecipe(RecipeUtil::stairsRecipeFrom)
            .build();
    /**
     * BPW template for buttons. Inherits from {@link #BASIC}. Button model, button blockstate.
     * Blockstate uses {@link MultiVariantGenerator} with different models for each combination of power state,
     * attach face, and facing direction. Additionally, tags the parent block with {@link BlockTags#BUTTONS}.
     * Automatically maps a recipe for the parent button.
     */
    public static final BlockPropertyWrapper<Block> BUTTON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.BUTTONS)
            .withModelDefinition(ModelUtil::button)
            .withBlockStateDefinition(ModelUtil::buttonBlockState)
            .withRecipe(RecipeUtil::buttonRecipeFrom)
            .build();
    /**
     * BPW template for pressure plates. Inherits from {@link #BASIC_PICKAXE}. Pressure plate model, pressure plate blockstate.
     * Blockstate uses {@link MultiVariantGenerator} with different models for each power state (pressed/unpressed).
     * Additionally, tags the parent block with {@link BlockTags#PRESSURE_PLATES}. Automatically maps a recipe for the
     * parent pressure plate.
     */
    public static final BlockPropertyWrapper<Block> PRESSURE_PLATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.PRESSURE_PLATES)
            .withModelDefinition(ModelUtil::pressurePlate)
            .withBlockStateDefinition(ModelUtil::pressurePlateBlockState)
            .withRecipe(RecipeUtil::pressurePlateRecipeFrom)
            .build();
    /**
     * BPW template for doors. Inherits from {@link #BASIC_PICKAXE}. Door model, door blockstate. Blockstate uses
     * {@link MultiVariantGenerator} with different models for each combination of hinge side, half, and open state.
     * Additionally, tags the parent block with {@link BlockTags#DOORS}. Automatically maps a recipe for the parent door.
     */
    public static final BlockPropertyWrapper<Block> DOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.DOORS)
            .withModelDefinition(ModelUtil::door)
            .withBlockStateDefinition(ModelUtil::doorBlockState)
            .withRecipe(RecipeUtil::doorRecipeFrom)
            .withLootTable(LootUtil::dropDoor)
            .build();
    /**
     * BPW template for trapdoors. Inherits from {@link #BASIC_PICKAXE}. Trapdoor model, trapdoor blockstate.
     * Blockstate uses {@link MultiVariantGenerator} with different models for each combination of facing, half,
     * and open state. Additionally, tags the parent block with {@link BlockTags#TRAPDOORS}. Automatically maps a recipe
     * for the parent trapdoor.
     */
    public static final BlockPropertyWrapper<Block> TRAPDOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.TRAPDOORS)
            .withModelDefinition(ModelUtil::trapdoor)
            .withBlockStateDefinition(ModelUtil::trapdoorBlockState)
            .withRecipe(RecipeUtil::trapdoorRecipeFrom)
            .build();
    /**
     * BPW template for walls. Inherits from {@link #BASIC_PICKAXE}. Wall model, wall blockstate.
     * Additionally, tags the parent block with {@link BlockTags#WALLS}. Automatically maps a recipe for the parent wall.
     */
    public static final BlockPropertyWrapper<Block> WALL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.WALLS)
            .withModelDefinition(ModelUtil::wall)
            .withBlockStateDefinition(ModelUtil::wallBlockState)
            .withRecipe(RecipeUtil::wallRecipeFrom)
            .build();

    /**
     * BPW template for wooden slabs. Inherits from {@link #BASIC_AXE}. Slab model, wooden slab blockstate,
     * slab loot table. Additionally, tags the parent block with {@link BlockTags#WOODEN_SLABS}. Automatically
     * maps a recipe for the parent slab.
     */
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
    /**
     * BPW template for wooden stairs. Inherits from {@link #BASIC_AXE}. Stairs model, stairs blockstate.
     * Additionally, tags the parent block with {@link BlockTags#WOODEN_STAIRS}. Automatically maps a recipe for the
     * parent stairs.
     */
    public static final BlockPropertyWrapper<Block> WOODEN_STAIRS = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_STAIRS)
            .withModelDefinition(ModelUtil::stairs)
            .withBlockStateDefinition(ModelUtil::stairsBlockState)
            .withRecipe(RecipeUtil::woodenStairsRecipeFrom)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    /**
     * BPW template for wooden buttons. Inherits from {@link #BASIC}. Button model, button blockstate.
     * Blockstate uses {@link MultiVariantGenerator} with different models for each combination of power state,
     * attach face, and facing direction. Additionally, tags the parent block with {@link BlockTags#WOODEN_BUTTONS}.
     * Automatically maps a recipe for the parent button.
     */
    public static final BlockPropertyWrapper<Block> WOODEN_BUTTON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_BUTTONS)
            .withModelDefinition(ModelUtil::button)
            .withBlockStateDefinition(ModelUtil::buttonBlockState)
            .withRecipe(RecipeUtil::buttonRecipeFrom)
            .build();
    /**
     * BPW template for wooden pressure plates. Inherits from {@link #BASIC_AXE}. Pressure plate model, pressure plate blockstate.
     * Blockstate uses {@link MultiVariantGenerator} with different models for each power state (pressed/unpressed).
     * Additionally, tags the parent block with {@link BlockTags#WOODEN_PRESSURE_PLATES}. Automatically maps a recipe for
     * the parent pressure plate.
     */
    public static final BlockPropertyWrapper<Block> WOODEN_PRESSURE_PLATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_PRESSURE_PLATES)
            .withModelDefinition(ModelUtil::pressurePlate)
            .withBlockStateDefinition(ModelUtil::pressurePlateBlockState)
            .withRecipe(RecipeUtil::pressurePlateRecipeFrom)
            .build();
    /**
     * BPW template for wooden doors. Inherits from {@link #BASIC_AXE}. Door model, door blockstate. Blockstate
     * and model assume the existence of an equivalent block with the suffix {@code "_planks"}. Additionally, tags the
     * parent block with {@link BlockTags#WOODEN_DOORS}. Automatically maps a recipe for the parent door.
     */
    public static final BlockPropertyWrapper<Block> WOODEN_DOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_DOORS)
            .withModelDefinition(ModelUtil::door)
            .withBlockStateDefinition(ModelUtil::doorBlockState)
            .withLootTable(LootUtil::dropDoor)
            .withRecipe(RecipeUtil::doorRecipeFrom)
            .build();
    /**
     * BPW template for wooden trapdoors. Inherits from {@link #BASIC_AXE}. Trapdoor model, trapdoor blockstate,
     * wooden trapdoor flammability. Blockstate uses {@link MultiVariantGenerator} with different models for each
     * combination of facing, half, and open state. Additionally, tags the parent block with {@link BlockTags#WOODEN_TRAPDOORS}.
     * Automatically maps a recipe for the parent trapdoor.
     */
    public static final BlockPropertyWrapper<Block> WOODEN_TRAPDOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_TRAPDOORS)
            .withModelDefinition(ModelUtil::trapdoor)
            .withBlockStateDefinition(ModelUtil::trapdoorBlockState)
            .withRecipe(RecipeUtil::woodenTrapdoorRecipeFrom)
            .build();

    /**
     * BPW template for fences. Inherits from {@link #BASIC_AXE}. Fence model, fence blockstate.
     * Blockstate uses {@link MultiPartGenerator} with different models for post and side connections
     * based on adjacent block connections. Additionally, tags the parent block with {@link BlockTags#WOODEN_FENCES}.
     * Automatically maps a recipe for the parent fence.
     */
    public static final BlockPropertyWrapper<Block> WOODEN_FENCE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_FENCES)
            .withModelDefinition(ModelUtil::fence)
            .withBlockStateDefinition(ModelUtil::fenceBlockState)
            .withRecipe(RecipeUtil::fenceRecipeFrom)
            .build();
    /**
     * BPW template for fence gates. Inherits from {@link #BASIC_AXE}. Fence gate model, fence gate blockstate.
     * Blockstate uses {@link MultiVariantGenerator} with different models for each combination of facing,
     * wall attachment, and open state. Additionally, tags the parent block with {@link BlockTags#FENCE_GATES}.
     * Automatically maps a recipe for the parent fence gate.
     */
    public static final BlockPropertyWrapper<Block> WOODEN_FENCE_GATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.FENCE_GATES)
            .withModelDefinition(ModelUtil::fenceGate)
            .withBlockStateDefinition(ModelUtil::fenceGateBlockState)
            .withRecipe(RecipeUtil::fenceGateRecipeFrom)
            .build();

    /**
     * BPW template for carpets. Inherits from {@link #BASIC}. Carpet model, simple blockstate, drops self.
     */
    public static final BlockPropertyWrapper<Block> CARPET = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withModelDefinition(ModelUtil::carpet)
            .build();

    /**
     * BPW template for wool. Inherits from {@link #BASIC}. Cube model, simple blockstate, drops self.
     * Additionally, tags the parent block with {@link BlockTags#WOOL}.
     */
    public static final BlockPropertyWrapper<Block> WOOL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC)
            .withTag(() -> BlockTags.WOOL)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    /**
     * BPW template for wool carpets. Inherits from {@link #CARPET}. Wool carpet model, simple blockstate, drops self.
     * Additionally, tags the parent block with {@link BlockTags#WOOL_CARPETS}. Automatically maps a recipe for the
     * parent carpet.
     */
    public static final BlockPropertyWrapper<Block> WOOL_CARPET = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(CARPET)
            .withTag(() -> BlockTags.WOOL_CARPETS)
            .withRecipe(RecipeUtil::woolCarpetRecipeFrom)
            .build();

    /**
     * BPW template for dirt. Inherits from {@link #BASIC_SHOVEL}. Dirt model, simple blockstate, drops self.
     * Additionally, tags the parent block with {@link BlockTags#DIRT}.
     */
    public static final BlockPropertyWrapper<Block> DIRT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.DIRT)
            .withBlockTilling(VanillaUtil::dirtFarmlandTillingAction)
            .withBlockFlattening(VanillaUtil::dirtPathFlatteningAction)
            .build();
    /**
     * BPW template for farmland. Inherits from {@link #BASIC_SHOVEL}. Farmland model, farmland blockstate.
     */
    public static final BlockPropertyWrapper<Block> FARMLAND = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withModelDefinition(ModelUtil::farmland)
            .withBlockStateDefinition(ModelUtil::farmlandBlockState)
            .withLootTable(LootUtil::dropFarmland)
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
