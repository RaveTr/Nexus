package com.mememan.nexus.property_wrapper.base.generic;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base interface for Property Wrapper Builders.
 * <br></br>
 * PWBs are the objects used to configure and mutate different properties, then store them in their owning
 * {@link PropertyWrapper} instance. Extensions of this {@code interface} provide their own specific methods to allow for
 * different properties to be configured and stored.
 * <br></br>
 * This is only the base {@code interface}, so it isn't going to do much on its own. It only provides the core
 * {@code build()} method (with overloads) and standard generic type constraints.
 *
 * @param <T> The object type being wrapped.
 * @param <SELF> Type reference generic for this PWB {@code interface}. Useful for implementations that implicitly require
 *               different generic types for their property wrappers.
 * @param <PW> The {@link PropertyWrapper} type being built, and whose generic type is {@code T}.
 *
 * @see PropertyWrapper
 */
public interface PropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, PW>, PW extends PropertyWrapper<T, PW, SELF>> extends Cloneable {

    /**
     * Copies data from the provided {@link PropertyWrapper} instance into this builder. Overrides all existing data
     * for this builder.
     * <br></br>
     * If you're attempting to inherit from a template whose parent object type is a supertype of this instance's parent
     * object (all as specified by {@link T}), you should use {@link #copyFromType(PropertyWrapper)} instead.
     *
     * @param propertyWrapper The {@link PropertyWrapper} instance to copy data from.
     *
     * @return {@link #self()} (builder method)
     *
     * @implSpec Implementations should ensure that properties are deep-copied rather than shallow-copied from the
     * provided {@code propertyWrapper} (e.g. instead of {@code #setSomeList(propertyWrapper.getSomeList())}, use
     * {@code #setSomeList(List.copyOf(propertyWrapper.getSomeList()))}).
     *
     * @see #copyFromType(PropertyWrapper)
     */
    SELF copyFrom(PW propertyWrapper);

    /**
     * Overloaded variant of {@link #copyFrom(PropertyWrapper)} that attempts to perform an unsafe cast on the provided
     * {@code propertyWrapper}.
     * <br></br>
     * Primarily useful in cases where copying from a template results in a compile-time error due to generic type
     * invariance for the 2 recursive generic types {@link SELF} and {@link PW}.
     *
     * @param propertyWrapper The wrapper whose properties should be deep-copied according to the spec of
     *                        {@link #copyFrom(PropertyWrapper)}.
     *
     * @return {@link #copyFrom(PropertyWrapper)} (builder method).
     */
    default SELF copyFromType(PropertyWrapper<? super T, ?, ?> propertyWrapper) {
        return copyFrom((PW) propertyWrapper);
    }

    /**
     * Overloaded variant of {@link #copyFrom(PropertyWrapper)} that copies data from the {@link PropertyWrapper}
     * mapped to the provided {@link Supplier} (retrieved through {@link PropertyWrapper.PropertyWrappersContainer#getWrapperFor(Supplier)}).
     *
     * @param associatedPWObject The parent object mapped to another PW.
     *
     * @return {@link #copyFrom(PropertyWrapper)}.
     */
    default SELF copyFrom(Supplier<T> associatedPWObject) {
        return copyFrom((PW) PropertyWrapper.PropertyWrappersContainer.getWrapperFor(associatedPWObject).orElse(null));
    }

    /**
     * Composite helper method that allows for end-developers to use the parent object with the current builder to call
     * additional builder methods that may not already take the parent object as the input.
     * <br></br>
     * By default, this works by deferring all calls made until {@link #build()} is called, at which point said calls are
     * applied in insertion order. This means that template PWs can make use of this for inheritors, since said calls
     * are not applied to templates themselves (only stored).
     *
     * @param contextualizedBuilder The contextualized builder function to apply to the current builder. Should conventionally
     *                              return the current builder being modified.
     *
     * @return {@link #self()} (builder method).
     *
     * @implNote Some implementations may already have methods that accept the parent object as input and even set data
     * that's already in the form of a {@link Function} that's later computed elsewhere.
     * <br></br>
     * Implementors are responsible for ensuring that calls made to builder methods in general do not get queried too
     * early (usually via getters in the owner {@link PropertyWrapper} instance) if the parent object is a registrable
     * object whose data is not yet fully initialized, thus causing errors.
     * <br></br>
     * The {@code default} implementation of this method is <b>NOT</b> thread-safe, which is fine for 99% of cases (since
     * property wrappers aren't inherently designed to work in multithreaded environments, like much of MC code that
     * deals with configuration).
     *
     * @implSpec Implementors are responsible for ensuring that calls made to this method actually have an effect through
     * {@link #build()} and {@link #buildAndGet()}, should they override default behaviour accordingly.
     */
    SELF compose(BiFunction<Supplier<T>, SELF, SELF> contextualizedBuilder);

    /**
     * Returns the owner {@link PropertyWrapper} instance of this builder.
     * <br></br>
     * This method also typically performs extra operations, such as mapping the owner PW to the parent object being
     * wrapped if it is not a template.
     *
     * @return The owner {@link PropertyWrapper} instance of this builder.
     *
     * @see PropertyWrapper#isTemplate()
     */
    PW build();

    /**
     * Gets the current owner {@link PropertyWrapper} instance of this builder. Does not perform any extra operations.
     *
     * @return The current owner {@link PropertyWrapper} instance of this builder.
     */
    @NotNull
    PW getCurrentOwnerWrapper();

    /**
     * Computes and returns a copy of contextualized builder calls made to this PW instance. Primarily used
     * to control behaviour when deriving such calls from templates, where computation is intentionally delegated to
     * the property wrappers with parent objects that are not templates.
     *
     * @return A copy of contextualized builder calls made to this PW instance.
     */
    List<BiFunction<Supplier<T>, SELF, SELF>> getContextualizedBuilders();

    /**
     * Safely clones this builder via {@link Object#clone()}. Useful if you need to create a copy of this builder rather
     * than copying from another one, but it is a bit more performance taxing.
     *
     * @return A deep copy of this builder instance (deep copying mostly depends on {@link #copyFrom(PropertyWrapper)}).
     */
    SELF clone();

    /**
     * Shortcut overload for {@link PropertyWrapperBuilder#build()} that returns the parent object {@link Supplier}
     * wrapped by the owner {@link PropertyWrapper} instance of this builder.
     *
     * @return The parent object {@link Supplier} wrapped by the owner {@link PropertyWrapper} instance of this builder.
     *
     * @see PropertyWrapper#getParentObject()
     *
     * @implSpec Typically, if this PW is a template, this method should return {@code Suppliers#ofInstance(null)}.
     */
    @NotNull
    default Supplier<T> buildAndGet() {
        return build().getParentObject();
    }

    /**
     * Convenience method to return a safe type-casted reference to this builder.
     *
     * @return {@code (SELF) this} (builder method)
     */
    default SELF self() {
        return (SELF) this;
    }
}
