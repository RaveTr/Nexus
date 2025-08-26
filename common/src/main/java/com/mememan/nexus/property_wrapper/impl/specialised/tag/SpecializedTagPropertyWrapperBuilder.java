package com.mememan.nexus.property_wrapper.impl.specialised.tag;

import com.mememan.nexus.property_wrapper.base.specialised.tag.TagBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.tag.TagBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class SpecializedTagPropertyWrapperBuilder<T, SELF extends TagBasedPropertyWrapperBuilder<T, SELF, TBPW>, TBPW extends TagBasedPropertyWrapper<T, TBPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, TBPW> implements TagBasedPropertyWrapperBuilder<T, SELF, TBPW> {
    protected final List<Supplier<TagKey<? super T>>> objectTagKeys = ObjectArrayList.of();
    protected final List<Supplier<TagKey<?>>> additionalTagKeys = ObjectArrayList.of();

    public SpecializedTagPropertyWrapperBuilder(@NotNull TBPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(TBPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .setTags(List.copyOf(propertyWrapper.getObjectTags()))
                .setAdditionalTags(List.copyOf(propertyWrapper.getAdditionalTags()));
    }

    @Override
    public SELF withTag(Supplier<TagKey<? super T>> targetTag) {
        this.objectTagKeys.add(targetTag);
        return self();
    }

    @Override
    public SELF withTags(List<Supplier<TagKey<? super T>>> targetTags) {
        this.objectTagKeys.addAll(targetTags);
        return self();
    }

    @Override
    public SELF setTags(List<Supplier<TagKey<? super T>>> targetTags) {
        this.objectTagKeys.clear();
        this.objectTagKeys.addAll(targetTags);
        return self();
    }

    @Override
    public SELF withAdditionalTag(Supplier<TagKey<?>> targetTag) {
        this.additionalTagKeys.add(targetTag);
        return self();
    }

    @Override
    public SELF withAdditionalTags(List<Supplier<TagKey<?>>> targetTags) {
        this.additionalTagKeys.addAll(targetTags);
        return self();
    }

    @Override
    public SELF setAdditionalTags(List<Supplier<TagKey<?>>> targetTags) {
        this.additionalTagKeys.clear();
        this.additionalTagKeys.addAll(targetTags);
        return self();
    }
}
