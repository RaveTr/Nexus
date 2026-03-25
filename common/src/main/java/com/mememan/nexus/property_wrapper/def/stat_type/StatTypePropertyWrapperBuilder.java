package com.mememan.nexus.property_wrapper.def.stat_type;

import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.NotNull;

/**
 * Definite builder implementation for {@link StatType} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}. Exists to go hand-in-hand with {@link StatTypePropertyWrapper}.
 *
 * @param <T> Any {@link StatType} type.
 *
 * @see StatTypePropertyWrapper
 */
public class StatTypePropertyWrapperBuilder<T, ST extends StatType<T>> extends SpecializedLanguagePropertyWrapperBuilder<ST, StatTypePropertyWrapperBuilder<T, ST>, StatTypePropertyWrapper<T, ST>> {

    public StatTypePropertyWrapperBuilder(@NotNull StatTypePropertyWrapper<T, ST> ownerWrapper) {
        super(ownerWrapper);
    }
}
