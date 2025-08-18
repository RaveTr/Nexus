package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;

public interface VanillaBasedPropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, VBPW>, VBPW extends PropertyWrapper<T, VBPW, SELF>> extends PropertyWrapperBuilder<T, SELF, VBPW> {


}
