package com.mememan.nexus.property_wrapper.def.creative_mode_tab;

import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Definite builder implementation for {@link CreativeModeTab} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}.
 *
 * @param <CMT> Any {@link CreativeModeTab} type.
 *
 * @see CreativeModeTabPropertyWrapper
 */
public class CreativeModeTabPropertyWrapperBuilder<CMT extends CreativeModeTab> extends BaseDataGenPropertyWrapperBuilder<CMT, CreativeModeTabPropertyWrapperBuilder<CMT>, CreativeModeTabPropertyWrapper<CMT>> implements DefaultableLanguageBasedPropertyWrapperBuilder<CMT, CreativeModeTabPropertyWrapperBuilder<CMT>, CreativeModeTabPropertyWrapper<CMT>> {
    protected final SpecializedLanguagePropertyWrapperBuilder<CMT, CreativeModeTabPropertyWrapperBuilder<CMT>, CreativeModeTabPropertyWrapper<CMT>> compositeLanguageBuilder;

    public CreativeModeTabPropertyWrapperBuilder(@NotNull CreativeModeTabPropertyWrapper<CMT> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<CMT, CreativeModeTabPropertyWrapperBuilder<CMT>, CreativeModeTabPropertyWrapper<CMT>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get()
                .bypassDefaultTranslation(); // Enabled by default for convenience
    }

    @Override
    public CreativeModeTabPropertyWrapperBuilder<CMT> copyFrom(Supplier<CMT> associatedPWObject) {
        DefaultableLanguageBasedPropertyWrapperBuilder.super.copyFrom(associatedPWObject);
        return super.copyFrom(associatedPWObject);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<CMT, CreativeModeTabPropertyWrapperBuilder<CMT>, CreativeModeTabPropertyWrapper<CMT>>> getSpecializedLanguageBuilder() {
        return Optional.of(compositeLanguageBuilder);
    }
}
