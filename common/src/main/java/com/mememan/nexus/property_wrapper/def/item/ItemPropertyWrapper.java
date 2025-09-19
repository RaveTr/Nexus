package com.mememan.nexus.property_wrapper.def.item;

import com.mememan.nexus.client.item.WrappedClampedItemPropertyFunction;
import com.mememan.nexus.client.item.WrappedItemColor;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.DefaultableVanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemPropertyWrapper<I extends Item> extends BaseDefaultableDataGenPropertyWrapper<I, ItemPropertyWrapper<I>, ItemPropertyWrapperBuilder<I>> implements DefaultableVanillaBasedPropertyWrapper<I, ItemPropertyWrapper<I>, ItemPropertyWrapperBuilder<I>> {
    protected final SpecializedVanillaPropertyWrapper<I, ?, ?> compositeVanillaWrapper;

    public ItemPropertyWrapper(Supplier<I> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, ItemPropertyWrapperBuilder::new, modId);

        this.compositeVanillaWrapper = new SpecializedVanillaPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public ItemPropertyWrapper(@NotNull Supplier<I> parentObject, String modId) {
        super(parentObject, ItemPropertyWrapperBuilder::new, modId);

        this.compositeVanillaWrapper = new SpecializedVanillaPropertyWrapper<>(parentObject, modId);
    }

    public ItemPropertyWrapper() {
        super(ItemPropertyWrapperBuilder::new);

        this.compositeVanillaWrapper = new SpecializedVanillaPropertyWrapper<>();
    }

    public Optional<Function<Supplier<I>, WrappedItemColor>> getItemColorMapper() {
        return rawBuilder().flatMap(builder -> builder.itemColorMappingFunc);
    }

    public Map<ResourceLocation, WrappedClampedItemPropertyFunction> getItemModelPredicates() {
        return rawBuilder().map(builder -> builder.itemModelPredicates).orElse(Map.of());
    }

    @Override
    public Optional<SpecializedVanillaPropertyWrapper<I, ?, ?>> getSpecializedVanillaWrapper() {
        return Optional.of(compositeVanillaWrapper);
    }
}
