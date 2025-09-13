package com.mememan.nexus.util;

import com.google.gson.JsonArray;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Utility {@code class} providing shortcut helper methods for converting objects to JSON directly, as well as some other
 * misc. JSON-related utilities.
 */
public final class JsonUtil {

    private JsonUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (JsonUtil)");
    }

    /**
     * Shortcut helper method that directly converts a {@linkplain Vector3f} to a {@linkplain JsonArray}.
     *
     * @param vec3f The {@link Vector3f} object to convert.
     *
     * @return The converted {@link JsonArray}.
     */
    public static JsonArray createVec3fArray(Vector3f vec3f) {
        JsonArray vec3fArray = new JsonArray();

        vec3fArray.add(vec3f.x);
        vec3fArray.add(vec3f.y);
        vec3fArray.add(vec3f.z);

        return vec3fArray;
    }

    /**
     * Shortcut helper method that directly converts a {@linkplain Vector3f} array to a {@linkplain JsonArray}.
     *
     * @param vecsToCombine The {@link Vector3f} objects to convert, where each {@code x, y, z} element is added directly.
     *
     * @return The converted {@link JsonArray}.
     */
    public static JsonArray flatConcatVec3fArrays(Vector3f... vecsToCombine) {
        JsonArray vec3fArray = new JsonArray();

        for (Vector3f vec : vecsToCombine) {
            vec3fArray.add(vec.x);
            vec3fArray.add(vec.y);
            vec3fArray.add(vec.z);
        }

        return vec3fArray;
    }

    /**
     * Shortcut helper method that directly converts a {@linkplain Vector2f} to a {@linkplain JsonArray}.
     *
     * @param vec2f The {@link Vector2f} object to convert.
     *
     * @return The converted {@link JsonArray}.
     */
    public static JsonArray createVec2fArray(Vector2f vec2f) {
        JsonArray vec2fArray = new JsonArray();

        vec2fArray.add(vec2f.x);
        vec2fArray.add(vec2f.y);

        return vec2fArray;
    }

    /**
     * Shortcut helper method that directly converts a {@linkplain Vector2f} array to a {@linkplain JsonArray}.
     *
     * @param vecsToCombine The {@link Vector2f} objects to convert, where each {@code x, y} element is added directly.
     *
     * @return The converted {@link JsonArray}.
     */
    public static JsonArray flatConcatVec2fArrays(Vector2f... vecsToCombine) {
        JsonArray vec2fArray = new JsonArray();

        for (Vector2f vec : vecsToCombine) {
            vec2fArray.add(vec.x);
            vec2fArray.add(vec.y);
        }

        return vec2fArray;
    }
}
