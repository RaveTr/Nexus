package com.mememan.nexus.property_wrapper.def.stat_type;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Definite wrapper implementation for {@link StatType} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}. Primarily exists to distinguish between custom state types and normal state types in localization.
 *
 * @param <T> Any {@link StatType} type.
 *
 * @see StatTypePropertyWrapperBuilder
 */
public class StatTypePropertyWrapper<T, ST extends StatType<T>> extends SpecializedLanguagePropertyWrapper<ST, StatTypePropertyWrapper<T, ST>, StatTypePropertyWrapperBuilder<T, ST>> {

    public StatTypePropertyWrapper(Supplier<ST> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, modId);
    }

    public StatTypePropertyWrapper(@NotNull Supplier<ST> parentObject, @NotNull String modId) {
        super(parentObject, modId);
    }

    public StatTypePropertyWrapper() {
        super();
    }

    @Override
    public @NotNull PropertyWrapperBuilder<ST, StatTypePropertyWrapperBuilder<T, ST>, StatTypePropertyWrapper<T, ST>> constructBuilder() {
        return new StatTypePropertyWrapperBuilder<>(this);
    }

    @Override
    public Optional<String> getDescriptionIdPrefix() {
        return Optional.ofNullable(getParentObject().get())
                .map(statTypeObj -> Objects.equals(statTypeObj.getRegistry().key(), Registries.CUSTOM_STAT) ? "stat" : "stat_type");
    }
}
