package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapper} with builder methods tailored towards handling generic Vanilla hooks that
 * can be generalised for {@linkplain ItemLike ItemLikes}.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link VanillaBasedPropertyWrapper}.
 *
 * @param <IL> Any {@link ItemLike} type.
 *
 * @see VanillaBasedPropertyWrapper
 */
public interface VanillaBasedPropertyWrapperBuilder<IL extends ItemLike, SELF extends PropertyWrapperBuilder<IL, SELF, VBPW>, VBPW extends PropertyWrapper<IL, VBPW, SELF>> extends PropertyWrapperBuilder<IL, SELF, VBPW> {

    /**
     * Defines a mapping {@link Function} that assigns a composting chance using the parent {@link ItemLike} as input.
     *
     * @param compostMapper The compost chance mapping {@link Function}.
     *
     * @return {@link #self()} (builder method).
     */
    SELF asCompostable(Function<Supplier<IL>, Float> compostMapper);

    /**
     * Defines a mapping {@link Function} that assigns a fuel value using the parent {@link ItemLike} as input, where
     * the fuel value represents the burn time (in furnaces and the likes) of the parent {@link ItemLike} in ticks.
     *
     * @param fuelTimeMapper The fuel time mapping {@link Function}. Represents burn time in ticks.
     *
     * @return {@link #self()} (builder method).
     */
    SELF asFuel(Function<Supplier<IL>, Integer> fuelTimeMapper);

    /**
     * Defines a parent {@link CreativeModeTab} in which the parent {@link ItemLike} should show up.
     *
     * @param parentTab The parent {@link CreativeModeTab}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withParentTabs(Collection)
     * @see #withParentTabs(Supplier[])
     * @see #setParentTabs(Collection)
     */
    SELF withParentTab(Supplier<CreativeModeTab> parentTab);

    /**
     * Defines a {@link Collection} of {@linkplain CreativeModeTab CreativeModeTabs} the parent {@link ItemLike} should
     * show up in.
     *
     * @param parentTabs The {@link Collection} of {@linkplain CreativeModeTab CreativeModeTabs} the parent {@link ItemLike}
     *                   should show up in.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withParentTab(Supplier)
     * @see #withParentTabs(Supplier[])
     * @see #setParentTabs(Collection)
     */
    SELF withParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs);

    /**
     * Overloaded variant of {@link #withParentTabs(Collection)} that allows for varargs to be passed in.
     *
     * @param parentTabs The {@linkplain CreativeModeTab CreativeModeTabs} the parent {@link ItemLike} should show up in.
     *
     * @return {@link #withParentTabs(Collection)} (builder method).
     *
     * @see #withParentTab(Supplier)
     * @see #setParentTabs(Collection)
     * @see #setParentTabs(Collection)
     */
    default SELF withParentTabs(Supplier<CreativeModeTab>... parentTabs) {
        return withParentTabs(ObjectArrayList.of(parentTabs));
    }

    /**
     * Sets the existing {@link List} of parent {@linkplain CreativeModeTab CreativeModeTabs} to the provided
     * {@code parentTabs}.
     *
     * @param parentTabs The {@link Collection} of {@linkplain CreativeModeTab CreativeModeTabs} to set the existing one
     *                   to.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withParentTab(Supplier)
     * @see #withParentTabs(Collection)
     * @see #withParentTabs(Supplier[])
     */
    SELF setParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs);
}
