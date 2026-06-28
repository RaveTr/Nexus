package com.mememan.nexus.client.model.general;

import com.google.gson.JsonObject;
import com.mememan.nexus.client.block.WrappedBlockColor;
import com.mememan.nexus.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

/**
 * Data-holder {@code record} representing an element within a model file.
 * <br></br>
 * Elements usually define core visual properties of a model, such as its cuboid's endpoints (from/to) and face UV
 * data.
 * <br></br>
 * Element data contained within a {@code ModelElement} is unclamped for the most part. Thus, while you can input whatever
 * values you want to work with, it's recommended that you follow the conventional constraints defined by Minecraft (see
 * references below).
 *
 * @param from The start point of this element, with values between -16 and 32.
 * @param to The end point of this element, with values between -16 and 32.
 * @param shade Whether shadows should be rendered for the parent model of this element. Defaults to {@code true}.
 * @param rotation The {@linkplain ElementRotationData rotation data} of this element.
 * @param faces The face UV data of this element, used to define texture coordinates for each face of this element, as
 *              well as other data.
 *
 * @see <a href="https://minecraft.wiki/w/Model">Minecraft Wiki: Model Files</a>
 * @see ElementFaceData
 */
public record ModelElement(Vector3f from, Vector3f to, boolean shade, ElementRotationData rotation, Map<Direction, ElementFaceData> faces) {

    /**
     * Side-safe variant of {@link net.minecraft.client.renderer.block.model.BlockElementFace}. Holds defined UV data
     * for each cardinal face of a model element, as well as generic metadata such as texture key/name.
     *
     * @param uv The {@linkplain FaceUVData UV data} of this face.
     * @param formattedTextureKey The texture key of this face, e.g. {@code "#slab"}.
     * @param cullFace The face to cull when touched by (typically) a block from the specified direction.
     * @param tintIndex The tint index of this face, whose behaviour is delegated to a default set of hardcoded tint
     *                  indices. This behaviour is handled by {@link net.minecraft.client.color.block.BlockColor}.
     *                  Defaults to -1.
     *
     * @see ResourceLocationUtil#formatModelUVTexture(String)
     * @see net.minecraft.client.color.block.BlockColor
     * @see WrappedBlockColor#getColor(BlockState, BlockAndTintGetter, BlockPos, int)
     */
    public record ElementFaceData(FaceUVData uv, String formattedTextureKey, Direction cullFace, int tintIndex) {
        public static final int DEFAULT_TINT_INDEX = -1;

        /**
         * Checks whether this face has a tint index besides the default one.
         *
         * @return {@code true} if this face has a tint index besides the default one, {@code false} otherwise.
         *
         * @see ModelElement.ElementFaceData#DEFAULT_TINT_INDEX
         */
        public boolean hasTintIndex() {
            return tintIndex != DEFAULT_TINT_INDEX;
        }
    }

    /**
     * Side-safe variant of {@link net.minecraft.client.renderer.block.model.BlockElementRotation}. Holds defined
     * rotation data for any given {@link ModelElement}.
     *
     * @param origin The origin about which rotations based on provided data are made.
     * @param axis The {@linkplain Direction.Axis rotation axis}.
     * @param angle The angle of the rotation, in degrees, confined to {@link ElementRotationData#VALID_ANGLES}.
     * @param rescale Whether faces should be rescaled across a whole (typically) cuboid/block, based on the formula
     *                {@code 1 / Math.cos(angle)}. Defaults to {@code false}.
     */
    public record ElementRotationData(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
        /**
         * The valid angles for any given element's rotation, as specified by
         * {@link net.minecraft.client.renderer.block.model.BlockElement.Deserializer#getAngle(JsonObject)}.
         */
        public static final float[] VALID_ANGLES = {-45.0F, -22.5F, 0.0F, 22.5F, 45.0F};

        /**
         * Checks if the angle of this rotation data is valid based on {@link ElementRotationData#VALID_ANGLES}.
         *
         * @return {@code true} if the angle is valid, {@code false} otherwise.
         */
        public boolean hasValidAngle() {
            return FloatArrayList.of(VALID_ANGLES).contains(angle());
        }
    }

    /**
     * Data-holder {@code record} storing UV coordinate data for a model element's face in {@code [x1, y1, x2, y2]} format,
     * held in {@link Vector2f} objects.
     *
     * @param from The texture area start bound. Should be between 0 and 16.
     * @param to The texture area end bound. Should be between 0 and 16.
     */
    public record FaceUVData(Vector2f from, Vector2f to) {}
}
