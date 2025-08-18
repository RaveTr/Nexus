package com.mememan.nexus.property_wrapper.impl.generic;

import com.google.common.base.Suppliers;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base class for all Property Wrappers, with base implementations from {@code interface}.
 *
 * @see BasePropertyWrapperBuilder
 */
public class BasePropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> implements PropertyWrapper<T, SELF, BUILDER> {
    protected final Supplier<T> parentObject;
    protected final boolean isTemplate;
    @Nullable
    protected BUILDER builder;

    public BasePropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        this.parentObject = isTemplate || parentObject == null ? Suppliers.ofInstance(null) : parentObject;
        this.isTemplate = isTemplate;
    }

    public BasePropertyWrapper(@NotNull Supplier<T> parentObject) {
        this(parentObject, false);
    }

    public BasePropertyWrapper() {
        this(null, false);
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
     * Used to create a new builder instance for this {@link PropertyWrapper} instance.
     *
     * @return A new builder instance for this {@link PropertyWrapper} instance.
     *
     * @apiNote Typecasting {@code BUILDER} is handled appropriately in other builder methods. This method is of base
     * type {@link PropertyWrapperBuilder} to allow for convenient return statements without having to typecast at every
     * turn.
     * @implNote Only needed if your property wrapper requires a specified builder implementation type.
     */
    @NotNull
    public PropertyWrapperBuilder<T, BUILDER, SELF> constructBuilder() {
        return new BasePropertyWrapperBuilder<>(this);
    }

    @Override
    public boolean isTemplate() {
        return isTemplate;
    }
}
