package com.mememan.nexus.datagen;

import com.mememan.nexus.datagen.standard.StandardLanguageProvider;
import com.mememan.nexus.loader.ModSide;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Basic hardcoded object-holder {@code enum} that represents natively-supported data providers offered by Nexus API.
 * <br></br>
 * Should be used by dependant mods where appropriate in order to allow for fine control over data being generated under
 * their namespaces by Nexus API.
 */
public enum NexusProviderTypes implements ProviderType {
    /**
     * Represents the {@link LootTableProvider.SubProviderEntry} responsible for generating block loot tables.
     */
    BLOCK_LOOT_SUB_PROVIDER(ModSide.SERVER),
    /**
     * Represents {@link BlockModelGenerators} specifically responsible for generating block models.
     */
    BLOCK_MODEL_PROVIDER(ModSide.CLIENT),
    /**
     * Represents {@link BlockModelGenerators} specifically responsible for generating block states.
     */
    BLOCK_STATE_PROVIDER(ModSide.CLIENT),
    /**
     * Represents the {@link IntrinsicHolderTagsProvider} responsible for generating block tags.
     */
    BLOCK_TAGS_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link TagsProvider} responsible for generating damage type tags.
     */
    DAMAGE_TYPE_TAGS_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link RegistriesDatapackGenerator} responsible for generating all datapack registry files.
     */
    DYNAMIC_REGISTRY_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link IntrinsicHolderTagsProvider} responsible for generating enchantment tags.
     */
    ENCHANTMENT_TAGS_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link LootTableProvider.SubProviderEntry} responsible for generating entity type loot tables.
     */
    ENTITY_TYPE_LOOT_SUB_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link IntrinsicHolderTagsProvider} responsible for generating entity type tags.
     */
    ENTITY_TYPE_TAGS_PROVIDER(ModSide.SERVER),
    /**
     * Represents {@link ItemModelGenerators} specifically responsible for generating item models.
     */
    ITEM_MODEL_PROVIDER(ModSide.CLIENT),
    /**
     * Represents the {@link IntrinsicHolderTagsProvider} responsible for generating item tags.
     */
    ITEM_TAGS_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link StandardLanguageProvider} types responsible for generating localization files for objects.
     */
    LANGUAGE_PROVIDER(ModSide.CLIENT),
    /**
     * Represents the {@link LootTableProvider} responsible for running all loot sub-providers and generating loot table
     * data. Configurations applied to this type take precedence over sub-providers'.
     */
    LOOT_TABLE_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link IntrinsicHolderTagsProvider} responsible for generating mob effect tags.
     */
    MOB_EFFECT_TAGS_PROVIDER(ModSide.SERVER),
    /**
     * Represents the general {@link RecipeProvider} responsible for generating both block and item recipes.
     */
    RECIPE_PROVIDER(ModSide.SERVER),
    ;

    private final ModSide targetGenSide;

    NexusProviderTypes(ModSide targetGenSide) {
        this.targetGenSide = targetGenSide;
    }

    /**
     * Whether this instance is a block-related data provider.
     *
     * @return Whether this instance is a block-related data provider.
     */
    public boolean isBlockProvider() {
        return this == BLOCK_LOOT_SUB_PROVIDER || this == BLOCK_MODEL_PROVIDER || this == BLOCK_STATE_PROVIDER || this == BLOCK_TAGS_PROVIDER;
    }

    /**
     * Whether this instance is an item-related data provider.
     *
     * @return Whether this instance is an item-related data provider.
     */
    public boolean isItemProvider() {
        return this == ITEM_MODEL_PROVIDER || this == ITEM_TAGS_PROVIDER;
    }

    /**
     * Whether this instance is an entity type-related data provider.
     *
     * @return Whether this instance is an entity type-related data provider.
     */
    public boolean isEntityTypeProvider() {
        return this == ENTITY_TYPE_LOOT_SUB_PROVIDER || this == ENTITY_TYPE_TAGS_PROVIDER;
    }

    /**
     * Whether this instance is a tag-related data provider.
     *
     * @return Whether this instance is a tag-related data provider.
     */
    public boolean isTagProvider() {
        return this == BLOCK_TAGS_PROVIDER || this == ENTITY_TYPE_TAGS_PROVIDER || this == ITEM_TAGS_PROVIDER;
    }

    /**
     * Whether this instance is a model-related data provider.
     *
     * @return Whether this instance is a model-related data provider.
     */
    public boolean isModelProvider() {
        return this == BLOCK_MODEL_PROVIDER || this == BLOCK_STATE_PROVIDER || this == ITEM_MODEL_PROVIDER;
    }

    @Override
    public @NotNull ModSide getSide() {
        return targetGenSide;
    }
}
