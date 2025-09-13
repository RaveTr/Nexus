package com.mememan.nexus.datagen.standard.tag;

import com.google.gson.JsonElement;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.tag.TagBasedPropertyWrapper;
import com.mememan.nexus.tag.TagWrapper;
import com.mememan.nexus.util.StringUtil;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class StandardTagProvider<T> extends IntrinsicHolderTagsProvider<T> implements ModDataProvider {
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final ObjectArrayList<TagWrapper<? extends T, TagKey<T>>> mappedModObjectTags;
    protected final Object2ObjectOpenHashMap<T, ObjectArrayList<TagKey<T>>> trackedTaggedObjects = new Object2ObjectOpenHashMap<>();
    protected final Object2ObjectOpenHashMap<TagKey<T>, ObjectArrayList<TagKey<T>>> trackedTags = new Object2ObjectOpenHashMap<>();

    public StandardTagProvider(PackOutput output, ResourceKey<? extends Registry<T>> targetTagObjectRegistry, CompletableFuture<HolderLookup.Provider> objectLookupProvider, Function<T, ResourceKey<T>> objectKeyExtractor, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(output, targetTagObjectRegistry, objectLookupProvider, objectKeyExtractor);

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedModObjectTags = TagWrapper.getCachedTWEntries().stream()
                .filter(curTW -> !curTW.excludeFromNativeDatagen() && curTW.getParentTag().get().isFor(registryKey) && curTW.getParentTag().get().location().getNamespace().equals(modId))
                .map(curTW -> (TagWrapper<? extends T, TagKey<T>>) curTW)
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    public StandardTagProvider(PackOutput output, ResourceKey<? extends Registry<T>> targetTagObjectRegistry, CompletableFuture<HolderLookup.Provider> objectLookupProvider, CompletableFuture<TagLookup<T>> tagLookup, Function<T, ResourceKey<T>> objectKeyExtractor, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(output, targetTagObjectRegistry, objectLookupProvider, tagLookup, objectKeyExtractor);

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedModObjectTags = TagWrapper.getCachedTWEntries().stream()
                .filter(curTW -> !curTW.excludeFromNativeDatagen() && curTW.getParentTag().get().isFor(registryKey) && curTW.getParentTag().get().location().getNamespace().equals(modId))
                .map(curTW -> (TagWrapper<? extends T, TagKey<T>>) curTW)
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput output) {
        return createContentsProvider().thenApply((contentProvider) -> {
            this.contentsDone.complete(null);
            return contentProvider;
        }).thenCombineAsync(parentProvider, (parentContentProvider, currentTag) -> {
            record CombinedData<T>(HolderLookup.Provider currentContents, TagLookup<T> parentTagLookup) {}

            return new CombinedData<T>(parentContentProvider, currentTag);
        }).thenCompose((combinedTagLookupData) -> {
            HolderLookup.RegistryLookup<T> regBasedContentLookup = combinedTagLookupData.currentContents.lookupOrThrow(registryKey);
            Predicate<ResourceLocation> elementPresenceWithinRegistryValidator = (tagLoc) -> regBasedContentLookup.get(ResourceKey.create(registryKey, tagLoc)).isPresent();
            Predicate<ResourceLocation> tagLocalOrParentPresenceValidator = (tagLoc) -> builders.containsKey(tagLoc) || combinedTagLookupData.parentTagLookup.contains(TagKey.create(registryKey, tagLoc));

            return CompletableFuture.allOf(builders.entrySet().stream().map((tagEntry) -> {
                ResourceLocation tagLoc = tagEntry.getKey();
                TagBuilder tagBuilder = tagEntry.getValue();
                List<TagEntry> serializedTagEntries = tagBuilder.build();
                List<TagEntry> missingSerializedTags = serializedTagEntries.stream().filter((curTagEntry) -> !curTagEntry.verifyIfPresent(elementPresenceWithinRegistryValidator, tagLocalOrParentPresenceValidator)).toList();
                boolean shouldCrash = validateAllEntries() && !missingSerializedTags.isEmpty();

                if (shouldCrash) throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s (required by mod of ID %s). Please ensure that these tags are registered and/or that their JSON files are generated beforehand (they don't have to be physically present, this primarily refers to generation order).", tagLoc, missingSerializedTags.stream().map(Objects::toString).collect(Collectors.joining(",")), modId));
                else {
                    DataResult<JsonElement> serializedTagResult = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(serializedTagEntries, false));
                    JsonElement serializedTagJson = serializedTagResult.getOrThrow(false, LOGGER::error);
                    Path targetTagPath = pathProvider.json(tagLoc);

                    return DataProvider.saveStable(output, serializedTagJson, targetTagPath);
                }
            }).toArray(CompletableFuture[]::new));
        });
    }

    public @NotNull <O> CompletableFuture<?> dummy(CachedOutput output) {
        List<? extends TagBasedPropertyWrapper<O, ?, ?>> propertyWrappers = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(TagBasedPropertyWrapper.class, modId);

        return createContentsProvider().thenApply((contentProvider) -> {
            this.contentsDone.complete(null);
            return contentProvider;
        }).thenCombineAsync(parentProvider, (parentContentProvider, currentTag) -> {
            record CombinedData<T>(HolderLookup.Provider currentContents, TagLookup<T> parentTagLookup) {}

            return new CombinedData<T>(parentContentProvider, currentTag);
        }).thenCompose((combinedTagLookupData) -> {
            HolderLookup.RegistryLookup<T> regBasedContentLookup = combinedTagLookupData.currentContents.lookupOrThrow(registryKey);
            Predicate<ResourceLocation> elementPresenceWithinRegistryValidator = (tagLoc) -> regBasedContentLookup.get(ResourceKey.create(registryKey, tagLoc)).isPresent();
            Predicate<ResourceLocation> tagLocalOrParentPresenceValidator = (tagLoc) -> builders.containsKey(tagLoc) || combinedTagLookupData.parentTagLookup.contains(TagKey.create(registryKey, tagLoc));

            return CompletableFuture.allOf(propertyWrappers.stream().map(pw -> {


                return DataProvider.saveStable(null, null, null);
            }).toArray(CompletableFuture[]::new));
        });
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
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }

    protected abstract void addObjectTags(HolderLookup.Provider provider);

    @NotNull
    @Override
    public abstract ProviderType getProviderType();

    @Nullable
    protected abstract String getObjectName(T targetObj);

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        refreshTrackedTagData();

        addObjectTags(provider);
        addTagWrappers();
    }

    protected void refreshTrackedTagData() {
        this.trackedTaggedObjects.clear();
        this.trackedTags.clear();
    }

    protected void addTagWrappers() {
        if (!mappedModObjectTags.isEmpty()) {
            String potentialTypeName = StringUtil.toTitleCase(registryKey.location().getPath());

            mappedModObjectTags.forEach(twEntry -> {
                TagKey<T> parentTagKey = twEntry.getParentTag().get();

                twEntry.getPredefinedTagEntries().forEach(tagEntry -> {
                    T objectTagEntry = tagEntry.get();

                    if (objectTagEntry != null) {
                        if (validateDupeObjectTag(objectTagEntry, parentTagKey)) {
                            NexusConstants.LOGGER.debug("[Tagging {}}]: {} -> {} (For mod of ID: {})", potentialTypeName, objectTagEntry, parentTagKey, modId);
                            tag(parentTagKey).add(objectTagEntry);
                        }
                    }
                });

                twEntry.getStoredTags().forEach(tagKeyEntry -> {
                    TagKey<T> storedTagKeyEntry = tagKeyEntry.get();

                    if (storedTagKeyEntry != null) {
                        if (validateDupeTag(parentTagKey, storedTagKeyEntry)) {
                            NexusConstants.LOGGER.debug("[Tagging {} Tag]: {} -> {} (For mod of ID: {})", potentialTypeName, storedTagKeyEntry, parentTagKey, modId);

                            if (validateAllEntries()) tag(storedTagKeyEntry); // At least have the file for the tag present so that we can actually reference it without crashing if validateAllEntries is true
                            tag(parentTagKey).addTag(storedTagKeyEntry);
                        }
                    }
                });

                twEntry.getParentTags().forEach(parentTagKeyEntry -> {
                    TagKey<T> storedParentTagKeyEntry = parentTagKeyEntry.get();

                    if (storedParentTagKeyEntry != null) {
                        if (validateDupeTag(storedParentTagKeyEntry, parentTagKey)) {
                            NexusConstants.LOGGER.debug("[Tagging {} Tag]: {} -> {} (For mod of ID: {})", potentialTypeName, parentTagKey, storedParentTagKeyEntry, modId);

                            if (validateAllEntries()) tag(parentTagKey); // At least have the file for the tag present so that we can actually reference it without crashing if validateAllEntries is true
                            tag(storedParentTagKeyEntry).addTag(parentTagKey);
                        }
                    }
                });
            });
        }
    }

    protected boolean validateDupeObjectTag(T targetObject, TagKey<T> targetTagKey) {
        ObjectArrayList<TagKey<T>> targetObjTags = trackedTaggedObjects.computeIfAbsent(targetObject, oK -> new ObjectArrayList<>());
        ResourceLocation tagLoc = targetTagKey.location();
        String potentialTypeName = registryKey.location().getPath();

        if (targetObjTags.stream().map(TagKey::location).anyMatch(tagLoc::equals)) {
            switch (getDuplicateDataPolicy()) {
                case CRASH -> throw new IllegalStateException(String.format("Attempted to tag %s %s with duplicate tag key %s from mod of ID %s, specified DuplicateDataPolicy is CRASH.", potentialTypeName, getObjectName(targetObject), tagLoc, getModId()));
                case EXCLUDE_WARN -> {
                    NexusConstants.LOGGER.warn("Attempted to tag {} {} with duplicate tag key {} from mod of ID {}, specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", potentialTypeName, getObjectName(targetObject), tagLoc, getModId());
                    return false;
                }
                case EXCLUDE_SILENT -> {
                    return false;
                }
                case OVERRIDE_WARN -> {
                    NexusConstants.LOGGER.warn("Overriding duplicate {} tag {} for {} {} from mod of ID {}, specified DuplicateDataPolicy is OVERRIDE_WARN.", potentialTypeName, tagLoc, potentialTypeName, getObjectName(targetObject), getModId());
                    return true;
                }
                case OVERRIDE_SILENT -> {
                    return true;
                }
            }
        } else targetObjTags.add(targetTagKey);

        return true;
    }

    protected boolean validateDupeTag(TagKey<T> targetTagKey, TagKey<T> containedTagKey) {
        ObjectArrayList<TagKey<T>> targetTags = trackedTags.computeIfAbsent(targetTagKey, oK -> new ObjectArrayList<>());
        ResourceLocation parentTagLoc = targetTagKey.location();
        ResourceLocation tagLoc = containedTagKey.location();

        if (targetTags.stream().map(TagKey::location).anyMatch(tagLoc::equals)) {
            switch (getDuplicateDataPolicy()) {
                case CRASH -> throw new IllegalStateException(String.format("Attempted to tag TagKey %s with duplicate tag key %s from mod of ID %s, specified DuplicateDataPolicy is CRASH.", parentTagLoc, tagLoc, getModId()));
                case EXCLUDE_WARN -> {
                    NexusConstants.LOGGER.warn("Attempted to tag TagKey {} with duplicate tag key {} from mod of ID {}, specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", parentTagLoc, tagLoc, getModId());
                    return false;
                }
                case EXCLUDE_SILENT -> {
                    return false;
                }
                case OVERRIDE_WARN -> {
                    NexusConstants.LOGGER.warn("Overriding duplicate tag {} for TagKey {} from mod of ID {}, specified DuplicateDataPolicy is OVERRIDE_WARN.", tagLoc, parentTagLoc, getModId());
                    return true;
                }
                case OVERRIDE_SILENT -> {
                    return true;
                }
            }
        } else targetTags.add(containedTagKey);

        return true;
    }
}
