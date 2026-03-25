package com.mememan.nexus.property_wrapper.def.sound_event;

import com.mememan.nexus.client.sound.SoundPropertyHolder;
import com.mememan.nexus.datagen.standard.resource_pack.StandardSoundDefinitionProvider;
import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Definite wrapper implementation for {@link SoundEvent} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}. Properties described in this wrapper represent the properties of a singular {@link SoundEvent} by ID,
 * as specified in the standard sound definition provider {@code class} (see references below).
 *
 * @param <SE> Any {@link SoundEvent} type.
 *
 * @see SoundEventPropertyWrapperBuilder
 * @see StandardSoundDefinitionProvider
 */
public class SoundEventPropertyWrapper<SE extends SoundEvent> extends BaseDataGenPropertyWrapper<SE, SoundEventPropertyWrapper<SE>, SoundEventPropertyWrapperBuilder<SE>> implements DefaultableLanguageBasedPropertyWrapper<SE, SoundEventPropertyWrapper<SE>, SoundEventPropertyWrapperBuilder<SE>> {
    protected final SpecializedLanguagePropertyWrapper<SE, ?, ?> compositeLanguageWrapper;

    public SoundEventPropertyWrapper(@NotNull Supplier<SE> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, SoundEventPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public SoundEventPropertyWrapper(@NotNull Supplier<SE> parentObject, String modId) {
        super(parentObject, SoundEventPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
    }

    public SoundEventPropertyWrapper() {
        super(SoundEventPropertyWrapperBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
    }

    /**
     * Retrieves a copy of all associated {@linkplain SoundPropertyHolder SoundPropertyHolders} associated with the
     * parent {@link SoundEvent}.
     *
     * @return A copy of all associated {@linkplain SoundPropertyHolder SoundPropertyHolders}.
     *
     * @see SoundEventPropertyWrapperBuilder#withSoundDefinition(SoundPropertyHolder)
     * @see SoundEventPropertyWrapperBuilder#withSoundDefinitions(List)
     * @see SoundEventPropertyWrapperBuilder#setSoundDefinitions(List)
     */
    public ObjectArrayList<SoundPropertyHolder> getSoundPropertyHolders() {
        return rawBuilder().map(builder -> new ObjectArrayList<>(builder.mappedSoundPropertyHolders)).orElse(ObjectArrayList.of());
    }

    /**
     * Gets the language key for the subtitle of the parent {@link SoundEvent}. Used only for metadata in the resultant
     * {@code sounds.json} entry for the parent {@link SoundEvent}. Localization is handled through the composite
     * {@link SpecializedLanguagePropertyWrapper} associated with this wrapper.
     *
     * @return The language key for the subtitle of the parent {@link SoundEvent}. May be empty.
     *
     * @see SoundEventPropertyWrapperBuilder#withSubtitleKey(String)
     * @see SoundEventPropertyWrapperBuilder#withSubtitleKey(String, String)
     */
    public Optional<String> getSubtitleLangKey() {
        return rawBuilder().map(builder -> builder.subtitleLangKey);
    }

    /**
     * Gets whether the parent {@link SoundEvent} should replace the original sound event of the same ID, if present.
     *
     * @return Whether the parent {@link SoundEvent} should replace the original sound event of the same ID.
     *
     * @see SoundEventPropertyWrapperBuilder#replaceOriginal(boolean)
     * @see SoundEventPropertyWrapperBuilder#replaceOriginal()
     */
    public boolean replacesOriginalSoundEvent() {
        return rawBuilder().map(builder -> builder.replaceOriginal).orElse(false);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<SE, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }
}