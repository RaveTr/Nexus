package com.mememan.nexus.property_wrapper.def.sound_event;

import com.mememan.nexus.client.sound.SoundPropertyHolder;
import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

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

    public ObjectArrayList<SoundPropertyHolder> getSoundPropertyHolders() {
        return rawBuilder().map(builder -> new ObjectArrayList<>(builder.mappedSoundPropertyHolders)).orElse(ObjectArrayList.of());
    }

    public Optional<String> getSubtitleLangKey() {
        return rawBuilder().map(builder -> builder.subtitleLangKey);
    }

    public boolean replacesOriginalSoundEvent() {
        return rawBuilder().map(builder -> builder.replaceOriginal).orElse(false);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<SE, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }
}