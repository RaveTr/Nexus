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

/**
 * Definite wrapper implementation for {@link Item} objects, with constrained generic types for {@code SELF} and
 * {@code BUILDER}. Exposes additional properties for vanilla-specific properties, such as item colors and model
 * predicates.
 * @param <I> Any {@link Item} type.
 *
 * @see ItemPropertyWrapperBuilder
 */
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

    /**
     * Retrieves the mapping function for the parent {@link Item}'s dynamic color.
     *
     * @return The mapping function for the parent {@link Item}'s dynamic color. May be empty.
     *
     * @see ItemPropertyWrapperBuilder#withItemColor(Function)
     * @see WrappedItemColor
     */
    public Optional<Function<Supplier<I>, WrappedItemColor>> getItemColorMapper() {
        return rawBuilder().map(builder -> builder.itemColorMappingFunc);
    }

    /**
     * Retrieves all model predicates for the parent {@link Item}.
     *
     * @return All model predicates for the parent {@link Item}.
     *
     * @see ItemPropertyWrapperBuilder#withItemModelPredicate(ResourceLocation, WrappedClampedItemPropertyFunction)
     * @see ItemPropertyWrapperBuilder#withItemModelPredicates(Map)
     * @see ItemPropertyWrapperBuilder#setItemModelPredicates(Map)
     */
    public Map<ResourceLocation, WrappedClampedItemPropertyFunction> getItemModelPredicates() {
        return rawBuilder().map(builder -> builder.itemModelPredicates).orElse(Map.of());
    }

    @Override
    public Optional<SpecializedVanillaPropertyWrapper<I, ?, ?>> getSpecializedVanillaWrapper() {
        return Optional.of(compositeVanillaWrapper);
    }
}
