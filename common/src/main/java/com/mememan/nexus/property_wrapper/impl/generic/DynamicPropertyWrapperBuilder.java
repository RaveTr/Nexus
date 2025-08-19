package com.mememan.nexus.property_wrapper.impl.generic;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public class DynamicPropertyWrapperBuilder<T, SELF extends DataGenPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW>, DPW extends DataGenPropertyWrapper<ResourceKey<T>, DPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<ResourceKey<T>, SELF, DPW> {

    public DynamicPropertyWrapperBuilder(@NotNull DPW ownerWrapper) {
        super(ownerWrapper);
    }
}
