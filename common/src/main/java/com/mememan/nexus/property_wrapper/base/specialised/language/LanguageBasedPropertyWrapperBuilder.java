package com.mememan.nexus.property_wrapper.base.specialised.language;

import com.mememan.nexus.datagen.standard.StandardLanguageProvider;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Extension of {@link DataGenPropertyWrapperBuilder} with builder methods tailored towards localization for both
 * description IDs and misc translation keys (e.g. tooltips).
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link LanguageBasedPropertyWrapper}.
 *
 * @see LanguageBasedPropertyWrapper
 */
public interface LanguageBasedPropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends PropertyWrapper<T, LBPW, SELF>> extends DataGenPropertyWrapperBuilder<T, SELF, LBPW> {
    ObjectArrayList<String> DEFAULT_SEPARATOR_WORDS = ObjectArrayList.of("Of", "And");

    /**
     * Assigns a custom translation key for datagen. By default, a basic regex algorithm is used to automatically localize
     * the object name into something more legible (I.E. The names you see in-game). This property is simply an override
     * mechanic which aims to give the end-developer more control over the resulting name instead of being forced to rely on
     * the aforementioned algorithm.
     * <br></br>
     * The algorithm in question, in a nutshell, works as follows (the code block below is purely demonstrative of the
     * localization process and has nothing to do with how the algorithm is actually written):
     * <pre>
     *     {@code
     *      public class AlgorithmExampleDescriptor {
     *
     *          public static void main(String[] args) {
     *              // Input
     *              String unlocalizedName = "block.mymodid.my_material_block"; // The registry name/initial unlocalized name
     *
     *              // Steps
     *              AlgorithmLanguageProvider.validateNullity(unlocalizedName); // Checks whether the provided 'unlocalizedName' is empty/all whitespaces/you get the point
     *              AlgorithmLanguageProvider.validateRegex(unlocalizedName); // Checks whether the provided 'unlocalizedName' has the signature registry name separator character "."
     *              AlgorithmLanguageProvider.formatCaps(unlocalizedName); // Output: "Block.Mymodid.My_Material_Block" <-- Capitalizes the first letter of each word based on regex-checks for special separators ("." and "_") (First character all the way to the left is always capitalized (duh), not that it matters)
     *              AlgorithmLanguageProvider.formatSeparators(unlocalizedName); // Output: "Block.Mymodid.My_Material_Block" <-- Any defined "separator" Strings are lowercased, see #withCustomSeparatorWords(List)
     *              AlgorithmLanguageProvider.formatSpecialSeparators(unlocalizedName); // Output: "Block of My Material" <-- All characters preceding the last "." are substringed/removed, and then any "_" characters are replaced with whitespaces
     *
     *              // End result
     *              System.out.println(unlocalizedName); // Output: "Block of My Material"
     *          }
     *      }
     *     }
     * </pre>
     *
     * @param manuallyLocalizedObjectName The name override used to localize the parent object's registry name.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote Some different implementations have several different misc. mutations applied to them automatically, like
     * how block description IDs can be transformed from "material_block" to "Block of Material". You may use this
     * method (or the methods referenced below and their overloaded variant(s)) to bypass that step if needed.
     * Otherwise, object names are literally translated.
     *
     * @see #withCustomSeparatorWords(List)
     * @see #withLocalization(Function)
     * @see #literalTranslation(boolean)
     * @see #bypassDefaultTranslation(boolean)
     * @see #withAdditionalLocalizationKey(String)
     * @see StandardLanguageProvider
     */
    SELF withCustomName(String manuallyLocalizedObjectName);

    /**
     * Marks this builder as using literal translations, meaning that corrections (like the one seen in the example
     * provided by {@link #withCustomName(String)}) are not applied.
     *
     * @param literalTranslation Whether to use literal translations.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withCustomName(String)
     * @see #withLocalization(Function)
     * @see #literalTranslation()
     * @see #bypassDefaultTranslation(boolean)
     * @see #withAdditionalLocalizationKey(String)
     */
    SELF literalTranslation(boolean literalTranslation);

    /**
     * A custom {@link Function} to apply miscellaneous modifications to the resulting localized block name. This is
     * influenced by {@link #withCustomName(String)} and {@link #literalTranslation(boolean)}, where applicable.
     *
     * @param objectTranslationFunc The {@link Function} responsible for directly modifying the resulting localized
     *                              object name.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withCustomName(String)
     * @see #literalTranslation(boolean)
     */
    SELF withLocalization(Function<String, String> objectTranslationFunc);

    /**
     * Overloaded variant of {@link #literalTranslation(boolean)} which marks this builder as using literal translations.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #literalTranslation(boolean)
     */
    default SELF literalTranslation() {
        return literalTranslation(true);
    }

    /**
     * Whether this LBPWBuilder instance should skip the translation process altogether.
     * <br></br>
     * Note that data won't be generated for this instance (NPEs may be thrown too, based on the validation policy
     * for your mod) unless {@link #literalTranslation(boolean)} is marked as {@code true} or {@link #withCustomName(String)}
     * is set to a non-{@code null} value.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #literalTranslation(boolean)
     * @see #withCustomName(String)
     * @see #bypassDefaultTranslation()
     */
    SELF bypassDefaultTranslation(boolean bypassDefaultTranslation);

    /**
     * Overloaded variant of {@link #bypassDefaultTranslation(boolean)}, marking this builder to be skipped by the
     * default localization algorithm Nexus API employs. See the base variant for more info.
     *
     * @return {@link #bypassDefaultTranslation(boolean)}
     *
     * @see #literalTranslation(boolean)
     * @see #withCustomName(String)
     * @see #bypassDefaultTranslation(boolean)
     */
    default SELF bypassDefaultTranslation() {
        return bypassDefaultTranslation(true);
    }

    /**
     * Assigns a custom separator word which is lowercased during the algorithm's de-localization process. This is
     * ignored if {@link #withCustomName(String)} is defined, {@link #literalTranslation()} is {@code true}, or if
     * {@link #withLocalization(Function)} is non-null.
     *
     * @param customSeparatorWord The custom separator word to lowercase while the algorithm is running.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote The default entries for this are {"Of", "And"}. This word is appended to the default separator
     * definitions rather than replacing them.
     *
     * @see #withCustomSeparatorWords(String...)
     * @see #withCustomSeparatorWords(List)
     * @see #withCustomName(String)
     * @see #withLocalization(Function)
     * @see #withCustomSeparatorWords(List)
     * @see #literalTranslation(boolean)
     */
    SELF withCustomSeparatorWord(String customSeparatorWord);

    /**
     * Assigns a {@link List} of custom separator words which are lowercased during the algorithm's de-localization
     * process. This is ignored if {@link #withCustomName(String)} is defined, {@link #literalTranslation()} is
     * {@code true}, or if {@link #withLocalization(Function)} is non-null.
     *
     * @param definedSeparatorWords The {@link List} of custom separator words to lowercase while the algorithm is
     *                              running.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote The default entries for this are {"Of", "And"}. This {@link List} is appended to the default
     * separator definitions rather than replacing them.
     *
     * @see #withCustomSeparatorWord(String)
     * @see #withCustomSeparatorWords(String...)
     * @see #withCustomName(String)
     * @see #withLocalization(Function)
     * @see #literalTranslation(boolean)
     */
    SELF withCustomSeparatorWords(List<String> definedSeparatorWords);

    /**
     * Overloaded variant of {@link #withCustomSeparatorWords(List)}, assigning a {@link List} of custom separator
     * words which are lowercased during the algorithm's de-localization process from the provided array.
     *
     * @param definedSeparatorWords The {@link List} of custom separator words to lowercase while the algorithm is
     *                              running.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote The default entries for this are {"Of", "And"}. This {@link List} is appended to the default
     * separator definitions rather than replacing them.
     *
     * @see #withCustomSeparatorWord(String)
     * @see #withCustomSeparatorWords(List)
     * @see #withCustomName(String)
     * @see #withLocalization(Function)
     * @see #literalTranslation(boolean)
     */
    default SELF withCustomSeparatorWords(String... definedSeparatorWords) {
        return withCustomSeparatorWords(ObjectArrayList.of(definedSeparatorWords));
    }

    /**
     * Sets a {@link List} of custom separator words which are lowercased during the algorithm's de-localization
     * process. This is ignored if {@link #withCustomName(String)} is defined, {@link #literalTranslation()} is
     * {@code true}, or if {@link #withLocalization(Function)} is non-null.
     *
     * @param definedSeparatorWords The {@link List} of custom separator words to lowercase while the algorithm is
     *                              running.
     *
     * @return {@link #self()} (builder method).
     *
     * @apiNote The default entries for this are {"Of", "And"}. This {@link List} is appended to the default
     * separator definitions rather than replacing them.
     *
     * @see #withCustomName(String)
     * @see #withLocalization(Function)
     * @see #withCustomSeparatorWords(List)
     * @see #literalTranslation(boolean)
     */
    SELF setCustomSeparatorWords(List<String> definedSeparatorWords);

    /**
     * Adds a new translation key to this LBPWBuilder instance for localization through datagen. Automatically localizes
     * the provided key using the standard Nexus localization algorithm (see {@link #withCustomName(String)} for more
     * info).
     * <br></br>
     * Note that mutations made via {@link #withCustomName(String)}, {@link #literalTranslation(boolean)}, and/or
     * {@link #withLocalization(Function)} are not applied here.
     *
     * @param localizationKey The key to localize (e.g. "tooltip.mod_id.block_name").
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withAdditionalLocalizationKey(String, String)
     * @see #withAdditionalLocalizationKeys(List)
     * @see #withAdditionalLocalizationKeys(String...)
     * @see #withAdditionalLocalizationKey(String, Function)
     * @see #withCustomName(String)
     */
    SELF withAdditionalLocalizationKey(String localizationKey);

    /**
     * Appends the provided list of keys to this LBPWBuilder instance for localization through datagen. Automatically
     * localizes the provided keys using the standard Nexus localization algorithm (see {@link #withCustomName(String)}
     * for more info).
     * <br></br>
     * Note that mutations made via {@link #withCustomName(String)}, {@link #literalTranslation(boolean)}, and/or
     * {@link #withLocalization(Function)} are not applied here.
     *
     * @param localizationKeys The keys to localize (e.g. "tooltip.mod_id.block_name").
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withAdditionalLocalizationKey(String)
     * @see #withAdditionalLocalizationKey(String, String)
     * @see #withAdditionalLocalizationKeys(String...)
     * @see #withAdditionalLocalizationKey(String, Function)
     * @see #withCustomName(String)
     */
    SELF withAdditionalLocalizationKeys(List<String> localizationKeys);

    /**
     * Appends the provided array of keys to this LBPWBuilder instance for localization through datagen. Automatically
     * localizes the provided keys using the standard Nexus localization algorithm (see {@link #withCustomName(String)}
     * for more info).
     * <br></br>
     * Note that mutations made via {@link #withCustomName(String)}, {@link #literalTranslation(boolean)}, and/or
     * {@link #withLocalization(Function)} are not applied here.
     *
     * @param localizationKeys The keys to localize (e.g. "tooltip.mod_id.block_name").
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withAdditionalLocalizationKey(String)
     * @see #withAdditionalLocalizationKey(String, String)
     * @see #withAdditionalLocalizationKeys(List)
     * @see #withAdditionalLocalizationKey(String, Function)
     * @see #withCustomName(String)
     */
    default SELF withAdditionalLocalizationKeys(String... localizationKeys) {
        return withAdditionalLocalizationKeys(ObjectArrayList.of(localizationKeys));
    }

    /**
     * Adds a new translation key to this LBPWBuilder instance for localization through datagen. Uses the provided
     * {@code localizedValue} directly instead of automatically localizing the provided key using the standard Nexus
     * localization algorithm (see {@link #withCustomName(String)} for more info).
     * <br></br>
     * Note that mutations made via {@link #withCustomName(String)}, {@link #literalTranslation(boolean)}, and/or
     * {@link #withLocalization(Function)} are not applied here.
     *
     * @param localizationKey The key to localize (e.g. "tooltip.mod_id.block_name").
     * @param localizedValue The localized value to use (e.g. "Block Name"). Note that if this is {@code null}, then
     *                       the key provided will behave similarly to {@link #withAdditionalLocalizationKey(String)}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withAdditionalLocalizationKey(String, String)
     * @see #withAdditionalLocalizationKey(String, Function)
     * @see #withAdditionalLocalizationKeys(String[], String[])
     * @see #withCustomName(String)
     */
    SELF withAdditionalLocalizationKey(String localizationKey, String localizedValue);

    /**
     * Adds the provided array of new translation keys to this LBPWBuilder instance for localization through datagen.
     * Uses the provided {@code localizedValues} directly instead of automatically localizing the provided keys using
     * the standard Nexus localization algorithm (see {@link #withCustomName(String)} for more info).
     * <br></br>
     * Each localization key is matched with a value at the same index. If both arrays differ in length, the smaller
     * array will be used for iteration.
     * <br></br>
     * Note that mutations made via {@link #withCustomName(String)}, {@link #literalTranslation(boolean)}, and/or
     * {@link #withLocalization(Function)} are not applied here.
     *
     * @param localizationKeys The keys to localize (e.g. "tooltip.mod_id.block_name").
     * @param localizedValues The localized values to use (e.g. "Block Name"). Note that any keys corresponding to
     *                        {@code null} values will behave similarly to {@link #withAdditionalLocalizationKey(String)}.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withAdditionalLocalizationKey(String, String)
     * @see #withAdditionalLocalizationKey(String, Function)
     * @see #withCustomName(String)
     */
    SELF withAdditionalLocalizationKeys(String[] localizationKeys, String[] localizedValues);

    /**
     * Adds a new translation key to this LBPWBuilder instance for localization through datagen. Uses the provided
     * {@code localizedValueMapper} directly instead of automatically localizing the provided key using the standard Nexus
     * localization algorithm (see {@link #withCustomName(String)} for more info).
     * <br></br>
     * The specified {@linkplain Function Functions} input is the auto-localized key (as specified in
     * {@link #withAdditionalLocalizationKey(String)}).
     *
     * <br></br>
     * Note that mutations made via {@link #withCustomName(String)}, {@link #literalTranslation(boolean)}, and/or
     * {@link #withLocalization(Function)} are not applied here.
     *
     * @param localizationKey The key to localize (e.g. "tooltip.mod_id.block_name").
     * @param localizedValueMapper A {@link Function} which provides the auto-localized key for modification and application.
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withAdditionalLocalizationKey(String)
     * @see #withAdditionalLocalizationKey(String, String)
     * @see #setAdditionalLocalizationKeys(Map)
     * @see #withCustomName(String)
     */
    SELF withAdditionalLocalizationKey(String localizationKey, Function<String, String> localizedValueMapper);

    /**
     * Sets a {@link Map} of new translation keys to this LBPWBuilder instance for localization through datagen. Overrides
     * the existing translation keys.
     * <br></br>
     * Note that mutations made via {@link #withCustomName(String)}, {@link #literalTranslation(boolean)}, and/or
     * {@link #withLocalization(Function)} are not applied here.
     *
     * @param localizationKeys A map of translation keys to localized values (e.g. "tooltip.mod_id.block_name" ->
     *                         "Block Name"). The specified {@linkplain Function Function's} input is the auto-localized
     *                         key (as specified in {@link #withAdditionalLocalizationKey(String)}).
     *
     * @return {@link #self()} (builder method).
     *
     * @see #withAdditionalLocalizationKey(String)
     * @see #withAdditionalLocalizationKey(String, String)
     * @see #withAdditionalLocalizationKey(String, Function)
     * @see #withCustomName(String)
     */
    SELF setAdditionalLocalizationKeys(Map<String, Function<String, String>> localizationKeys);
}
