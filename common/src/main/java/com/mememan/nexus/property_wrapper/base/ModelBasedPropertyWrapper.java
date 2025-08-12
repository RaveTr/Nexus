package com.mememan.nexus.property_wrapper.base;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
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
     * @return A model definition for the object being wrapped. May be empty.
     *
     * @see ModelBasedPropertyWrapperBuilder#withModelDefinition(Function)
     */
    Optional<Function<T, ModelDefinition>> getModelDefinition();

    /**
     * Nested data {@code interface} representing models for different types of objects.
     * <br></br>
     * Different implementations for different object types may contain additional data used to properly generate
     * an object type's model (like how blocks should also have item models, thus storing item model definitions inside
     * their own block model definitions).
     */
    interface ModelDefinition {

        /**
         * Defines another model definition to be used after this one as a child/contained definition.
         *
         * @param modelDefinition The model definition to add and apply after this one.
         *
         * @return {@code this} (builder method)
         *
         * @see #withOrdinalModelDefinitions(List)
         * @see #withOrdinalModelDefinitions(ModelDefinition...)
         */
        ModelDefinition withOrdinalModelDefinition(ModelDefinition modelDefinition);

        /**
         * Defines a list of model definitions to be used after this one as children/contained definitions.
         *
         * @param modelDefinitions The model definitions to add and apply after this one.
         *
         * @return {@code this} (builder method)
         *
         * @see #withOrdinalModelDefinition(ModelDefinition)
         * @see #withOrdinalModelDefinitions(ModelDefinition...)
         */
        ModelDefinition withOrdinalModelDefinitions(List<ModelDefinition> modelDefinitions);

        /**
         * Overloaded variant of {@link #withOrdinalModelDefinitions(List)}. Defines an array of model definitions to be
         * used after this one as children/contained definitions.
         *
         * @param modelDefinition The model definitions to add and apply after this one.
         *
         * @return {@code this} (builder method)
         *
         * @see #withOrdinalModelDefinition(ModelDefinition)
         * @see #withOrdinalModelDefinitions(List)
         */
        default ModelDefinition withOrdinalModelDefinitions(ModelDefinition... modelDefinition) {
            return withOrdinalModelDefinitions(ObjectArrayList.of(modelDefinition));
        }

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

        /**
         * A {@link List} of all contained model definitions within this definition. Does not traverse down contained
         * definitions' children.
         *
         * @return A {@link List} of all contained model definitions within this definition.
         *
         * @see #getFlattenedModelDefinitions()
         */
        List<ModelDefinition> getOrdinalModelDefinitions();

        /**
         * Gets a flattened view of all contained model definitions within this definition and their children, all the
         * way down the logical hierarchy.
         *
         * @return A flattened list of all contained model definitions.
         *
         * @see #getOrdinalModelDefinitions()
         */
        default List<ModelDefinition> getFlattenedModelDefinitions() {
            if (getOrdinalModelDefinitions().isEmpty()) return ObjectArrayList.of();

            List<ModelDefinition> flattenedDefinitions = ObjectArrayList.of();
            Queue<ModelDefinition> toProcess = new LinkedList<>(getOrdinalModelDefinitions());

            while (!toProcess.isEmpty()) { // Recursively flatten definitions all the way down
                ModelDefinition next = toProcess.poll();
                flattenedDefinitions.add(next);

                if (!next.getOrdinalModelDefinitions().isEmpty()) {
                    toProcess.addAll(next.getOrdinalModelDefinitions());
                }
            }

            return flattenedDefinitions;
        }
    }
}
