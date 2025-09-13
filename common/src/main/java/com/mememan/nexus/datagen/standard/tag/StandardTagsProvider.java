package com.mememan.nexus.datagen.standard.tag;

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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
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

public class StandardTagsProvider extends TagsProvider<Object> implements ModDataProvider {
    protected final PackOutput rootOutput;
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final List<? extends TagBasedPropertyWrapper<?, ?, ?>> mappedTagPWs;
    protected final Map<ResourceKey<? extends Registry<?>>, Map<ResourceLocation, TagBuilder>> registryMappedBuilders = new Object2ObjectOpenHashMap<>();

    public StandardTagsProvider(PackOutput targetOutput, CompletableFuture<HolderLookup.Provider> regLookup, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetOutput, (ResourceKey<? extends Registry<Object>>) DataGenPropertyWrapper.RegistryLookupContainer.UNMAPPED_REGISTRY, regLookup);

        this.rootOutput = targetOutput;

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedTagPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(TagBasedPropertyWrapper.class, modId);
    }

    public StandardTagsProvider(PackOutput targetOutput, CompletableFuture<HolderLookup.Provider> regLookup, CompletableFuture<TagLookup<Object>> tagLookupFuture, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        super(targetOutput, (ResourceKey<? extends Registry<Object>>) DataGenPropertyWrapper.RegistryLookupContainer.UNMAPPED_REGISTRY, regLookup, tagLookupFuture);

        this.rootOutput = targetOutput;

        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.mappedTagPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(TagBasedPropertyWrapper.class, modId);
    }

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

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        registryMappedBuilders.clear();

        addObjectTags(provider);
    }

    protected <T> void addObjectTags(HolderLookup.Provider provider) {
        if (!mappedTagPWs.isEmpty()) {
            mappedTagPWs.stream()
                    .map(curPW -> (TagBasedPropertyWrapper<T, ?, ?>) curPW)
                    .forEach(curPW -> {
                        List<Supplier<TagKey<? super T>>> objectTags = curPW.getObjectTags();
                        List<Supplier<TagKey<?>>> additionalTags = curPW.getAdditionalTags();
                        Supplier<T> parentObject = curPW.getParentObject();
                        String objectName = curPW.getObjectDescriptionId();
                        String objectClassName = parentObject.get().getClass().getSimpleName();
                        AtomicBoolean isTag = new AtomicBoolean();
                        ResourceLocation parentObjLoc = parentObject.get() instanceof ResourceKey<?>
                                ? ((ResourceKey<T>) parentObject.get()).location()
                                : ((Registry<T>) BuiltInRegistries.REGISTRY.get(curPW.getObjectRegistryKey()
                                .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to tag a non-registry object of type %s: %s", objectClassName, objectName))).location()))
                                .getResourceKey(parentObject.get())
                                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for object of type %s: %s", objectClassName, objectName)))
                                .location();

                        if (curPW instanceof TagPropertyWrapper<?, ?> curTPW) { // Do tag-specific processing here
                            TagPropertyWrapper<T, TagKey<T>> curTagPW = (TagPropertyWrapper<T, TagKey<T>>) curPW;

                            isTag.set(true);
                        }

                        if (!isTag.get() && objectTags.isEmpty() && additionalTags.isEmpty() && (validateAllEntries() || curPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false))) {
                            throw new NullPointerException(String.format("Missing tag entry for %s: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the object itself requires validation through DataGenBasedPropertyWrapper#getProviderTypeRequisites().", objectClassName, curPW.getObjectDescriptionId(), modId));
                        }

                        objectTags.forEach(tK -> {
                            TagKey<? super T> parentTagKey = tK.get();

                            if (validateDupeObjectTag(parentTagKey, objectClassName, parentObjLoc)) {
                                NexusConstants.LOGGER.debug("[{}] [Tagging {}]: {} -> {}", getModId(), objectClassName, objectName, parentTagKey);

                                if (isTag.get()) trackTag(parentTagKey).addTag(parentObjLoc);
                                else trackTag(parentTagKey).addElement(parentObjLoc);
                            }
                        });

                        additionalTags.forEach(tK -> {
                            TagKey<?> parentTagKey = tK.get();

                            if (validateDupeObjectTag(parentTagKey, objectClassName, parentObjLoc)) {
                                NexusConstants.LOGGER.debug("[{}] [Tagging {}]: {} -> {} (Additional Tag for Registry: {})", getModId(), objectClassName, objectName, parentTagKey, parentTagKey.registry());

                                if (isTag.get()) trackTag(parentTagKey).addTag(parentObjLoc);
                                else trackTag(parentTagKey).addElement(parentObjLoc);
                            }
                        });
                    });
        }
    }

    protected <T> CompletableFuture<?> serializeTagEntries(CachedOutput targetOutput, HolderLookup.Provider currentContents, TagLookup<T> parentTagLookup) {
        return CompletableFuture.allOf(registryMappedBuilders.entrySet().stream()
                .flatMap(curEntry -> {
                    ResourceKey<? extends Registry<T>> registryKey = (ResourceKey<? extends Registry<T>>) curEntry.getKey();
                    Map<ResourceLocation, TagBuilder> tagBuilders = curEntry.getValue();

                    HolderLookup.RegistryLookup<T> regBasedContentLookup = currentContents.lookupOrThrow(registryKey);
                    Predicate<ResourceLocation> elementPresenceWithinRegistryValidator = (tagLoc) -> regBasedContentLookup.get(ResourceKey.create(registryKey, tagLoc)).isPresent();
                    Predicate<ResourceLocation> tagLocalOrParentPresenceValidator = (tagLoc) -> builders.containsKey(tagLoc) || parentTagLookup.contains(TagKey.create(registryKey, tagLoc));

                    return tagBuilders.entrySet().stream()
                            .map(curTagEntry -> {
                                ResourceLocation tagLoc = curTagEntry.getKey();
                                TagBuilder tagBuilder = curTagEntry.getValue();
                                List<TagEntry> serializedTagEntries = tagBuilder.build();
                                List<TagEntry> missingSerializedTags = serializedTagEntries.stream().filter((tagEntry) -> !tagEntry.verifyIfPresent(elementPresenceWithinRegistryValidator, tagLocalOrParentPresenceValidator)).toList();
                                boolean shouldCrash = validateAllEntries() && !missingSerializedTags.isEmpty();

                                if (shouldCrash) throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s (required by mod of ID %s). Please ensure that these tags are registered and/or that their JSON files are generated beforehand (they don't have to be physically present, this primarily refers to generation order).", tagLoc, missingSerializedTags.stream().map(Objects::toString).collect(Collectors.joining(",")), modId));
                                else {
                                    DataResult<JsonElement> serializedTagResult = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(serializedTagEntries, false));
                                    JsonElement serializedTagJson = serializedTagResult.getOrThrow(false, LOGGER::error);
                                    PackOutput.PathProvider actualPathProvider = rootOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(registryKey));
                                    Path targetTagPath = actualPathProvider.json(tagLoc);

                                    return DataProvider.saveStable(targetOutput, serializedTagJson, targetTagPath);
                                }
                            });
                })
                .toArray(CompletableFuture[]::new));
    }

    protected <T> TagBuilder trackTag(TagKey<T> tagKeyToTrack) {
        return registryMappedBuilders
                .computeIfAbsent(tagKeyToTrack.registry(), (tkRegLoc) -> new Object2ObjectOpenHashMap<>())
                .computeIfAbsent(tagKeyToTrack.location(), (tkLoc) -> TagBuilder.create());
    }

    protected <T> boolean validateDupeObjectTag(TagKey<T> tagKeyToTrack, String objectClassName, ResourceLocation objectLoc) {
        Map<ResourceLocation, TagBuilder> mappedTagBuilders = registryMappedBuilders.get(tagKeyToTrack.registry());

        if (mappedTagBuilders != null) {
            boolean objectAlreadyTaggedWithSameTag = mappedTagBuilders.get(tagKeyToTrack.location()).build().stream().anyMatch(curEntry -> curEntry.verifyIfPresent(objectLoc::equals, objectLoc::equals));

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

    protected <T> ResourceLocation makeAndMarkTag(TagKey<T> targetKey, AtomicBoolean tagFlag) {
        tagFlag.set(true);
        return targetKey.location();
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
