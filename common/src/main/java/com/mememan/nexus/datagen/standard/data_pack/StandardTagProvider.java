package com.mememan.nexus.datagen.standard.data_pack;

import com.google.gson.JsonElement;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.tag.TagBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapper;
import com.mememan.nexus.util.DataGenUtil;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StandardTagProvider extends TagsProvider<Object> implements ModDataProvider {
    protected final PackOutput rootOutput;
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final List<TagBasedPropertyWrapper<?, ?, ?>> mappedTagPWs;
    protected final Map<ResourceKey<? extends Registry<?>>, Map<ResourceLocation, TagBuilder>> registryMappedBuilders = new Object2ObjectOpenHashMap<>();

    public StandardTagProvider(PackOutput targetOutput, CompletableFuture<HolderLookup.Provider> regLookup, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetOutput, DataGenPropertyWrapper.RegistryLookupContainer.UNMAPPED_REGISTRY, regLookup);

        this.rootOutput = targetOutput;

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedTagPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(TagBasedPropertyWrapper.class, modId);
    }

    public StandardTagProvider(PackOutput targetOutput, CompletableFuture<HolderLookup.Provider> regLookup, CompletableFuture<TagLookup<Object>> tagLookupFuture, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetOutput, DataGenPropertyWrapper.RegistryLookupContainer.UNMAPPED_REGISTRY, regLookup, tagLookupFuture);

        this.rootOutput = targetOutput;

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedTagPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(TagBasedPropertyWrapper.class, modId);
    }

    /**
     * Main entrypoint responsible for chaining all operations for this data provider.
     * <br></br>
     * First, {@link #createContentsProvider()} is called to generate a {@link CompletableFuture} that will
     * generate all tag contents and signal {@link #contentsDone} when it's finished to allow for proper value retrieval
     * from {@link #contentsGetter()}.
     * <br></br>
     * Then, a dummy object is generated to allow for querying registry entries from both the resultant
     * {@link HolderLookup.Provider} and {@link TagLookup}.
     * <br></br>
     * Finally, all tag entries mapped to their respective registry {@linkplain ResourceKey ResourceKeys} are serialized
     * and saved to disk appropriately, with missing references and such handled by the same method (see references
     * below).
     *
     * @param output The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return A {@link CompletableFuture} representing the completion of all tag serialization tasks.
     *
     * @see #serializeTagEntries(CachedOutput, HolderLookup.Provider, TagLookup)
     * @see #addTags(HolderLookup.Provider)
     */
    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput output) {
        return createContentsProvider().thenApply((contentProvider) -> {
            this.contentsDone.complete(null); // Allow for actual behaviour in #contentsGetter()
            return contentProvider;
        }).thenCombineAsync(parentProvider, (parentContentProvider, currentTag) -> {
            record CombinedData<T>(HolderLookup.Provider currentContents, TagLookup<T> parentTagLookup) {}

            return new CombinedData<>(parentContentProvider, currentTag);
        }).thenCompose((combinedTagLookupData) -> serializeTagEntries(output, combinedTagLookupData.currentContents, combinedTagLookupData.parentTagLookup));
    }

    /**
     * Primary entrypoint responsible for refreshing and regenerating data for {@link #registryMappedBuilders}. Called
     * in {@link #createContentsProvider()} to allow for {@link CompletableFuture} chaining and updates for
     * {@link #contentsGetter()} via {@link #contentsDone}.
     *
     * @param provider The {@link HolderLookup.Provider} to use for lookups. Currently unused.
     *
     * @see #addObjectTags(HolderLookup.Provider)
     * @see #run(CachedOutput)
     */
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addObjectTags(provider);
    }

    /**
     * Generic delegator variant of {@link #addTags(HolderLookup.Provider)}. Responsible for populating
     * {@link #registryMappedBuilders} based on data from {@link #mappedTagPWs}. Additionally, handles nullity checks
     * for any required PW entries.
     *
     * @param provider Lookup provider, in case dynamic entry lookups and/or the likes are needed. Currently unused,
     *                 primarily here for convenience.
     *
     * @param <T> The parent object type.
     *
     * @see #addTags(HolderLookup.Provider)
     * @see #validateDupeObjectTag(TagKey, String, ResourceLocation)
     */
    protected <T> void addObjectTags(HolderLookup.Provider provider) {
        if (!mappedTagPWs.isEmpty()) {
            mappedTagPWs.stream()
                    .map(curPW -> (TagBasedPropertyWrapper<T, ?, ?>) curPW)
                    .forEach(curPW -> {
                        List<Supplier<TagKey<? super T>>> objectTags = curPW.getObjectTags();
                        List<Supplier<TagKey<?>>> additionalTags = curPW.getAdditionalTags();
                        Supplier<T> parentObject = curPW.getParentObject();
                        String objectDescId = curPW.getObjectDescriptionId();
                        String objectClassName = parentObject.get().getClass().getSimpleName();
                        AtomicBoolean isTag = new AtomicBoolean();
                        AtomicBoolean tagHasNoExclusiveData = new AtomicBoolean();

                        if (curPW instanceof TagPropertyWrapper<?, ?> curTPW) { // Do tag-specific processing here
                            TagPropertyWrapper<T, TagKey<T>> curTagPW = (TagPropertyWrapper<T, TagKey<T>>) curTPW;
                            Supplier<TagKey<T>> parentTag = curTagPW.getParentObject();
                            TagKey<T> parentTagObj = parentTag.get();
                            List<Supplier<T>> taggedObjects = curTagPW.getTaggedObjects();
                            List<Supplier<TagKey<T>>> tagKeys = curTagPW.getChildTags();

                            if (taggedObjects.isEmpty() && tagKeys.isEmpty()) tagHasNoExclusiveData.set(true);
                            else {
                                taggedObjects.forEach(curTaggedObject -> {
                                    T taggedObject = curTaggedObject.get();
                                    String taggedObjClassName = taggedObject.getClass().getSimpleName();
                                    ResourceLocation childObjLoc = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(taggedObject);

                                    if (validateDupeObjectTag(parentTagObj, taggedObjClassName, childObjLoc)) {
                                        NexusConstants.LOGGER.debug("[{}] [Tagging {}]: {} -> {}", getModId(), taggedObjClassName, childObjLoc, parentTagObj);

                                        trackTag(parentTagObj).addElement(childObjLoc);
                                    }
                                });

                                tagKeys.forEach(curTagKey -> {
                                    TagKey<T> taggedTagKey = curTagKey.get();
                                    ResourceLocation taggedTagKeyLoc = taggedTagKey.location();

                                    if (validateDupeObjectTag(taggedTagKey, objectClassName, taggedTagKeyLoc)) {
                                        NexusConstants.LOGGER.debug("[{}] [Tagging TagKey]: {} -> {}", getModId(), taggedTagKeyLoc, parentTagObj);

                                        trackTag(parentTagObj).addTag(taggedTagKeyLoc);
                                    }
                                });
                            }

                            isTag.set(true);
                        }

                        if ((!isTag.get() || tagHasNoExclusiveData.get()) && objectTags.isEmpty() && additionalTags.isEmpty() && (validateAllEntries() || curPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false))) {
                            throw new NullPointerException(String.format("Missing tag entry for %s: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the object itself requires validation through DataGenBasedPropertyWrapper#getProviderTypeRequisites().", objectClassName, curPW.getObjectDescriptionId(), modId));
                        }

                        ResourceLocation parentObjLoc = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentObject.get());

                        objectTags.forEach(tK -> {
                            TagKey<? super T> parentTagKey = tK.get();

                            if (validateDupeObjectTag(parentTagKey, objectClassName, parentObjLoc)) {
                                NexusConstants.LOGGER.debug("[{}] [Tagging {}]: {} -> {}", getModId(), objectClassName, parentObjLoc, parentTagKey);

                                if (isTag.get()) trackTag(parentTagKey).addTag(parentObjLoc);
                                else trackTag(parentTagKey).addElement(parentObjLoc);
                            }
                        });

                        additionalTags.forEach(tK -> {
                            TagKey<?> parentTagKey = tK.get();

                            if (validateDupeObjectTag(parentTagKey, objectClassName, parentObjLoc)) {
                                NexusConstants.LOGGER.debug("[{}] [Tagging {}]: {} -> {} (Additional Tag for Registry: {})", getModId(), objectClassName, parentObjLoc, parentTagKey, parentTagKey.registry());

                                if (isTag.get()) trackTag(parentTagKey).addTag(parentObjLoc);
                                else trackTag(parentTagKey).addElement(parentObjLoc);
                            }
                        });
                    });
        }
    }

    /**
     * Queries and processes all entries in {@link #registryMappedBuilders}. Handles tags and their respective
     * {@linkplain ResourceLocation ResourceLocations} according to their key {@linkplain ResourceKey registry keys}.
     *
     * @param targetOutput The {@link CachedOutput} used to write the tag files to disk.
     * @param currentContents The {@link HolderLookup.Provider} used to query the registry for the presence of objects.
     * @param parentTagLookup Miscellaneous {@code interface} to allow for alternate tag lookups in order to verify tag
     *                        presence alongside {@code currentContents}.
     *
     * @return A {@link CompletableFuture} that is completed when all tag entries have been processed.
     *
     * @param <T> The parent {@link Registry} type, pertaining to its respective {@link TagKey} type(s).
     *
     * @throws IllegalArgumentException If a tag entry is missing a required reference (i.e. missing registry entry for
     * a tag or object within said defined tag entry) and {@link #validateAllEntries} is set to {@code true}.
     *
     * @see #trackTag(TagKey)
     */
    protected <T> CompletableFuture<?> serializeTagEntries(CachedOutput targetOutput, HolderLookup.Provider currentContents, TagLookup<T> parentTagLookup) {
        return CompletableFuture.allOf(registryMappedBuilders.entrySet().stream()
                .flatMap(curEntry -> {
                    ResourceKey<? extends Registry<T>> registryKey = (ResourceKey<? extends Registry<T>>) curEntry.getKey();
                    Map<ResourceLocation, TagBuilder> tagBuilders = curEntry.getValue();

                    HolderLookup.RegistryLookup<T> regBasedContentLookup = currentContents.lookupOrThrow(registryKey);
                    Predicate<ResourceLocation> elementPresenceWithinRegistryValidator = (tagLoc) -> regBasedContentLookup.get(ResourceKey.create(registryKey, tagLoc)).isPresent();
                    Predicate<ResourceLocation> tagLocalOrParentPresenceValidator = (tagLoc) -> (registryMappedBuilders.containsKey(registryKey) && registryMappedBuilders.get(registryKey).containsKey(tagLoc)) || builders.containsKey(tagLoc) || parentTagLookup.contains(TagKey.create(registryKey, tagLoc));

                    return tagBuilders.entrySet().stream()
                            .map(curTagEntry -> {
                                ResourceLocation tagLoc = curTagEntry.getKey();
                                TagBuilder tagBuilder = curTagEntry.getValue();
                                List<TagEntry> serializedTagEntries = new ObjectArrayList<>(tagBuilder.build());
                                List<TagEntry> missingSerializedTags = serializedTagEntries.stream().filter((tagEntry) -> !tagEntry.verifyIfPresent(elementPresenceWithinRegistryValidator, tagLocalOrParentPresenceValidator)).toList();
                                boolean shouldCrash = validateAllEntries() && !missingSerializedTags.isEmpty();

                                if (shouldCrash) throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s (required by mod of ID %s). Please ensure that these tags are registered and/or that their JSON files are generated beforehand (they don't have to be physically present, this primarily refers to generation order).", tagLoc, missingSerializedTags.stream().map(Objects::toString).collect(Collectors.joining(",")), modId));
                                else {
                                    if (!missingSerializedTags.isEmpty()) serializedTagEntries.removeAll(missingSerializedTags);

                                    DataResult<JsonElement> serializedTagResult = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(serializedTagEntries, false));
                                    JsonElement serializedTagJson = serializedTagResult.getOrThrow(false, LOGGER::error);
                                    PackOutput.PathProvider actualPathProvider = rootOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(registryKey));
                                    Path targetTagPath = actualPathProvider.json(tagLoc);

                                    return DataGenUtil.saveStableAndMerge(targetOutput, serializedTagJson, targetTagPath, DataGenUtil.TAG_FILE_MERGER);
                                }
                            });
                })
                .toArray(CompletableFuture[]::new));
    }

    /**
     * Refreshes/clears {@link #registryMappedBuilders} and {@link #builders} before regenerating tag entries via
     * {@link #addObjectTags(HolderLookup.Provider)} once {@link #lookupProvider} is ready.
     *
     * @return A {@link CompletableFuture} that completes when all tag entries have been generated and are ready for
     * further processing (in {@link #run(CachedOutput)} (duh)).
     */
    @Override
    protected @NotNull CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return this.lookupProvider.thenApply((curProvider) -> {
            this.builders.clear(); // JIC
            this.registryMappedBuilders.clear();

            addTags(curProvider);
            return curProvider;
        });
    }

    /**
     * Native variant of {@link #tag(TagKey)} that directly returns a {@link TagBuilder} for the given tag key. Also
     * tracks keys by their registry {@link ResourceKey ResourceKeys} in {@link #registryMappedBuilders}.
     *
     * @param tagKeyToTrack The {@link TagKey} to potentially compute a {@link TagBuilder} for.
     *
     * @return The {@link TagBuilder} for the given {@link TagKey}.
     *
     * @param <T> The provided {@link TagKey} type, pertaining to its respective object type(s).
     *
     * @see #serializeTagEntries(CachedOutput, HolderLookup.Provider, TagLookup)
     */
    protected <T> TagBuilder trackTag(TagKey<T> tagKeyToTrack) {
        return registryMappedBuilders
                .computeIfAbsent(tagKeyToTrack.registry(), (tkRegLoc) -> new Object2ObjectOpenHashMap<>())
                .computeIfAbsent(tagKeyToTrack.location(), (tkLoc) -> TagBuilder.create());
    }

    /**
     * Validates the given {@code objectLoc} based on the {@code tagKeyToTrack} against the presence of duplicates, and
     * handles duplicate cases according to the specified {@link #dupeStrat}.
     *
     * @param tagKeyToTrack The {@link TagKey} to check the presence of {@code objectLoc} within.
     * @param objectClassName The {@code class} name for the backing object. Primarily used for logging.
     * @param objectLoc The {@link ResourceLocation} representing the registry entry of an object.
     *
     * @return {@code true} if the {@code objectLoc} is not already tagged with the same {@code tagKeyToTrack},
     * {@code false} otherwise.
     *
     * @param <T> The provided {@link TagKey} type, pertaining to its respective object type.
     */
    protected <T> boolean validateDupeObjectTag(TagKey<T> tagKeyToTrack, String objectClassName, ResourceLocation objectLoc) {
        Map<ResourceLocation, TagBuilder> mappedTagBuilders = registryMappedBuilders.get(tagKeyToTrack.registry());

        if (mappedTagBuilders != null) {
            boolean objectAlreadyTaggedWithSameTag = mappedTagBuilders.get(tagKeyToTrack.location()) != null && mappedTagBuilders.get(tagKeyToTrack.location()).build().stream().anyMatch(curEntry -> curEntry.verifyIfPresent(objectLoc::equals, objectLoc::equals));

            if (objectAlreadyTaggedWithSameTag) {
                String objectName = objectLoc.toString();

                switch (getDuplicateDataPolicy()) {
                    case CRASH -> throw new IllegalStateException(String.format("Attempted to tag %s %s with duplicate tag key %s (from mod of ID %s), specified DuplicateDataPolicy is CRASH.", objectClassName, objectName, tagKeyToTrack.location(), getModId()));
                    case EXCLUDE_WARN -> {
                        NexusConstants.LOGGER.warn("Attempted to tag {} {} with duplicate tag key {} (from mod of ID {}), specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", objectClassName, objectName, tagKeyToTrack.location(), getModId());
                        return false;
                    }
                    case EXCLUDE_SILENT -> {
                        return false;
                    }
                    case OVERRIDE_WARN -> {
                        NexusConstants.LOGGER.warn("Overriding duplicate tag key {} for {} {} (from mod of ID {}), specified DuplicateDataPolicy is OVERRIDE_WARN.", tagKeyToTrack.location(), objectClassName, objectName, getModId());
                        mappedTagBuilders.put(tagKeyToTrack.location(), TagBuilder.create()); // Force override even though we compute the specific tag key after (usually)

                        return true;
                    }
                    case OVERRIDE_SILENT -> {
                        mappedTagBuilders.put(tagKeyToTrack.location(), TagBuilder.create());
                        return true;
                    }
                }
            } else return true;
        }

        return true;
    }

    @Override
    public @NotNull String getName() {
        return String.format("Tags [%s]", getModId());
    }

    @Override
    public @NotNull String getModId() {
        return modId;
    }

    @Override
    public boolean validateAllEntries() {
        return validateAllEntries;
    }

    @Override
    public @NotNull ProviderType getProviderType() {
        return NexusProviderTypes.TAG_PROVIDER;
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }
}
