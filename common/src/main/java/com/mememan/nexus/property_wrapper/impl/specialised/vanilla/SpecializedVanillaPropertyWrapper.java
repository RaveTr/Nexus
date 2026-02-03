package com.mememan.nexus.property_wrapper.impl.specialised.vanilla;

import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BasePropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized implementation of {@link VanillaBasedPropertyWrapper}. Implements all vanilla-related getter methods,
 * generic types, and default behaviour for vanilla-based property wrapper handling for {@linkplain ItemLike ItemLikes}.
 *
 * @see SpecializedVanillaPropertyWrapperBuilder
 */
public class SpecializedVanillaPropertyWrapper<IL extends ItemLike, SELF extends VanillaBasedPropertyWrapper<IL, SELF, BUILDER>, BUILDER extends SpecializedVanillaPropertyWrapperBuilder<IL, BUILDER, SELF>> extends BasePropertyWrapper<IL, SELF, BUILDER> implements VanillaBasedPropertyWrapper<IL, SELF, BUILDER> {

    public SpecializedVanillaPropertyWrapper(Supplier<IL> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, SpecializedVanillaPropertyWrapperBuilder::new, modId);
    }

    public SpecializedVanillaPropertyWrapper(@NotNull Supplier<IL> parentObject, @NotNull String modId) {
        super(parentObject, SpecializedVanillaPropertyWrapperBuilder::new, modId);
    }

    public SpecializedVanillaPropertyWrapper() {
        super(SpecializedVanillaPropertyWrapperBuilder::new);
    }

    @Override
    public Optional<Function<Supplier<IL>, Float>> getCompostMapper() {
        return rawBuilder().map(b -> b.compostMapperFunc);
    }

    @Override
    public Optional<Function<Supplier<IL>, Integer>> getFuelMapper() {
        return rawBuilder().map(b -> b.fuelMapperFunc);
    }

    @Override
    public Optional<Function<Supplier<IL>, DispenseItemBehavior>> getDispenseBehaviourMapper() {
        return rawBuilder().map(b -> b.dispenseBehaviourMapperFunc);
    }

    @Override
    public List<Supplier<CreativeModeTab>> getParentCreativeModeTabs() {
        return rawBuilder().map(builder -> new ObjectArrayList<>(builder.parentTabs)).orElse(ObjectArrayList.of());
    }
}
