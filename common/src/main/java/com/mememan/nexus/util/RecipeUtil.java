package com.mememan.nexus.util;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility {@code class} containing helpful recipe shortcut/delegator helper methods, as well as some re-used constants
 * related to recipes in general.
 * <br></br>
 * Conventionally, recipe utility methods generate recipes from provided parent objects, not the other way around.
 */
public final class RecipeUtil {

    private RecipeUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (RecipeUtil)");
    }

    public static <B extends Block> Consumer<Supplier<B>> slabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> slabComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);
                    
            B componentItemLike = slabComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 6)
                        .define('#', componentItemLike)
                        .pattern("###")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(componentItemLike), RecipeCategory.BUILDING_BLOCKS, parentItemLike, 2)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath() + "_stonecutting"));
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> slabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return slabRecipeFrom(finishedRecipe, parentSlab -> RegistryUtil.getObjectFrom(parentSlab, parentSlabId -> RegistryUtil.pickBlockId(() -> parentSlab)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> slabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return slabRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenSlabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> slabComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = slabComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 6)
                        .define('#', componentItemLike)
                        .pattern("###")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenSlabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenSlabRecipeFrom(finishedRecipe, parentSlab -> RegistryUtil.getObjectFrom(parentSlab, parentSlabId -> RegistryUtil.pickBlockId(() -> parentSlab)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenSlabRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenSlabRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> stairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> stairsComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = stairsComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 4)
                        .define('#', componentItemLike)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(componentItemLike), RecipeCategory.BUILDING_BLOCKS, parentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath() + "_stonecutting"));
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> stairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return stairsRecipeFrom(finishedRecipe, parentStairs -> RegistryUtil.getObjectFrom(parentStairs, parentStairsId -> RegistryUtil.pickBlockId(() -> parentStairs)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> stairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return stairsRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenStairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> stairsComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = stairsComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 6)
                        .define('#', componentItemLike)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenStairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenStairsRecipeFrom(finishedRecipe, parentStairs -> RegistryUtil.getObjectFrom(parentStairs, parentStairsId -> RegistryUtil.pickBlockId(() -> parentStairs)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenStairsRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenStairsRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> wallRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> wallComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = wallComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 6)
                        .define('#', componentItemLike)
                        .pattern("###")
                        .pattern("###")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(componentItemLike), RecipeCategory.BUILDING_BLOCKS, parentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath() + "_stonecutting"));
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> wallRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return wallRecipeFrom(finishedRecipe, parentWall -> RegistryUtil.getObjectFrom(parentWall, parentWallId -> RegistryUtil.pickBlockId(() -> parentWall)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> wallRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return wallRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> doorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> doorComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = doorComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, parentItemLike, 3)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .pattern("##")
                        .pattern("##")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> doorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return doorRecipeFrom(finishedRecipe, parentDoor -> RegistryUtil.getObjectFrom(parentDoor, parentDoorId -> RegistryUtil.pickBlockId(() -> parentDoor)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> doorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return doorRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> trapdoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> trapdoorComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = trapdoorComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, parentItemLike)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .pattern("##")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> trapdoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return trapdoorRecipeFrom(finishedRecipe, parentTrapdoor -> RegistryUtil.getObjectFrom(parentTrapdoor, parentTrapdoorId -> RegistryUtil.pickBlockId(() -> parentTrapdoor)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> trapdoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return trapdoorRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenTrapdoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> woodenTrapdoorComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = woodenTrapdoorComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, parentItemLike, 2)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .pattern("##")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenTrapdoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenTrapdoorRecipeFrom(finishedRecipe, parentWoodenTrapdoor -> RegistryUtil.getObjectFrom(parentWoodenTrapdoor, parentWoodenTrapdoorId -> RegistryUtil.pickBlockId(() -> parentWoodenTrapdoor)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenTrapdoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenTrapdoorRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> fenceRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> fenceComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = fenceComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, parentItemLike, 3)
                        .define('#', componentItemLike)
                        .define('S', Items.STICK)
                        .pattern("#S#")
                        .pattern("#S#")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> fenceRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return fenceRecipeFrom(finishedRecipe, parentFence -> RegistryUtil.getObjectFrom(parentFence, parentFenceId -> RegistryUtil.pickBlockId(() -> parentFence)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> fenceRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return fenceRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> fenceGateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> fenceGateComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = fenceGateComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, parentItemLike)
                        .define('#', componentItemLike)
                        .define('S', Items.STICK)
                        .pattern("S#S")
                        .pattern("S#S")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> fenceGateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return fenceGateRecipeFrom(finishedRecipe, parentFenceGate -> RegistryUtil.getObjectFrom(parentFenceGate, parentFenceGateId -> RegistryUtil.pickBlockId(() -> parentFenceGate)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> fenceGateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return fenceGateRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> buttonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> buttonComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = buttonComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, parentItemLike)
                        .requires(componentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> buttonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return buttonRecipeFrom(finishedRecipe, parentButton -> RegistryUtil.getObjectFrom(parentButton, parentButtonId -> RegistryUtil.pickBlockId(() -> parentButton)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> buttonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return buttonRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> pressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> pressurePlateComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = pressurePlateComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, parentItemLike)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> pressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return pressurePlateRecipeFrom(finishedRecipe, parentPressurePlate -> RegistryUtil.getObjectFrom(parentPressurePlate, parentPressurePlateId -> RegistryUtil.pickBlockId(() -> parentPressurePlate)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> pressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return pressurePlateRecipeFrom(finishedRecipe, Function.identity());
    }
}
