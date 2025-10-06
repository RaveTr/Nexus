package com.mememan.nexus.util;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * Utility {@code class} containing extensive implementations and helpful shortcut/delegator methods for different data
 * generation tasks.
 */
public final class DataGenUtil {
    public static final BiFunction<JsonElement, JsonElement, JsonElement> TAG_FILE_MERGER = (existingContent, newContent) -> {
        if (!(existingContent instanceof JsonObject existingObj) || !(newContent instanceof JsonObject newObj)) {
            return newContent; // Directly overwrite if either is not a JSON object (i.e. invalid, duh)
        }

        JsonObject result = new JsonObject();
        JsonArray values = new JsonArray();
        Set<String> existingValues = new ObjectOpenHashSet<>(); // Keep track of added values to avoid dupes
        Set<String> newValues = new ObjectOpenHashSet<>(); // Avoid keeping stale values

        if (newObj.has("values")) { // Add new values (if any)
            newObj.getAsJsonArray("values").forEach(value -> {
                String valueStr = value.toString();

                newValues.add(valueStr);
                if (existingValues.add(valueStr)) values.add(value);
            });
        }

        if (existingObj.has("values")) {  // Add existing values (if any)
            existingObj.getAsJsonArray("values").forEach(value -> {
                String valueStr = value.toString();

                if (newValues.contains(valueStr) && existingValues.add(valueStr)) values.add(value);
            });
        }

        result.add("values", values);

        // Handle replace flag (if present)
        if (newObj.has("replace")) result.addProperty("replace", newObj.get("replace").getAsBoolean());
        else if (existingObj.has("replace")) result.addProperty("replace", existingObj.get("replace").getAsBoolean());

        return result;
    };

    private DataGenUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (DataGenUtil)");
    }

    /**
     * Extended delegator variant of {@link DataProvider#saveStable(CachedOutput, JsonElement, Path)} with the optional
     * functionality of lazily merging the provided {@code newContent} with its existing counterpart, if present.
     * <br></br>
     * Useful in cases where resources may be targeted by multiple mods, such as tags from Minecraft's namespace: If
     * 2 mods using Nexus are targeting the same tag file, this method ensures that both of their outputs are properly
     * merged instead of silently overwriting each other.
     *
     * @param targetOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     * @param newContent The content/file to save.
     * @param outputPath The {@link Path} to which {@code newContent} should be written/saved.
     * @param mergerFunc The {@link BiFunction} to use for merging the existing content with the new content, if
     *                   appropriate.
     *
     * @return A {@link CompletableFuture} that completes when the serialization to disk is complete.
     *
     * @see #TAG_FILE_MERGER
     * @see DataProvider#saveStable(CachedOutput, JsonElement, Path)
     */
    public static CompletableFuture<?> saveStableAndMerge(CachedOutput targetOutput, JsonElement newContent, Path outputPath, BiFunction<JsonElement, JsonElement, JsonElement> mergerFunc) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!Files.exists(outputPath)) return DataProvider.saveStable(targetOutput, newContent, outputPath); // If file doesn't exist, just save the new content (well, duh)

                JsonElement existingContent;

                try (BufferedReader reader = Files.newBufferedReader(outputPath, StandardCharsets.UTF_8)) {
                    existingContent = JsonParser.parseReader(reader);
                } catch (JsonParseException e) {
                    DataProvider.LOGGER.warn("Failed to parse existing file {}, will overwrite", outputPath, e);
                    return DataProvider.saveStable(targetOutput, newContent, outputPath);
                }

                String existingHash = JsonUtil.hashJson(existingContent);
                String newHash = JsonUtil.hashJson(newContent);

                if (Objects.equals(existingHash, newHash)) return DataProvider.saveStable(targetOutput, existingContent, outputPath); // Skip doing anything entirely and just return the existing content (avoid un-writing from disk in cases of re-generation)

                // Otherwise, perform merge and save
                JsonElement mergedContent = mergerFunc.apply(existingContent, newContent);

                return DataProvider.saveStable(targetOutput, mergedContent, outputPath);
            } catch (IOException e) {
                throw new UncheckedIOException(String.format("Failed to process file: %s", outputPath), e);
            }
        }, Util.backgroundExecutor())
                .thenCompose(innerFuture -> innerFuture != null ? innerFuture : CompletableFuture.completedFuture(null)); // Actually return the result (kinda similar to how terminal operations work in streams, but, y'know, we need a value returned, duh)
    }
}
