package com.mememan.nexus.util;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
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
        return slabRecipeFrom(finishedRecipe, parentSlab -> RegistryUtil.getObjectFrom(parentSlab, parentSlabId -> RegistryUtil.pickBlockId(() -> parentSlab)).get(), recipeIdMapper);
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
        return woodenSlabRecipeFrom(finishedRecipe, parentSlab -> RegistryUtil.getObjectFrom(parentSlab, parentSlabId -> RegistryUtil.pickBlockId(() -> parentSlab)).get(), recipeIdMapper);
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
        return stairsRecipeFrom(finishedRecipe, parentStairs -> RegistryUtil.getObjectFrom(parentStairs, parentStairsId -> RegistryUtil.pickBlockId(() -> parentStairs)).get(), recipeIdMapper);
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
        return woodenStairsRecipeFrom(finishedRecipe, parentStairs -> RegistryUtil.getObjectFrom(parentStairs, parentStairsId -> RegistryUtil.pickBlockId(() -> parentStairs)).get(), recipeIdMapper);
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
        return wallRecipeFrom(finishedRecipe, parentWall -> RegistryUtil.getObjectFrom(parentWall, parentWallId -> RegistryUtil.pickBlockId(() -> parentWall)).get(), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> wallRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return wallRecipeFrom(finishedRecipe, Function.identity());
    }
}
