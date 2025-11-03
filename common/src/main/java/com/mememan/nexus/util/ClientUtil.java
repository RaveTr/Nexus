package com.mememan.nexus.util;

import com.mememan.nexus.client.block.WrappedBlockColor;
import com.mememan.nexus.client.item.WrappedClampedItemPropertyFunction;
import com.mememan.nexus.client.item.WrappedItemColor;
import com.mememan.nexus.client.item.WrappedItemPropertyFunction;
import com.mememan.nexus.loader.EnvironmentSide;
import com.mememan.nexus.platform.NexusServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utility {@code class} that provides side-safe methods to avoid accidental classloading via imports or other means.
 * <br></br>
 * <b>Note</b>: For object conversion methods, we avoid using {@link Optional} to prevent unnecessary object allocation,
 * and the pattern-enforcement of {@link Optional} provides diminishing returns in this context compared to raw
 * nullability.
 */
public class ClientUtil {

    private ClientUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (ClientUtil)");
    }

    /**
     * Shortcut method for checking if the current environment is the physical client.
     *
     * @return Whether the current environment is the physical client.
     */
    public static boolean onClient() {
        return NexusServices.PLATFORM_MANAGER.getEnvironmentSide() == EnvironmentSide.CLIENT;
    }

    /**
     * Side-safe getter for {@link Minecraft#getInstance}.
     *
     * @return The current {@link Minecraft} instance. May be {@code null} if on the server.
     */
    @Nullable
    public static Minecraft getClient() {
        return onClient() ? Minecraft.getInstance() : null;
    }

    /**
     * Side-safe getter for {@link Minecraft#player}.
     *
     * @return The current {@link LocalPlayer} instance. May be {@code null} if on the server.
     */
    @Nullable
    public static Player getClientPlayer() {
        return onClient() ? getClient().player : null;
    }

    /**
     * Side-safe getter for {@link Minecraft#level}.
     *
     * @return The current {@link ClientLevel} instance. May be {@code null} if on the server.
     */
    @Nullable
    public static Level getClientLevel() {
        return onClient() ? getClient().level : null;
    }

    /**
     * Side-safe object conversion method that attempts to convert a {@link WrappedItemPropertyFunction} to a
     * {@link ItemPropertyFunction} only if on the client. May be {@code null}.
     *
     * @param itemModelPredicateFunc The {@link WrappedItemPropertyFunction} to convert.
     *
     * @return The converted {@link ItemPropertyFunction} if on the client. May be {@code null}.
     */
    @Nullable
    public static ItemPropertyFunction toItemPropertyFunction(WrappedItemPropertyFunction itemModelPredicateFunc) {
        return onClient() && itemModelPredicateFunc != null ? itemModelPredicateFunc::getValueForStack : null;
    }

    /**
     * Side-safe object conversion method that attempts to convert a {@link WrappedClampedItemPropertyFunction} to a
     * {@link ClampedItemPropertyFunction} only if on the client. May be {@code null}.
     *
     * @param itemModelPredicateFunc The {@link WrappedClampedItemPropertyFunction} to convert.
     *
     * @return The converted {@link ClampedItemPropertyFunction} if on the client. May be {@code null}.
     */
    @Nullable
    public static ClampedItemPropertyFunction toClampedItemPropertyFunction(WrappedClampedItemPropertyFunction itemModelPredicateFunc) {
        return onClient() && itemModelPredicateFunc != null ? itemModelPredicateFunc::getValueForStack : null;
    }

    /**
     * Side-safe object conversion method that attempts to convert a {@link WrappedBlockColor} to a {@link BlockColor}
     * only if on the client. May be {@code null}.
     *
     * @param targetBlockColor The {@link WrappedBlockColor} to convert.
     *
     * @return The converted {@link BlockColor} if on the client. May be {@code null}.
     */
    @Nullable
    public static BlockColor toBlockColor(WrappedBlockColor targetBlockColor) {
        return onClient() && targetBlockColor != null ? targetBlockColor::getColor : null;
    }

    /**
     * Side-safe object conversion method that attempts to convert a {@link WrappedItemColor} to a {@link ItemColor}
     * only if on the client. May be {@code null}.
     *
     * @param targetItemColor The {@link WrappedItemColor} to convert.
     *
     * @return The converted {@link ItemColor} if on the client. May be {@code null}.
     */
    @Nullable
    public static ItemColor toItemColor(WrappedItemColor targetItemColor) {
        return onClient() && targetItemColor != null ? targetItemColor::getColor : null;
    }

    @Nullable
    public static Material createSignMaterial(WoodType signWoodType) {
        return onClient() && signWoodType != null ? new Material(Sheets.SIGN_SHEET, RegistryUtil.pickPrefix(new ResourceLocation(signWoodType.name()), "entity/signs/")) : null;
    }

    @Nullable
    public static Material createHangingSignMaterial(WoodType hangingSignWoodType) {
        return onClient() && hangingSignWoodType != null ? new Material(Sheets.SIGN_SHEET, RegistryUtil.pickPrefix(new ResourceLocation(hangingSignWoodType.name()), "entity/signs/hanging/")) : null;
    }

    @Nullable
    public static Material createBannerMaterial(ResourceKey<BannerPattern> bannerPatternKey) {
        return onClient() && bannerPatternKey != null ? new Material(Sheets.BANNER_SHEET, RegistryUtil.pickPrefix(BannerPattern.location(bannerPatternKey, true), "entity/banner/")) : null;
    }

    @Nullable
    public static Material createShieldMaterial(ResourceKey<BannerPattern> shieldPatternKey) {
        return onClient() && shieldPatternKey != null ? new Material(Sheets.SHIELD_SHEET, RegistryUtil.pickPrefix(BannerPattern.location(shieldPatternKey, false), "entity/shield/")) : null;
    }

    @Nullable
    public static Material createDecoratedPotMaterial(ResourceKey<String> decoratedPotMaterialName) {
        return onClient() && decoratedPotMaterialName != null ? new Material(Sheets.DECORATED_POT_SHEET, RegistryUtil.pickPrefix(DecoratedPotPatterns.location(decoratedPotMaterialName), "entity/decorated_pot/")) : null;
    }
}
