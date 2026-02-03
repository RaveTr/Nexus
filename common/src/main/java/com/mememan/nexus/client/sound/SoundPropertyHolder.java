package com.mememan.nexus.client.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Objects;

/**
 * Holder {@code record} for all known sound event properties grouped together for convenience.
 *
 * @param soundFileLocation The {@link ResourceLocation} pointing towards the sound file to play, minus the extension.
 *                          Is treated as a {@link SoundEvent} ID if {@code soundType} is {@link SoundType#EVENT}.
 * @param volume The volume of the sound, ranging from 0.0F to 1.0F.
 * @param pitch The pitch of the sound, ranging from 0.0F to 1.0F.
 * @param weight The weight of the sound, indicating prominence in the sound pool (has no effect if this holder is the
 *               only one in the sound pool).
 * @param stream Whether the sound should be streamed or not. Longer sounds (e.g. soundtracks) benefit from this being
 *               set to {@code true}.
 * @param attenuationDistance The distance at which the given sound will attenuate (i.e. reduction rate modifier).
 * @param preload Whether the sound should be preloaded or not. Used to load during pack loading instead of when the
 *                sound is first played.
 * @param soundType How {@code soundFileLocation} is treated, either {@link SoundType#EVENT} or {@link SoundType#FILE}.
 *
 * @see <a href="https://minecraft.wiki/w/Sounds.json#Java_Edition">Minecraft Wiki: Sounds.json (Java Edition)</a>
 * @see SoundType
 */
public record SoundPropertyHolder(ResourceLocation soundFileLocation, float volume, float pitch, int weight, boolean stream, float attenuationDistance, boolean preload, SoundType soundType) {
    public static final float DEFAULT_VOLUME = 1.0F;
    public static final float DEFAULT_PITCH = 1.0F;
    public static final int DEFAULT_WEIGHT = 1;
    public static final boolean DEFAULT_STREAM = false;
    public static final float DEFAULT_ATTENUATION_DISTANCE = 16.0F;
    public static final boolean DEFAULT_PRELOAD = false;
    public static final SoundType DEFAULT_SOUND_TYPE = SoundType.FILE;

    /**
     * Overloaded variant of {@link SoundPropertyHolder}, with the {@code soundType} set to {@link #DEFAULT_SOUND_TYPE}.
     *
     * @param soundFileLocation The {@link ResourceLocation} pointing towards the sound file to play, minus the extension.
     *                          Is treated as a {@link SoundEvent} ID if {@code soundType} is {@link SoundType#EVENT}.
     * @param volume The volume of the sound, ranging from 0.0F to 1.0F.
     * @param pitch The pitch of the sound, ranging from 0.0F to 1.0F.
     * @param weight The weight of the sound, indicating prominence in the sound pool (has no effect if this holder is the
     *               only one in the sound pool).
     * @param stream Whether the sound should be streamed or not. Longer sounds (e.g. soundtracks) benefit from this being
     *               set to {@code true}.
     * @param attenuationDistance The distance at which the given sound will attenuate (i.e. reduction rate modifier).
     * @param preload Whether the sound should be preloaded or not. Used to load during pack loading instead of when the
     *                sound is first played.
     */
    public SoundPropertyHolder(ResourceLocation soundFileLocation, float volume, float pitch, int weight, boolean stream, float attenuationDistance, boolean preload) {
        this(soundFileLocation, volume, pitch, weight, stream, attenuationDistance, preload, DEFAULT_SOUND_TYPE);
    }

    /**
     * Overloaded variant of {@link SoundPropertyHolder}, with all properties set to their default values.
     *
     * @param soundFileLocation The {@link ResourceLocation} pointing towards the sound file to play, minus the extension.
     *                          Is treated as a {@link SoundEvent} ID if {@code soundType} is {@link SoundType#EVENT}.
     */
    public SoundPropertyHolder(ResourceLocation soundFileLocation) {
        this(soundFileLocation, DEFAULT_VOLUME, DEFAULT_PITCH, DEFAULT_WEIGHT, DEFAULT_STREAM, DEFAULT_ATTENUATION_DISTANCE, DEFAULT_PRELOAD, DEFAULT_SOUND_TYPE);
    }

    /**
     * Checks whether this holder's properties are all set to their default values.
     *
     * @return {@code true} if all properties are set to their default values, {@code false} otherwise.
     */
    public boolean hasDefaultProperties() {
        return Objects.equals(soundType, DEFAULT_SOUND_TYPE)
                && Float.compare(volume, DEFAULT_VOLUME) == 0
                && Float.compare(pitch, DEFAULT_PITCH) == 0
                && weight == DEFAULT_WEIGHT
                && Boolean.compare(stream, DEFAULT_STREAM) == 0
                && Float.compare(attenuationDistance, DEFAULT_ATTENUATION_DISTANCE) == 0.0F
                && Boolean.compare(preload, DEFAULT_PRELOAD) == 0;
    }

    /**
     * Checks whether this holder's {@code soundType} is set to its default value.
     *
     * @return {@code true} if {@code soundType} is set to its default value, {@code false} otherwise.
     */
    public boolean hasDefaultSoundType() {
        return Objects.equals(soundType, DEFAULT_SOUND_TYPE);
    }

    /**
     * Checks whether this holder's {@code volume} is set to its default value.
     *
     * @return {@code true} if {@code volume} is set to its default value, {@code false} otherwise.
     */
    public boolean hasDefaultVolume() {
        return Float.compare(volume, DEFAULT_VOLUME) == 0;
    }

    /**
     * Checks whether this holder's {@code pitch} is set to its default value.
     *
     * @return {@code true} if {@code pitch} is set to its default value, {@code false} otherwise.
     */
    public boolean hasDefaultPitch() {
        return Float.compare(pitch, DEFAULT_PITCH) == 0;
    }

    /**
     * Checks whether this holder's {@code weight} is set to its default value.
     *
     * @return {@code true} if {@code weight} is set to its default value, {@code false} otherwise.
     */
    public boolean hasDefaultWeight() {
        return weight == DEFAULT_WEIGHT;
    }

    /**
     * Checks whether this holder's {@code stream} is set to its default value.
     *
     * @return {@code true} if {@code stream} is set to its default value, {@code false} otherwise.
     */
    public boolean streamsByDefault() {
        return Boolean.compare(stream, DEFAULT_STREAM) == 0;
    }

    /**
     * Checks whether this holder's {@code attenuationDistance} is set to its default value.
     *
     * @return {@code true} if {@code attenuationDistance} is set to its default value, {@code false} otherwise.
     */
    public boolean hasDefaultAttenuationDistance() {
        return Float.compare(attenuationDistance, DEFAULT_ATTENUATION_DISTANCE) == 0;
    }

    /**
     * Checks whether this holder's {@code preload} is set to its default value.
     *
     * @return {@code true} if {@code preload} is set to its default value, {@code false} otherwise.
     */
    public boolean preloadsByDefault() {
        return Boolean.compare(preload, DEFAULT_PRELOAD) == 0;
    }

    /**
     * Basic holder {@code enum} that indicates a given {@linkplain SoundEvent SoundEvent's} type. See entries for
     * more info.
     *
     * @see SoundType#EVENT
     * @see SoundType#FILE
     * @see SoundPropertyHolder#soundType()
     */
    public enum SoundType {
        /**
         * Indicates that a given {@link SoundEvent} is the registry ID of some other {@link SoundEvent}.
         */
        EVENT,
        /**
         * Indicates that a given {@link SoundEvent} is a file path pointing to a sound file.
         */
        FILE
    }
}
