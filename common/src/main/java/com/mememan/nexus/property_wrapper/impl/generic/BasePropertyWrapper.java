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
    @Nullable
    protected BUILDER builder;

    public BasePropertyWrapper(Supplier<T> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        this.parentObject = isTemplate || parentObject == null ? Suppliers.ofInstance(null) : parentObject;
        this.isTemplate = isTemplate;
        this.builderFactory = builderFactory;
    }

    public BasePropertyWrapper(@NotNull Supplier<T> parentObject, Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        this(parentObject, false, builderFactory);
    }

    public BasePropertyWrapper(Function<SELF, PropertyWrapperBuilder<T, BUILDER, SELF>> builderFactory) {
        this(null, false, builderFactory);
    }

    @Override
    public @NotNull Supplier<T> getParentObject() {
        return parentObject;
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

    /**
     * Used to create a new builder instance for this {@link PropertyWrapper} instance. Uses {@link #builderFactory} to
     * create the builder.
     *
     * @return A new builder instance for this {@link PropertyWrapper} instance.
     *
     * @apiNote Typecasting {@code BUILDER} is handled appropriately in other builder methods. This method is of base
     * type {@link PropertyWrapperBuilder} to allow for convenient return statements without having to typecast at every
     * turn.
     */
    @NotNull
    public PropertyWrapperBuilder<T, BUILDER, SELF> constructBuilder() {
        return builderFactory.apply((SELF) this);
    }

    @Override
    public boolean isTemplate() {
        return isTemplate;
    }
}
