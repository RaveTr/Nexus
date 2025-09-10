package com.mememan.nexus.datagen;

import com.mememan.nexus.datagen.standard.StandardLanguageProvider;
import com.mememan.nexus.loader.ModSide;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.ModelProvider;
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
     * Represents the {@link RegistriesDatapackGenerator} responsible for generating all datapack registry files.
     */
    DYNAMIC_REGISTRY_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link StandardLanguageProvider} types responsible for generating localization files for objects.
     */
    LANGUAGE_PROVIDER(ModSide.CLIENT),
    /**
     * Represents the {@link LootTableProvider} responsible for running all loot sub-providers and generating loot table
     * data. Configurations applied to this type take precedence over sub-providers.
     */
    LOOT_TABLE_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@link ModelProvider} responsible for generating all types of model JSONs (including blockstates).
     */
    MODEL_PROVIDER(ModSide.CLIENT),
    /**
     * Represents the general {@link RecipeProvider} responsible for generating both block and item recipes.
     */
    RECIPE_PROVIDER(ModSide.SERVER),
    ;

    private final ModSide targetGenSide;

    NexusProviderTypes(ModSide targetGenSide) {
        this.targetGenSide = targetGenSide;
    }

    @Override
    public @NotNull ModSide getSide() {
        return targetGenSide;
    }
}
