package com.mememan.nexus.property_wrapper.base.generic;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
 * @param <BUILDER> The {@link PropertyWrapperBuilder} type, primarily used for type inference within inheriting classes
 *                  and interfaces.
 *
 * @implNote Some implementations extending from base classes implementing this {@code interface} may have some
 * factory helpers and private constructors to enforce factory pattern when used.
 *
 * @see PropertyWrapperBuilder
 * @see <a href="https://github.com/RaveTr/Nexus/wiki/Property-Wrappers">Nexus Wiki: Property Wrappers</a>
 */
public interface PropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> {

    /**
     * Gets the parent {@code Supplier<PW>} of this PW instance.
     *
     * @return The parent {@code Supplier<PW>} stored in this PW instance.
     *
     * @implNote Note that while the {@linkplain Supplier} itself should never be {@code null}, the object it's wrapping
     * may be a default object delegate (similar to how referencing blocks too early results in an air delegate) or
     * {@code null}, depending on the object being wrapped and implemented.
     * <br></br>
     * If this PW instance happens to be a template, this method should always return {@code Suppliers#ofInstance(null)}.
     */
    @NotNull
    Supplier<T> getParentObject();

    /**
     * Gets the parent mod ID of this PW instance. Wrapped in an {@link Optional} to allow for nullability and defaulting
     * values (since not all PWs have to be associated with a particular mod).
     * <br></br>
     * Note that templates do not store their parent mod ID, and thus this method will always return an empty
     * {@link Optional} if {@link #isTemplate()} is {@code true}.
     *
     * @return The parent mod ID of this PW instance, wrapped in an {@link Optional}. May be empty.
     */
    Optional<String> getModId();

    /**
     * Creates a new builder for this PW instance if the one currently stored is {@code null} or if
     * {@code overrideExistingBuilder} is {@code true}. Otherwise, returns the current builder as-is with its
     * stored/set properties.
     *
     * @param overrideExistingBuilder Whether to create a new builder even if one is already stored.
     *
     * @return A new builder with default properties for this PW instance, or the current builder if it is not
     * {@code null} and {@code overrideExistingBuilder} is {@code false}.
     */
    BUILDER builder(boolean overrideExistingBuilder);

    /**
     * Overloaded variant of {@link #builder(boolean)} with {@code overrideExistingBuilder} set to {@code false}.
     *
     * @return A new builder for this PW instance, or the current builder if it is not {@code null}.
     */
    default BUILDER builder() {
        return builder(false);
    }

    /**
     * Used to create a new builder instance for this {@link PropertyWrapper} instance. Uses a factory {@link Function}
     * to create the builder. May be overridden to provide a custom builder instance otherwise.
     *
     * @return A new builder instance for this {@link PropertyWrapper} instance.
     *
     * @apiNote Typecasting {@code BUILDER} is handled appropriately in other builder methods. This method is of base
     * type {@link PropertyWrapperBuilder} to allow for convenient return statements without having to typecast at every
     * turn.
     */
    @NotNull
    PropertyWrapperBuilder<T, BUILDER, SELF> constructBuilder();

    /**
     * Method that wraps the stored {@code builder} instance in an {@link Optional} to allow for nullability and defaulting
     * values.
     *
     * @return The builder instance wrapped in an {@link Optional}. May be empty.
     */
    Optional<? extends PropertyWrapperBuilder<T, BUILDER, SELF>> rawBuilder();

    /**
     * Whether this {@link PropertyWrapper} instance is a template.
     * <br></br>
     * Templates are not stored or tracked in any PW collection, and have no parent object.
     *
     * @return Whether this {@link PropertyWrapper} instance is a template.
     */
    boolean isTemplate();

    /**
     * Gets a copied view (via {@link Object2ObjectOpenHashMap}) of {@link PropertyWrappersContainer#MAPPED_PROPERTY_WRAPPERS},
     * primarily to allow for O(1) lookups while preventing modifications from being made to the original map.
     *
     * @return An immutable copy of {@link PropertyWrappersContainer#MAPPED_PROPERTY_WRAPPERS}.
     */
    static Object2ObjectOpenHashMap<Supplier<?>, PropertyWrapper<?, ? extends PropertyWrapper<?, ?, ?>, ? extends PropertyWrapperBuilder<?, ?, ?>>> getMappedPropertyWrappers() {
        return new Object2ObjectOpenHashMap<>(PropertyWrappersContainer.MAPPED_PROPERTY_WRAPPERS);
    }

    /**
     * Container {@code class} for storing an access-protected {@link Object2ObjectOpenHashMap} of PWs mapped
     * to their parent objects. Additionally, provides utility methods for registering and unregistering PWs, as well
     * as performing efficient lookups based on type and/or mod ID.
     */
    class PropertyWrappersContainer {
        private static final Object2ObjectOpenHashMap<Supplier<?>, PropertyWrapper<?, ? extends PropertyWrapper<?, ?, ?>, ? extends PropertyWrapperBuilder<?, ?, ?>>> MAPPED_PROPERTY_WRAPPERS = new Object2ObjectOpenHashMap<>();
        private static final Object2ObjectOpenHashMap<Class<?>, Map<String, List<PropertyWrapper<?, ? extends PropertyWrapper<?, ?, ?>, ? extends PropertyWrapperBuilder<?, ?, ?>>>>> CACHED_WRAPPER_LOOKUP = new Object2ObjectOpenHashMap<>();

        private PropertyWrappersContainer() {
            throw new IllegalAccessError("Attempted to construct instance of container class! (PropertyWrappersContainer)");
        }

        /**
         * Registers a {@link PropertyWrapper} instance mapped to its parent object.
         * <br></br>
         * Non-template PWs are added to {@link #MAPPED_PROPERTY_WRAPPERS}.
         *
         * @param parentObject The parent object of the {@code propertyWrapper}.
         * @param propertyWrapper The {@link PropertyWrapper} instance to register and map.
         *
         * @return The registered {@code propertyWrapper}.
         *
         * @param <T> The parent object type.
         * @param <PW> The {@link PropertyWrapper} type.
         * @param <PWB> The {@link PropertyWrapperBuilder} type.
         *
         * @apiNote {@link #MAPPED_PROPERTY_WRAPPERS} is stored as a map to ensure the presence of exactly 1 {@link PropertyWrapper}
         * instance per parent object.
         *
         * @see PropertyWrapper#getMappedPropertyWrappers()
         */
        public static <T, PW extends PropertyWrapper<T, PW, PWB>, PWB extends PropertyWrapperBuilder<T, PWB, PW>> PW registerPropertyWrapper(Supplier<T> parentObject, PW propertyWrapper) {
            if (!propertyWrapper.isTemplate()) MAPPED_PROPERTY_WRAPPERS.putIfAbsent(parentObject, propertyWrapper);

            return propertyWrapper;
        }

        /**
         * Unregisters a {@link PropertyWrapper} instance by its mapped parent object.
         *
         * @param parentObject The parent object of the {@code propertyWrapper} to unregister.
         *
         * @param <T> The parent object type.
         */
        public static <T> void unregisterPropertyWrapper(Supplier<T> parentObject) {
            MAPPED_PROPERTY_WRAPPERS.remove(parentObject);
        }

        /**
         * Compiles an {@link ObjectArrayList} of all {@link PropertyWrapper} instances of the provided type
         * {@code class}, including subclasses.
         * <br></br>
         * <b>NOTE:</b> For variable value setting, collections will have to define explicit type arguments that
         * correspond to {@code PW} and {@code PWB} due to how Java's generic type inference works. To bypass this
         * constraint, use the {@linkplain #getInferrableWrappersOfType(Class) wildcard variant} of this method.
         * <br></br>
         * If you're working with datagen, you should prefer {@link #getInferrableDataGennableWrappersOfType(Class, String)}
         * instead.
         *
         * @param propertyWrapperTypeClass The {@link PropertyWrapper} type to filter by.
         *
         * @return A list of all {@link PropertyWrapper} instances corresponding to the provided type. Includes
         * subclasses. May be empty.
         *
         * @param <T> The parent object type.
         * @param <PW> The {@link PropertyWrapper} type.
         * @param <PWB> The {@link PropertyWrapperBuilder} type.
         *
         * @see #getWrappersOfType(Class, String)
         * @see #getWrappersFromMod(String)
         * @see #getInferrableWrappersOfType(Class)
         * @see #getInferrableWrappersFromMod(String)
         */
        public static <T, PW extends PropertyWrapper<T, PW, PWB>, PWB extends PropertyWrapperBuilder<T, PWB, PW>> ObjectArrayList<PW> getWrappersOfType(Class<? extends PW> propertyWrapperTypeClass) {
            return (ObjectArrayList<PW>) CACHED_WRAPPER_LOOKUP
                    .computeIfAbsent(propertyWrapperTypeClass, k -> new Object2ObjectOpenHashMap<>())
                    .computeIfAbsent("*", modId ->
                            MAPPED_PROPERTY_WRAPPERS.values().stream()
                                    .filter(propertyWrapperTypeClass::isInstance)
                                    .map(propertyWrapperTypeClass::cast)
                                    .collect(Collectors.toCollection(ObjectArrayList::new))
                    );
        }

        /**
         * Alternate variant of {@link #getWrappersOfType(Class)} that allows for broader inference of generic type
         * arguments.
         * <br></br>
         * If you're working with datagen, you should prefer {@link #getInferrableDataGennableWrappersOfType(Class, String)}
         * instead.
         *
         * @param propertyWrapperTypeClass The {@link PropertyWrapper} type to filter by.
         *
         * @return A list of all {@link PropertyWrapper} instances corresponding to the provided type. Includes
         * subclasses. May be empty.
         *
         * @param <PW> The {@link PropertyWrapper} type.
         *
         * @see #getWrappersOfType(Class)
         * @see #getWrappersFromMod(String)
         * @see #getWrappersOfType(Class, String)
         * @see #getInferrableWrappersFromMod(String)
         */
        public static <PW extends PropertyWrapper<?, ?, ?>> ObjectArrayList<PW> getInferrableWrappersOfType(Class<?> propertyWrapperTypeClass) {
            return (ObjectArrayList<PW>) CACHED_WRAPPER_LOOKUP
                    .computeIfAbsent(propertyWrapperTypeClass, k -> new Object2ObjectOpenHashMap<>())
                    .computeIfAbsent("*", modId ->
                            MAPPED_PROPERTY_WRAPPERS.values().stream()
                                    .filter(propertyWrapperTypeClass::isInstance)
                                    .collect(Collectors.toCollection(ObjectArrayList::new))
                    );
        }

        /**
         * Compiles an {@link ObjectArrayList} of all {@link PropertyWrapper} instances that are associated with the
         * provided mod ID. Automatically maps to {@code PW}.
         * <br></br>
         * <b>NOTE:</b> For variable value setting, collections will have to define explicit type arguments that
         * correspond to {@code PW} and {@code PWB} due to how Java's generic type inference works. To bypass this
         * constraint, use the {@linkplain #getInferrableWrappersFromMod(String) wildcard variant} of this method.
         * <br></br>
         * If you're working with datagen, you should prefer {@link #getInferrableDataGennableWrappersOfType(Class, String)}
         * instead.
         *
         * @param modId The mod ID to filter by.
         *
         * @return A list of all {@link PropertyWrapper} instances corresponding to the provided mod ID. May be empty.
         *
         * @param <T> The parent object type.
         * @param <PW> The {@link PropertyWrapper} type.
         * @param <PWB> The {@link PropertyWrapperBuilder} type.
         *
         * @apiNote Not an overload of {@link #getWrappersOfType(Class, String)} cuz memory overhead - O(n) VS O(2n).
         *
         * @see #getWrappersOfType(Class)
         * @see #getWrappersOfType(Class, String)
         * @see #getInferrableWrappersFromMod(String)
         */
        public static <T, PW extends PropertyWrapper<T, PW, PWB>, PWB extends PropertyWrapperBuilder<T, PWB, PW>> ObjectArrayList<PW> getWrappersFromMod(String modId) {
            return (ObjectArrayList<PW>) CACHED_WRAPPER_LOOKUP
                    .computeIfAbsent(PropertyWrapper.class, k -> new Object2ObjectOpenHashMap<>())
                    .computeIfAbsent(modId, curModId ->
                            MAPPED_PROPERTY_WRAPPERS.values().stream()
                                    .filter(propertyWrapper -> propertyWrapper.getModId().map(curModId::equals).orElse(false))
                                    .collect(Collectors.toCollection(ObjectArrayList::new))
                    );
        }

        /**
         * Alternate variant of {@link #getWrappersFromMod(String)} that allows for broader inference of generic type
         * arguments.
         * <br></br>
         * If you're working with datagen, you should prefer {@link #getInferrableDataGennableWrappersOfType(Class, String)}
         * instead.
         *
         * @param modId The mod ID to filter by.
         *
         * @return A list of all {@link PropertyWrapper} instances corresponding to the provided mod ID. May be empty.
         *
         * @param <PW> The {@link PropertyWrapper} type.
         *
         * @see #getWrappersFromMod(String)
         * @see #getWrappersOfType(Class, String)
         * @see #getInferrableWrappersOfType(Class, String)
         */
        public static <PW extends PropertyWrapper<?, ?, ?>> ObjectArrayList<PW> getInferrableWrappersFromMod(String modId) {
            return (ObjectArrayList<PW>) CACHED_WRAPPER_LOOKUP
                    .computeIfAbsent(PropertyWrapper.class, k -> new Object2ObjectOpenHashMap<>())
                    .computeIfAbsent(modId, curModId ->
                            MAPPED_PROPERTY_WRAPPERS.values().stream()
                                    .filter(propertyWrapper -> propertyWrapper.getModId().map(curModId::equals).orElse(false))
                                    .collect(Collectors.toCollection(ObjectArrayList::new))
                    );
        }

        /**
         * Compiles an {@link ObjectArrayList} of all {@link PropertyWrapper} instances of the provided type
         * {@code class}, including subclasses. Only includes instances that are associated with the provided mod ID.
         * <br></br>
         * <b>NOTE:</b> For variable value setting, collections will have to define explicit type arguments that
         * correspond to {@code PW} and {@code PWB} due to how Java's generic type inference works. To bypass this
         * constraint, use the {@linkplain #getInferrableWrappersOfType(Class, String) wildcard variant} of this method.
         * <br></br>
         * If you're working with datagen, you should prefer {@link #getInferrableDataGennableWrappersOfType(Class, String)}
         * instead.
         *
         * @param propertyWrapperTypeClass The {@link PropertyWrapper} type to filter by.
         * @param modId The mod ID to filter by.
         *
         * @return A list of all {@link PropertyWrapper} instances corresponding to the provided type and mod ID. Includes
         * subclasses. May be empty
         *
         * @param <T> The parent object type.
         * @param <PW> The {@link PropertyWrapper} type.
         * @param <PWB> The {@link PropertyWrapperBuilder} type.
         *
         * @apiNote Not an overload of {@link #getWrappersOfType(Class)} cuz memory overhead - O(n) VS O(2n).
         *
         * @see #getWrappersOfType(Class)
         * @see #getWrappersFromMod(String)
         * @see #getInferrableWrappersOfType(Class, String)
         * @see #getInferrableWrappersFromMod(String)
         */
        public static <T, PW extends PropertyWrapper<T, PW, PWB>, PWB extends PropertyWrapperBuilder<T, PWB, PW>> ObjectArrayList<PW> getWrappersOfType(Class<? extends PW> propertyWrapperTypeClass, String modId) {
            return (ObjectArrayList<PW>) CACHED_WRAPPER_LOOKUP
                    .computeIfAbsent(propertyWrapperTypeClass, k -> new Object2ObjectOpenHashMap<>())
                    .computeIfAbsent(modId, curModId ->
                            MAPPED_PROPERTY_WRAPPERS.values().stream()
                                    .filter(propertyWrapper -> propertyWrapperTypeClass.isInstance(propertyWrapper) && propertyWrapper.getModId().map(modId::equals).orElse(false))
                                    .map(propertyWrapperTypeClass::cast)
                                    .collect(Collectors.toCollection(ObjectArrayList::new))
                    );
        }

        /**
         * Alternate variant of {@link #getWrappersOfType(Class, String)} that allows for broader inference of generic type
         * arguments. Primarily helpful when defining field values using these methods.
         * <br></br>
         * If you're working with datagen, you should prefer {@link #getInferrableDataGennableWrappersOfType(Class, String)}
         * instead.
         *
         * @param pwClazz The {@link PropertyWrapper} type to filter by.
         * @param modId The mod ID to filter by.
         *
         * @return A list of all {@link PropertyWrapper} instances corresponding to the provided type and mod ID. Includes
         * subclasses. May be empty.
         *
         * @param <PW> The {@link PropertyWrapper} type.
         *
         * @see #getWrappersOfType(Class, String)
         * @see #getWrappersFromMod(String)
         * @see #getInferrableWrappersFromMod(String)
         */
        public static <PW extends PropertyWrapper<?, ?, ?>> ObjectArrayList<PW> getInferrableWrappersOfType(Class<?> pwClazz, String modId) {
            return (ObjectArrayList<PW>) CACHED_WRAPPER_LOOKUP
                    .computeIfAbsent(pwClazz, k -> new Object2ObjectOpenHashMap<>())
                    .computeIfAbsent(modId, curModId ->
                            MAPPED_PROPERTY_WRAPPERS.values().stream()
                                    .filter(propertyWrapper -> pwClazz.isInstance(propertyWrapper) && propertyWrapper.getModId().map(modId::equals).orElse(false))
                                    .map(pw -> (PW) pw)
                                    .collect(Collectors.toCollection(ObjectArrayList::new))
                    );
        }

        /**
         * Overloaded variant of {@link #getInferrableWrappersOfType(Class, String)} that filters for only
         * {@link DataGenPropertyWrapper} instances that are not excluded from data generation via
         * {@link DataGenPropertyWrapper#isExcludedFromDataGen()}.
         *
         * @param pwClazz The {@link PropertyWrapper} type to filter by.
         * @param modId The mod ID to filter by.
         *
         * @return A list of all {@link DataGenPropertyWrapper} instances corresponding to the provided type and mod ID,
         * and that are not excluded from data generation. Includes subclasses. May be empty.
         *
         * @param <PW> The {@link PropertyWrapper} type.
         *
         * @see #getInferrableWrappersOfType(Class, String)
         */
        public static <PW extends PropertyWrapper<?, ?, ?>> ObjectArrayList<PW> getInferrableDataGennableWrappersOfType(Class<?> pwClazz, String modId) {
            return getInferrableWrappersOfType(pwClazz, modId).stream()
                    .filter(propertyWrapper -> pwClazz.isInstance(propertyWrapper) && propertyWrapper instanceof DataGenPropertyWrapper<?, ?, ?> dgpw && !dgpw.isExcludedFromDataGen())
                    .map(propertyWrapper -> (PW) propertyWrapper)
                    .collect(Collectors.toCollection(ObjectArrayList::new));
        }
    }
}
