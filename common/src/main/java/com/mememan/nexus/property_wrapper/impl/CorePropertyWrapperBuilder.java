package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Core implementation of Property Wrapper Builders that uses composition to attach properties from specialised property
 * wrappers. Utilises composition to attach several specialised builders rather than implementing them directly in order
 * to minimize tech debt and improve maintainability.
 *
 * @see CorePropertyWrapper
 */
public class CorePropertyWrapperBuilder<T, SELF extends CorePropertyWrapperBuilder<T, SELF, CPW>, CPW extends CorePropertyWrapper<T, CPW, SELF>> extends BasePropertyWrapperBuilder<T, SELF, CPW> {
    protected final Set<PropertyWrapperBuilder<T, ?, ?>> configuredBuilders = ObjectOpenHashSet.of();

    public CorePropertyWrapperBuilder(@NotNull PropertyWrapper<T, CPW, SELF> ownerWrapper) {
        super(ownerWrapper);
    }

    public SELF withBuilder(PropertyWrapperBuilder<T, ?, ?> builder) {
        configuredBuilders.add(builder);
        return self();
    }
}
