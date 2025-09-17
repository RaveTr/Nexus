package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards handling generic Vanilla hooks that can be
 * generalised for {@linkplain ItemLike ItemLikes}.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link VanillaBasedPropertyWrapperBuilder}.
 *
 * @param <IL> Any {@link ItemLike} type.
 *
 * @see VanillaBasedPropertyWrapperBuilder
 */
public interface VanillaBasedPropertyWrapper<IL extends ItemLike, SELF extends PropertyWrapper<IL, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<IL, BUILDER, SELF>> extends PropertyWrapper<IL, SELF, BUILDER> {

    /**
     * Gets the compost value mapping {@link Function}, if present, representing the parent
     * {@linkplain #getParentObject() ItemLike's} composting chance.
     *
     * @return The compost value mapping {@link Function}. May be empty.
     */
    Optional<Function<Supplier<IL>, Float>> getCompostMapper();

    /**
     * Gets the fuel value mapping {@link Function}, if present, representing the parent
     * {@linkplain #getParentObject() ItemLike's} burn time in ticks when used in a furnace or similar.
     *
     * @return The fuel value mapping {@link Function}. May be empty.
     */
    Optional<Function<Supplier<IL>, Integer>> getFuelMapper();

    /**
     * Gets a {@link List} of tabs the parent {@linkplain #getParentObject() ItemLike} should be listed/show up in.
     *
     * @return The {@link List} of tabs the parent {@linkplain #getParentObject() ItemLike} should be listed/show up in.
     * May be empty.
     */
    List<Supplier<CreativeModeTab>> getParentCreativeModeTabs();
}
