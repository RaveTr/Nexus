package com.mememan.nexus.property_wrapper.def.tag;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TagPropertyWrapper<T, TK extends TagKey<T>> extends SpecializedTagPropertyWrapper<TK, TagPropertyWrapper<T, TK>, TagPropertyWrapperBuilder<T, TK>> {

    public TagPropertyWrapper(Supplier<TK> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, modId);
    }

    public TagPropertyWrapper(@NotNull Supplier<TK> parentObject, @NotNull String modId) {
        super(parentObject, modId);
    }

    public TagPropertyWrapper() {
        super();
    }

    @Override
    public @NotNull PropertyWrapperBuilder<TK, TagPropertyWrapperBuilder<T, TK>, TagPropertyWrapper<T, TK>> constructBuilder() {
        return new TagPropertyWrapperBuilder<>(this);
    }

    public List<Supplier<T>> getTaggedObjects() {
        return rawBuilder().map(builder -> builder.storedTaggedObjects).orElse(ObjectArrayList.of());
    }

    public List<Supplier<TK>> getChildTags() {
        return rawBuilder().map(builder -> builder.storedTags).orElse(ObjectArrayList.of());
    }

    public Optional<Integer> getCookTime() {
        return rawBuilder().flatMap(builder -> builder.cookTime);
    }

    public Optional<IntIntMutablePair> getFlammabilityPair() {
        return rawBuilder().flatMap(builder -> builder.flammabilityPair);
    }
}
