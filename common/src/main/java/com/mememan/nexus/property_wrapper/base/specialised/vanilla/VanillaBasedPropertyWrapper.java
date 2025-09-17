package com.mememan.nexus.property_wrapper.base.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface VanillaBasedPropertyWrapper<IL extends ItemLike, SELF extends PropertyWrapper<IL, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<IL, BUILDER, SELF>> extends PropertyWrapper<IL, SELF, BUILDER> {

    /**
     *
     *
     * @return
     */
    Optional<Function<Supplier<IL>, Float>> getCompostMapper();

    /**
     *
     *
     * @return
     */
    Optional<Function<Supplier<IL>, Integer>> getFuelMapper();

    /**
     *
     *
     * @return
     */
    List<Supplier<CreativeModeTab>> getParentCreativeModeTabs();
}
