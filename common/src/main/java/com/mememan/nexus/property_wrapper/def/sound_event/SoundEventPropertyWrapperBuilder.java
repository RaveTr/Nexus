package com.mememan.nexus.property_wrapper.def.sound_event;

import com.mememan.nexus.client.sound.SoundPropertyHolder;
import com.mememan.nexus.datagen.standard.resource_pack.StandardSoundDefinitionProvider;
import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Definite builder implementation for {@link SoundEvent} objects, with constrained generic types for {@code SELF} and
 * {@code DGPW}. Includes relevant properties for sound events, plus localization support. An instance here represents
 * the properties of a singular {@link SoundEvent} by ID.
 *
 * @param <SE> Any {@link SoundEvent} type.
 *
 * @see SoundEventPropertyWrapper
 * @see StandardSoundDefinitionProvider
 */
public class SoundEventPropertyWrapperBuilder<SE extends SoundEvent> extends BaseDataGenPropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>> implements DefaultableLanguageBasedPropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>> {
    protected final SpecializedLanguagePropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>> compositeLanguageBuilder;
    protected final ObjectArrayList<SoundPropertyHolder> mappedSoundPropertyHolders = new ObjectArrayList<>();
    protected String subtitleLangKey;
    protected boolean replaceOriginal = false;

    public SoundEventPropertyWrapperBuilder(@NotNull SoundEventPropertyWrapper<SE> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get()
                .bypassDefaultTranslation(); // By default, for convenience (cuz SoundEvents don't have language keys on their own, duh)
    }

    @Override
    public SoundEventPropertyWrapperBuilder<SE> copyFrom(SoundEventPropertyWrapper<SE> propertyWrapper) {
        DefaultableLanguageBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper)
                .setSoundDefinitions(propertyWrapper.getSoundPropertyHolders())
                .withSubtitleKey(propertyWrapper.getSubtitleLangKey().orElse(null))
                .replaceOriginal(propertyWrapper.replacesOriginalSoundEvent());
    }

    /**
     * Specifies a sound to be mapped to the parent {@link SoundEvent}. Appends to the existing sound definitions.
     *
     * @param soundPropertyHolder The {@link SoundPropertyHolder} representing the properties of the parent sound to
     *                            be added.
     *
     * @return {@link #self()} (builder method).
     *
     * @see SoundPropertyHolder
     * @see #withSoundDefinitions(List)
     * @see #setSoundDefinitions(List)
     */
    public SoundEventPropertyWrapperBuilder<SE> withSoundDefinition(SoundPropertyHolder soundPropertyHolder) {
        this.mappedSoundPropertyHolders.add(soundPropertyHolder);
        return self();
    }

    /**
     * Adds multiple sound definitions to the parent {@link SoundEvent}. Appends to the existing sound definitions.
     *
     * @param soundPropertyHolders The {@link SoundPropertyHolder}s representing the properties of the parent sound to
     *                             be added.
     *
     * @return {@link #self()} (builder method).
     *
     * @see SoundPropertyHolder
     * @see #withSoundDefinition(SoundPropertyHolder)
     * @see #setSoundDefinitions(List)
     */
    public SoundEventPropertyWrapperBuilder<SE> withSoundDefinitions(List<SoundPropertyHolder> soundPropertyHolders) {
        this.mappedSoundPropertyHolders.addAll(soundPropertyHolders);
        return self();
    }

    /**
     * Sets the sound definitions for the parent {@link SoundEvent}.
     *
     * @param soundPropertyHolders The {@link SoundPropertyHolder}s representing the properties of the parent sound to
     *                             be set.
     *
     * @return {@link #self()} (builder method).
     *
     * @see SoundPropertyHolder
     * @see #withSoundDefinition(SoundPropertyHolder)
     * @see #withSoundDefinitions(List)
     */
    public SoundEventPropertyWrapperBuilder<SE> setSoundDefinitions(List<SoundPropertyHolder> soundPropertyHolders) {
        this.mappedSoundPropertyHolders.clear();
        this.mappedSoundPropertyHolders.addAll(soundPropertyHolders);
        return self();
    }

    /**
     * Specifies the language key for the subtitle of the parent {@link SoundEvent}. Does NOT add generate an entry for
     * it in any language files.
     *
     * @param subtitleLangKey The language key for the subtitle of the parent {@link SoundEvent}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withSubtitleKey(String, String)
     */
    public SoundEventPropertyWrapperBuilder<SE> withSubtitleKey(String subtitleLangKey) {
        this.subtitleLangKey = subtitleLangKey;
        return self();
    }

    /**
     * Convenient overload of {@link #withSubtitleKey(String)}. Localizes the {@code subtitleLangKey} passed in using
     * {@code localizationSubtitle}.
     *
     * @param subtitleLangKey The language key for the subtitle of the parent {@link SoundEvent}.
     * @param localizedSubtitle The localized value for the subtitle of the parent {@link SoundEvent}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withSubtitleKey(String)
     */
    public SoundEventPropertyWrapperBuilder<SE> withSubtitleKey(String subtitleLangKey, String localizedSubtitle) {
        return withSubtitleKey(subtitleLangKey)
                .withAdditionalLocalizationKey(subtitleLangKey, localizedSubtitle);
    }

    /**
     * Specifies whether the parent {@link SoundEvent} should replace the original sound event of the same ID, if
     * present.
     *
     * @param replaceOriginal Whether the parent {@link SoundEvent} should replace the original sound event of the same
     *                       ID.
     *
     * @return {@link #self()} (builder method).
     */
    public SoundEventPropertyWrapperBuilder<SE> replaceOriginal(boolean replaceOriginal) {
        this.replaceOriginal = replaceOriginal;
        return self();
    }

    /**
     * Overloaded variant of {@link #replaceOriginal(boolean)} with {@code replaceOriginal} set to {@code true}.
     *
     * @return {@link #self()} (builder method).
     */
    public SoundEventPropertyWrapperBuilder<SE> replaceOriginal() {
        return replaceOriginal(true);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>>> getSpecializedLanguageBuilder() {
        return Optional.of(compositeLanguageBuilder);
    }
}
