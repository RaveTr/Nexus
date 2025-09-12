package com.mememan.nexus.util;

import java.util.List;
import java.util.Locale;

/**
 * Generic utility {@code class} that provides additional methods for convenient {@link String manipulation} beyond what
 * is provided by both Java's standard library and other included libraries (such as Guava and Apache Commons).
 */
public final class StringUtil {

    private StringUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (StringUtil)");
    }

    /**
     * Transforms the {@code targetString} into a title case {@link String} based on the specified {@code delimiters} with
     * O(n) time complexity.
     *
     * @param targetString The input to process.
     * @param delimiters The delimiters to use as breaks for capitalization.
     *
     * @return The transformed {@link String}, or the original {@code targetString} if it is {@code null}, blank, or if
     *         {@code delimiters} is empty.
     */
    public static String toTitleCase(String targetString, char... delimiters) {
        if (targetString == null || targetString.isBlank() || delimiters.length == 0) return targetString;

        boolean[] isDelimiter = new boolean[Character.MAX_VALUE + 1]; // Pre-process delimiters into a set for O(1) lookups

        for (char delimiter : delimiters) isDelimiter[delimiter] = true;

        char[] inputChars = targetString.toCharArray();
        boolean capitalizeNext = true;
        int writeIndex = 0;

        for (int i = 0; i < inputChars.length; i++) {
            char current = inputChars[i];

            if (capitalizeNext && Character.isLetter(current)) {
                inputChars[writeIndex++] = Character.toTitleCase(current);
                capitalizeNext = false;
            } else if (current < isDelimiter.length && isDelimiter[current]) {
                if (writeIndex > 0 && (writeIndex >= inputChars.length || !isDelimiter[inputChars[writeIndex - 1]])) {
                    inputChars[writeIndex++] = ' ';
                }

                capitalizeNext = true;
            } else {
                inputChars[writeIndex++] = current;
                capitalizeNext = false;
            }
        }

        if (writeIndex > 0 && inputChars[writeIndex - 1] == ' ') writeIndex--; // In case we ended with a delimiter

        return new String(inputChars, 0, writeIndex);
    }

    /**
     * Overloaded variant of {@link #toTitleCase(String, char...)} with {@code delimiters} set to {@code ['_', '-', ' ']}.
     * Capitalizes a {@link String} formatted in snake case into a title case {@link String}, e.g.
     * {@code "some_lower_case_string"} -> {@code "Some Lower Case String"}.
     *
     * @param targetString the {@link String} to capitalize.
     *
     * @return The capitalized {@link String}.
     *
     * @see #toTitleCase(String, char...)
     */
    public static String toTitleCase(String targetString) {
        return toTitleCase(targetString, '_', '-', ' ');
    }

    /**
     * Attempts to localize a string by removing all prefixed words behind the last {@code '.'} and capitalizing the
     * remaining {@link String} using {@link #toTitleCase(String)}.
     *
     * @param unlocalizedInput The unlocalized key to localize.
     * @param separatorWords An optional {@link List} of words to un-capitalize from the unlocalized key.
     *
     * @return The literally-localized {@link String}, or the original {@code unlocalizedInput} if it is {@code null},
     * blank, or does not contain {@code '.'}.
     */
    public static String literallyLocalize(String unlocalizedInput, List<String> separatorWords) {
        if (unlocalizedInput == null || unlocalizedInput.isBlank() || !unlocalizedInput.contains(".")) return unlocalizedInput;

        String transformedPrunedInput = toTitleCase(unlocalizedInput.substring(unlocalizedInput.lastIndexOf(".") + 1));

        for (String separatorWord : separatorWords) {
            transformedPrunedInput = transformedPrunedInput.replaceAll("\\b" + separatorWord + "\\b", separatorWord.toLowerCase(Locale.ROOT)); // Match using word boundaries to prevent partial matches within words (e.g. Match "of" in "X of", but not "Stroganoff")
        }

        return transformedPrunedInput;
    }

    /**
     * Attempts to localize a string by removing all prefixed words behind {@code '.'} and capitalizing the remaining
     * {@link String} using {@link #toTitleCase(String)}. Modifies the resulting literally-localized {@link String}
     * based on default presumptions.
     *
     * @param unlocalizedInput The unlocalized key to localize.
     * @param separatorWords An optional {@link List} of words to un-capitalize from the unlocalized key.
     *
     * @return The post-processed localized {@link String}.
     */
    public static String localizeWithDefaultAssertions(String unlocalizedInput, List<String> separatorWords) {
        String localizedInput = literallyLocalize(unlocalizedInput, separatorWords);

        // Blocks
        if (localizedInput.endsWith(" Block")) {
            localizedInput = "Block of " + localizedInput.substring(0, localizedInput.length() - " Block".length());
        }

        return localizedInput;
    }
}
