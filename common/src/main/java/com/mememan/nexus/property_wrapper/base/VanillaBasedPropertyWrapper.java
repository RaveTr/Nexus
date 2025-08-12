package com.mememan.nexus.property_wrapper.base;

public interface VanillaBasedPropertyWrapper<T, SELF extends VanillaBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends VanillaBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends PropertyWrapper<T, SELF, BUILDER> {
}
