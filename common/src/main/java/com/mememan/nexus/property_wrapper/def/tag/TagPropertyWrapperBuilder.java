package com.mememan.nexus.property_wrapper.def.tag;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class TagPropertyWrapperBuilder<T, TK extends TagKey<T>> extends SpecializedTagPropertyWrapperBuilder<TK, TagPropertyWrapperBuilder<T, TK>, TagPropertyWrapper<T, TK>> {
    protected final List<Supplier<T>> storedTaggedObjects = new ObjectArrayList<>();
    protected final List<Supplier<TK>> storedTags = new ObjectArrayList<>();

    public TagPropertyWrapperBuilder(@NotNull TagPropertyWrapper<T, TK> ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public TagPropertyWrapperBuilder<T, TK> copyFrom(TagPropertyWrapper<T, TK> propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .setTaggedObjects(propertyWrapper.getTaggedObjects())
                .setChildTags(propertyWrapper.getChildTags());
    }

    /**
     * Specifies an object to be tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}).
     * <br></br>
     * It should be noted that the object should be a valid, retrievable type from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObject The object to tag with this instance's parent {@link TagKey}.
     *
     * @return {@code this} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObjects(Supplier[])
     * @see #withTaggedObjects(List)
     * @see #setTaggedObjects(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withTaggedObject(Supplier<T> taggedObject) {
        this.storedTaggedObjects.add(taggedObject);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> withTaggedObjects(Supplier<T>... taggedObjects) {
        return withTaggedObjects(ObjectArrayList.of(taggedObjects));
    }

    public TagPropertyWrapperBuilder<T, TK> withTaggedObjects(List<Supplier<T>> taggedObjects) {
        this.storedTaggedObjects.addAll(taggedObjects);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> setTaggedObjects(List<Supplier<T>> taggedObjects) {
        this.storedTaggedObjects.clear();
        this.storedTaggedObjects.addAll(taggedObjects);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> withChildTag(Supplier<TK> tag) {
        this.storedTags.add(tag);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> withChildTags(Supplier<TK>... tags) {
        return withChildTags(ObjectArrayList.of(tags));
    }

    public TagPropertyWrapperBuilder<T, TK> withChildTags(List<Supplier<TK>> tags) {
        this.storedTags.addAll(tags);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> setChildTags(List<Supplier<TK>> tags) {
        this.storedTags.clear();
        this.storedTags.addAll(tags);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> withTaggedObjectOfType(Supplier<? extends T> taggedObject) {
        this.storedTaggedObjects.add((Supplier<T>) taggedObject);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> withTaggedObjectsOfType(Supplier<? extends T>... taggedObjects) {
        return withTaggedObjectsOfType(ObjectArrayList.of(taggedObjects));
    }

    public TagPropertyWrapperBuilder<T, TK> withTaggedObjectsOfType(List<Supplier<? extends T>> taggedObjects) {
        this.storedTaggedObjects.addAll((List) taggedObjects);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> setTaggedObjectsOfType(List<Supplier<? extends T>> taggedObjects) {
        this.storedTaggedObjects.clear();
        this.storedTaggedObjects.addAll((List) taggedObjects);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> withChildTagOfType(Supplier<? extends TK> tag) {
        this.storedTags.add((Supplier<TK>) tag);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> withChildTagsOfType(Supplier<? extends TK>... tags) {
        return withChildTagsOfType(ObjectArrayList.of(tags));
    }

    public TagPropertyWrapperBuilder<T, TK> withChildTagsOfType(List<Supplier<? extends TK>> tags) {
        this.storedTags.addAll((List) tags);
        return this;
    }

    public TagPropertyWrapperBuilder<T, TK> setChildTagsOfType(List<Supplier<? extends TK>> tags) {
        this.storedTags.clear();
        this.storedTags.addAll((List) tags);
        return this;
    }
}
