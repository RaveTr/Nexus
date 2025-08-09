package com.mememan.nexus.property_wrapper.base;

/**
 * Extension of {@link DataGenPropertyWrapperBuilder} with builder methods tailored towards localization for both
 * description IDs and misc translation keys (e.g. tooltips).
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link LanguageBasedPropertyWrapper}.
 *
 * @param <T> The object type being wrapped.
 * @param <LBPW> The {@link LanguageBasedPropertyWrapper} type being built, and whose generic type is {@code T}.
 */
public interface LanguageBasedPropertyWrapperBuilder<T, LBPW extends LanguageBasedPropertyWrapper<T, LBPW>> extends DataGenPropertyWrapperBuilder<T, LBPW> {

    

}
