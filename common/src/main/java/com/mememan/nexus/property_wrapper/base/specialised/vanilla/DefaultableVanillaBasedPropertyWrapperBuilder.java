package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapperBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link VanillaBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedVanillaBuilder()}.
 *
 * @see VanillaBasedPropertyWrapper
 */
public interface DefaultableVanillaBasedPropertyWrapperBuilder<IL extends ItemLike, SELF extends VanillaBasedPropertyWrapperBuilder<IL, SELF, VBPW>, VBPW extends VanillaBasedPropertyWrapper<IL, VBPW, SELF>> extends VanillaBasedPropertyWrapperBuilder<IL, SELF, VBPW> {

    Optional<SpecializedVanillaPropertyWrapperBuilder<IL, SELF, VBPW>> getSpecializedVanillaBuilder();

    @Override
    default SELF asCompostable(Function<Supplier<IL>, Float> compostMapper) {
        getSpecializedVanillaBuilder().ifPresent(builder -> builder.asCompostable(compostMapper));
        return self();
    }

    @Override
    default SELF asFuel(Function<Supplier<IL>, Integer> fuelMapper) {
        getSpecializedVanillaBuilder().ifPresent(builder -> builder.asFuel(fuelMapper));
        return self();
    }

    @Override
    default SELF withParentTab(Supplier<CreativeModeTab> parentTab) {
        getSpecializedVanillaBuilder().ifPresent(builder -> builder.withParentTab(parentTab));
        return self();
    }

    @Override
    default SELF withParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs) {
        getSpecializedVanillaBuilder().ifPresent(builder -> builder.withParentTabs(parentTabs));
        return self();
    }

    @Override
    default SELF setParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs) {
        getSpecializedVanillaBuilder().ifPresent(builder -> builder.setParentTabs(parentTabs));
        return self();
    }

    @Override
    default SELF copyFrom(VBPW propertyWrapper) {
        getSpecializedVanillaBuilder().ifPresent(builder -> builder.copyFrom(propertyWrapper));
        return self();
    }
}
