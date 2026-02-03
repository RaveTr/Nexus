package com.mememan.nexus.property_wrapper.impl.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BasePropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized implementation of {@link VanillaBasedPropertyWrapperBuilder}. Implements all vanilla-related builder methods,
 * generic types, and default behaviour for vanilla-based property wrapper handling for {@linkplain ItemLike ItemLikes}.
 *
 * @see SpecializedVanillaPropertyWrapper
 */
public class SpecializedVanillaPropertyWrapperBuilder<IL extends ItemLike, SELF extends VanillaBasedPropertyWrapperBuilder<IL, SELF, VBPW>, VBPW extends VanillaBasedPropertyWrapper<IL, VBPW, SELF>> extends BasePropertyWrapperBuilder<IL, SELF, VBPW> implements VanillaBasedPropertyWrapperBuilder<IL, SELF, VBPW> {
    protected Function<Supplier<IL>, Float> compostMapperFunc;
    protected Function<Supplier<IL>, Integer> fuelMapperFunc;
    protected Function<Supplier<IL>, DispenseItemBehavior> dispenseBehaviourMapperFunc;
    protected final List<Supplier<CreativeModeTab>> parentTabs = new ObjectArrayList<>();

    public SpecializedVanillaPropertyWrapperBuilder(@NotNull VBPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(VBPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .asCompostable(propertyWrapper.getCompostMapper().orElse(null))
                .asFuel(propertyWrapper.getFuelMapper().orElse(null))
                .asDispensable(propertyWrapper.getDispenseBehaviourMapper().orElse(null))
                .setParentTabs(propertyWrapper.getParentCreativeModeTabs());
    }

    @Override
    public SELF asCompostable(Function<Supplier<IL>, Float> compostMapper) {
        this.compostMapperFunc = compostMapper;
        return self();
    }

    @Override
    public SELF asFuel(Function<Supplier<IL>, Integer> fuelTimeMapper) {
        this.fuelMapperFunc = fuelTimeMapper;
        return self();
    }

    @Override
    public SELF asDispensable(Function<Supplier<IL>, DispenseItemBehavior> dispenseBehaviourMapper) {
        this.dispenseBehaviourMapperFunc = dispenseBehaviourMapper;
        return self();
    }

    @Override
    public SELF withParentTab(Supplier<CreativeModeTab> parentTab) {
        this.parentTabs.add(parentTab);
        return self();
    }

    @Override
    public SELF withParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs) {
        this.parentTabs.addAll(parentTabs);
        return self();
    }

    @Override
    public SELF setParentTabs(Collection<Supplier<CreativeModeTab>> parentTabs) {
        this.parentTabs.clear();
        this.parentTabs.addAll(parentTabs);
        return self();
    }
}
