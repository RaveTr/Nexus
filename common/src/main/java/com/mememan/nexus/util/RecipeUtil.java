package com.mememan.nexus.util;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility {@code class} containing helpful recipe shortcut/delegator helper methods, as well as some re-used constants
 * related to recipes in general.
 */
public final class RecipeUtil {

    private RecipeUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (RecipeUtil)");
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> slabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultantSlabMapper.apply(parentItemLike), 6)
                    .define('P', parentItemLike)
                    .pattern("PPP")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> slabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return slabRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("slab")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> slabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return slabRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("slab"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenSlabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultantSlabMapper.apply(parentItemLike), 6)
                    .define('P', parentItemLike)
                    .pattern("PPP")
                    .group("wooden_slab")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenSlabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenSlabRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("slab")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenSlabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenSlabRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("slab"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> stairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultantSlabMapper.apply(parentItemLike), 4)
                    .define('P', parentItemLike)
                    .pattern("P")
                    .pattern("PP")
                    .pattern("PPP")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> stairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return stairsRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("stairs")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> stairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return stairsRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("stairs"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenStairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultantSlabMapper.apply(parentItemLike), 4)
                    .define('P', parentItemLike)
                    .pattern("P")
                    .pattern("PP")
                    .pattern("PPP")
                    .group("wooden_stairs")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenStairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenStairsRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("stairs")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenStairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenStairsRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("stairs"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> wallRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultantSlabMapper.apply(parentItemLike), 6)
                    .define('P', parentItemLike)
                    .pattern("PPP")
                    .pattern("PPP")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> wallRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return wallRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("wall")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> wallRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return wallRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("wall"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> buttonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike))
                    .requires(parentItemLike)
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> buttonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return buttonRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("button")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> buttonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return buttonRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("button"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenButtonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike))
                    .requires(parentItemLike)
                    .group("wooden_button")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenButtonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenButtonRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("button")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenButtonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenButtonRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("button"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> pressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike))
                    .define('P', parentItemLike)
                    .pattern("PP")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> pressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return pressurePlateRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("pressure_plate")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> pressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return pressurePlateRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("pressure_plate"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenPressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike))
                    .define('P', parentItemLike)
                    .pattern("PP")
                    .group("wooden_pressure_plate")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenPressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenPressurePlateRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("pressure_plate")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenPressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenPressurePlateRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("pressure_plate"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenFenceRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultantSlabMapper.apply(parentItemLike), 3)
                    .define('P', parentItemLike)
                    .define('S', Items.STICK)
                    .pattern("PSP")
                    .pattern("PSP")
                    .group("wooden_fence")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenFenceRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenFenceRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("fence")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenFenceRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenFenceRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("fence"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenFenceGateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike))
                    .define('P', parentItemLike)
                    .define('S', Items.STICK)
                    .pattern("SPS")
                    .pattern("SPS")
                    .group("wooden_fence_gate")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenFenceGateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenFenceGateRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("fence_gate")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenFenceGateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenFenceGateRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("fence_gate"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> doorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike))
                    .define('P', parentItemLike)
                    .pattern("PP")
                    .pattern("PP")
                    .pattern("PP")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> doorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return doorRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("door")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> doorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return doorRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("door"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike), 3)
                    .define('P', parentItemLike)
                    .pattern("PP")
                    .pattern("PP")
                    .pattern("PP")
                    .group("wooden_door")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenDoorRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("door")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenDoorRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("door"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> trapDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike))
                    .define('P', parentItemLike)
                    .pattern("PP")
                    .pattern("PP")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> trapDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return trapDoorRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("trapdoor")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> trapDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return trapDoorRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("trapdoor"));
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenTrapDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<IL, IL> resultantSlabMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            IL parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(parentItemLike)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for ItemLike of type %s: %s", parentItemLike.getClass().getSimpleName(), parentItemLike)));

            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resultantSlabMapper.apply(parentItemLike), 2)
                    .define('P', parentItemLike)
                    .pattern("PP")
                    .pattern("PP")
                    .group("wooden_trapdoor")
                    .unlockedBy("has_" + parentItemLikeId.getPath(), PredicateUtil.has(parentItemLike))
                    .save(finishedRecipe, recipeIdMapper.apply(parentItemLikeId));
        };
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenTrapDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenTrapDoorRecipeFrom(finishedRecipe, parentItemLike -> RegistryUtil.getObjectFromOrThrow(parentItemLike, RegistryUtil.replaceSuffix("trapdoor")), recipeIdMapper);
    }

    public static <IL extends ItemLike> Consumer<Supplier<IL>> woodenTrapDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenTrapDoorRecipeFrom(finishedRecipe, RegistryUtil.replaceSuffix("trapdoor"));
    }
}
