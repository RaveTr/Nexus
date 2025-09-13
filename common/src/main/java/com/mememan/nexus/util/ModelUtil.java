package com.mememan.nexus.util;

import net.minecraft.data.models.model.TextureMapping;

/**
 * Utility {@code class} containing helpful model shortcut/delegator helper methods, as well as some re-used constants
 * related to models in general.
 */
public final class ModelUtil {
    public static final TextureMapping EMPTY_TEXTURE_MAPPING = new TextureMapping();

    private ModelUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (ModelUtil)");
    }
}
