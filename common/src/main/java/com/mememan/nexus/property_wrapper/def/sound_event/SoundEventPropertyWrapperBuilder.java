package com.mememan.nexus.property_wrapper.def.sound_event;

import com.mememan.nexus.client.sound.SoundPropertyHolder;
import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SoundEventPropertyWrapperBuilder<SE extends SoundEvent> extends BaseDataGenPropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>> implements DefaultableLanguageBasedPropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>> {
    protected final SpecializedLanguagePropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>> compositeLanguageBuilder;
    protected final ObjectArrayList<SoundPropertyHolder> mappedSoundPropertyHolders = new ObjectArrayList<>();
    protected String subtitleLangKey;
    protected boolean replaceOriginal = false;

    public SoundEventPropertyWrapperBuilder(@NotNull SoundEventPropertyWrapper<SE> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get()
                .bypassDefaultTranslation(); // By default, for convenience (cuz SoundEvents don't have lang keys on their own, duh)
    }

    @Override
    public SoundEventPropertyWrapperBuilder<SE> copyFrom(SoundEventPropertyWrapper<SE> propertyWrapper) {
        DefaultableLanguageBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper)
                .setSoundDefinitions(propertyWrapper.getSoundPropertyHolders())
                .withSubtitleKey(propertyWrapper.getSubtitleLangKey().orElse(null))
                .replaceOriginal(propertyWrapper.replacesOriginalSoundEvent());
    }

    public SoundEventPropertyWrapperBuilder<SE> withSoundDefinition(SoundPropertyHolder soundPropertyHolder) {
        this.mappedSoundPropertyHolders.add(soundPropertyHolder);
        return self();
    }

    public SoundEventPropertyWrapperBuilder<SE> withSoundDefinitions(List<SoundPropertyHolder> soundPropertyHolders) {
        this.mappedSoundPropertyHolders.addAll(soundPropertyHolders);
        return self();
    }

    public SoundEventPropertyWrapperBuilder<SE> setSoundDefinitions(List<SoundPropertyHolder> soundPropertyHolders) {
        this.mappedSoundPropertyHolders.clear();
        this.mappedSoundPropertyHolders.addAll(soundPropertyHolders);
        return self();
    }

    public SoundEventPropertyWrapperBuilder<SE> withSubtitleKey(String subtitleLangKey) {
        this.subtitleLangKey = subtitleLangKey;
        return self();
    }

    public SoundEventPropertyWrapperBuilder<SE> withSubtitleKey(String subtitleLangKey, String localizedSubtitle) {
        return withSubtitleKey(subtitleLangKey)
                .withAdditionalLocalizationKey(subtitleLangKey, localizedSubtitle);
    }

    public SoundEventPropertyWrapperBuilder<SE> replaceOriginal(boolean replaceOriginal) {
        this.replaceOriginal = replaceOriginal;
        return self();
    }

    public SoundEventPropertyWrapperBuilder<SE> replaceOriginal() {
        return replaceOriginal(true);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<SE, SoundEventPropertyWrapperBuilder<SE>, SoundEventPropertyWrapper<SE>>> getSpecializedLanguageBuilder() {
        return Optional.of(compositeLanguageBuilder);
    }
}
