package com.mememan.nexus.property_wrapper.impl;

import com.google.common.base.Suppliers;
import com.mememan.nexus.property_wrapper.base.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.PropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Core implementation of Property Wrappers that uses composition to attach properties from specialised property wrappers.
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
}
