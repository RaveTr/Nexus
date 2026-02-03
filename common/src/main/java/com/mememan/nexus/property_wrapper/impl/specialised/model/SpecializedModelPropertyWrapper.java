package com.mememan.nexus.property_wrapper.impl.specialised.model;

import com.mememan.nexus.property_wrapper.base.specialised.model.ModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized implementation of {@link ModelBasedPropertyWrapper}. Implements all model-related getter methods,
 * generic types, and default behaviour for model-based property wrapper handling.
 *
 * @see SpecializedModelPropertyWrapperBuilder
 */
public class SpecializedModelPropertyWrapper<T, SELF extends ModelBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedModelPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements ModelBasedPropertyWrapper<T, SELF, BUILDER> {

    public SpecializedModelPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, SpecializedModelPropertyWrapperBuilder::new, modId);
    }

    public SpecializedModelPropertyWrapper(@NotNull Supplier<T> parentObject, @NotNull String modId) {
        super(parentObject, SpecializedModelPropertyWrapperBuilder::new, modId);
    }

    public SpecializedModelPropertyWrapper() {
        super(SpecializedModelPropertyWrapperBuilder::new);
    }

    @Override
    public Optional<Function<Supplier<T>, ModelDefinition>> getModelDefinition() {
        return rawBuilder().map(b -> b.modelDefinitionsMapper);
    }
}
