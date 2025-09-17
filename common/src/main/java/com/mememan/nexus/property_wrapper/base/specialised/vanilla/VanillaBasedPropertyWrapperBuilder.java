package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
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
     *
     *
     * @param compostMapper
     *
     * @return
     */
    SELF withCompostMapper(Function<Supplier<IL>, Float> compostMapper);

    /**
     *
     *
     * @param fuelTimeMapper
     *
     * @return
     */
    SELF withFuelMapper(Function<Supplier<IL>, Integer> fuelTimeMapper);

    /**
     *
     * @param parentTab
     *
     * @return
     */
    SELF withParentTab(Supplier<CreativeModeTab> parentTab);

    /**
     *
     * @param parentTabs
     *
     * @return
     */
    SELF withParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs);

    /**
     *
     * @param parentTabs
     *
     * @return
     */
    default SELF withParentTabs(Supplier<CreativeModeTab>... parentTabs) {
        return withParentTabs(ObjectArrayList.of(parentTabs));
    }

    /**
     *
     *
     * @param parentTabs
     *
     * @return
     */
    SELF setParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs);
}
