package com.mememan.nexus.property_wrapper.def.tag;

import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class TagPropertyWrapperBuilder<T, TK extends TagKey<T>> extends SpecializedTagPropertyWrapperBuilder<TK, TagPropertyWrapperBuilder<T, TK>, TagPropertyWrapper<T, TK>> {

    public TagPropertyWrapperBuilder(@NotNull TagPropertyWrapper<T, TK> ownerWrapper) {
        super(ownerWrapper);
    }
}
