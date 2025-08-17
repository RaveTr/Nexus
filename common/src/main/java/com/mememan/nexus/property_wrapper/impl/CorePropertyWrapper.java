package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Core implementation of Property Wrappers that uses composition to attach properties from specialised property wrappers.
 * <br></br>
 * Useful for selectively composing a builder with several different PWB types. Each builder passed into here is
 * defensively cloned, stored, and re-built to ensure proper registration and segregation between value mutation.
 * <br></br>
 * Can be extended for specialization. This base {@code class} handles all general cases.
 *
 * @see PropertyWrapper
 * @see CorePropertyWrapperBuilder
 */
public class CorePropertyWrapper<T, SELF extends CorePropertyWrapper<T, SELF, BUILDER>, BUILDER extends CorePropertyWrapperBuilder<T, BUILDER, SELF>> extends BasePropertyWrapper<T, SELF, BUILDER> {

    public CorePropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        super(parentObject, isTemplate);
    }

    public CorePropertyWrapper(@NotNull Supplier<T> parentObject) {
        super(parentObject);
    }

    public CorePropertyWrapper() {
        super();
    }

    public Set<PropertyWrapperBuilder<T, ?, ?>> getConfiguredBuilders() {
        return rawBuilder().map(b -> b.configuredBuilders).orElse(ObjectOpenHashSet.of());
    }
}
