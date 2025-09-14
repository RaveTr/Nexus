package com.mememan.nexus.property_wrapper.base.generic;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Extension of {@link PropertyWrapper} that provides additional getters for Nexus data generation.
 * <br></br>
 * Note that this {@code interface} only provides generic getters for handling general cases, such as excluding this
 * PW from Nexus data generation, or manipulating how different {@linkplain ProviderType ProviderTypes} interact with
 * it.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link DataGenPropertyWrapperBuilder}.
 *
 * @see DataGenPropertyWrapperBuilder
 */
public interface DataGenPropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> extends PropertyWrapper<T, SELF, BUILDER> {

    /**
     * Whether this DGPW instance should be excluded from Nexus data generation entirely.
     * <br></br>
     * This takes precedence over {@link #getProviderTypeRequisites()} in determining whether a DGPW instance contain
     *
     * @return Whether this DGPW instance should be excluded from Nexus data generation entirely.
     *
     * @see #getProviderTypeRequisites()
     * @see DataGenPropertyWrapperBuilder#excludeFromNativeDatagen(boolean)
     */
    boolean isExcludedFromDataGen();

    /**
     * Gets a {@link Map} (usually {@link Object2BooleanOpenHashMap}) specifying the {@linkplain ProviderType ProviderTypes}
     * for which this DGPW instance requires data to present for generation. May be empty if {@link #builder} is {@code null}
     * or the underlying {@link Map} is also empty.
     * <br></br>
     * This method also takes precedence over {@link ModDataProvider#validateAllEntries()}.
     *
     * @return The {@link Map} representing different {@linkplain ProviderType ProviderTypes} and their requirements for
     * datagen. May be empty.
     *
     * @see #isExcludedFromDataGen()
     * @see DataGenPropertyWrapperBuilder#requiresDatagenEntry(ProviderType, boolean)
     */
    Map<ProviderType, Boolean> getProviderTypeRequisites();

    /**
     * Gets the description ID for the object being wrapped. Used for the object's key during automatic localization,
     * logging, and general identification.
     *
     * @return The description ID for the object being wrapped, or "Template " concatenated with the {@code class} name
     * if the object is a template.
     */
    @NotNull
    default String getObjectDescriptionId() {
        return isTemplate()
                ? "Template ".concat(getClass().getSimpleName())
                : getParentObject().get() instanceof ItemLike ilParent
                ? ilParent.asItem().getDescriptionId()
                : getParentObject().get().toString();
    }

    /**
     * Optional definition of a registry key for the object being wrapped. This is primarily used during data generation
     * for some provider types (such as loot table and tag providers) to properly discover objects' locations and types,
     * and map them out appropriately.
     *
     * @return An {@link Optional} containing the registry key for the object being wrapped, or an empty {@link Optional}
     * if the object is a template. The registry key itself may be unmapped if the object has no pertaining registries
     * at all.
     *
     * @see #isTemplate()
     * @see RegistryLookupContainer#computeForObject(Object)
     */
    default Optional<ResourceKey<Registry<? super T>>> getObjectRegistryKey() {
        return isTemplate() ? Optional.empty() : RegistryLookupContainer.computeForObject(getParentObject().get());
    }

    /**
     * Container {@code class} for registry key lookups, primarily used to determine which registry a given PW's parent
     * object belongs to (if any).
     */
    class RegistryLookupContainer {
        public static final ResourceKey<? extends Registry<? super Object>> UNMAPPED_REGISTRY = ResourceKey.createRegistryKey(NexusConstants.prefix("unknown"));
        private static final Map<Class<?>, ResourceKey<? extends Registry<?>>> NATIVE_REGISTRY_KEY_LOOKUP = Util.make(new Object2ObjectOpenHashMap<>(), regKeyMap -> {
            regKeyMap.put(Activity.class, Registries.ACTIVITY);
            regKeyMap.put(Attribute.class, Registries.ATTRIBUTE);
            regKeyMap.put(BannerPattern.class, Registries.BANNER_PATTERN);
            regKeyMap.put(BiomeSource.class, Registries.BIOME_SOURCE);
            regKeyMap.put(Block.class, Registries.BLOCK);
            regKeyMap.put(BlockEntityType.class, Registries.BLOCK_ENTITY_TYPE);
            regKeyMap.put(BlockPredicateType.class, Registries.BLOCK_PREDICATE_TYPE);
            regKeyMap.put(BlockStateProviderType.class, Registries.BLOCK_STATE_PROVIDER_TYPE);
            regKeyMap.put(WorldCarver.class, Registries.CARVER);
            regKeyMap.put(CatVariant.class, Registries.CAT_VARIANT);
            regKeyMap.put(ChunkGenerator.class, Registries.CHUNK_GENERATOR);
            regKeyMap.put(ChunkStatus.class, Registries.CHUNK_STATUS);
            regKeyMap.put(ArgumentTypeInfo.class, Registries.COMMAND_ARGUMENT_TYPE);
            regKeyMap.put(CreativeModeTab.class, Registries.CREATIVE_MODE_TAB);
            regKeyMap.put(ResourceLocation.class, Registries.CUSTOM_STAT);
            regKeyMap.put(DamageType.class, Registries.DAMAGE_TYPE);
            regKeyMap.put(DensityFunction.class, Registries.DENSITY_FUNCTION_TYPE);
            regKeyMap.put(Enchantment.class, Registries.ENCHANTMENT);
            regKeyMap.put(EntityType.class, Registries.ENTITY_TYPE);
            regKeyMap.put(Feature.class, Registries.FEATURE);
            regKeyMap.put(FeatureSizeType.class, Registries.FEATURE_SIZE_TYPE);
            regKeyMap.put(FloatProviderType.class, Registries.FLOAT_PROVIDER_TYPE);
            regKeyMap.put(Fluid.class, Registries.FLUID);
            regKeyMap.put(FoliagePlacerType.class, Registries.FOLIAGE_PLACER_TYPE);
            regKeyMap.put(FrogVariant.class, Registries.FROG_VARIANT);
            regKeyMap.put(GameEvent.class, Registries.GAME_EVENT);
            regKeyMap.put(HeightProviderType.class, Registries.HEIGHT_PROVIDER_TYPE);
            regKeyMap.put(Instrument.class, Registries.INSTRUMENT);
            regKeyMap.put(IntProviderType.class, Registries.INT_PROVIDER_TYPE);
            regKeyMap.put(Item.class, Registries.ITEM);
            regKeyMap.put(LootItemConditionType.class, Registries.LOOT_CONDITION_TYPE);
            regKeyMap.put(LootItemFunctionType.class, Registries.LOOT_FUNCTION_TYPE);
            regKeyMap.put(LootNbtProviderType.class, Registries.LOOT_NBT_PROVIDER_TYPE);
            regKeyMap.put(LootNumberProviderType.class, Registries.LOOT_NUMBER_PROVIDER_TYPE);
            regKeyMap.put(LootPoolEntryType.class, Registries.LOOT_POOL_ENTRY_TYPE);
            regKeyMap.put(LootScoreProviderType.class, Registries.LOOT_SCORE_PROVIDER_TYPE);
            regKeyMap.put(SurfaceRules.ConditionSource.class, Registries.MATERIAL_CONDITION);
            regKeyMap.put(SurfaceRules.RuleSource.class, Registries.MATERIAL_RULE);
            regKeyMap.put(MemoryModuleType.class, Registries.MEMORY_MODULE_TYPE);
            regKeyMap.put(MenuType.class, Registries.MENU);
            regKeyMap.put(MobEffect.class, Registries.MOB_EFFECT);
            regKeyMap.put(PaintingVariant.class, Registries.PAINTING_VARIANT);
            regKeyMap.put(ParticleType.class, Registries.PARTICLE_TYPE);
            regKeyMap.put(PlacementModifierType.class, Registries.PLACEMENT_MODIFIER_TYPE);
            regKeyMap.put(PoiType.class, Registries.POINT_OF_INTEREST_TYPE);
            regKeyMap.put(PositionSourceType.class, Registries.POSITION_SOURCE_TYPE);
            regKeyMap.put(PosRuleTestType.class, Registries.POS_RULE_TEST);
            regKeyMap.put(Potion.class, Registries.POTION);
            regKeyMap.put(RecipeSerializer.class, Registries.RECIPE_SERIALIZER);
            regKeyMap.put(RecipeType.class, Registries.RECIPE_TYPE);
            regKeyMap.put(RootPlacerType.class, Registries.ROOT_PLACER_TYPE);
            regKeyMap.put(RuleTestType.class, Registries.RULE_TEST);
            regKeyMap.put(RuleBlockEntityModifierType.class, Registries.RULE_BLOCK_ENTITY_MODIFIER);
            regKeyMap.put(Schedule.class, Registries.SCHEDULE);
            regKeyMap.put(SensorType.class, Registries.SENSOR_TYPE);
            regKeyMap.put(SoundEvent.class, Registries.SOUND_EVENT);
            regKeyMap.put(StatType.class, Registries.STAT_TYPE);
            regKeyMap.put(StructurePieceType.class, Registries.STRUCTURE_PIECE);
            regKeyMap.put(StructurePlacementType.class, Registries.STRUCTURE_PLACEMENT);
            regKeyMap.put(StructurePoolElementType.class, Registries.STRUCTURE_POOL_ELEMENT);
            regKeyMap.put(StructureProcessorType.class, Registries.STRUCTURE_PROCESSOR);
            regKeyMap.put(StructureType.class, Registries.STRUCTURE_TYPE);
            regKeyMap.put(TreeDecoratorType.class, Registries.TREE_DECORATOR_TYPE);
            regKeyMap.put(TrunkPlacerType.class, Registries.TRUNK_PLACER_TYPE);
            regKeyMap.put(VillagerProfession.class, Registries.VILLAGER_PROFESSION);
            regKeyMap.put(VillagerType.class, Registries.VILLAGER_TYPE);
            regKeyMap.put(String.class, Registries.DECORATED_POT_PATTERNS);
            regKeyMap.put(Biome.class, Registries.BIOME);
            regKeyMap.put(ChatType.class, Registries.CHAT_TYPE);
            regKeyMap.put(ConfiguredWorldCarver.class, Registries.CONFIGURED_CARVER);
            regKeyMap.put(ConfiguredFeature.class, Registries.CONFIGURED_FEATURE);
            regKeyMap.put(DimensionType.class, Registries.DIMENSION_TYPE);
            regKeyMap.put(FlatLevelGeneratorPreset.class, Registries.FLAT_LEVEL_GENERATOR_PRESET);
            regKeyMap.put(NoiseGeneratorSettings.class, Registries.NOISE_SETTINGS);
            regKeyMap.put(NormalNoise.NoiseParameters.class, Registries.NOISE);
            regKeyMap.put(PlacedFeature.class, Registries.PLACED_FEATURE);
            regKeyMap.put(Structure.class, Registries.STRUCTURE);
            regKeyMap.put(StructureProcessorList.class, Registries.PROCESSOR_LIST);
            regKeyMap.put(StructureSet.class, Registries.STRUCTURE_SET);
            regKeyMap.put(StructureTemplatePool.class, Registries.TEMPLATE_POOL);
            regKeyMap.put(TrimMaterial.class, Registries.TRIM_MATERIAL);
            regKeyMap.put(TrimPattern.class, Registries.TRIM_PATTERN);
            regKeyMap.put(WorldPreset.class, Registries.WORLD_PRESET);
            regKeyMap.put(MultiNoiseBiomeSourceParameterList.class, Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
            regKeyMap.put(Level.class, Registries.DIMENSION);
            regKeyMap.put(LevelStem.class, Registries.LEVEL_STEM);
        });

        private RegistryLookupContainer() {
            throw new IllegalAccessError("Attempted to construct instance of container class! (RegistryLookupContainer)");
        }

        /**
         * {@code static} helper method for getting around Java's generic type invariance by typecasting to some generic
         * {@code T} pertaining to the parent object type.
         * <br></br>
         * Intended for use with registry resource keys. This will fail to typecast otherwise.
         *
         * @param registryKey The wildcard {@link ResourceKey} to typecast.
         *
         * @return The {@link Optional} containing the type-casted {@link ResourceKey}. May be empty based on the
         * provided {@code registryKey}.
         *
         * @param <T> The type of the parent object of the registry resource key.
         *
         * @see Registries
         * @see #computeForObject(Object)
         */
        public static <T> Optional<ResourceKey<Registry<? super T>>> ofRegistryKey(ResourceKey<?> registryKey) {
            return Optional.ofNullable((ResourceKey<Registry<? super T>>) registryKey);
        }

        /**
         * Attempts to compute a registry {@link ResourceKey} for the provided {@code targetObj}.
         * <br></br>
         * First attempts to find a registry key for the provided {@code targetObj} in the base
         * {@link #NATIVE_REGISTRY_KEY_LOOKUP} {@link Map}. If no exact match is found, then a value is looked up from
         * the same {@link Map} and returned based on whether any of the base types are assignable from the provided
         * {@code targetObj} (i.e. {@link Class#isAssignableFrom(Class)}).
         * <br></br>
         * If no match is found, it is assumed that the registry may be a custom one added by a mod. In that case,
         * {@link BuiltInRegistries#REGISTRY} is used for registry lookup based on element type for each element within
         * each (appropriate) registry. Finally, if no match is found at all, {@link #UNMAPPED_REGISTRY} is mapped to the
         * provided object's {@code class} to indicate that it has no registry {@link ResourceKey}.
         *
         * @param targetObj The object to use as base for registry key lookup.
         *
         * @return An {@link Optional} containing the registry {@link ResourceKey} for the provided object. If no
         * registry {@link ResourceKey} is found, an {@link Optional} containing {@link #UNMAPPED_REGISTRY} is returned.
         * If the provided {@code targetObj} is {@code null}, an empty {@link Optional} is returned.
         *
         * @param <T> The parent object type.
         *
         * @apiNote This method has time complexity between O(1) - O(k + m + n), where it's O(1) if the registry key is
         * already cached, and O(k + m + n) if it has to traverse all the way down to try and find any corresponding
         * registry resource keys - where k is the number of entries in {@link #NATIVE_REGISTRY_KEY_LOOKUP}, m is the
         * number of entries (post-filter) in {@link BuiltInRegistries#REGISTRY}, and n is the number of flattened
         * elements per registry (m). Oftentimes better than O(k + m + n) as long as the registry resource key being
         * looked up actually exists, and only O(k + m + n) on first lookup otherwise.
         *
         * @see #ofRegistryKey(ResourceKey)
         */
        public static <T> Optional<ResourceKey<Registry<? super T>>> computeForObject(T targetObj) {
            return targetObj == null ? Optional.empty() : ofRegistryKey(NATIVE_REGISTRY_KEY_LOOKUP.computeIfAbsent(targetObj.getClass(), objClazz -> NATIVE_REGISTRY_KEY_LOOKUP.entrySet().stream()
                    .filter(regEntry -> regEntry.getKey().isAssignableFrom(objClazz))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .flatMap(RegistryLookupContainer::ofRegistryKey)
                    .orElseGet(() -> BuiltInRegistries.REGISTRY.entrySet().stream()
                            .filter(curRegEntry -> !NATIVE_REGISTRY_KEY_LOOKUP.containsValue(curRegEntry.getKey()))
                            .flatMap(curRegEntry -> curRegEntry.getValue().entrySet().stream())
                            .filter(regEntry -> regEntry.getValue().getClass().isAssignableFrom(objClazz))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .flatMap(RegistryLookupContainer::ofRegistryKey)
                            .orElse((ResourceKey<Registry<? super Object>>) UNMAPPED_REGISTRY))));
        }

        /**
         * Attempts to find the literal {@link Registry} associated with the provided object's type by querying
         * {@link #NATIVE_REGISTRY_KEY_LOOKUP} via {@link #computeForObject(Object)}, then using the resultant
         * registry {@link ResourceKey} (if found) to find the literal {@link Registry} via {@link BuiltInRegistries#REGISTRY}.
         *
         * @param targetObj The object to use as base for registry lookup.
         *
         * @return An {@link Optional} containing the literal {@link Registry} associated with the provided object's type,
         * or {@link Optional#empty()} if the provided object is {@code null}.
         *
         * @throws IllegalArgumentException If the provided object is not associable with any registry {@link ResourceKey}.
         *
         * @param <T> The parent object type.
         *
         * @see #computeForObject(Object)
         * @see BuiltInRegistries#REGISTRY
         */
        public static <T> Optional<Registry<T>> getRegistryForObject(T targetObj) {
            return targetObj == null
                    ? Optional.empty()
                    : Optional.ofNullable(((Registry<T>) BuiltInRegistries.REGISTRY.get(computeForObject(targetObj) // Registries are ultimately backed by and stored in a HashMap, which should average O(1) time complexity if present
                    .filter(regResourceKey -> !regResourceKey.location().equals(RegistryLookupContainer.UNMAPPED_REGISTRY.location()))
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to find registry resource key for non-registry object of type %s: %s", targetObj.getClass().getSimpleName(), targetObj))).location())));
        }

        /**
         * Attempts to map the provided {@code targetObj} with a {@linkplain ResourceLocation registry identifier key}
         * based on its type.
         * <br></br>
         * If the provided {@code targetObj} is a {@link ResourceKey}, then it is directly returned. If it is a
         * {@link TagKey}, then {@link TagKey#location()} is returned.
         * <br></br>
         * Otherwise, a lookup is performed via {@link #getRegistryForObject(Object)} to attempt to find the registry for
         * the provided {@code targetObj}, with {@link Registry#getResourceKey(Object)} being used to find the
         * {@link ResourceLocation} of the provided {@code targetObj}.
         *
         * @param targetObj The parent object type whose key should be looked up.
         *
         * @return An {@link Optional} containing the {@link ResourceLocation} of the provided {@code targetObj}, or
         * {@link Optional#empty()} if the provided {@code targetObj} is {@code null}.
         *
         * @throws IllegalArgumentException If the provided {@code targetObj} is not associable with any {@link Registry}.
         *
         * @param <T> The parent object type.
         */
        public static <T> Optional<ResourceLocation> getObjectRegistryId(T targetObj) {
            return targetObj instanceof ResourceKey<?>
                    ? Optional.of(((ResourceKey<T>) targetObj).location())
                    : targetObj instanceof TagKey<?>
                    ? Optional.of(((TagKey<T>) targetObj).location())
                    : getRegistryForObject(targetObj)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to find registry for unregistered or unmapped object of type %s: %s", targetObj.getClass().getSimpleName(), targetObj)))
                    .getResourceKey(targetObj)
                    .map(ResourceKey::location);
        }
    }
}
