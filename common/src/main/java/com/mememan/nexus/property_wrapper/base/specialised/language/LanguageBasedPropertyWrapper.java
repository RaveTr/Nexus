package com.mememan.nexus.property_wrapper.base.specialised.language;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards localization for both description IDs and
 * misc translation keys (e.g. tooltips).
 * <br></br>
 * This PW extension goes hand-in-hand with {@link LanguageBasedPropertyWrapperBuilder}.
 *
 * @see LanguageBasedPropertyWrapperBuilder
 */
public interface LanguageBasedPropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> extends DataGenPropertyWrapper<T, SELF, BUILDER> {

    /**
     * Gets the description ID for the object being wrapped. Used as the object's key during automatic localization.
     *
     * @return The description ID for the object being wrapped.
     */
    @NotNull
    String getObjectDescriptionId();

    /**
     * Gets the custom name for the object being wrapped, if present. May be empty.
     *
     * @return The custom name for the object being wrapped, if present.
     *
     * @see LanguageBasedPropertyWrapperBuilder#withCustomName(String)
     */
    Optional<String> getCustomName();

    /**
     * Gets the custom post-translation mapper for the object being wrapped, if present. May be empty.
     *
     * @return The custom post-translation mapper for the object being wrapped, if present.
     *
     * @see LanguageBasedPropertyWrapperBuilder#withLocalization(Function)
     */
    Optional<Function<String, String>> getObjectPostTranslationMapper();

    /**
     * Gets the custom separator words for the localized name of the object being wrapped, if present. Defaults to
     * a {@link ObjectArrayList} of "Of" and "And".
     *
     * @return The custom separator words for the object being wrapped, or the default values if no custom separators are
     * specified.
     *
     * @see LanguageBasedPropertyWrapperBuilder#withCustomSeparatorWords(List)
     */
    List<String> getCustomSeparatorWords();

    /**
     * Whether the object being wrapped has a literal translation (e.g. "material_block" -> "Material Block").
     *
     * @return Whether the object being wrapped has a literal translation.
     *
     * @see LanguageBasedPropertyWrapperBuilder#literalTranslation(boolean)
     * @see LanguageBasedPropertyWrapperBuilder#withCustomName(String)
     */
    boolean hasLiteralTranslation();

    /**
     * Whether the object being wrapped should skip Nexus' default translation process.
     *
     * @return Whether the object being wrapped should skip Nexus' default translation process.
     *
     * @see LanguageBasedPropertyWrapperBuilder#bypassDefaultTranslation(boolean)
     */
    boolean bypassesDefaultTranslation();

    /**
     * Gets a {@link Map} of translation keys to localized values (e.g. "tooltip.mod_id.block_name" -> "Block Name").
     *
     * @return A {@link Map} of translation keys to localized values associated with this LBPW. May be empty.
     *
     * @see LanguageBasedPropertyWrapperBuilder#withAdditionalLocalizationKey(String)
     * @see LanguageBasedPropertyWrapperBuilder#withAdditionalLocalizationKey(String, String)
     * @see LanguageBasedPropertyWrapperBuilder#withAdditionalLocalizationKey(String, Function)
     */
    Map<String, Function<String, String>> getAdditionalLocalizationKeys();
}
