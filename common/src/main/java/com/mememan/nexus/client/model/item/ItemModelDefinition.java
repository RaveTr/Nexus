package com.mememan.nexus.client.model.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mememan.nexus.client.model.general.BaseModelDefinition;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Item-oriented variant of {@link BaseModelDefinition}, whose backing directory is set to "item". Adds support for
 * custom model overrides.
 */
public class ItemModelDefinition extends BaseModelDefinition<ItemModelDefinition> {
    protected final Map<ResourceLocation, Map<String, Float>> modelTextureOverrides = new Object2ObjectLinkedOpenHashMap<>();

    public ItemModelDefinition(@NotNull ModelTemplate parentModel) {
        super(parentModel, "item");
    }

    /**
     * Defines a custom texture override for the item model. The key represents a {@link ResourceLocation} pointing to
     * the item model to use when the predicates are fulfilled, and the value represents a {@link Map} of predicates
     * (fulfilled based on the criteria of the value's... well, value).
     * <br></br>
     * An entry would generally be structured as:
     * <br>
     * K: {@code new ResourceLocation("mymodid:my_conditional_texture_location")}.
     * <p></p>
     * V: {@code Map.of(new ResourceLocation("mymodid:my_predicate_location").toString(), 1.0F, ...)}, <- Predicate ID doesn't necessarily have to be a {@link ResourceLocation}.
     *
     * @param textureOverride The {@link ResourceLocation} of the item model to delegate to if the predicates are fulfilled.
     * @param textureOverridePredicates The {@link Map} of predicates that must be fulfilled for this override to apply.
     *
     * @return {@code this} (builder method)
     *
     * @see #withItemModelTextureOverrides(Map)
     * @see #setItemModelTextureOverrides(Map)
     * @see #getItemModelTextureOverrides()
     */
    public ItemModelDefinition withItemModelTextureOverride(ResourceLocation textureOverride, Map<String, Float> textureOverridePredicates) {
        this.modelTextureOverrides.put(textureOverride, textureOverridePredicates);
        return this;
    }

    /**
     * Defines a custom {@link Map} of texture overrides for the item model. The key is a
     * {@link ResourceLocation} representing the item model to delegate to, and the value is a {@link Map} of
     * predicates that must be fulfilled for said override to apply.
     * <br></br>
     * An entry would generally be structured as:
     * <br>
     * K: {@code new ResourceLocation("mymodid:my_conditional_texture_location")}.
     * <p></p>
     * V: {@code Map.of(new ResourceLocation("mymodid:my_predicate_location").toString(), 1.0F, ...)}, <- Predicate ID doesn't necessarily have to be a {@link ResourceLocation}.
     *
     * @param textureOverrides The custom {@link Map} of texture overrides for the item model.
     *
     * @return {@code this} (builder method)
     *
     * @see #withItemModelTextureOverride(ResourceLocation, Map)
     * @see #setItemModelTextureOverrides(Map)
     * @see #getItemModelTextureOverrides()
     */
    public ItemModelDefinition withItemModelTextureOverrides(Map<ResourceLocation, Map<String, Float>> textureOverrides) {
        this.modelTextureOverrides.putAll(textureOverrides);
        return this;
    }

    /**
     * Sets the texture override {@link Map} for this item model definition (see references below).
     *
     * @param textureOverrides The custom {@link Map} of texture overrides for the item model.
     *
     * @return {@code this} (builder method)
     *
     * @see #withItemModelTextureOverride(ResourceLocation, Map)
     * @see #withItemModelTextureOverrides(Map)
     * @see #getItemModelTextureOverrides()
     */
    public ItemModelDefinition setItemModelTextureOverrides(Map<ResourceLocation, Map<String, Float>> textureOverrides) {
        this.modelTextureOverrides.clear();
        this.modelTextureOverrides.putAll(textureOverrides);
        return this;
    }

    /**
     * Gets the custom map of texture overrides for the item model. The key is a {@link ResourceLocation} pointing to
     * the item model to delegate to, and its value is the {@link Map} of predicates that must be fulfilled.
     *
     * @return The texture overrides {@link Map}. May be empty.
     *
     * @see #withItemModelTextureOverride(ResourceLocation, Map)
     */
    public Map<ResourceLocation, Map<String, Float>> getItemModelTextureOverrides() {
        return modelTextureOverrides;
    }

    @Override
    public JsonObject constructJson(ResourceLocation finalizedModelLoc, Supplier<JsonElement> baseModelJson) {
        JsonObject modifiedSuperModelJson = super.constructJson(finalizedModelLoc, baseModelJson);

        if (!modelTextureOverrides.isEmpty()) {
            JsonArray modelTextureOverridesArray = new JsonArray();

            modelTextureOverrides.forEach((overrideTextureLoc, overrideConditions) -> {
                JsonObject rootOverridesObj = new JsonObject();

                if (!overrideConditions.isEmpty()) {
                    overrideConditions.forEach((predicateId, predicateValue) -> {
                        JsonObject predicateObj = new JsonObject();

                        if (predicateId != null && predicateValue != null) predicateObj.addProperty(predicateId, predicateValue);

                        rootOverridesObj.add("predicate", predicateObj);
                    });
                }

                if (overrideTextureLoc != null) rootOverridesObj.addProperty("model", overrideTextureLoc.toString());

                modelTextureOverridesArray.add(rootOverridesObj);
            });

            modifiedSuperModelJson.add("overrides", modelTextureOverridesArray);
        }

        return modifiedSuperModelJson;
    }
}
