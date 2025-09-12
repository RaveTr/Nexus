package com.mememan.nexus.client.model;

import org.joml.Vector3f;

import java.util.Objects;

/**
 * Data-holder {@code record} that stores immutable model transform data. This is effectively a side-safe data-storing
 * generalized variant of {@link net.minecraft.client.renderer.block.model.ItemTransform}.
 * <br></br>
 * Primarily used for item model transformations, but can be implemented anywhere, really (duh).
 *
 * @param leftRotation The target rotation of the model from the left.
 * @param rightRotation The target rotation of the model from the right.
 * @param translation The target model's translation (offset) from its origin position.
 * @param scale The target model's scale.
 */
public record ModelTransform(Vector3f leftRotation, Vector3f rightRotation, Vector3f translation, Vector3f scale) {
    public static final Vector3f DEFAULT_ROTATION = new Vector3f();
    public static final Vector3f DEFAULT_TRANSLATION = new Vector3f();
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

    /**
     * Convenience method to check if the left rotation is present and different from the {@linkplain #DEFAULT_ROTATION
     * default rotation}.
     *
     * @return {@code true} if the left rotation is present and different from the {@linkplain #DEFAULT_ROTATION default
     * rotation}, {@code false} otherwise.
     */
    public boolean hasLeftRotation() {
        return leftRotation != null && !Objects.equals(leftRotation, DEFAULT_ROTATION);
    }

    /**
     * Convenience method to check if the right rotation is present and different from the {@linkplain #DEFAULT_ROTATION
     * default rotation}.
     *
     * @return {@code true} if the right rotation is present and different from the {@linkplain #DEFAULT_ROTATION default rotation},
     * {@code false} otherwise.
     */
    public boolean hasRightRotation() {
        return rightRotation != null && !Objects.equals(rightRotation, DEFAULT_ROTATION);
    }

    /**
     * Convenience method to check if the translation is present and different from the {@linkplain #DEFAULT_TRANSLATION
     * default translation}.
     *
     * @return {@code true} if the translation is present and different from the {@linkplain #DEFAULT_TRANSLATION default translation},
     * {@code false} otherwise.
     */
    public boolean hasTranslation() {
        return translation != null && !Objects.equals(translation, DEFAULT_TRANSLATION);
    }

    /**
     * Convenience method to check if the scale is present and different from the {@linkplain #DEFAULT_SCALE default scale}.
     *
     * @return {@code true} if the scale is present and different from the {@linkplain #DEFAULT_SCALE default scale},
     * {@code false} otherwise.
     */
    public boolean hasScale() {
        return scale != null && !Objects.equals(scale, DEFAULT_SCALE);
    }

    /**
     * Constructs a {@link ModelTransform} object with default configuration (rotation/translation -> 0, scale -> 1).
     *
     * @return A {@link ModelTransform} with default configuration.
     */
    public static ModelTransform defaultTransform() {
        return new ModelTransform(DEFAULT_ROTATION, DEFAULT_ROTATION, DEFAULT_TRANSLATION, DEFAULT_SCALE);
    }
}
