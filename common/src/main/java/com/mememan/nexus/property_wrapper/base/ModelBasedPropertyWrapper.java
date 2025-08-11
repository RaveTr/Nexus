package com.mememan.nexus.property_wrapper.base;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards handling models for the object being wrapped.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link ModelBasedPropertyWrapperBuilder}.
 *
 * @see ModelBasedPropertyWrapperBuilder
 */
public interface ModelBasedPropertyWrapper<T, SELF extends ModelBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends ModelBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends DataGenPropertyWrapper<T, SELF, BUILDER> {

    /**
     * Gets the {@link List} of model definitions for the object being wrapped, represented as a {@link Function} taking
     * the parent object as input and returning a {@link ModelDefinition}.
     *
     * @return  A {@link List} of model definitions for the object being wrapped.
     *
     * @see #getGroupedModelDefinitions()
     * @see ModelBasedPropertyWrapperBuilder#withModelDefinition(Function)
     */
    List<Function<T, ModelDefinition>> getModelDefinitions();

    /**
     * Gets the {@link List} of model definitions for the object being wrapped, represented as a {@link Function} taking
     * the parent object as input and returning a {@link List} of {@linkplain ModelDefinition ModelDefinitions}
     * associated with the parent object.
     *
     * @return  A {@link List} of model definitions for the object being wrapped.
     *
     * @see #getModelDefinitions()
     * @see ModelBasedPropertyWrapperBuilder#withModelDefinitions(Function)
     */
    List<Function<T, List<ModelDefinition>>> getGroupedModelDefinitions();

    /**
     * Nested data {@code interface} representing models for different types of objects.
     * <br></br>
     * Different implementations for different object types may contain additional data used to properly generate
     * an object type's model (like how blocks should also have item models, thus storing item model definitions inside
     * their own block model definitions).
     */
    interface ModelDefinition {

        /**
         * Gets the parent model to use as a template for this definition's model.
         *
         * @return The parent model.
         */
        @NotNull
        ModelTemplate getParentModel();

        /**
         * Gets the texture mapping to use for this definition's model (i.e. the texture locations mapped to each
         * {@link TextureSlot} required by the parent model).
         *
         * @return The texture mapping to use for this definition's model, influenced by the parent model.
         */
        @NotNull
        TextureMapping getTextureMapping();

        /**
         * Gets the custom model name to use for this definition's model file name.
         *
         * @return The custom model name. May be empty.
         */
        Optional<String> getCustomModelName();

        /**
         * Gets whether this model definition should exude ambient occlusion.
         *
         * @return Whether this model definition should exude ambient occlusion.
         */
        boolean hasAmbientOcclusion();
    }
}
