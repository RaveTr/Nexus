package com.mememan.nexus.tag;

import com.google.common.collect.ImmutableList;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A wrapper-builder {@code class} used to store information about tags and their pre-defined references in datagen to
 * simplify creating data entries for tags.
 *
 * @param <T> The object type stored by the parent {@link TagKey}.
 * @param <TK> The {@link TagKey} object itself parenting the objects stored in this wrapper.
 */
public class TagWrapper<T, TK extends TagKey<T>> {
    private static final ObjectArrayList<TagWrapper<?, ? extends TagKey<?>>> CACHED_WRAPPERS = new ObjectArrayList<>();
    @NotNull
    protected final Supplier<TK> parentTag;
    protected ObjectArrayList<Supplier<T>> storedTaggedObjects = new ObjectArrayList<>();
    protected ObjectArrayList<Supplier<TK>> storedCopiedTags = new ObjectArrayList<>();
    protected ObjectArrayList<Supplier<TK>> storedParentTags = new ObjectArrayList<>();
    protected int cookTime = 0;
    @Nullable
    protected IntIntMutablePair flammabilityPair;
    protected boolean excludeFromNativeDatagen = false;
    protected final Map<ProviderType, Boolean> mappedProviderRequisites = new Object2BooleanOpenHashMap<>();

    private TagWrapper(Supplier<TK> parentTag) {
        this.parentTag = parentTag;
    }

    /**
     * Creates a new TW instance.
     *
     * @param parentTag The {@link TagKey} parenting all stored objects in this wrapper instance. Cannot be {@code null}.
     *
     * @return The newly-constructed TW instance.
     *
     * @param <T> The object type stored by the parent {@link TagKey}.
     * @param <TK> The {@link TagKey} object itself parenting the objects stored in this wrapper.
     */
    public static <T, TK extends TagKey<T>> TagWrapper<T, TK> create(@NotNull Supplier<TK> parentTag) {
        TagWrapper<T, TK> createdWrapper =  new TagWrapper<>(parentTag);
        CACHED_WRAPPERS.add(createdWrapper);
        return createdWrapper;
    }

    /**
     * Adds a pre-defined object entry to this TW instance's parent {@link TagKey}.
     *
     * @param tagEntry The object entry to add to the collection of pre-defined objects tagged with this TW instance's parent {@link TagKey}.
     *
     * @return {@code this} (builder method).
     *
     * @see #withEntries(List)
     * @see #withTagEntry(Supplier)
     * @see #withTagEntries(List)
     * @see #withParentTagEntry(Supplier)
     * @see #withParentTagEntries(List)
     */
    public TagWrapper<T, TK> withEntry(Supplier<T> tagEntry) {
        this.storedTaggedObjects.add(tagEntry);
        return this;
    }

    /**
     * Adds a pre-defined {@link List} of object entries to this TW instance's parent {@link TagKey}.
     *
     * @param tagEntries The {@link List} of object entries to add to the collection of pre-defined objects tagged with
     *                   this TW instance's parent {@link TagKey}.
     *
     * @return {@code this} (builder method).
     *
     * @see #withEntry(Supplier)
     * @see #withTagEntry(Supplier)
     * @see #withTagEntries(List)
     * @see #withParentTagEntry(Supplier)
     * @see #withParentTagEntries(List)
     */
    public TagWrapper<T, TK> withEntries(List<Supplier<T>> tagEntries) {
        this.storedTaggedObjects.addAll(tagEntries);
        return this;
    }

    /**
     * Adds a pre-defined {@link TagKey} entry to this TW instance's {@link ObjectArrayList} of
     * {@linkplain TagKey TagKeys} to inherit object entries from.
     *
     * @param tagToCopyFrom The {@link TagKey} entry to add to this TW instance's {@link ObjectArrayList}
     *                      of {@linkplain TagKey TagKeys} to inherit object entries from.
     *
     * @return {@code this} (builder method).
     *
     * @see #withEntry(Supplier)
     * @see #withEntries(List)
     * @see #withTagEntries(List)
     * @see #withParentTagEntry(Supplier)
     * @see #withParentTagEntries(List)
     */
    public TagWrapper<T, TK> withTagEntry(Supplier<TK> tagToCopyFrom) {
        this.storedCopiedTags.add(tagToCopyFrom);
        return this;
    }

    /**
     * Adds a pre-defined {@link List} of {@link TagKey} entries to this TW instance's {@link ObjectArrayList} of
     * {@linkplain TagKey TagKeys} to inherit object entries from.
     *
     * @param tagEntries The {@link List} of {@link TagKey} entries to add to this TW instance's {@link ObjectArrayList}
     *                   of {@linkplain TagKey TagKeys} to inherit object entries from.
     *
     * @return {@code this} (builder method).
     *
     * @see #withEntry(Supplier)
     * @see #withEntries(List)
     * @see #withTagEntry(Supplier)
     * @see #withParentTagEntry(Supplier)
     * @see #withParentTagEntries(List)
     */
    public TagWrapper<T, TK> withTagEntries(List<Supplier<TK>> tagEntries) {
        this.storedCopiedTags.addAll(tagEntries);
        return this;
    }

    /**
     * Adds a pre-defined {@link TagKey} entry to this TW instance's {@link ObjectArrayList} of
     * {@linkplain TagKey TagKeys} to be added to.
     *
     * @param parentTag The {@link TagKey} entry to add to this TW instance's {@link ObjectArrayList} of parent
     *                  {@linkplain TagKey TagKeys} to be added to.
     *
     * @return {@code this} (builder method).
     *
     * @see #withEntry(Supplier)
     * @see #withEntries(List)
     * @see #withTagEntries(List)
     * @see #withParentTagEntries(List)
     */
    public TagWrapper<T, TK> withParentTagEntry(Supplier<TK> parentTag) {
        this.storedParentTags.add(parentTag);
        return this;
    }

    /**
     * Adds a pre-defined {@link List} of {@link TagKey} entries to this TW instance's {@link ObjectArrayList} of
     * {@linkplain TagKey TagKeys} to be added to.
     *
     * @param parentTagEntries The {@link List} of {@link TagKey} entries to add to this TW instance's
     *                         {@link ObjectArrayList} of parent {@linkplain TagKey TagKeys} to be added to.
     *
     * @return {@code this} (builder method).
     *
     * @see #withEntry(Supplier)
     * @see #withEntries(List)
     * @see #withTagEntry(Supplier)
     * @see #withParentTagEntry(Supplier)
     */
    public TagWrapper<T, TK> withParentTagEntries(List<Supplier<TK>> parentTagEntries) {
        this.storedParentTags.addAll(parentTagEntries);
        return this;
    }

    /**
     * Defines flammability options using an optional {@link IntIntMutablePair} representing the burn time (in ticks)
     * and spread. Only applied to {@link Block} {@linkplain TK TagKeys}.
     *
     * @param flammabilityPair The mapping pair representing the parent {@linkplain TK TagKey's} optional flammability
     *                         settings, with the {@link IntIntMutablePair} representing the burn time (in ticks) and
     *                         spread respectively.
     *
     * @return {@code this} (builder method).
     */
    public TagWrapper<T, TK> withFlammability(IntIntMutablePair flammabilityPair) {
        this.flammabilityPair = flammabilityPair;
        return this;
    }

    /**
     * Defines this TW instance as flammable with the provided {@code cookTime}, in ticks. Only applied to {@link Item}
     * {@linkplain TK TagKeys}.
     *
     * @param cookTime The {@code cookTime} for all objects under the registered tag, in ticks.
     *
     * @return {@code this} (builder method).
     */
    public TagWrapper<T, TK> asFuel(int cookTime) {
        this.cookTime = cookTime;
        return this;
    }

    /**
     * Determines whether this TagWrapper instance should be entirely excluded from Nexus' native datagen.
     * <br></br>
     * Fundamentally, all this does is flag this instance as not needing a data entry to be mapped to it. You may
     * choose to generate data for it yourself if needed, since Nexus won't handle datagen for this particular object.
     * <br></br>
     * If a tag-specific data provider has {@link ModDataProvider#validateAllEntries()} set to {@code true}, this
     * instance (and its children, so long as this value isn't modified) will still be excluded from datagen, and thus
     * an exception won't be thrown for it.
     *
     * @param excludeFromNativeDatagen Whether this instance's data should be passed into Nexus' native datagen for
     *                                 data generation.
     *
     * @return {@code this} (builder method).
     *
     * @see #excludeFromNativeDatagen()
     * @see #requiresDatagenEntry(ProviderType, boolean)
     */
    public TagWrapper<T, TK> excludeFromNativeDatagen(boolean excludeFromNativeDatagen) {
        this.excludeFromNativeDatagen = excludeFromNativeDatagen;
        return this;
    }

    /**
     * Determines whether this TagWrapper instance is required to generate necessary tag-related data based on the
     * {@link ProviderType} passed in.
     * <br></br>
     * By default, unmapped providers will not require an entry for this TagWrapper to be generated unless
     * {@link ModDataProvider#validateAllEntries()} is set to {@code true}.
     * <br></br>
     * Mapping the related provider passed in here to {@code requiresDatagenEntry}, set to {@code true}, will flag
     * this TagWrapper instance for requiring related data regardless of what
     * {@link ModDataProvider#validateAllEntries()} is set to.
     *
     * @param targetProviderType The {@link ProviderType} to modify the data entry requirement for.
     * @param requiresDatagenEntry Whether this TagWrapper should require data related to the specified
     *                             {@code targetProviderType} to be present.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(Map)
     * @see #excludeFromNativeDatagen(boolean)
     */
    public TagWrapper<T, TK> requiresDatagenEntry(ProviderType targetProviderType, boolean requiresDatagenEntry) {
        mappedProviderRequisites.put(targetProviderType, requiresDatagenEntry);
        return this;
    }

    /**
     * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
     * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}.
     *
     * @param targetProviderTypes The {@link List} of {@linkplain ProviderType ProviderTypes} to modify the data
     *                            entry requirements for.
     * @param requiresDatagenEntry Whether this TagWrapper should require data related to each of the
     *                             specified {@code targetProviderTypes} to be present.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntry(ProviderType, boolean)
     * @see #requiresSetDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(Map)
     * @see #excludeFromNativeDatagen(boolean)
     */
    public TagWrapper<T, TK> requiresDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
        targetProviderTypes.forEach(type -> requiresDatagenEntry(type, requiresDatagenEntry));
        return this;
    }

    /**
     * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
     * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}. Overrides the existing
     * {@link Map}.
     *
     * @param targetProviderTypes The {@link List} of {@linkplain ProviderType ProviderTypes} to modify the data
     *                            entry requirements for.
     * @param requiresDatagenEntry Whether this TagWrapper should require data related to each of the
     *                             specified {@code targetProviderTypes} to be present.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntry(ProviderType, boolean)
     * @see #requiresDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(Map)
     * @see #excludeFromNativeDatagen(boolean)
     */
    public TagWrapper<T, TK> requiresSetDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
        mappedProviderRequisites.clear();
        targetProviderTypes.forEach(type -> requiresDatagenEntry(type, requiresDatagenEntry));
        return this;
    }

    /**
     * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
     * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}. Overrides the existing
     * {@link Map}.
     *
     * @param mappedProviderRequisites The {@link Map} of provider requisites to override the existing {@link Map}
     *                                 with.
     *
     * @return {@code this} (builder method).
     *
     * @see #requiresDatagenEntry(ProviderType, boolean)
     * @see #requiresDatagenEntries(List, boolean)
     * @see #requiresSetDatagenEntries(List, boolean)
     * @see #excludeFromNativeDatagen(boolean)
     */
    public TagWrapper<T, TK> requiresSetDatagenEntries(Map<ProviderType, Boolean> mappedProviderRequisites) {
        this.mappedProviderRequisites.clear();
        this.mappedProviderRequisites.putAll(mappedProviderRequisites);
        return this;
    }

    /**
     * Gets the parent {@link Supplier<TK>} stored in this TW instance.
     *
     * @return The parent {@link Supplier<TK>}.
     */
    @NotNull
    public Supplier<TK> getParentTag() {
        return parentTag;
    }

    /**
     * Gets the pre-defined {@link List} of object entries stored in this TW instance.
     *
     * @return An immutable view of the pre-defined {@link List} of object entries stored in this TW instance.
     */
    public ImmutableList<Supplier<T>> getPredefinedTagEntries() {
        return ImmutableList.copyOf(storedTaggedObjects.stream()
                .filter(curSup -> {
                    if (curSup == null || curSup.get() == null) NexusConstants.LOGGER.warn("Null entry found in TagWrapper for: {}. Please make sure that your tag class is being initialized after the class(es) containing the object entries you're attempting to add is/are initialized without circular dependencies (e.g. accessing your tag registrar class from the same class one of your predefined tag object entries is in [referring to blocks, items, etc.], causing an NPE to be thrown or this to happen).", parentTag.get().location());
                    return Objects.nonNull(curSup);
                })
                .toList());
    }

    /**
     * Gets the pre-defined {@link List} of {@link TagKey} entries stored in this TW instance.
     *
     * @return An immutable view of the pre-defined {@link List} of {@link TagKey} entries stored in this TW instance.
     */
    public ImmutableList<Supplier<TK>> getStoredTags() {
        return ImmutableList.copyOf(storedCopiedTags);
    }

    /**
     * Gets the pre-defined {@link List} of parent {@link TagKey} entries stored in this TW instance.
     *
     * @return An immutable view of the pre-defined {@link List} of parent {@link TagKey} entries stored in this TW instance.
     */
    public ImmutableList<Supplier<TK>> getParentTags() {
        return ImmutableList.copyOf(storedParentTags);
    }

    /**
     * Gets the {@code cookTime} for all objects under the registered tag, in ticks. 0 is treated as none, values under
     * 0 are simply {@linkplain Math#abs(int) abs'd}. Only applied to {@link Item} {@linkplain TK TagKeys}.
     *
     * @return The {@code cookTime} for all objects under the registered tag, in ticks.
     */
    public int getCookTime() {
        return cookTime;
    }

    /**
     * Gets an {@link IntIntMutablePair} representing the burn time (in ticks) and spread. Only applied to {@link Block}
     * {@linkplain TK TagKeys}. May be {@code null}.
     *
     * @return The {@link IntIntMutablePair} representing the burn time (in ticks) and spread. May be {@code null}.
     */
    @Nullable
    public IntIntMutablePair getFlammabilitySettings() {
        return flammabilityPair;
    }

    /**
     * Whether {@code TagWrapper} data should automatically be handled/generated by Nexus API.
     *
     * @return {@code true} if {@link TagWrapper#excludeFromNativeDatagen} is set to {@code true}, {@code false}
     * otherwise.
     */
    public boolean excludeFromNativeDatagen() {
        return excludeFromNativeDatagen;
    }

    /**
     * Gets a {@link Map} (usually {@link Object2BooleanOpenHashMap}) specifying the {@linkplain ProviderType ProviderTypes}
     * for which this TagWrapper instance requires data to present for generation.
     *
     * @return The {@link Map} representing different {@linkplain ProviderType ProviderTypes} and their requirements for
     * datagen. May be empty.
     */
    public Map<ProviderType, Boolean> getProviderTypeRequisites() {
        return mappedProviderRequisites;
    }

    /**
     * Gets an {@link ImmutableList} representing all created TW entries, cached in a {@code static} collection.
     *
     * @return An immutable view of {@link #CACHED_WRAPPERS}.
     */
    public static ImmutableList<TagWrapper<?, ? extends TagKey<?>>> getCachedTWEntries() {
        return ImmutableList.copyOf(CACHED_WRAPPERS);
    }
}
