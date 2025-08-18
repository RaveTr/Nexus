package com.mememan.nexus.property_wrapper.impl.specialised.tag;

import com.mememan.nexus.property_wrapper.base.specialised.tag.TagBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class SpecializedTagPropertyWrapper<T, SELF extends TagBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedTagPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements TagBasedPropertyWrapper<T, SELF, BUILDER> {

    public SpecializedTagPropertyWrapper(Supplier<T> parentObject, boolean isTemplate) {
        super(parentObject, isTemplate);
    }

    public SpecializedTagPropertyWrapper(@NotNull Supplier<T> parentObject) {
        super(parentObject);
    }

    public SpecializedTagPropertyWrapper() {
        super();
    }

    @Override
    public List<TagKey<T>> getObjectTags() {
        return rawBuilder().map(b -> b.objectTagKeys).orElse(ObjectArrayList.of());
    }

    @Override
    public List<TagKey<?>> getAdditionalTags() {
        return rawBuilder().map(b -> b.additionalTagKeys).orElse(ObjectArrayList.of());
    }
}
