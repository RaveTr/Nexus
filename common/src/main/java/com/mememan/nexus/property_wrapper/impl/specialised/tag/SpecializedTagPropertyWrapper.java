package com.mememan.nexus.property_wrapper.impl.specialised.tag;

import com.mememan.nexus.property_wrapper.base.specialised.tag.TagBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class SpecializedTagPropertyWrapper<T, SELF extends TagBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedTagPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements TagBasedPropertyWrapper<T, SELF, BUILDER> {

    public SpecializedTagPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, SpecializedTagPropertyWrapperBuilder::new, modId);
    }

    public SpecializedTagPropertyWrapper(@NotNull Supplier<T> parentObject, @NotNull String modId) {
        super(parentObject, SpecializedTagPropertyWrapperBuilder::new, modId);
    }

    public SpecializedTagPropertyWrapper() {
        super(SpecializedTagPropertyWrapperBuilder::new);
    }

    @Override
    public List<Supplier<TagKey<? super T>>> getObjectTags() {
        return rawBuilder().map(b -> b.objectTagKeys).orElse(ObjectArrayList.of());
    }

    @Override
    public List<Supplier<TagKey<?>>> getAdditionalTags() {
        return rawBuilder().map(b -> b.additionalTagKeys).orElse(ObjectArrayList.of());
    }
}
