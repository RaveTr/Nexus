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

/**
 * Definite builder implementation for {@link Item} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}. Provides additional builders for miscellaneous vanilla properties, such as item colors and model
 * predicates.
 *
 * @param <I> Any {@link Item} type.
 *
 * @see ItemPropertyWrapper
 */
public class ItemPropertyWrapperBuilder<I extends Item> extends BaseDefaultableDataGenPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>> implements DefaultableVanillaBasedPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>> {
    protected final SpecializedVanillaPropertyWrapperBuilder<I, ItemPropertyWrapperBuilder<I>, ItemPropertyWrapper<I>> compositeVanillaBuilder;
    protected Function<Supplier<I>, WrappedItemColor> itemColorMappingFunc;
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

    /**
     * Defines a {@link Function} that outputs a {@link WrappedItemColor} representing the dynamic color of the parent
     * {@link Item}.
     *
     * @param itemColorMappingFunc The mapping function used to output the color of the parent {@link Item}, with the
     *                             parent {@link Item} as the input. May be {@code null}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see WrappedItemColor
     */
    public ItemPropertyWrapperBuilder<I> withItemColor(Function<Supplier<I>, WrappedItemColor> itemColorMappingFunc) {
        this.itemColorMappingFunc = itemColorMappingFunc;
        return this;
    }

    /**
     * Defines a {@link WrappedClampedItemPropertyFunction} representing a model predicate for the parent {@link Item}.
     * Appends to the existing model predicates.
     * <br></br>
     * Model predicates are used in item models to change the appearance of the item based on certain conditions, as
     * specified by the {@link WrappedClampedItemPropertyFunction}.
     *
     * @param predicateId The identifier for the model predicate.
     * @param predicate The {@link WrappedClampedItemPropertyFunction} representing the model predicate to validate/check
     *                  against.
     *
     * @return {@link #self()} (builder method).
     *
     * @see WrappedClampedItemPropertyFunction
     * @see #withItemModelPredicates(Map)
     * @see #setItemModelPredicates(Map)
     */
    public ItemPropertyWrapperBuilder<I> withItemModelPredicate(ResourceLocation predicateId, WrappedClampedItemPropertyFunction predicate) {
        this.itemModelPredicates.put(predicateId, predicate);
        return this;
    }

    /**
     * Defines multiple {@linkplain WrappedClampedItemPropertyFunction WrappedClampedItemPropertyFunctions}
     * representing model predicates for the parent {@link Item}. Appends to the existing model predicates.
     * <br></br>
     * Model predicates are used in item models to change the appearance of the item based on certain conditions, as
     * specified by the {@linkplain WrappedClampedItemPropertyFunction WrappedClampedItemPropertyFunctions}.
     *
     * @param predicates The {@link Map} of {@link ResourceLocation} keys to {@linkplain WrappedClampedItemPropertyFunction WrappedClampedItemPropertyFunctions}
     *                   representing the model predicates to validate/check against, where each {@link ResourceLocation}
     *                   represents the predicate's ID.
     *
     * @return {@link #self()} (builder method).
     *
     * @see WrappedClampedItemPropertyFunction
     * @see #withItemModelPredicate(ResourceLocation, WrappedClampedItemPropertyFunction)
     * @see #setItemModelPredicates(Map)
     */
    public ItemPropertyWrapperBuilder<I> withItemModelPredicates(Map<ResourceLocation, WrappedClampedItemPropertyFunction> predicates) {
        this.itemModelPredicates.putAll(predicates);
        return this;
    }

    /**
     * Sets the model predicates for the parent {@link Item}.
     * <br></br>
     * Model predicates are used in item models to change the appearance of the item based on certain conditions, as
     * specified by the {@linkplain WrappedClampedItemPropertyFunction WrappedClampedItemPropertyFunctions}.
     *
     * @param predicates The {@link Map} of model predicates to set for the parent {@link Item}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see WrappedClampedItemPropertyFunction
     * @see #withItemModelPredicate(ResourceLocation, WrappedClampedItemPropertyFunction)
     * @see #withItemModelPredicates(Map)
     */
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
