package com.mememan.nexus.property_wrapper.def.creative_mode_tab;

import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class CreativeModeTabPropertyWrapper<CMT extends CreativeModeTab> extends BaseDataGenPropertyWrapper<CMT, CreativeModeTabPropertyWrapper<CMT>, CreativeModeTabPropertyWrapperBuilder<CMT>> implements DefaultableLanguageBasedPropertyWrapper<CMT, CreativeModeTabPropertyWrapper<CMT>, CreativeModeTabPropertyWrapperBuilder<CMT>> {
    protected final SpecializedLanguagePropertyWrapper<CMT, ?, ?> compositeLanguageWrapper;

    public CreativeModeTabPropertyWrapper(Supplier<CMT> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, CreativeModeTabPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public CreativeModeTabPropertyWrapper(@NotNull Supplier<CMT> parentObject, String modId) {
        super(parentObject, CreativeModeTabPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
    }

    public CreativeModeTabPropertyWrapper() {
        super(CreativeModeTabPropertyWrapperBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<CMT, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }
}
