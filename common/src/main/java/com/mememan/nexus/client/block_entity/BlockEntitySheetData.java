package com.mememan.nexus.client.block_entity;

import com.mememan.nexus.property_wrapper.def.block_entity.BlockEntityTypePropertyWrapperBuilder;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Side-safe data-holding {@code record} whose primary purpose is to hold optional data for block entities to be safely
 * registered later on through the {@link Sheets} {@code class}.
 *
 * @param signWoodType The {@link WoodType} of a given {@link SignBlockEntity}, stitched in the
 *                     {@linkplain Sheets#SIGN_SHEET sign texture atlas}. Texture path is typically set to
 *                     {@code "entity/signs/{signWoodType.name()}"}, as per the specification in
 *                     {@link Sheets#createSignMaterial(WoodType)}. May be {@code null}.
 * @param hangingSignWoodType The {@link WoodType} of a given {@link SignBlockEntity}, stitched in the
 *                            {@linkplain Sheets#SIGN_SHEET sign texture atlas}. Texture path is typically set to
 *                            {@code "entity/signs/hanging/{hangingSignWoodType.name()}"}, as per the specification in
 *                            {@link Sheets#createHangingSignMaterial(WoodType)}. May be {@code null}.
 * @param bannerPatternKey The {@link ResourceKey} representing some registered {@link BannerPattern}, stitched in the
 *                         {@linkplain Sheets#BANNER_SHEET banner texture atlas}. Texture path is typically set to
 *                         {@code "entity/banner/{bannerPatternKey.location().getPath()}"}, as per the specification in
 *                         {@link Sheets#createBannerMaterial(ResourceKey)} (and, by extension,
 *                         {@link BannerPattern#location(ResourceKey, boolean)}). May be {@code null}.
 * @param shieldPatternKey The {@link ResourceKey} representing some registered {@link BannerPattern}, stitched in the
 *                         {@linkplain Sheets#SHIELD_SHEET shield texture atlas}. Texture path is typically set to
 *                         {@code "entity/shield/{shieldPatternKey.location().getPath()}"}, as per the specification in
 *                         {@link Sheets#createShieldMaterial(ResourceKey)} (and, by extension,
 *                         {@link BannerPattern#location(ResourceKey, boolean)}). May be {@code null}.
 * @param decoratedPotMaterialName The {@link ResourceKey} representing some registered
 *                                 {@linkplain String decorated pot material}, stitched in the
 *                                 {@linkplain Sheets#DECORATED_POT_SHEET decorated pot texture atlas}. Texture path is
 *                                 typically set to {@code "entity/decorated_pot/{decoratedPotMaterialName.location().getPath()}"},
 *                                 as per the specification in {@link Sheets#createDecoratedPotMaterial(ResourceKey)}
 *                                 (and, by extension, {@link DecoratedPotPatterns#location(ResourceKey)}). May be {@code null}.
 *
 * @see BlockEntityClientData
 * @see BlockEntityTypePropertyWrapperBuilder#withClientData(Supplier)
 * @see Sheets
 * @see BannerPattern
 * @see DecoratedPotPatterns
 */
public record BlockEntitySheetData(@Nullable WoodType signWoodType, @Nullable WoodType hangingSignWoodType,
                                   @Nullable ResourceKey<BannerPattern> bannerPatternKey, @Nullable ResourceKey<BannerPattern> shieldPatternKey,
                                   @Nullable ResourceKey<String> decoratedPotMaterialName) {
    public static final BlockEntitySheetData EMPTY = new BlockEntitySheetData(null, null, null, null, null);

    /**
     * Creates a new {@link BlockEntitySheetData} instance with the specified {@link WoodType}s for a given sign and
     * hanging sign.
     *
     * @param signWoodType The {@link WoodType} of the sign.
     * @param hangingSignWoodType The {@link WoodType} of the hanging sign.
     *
     * @return A new {@link BlockEntitySheetData} instance with the specified {@link WoodType}s for some given sign and
     * hanging sign.
     */
    public static BlockEntitySheetData forSign(WoodType signWoodType, WoodType hangingSignWoodType) {
        return new BlockEntitySheetData(signWoodType, hangingSignWoodType, null, null, null);
    }

    /**
     * Creates a new {@link BlockEntitySheetData} instance with the specified {@link WoodType} shared for a given sign
     * and hanging sign.
     *
     * @param signWoodType The {@link WoodType} of both the sign and hanging sign.
     *
     * @return A new {@link BlockEntitySheetData} instance with the specified {@link WoodType} for some given sign and
     * hanging sign.
     */
    public static BlockEntitySheetData forSign(WoodType signWoodType) {
        return forSign(signWoodType, signWoodType);
    }

    /**
     * Creates a new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey}s for some given banner
     * and shield patterns.
     *
     * @param bannerPatternKey The {@link ResourceKey} of the registered banner {@link BannerPattern}.
     * @param shieldPatternKey The {@link ResourceKey} of the registered shield {@link BannerPattern}.
     *
     * @return A new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey}s for some given banner
     * and shield patterns.
     */
    public static BlockEntitySheetData forBannerPattern(ResourceKey<BannerPattern> bannerPatternKey, ResourceKey<BannerPattern> shieldPatternKey) {
        return new BlockEntitySheetData(null, null, bannerPatternKey, shieldPatternKey, null);
    }

    /**
     * Creates a new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey} for a given banner
     * pattern.
     *
     * @param bannerPatternKey The {@link ResourceKey} of the registered banner {@link BannerPattern}.
     *
     * @return A new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey} for a given banner
     * pattern.
     */
    public static BlockEntitySheetData forBannerPattern(ResourceKey<BannerPattern> bannerPatternKey) {
        return forBannerPattern(bannerPatternKey, null);
    }

    /**
     * Creates a new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey} for a given shield
     * pattern.
     *
     * @param shieldPatternKey The {@link ResourceKey} of the registered shield {@link BannerPattern}.
     *
     * @return A new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey} for a given shield
     * pattern.
     */
    public static BlockEntitySheetData forShieldBannerPattern(ResourceKey<BannerPattern> shieldPatternKey) {
        return forBannerPattern(null, shieldPatternKey);
    }

    /**
     * Creates a new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey} for a given decorated
     * pot material.
     *
     * @param decoratedPotMaterialName The {@link ResourceKey} of the registered decorated pot material.
     *
     * @return A new {@link BlockEntitySheetData} instance with the specified {@link ResourceKey} for a given decorated
     * pot material.
     */
    public static BlockEntitySheetData forDecoratedPot(ResourceKey<String> decoratedPotMaterialName) {
        return new BlockEntitySheetData(null, null, null, null, decoratedPotMaterialName);
    }
}
