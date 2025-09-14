package com.mememan.nexus.datagen;

import com.mememan.nexus.datagen.standard.StandardDatapackRegistryProvider;
import com.mememan.nexus.datagen.standard.StandardLanguageProvider;
import com.mememan.nexus.datagen.standard.StandardRecipeProvider;
import com.mememan.nexus.datagen.standard.loot.StandardLootProvider;
import com.mememan.nexus.datagen.standard.model.StandardModelProvider;
import com.mememan.nexus.datagen.standard.tag.StandardTagProvider;
import com.mememan.nexus.loader.ModSide;
import org.jetbrains.annotations.NotNull;

/**
 * Basic hardcoded object-holder {@code enum} that represents natively-supported data providers offered by Nexus API.
 * <br></br>
 * Should be used by dependant mods where appropriate in order to allow for fine control over data being generated under
 * their namespaces by Nexus API.
 */
public enum NexusProviderTypes implements ProviderType {
    /**
     * Represents the {@linkplain StandardDatapackRegistryProvider providers} responsible for generating all datapack
     * registry files.
     */
    DYNAMIC_REGISTRY_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@linkplain StandardLanguageProvider providers} types responsible for generating localization files
     * for objects.
     */
    LANGUAGE_PROVIDER(ModSide.CLIENT),
    /**
     * Represents the {@linkplain StandardLootProvider providers} responsible for running all loot sub-providers and
     * generating loot table data. Configurations applied to this type take precedence over sub-providers.
     */
    LOOT_TABLE_PROVIDER(ModSide.SERVER),
    /**
     * Represents the {@linkplain StandardModelProvider providers} responsible for generating all types of model JSONs.
     */
    MODEL_PROVIDER(ModSide.CLIENT),
    /**
     * Represents the general {@linkplain StandardRecipeProvider providers} responsible for generating both block and
     * item (as well as any other object type) recipes.
     */
    RECIPE_PROVIDER(ModSide.SERVER),
    /**
     * Represents the general {@linkplain StandardTagProvider providers} responsible for generating all types of tags.
     */
    TAG_PROVIDER(ModSide.SERVER),
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
