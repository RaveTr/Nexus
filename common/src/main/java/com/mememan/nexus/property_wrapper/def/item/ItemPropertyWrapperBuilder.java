package com.mememan.nexus.property_wrapper.def.item;

import com.mememan.nexus.client.item.WrappedClampedItemPropertyFunction;
import com.mememan.nexus.client.item.WrappedItemColor;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.DefaultableVanillaBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.misc.BaseDefaultableDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.vanilla.SpecializedVanillaPropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemPropertyWrapperBuilder<I extends Item> extends BaseDefaultableDataGenPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>> implements DefaultableVanillaBasedPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>> {
    protected final SpecializedVanillaPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>> compositeVanillaBuilder;
    protected Optional<Function<Supplier<I>, WrappedItemColor>> itemColorMappingFunc = Optional.empty();
    protected final Map<ResourceLocation, WrappedClampedItemPropertyFunction> itemModelPredicates = new Object2ObjectOpenHashMap<>();

    public ItemPropertyWrapperBuilder(@NotNull ItemPropertyWrapper<I> ownerWrapper) {
        super(ownerWrapper);

        this.compositeVanillaBuilder = (SpecializedVanillaPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>>) ownerWrapper.getSpecializedVanillaWrapper().map(SpecializedVanillaPropertyWrapper::builder).get();
    }

    @Override
    public ItemPropertyWrapperBuilder<I> copyFrom(ItemPropertyWrapper<I> propertyWrapper) {
        DefaultableVanillaBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper)
                .withItemColor(propertyWrapper.getItemColorMapper().orElse(null))
                .setItemModelPredicates(propertyWrapper.getItemModelPredicates());
    }

    public ItemPropertyWrapperBuilder<I> withItemColor(Function<Supplier<I>, WrappedItemColor> itemColorMappingFunc) {
        this.itemColorMappingFunc = Optional.ofNullable(itemColorMappingFunc);
        return this;
    }

    public ItemPropertyWrapperBuilder<I> withItemModelPredicate(ResourceLocation predicateId, WrappedClampedItemPropertyFunction predicate) {
        this.itemModelPredicates.put(predicateId, predicate);
        return this;
    }

    public ItemPropertyWrapperBuilder<I> withItemModelPredicates(Map<ResourceLocation, WrappedClampedItemPropertyFunction> predicates) {
        this.itemModelPredicates.putAll(predicates);
        return this;
    }

    public ItemPropertyWrapperBuilder<I> setItemModelPredicates(Map<ResourceLocation, WrappedClampedItemPropertyFunction> predicates) {
        this.itemModelPredicates.clear();
        this.itemModelPredicates.putAll(predicates);
        return this;
    }

    @Override
    public Optional<SpecializedVanillaPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>>> getSpecializedVanillaBuilder() {
        return Optional.of(compositeVanillaBuilder);
    }
}
