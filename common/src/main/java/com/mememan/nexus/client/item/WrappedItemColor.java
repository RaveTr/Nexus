package com.mememan.nexus.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Side-safe equivalent of {@link net.minecraft.client.color.item.ItemColor} to avoid accidental classloading via
 * imports or other means. Mainly used for determining and registering different dynamic item colors.
 */
public interface WrappedItemColor {

    /**
     * Gets the packed RGB color for the given {@code targetStack}.
     *
     * @param targetStack The target {@link ItemStack} to evaluate the color of.
     * @param tintIndex The computed {@code tintIndex} that represents the current tint of the {@link ItemStack}.
     *
     * @return The packed RGB color of the given {@link ItemStack}.
     *
     * @see net.minecraft.client.renderer.entity.ItemRenderer#renderQuadList(PoseStack, VertexConsumer, List, ItemStack, int, int)
     */
    int getColor(ItemStack targetStack, int tintIndex);
}
