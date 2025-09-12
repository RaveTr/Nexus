package com.mememan.nexus.client.model;

import org.joml.Vector3f;

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
     * Constructs a {@link ModelTransform} object with default configuration (rotation/translation -> 0, scale -> 1).
     *
     * @return A {@link ModelTransform} with default configuration.
     */
    public static ModelTransform defaultTransform() {
        return new ModelTransform(DEFAULT_ROTATION, DEFAULT_ROTATION, DEFAULT_TRANSLATION, DEFAULT_SCALE);
    }
}
