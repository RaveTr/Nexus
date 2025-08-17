package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.PropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Base implementation for Property Wrapper Builders, implementing {@link PropertyWrapperBuilder}.
 *
 * @see BasePropertyWrapper
 */
public class BasePropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, PW>, PW extends PropertyWrapper<T, PW, SELF>> implements PropertyWrapperBuilder<T, SELF, PW> {
    @NotNull
    protected final PW ownerWrapper;

    public BasePropertyWrapperBuilder(@NotNull PropertyWrapper<T, PW, SELF> ownerWrapper) {
        this.ownerWrapper = (PW) ownerWrapper;
    }

    @Override
    public SELF copyFrom(PW propertyWrapper) {
        return self();
    }

    @Override
    public PW build() {
        return PropertyWrapper.PropertyWrappersContainer.registerPropertyWrapper(ownerWrapper.getParentObject(), ownerWrapper);
    }

    @Override
    public @NotNull PW getCurrentOwnerWrapper() {
        return ownerWrapper;
    }

    @Override
    public SELF clone() {
        try {
            SELF self = (SELF) super.clone();

            return self.copyFrom(ownerWrapper);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
