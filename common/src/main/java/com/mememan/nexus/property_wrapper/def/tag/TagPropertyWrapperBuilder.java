package com.mememan.nexus.property_wrapper.def.tag;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TagPropertyWrapperBuilder<T, TK extends TagKey<T>> extends SpecializedTagPropertyWrapperBuilder<TK, TagPropertyWrapperBuilder<T, TK>, TagPropertyWrapper<T, TK>> {
    protected final List<Supplier<T>> storedTaggedObjects = new ObjectArrayList<>();
    protected final List<Supplier<TK>> storedTags = new ObjectArrayList<>();
    protected Optional<Integer> cookTime = Optional.empty();
    protected Optional<IntIntMutablePair> flammabilityPair = Optional.empty();

    public TagPropertyWrapperBuilder(@NotNull TagPropertyWrapper<T, TK> ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public TagPropertyWrapperBuilder<T, TK> copyFrom(TagPropertyWrapper<T, TK> propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .setTaggedObjects(propertyWrapper.getTaggedObjects())
                .setChildTags(propertyWrapper.getChildTags())
                .withCookTime(propertyWrapper.getCookTime().orElse(null))
                .withFlammability(propertyWrapper.getFlammabilityPair().orElse(null));
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
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObjects(Supplier[])
     * @see #withTaggedObjects(List)
     * @see #setTaggedObjects(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withTaggedObject(Supplier<T> taggedObject) {
        this.storedTaggedObjects.add(taggedObject);
        return self();
    }

    /**
     * Specifies multiple objects to be tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). Accepts a variable number of object suppliers.
     * <br></br>
     * It should be noted that the objects should be valid, retrievable types from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObjects The objects to tag with this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObject(Supplier)
     * @see #withTaggedObjects(List)
     * @see #setTaggedObjects(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withTaggedObjects(Supplier<T>... taggedObjects) {
        return withTaggedObjects(ObjectArrayList.of(taggedObjects));
    }

    /**
     * Specifies multiple objects to be tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). Accepts a {@link List} of object suppliers.
     * <br></br>
     * It should be noted that the objects should be valid, retrievable types from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObjects The {@link List} of objects to tag with this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObject(Supplier)
     * @see #withTaggedObjects(Supplier[])
     * @see #setTaggedObjects(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withTaggedObjects(List<Supplier<T>> taggedObjects) {
        this.storedTaggedObjects.addAll(taggedObjects);
        return self();
    }

    /**
     * Sets the objects to be tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}), replacing any previously specified tagged objects.
     * <br></br>
     * It should be noted that the objects should be valid, retrievable types from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObjects The {@link List} of objects to tag with this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObject(Supplier)
     * @see #withTaggedObjects(Supplier[])
     * @see #withTaggedObjects(List)
     */
    public TagPropertyWrapperBuilder<T, TK> setTaggedObjects(List<Supplier<T>> taggedObjects) {
        this.storedTaggedObjects.clear();
        this.storedTaggedObjects.addAll(taggedObjects);
        return self();
    }

    /**
     * Specifies a child tag to be included within this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}).
     *
     * @param tag The child tag to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTags(Supplier[])
     * @see #withChildTags(List)
     * @see #setChildTags(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withChildTag(Supplier<TK> tag) {
        this.storedTags.add(tag);
        return self();
    }

    /**
     * Specifies multiple child tags to be included within this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). Accepts a variable number of tag suppliers.
     *
     * @param tags The child tags to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTag(Supplier)
     * @see #withChildTags(List)
     * @see #setChildTags(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withChildTags(Supplier<TK>... tags) {
        return withChildTags(ObjectArrayList.of(tags));
    }

    /**
     * Specifies multiple child tags to be included within this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). Accepts a {@link List} of tag suppliers.
     *
     * @param tags The {@link List} of child tags to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTag(Supplier)
     * @see #withChildTags(Supplier[])
     * @see #setChildTags(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withChildTags(List<Supplier<TK>> tags) {
        this.storedTags.addAll(tags);
        return self();
    }

    /**
     * Sets the child tags to be included within this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}), replacing any previously specified child tags.
     *
     * @param tags The {@link List} of child tags to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTag(Supplier)
     * @see #withChildTags(Supplier[])
     * @see #withChildTags(List)
     */
    public TagPropertyWrapperBuilder<T, TK> setChildTags(List<Supplier<TK>> tags) {
        this.storedTags.clear();
        this.storedTags.addAll(tags);
        return self();
    }

    /**
     * Specifies an object of a compatible type to be tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). This method provides type-safe casting for compatible object types.
     * <br></br>
     * It should be noted that the object should be a valid, retrievable type from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObject The object to tag with this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObjectsOfType(Supplier[])
     * @see #withTaggedObjectsOfType(List)
     * @see #setTaggedObjectsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withTaggedObjectOfType(Supplier<? extends T> taggedObject) {
        this.storedTaggedObjects.add((Supplier<T>) taggedObject);
        return self();
    }

    /**
     * Overloaded variant of {@link #withTaggedObjectOfType(Supplier)}. Specifies multiple objects of compatible types
     * to be tagged with this instance's parent {@link TagKey} (via its owner {@link TagPropertyWrapper}). Accepts a
     * variable number of object suppliers with type-safe casting.
     * <br></br>
     * It should be noted that the objects should be valid, retrievable types from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObjects The objects to tag with this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObjectOfType(Supplier)
     * @see #withTaggedObjectsOfType(List)
     * @see #setTaggedObjectsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withTaggedObjectsOfType(Supplier<? extends T>... taggedObjects) {
        return withTaggedObjectsOfType(ObjectArrayList.of(taggedObjects));
    }

    /**
     * Specifies multiple objects of compatible types to be tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). Accepts a {@link List} of object suppliers with type-safe casting.
     * <br></br>
     * It should be noted that the objects should be valid, retrievable types from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObjects The {@link List} of objects to tag with this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObjectOfType(Supplier)
     * @see #withTaggedObjectsOfType(Supplier[])
     * @see #setTaggedObjectsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withTaggedObjectsOfType(List<Supplier<? extends T>> taggedObjects) {
        this.storedTaggedObjects.addAll((List) taggedObjects);
        return self();
    }

    /**
     * Sets the objects of compatible types to be tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}), replacing any previously specified tagged objects. Provides type-safe casting for
     * compatible object types.
     * <br></br>
     * It should be noted that the objects should be valid, retrievable types from any registry ({@link Block},
     * {@link Item}, {@link EntityType}, etc.)
     *
     * @param taggedObjects The {@link List} of objects to tag with this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see DataGenPropertyWrapper.RegistryLookupContainer#getObjectRegistryId(Object)
     * @see #withTaggedObjectOfType(Supplier)
     * @see #withTaggedObjectsOfType(Supplier[])
     * @see #withTaggedObjectsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> setTaggedObjectsOfType(List<Supplier<? extends T>> taggedObjects) {
        this.storedTaggedObjects.clear();
        this.storedTaggedObjects.addAll((List) taggedObjects);
        return self();
    }

    /**
     * Specifies a child tag of a compatible type to be included within this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). This method provides type-safe casting for compatible tag types.
     *
     * @param tag The child tag to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTagsOfType(Supplier[])
     * @see #withChildTagsOfType(List)
     * @see #setChildTagsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withChildTagOfType(Supplier<? extends TK> tag) {
        this.storedTags.add((Supplier<TK>) tag);
        return self();
    }

    /**
     * Overloaded variant of {@link #withChildTagOfType(Supplier)}. Specifies multiple child tags of compatible types to
     * be included within this instance's parent {@link TagKey} (via its owner {@link TagPropertyWrapper}). Accepts a
     * variable number of tag suppliers with type-safe casting.
     *
     * @param tags The child tags to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTagOfType(Supplier)
     * @see #withChildTagsOfType(List)
     * @see #setChildTagsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withChildTagsOfType(Supplier<? extends TK>... tags) {
        return withChildTagsOfType(ObjectArrayList.of(tags));
    }

    /**
     * Specifies multiple child tags of compatible types to be included within this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). Accepts a {@link List} of tag suppliers with type-safe casting.
     *
     * @param tags The {@link List} of child tags to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTagOfType(Supplier)
     * @see #withChildTagsOfType(Supplier[])
     * @see #setChildTagsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> withChildTagsOfType(List<Supplier<? extends TK>> tags) {
        this.storedTags.addAll((List) tags);
        return self();
    }

    /**
     * Sets the child tags of compatible types to be included within this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}), replacing any previously specified child tags. Provides type-safe casting for
     * compatible tag types.
     *
     * @param tags The {@link List} of child tags to include within this instance's parent {@link TagKey}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withChildTagOfType(Supplier)
     * @see #withChildTagsOfType(Supplier[])
     * @see #withChildTagsOfType(List)
     */
    public TagPropertyWrapperBuilder<T, TK> setChildTagsOfType(List<Supplier<? extends TK>> tags) {
        this.storedTags.clear();
        this.storedTags.addAll((List) tags);
        return self();
    }

    /**
     * Specifies the cook time for objects tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). The cook time represents how long items take to cook/smelt in a furnace.
     * <br></br>
     * If the provided cook time is {@code null} or less than or equal to 0, no cook time will be set.
     * <br></br>
     * Only applies to block and item tags.
     *
     * @param cookTime The cook time in ticks, or {@code null} to clear the cook time.
     *
     * @return {@link #self()} (builder method).
     */
    public TagPropertyWrapperBuilder<T, TK> withCookTime(Integer cookTime) {
        this.cookTime = Optional.ofNullable(Math.abs(cookTime) > 0 ? cookTime : null);
        return self();
    }

    /**
     * Specifies the flammability properties for objects tagged with this instance's parent {@link TagKey} (via its owner
     * {@link TagPropertyWrapper}). Flammability determines how easily blocks catch fire and the chance blocks are consumed
     * by fire. Only applies to block tags.
     *
     * @param flammabilityPair The {@link IntIntMutablePair} containing burn odd and spread chance values, where left
     *                         represents ignition chance and right representing consumption chance (0 - 300 normalizing
     *                         to 0% - 100%).
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withFlammability(int, int)
     */
    public TagPropertyWrapperBuilder<T, TK> withFlammability(IntIntMutablePair flammabilityPair) {
        this.flammabilityPair = Optional.ofNullable(flammabilityPair);
        return self();
    }

    /**
     * Overloaded variant of {@link #withFlammability(IntIntMutablePair)}. Specifies the flammability properties for
     * objects tagged with this instance's parent {@link TagKey} (via its owner {@link TagPropertyWrapper}). Flammability
     * determines how easily blocks catch fire and the chance blocks are consumed by fire.
     *
     * @param igniteChance The chance a tagged block will ignite on fire.
     * @param spread The chance a tagged block will be consumed. 0 - 300 = to 0% - 100%.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withFlammability(IntIntMutablePair)
     */
    public TagPropertyWrapperBuilder<T, TK> withFlammability(int igniteChance, int spread) {
        return withFlammability(IntIntMutablePair.of(igniteChance, spread));
    }
}
