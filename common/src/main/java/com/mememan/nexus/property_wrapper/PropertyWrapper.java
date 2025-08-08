package com.mememan.nexus.property_wrapper;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Base implementation for Property Wrappers.
 * <br></br>
 * Fundamentally, Property Wrappers are objects with builders that allow you to configure and attach different properties
 * to an in-game object, such as {@linkplain Block Blocks} or {@linkplain Item Items}. Extensions of this {@code interface}
 * provide their own specific methods to allow for Nexus API to interact with or generate data for them.
 * <br></br>
 * This is only the base {@code interface}, so it isn't going to do much on its own.
 *
 * @param <T> The object type being wrapped.
 * @param <SELF> Generic type for this PW {@code interface}. You would usually pass the implementing {@code class} or
 *               extending {@code interface} here.
 *
 * @see PropertyWrapperBuilder
 * @see <a href="https://github.com/RaveTr/Nexus/wiki/Property-Wrappers">Nexus Wiki: Property Wrappers</a>
 */
public interface PropertyWrapper<T, SELF extends PropertyWrapper<T, SELF>> {

    /**
     * Gets the parent {@code Supplier<T>} of this PW instance.
     *
     * @return The parent {@code Supplier<T>} stored in this PW instance.
     *
     * @implNote Note that while the {@linkplain Supplier} itself should never be {@code null}, the object it's wrapping
     * may be a default object delegate (similar to how referencing blocks too early results in an air delegate) or
     * {@code null}, depending on the object being wrapped and implemented.
     * <br></br>
     * If this PW instance happens to be a template, this method should always return {@code Suppliers#instanceOf(null)}.
     */
    @NotNull
    Supplier<T> getParentObject();

    /**
     * Creates a new builder for this PW instance if the one currently stored is {@code null} or if
     * {@code overrideExistingBuilder} is {@code true}. Otherwise, returns the current builder as-is with its
     * stored/set properties.
     *
     * @param overrideExistingBuilder Whether to create a new builder even if one is already stored.
     *
     * @return A new builder for this PW instance, or the current builder if it is not {@code null} and
     * {@code overrideExistingBuilder} is {@code false}.
     */
    PropertyWrapperBuilder<T, SELF> builder(boolean overrideExistingBuilder);

    /**
     * Overloaded variant of {@link #builder(boolean)} with {@code overrideExistingBuilder} set to {@code false}.
     *
     * @return A new builder for this PW instance, or the current builder if it is not {@code null}.
     */
    default PropertyWrapperBuilder<T, SELF> builder() {
        return builder(false);
    }

    /**
     * Whether this {@link PropertyWrapper} instance is a template.
     * <br></br>
     * Templates are not stored or tracked in any PW collection, and have no parent object.
     *
     * @return Whether this {@link PropertyWrapper} instance is a template.
     */
    boolean isTemplate();
}
