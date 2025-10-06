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

    /**
     * Gets the {@link List} of {@link Supplier}s for objects that should be tagged with this instance's parent
     * {@link TagKey}.
     *
     * @return The {@link List} of {@link Supplier}s for tagged objects. May be empty.
     *
     * @see TagPropertyWrapperBuilder#withTaggedObject(Supplier)
     */
    public List<Supplier<T>> getTaggedObjects() {
        return rawBuilder().map(builder -> builder.storedTaggedObjects).orElse(ObjectArrayList.of());
    }

    /**
     * Gets the {@link List} of {@link Supplier}s for child tags that should be included within this instance's parent
     * {@link TagKey}.
     *
     * @return The {@link List} of {@link Supplier}s for child tags. May be empty.
     *
     * @see TagPropertyWrapperBuilder#withChildTag(Supplier)
     */
    public List<Supplier<TK>> getChildTags() {
        return rawBuilder().map(builder -> builder.storedTags).orElse(ObjectArrayList.of());
    }

    /**
     * Gets the cook time for objects tagged with this instance's parent {@link TagKey}. The cook time represents
     * how long items take to cook/smelt in a furnace. Only applies to block and item tags.
     *
     * @return The cook time in ticks. May be empty.
     *
     * @see TagPropertyWrapperBuilder#withCookTime(Integer)
     */
    public Optional<Integer> getCookTime() {
        return rawBuilder().flatMap(builder -> builder.cookTime);
    }

    /**
     * Gets the flammability properties for objects tagged with this instance's parent {@link TagKey}. Flammability
     * determines how easily blocks catch fire and the chance blocks are consumed by fire.
     *
     * @return The {@link IntIntMutablePair} containing burn time and spread values. May be empty.
     *
     * @see TagPropertyWrapperBuilder#withFlammability(IntIntMutablePair)
     */
    public Optional<IntIntMutablePair> getFlammabilityPair() {
        return rawBuilder().flatMap(builder -> builder.flammabilityPair);
    }
}
