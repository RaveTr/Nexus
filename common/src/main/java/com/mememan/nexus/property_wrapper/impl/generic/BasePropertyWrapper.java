package com.mememan.nexus.property_wrapper.impl.generic;

import com.google.common.base.Suppliers;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class for all Property Wrappers, with base implementations from {@code interface}.
 *
 * @see BasePropertyWrapperBuilder
 */
public class BasePropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> implements PropertyWrapper<T, SELF, BUILDER> {
    protected final Supplier<T> parentObject;
    protected final boolean isTemplate;
    protected final Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory; // Output can't be BUILDER cuz generic type invariance
    protected final Optional<String> modId;
    @Nullable
    protected BUILDER builder;

    public BasePropertyWrapper(Supplier<T> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory, String modId) {
        this.parentObject = isTemplate || parentObject == null ? Suppliers.ofInstance(null) : parentObject;
        this.isTemplate = isTemplate;
        this.builderFactory = builderFactory;
        this.modId = Optional.ofNullable(modId);
    }

    public BasePropertyWrapper(@NotNull Supplier<T> parentObject, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory, String modId) {
        this(parentObject, false, builderFactory, modId);
    }

    public BasePropertyWrapper(Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        this(null, true, builderFactory, null);
    }

    public BasePropertyWrapper(Supplier<T> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        this(parentObject, isTemplate, builderFactory, null);
    }

    public BasePropertyWrapper(@NotNull Supplier<T> parentObject, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        this(parentObject, false, builderFactory);
    }

    public BasePropertyWrapper() {
        this(null, true, BasePropertyWrapperBuilder::new);
    }

    @Override
    public @NotNull Supplier<T> getParentObject() {
        return parentObject;
    }

    @Override
    public Optional<String> getModId() {
        return isTemplate() ? Optional.empty() : modId;
    }

    @Override
    public BUILDER builder(boolean overrideExistingBuilder) {
        return overrideExistingBuilder || builder == null
                ? builder = (BUILDER) constructBuilder()
                : builder;
    }

    @Override
    public Optional<BUILDER> rawBuilder() {
        return Optional.ofNullable(builder);
    }

    @Override
    public @NotNull PropertyWrapperBuilder<T, BUILDER, SELF> constructBuilder() {
        return builderFactory.apply((SELF) this);
    }

    @Override
    public boolean isTemplate() {
        return isTemplate;
    }
}
