package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link VanillaBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link VanillaBasedPropertyWrapperBuilder}) using {@link #getSpecializedVanillaWrapper()}.
 *
 * @see DefaultableVanillaBasedPropertyWrapperBuilder
 */
public interface DefaultableVanillaBasedPropertyWrapper<IL extends ItemLike, SELF extends VanillaBasedPropertyWrapper<IL, SELF, BUILDER>, BUILDER extends VanillaBasedPropertyWrapperBuilder<IL, BUILDER, SELF>> extends VanillaBasedPropertyWrapper<IL, SELF, BUILDER> {

    /**
     * The specialized wrapper to which all getters should delegate.
     *
     * @return The specialized wrapper to which all getters should delegate. May be empty.
     */
    Optional<SpecializedVanillaPropertyWrapper<IL, ?, ?>> getSpecializedVanillaWrapper();

    @Override
    default Optional<Function<Supplier<IL>, Float>> getCompostMapper() {
        return getSpecializedVanillaWrapper().flatMap(SpecializedVanillaPropertyWrapper::getCompostMapper);
    }

    @Override
    default Optional<Function<Supplier<IL>, Integer>> getFuelMapper() {
        return getSpecializedVanillaWrapper().flatMap(SpecializedVanillaPropertyWrapper::getFuelMapper);
    }

    @Override
    default List<Supplier<CreativeModeTab>> getParentCreativeModeTabs() {
        return getSpecializedVanillaWrapper().map(SpecializedVanillaPropertyWrapper::getParentCreativeModeTabs).orElse(ObjectArrayList.of());
    }
}
