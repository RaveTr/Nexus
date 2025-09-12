package com.mememan.nexus.client.model;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Side-safe variant of Minecraft's {@link net.minecraft.client.renderer.block.model.BlockModel.GuiLight}.
 */
public enum ModelGuiLight implements StringRepresentable {
    FRONT,
    SIDE;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
