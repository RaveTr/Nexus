package com.mememan.nexus.util;

import com.google.gson.JsonArray;
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
}
