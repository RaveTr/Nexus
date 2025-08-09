package com.mememan.nexus.property_wrapper.base;

import java.util.List;
import java.util.function.Function;

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
     * @return {@code this} (builder method).
     *
     * @apiNote Some different implementations have several different misc. mutations applied to them automatically, like
     * how block description IDs can be transformed from "material_block" to "Block of Material." You may use this
     * method (or the methods referenced below and their overloaded variant(s)) to bypass that step if needed.
     * Otherwise, object names are literally translated.
     *
     * @see #withCustomSeparatorWords(List)
     * @see #withLocalization(Function)
     * @see #literalTranslation(boolean)
     * @see #bypassDefaultTranslation(boolean)
     */
    LanguageBasedPropertyWrapperBuilder<T, LBPW> withCustomName(String manuallyLocalizedObjectName);

    /**
     * Marks this builder as using literal translations, meaning that corrections (like the one seen in the example
     * provided by {@link #withCustomName(String)}) are not applied.
     *
     * @param literalTranslation Whether to use literal translations.
     *
     * @return {@code this} (builder method).
     *
     * @see #withCustomName(String)
     * @see #withLocalization(Function)
     * @see #literalTranslation()
     * @see #bypassDefaultTranslation(boolean)
     */
    LanguageBasedPropertyWrapperBuilder<T, LBPW> literalTranslation(boolean literalTranslation);

    /**
     * A custom {@link Function} to apply miscellaneous modifications to the resulting localized block name. This is
     * influenced by {@link #withCustomName(String)} and {@link #literalTranslation(boolean)}, where applicable.
     *
     * @param objectTranslationFunc The {@link Function} responsible for directly modifying the resulting localized
     *                              object name.
     *
     * @return {@code this} (builder method).
     *
     * @see #withCustomName(String)
     * @see #literalTranslation(boolean)
     */
    LanguageBasedPropertyWrapperBuilder<T, LBPW> withLocalization(Function<String, String> objectTranslationFunc);

    /**
     * Overloaded variant of {@link #literalTranslation(boolean)} which marks this builder as using literal translations.
     *
     * @return {@code this} (builder method).
     *
     * @see #literalTranslation(boolean)
     */
    default LanguageBasedPropertyWrapperBuilder<T, LBPW> literalTranslation() {
        return literalTranslation(true);
    }

    /**
     * Whether this LBPWBuilder instance should skip the translation process altogether.
     * <br></br>
     * Note that data won't be generated for this instance (NPEs may be thrown too, based on the validation policy
     * for your mod) unless {@link #literalTranslation(boolean)} is marked as {@code true} or {@link #withCustomName(String)}
     * is set to a non-{@code null} value.
     *
     * @return {@code this} (builder method).
     *
     * @see #literalTranslation(boolean)
     * @see #withCustomName(String)
     * @see #bypassDefaultTranslation()
     */
    LanguageBasedPropertyWrapperBuilder<T, LBPW> bypassDefaultTranslation(boolean bypassDefaultTranslation);

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
    default LanguageBasedPropertyWrapperBuilder<T, LBPW> bypassDefaultTranslation() {
        return bypassDefaultTranslation(true);
    }

    /**
     * Assigns a {@link List} of custom separator words which are lowercased during the algorithm's de-localization
     * process. This is ignored if {@link #withCustomName(String)} is defined, {@link #literalTranslation()} is
     * {@code true}, or if {@link #withLocalization(Function)} is non-null.
     *
     * @param definedSeparatorWords The {@link List} of custom separator words to lowercase while the algorithm is
     *                              running.
     *
     * @return {@code this} (builder method).
     *
     * @apiNote The default entries for this are {"Of", "And"}. This {@link List} is appended to the default
     * separator definitions rather than replacing them.
     *
     * @see #withCustomName(String)
     * @see #withLocalization(Function)
     * @see #literalTranslation(boolean)
     */
    LanguageBasedPropertyWrapperBuilder<T, LBPW> withCustomSeparatorWords(List<String> definedSeparatorWords);

    LanguageBasedPropertyWrapperBuilder<T, LBPW> withAdditionalLocalizationKey(String localizationKey);

    LanguageBasedPropertyWrapperBuilder<T, LBPW> withAdditionalLocalizationKey(String localizationKey, String localizedValue);
}
