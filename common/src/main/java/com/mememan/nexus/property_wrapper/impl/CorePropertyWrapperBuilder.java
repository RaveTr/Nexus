package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.PropertyWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Core implementation of Property Wrapper Builders that uses composition to attach properties from specialised property
 * wrappers.
 *
 * @see CorePropertyWrapper
 */
public class CorePropertyWrapperBuilder<T, SELF extends CorePropertyWrapperBuilder<T, SELF, CPW>, CPW extends CorePropertyWrapper<T, CPW, SELF>> extends BasePropertyWrapperBuilder<T, SELF, CPW> {

    public CorePropertyWrapperBuilder(@NotNull PropertyWrapper<T, CPW, SELF> ownerWrapper) {
        super(ownerWrapper);
    }

    //TODO Composition because maintainability


}
