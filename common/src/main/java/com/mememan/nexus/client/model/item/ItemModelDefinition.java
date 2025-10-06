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
    protected final Map<Map<ResourceLocation, Float>, ResourceLocation> modelTextureOverrides = new Object2ObjectLinkedOpenHashMap<>();

    public ItemModelDefinition(@NotNull ModelTemplate parentModel) {
        super(parentModel, "item");
    }

    /**
     * Defines a custom texture override for the item model. The key represents a {@link Map} of predicates (fulfilled
     * based on the criteria of the value's... well, value) and the value represents a {@link ResourceLocation}
     * representing the item model to delegate to if said predicate(s) is/are fulfilled.
     * <br></br>
     * An entry would generally be structured as:
     * <br>
     * K: {@code Map.of(new ResourceLocation("mymodid:my_predicate_location"), 1.0F, ...)},
     * <p></p>
     * V: {@code new ResourceLocation("mymodid:my_conditional_texture_location")}.
     *
     * @param textureOverridePredicates The custom map of texture overrides for the item model.
     * @param textureOverride The custom map of texture overrides for the item model.
     *
     * @return {@code this} (builder method)
     *
     * @see #withItemModelTextureOverrides(Map)
     * @see #setItemModelTextureOverrides(Map)
     * @see #getItemModelTextureOverrides()
     */
    public ItemModelDefinition withItemModelTextureOverride(Map<ResourceLocation, Float> textureOverridePredicates, ResourceLocation textureOverride) {
        this.modelTextureOverrides.put(textureOverridePredicates, textureOverride);
        return this;
    }

    /**
     * Defines a custom {@link Map} of texture overrides for the item model. The key represents a {@link Map} of
     * predicates (fulfilled based on the criteria of the value's... well, value) per
     * {@link ResourceLocation} representing the item model to delegate to if said predicate(s) is/are fulfilled.
     * <br></br>
     * An entry would generally be structured as:
     * <br>
     * K: {@code Map.of(new ResourceLocation("mymodid:my_predicate_location"), 1.0F, ...)},
     * <p></p>
     * V: {@code new ResourceLocation("mymodid:my_conditional_texture_location")}.
     *
     * @param textureOverrides The custom {@link Map} of texture overrides for the item model.
     *
     * @return {@code this} (builder method)
     *
     * @see #withItemModelTextureOverride(Map, ResourceLocation)
     * @see #setItemModelTextureOverrides(Map)
     * @see #getItemModelTextureOverrides()
     */
    public ItemModelDefinition withItemModelTextureOverrides(Map<Map<ResourceLocation, Float>, ResourceLocation> textureOverrides) {
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
     * @see #withItemModelTextureOverride(Map, ResourceLocation)
     * @see #withItemModelTextureOverrides(Map)
     * @see #getItemModelTextureOverrides()
     */
    public ItemModelDefinition setItemModelTextureOverrides(Map<Map<ResourceLocation, Float>, ResourceLocation> textureOverrides) {
        this.modelTextureOverrides.clear();
        this.modelTextureOverrides.putAll(textureOverrides);
        return this;
    }

    /**
     * Gets the custom map of texture overrides for the item model. Texture overrides are applied based on the
     * {@link Map} of predicate values they share.
     *
     * @return The texture overrides {@link Map}. May be empty.
     *
     * @see #withItemModelTextureOverride(Map, ResourceLocation)
     */
    public Map<Map<ResourceLocation, Float>, ResourceLocation> getItemModelTextureOverrides() {
        return modelTextureOverrides;
    }

    @Override
    public JsonObject constructJson(ResourceLocation finalizedModelLoc, Supplier<JsonElement> baseModelJson) {
        JsonObject modifiedSuperModelJson = super.constructJson(finalizedModelLoc, baseModelJson);

        if (!modelTextureOverrides.isEmpty()) {
            JsonArray modelTextureOverridesArray = new JsonArray();

            modelTextureOverrides.forEach((overrideConditions, overrideTextureLoc) -> {
                JsonObject rootOverridesObj = new JsonObject();

                if (!overrideConditions.isEmpty()) {
                    overrideConditions.forEach((predicateLoc, predicateValue) -> {
                        JsonObject predicateObj = new JsonObject();

                        if (predicateLoc != null && predicateValue != null) predicateObj.addProperty(predicateLoc.toString(), predicateValue);

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
