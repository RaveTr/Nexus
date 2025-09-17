package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

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
    SELF withParentTab(CreativeModeTab parentTab);

    /**
     *
     * @param parentTabs
     *
     * @return
     */
    SELF withParentTabs(Collection<CreativeModeTab> parentTabs);

    /**
     *
     * @param parentTabs
     *
     * @return
     */
    SELF withParentTabs(CreativeModeTab... parentTabs);

    /**
     *
     *
     * @param parentTabs
     * 
     * @return
     */
    SELF setParentTabs(Collection<CreativeModeTab> parentTabs);
}
