package com.mememan.nexus.util;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility {@code class} containing helpful recipe shortcut/delegator helper methods, as well as some re-used constants
 * related to recipes in general.
 * <br></br>
 * Conventionally, recipe utility methods generate recipes for provided parent objects, not the other way around.
 */
public final class RecipeUtil { //TODO Refactor tf out of this (tons of redundant overloads and whatnot)

    private RecipeUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (RecipeUtil)");
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromTag(Consumer<FinishedRecipe> finishedRecipe, TagKey<Item> componentTag, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            if (componentTag != null) {
                ResourceLocation componentTagIdPath = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentTag);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 4)
                        .requires(componentTag)
                        .group("planks")
                        .unlockedBy("has_any_" + componentTagIdPath.getPath(), PredicateUtil.has(componentTag))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromTag(Consumer<FinishedRecipe> finishedRecipe, TagKey<Item> componentTag) {
        return woodenPlanksRecipeFromTag(finishedRecipe, componentTag, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromComponent(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> planksComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = planksComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 4)
                        .requires(componentItemLike)
                        .group("planks")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId + "_from_" + componentItemLikeId.getPath());
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromLog(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenPlanksRecipeFromComponent(finishedRecipe, parentPlanks -> RegistryUtil.getObjectFrom(parentPlanks, parentPlanksId -> parentPlanksId.withPath(parentPlanksId.getPath().replace("_planks", "_log"))).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromLog(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenPlanksRecipeFromLog(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromStrippedLog(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenPlanksRecipeFromComponent(finishedRecipe, parentPlanks -> RegistryUtil.getObjectFrom(parentPlanks, parentPlanksId -> parentPlanksId.withPath(parentPlanksId.getPath().replace("_planks", "_log")).withPrefix("stripped_")).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromStrippedLog(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenPlanksRecipeFromStrippedLog(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromWood(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenPlanksRecipeFromComponent(finishedRecipe, parentPlanks -> RegistryUtil.getObjectFrom(parentPlanks, parentPlanksId -> parentPlanksId.withPath(parentPlanksId.getPath().replace("_planks", "_wood")))
                .or(() -> RegistryUtil.getObjectFrom(parentPlanks, parentPlanksId -> parentPlanksId.withPath(p -> p.replace("_planks", ""))))
                .orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromWood(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenPlanksRecipeFromWood(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPlanksRecipeFromComponents(Consumer<FinishedRecipe> finishedRecipe) { // Default to this unless the end-developer specifies a particular tag for their wood types via #woodenPlanksRecipeFromTag(...) (as such recipes should be)
        return parentItemLikeSup -> {
            Runnable componentRecipeGenerator = () -> {
                woodenPlanksRecipeFromLog(finishedRecipe).accept((Supplier<Block>) parentItemLikeSup);
                woodenPlanksRecipeFromStrippedLog(finishedRecipe).accept((Supplier<Block>) parentItemLikeSup);
                woodenPlanksRecipeFromWood(finishedRecipe).accept((Supplier<Block>) parentItemLikeSup);
            };

            PropertyWrapper.PropertyWrappersContainer.getWrapperFor(RegistryUtil.getSuppliedObjectFrom(parentItemLikeSup, parentItemLikeId -> parentItemLikeId.withPath(parentItemLikeId.getPath().replace("_planks", "_log"))).orElse(null))
                    .map(curPW -> (BlockPropertyWrapper<B>) curPW)
                    .ifPresentOrElse(curPW -> {
                        curPW.getAdditionalTags().stream()
                                .filter(curTagKey -> curTagKey.get().isFor(Registries.ITEM))
                                .map(curTagKey -> (TagKey<Item>) curTagKey.get())
                                .filter(curTagKey -> {
                                    String objRegName = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLikeSup.get()).getPath();

                                    return curTagKey.location().getPath().endsWith(objRegName.substring(0, objRegName.lastIndexOf('_')).concat("_logs"));
                                })
                                .findFirst()
                                .ifPresentOrElse(curTagKey -> woodenPlanksRecipeFromTag(finishedRecipe, curTagKey).accept((Supplier<Block>) parentItemLikeSup), componentRecipeGenerator);
                    }, componentRecipeGenerator);
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> woodComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = woodComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 3)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .pattern("##")
                        .group("bark")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodRecipeFrom(finishedRecipe, parentWood -> RegistryUtil.getObjectFrom(parentWood, parentWoodId -> parentWoodId.withPath(parentWoodId.getPath().replace("_wood", "_log")))
                .filter(wood -> !Objects.equals(wood, parentWood))
                .or(() -> RegistryUtil.getObjectFrom(parentWood, parentWoodId -> parentWoodId.withSuffix("_log")))
                .orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodRecipeFrom(finishedRecipe, Function.identity());
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
                        .group("wooden_slab")
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
                        .group("wooden_stairs")
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

    public static <B extends Block> Consumer<Supplier<B>> baseStoneFromCobbled(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> baseStoneComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B baseStoneItemLike = baseStoneComponentMapper.apply(parentItemLike);

            if (baseStoneItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(baseStoneItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                SimpleCookingRecipeBuilder.smelting(Ingredient.of(baseStoneItemLike), RecipeCategory.MISC, parentItemLike, 0.35F, 200)
                        .group(parentItemLikeId.getNamespace())
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(baseStoneItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_smelting_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> baseStoneFromCobbled(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return baseStoneFromCobbled(finishedRecipe, parentBaseStone -> RegistryUtil.getObjectFrom(parentBaseStone, parentBaseStoneId -> parentBaseStoneId.withPrefix("cobbled_")).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> baseStoneFromCobbled(Consumer<FinishedRecipe> finishedRecipe) {
        return baseStoneFromCobbled(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> bricksRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> bricksComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = bricksComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 4)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .pattern("##")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(componentItemLike), RecipeCategory.BUILDING_BLOCKS, parentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath() + "_stonecutting"));
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> bricksRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return bricksRecipeFrom(finishedRecipe, parentBricks -> RegistryUtil.getObjectFrom(parentBricks, parentBricksId -> parentBricksId.withPath(curPath -> curPath.replace("_bricks", "")))
                .or(() -> RegistryUtil.getObjectFrom(parentBricks, parentBricksId -> parentBricksId.withPath(curPath -> curPath.replace("_bricks", "_block"))))
                .orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> bricksRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return bricksRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> pillarRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> pillarComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = pillarComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 2)
                        .define('#', componentItemLike)
                        .pattern("#")
                        .pattern("#")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(componentItemLike), RecipeCategory.BUILDING_BLOCKS, parentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath() + "_stonecutting"));
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> pillarRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return pillarRecipeFrom(finishedRecipe, parentPillar -> RegistryUtil.getObjectFrom(parentPillar, parentBricksId -> parentBricksId.withPath(curPath -> curPath.replace("_pillar", "")))
                .or(() -> RegistryUtil.getObjectFrom(parentPillar, parentBricksId -> parentBricksId.withPath(curPath -> curPath.replace("_pillar", "_block"))))
                .orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> pillarRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return pillarRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromSlab(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> chiseledComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = chiseledComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike, 1)
                        .define('#', componentItemLike)
                        .pattern("#")
                        .pattern("#")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromSlab(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return chiseledRecipeFromSlab(finishedRecipe, parentChiseled -> RegistryUtil.getObjectFrom(parentChiseled, parentChiseledId -> parentChiseledId.withPath(curPath -> curPath.replace("chiseled_", "").concat("_brick_slab")))
                .or(() -> RegistryUtil.getObjectFrom(parentChiseled, parentChiseledId -> parentChiseledId.withPath(curPath -> StringUtil.firstToken(curPath.replace("chiseled_", "")).concat("_brick_slab"))))
                .orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromSlab(Consumer<FinishedRecipe> finishedRecipe) {
        return chiseledRecipeFromSlab(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromCobbledSlab(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return chiseledRecipeFromSlab(finishedRecipe, parentChiseled -> RegistryUtil.getObjectFrom(parentChiseled, parentChiseledId -> parentChiseledId.withPath(curPath -> curPath.replace("chiseled_", "cobbled_").concat("_slab")))
                .or(() -> RegistryUtil.getObjectFrom(parentChiseled, parentChiseledId -> parentChiseledId.withPath(curPath -> "cobbled_" + StringUtil.firstToken(curPath.replace("chiseled_", "")).concat("_slab"))))
                .orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromCobbledSlab(Consumer<FinishedRecipe> finishedRecipe) {
        return chiseledRecipeFromCobbledSlab(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromStoneCutting(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> chiseledComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = chiseledComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(componentItemLike), RecipeCategory.BUILDING_BLOCKS, parentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath() + "_stonecutting"));
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromStoneCutting(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> chiseledComponentMapper) {
        return chiseledRecipeFromStoneCutting(finishedRecipe, chiseledComponentMapper, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return parentItemLikeSup -> {
            chiseledRecipeFromSlab(finishedRecipe).accept((Supplier<Block>) parentItemLikeSup);

            boolean endsWithBricks = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLikeSup.get()).getPath().endsWith("_bricks");

            if (endsWithBricks) {
                chiseledRecipeFromStoneCutting(finishedRecipe, parentBlock -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "").replace("_bricks", "")))
                        .or(() -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "").replace("_bricks", "").concat("_block"))))
                        .orElse(null))
                        .accept((Supplier<Block>) parentItemLikeSup);
                chiseledRecipeFromStoneCutting(finishedRecipe, parentBlock -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "")))
                        .orElse(null))
                        .accept((Supplier<Block>) parentItemLikeSup);
            } else {
                chiseledRecipeFromStoneCutting(finishedRecipe, parentBlock -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "")))
                        .or(() -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "").concat("_block"))))
                        .orElse(null))
                        .accept((Supplier<Block>) parentItemLikeSup);
                chiseledRecipeFromStoneCutting(finishedRecipe, parentBlock -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "").concat("_bricks")))
                        .orElse(null))
                        .accept((Supplier<Block>) parentItemLikeSup);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> chiseledRecipeFromCobbled(Consumer<FinishedRecipe> finishedRecipe) {
        return parentItemLikeSup -> {
            chiseledRecipeFromCobbledSlab(finishedRecipe).accept((Supplier<Block>) parentItemLikeSup);

            boolean endsWithBricks = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLikeSup.get()).getPath().endsWith("_bricks");

            if (endsWithBricks) {
                chiseledRecipeFromStoneCutting(finishedRecipe, parentBlock -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "cobbled_").replace("_bricks", "")))
                        .or(() -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "cobbled_").replace("_bricks", "").concat("_block"))))
                        .orElse(null))
                        .accept((Supplier<Block>) parentItemLikeSup);
            } else {
                chiseledRecipeFromStoneCutting(finishedRecipe, parentBlock -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "cobbled_")))
                        .or(() -> RegistryUtil.getObjectFrom(parentBlock, parentBlockId -> parentBlockId.withPath(curPath -> curPath.replace("chiseled_", "cobbled_").concat("_block"))))
                        .orElse(null))
                        .accept((Supplier<Block>) parentItemLikeSup);
            }
        };
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

                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, parentItemLike)
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

    public static <B extends Block> Consumer<Supplier<B>> woodenDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> doorComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
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
                        .group("wooden_door")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenDoorRecipeFrom(finishedRecipe, parentDoor -> RegistryUtil.getObjectFrom(parentDoor, parentDoorId -> RegistryUtil.pickBlockId(() -> parentDoor)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenDoorRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenDoorRecipeFrom(finishedRecipe, Function.identity());
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
                        .group("wooden_trapdoor")
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
                        .group("wooden_fence")
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
                        .group("wooden_fence_gate")
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

    public static <B extends Block> Consumer<Supplier<B>> woodenButtonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> buttonComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = buttonComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, parentItemLike)
                        .requires(componentItemLike)
                        .group("wooden_button")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenButtonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenButtonRecipeFrom(finishedRecipe, parentButton -> RegistryUtil.getObjectFrom(parentButton, parentButtonId -> RegistryUtil.pickBlockId(() -> parentButton)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenButtonRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenButtonRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> pressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> pressurePlateComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = pressurePlateComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, parentItemLike)
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

    public static <B extends Block> Consumer<Supplier<B>> woodenPressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> pressurePlateComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = pressurePlateComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, parentItemLike)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .group("wooden_pressure_plate")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenPressurePlateRecipeFrom(finishedRecipe, parentPressurePlate -> RegistryUtil.getObjectFrom(parentPressurePlate, parentPressurePlateId -> RegistryUtil.pickBlockId(() -> parentPressurePlate)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenPressurePlateRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenPressurePlateRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenSignRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> woodenSignComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = woodenSignComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, parentItemLike, 3)
                        .define('#', componentItemLike)
                        .define('S', Items.STICK)
                        .pattern("###")
                        .pattern("###")
                        .pattern(" S ")
                        .group("wooden_sign")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenSignRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenSignRecipeFrom(finishedRecipe, parentWoodenSign -> RegistryUtil.getObjectFrom(parentWoodenSign, parentWoodenSignId -> RegistryUtil.pickBlockId(() -> parentWoodenSign)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenSignRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenSignRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenHangingSignRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> woodenHangingSignComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = woodenHangingSignComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, parentItemLike, 6)
                        .define('#', componentItemLike)
                        .define('C', Items.CHAIN)
                        .pattern("C C")
                        .pattern("###")
                        .pattern("###")
                        .group("hanging_sign")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenHangingSignRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenHangingSignRecipeFrom(finishedRecipe, parentHangingWoodenSign -> RegistryUtil.getObjectFrom(parentHangingWoodenSign, parentWoodenHangingSignId -> RegistryUtil.pickBlockId(() -> parentHangingWoodenSign)).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woodenHangingSignRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenHangingSignRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block> Consumer<Supplier<B>> woolCarpetRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, B> woolCarpetComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = woolCarpetComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, parentItemLike, 3)
                        .define('#', componentItemLike)
                        .pattern("##")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> woolCarpetRecipeFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woolCarpetRecipeFrom(finishedRecipe, parentWoolCarpet -> RegistryUtil.getObjectFrom(parentWoolCarpet, parentWoolCarpetId -> RegistryUtil.pickItemLikeId(() -> parentWoolCarpet, parentWoolCarpetPath -> parentWoolCarpetPath.replace("_carpet", "_wool"))).orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> woolCarpetRecipeFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woolCarpetRecipeFrom(finishedRecipe, Function.identity());
    }

    public static <B extends Block, I extends Item> Consumer<Supplier<B>> materialBlockFrom(Consumer<FinishedRecipe> finishedRecipe, Function<B, I> materialComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            B parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = materialComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, parentItemLike)
                        .define('#', componentItemLike)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId);
            }
        };
    }

    public static <B extends Block> Consumer<Supplier<B>> materialBlockFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return materialBlockFrom(finishedRecipe, parentBlock -> RegistryUtil.getObjectFrom(parentBlock.asItem(), parentBlockId -> RegistryUtil.pickMaterialIngotId(parentBlock::asItem))
                .or(() -> RegistryUtil.getObjectFrom(parentBlock.asItem(), parentBlockId -> RegistryUtil.pickMaterialId(parentBlock::asItem, "_lump")))
                .or(() -> RegistryUtil.getObjectFrom(parentBlock.asItem(), parentBlockId -> RegistryUtil.pickMaterialId(parentBlock::asItem, "_gem")))
                .or(() -> RegistryUtil.getObjectFrom(parentBlock.asItem(), parentBlockId -> RegistryUtil.pickMaterialId(parentBlock::asItem, "_crystal")))
                .or(() -> RegistryUtil.getObjectFrom(parentBlock.asItem(), parentBlockId -> RegistryUtil.pickMaterialId(parentBlock::asItem)))
                .orElse(null), recipeIdMapper);
    }

    public static <B extends Block> Consumer<Supplier<B>> materialBlockFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return materialBlockFrom(finishedRecipe, Function.identity());
    }

    public static <I extends Item, B extends Block> Consumer<Supplier<I>> materialFromOre(Consumer<FinishedRecipe> finishedRecipe, Function<I, B> materialComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = materialComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                B deepslateComponentItemLike = RegistryUtil.getObjectFrom(componentItemLike, parentComponentItemLikeId -> RegistryUtil.pickMaterialDeepslateOreId(() -> componentItemLike)).orElse(null);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                SimpleCookingRecipeBuilder.smelting(Ingredient.of(componentItemLike), RecipeCategory.MISC, parentItemLike, 0.35F, 200)
                        .group(parentItemLikeId.getNamespace())
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_smelting_" + componentItemLikeId.getPath()));

                SimpleCookingRecipeBuilder.blasting(Ingredient.of(componentItemLike), RecipeCategory.MISC, parentItemLike, 0.7F, 100)
                        .group(parentItemLikeId.getNamespace())
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_blasting_" + componentItemLikeId.getPath()));

                if (deepslateComponentItemLike != null) {
                    ResourceLocation deepslateComponentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(deepslateComponentItemLike);

                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(deepslateComponentItemLike), RecipeCategory.MISC, parentItemLike, 0.35F, 200)
                            .group(parentItemLikeId.getNamespace())
                            .unlockedBy("has_" + deepslateComponentItemLikeId.getPath(), PredicateUtil.has(deepslateComponentItemLike))
                            .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_smelting_" + deepslateComponentItemLikeId.getPath()));

                    SimpleCookingRecipeBuilder.blasting(Ingredient.of(deepslateComponentItemLike), RecipeCategory.MISC, parentItemLike, 0.7F, 100)
                            .group(parentItemLikeId.getNamespace())
                            .unlockedBy("has_" + deepslateComponentItemLikeId.getPath(), PredicateUtil.has(deepslateComponentItemLike))
                            .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_blasting_" + deepslateComponentItemLikeId.getPath()));
                }
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFromOre(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return materialFromOre(finishedRecipe, parentMaterial -> BuiltInRegistries.BLOCK.getOptional(RegistryUtil.pickMaterialOreId(() -> parentMaterial)).orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFromOre(Consumer<FinishedRecipe> finishedRecipe) {
        return materialFromOre(finishedRecipe, Function.identity());
    }

    public static <I extends Item, B extends Block> Consumer<Supplier<I>> materialFromBlock(Consumer<FinishedRecipe> finishedRecipe, Function<I, B> materialComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            B componentItemLike = materialComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, parentItemLike, 9)
                        .requires(componentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFromBlock(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return materialFromBlock(finishedRecipe, parentMaterial -> BuiltInRegistries.BLOCK.getOptional(RegistryUtil.pickMaterialBlockId(() -> parentMaterial)).orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFromBlock(Consumer<FinishedRecipe> finishedRecipe) {
        return materialFromBlock(finishedRecipe, Function.identity());
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFromNugget(Consumer<FinishedRecipe> finishedRecipe, Function<I, I> materialComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = materialComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, parentItemLike)
                        .define('#', componentItemLike)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFromNugget(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return materialFromNugget(finishedRecipe, parentMaterial -> RegistryUtil.getObjectFrom(parentMaterial, parentMaterialId -> RegistryUtil.pickMaterialNuggetId(() -> parentMaterial)).orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFromNugget(Consumer<FinishedRecipe> finishedRecipe) {
        return materialFromNugget(finishedRecipe, Function.identity());
    }

    public static <I extends Item> Consumer<Supplier<I>> materialFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return parentItemLikeSup -> {
            materialFromNugget(finishedRecipe).accept((Supplier<Item>) parentItemLikeSup);
            materialFromBlock(finishedRecipe).accept((Supplier<Item>) parentItemLikeSup);
            materialFromOre(finishedRecipe).accept((Supplier<Item>) parentItemLikeSup);
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> materialPieceFrom(Consumer<FinishedRecipe> finishedRecipe, Function<I, I> materialPieceComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = materialPieceComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, parentItemLike, 9)
                        .requires(componentItemLike)
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> materialPieceFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return materialPieceFrom(finishedRecipe, parentMaterialPiece -> RegistryUtil.getObjectFrom(parentMaterialPiece, parentMaterialPieceId -> RegistryUtil.pickMaterialId(() -> parentMaterialPiece))
                .or(() -> RegistryUtil.getObjectFrom(parentMaterialPiece, parentMaterialPieceId -> RegistryUtil.pickMaterialIngotId(() -> parentMaterialPiece)))
                .or(() -> RegistryUtil.getObjectFrom(parentMaterialPiece, parentMaterialPieceId -> RegistryUtil.pickMaterialId(() -> parentMaterialPiece, "_gem")))
                .or(() -> RegistryUtil.getObjectFrom(parentMaterialPiece, parentMaterialPieceId -> RegistryUtil.pickMaterialId(() -> parentMaterialPiece, "_crystal")))
                .orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> materialPieceFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return materialPieceFrom(finishedRecipe, Function.identity());
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromSmelting(Consumer<FinishedRecipe> finishedRecipe, Function<I, I> cookedFoodComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = cookedFoodComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                SimpleCookingRecipeBuilder.smelting(Ingredient.of(componentItemLike), RecipeCategory.FOOD, parentItemLike, 0.35F, 200)
                        .group(parentItemLikeId.getNamespace())
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_smelting_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromSmelting(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return cookedFoodFromSmelting(finishedRecipe, parentCookedFood -> RegistryUtil.getObjectFrom(parentCookedFood, parentCookedFoodId -> parentCookedFoodId.withPath(parentCookedFoodId.getPath().replace("cooked_", "raw_")))
                .or(() -> RegistryUtil.getObjectFrom(parentCookedFood, parentCookedFoodId -> parentCookedFoodId.withPath(parentCookedFoodId.getPath().replace("cooked_", ""))))
                .orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromSmelting(Consumer<FinishedRecipe> finishedRecipe) {
        return cookedFoodFromSmelting(finishedRecipe, Function.identity());
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromSmoking(Consumer<FinishedRecipe> finishedRecipe, Function<I, I> cookedFoodComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = cookedFoodComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                SimpleCookingRecipeBuilder.smoking(Ingredient.of(componentItemLike), RecipeCategory.FOOD, parentItemLike, 0.35F, 100)
                        .group(parentItemLikeId.getNamespace())
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_smoking_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromSmoking(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return cookedFoodFromSmoking(finishedRecipe, parentCookedFood -> RegistryUtil.getObjectFrom(parentCookedFood, parentCookedFoodId -> parentCookedFoodId.withPath(parentCookedFoodId.getPath().replace("cooked_", "raw_")))
                .or(() -> RegistryUtil.getObjectFrom(parentCookedFood, parentCookedFoodId -> parentCookedFoodId.withPath(parentCookedFoodId.getPath().replace("cooked_", ""))))
                .orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromSmoking(Consumer<FinishedRecipe> finishedRecipe) {
        return cookedFoodFromSmoking(finishedRecipe, Function.identity());
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromCampfireCooking(Consumer<FinishedRecipe> finishedRecipe, Function<I, I> cookedFoodComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = cookedFoodComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(componentItemLike), RecipeCategory.FOOD, parentItemLike, 0.35F, 600)
                        .group(parentItemLikeId.getNamespace())
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_campfire_cooking_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromCampfireCooking(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return cookedFoodFromCampfireCooking(finishedRecipe, parentCookedFood -> RegistryUtil.getObjectFrom(parentCookedFood, parentCookedFoodId -> parentCookedFoodId.withPath(parentCookedFoodId.getPath().replace("cooked_", "raw_")))
                .or(() -> RegistryUtil.getObjectFrom(parentCookedFood, parentCookedFoodId -> parentCookedFoodId.withPath(parentCookedFoodId.getPath().replace("cooked_", ""))))
                .orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFromCampfireCooking(Consumer<FinishedRecipe> finishedRecipe) {
        return cookedFoodFromCampfireCooking(finishedRecipe, Function.identity());
    }

    public static <I extends Item> Consumer<Supplier<I>> cookedFoodFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return parentItemLikeSup -> {
            cookedFoodFromSmelting(finishedRecipe).accept((Supplier<Item>) parentItemLikeSup);
            cookedFoodFromSmoking(finishedRecipe).accept((Supplier<Item>) parentItemLikeSup);
            cookedFoodFromCampfireCooking(finishedRecipe).accept((Supplier<Item>) parentItemLikeSup);
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> woodenBoatFrom(Consumer<FinishedRecipe> finishedRecipe, Function<I, I> woodenBoatComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = woodenBoatComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, parentItemLike)
                        .define('#', componentItemLike)
                        .pattern("# #")
                        .pattern("###")
                        .group("boat")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> woodenBoatFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenBoatFrom(finishedRecipe, parentWoodenBoat -> RegistryUtil.getObjectFrom(parentWoodenBoat, parentWoodenBoatId -> parentWoodenBoatId.withPath(curPath -> StringUtil.subLastToken(curPath).concat("_planks"))).orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> woodenBoatFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenBoatFrom(finishedRecipe, Function.identity());
    }

    public static <I extends Item> Consumer<Supplier<I>> woodenChestBoatFrom(Consumer<FinishedRecipe> finishedRecipe, Function<I, I> woodenChestBoatComponentMapper, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            I componentItemLike = woodenChestBoatComponentMapper.apply(parentItemLike);

            if (componentItemLike != null) {
                ResourceLocation componentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(componentItemLike);
                ResourceLocation baseRecipeId = recipeIdMapper.apply(parentItemLikeId);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, parentItemLike)
                        .requires(componentItemLike)
                        .requires(Items.CHEST)
                        .group("chest_boat")
                        .unlockedBy("has_" + componentItemLikeId.getPath(), PredicateUtil.has(componentItemLike))
                        .save(finishedRecipe, baseRecipeId.withPath(baseRecipeId.getPath() + "_from_" + componentItemLikeId.getPath()));
            }
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> woodenChestBoatFrom(Consumer<FinishedRecipe> finishedRecipe, Function<ResourceLocation, ResourceLocation> recipeIdMapper) {
        return woodenChestBoatFrom(finishedRecipe, parentWoodenChestBoat -> RegistryUtil.getObjectFrom(parentWoodenChestBoat, parentWoodenBoatId -> parentWoodenBoatId.withPath(curPath -> curPath.replace("_chest_boat", "_boat"))).orElse(null), recipeIdMapper);
    }

    public static <I extends Item> Consumer<Supplier<I>> woodenChestBoatFrom(Consumer<FinishedRecipe> finishedRecipe) {
        return woodenChestBoatFrom(finishedRecipe, Function.identity());
    }
}
