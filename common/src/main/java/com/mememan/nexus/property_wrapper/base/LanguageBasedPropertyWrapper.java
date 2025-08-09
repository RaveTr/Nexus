package com.mememan.nexus.property_wrapper.base;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards localization for both description IDs and
 * misc translation keys (e.g. tooltips).
 * <br></br>
 * This PW extension goes hand-in-hand with {@link LanguageBasedPropertyWrapperBuilder}.
 *
 * @param <T> The object type being wrapped.
 * @param <SELF> Generic type for this PW {@code interface}. You would usually pass the implementing {@code class} or
 *               extending {@code interface} here.
 */
public interface LanguageBasedPropertyWrapper<T, SELF extends LanguageBasedPropertyWrapper<T, SELF>> extends DataGenPropertyWrapper<T, SELF> {



}
