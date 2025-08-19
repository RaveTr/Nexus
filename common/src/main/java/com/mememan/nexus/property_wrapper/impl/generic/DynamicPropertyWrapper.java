package com.mememan.nexus.property_wrapper.impl.generic;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
public class DynamicPropertyWrapper<T, SELF extends DataGenPropertyWrapper<ResourceKey<T>, SELF, BUILDER>, BUILDER extends DynamicPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<ResourceKey<T>, SELF, BUILDER> {

    public DynamicPropertyWrapper(Supplier<ResourceKey<T>> parentObject, boolean isTemplate, Function<SELF, PropertyWrapperBuilder<ResourceKey<T>, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, isTemplate, builderFactory, modId);
    }

    public DynamicPropertyWrapper(@NotNull Supplier<ResourceKey<T>> parentObject, Function<SELF, PropertyWrapperBuilder<ResourceKey<T>, BUILDER, SELF>> builderFactory, String modId) {
        super(parentObject, builderFactory, modId);
    }

    public DynamicPropertyWrapper(Function<SELF, PropertyWrapperBuilder<ResourceKey<T>, BUILDER, SELF>> builderFactory) {
        super(builderFactory);
    }

    public DynamicPropertyWrapper(Supplier<ResourceKey<T>> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, DynamicPropertyWrapperBuilder<T, BUILDER, SELF>::new, modId);
    }

    public DynamicPropertyWrapper(@NotNull Supplier<ResourceKey<T>> parentObject, String modId) {
        super(parentObject, DynamicPropertyWrapperBuilder<T, BUILDER, SELF>::new, modId);
    }

    public DynamicPropertyWrapper() {
        super(DynamicPropertyWrapperBuilder<T, BUILDER, SELF>::new);
    }
}
