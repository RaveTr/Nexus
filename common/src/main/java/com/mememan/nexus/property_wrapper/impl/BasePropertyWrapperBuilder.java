package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.PropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Base implementation for Property Wrapper Builders, implementing {@link PropertyWrapperBuilder}.
 */
public class BasePropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, PW>, PW extends PropertyWrapper<T, PW, SELF>> implements PropertyWrapperBuilder<T, SELF, PW> {
    @NotNull
    protected final PW ownerWrapper;

    public BasePropertyWrapperBuilder(@NotNull PropertyWrapper<T, PW, SELF> ownerWrapper) {
        this.ownerWrapper = (PW) ownerWrapper;
    }

    @Override
    public PropertyWrapperBuilder<T, SELF, PW> copyFrom(PW propertyWrapper) {
        return this;
    }

    @Override
    public PW build() {
        return PropertyWrapper.PropertyWrappersContainer.registerPropertyWrapper(ownerWrapper.getParentObject(), ownerWrapper);
    }
}
