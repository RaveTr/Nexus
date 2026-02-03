package com.mememan.nexus.datagen.standard.resource_pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mememan.nexus.client.sound.SoundPropertyHolder;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.def.sound_event.SoundEventPropertyWrapper;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Standard loader-agnostic mod-specific sound definition provider in Nexus API. Instanced based on the provided mod ID.
 * Handles generation of the {@code sounds.json} file for a given mod within its resource pack. Generates all sound
 * definitions independently based on valid {@link SoundEventPropertyWrapper} entries.
 *
 * @see SoundEventPropertyWrapper
 * @see SoundEngine
 * @see SoundPropertyHolder
 */
public class StandardSoundDefinitionProvider implements ModDataProvider {
    protected final PackOutput rootOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modId;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final List<SoundEventPropertyWrapper<? extends SoundEvent>> mappedSoundEventPWs;

    public StandardSoundDefinitionProvider(PackOutput targetOutput, String modId, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        this.rootOutput = targetOutput;
        this.modId = modId;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.pathProvider = targetOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "");
        this.mappedSoundEventPWs = PropertyWrapper.PropertyWrappersContainer.getInferrableDataGennableWrappersOfType(SoundEventPropertyWrapper.class, modId);
    }

    /**
     * Serializes all valid sound definitions, based on all found {@link SoundEventPropertyWrapper} instances stored in
     * {@link #mappedSoundEventPWs}.
     *
     * @param cachedOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return A {@link CompletableFuture} representing the completion of sound definition serialization.
     *
     * @see #serializeSoundDefinitions(JsonObject)
     */
    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput cachedOutput) {
        JsonObject soundsFile = new JsonObject();

        serializeSoundDefinitions(soundsFile);

        return CompletableFuture.completedFuture(DataProvider.saveStable(cachedOutput, soundsFile, pathProvider.json(new ResourceLocation(modId, "sounds"))));
    }

    /**
     *
     */
    protected void serializeSoundDefinitions(JsonObject soundsFile) {
        mappedSoundEventPWs.forEach(curPW -> {
            ResourceLocation soundEventRL = curPW.getParentObject().get().getLocation();

            soundsFile.add(soundEventRL.getPath(), serializeSoundDefinition(curPW));
        });
    }

    protected <SE extends SoundEvent> JsonElement serializeSoundDefinition(SoundEventPropertyWrapper<SE> targetSEPW) {
        JsonObject mappedObj = new JsonObject();
        boolean replaceOriginal = targetSEPW.replacesOriginalSoundEvent();

        if (replaceOriginal) mappedObj.addProperty("replace", replaceOriginal);

        JsonArray actualSoundFileLocs = new JsonArray();
        List<SoundPropertyHolder> soundPropertyHolders = targetSEPW.getSoundPropertyHolders();

        if (!soundPropertyHolders.isEmpty()) {
            for (SoundPropertyHolder curHolder : soundPropertyHolders) {
                JsonObject curHolderObj = new JsonObject();

                curHolderObj.addProperty("name", curHolder.soundFileLocation().toString());

                if (!curHolder.hasDefaultProperties()) {
                    if (!curHolder.hasDefaultVolume()) curHolderObj.addProperty("volume", curHolder.volume());
                    if (!curHolder.hasDefaultPitch()) curHolderObj.addProperty("pitch", curHolder.pitch());
                    if (!curHolder.hasDefaultWeight()) curHolderObj.addProperty("weight", curHolder.weight());
                    if (!curHolder.streamsByDefault()) curHolderObj.addProperty("stream", curHolder.stream());
                    if (!curHolder.hasDefaultAttenuationDistance()) curHolderObj.addProperty("attenuation_distance", curHolder.attenuationDistance());
                    if (!curHolder.preloadsByDefault()) curHolderObj.addProperty("preload", curHolder.preload());
                    if (!curHolder.hasDefaultSoundType()) curHolderObj.addProperty("type", curHolder.soundType().name().toLowerCase());
                }

                actualSoundFileLocs.add(curHolderObj);
            }
        }

        mappedObj.add("sounds", actualSoundFileLocs);

        targetSEPW.getSubtitleLangKey().ifPresent(subtitleLangKey -> mappedObj.addProperty("subtitle", subtitleLangKey));

        return mappedObj;
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
        return NexusProviderTypes.SOUND_DEFINITION_PROVIDER;
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }
}
