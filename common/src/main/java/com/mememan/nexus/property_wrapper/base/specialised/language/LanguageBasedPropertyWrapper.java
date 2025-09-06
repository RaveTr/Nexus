package com.mememan.nexus.property_wrapper.base.specialised.language;

import com.mememan.nexus.datagen.standard.StandardLanguageProvider;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.util.StringUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
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

    /**
     * The backing method responsible for automatically localizing keys for the object being wrapped if it has no
     * {@linkplain #getCustomName() custom name}, does not {@linkplain #bypassesDefaultTranslation() bypass default
     * translation}, and does not have a {@linkplain #hasLiteralTranslation() literal translation}. Factors
     * {@linkplain #getCustomSeparatorWords() separator words} into account. Additionally, handles
     * {@linkplain #getObjectPostTranslationMapper() post-translation mapping} (if applicable).
     *
     * @return The localized key wrapped in an {@link Optional}. May be empty to indicate that the data in this property
     * wrapper cannot logically produce a localized value for the wrapped object's key (e.g. if the object
     * {@linkplain #bypassesDefaultTranslation() bypasses default translation} and has no {@linkplain #getCustomName()
     * custom name} or {@linkplain #hasLiteralTranslation() literal translation}).
     *
     * @param postTranslationLoggingCallback An optional {@link BiConsumer} to allow for additional logging of
     *                                       post-translation mapping based on the resulting localized value of the
     *                                       {@linkplain #getParentObject() parent object}, if applicable.
     *
     * @see LanguageBasedPropertyWrapperBuilder#withCustomName(String)
     * @see StringUtil#literallyLocalize(String, List)
     * @see StringUtil#localizeWithDefaultAssertions(String, List)
     * @see StandardLanguageProvider
     */
    default Optional<String> getLocalizedObjectKey(@Nullable BiConsumer<String, String> postTranslationLoggingCallback) {
        String unlocalizedKey = getObjectDescriptionId();

        return Optional.ofNullable(getCustomName().orElseGet(() -> {
            if (hasLiteralTranslation()) return StringUtil.literallyLocalize(unlocalizedKey, getCustomSeparatorWords());
            else if (bypassesDefaultTranslation()) return null;
            else return StringUtil.localizeWithDefaultAssertions(unlocalizedKey, getCustomSeparatorWords());
        })).map(locVal -> {
            if (getObjectPostTranslationMapper().isPresent()) {
                String mappedValue = getObjectPostTranslationMapper().get().apply(locVal);

                if (postTranslationLoggingCallback != null) postTranslationLoggingCallback.accept(locVal, mappedValue);

                return mappedValue;
            } else return locVal;
        });
    }
}
