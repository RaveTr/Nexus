package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapperBuilder;
import com.mememan.nexus.util.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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

    public static final BlockPropertyWrapper<Block> ROTATED_PILLAR_PICKAXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withModelDefinition(ModelUtil::rotatedPillar)
            .withBlockStateDefinition(ModelUtil::rotatedPillarBlockState)
            .build();
    public static final BlockPropertyWrapper<Block> ROTATED_PILLAR_AXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withModelDefinition(ModelUtil::rotatedPillar)
            .withBlockStateDefinition(ModelUtil::rotatedPillarBlockState)
            .build();
    public static final BlockPropertyWrapper<Block> ROTATED_PILLAR_HOE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withModelDefinition(ModelUtil::rotatedPillar)
            .withBlockStateDefinition(ModelUtil::rotatedPillarBlockState)
            .build();
    public static final BlockPropertyWrapper<Block> ROTATED_PILLAR_SHOVEL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withModelDefinition(ModelUtil::rotatedPillar)
            .withBlockStateDefinition(ModelUtil::rotatedPillarBlockState)
            .build();

    public static final BlockPropertyWrapper<Block> AXIS_ALIGNED_PICKAXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withModelDefinition(ModelUtil::cubeColumn)
            .withBlockStateDefinition(ModelUtil::axisAlignedBlock)
            .build();
    public static final BlockPropertyWrapper<Block> AXIS_ALIGNED_AXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withModelDefinition(ModelUtil::cubeColumn)
            .withBlockStateDefinition(ModelUtil::axisAlignedBlock)
            .build();
    public static final BlockPropertyWrapper<Block> AXIS_ALIGNED_HOE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withModelDefinition(ModelUtil::cubeColumn)
            .withBlockStateDefinition(ModelUtil::axisAlignedBlock)
            .build();
    public static final BlockPropertyWrapper<Block> AXIS_ALIGNED_SHOVEL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withModelDefinition(ModelUtil::cubeColumn)
            .withBlockStateDefinition(ModelUtil::axisAlignedBlock)
            .build();

    public static final BlockPropertyWrapper<Block> LOG = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(ROTATED_PILLAR_AXE)
            .withTag(() -> BlockTags.LOGS)
            .withAdditionalTag(() -> ItemTags.LOGS)
            .build();

    public static final BlockPropertyWrapper<Block> SLAB = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.SLABS)
            .withAdditionalTag(() -> ItemTags.SLABS)
            .withModelDefinition(ModelUtil::slab)
            .withBlockStateDefinition(ModelUtil::slabBlockState)
            .withLootTable(LootUtil::dropSlab)
            .withRecipe(RecipeUtil::slabRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> STAIRS = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.STAIRS)
            .withAdditionalTag(() -> ItemTags.STAIRS)
            .withModelDefinition(ModelUtil::stairs)
            .withBlockStateDefinition(ModelUtil::stairsBlockState)
            .withRecipe(RecipeUtil::stairsRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> BUTTON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.BUTTONS)
            .withAdditionalTag(() -> ItemTags.BUTTONS)
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
            .withAdditionalTag(() -> ItemTags.DOORS)
            .withModelDefinition(ModelUtil::door)
            .withBlockStateDefinition(ModelUtil::doorBlockState)
            .withRecipe(RecipeUtil::doorRecipeFrom)
            .withLootTable(LootUtil::dropDoor)
            .build();
    public static final BlockPropertyWrapper<Block> TRAPDOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.TRAPDOORS)
            .withAdditionalTag(() -> ItemTags.TRAPDOORS)
            .withModelDefinition(ModelUtil::trapdoor)
            .withBlockStateDefinition(ModelUtil::trapdoorBlockState)
            .withRecipe(RecipeUtil::trapdoorRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WALL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withTag(() -> BlockTags.WALLS)
            .withAdditionalTag(() -> ItemTags.WALLS)
            .withModelDefinition(ModelUtil::wall)
            .withBlockStateDefinition(ModelUtil::wallBlockState)
            .withRecipe(RecipeUtil::wallRecipeFrom)
            .build();

    public static final BlockPropertyWrapper<Block> WOODEN_SLAB = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_SLABS)
            .withAdditionalTag(() -> ItemTags.WOODEN_SLABS)
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
            .withAdditionalTag(() -> ItemTags.WOODEN_STAIRS)
            .withModelDefinition(ModelUtil::stairs)
            .withBlockStateDefinition(ModelUtil::stairsBlockState)
            .withRecipe(RecipeUtil::woodenStairsRecipeFrom)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_BUTTON = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_BUTTONS)
            .withAdditionalTag(() -> ItemTags.WOODEN_BUTTONS)
            .withModelDefinition(ModelUtil::button)
            .withBlockStateDefinition(ModelUtil::buttonBlockState)
            .withRecipe(RecipeUtil::woodenButtonRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_PRESSURE_PLATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_PRESSURE_PLATES)
            .withAdditionalTag(() -> ItemTags.WOODEN_PRESSURE_PLATES)
            .withModelDefinition(ModelUtil::pressurePlate)
            .withBlockStateDefinition(ModelUtil::pressurePlateBlockState)
            .withRecipe(RecipeUtil::woodenPressurePlateRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_DOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_DOORS)
            .withAdditionalTag(() -> ItemTags.WOODEN_DOORS)
            .withModelDefinition(ModelUtil::door)
            .withBlockStateDefinition(ModelUtil::doorBlockState)
            .withLootTable(LootUtil::dropDoor)
            .withRecipe(RecipeUtil::woodenDoorRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_TRAPDOOR = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_TRAPDOORS)
            .withAdditionalTag(() -> ItemTags.WOODEN_TRAPDOORS)
            .withModelDefinition(ModelUtil::trapdoor)
            .withBlockStateDefinition(ModelUtil::trapdoorBlockState)
            .withRecipe(RecipeUtil::woodenTrapdoorRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_FENCE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.WOODEN_FENCES)
            .withAdditionalTag(() -> ItemTags.WOODEN_FENCES)
            .withModelDefinition(ModelUtil::fence)
            .withBlockStateDefinition(ModelUtil::fenceBlockState)
            .withRecipe(RecipeUtil::fenceRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_FENCE_GATE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.FENCE_GATES)
            .withAdditionalTag(() -> ItemTags.FENCE_GATES)
            .withModelDefinition(ModelUtil::fenceGate)
            .withBlockStateDefinition(ModelUtil::fenceGateBlockState)
            .withRecipe(RecipeUtil::fenceGateRecipeFrom)
            .build();

    public static final BlockPropertyWrapper<Block> WOODEN_LOG = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(ROTATED_PILLAR_AXE)
            .withTag(() -> BlockTags.LOGS_THAT_BURN)
            .withAdditionalTag(() -> ItemTags.LOGS_THAT_BURN)
            .withBlockStripping(VanillaUtil::standardWoodLogStrippingState)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> STRIPPED_WOODEN_LOG = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(WOODEN_LOG)
            .withModelDefinition(parentBlock ->
                    ModelUtil.rotatedPillar(
                            parentBlock,
                            RegistryUtil.getTextureLocationOrDefault(parentBlock),
                            RegistryUtil.getTextureLocationOrDefaultWithSuffix(parentBlock, "_top")
                    )
            )
            .withBlockStripping(null)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_PLANKS = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withTag(() -> BlockTags.PLANKS)
            .withAdditionalTag(() -> ItemTags.PLANKS)
            .withRecipe(RecipeUtil::woodenPlanksRecipeFromComponents)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> WOOD = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(AXIS_ALIGNED_AXE)
            .withTag(() -> BlockTags.LOGS_THAT_BURN)
            .withAdditionalTag(() -> ItemTags.LOGS_THAT_BURN)
            .withModelDefinition(parentBlock -> {
                ResourceLocation parentBlockId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentBlock.get());
                ResourceLocation logTexLoc = parentBlockId.withPath(parentBlockId.getPath().replace("_wood", "_log"));

                return ModelUtil.cubeColumn(parentBlock, RegistryUtil.getTextureLocation(logTexLoc.withSuffix("_side")).orElse(RegistryUtil.getTextureLocationOrDefault(logTexLoc)));
            })
            .withRecipe(RecipeUtil::woodRecipeFrom)
            .withBlockStripping(VanillaUtil::standardWoodLogStrippingState)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();

    public static final BlockPropertyWrapper<Block> WOODEN_STANDING_SIGN = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withModelDefinition(ModelUtil::sign)
            .withTag(() -> BlockTags.STANDING_SIGNS)
            .withAdditionalTag(() -> ItemTags.SIGNS)
            .withRecipe(RecipeUtil::woodenSignRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_WALL_SIGN = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(WOODEN_STANDING_SIGN)
            .withModelDefinition(null)
            .withBlockStateDefinition(parentBlock -> ModelUtil.simpleBlockState(parentBlock, RegistryUtil.pickBlockPrefix(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentBlock.get()).withPath(curPath -> curPath.replace("_wall_sign", "_sign")))))
            .setTags(ObjectArrayList.of(() -> BlockTags.WALL_SIGNS))
            .setAdditionalTags(ObjectArrayList.of())
            .withRecipe(null)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_CEILING_HANGING_SIGN = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withModelDefinition(ModelUtil::hangingSign)
            .withTag(() -> BlockTags.CEILING_HANGING_SIGNS)
            .withAdditionalTag(() -> ItemTags.HANGING_SIGNS)
            .withRecipe(RecipeUtil::woodenHangingSignRecipeFrom)
            .build();
    public static final BlockPropertyWrapper<Block> WOODEN_WALL_HANGING_SIGN = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(WOODEN_CEILING_HANGING_SIGN)
            .withModelDefinition(null)
            .withBlockStateDefinition(parentBlock -> ModelUtil.simpleBlockState(parentBlock, RegistryUtil.pickBlockPrefix(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentBlock.get()).withPath(curPath -> curPath.replace("_wall_hanging_sign", "_hanging_sign")))))
            .setTags(ObjectArrayList.of(() -> BlockTags.WALL_HANGING_SIGNS))
            .setAdditionalTags(ObjectArrayList.of())
            .withRecipe(null)
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
            .withAdditionalTag(() -> ItemTags.WOOL)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> WOOL_CARPET = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(CARPET)
            .withTag(() -> BlockTags.WOOL_CARPETS)
            .withAdditionalTag(() -> ItemTags.WOOL_CARPETS)
            .withRecipe(RecipeUtil::woolCarpetRecipeFrom)
            .build();

    public static final BlockPropertyWrapper<Block> DIRT = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withTag(() -> BlockTags.DIRT)
            .withAdditionalTag(() -> ItemTags.DIRT)
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
            .withAdditionalTag(() -> ItemTags.FLOWERS)
            .withModelDefinition(ModelUtil::crossCutout)
            .withLootTable(LootUtil::dropSelf)
            .withBlockColor(null)
            .withFlammability(VanillaUtil::standardFlammability)
            .build();
    public static final BlockPropertyWrapper<Block> SMALL_FLOWER = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(FLOWER)
            .setTags(ObjectArrayList.of(() -> BlockTags.SMALL_FLOWERS))
            .setAdditionalTags(ObjectArrayList.of(() -> ItemTags.SMALL_FLOWERS))
            .build();
    public static final BlockPropertyWrapper<Block> TALL_FLOWER = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(FLOWER)
            .setTags(ObjectArrayList.of(() -> BlockTags.TALL_FLOWERS))
            .setAdditionalTags(ObjectArrayList.of(() -> ItemTags.TALL_FLOWERS))
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

    public static final BlockPropertyWrapper<Block> MATERIAL_BLOCK_PICKAXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_PICKAXE)
            .withRecipe(RecipeUtil::materialBlockFrom)
            .build();
    public static final BlockPropertyWrapper<Block> MATERIAL_BLOCK_HOE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_HOE)
            .withRecipe(RecipeUtil::materialBlockFrom)
            .build();
    public static final BlockPropertyWrapper<Block> MATERIAL_BLOCK_AXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_AXE)
            .withRecipe(RecipeUtil::materialBlockFrom)
            .build();
    public static final BlockPropertyWrapper<Block> MATERIAL_BLOCK_SHOVEL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(BASIC_SHOVEL)
            .withRecipe(RecipeUtil::materialBlockFrom)
            .build();

    public static final BlockPropertyWrapper<Block> COMPONENT_BLOCK_PICKAXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(MATERIAL_BLOCK_PICKAXE)
            .withLootTable(LootUtil::dropComponents)
            .build();
    public static final BlockPropertyWrapper<Block> COMPONENT_BLOCK_HOE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(MATERIAL_BLOCK_HOE)
            .withLootTable(LootUtil::dropComponents)
            .build();
    public static final BlockPropertyWrapper<Block> COMPONENT_BLOCK_AXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(MATERIAL_BLOCK_AXE)
            .withLootTable(LootUtil::dropComponents)
            .build();
    public static final BlockPropertyWrapper<Block> COMPONENT_BLOCK_SHOVEL = new BlockPropertyWrapper<>()
            .builder()
            .copyFrom(MATERIAL_BLOCK_SHOVEL)
            .withLootTable(LootUtil::dropComponents)
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

    /**
     * Registers and returns the provided {@link Block}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
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
    public static <B extends Block> Supplier<B> registerBlockAndReflect(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = NexusServices.REGISTRAR.registerObjectAndReflect(blockId, blockSup, BuiltInRegistries.BLOCK);

        if (blockSupCol != null) blockSupCol.add((Supplier<Block>) registeredBlock);

        return registeredBlock;
    }

    /**
     * Overloaded variant of {@link #registerBlockAndReflect(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockAndReflect(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBlockAndReflect(blockId, blockSup, null);
    }

    /**
     * Registers and returns the provided {@link Block}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
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
    public static <B extends Block> Supplier<B> registerBlockWithItemAndReflect(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = NexusServices.REGISTRAR.registerObjectAndReflect(blockId, blockSup, BuiltInRegistries.BLOCK);

        if (blockSupCol != null) blockSupCol.add((Supplier<Block>) registeredBlock);

        return registeredBlock;
    }

    /**
     * Overloaded variant of {@link #registerBlockWithItemAndReflect(ResourceLocation, Supplier, Collection, Collection)} that
     * does not track the registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockWithItemAndReflect(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBlockWithItemAndReflect(blockId, blockSup, null, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapping it to a new {@link BlockPropertyWrapper} inheriting
     * from the provided {@link BlockPropertyWrapper} template. Optionally tracks the registered {@link Block} to a
     * custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
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
    public static <B extends Block> Supplier<B> registerBlockFromTemplateAndReflect(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlockAndReflect(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockFromTemplateAndReflect(ResourceLocation, Supplier, BlockPropertyWrapper, Collection)} that does not track the
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
    public static <B extends Block> Supplier<B> registerBlockFromTemplateAndReflect(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerBlockFromTemplateAndReflect(blockId, blockSup, templateBPW, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapping it to a new {@link BlockPropertyWrapper} inheriting
     * from the provided {@link BlockPropertyWrapper} template, and automatically creates a {@link BlockItem} for it.
     * Optionally tracks both the registered {@link Block} and its corresponding {@link BlockItem} to custom
     * {@link Collection}s.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
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
    public static <B extends Block> Supplier<B> registerBlockWithItemFromTemplateAndReflect(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = registerBlockFromTemplateAndReflect(blockId, blockSup, templateBPW, blockSupCol);

        ItemPropertyWrapperTemplates.registerItemAndReflect(blockId, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), blockItemSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockWithItemFromTemplateAndReflect(ResourceLocation, Supplier, BlockPropertyWrapper, Collection, Collection)} that does not track the
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
    public static <B extends Block> Supplier<B> registerBlockWithItemFromTemplateAndReflect(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerBlockWithItemFromTemplateAndReflect(blockId, blockSup, templateBPW, null, null);
    }

    /**
     * Registers the provided {@link Block} and returns its {@link BlockPropertyWrapperBuilder} inheriting from the
     * provided {@link BlockPropertyWrapper} template. Optionally tracks the registered {@link Block} to a custom
     * {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
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
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndReflectAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlockAndReflect(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, BlockPropertyWrapper, Collection)} that does not track the
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
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndReflectAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerAndReflectAndChain(blockId, blockSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Block} and returns its {@link BlockPropertyWrapperBuilder}. Optionally tracks the
     * registered {@link Block} to a custom {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndReflectAndChain(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlockAndReflect(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndReflectAndChain(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Block}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndReflectAndChain(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerAndReflectAndChain(blockId, blockSup, (Collection<Supplier<Block>>) null);
    }

    /**
     * Registers the provided {@link Block}, automatically creates a {@link BlockItem} for it, and returns its
     * {@link BlockPropertyWrapperBuilder} inheriting from the provided {@link BlockPropertyWrapper} template.
     * Optionally tracks both the registered {@link Block} and its corresponding {@link BlockItem} to custom
     * {@link Collection}s.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
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
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerWithItemAndReflectAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = registerBlockAndReflect(blockId, blockSup, blockSupCol);

        ItemPropertyWrapperTemplates.registerItemAndReflect(blockId, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), blockItemSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerWithItemAndReflectAndChain(ResourceLocation, Supplier, BlockPropertyWrapper, Collection, Collection)} that does not track the
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
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerWithItemAndReflectAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerWithItemAndReflectAndChain(blockId, blockSup, templateBPW, null, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapped to a new {@link BlockPropertyWrapper} inheriting
     * from the {@link #BASIC} template. Optionally tracks the registered {@link Block} to a custom
     * {@link Collection}.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
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
    public static <B extends Block> Supplier<B> registerBasicBlockAndReflect(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        return registerBlockFromTemplateAndReflect(blockId, blockSup, BASIC, blockSupCol);
    }

    /**
     * Overloaded variant of {@link #registerBasicBlockAndReflect(ResourceLocation, Supplier, Collection)} that does not track the
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
    public static <B extends Block> Supplier<B> registerBasicBlockAndReflect(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBasicBlockAndReflect(blockId, blockSup, null);
    }

    /**
     * Registers a new basic {@link Block} with default properties and returns it, mapped to a new
     * {@link BlockPropertyWrapper} inheriting from the {@link #BASIC} template.
     * <br></br>
     * Uses {@link Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)} instead of
     * {@link Registrar#registerObject(ResourceLocation, Supplier, Registry)}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the {@code BASIC} template.
     */
    public static Supplier<Block> registerBasicBlockAndReflect(ResourceLocation blockId) {
        return registerBasicBlockAndReflect(blockId, () -> new Block(BlockBehaviour.Properties.of()));
    }
}
