package com.mememan.nexus.util;

import com.google.common.base.Suppliers;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.template.object.block.entity.sign.DefaultableCeilingHangingSignBlock;
import com.mememan.nexus.template.object.block.entity.sign.DefaultableStandingSignBlock;
import com.mememan.nexus.template.object.block.entity.sign.DefaultableWallHangingSignBlock;
import com.mememan.nexus.template.object.block.entity.sign.DefaultableWallSignBlock;
import com.mememan.nexus.template.object.block.misc.StoneBlockGroup;
import com.mememan.nexus.template.object.block.misc.WoodenBlockGroup;
import com.mememan.nexus.template.object.block_entity.sign.DefaultableHangingSignBlockEntity;
import com.mememan.nexus.template.object.block_entity.sign.DefaultableSignBlockEntity;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableBoat;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableChestBoat;
import com.mememan.nexus.template.object.item.entity.boat.BoatType;
import com.mememan.nexus.template.object.item.entity.boat.DefaultableBoatItem;
import com.mememan.nexus.template.property_wrapper.BlockEntityTypePropertyWrapperTemplates;
import com.mememan.nexus.template.property_wrapper.BlockPropertyWrapperTemplates;
import com.mememan.nexus.template.property_wrapper.EntityTypePropertyWrapperTemplates;
import com.mememan.nexus.template.property_wrapper.ItemPropertyWrapperTemplates;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility {@code class} providing helper/shortcut methods to help with game/data-related registry operations and the
 * likes.
 */
public final class RegistryUtil {
    private static final ObjectArrayList<ResourceLocation> CACHED_TEXTURE_LOOKUP = Util.make(new ObjectArrayList<>(), texLookup -> NexusServices.PLATFORM_MANAGER.getModData().stream()
            .flatMap(curModData -> curModData.getAllResourcePaths(".png").stream())
            .filter(curPath -> curPath.contains("assets/")) // RPs only
            .forEach(curPath -> {
                if (curPath.contains("/textures/") && curPath.endsWith(".png")) {
                    String assumedModId = curPath.substring(curPath.indexOf("assets/") + "assets/".length(), curPath.indexOf("/textures/"));
                    String formattedTexturePath = curPath.substring(curPath.indexOf("/textures/") + "/textures/".length(), curPath.indexOf(".png"));

                    texLookup.add(new ResourceLocation(assumedModId, formattedTexturePath));
                }
            }));
    private static final String[] DERIVED_BLOCK_SUFFIXES = new String[] { // Longest-first ordering to avoid partial matches (Stored in a raw array cuz no need for a whole list or map)
            "_hanging_sign",
            "_pressure_plate",
            "_fence_gate",
            "_trapdoor",
            "_button",
            "_stairs",
            "_slab",
            "_fence",
            "_wall",
            "_door",
            "_sign"
    };
    private static final String[] WOOD_COMPONENT_SUFFIXES = new String[] { // Wood-only components; if we strip one of these and the base looks like a wood family name, append _planks.
            "_pressure_plate", "_hanging_sign", "_fence_gate", "_trapdoor", "_stairs", "_button", "_fence", "_door", "_slab", "_sign"
    };
    private static final String[] VANILLA_WOOD_MATERIALS = new String[] {
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped"
    };
    private static final String[] NORMALIZABLE_SUFFIXES = new String[] {
            "_brick", "_plank"
    };
    private static final String[] MATERIAL_SUFFIXES = new String[] {
            "_ingot", "_nugget", "_gem", "_shard", "_dust", "_crystal", "_ore", "_block"
    };
    private static final BiFunction<ResourceLocation, ResourceLocation, ResourceLocation> BRICK_MEMBER_ID_MAPPER = (familyId, memberId) -> memberId.getPath().equals(familyId.getPath())
            ? memberId.withSuffix("_bricks")
            : memberId.withPath(curPath -> familyId.getPath()
            .concat("_brick")
            .concat(curPath.substring(familyId.getPath().length())));
    private static final BiFunction<ResourceLocation, Supplier<Block>, LootTable.Builder> BASE_TO_COBBLED_LOOT_TABLE_BUILDER = (familyId, baseStoneBlockSup) -> BuiltInRegistries.BLOCK.getOptional(familyId.withPrefix("cobbled_"))
            .map(foundCobbledBlock -> (Supplier<Block>) () -> foundCobbledBlock)
            .map(foundCobbledBlock -> LootUtil.dropConditional(baseStoneBlockSup, foundCobbledBlock, LootUtil.HAS_SILK_TOUCH))
            .orElse(LootUtil.dropSelf(baseStoneBlockSup));

    private RegistryUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (RegistryUtil)");
    }

    /**
     * Attempts to retrieve the {@link ResourceLocation} of the texture with the given {@code textureName} from
     * {@link #CACHED_TEXTURE_LOOKUP}.
     *
     * @param textureName The texture's {@link ResourceLocation}. Matched through namespace, where the path must only
     *                    be the name of the texture file to look for (excluding the file extension).
     * @param rawObjectRegistryKey An optional intermediary {@link String} to match for a specific registry directory
     *                             for the target asset. Essentially boils down to an extra check for the presence of
     *                             a path component.
     *
     * @return An {@link Optional} containing the {@link ResourceLocation} of the texture with the given
     * {@code textureName} and {@code rawObjectRegistryKey} (if any). May be empty.
     */
    public static Optional<ResourceLocation> getTextureLocation(ResourceLocation textureName, @Nullable String rawObjectRegistryKey) {
        return textureName == null ? Optional.empty() : CACHED_TEXTURE_LOOKUP.stream()
                .filter(curLoc -> curLoc.getNamespace().equals(textureName.getNamespace()) && (rawObjectRegistryKey == null || curLoc.getPath().contains("/" + rawObjectRegistryKey + "/") || curLoc.getPath().contains(rawObjectRegistryKey + "/")) && curLoc.getPath().substring(curLoc.getPath().lastIndexOf('/') + 1).equals(textureName.getPath()))
                .findFirst();
    }

    /**
     * Overloaded variant of {@link #getTextureLocation(ResourceLocation, String)}. Attempts to find a texture file
     * matching the given {@code textureName} without any intermediary path components.
     *
     * @param textureName The texture's {@link ResourceLocation}. Matched through namespace, where the path must only
     *                    be the name of the texture file to look for (excluding the file extension).
     *
     * @return An {@link Optional} containing the {@link ResourceLocation} of the texture with the given
     * {@code textureName}. May be empty.
     */
    public static Optional<ResourceLocation> getTextureLocation(ResourceLocation textureName) {
        return getTextureLocation(textureName, null);
    }

    /**
     * Overloaded variant of {@link #getTextureLocation(ResourceLocation, String)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture with the given {@code textureName} from {@link #CACHED_TEXTURE_LOOKUP},
     * returning the provided {@code defaultTextureLocation} if no matching texture is found.
     *
     * @param textureName The texture's {@link ResourceLocation}. Matched through namespace, where the path must only
     *                    be the name of the texture file to look for (excluding the file extension).
     * @param defaultTextureLocation The default {@link ResourceLocation} to return if no matching texture is found.
     *
     * @return The {@link ResourceLocation} of the texture with the given {@code textureName}, or the provided
     * {@code defaultTextureLocation} if no match is found.
     *
     * @see #getTextureLocation(ResourceLocation)
     */
    @NotNull
    public static ResourceLocation getTextureLocationOrDefault(ResourceLocation textureName, @NotNull ResourceLocation defaultTextureLocation) {
        return getTextureLocation(textureName).orElseGet(() -> {
            NexusConstants.LOGGER.warn("Attempted to locate non-existent texture '{}', falling back to provided default texture '{}'", textureName, defaultTextureLocation);

            return defaultTextureLocation;
        });
    }

    /**
     * Overloaded variant of {@link #getTextureLocation(ResourceLocation, String)}. Attempts to
     * retrieve the {@link ResourceLocation} of the texture with the given {@code textureName} within any directories
     * containing the provided {@code dirPrefix} from {@link #CACHED_TEXTURE_LOOKUP}, returning the provided
     * {@code defaultTextureLocation} if no matching texture is found.
     *
     * @param textureName The texture's {@link ResourceLocation}. Matched through namespace, where the path must only
     *                    be the name of the texture file to look for (excluding the file extension).
     * @param dirPrefix The directory prefix to look for the texture in (e.g. {@code "item"}, {@code "block/special_dir"}).
     * @param defaultTextureLocation The default {@link ResourceLocation} to return if no matching texture is found.
     *
     * @return The {@link ResourceLocation} of the texture with the given {@code textureName}, or the provided
     * {@code defaultTextureLocation} if no match is found.
     *
     * @see #getTextureLocation(ResourceLocation)
     */
    @NotNull
    public static ResourceLocation getTextureLocationOrDefault(ResourceLocation textureName, String dirPrefix, @NotNull ResourceLocation defaultTextureLocation) {
        return getTextureLocation(textureName, dirPrefix).orElseGet(() -> {
            NexusConstants.LOGGER.warn("Attempted to locate non-existent texture '{}' (under directory containing '{}'), falling back to provided default texture '{}'", textureName, dirPrefix, defaultTextureLocation);

            return defaultTextureLocation;
        });
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(ResourceLocation, ResourceLocation)}. Attempts to
     * retrieve the {@link ResourceLocation} of the texture with the given {@code textureName} within any directories
     * containing the provided {@code dirPrefix} from {@link #CACHED_TEXTURE_LOOKUP}, returning the {@code textureName}
     * itself if no matching texture is found.
     *
     * @param textureName The texture's {@link ResourceLocation}. Matched through namespace, where the path must only
     *                    be the name of the texture file to look for (excluding the file extension).
     *
     * @return The {@link ResourceLocation} of the texture with the given {@code textureName}, or the
     * {@code textureName} itself if no match is found.
     *
     * @see #getTextureLocationOrDefault(ResourceLocation, ResourceLocation)
     */
    @NotNull
    public static ResourceLocation getTextureLocationOrDefault(ResourceLocation textureName) {
        return getTextureLocationOrDefault(textureName, textureName);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(ResourceLocation, String, ResourceLocation)}.
     * Attempts to retrieve the {@link ResourceLocation} of the texture with the given {@code textureName} within any
     * directories containing the provided {@code dirPrefix} from {@link #CACHED_TEXTURE_LOOKUP}, returning the
     * {@code textureName} itself if no matching texture is found.
     *
     * @param textureName The texture's {@link ResourceLocation}. Matched through namespace, where the path must only
     *                    be the name of the texture file to look for (excluding the file extension).
     * @param dirPrefix The directory prefix to look for the texture in (e.g. {@code "item"}, {@code "block/special_dir"}).
     *
     * @return The {@link ResourceLocation} of the texture with the given {@code textureName}, or the
     * {@code textureName} itself if no match is found.
     *
     * @see #getTextureLocationOrDefault(ResourceLocation, String, ResourceLocation)
     */
    @NotNull
    public static ResourceLocation getTextureLocationOrDefault(ResourceLocation textureName, String dirPrefix) {
        return getTextureLocationOrDefault(textureName, dirPrefix, textureName);
    }

    /**
     * Overloaded variant of {@link #getTextureLocation(ResourceLocation)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture for the given {@code targetObj} from {@link #CACHED_TEXTURE_LOOKUP}.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     *
     * @param <T> The type of the target object.
     *
     * @return An {@link Optional} containing the {@link ResourceLocation} of the texture for the given
     * {@code targetObj}. May be empty.
     *
     * @see #getTextureLocation(ResourceLocation)
     */
    public static <T> Optional<ResourceLocation> getTextureLocation(Supplier<T> targetObj) {
        return getTextureLocation(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()));
    }

    /**
     * Overloaded variant of {@link #getTextureLocation(ResourceLocation, String)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture for the given {@code targetObj} within any directories containing the
     * provided {@code dirPrefix} from {@link #CACHED_TEXTURE_LOOKUP}.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     * @param dirPrefix The directory prefix to look for the texture in (e.g. {@code "item"}, {@code "block/special_dir"}).
     *
     * @param <T> The type of the target object.
     *
     * @return An {@link Optional} containing the {@link ResourceLocation} of the texture for the given
     * {@code targetObj}. May be empty.
     *
     * @see #getTextureLocation(Supplier)
     */
    public static <T> Optional<ResourceLocation> getTextureLocation(Supplier<T> targetObj, String dirPrefix) {
        return getTextureLocation(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()), dirPrefix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocation(Supplier)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture for the given {@code targetObj} from {@link #CACHED_TEXTURE_LOOKUP},
     * returning the provided {@code defaultTextureLocation} if no matching texture is found.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     * @param defaultTextureLocation The default {@link ResourceLocation} to return if no matching texture is found.
     *
     * @param <T> The type of the target object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}, or the provided
     * {@code defaultTextureLocation} if no match is found.
     *
     * @see #getTextureLocation(Supplier)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefault(Supplier<T> targetObj, @NotNull ResourceLocation defaultTextureLocation) {
        return getTextureLocation(targetObj).orElseGet(() -> {
            NexusConstants.LOGGER.warn("Attempted to locate non-existent texture for {} '{}', falling back to provided default texture '{}'", targetObj.get().getClass().getSimpleName(), DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()), defaultTextureLocation);

            return defaultTextureLocation;
        });
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(Supplier, String)}. Attempts to
     * retrieve the {@link ResourceLocation} of the texture for the given {@code targetObj} within any directories
     * containing the provided {@code dirPrefix} from {@link #CACHED_TEXTURE_LOOKUP}, returning the provided
     * {@code defaultTextureLocation} if no matching texture is found.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     * @param dirPrefix The directory prefix to look for the texture in (e.g. {@code "item"}, {@code "block/special_dir"}).
     * @param defaultTextureLocation The default {@link ResourceLocation} to return if no matching texture is found.
     *
     * @param <T> The type of the target object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}, or the provided
     * {@code defaultTextureLocation} if no match is found.
     *
     * @see #getTextureLocationOrDefault(Supplier, String)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefault(Supplier<T> targetObj, String dirPrefix, @NotNull ResourceLocation defaultTextureLocation) {
        return getTextureLocation(targetObj, dirPrefix).orElseGet(() -> {
            NexusConstants.LOGGER.warn("Attempted to locate non-existent texture for {} '{}' (under directory containing '{}'), falling back to provided default texture '{}'", targetObj.get().getClass().getSimpleName(), DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()), dirPrefix, defaultTextureLocation);

            return defaultTextureLocation;
        });
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(Supplier, ResourceLocation)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture for the given {@code targetObj} from {@link #CACHED_TEXTURE_LOOKUP},
     * returning a default {@link ResourceLocation} with the namespace "invalid" if no matching texture is found.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     *
     * @param <T> The type of the target object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}, or a default
     * {@link ResourceLocation} with the path "invalid" if no match is found.
     *
     * @see #getTextureLocationOrDefault(Supplier, ResourceLocation)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefault(Supplier<T> targetObj) {
        return getTextureLocationOrDefault(targetObj, new ResourceLocation("invalid"));
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(Supplier, String, ResourceLocation)}. Attempts to
     * retrieve the {@link ResourceLocation} of the texture for the given {@code targetObj} within any directories
     * containing the provided {@code dirPrefix} from {@link #CACHED_TEXTURE_LOOKUP}, returning the provided
     * {@code defaultTextureLocation} if no matching texture is found.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     * @param dirPrefix The directory prefix to look for the texture in (e.g. {@code "item"}, {@code "block/special_dir"}).
     *
     * @param <T> The type of the target object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}, or the provided
     * {@code defaultTextureLocation} if no match is found.
     *
     * @see #getTextureLocationOrDefault(Supplier, String, ResourceLocation)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefault(Supplier<T> targetObj, String dirPrefix) {
        return getTextureLocationOrDefault(targetObj, dirPrefix, new ResourceLocation("invalid"));
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(Supplier)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture for the given {@code targetObj} from {@link #CACHED_TEXTURE_LOOKUP}
     * and prepends the provided {@code prefix} to the resulting path.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     * @param prefix The prefix to prepend to the texture path.
     *
     * @param <T> The type of the target object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj} with the provided
     * {@code prefix} prepended to its path.
     *
     * @see #getTextureLocationOrDefault(Supplier)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefaultWithPrefix(Supplier<T> targetObj, String prefix) {
        return pickPrefix(getTextureLocationOrDefault(targetObj), prefix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(Supplier)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture for the given {@code targetObj} from {@link #CACHED_TEXTURE_LOOKUP}
     * and appends the provided {@code suffix} to the resulting path.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     * @param suffix The suffix to append to the texture path.
     *
     * @param <T> The type of the target object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj} with the provided
     * {@code suffix} appended to its path.
     *
     * @see #getTextureLocationOrDefault(Supplier)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefaultWithSuffix(Supplier<T> targetObj, String suffix) {
        return pickSuffix(getTextureLocationOrDefault(targetObj), suffix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(ResourceLocation)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture based on the given {@code baseLoc} from {@link #CACHED_TEXTURE_LOOKUP}
     * and prepends the provided {@code prefix} to the resulting path.
     *
     * @param baseLoc The base texture location to use for resolution.
     * @param prefix The prefix to prepend to the texture path.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj} with the provided
     * {@code prefix} prepended to its path.
     *
     * @see #getTextureLocationOrDefault(ResourceLocation)
     */
    @NotNull
    public static ResourceLocation getTextureLocationOrDefaultWithPrefix(ResourceLocation baseLoc, String prefix) {
        return pickPrefix(getTextureLocationOrDefault(baseLoc), prefix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(ResourceLocation)}. Attempts to retrieve the
     * {@link ResourceLocation} of the texture based on the given {@code baseLoc} from {@link #CACHED_TEXTURE_LOOKUP}
     * and appends the provided {@code suffix} to the resulting path.
     *
     * @param baseLoc The base texture location to use for resolution.
     * @param suffix The suffix to append to the texture path.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj} with the provided
     * {@code suffix} appended to its path.
     *
     * @see #getTextureLocationOrDefault(Supplier)
     */
    @NotNull
    public static ResourceLocation getTextureLocationOrDefaultWithSuffix(ResourceLocation baseLoc, String suffix) {
        return pickSuffix(getTextureLocationOrDefault(baseLoc), suffix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationWithPrefixOrDefault(ResourceLocation, String)}. Attempts to retrieve
     * the path of the texture based on the given {@code targetObj} with {@code prefix} prepended
     * from {@link #CACHED_TEXTURE_LOOKUP}.
     *
     * @param targetObj The object whose id should be used for resolution.
     * @param prefix The prefix to prepend to the texture being looked up.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}.
     *
     * @see #getTextureLocationWithPrefixOrDefault(Supplier, String, String)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationWithPrefixOrDefault(Supplier<T> targetObj, String prefix) {
        return getTextureLocationWithPrefixOrDefault(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()), prefix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationWithPrefixOrDefault(ResourceLocation, String, String)}. Attempts
     * to retrieve the path of the texture based on the given {@code targetObj} with {@code prefix} prepended from
     * {@link #CACHED_TEXTURE_LOOKUP} within any directories containing the provided {@code dirPrefix}.
     *
     * @param targetObj The object whose id should be used for resolution.
     * @param prefix The prefix to prepend to the texture being looked up.
     * @param dirPrefix The directory prefix to validate any texture file path matches against (e.g. {@code "item"},
     * {@code "block"}).
     *
     * @param <T> The type of the object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}.
     *
     * @see #getTextureLocationWithPrefixOrDefault(Supplier, String)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationWithPrefixOrDefault(Supplier<T> targetObj, String prefix, String dirPrefix) {
        return getTextureLocationWithPrefixOrDefault(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()), prefix, dirPrefix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationWithSuffixOrDefault(ResourceLocation, String)}. Attempts to retrieve
     * the path of the texture based on the given {@code targetObj} with {@code suffix} appended
     * from {@link #CACHED_TEXTURE_LOOKUP}.
     *
     * @param targetObj The object whose id should be used for resolution.
     * @param suffix The suffix to append to the texture being looked up.
     *
     * @param <T> The type of the object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}.
     *
     * @see #getTextureLocationWithSuffixOrDefault(Supplier, String, String)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationWithSuffixOrDefault(Supplier<T> targetObj, String suffix) {
        return getTextureLocationWithSuffixOrDefault(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()), suffix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationWithSuffixOrDefault(ResourceLocation, String, String)}. Attempts to retrieve
     * the {@link ResourceLocation} of the texture for the given {@code targetObj} with {@code suffix} appended
     * from {@link #CACHED_TEXTURE_LOOKUP} within any directories containing the provided {@code dirPrefix}.
     *
     * @param targetObj The {@link Supplier} of the target object to find the texture for.
     * @param suffix The suffix to append to the texture path.
     * @param dirPrefix The directory prefix to validate any texture file path matches against (e.g. {@code "item"},
     *                  {@code "block"}).
     *
     * @param <T> The type of the target object.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}, or the provided
     * {@code defaultTextureLocation} if no match is found.
     *
     * @see #getTextureLocationWithSuffixOrDefault(Supplier, String)
     */
    @NotNull
    public static <T> ResourceLocation getTextureLocationWithSuffixOrDefault(Supplier<T> targetObj, String suffix, String dirPrefix) {
        return getTextureLocationWithSuffixOrDefault(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetObj.get()), suffix, dirPrefix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(ResourceLocation)}. Attempts to retrieve
     * the {@link ResourceLocation} of the texture based on the given {@code baseLoc} with {@code prefix} prepended
     * from {@link #CACHED_TEXTURE_LOOKUP}.
     *
     * @param baseLoc The base texture location to use for resolution.
     * @param prefix The prefix to prepend to the texture being looked up.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}.
     *
     * @see #getTextureLocationOrDefault(ResourceLocation)
     * @see #getTextureLocationWithPrefixOrDefault(ResourceLocation, String, String)
     */
    @NotNull
    public static ResourceLocation getTextureLocationWithPrefixOrDefault(ResourceLocation baseLoc, String prefix) {
        return getTextureLocationOrDefault(pickPrefix(baseLoc, prefix));
    }

    /**
     * Overloaded variant of {@link #getTextureLocationWithPrefixOrDefault(ResourceLocation, String)}. Attempts to retrieve
     * the {@link ResourceLocation} of the texture based on the given {@code baseLoc} with {@code prefix} prepended
     * from {@link #CACHED_TEXTURE_LOOKUP} within any directories containing the provided {@code dirPrefix}.
     *
     * @param baseLoc The base texture location to use for resolution.
     * @param prefix The prefix to prepend to the texture being looked up.
     * @param dirPrefix The directory prefix to validate any texture file path matches against (e.g. {@code "item"},
     *                  {@code "block"}).
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}.
     *
     * @see #getTextureLocationWithPrefixOrDefault(ResourceLocation, String)
     */
    @NotNull
    public static ResourceLocation getTextureLocationWithPrefixOrDefault(ResourceLocation baseLoc, String prefix, String dirPrefix) {
        return getTextureLocationOrDefault(pickPrefix(baseLoc, prefix), dirPrefix);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(ResourceLocation)}. Attempts to retrieve
     * the {@link ResourceLocation} of the texture based on the given {@code baseLoc} with {@code suffix} appended
     * from {@link #CACHED_TEXTURE_LOOKUP}.
     *
     * @param baseLoc The base texture location to use for resolution.
     * @param suffix The suffix to append to the texture being looked up.
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}.
     *
     * @see #getTextureLocationOrDefault(ResourceLocation)
     * @see #getTextureLocationWithSuffixOrDefault(ResourceLocation, String, String)
     */
    @NotNull
    public static ResourceLocation getTextureLocationWithSuffixOrDefault(ResourceLocation baseLoc, String suffix) {
        return getTextureLocationOrDefault(pickSuffix(baseLoc, suffix));
    }

    /**
     * Overloaded variant of {@link #getTextureLocationWithSuffixOrDefault(ResourceLocation, String)}. Attempts to retrieve
     * the {@link ResourceLocation} of the texture based on the given {@code baseLoc} with {@code suffix} appended
     * from {@link #CACHED_TEXTURE_LOOKUP}, within any directories containing the provided {@code dirPrefix}.
     *
     * @param baseLoc The base texture location to use for resolution.
     * @param suffix The suffix to append to the texture path.
     * @param dirPrefix The directory prefix to validate any texture file path matches against (e.g. {@code "item"},
     *                  {@code "block"}).
     *
     * @return The {@link ResourceLocation} of the texture for the given {@code targetObj}, or the provided
     * {@code defaultTextureLocation} if no match is found.
     *
     * @see #getTextureLocationWithSuffixOrDefault(ResourceLocation, String)
     */
    @NotNull
    public static ResourceLocation getTextureLocationWithSuffixOrDefault(ResourceLocation baseLoc, String suffix, String dirPrefix) {
        return getTextureLocationOrDefault(pickSuffix(baseLoc, suffix), dirPrefix);
    }

    /**
     * Modifies the {@link ResourceLocation} passed in by prepending the provided {@code prefix} to its path if it isn't
     * already... prefixed with said {@code prefix} (duh).
     *
     * @param baseLoc The {@link ResourceLocation} to pick the provided {@code prefix} for.
     * @param prefix The path prefix to search for/prepend the provided {@code baseLoc} with.
     *
     * @return A modified variant of the provided {@code baseLoc} with the provided {@code prefix} picked/appropriately
     * and safely prepended.
     *
     * @see #pickBlockPrefix(ResourceLocation)
     * @see #pickItemPrefix(ResourceLocation)
     */
    public static ResourceLocation pickPrefix(ResourceLocation baseLoc, String prefix) {
        return baseLoc.getPath().startsWith(prefix) ? baseLoc : baseLoc.withPrefix(prefix);
    }

    /**
     * Modifies the {@link ResourceLocation} passed in by appending the provided {@code suffix} to its path if it isn't
     * already... suffixed with said {@code suffix} (duh).
     *
     * @param baseLoc The {@link ResourceLocation} to pick the provided {@code suffix} for.
     * @param suffix The path suffix to search for/append the provided {@code baseLoc} with.
     *
     * @return A modified variant of the provided {@code baseLoc} with the provided {@code suffix} picked/appropriately
     * and safely appended.
     */
    public static ResourceLocation pickSuffix(ResourceLocation baseLoc, String suffix) {
        return baseLoc.getPath().endsWith(suffix) ? baseLoc : baseLoc.withSuffix(suffix);
    }

    /**
     * Overloaded variant of {@link #pickPrefix(ResourceLocation, String)}. Modifies the {@link ResourceLocation} passed
     * in by prepending the {@code "block/"} prefix to its path if it isn't already prefixed with said prefix (duh).
     *
     * @param baseBlockLoc The {@link ResourceLocation} to pick the {@code "block/"} prefix for.
     *
     * @return A modified variant of the provided {@code baseBlockLoc} with the {@code "block/"} prefix
     * picked/appropriately and safely prepended.
     *
     * @see #pickPrefix(ResourceLocation, String)
     * @see #pickItemPrefix(ResourceLocation)
     */
    public static ResourceLocation pickBlockPrefix(ResourceLocation baseBlockLoc) {
        return pickPrefix(baseBlockLoc, "block/");
    }

    /**
     * Overloaded variant of {@link #pickPrefix(ResourceLocation, String)}. Modifies the {@link ResourceLocation} passed
     * in by prepending the {@code "item/"} prefix to its path if it isn't already prefixed
     * with said prefix (duh).
     *
     * @param baseItemLoc The {@link ResourceLocation} to pick the {@code "item/"} prefix for.
     *
     * @return A modified variant of the provided {@code baseItemLoc} with the {@code "item/"} prefix
     * picked/appropriately and safely prepended.
     *
     * @see #pickPrefix(ResourceLocation, String)
     * @see #pickBlockPrefix(ResourceLocation)
     */
    public static ResourceLocation pickItemPrefix(ResourceLocation baseItemLoc) {
        return pickPrefix(baseItemLoc, "item/");
    }

    /**
     * Returns a {@link Function} that modifies a {@link ResourceLocation} by prepending the provided {@code prefix}
     * to its path if it isn't already prefixed with said prefix.
     *
     * @param prefix The path prefix to search for/prepend.
     *
     * @return A {@link Function} that prepends the provided {@code prefix} to a {@link ResourceLocation}'s path.
     *
     * @see #pickPrefix(ResourceLocation, String)
     */
    public static Function<ResourceLocation, ResourceLocation> pickPrefix(String prefix) {
        return baseLoc -> pickPrefix(baseLoc, prefix);
    }

    /**
     * Returns a {@link Function} that modifies a {@link ResourceLocation} by appending the provided {@code suffix}
     * to its path if it isn't already suffixed with said suffix.
     *
     * @param suffix The path suffix to search for/append.
     *
     * @return A {@link Function} that appends the provided {@code suffix} to a {@link ResourceLocation}'s path.
     *
     * @see #pickSuffix(ResourceLocation, String)
     */
    public static Function<ResourceLocation, ResourceLocation> pickSuffix(String suffix) {
        return baseLoc -> pickSuffix(baseLoc, suffix);
    }

    /**
     * Returns a {@link Function} that modifies a {@link ResourceLocation} by prepending the provided {@code prefix}
     * and appending the provided {@code suffix} to its path if they aren't already present.
     *
     * @param prefix The path prefix to search for/prepend.
     * @param suffix The path suffix to search for/append.
     *
     * @return A {@link Function} that prepends the provided {@code prefix} and appends the provided {@code suffix}
     * to a {@link ResourceLocation}'s path.
     */
    public static Function<ResourceLocation, ResourceLocation> pickPrefixAndSuffix(String prefix, String suffix) {
        return baseLoc -> baseLoc.getPath().startsWith(prefix) && baseLoc.getPath().endsWith(suffix) ? baseLoc : baseLoc.withPrefix(prefix).withSuffix(suffix);
    }

    /**
     * Returns a {@link Function} that replaces the prefix of a {@link ResourceLocation}'s path with the provided
     * {@code replacedPrefix} if the path contains an underscore and doesn't already start with the prefix.
     *
     * @param replacedPrefix The prefix to replace the existing prefix with.
     *
     * @return A {@link Function} that replaces the prefix of a {@link ResourceLocation}'s path.
     */
    public static Function<ResourceLocation, ResourceLocation> replacePrefix(String replacedPrefix) {
        return baseLoc -> {
            String baseLocPath = baseLoc.getPath();

            return baseLocPath.startsWith(replacedPrefix) || !baseLocPath.contains("_")
                    ? baseLoc
                    : baseLoc.withPath(baseLocPath.replace(StringUtils.substringBefore(baseLocPath, baseLoc.getPath().indexOf('_')), replacedPrefix));
        };
    }

    /**
     * Returns a {@link Function} that replaces the suffix of a {@link ResourceLocation}'s path with the provided
     * {@code replacedSuffix} if the path contains an underscore and doesn't already end with the suffix.
     *
     * @param replacedSuffix The suffix to replace the existing suffix with.
     *
     * @return A {@link Function} that replaces the suffix of a {@link ResourceLocation}'s path.
     */
    public static Function<ResourceLocation, ResourceLocation> replaceSuffix(String replacedSuffix) {
        return baseLoc -> {
            String baseLocPath = baseLoc.getPath();

            return baseLocPath.endsWith(replacedSuffix) || !baseLocPath.contains("_")
                    ? baseLoc
                    : baseLoc.withPath(baseLocPath.replace(baseLocPath.substring(baseLoc.getPath().lastIndexOf('_') + 1), replacedSuffix));
        };
    }

    /**
     * Attempts to retrieve a {@link Supplier} for an object from the registry using the provided base object and
     * mapping function. Optionally throws an exception if the target object is not found.
     *
     * @param baseObjSup The {@link Supplier} of the base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     * @param throwIfMissing Whether to throw an exception if the target object is not found.
     *
     * @param <T> The type of the objects.
     *
     * @return An {@link Optional} containing a {@link Supplier} for the target object, or empty if not found and
     * {@code throwIfMissing} is false.
     *
     * @throws IllegalArgumentException If {@code throwIfMissing} is true and the target object is not found.
     *
     * @apiNote This method searches within the same registry that the base object is registered in (determined based
     * on its type, see {@link DataGenPropertyWrapper.RegistryLookupContainer#getRegistryForObject(Object)}).
     *
     * @see #getObjectFrom(Supplier, Function, boolean)
     */
    public static <T> Optional<Supplier<T>> getSuppliedObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper, boolean throwIfMissing) {
        T baseObj = baseObjSup.get();
        String targetObjClassName = baseObj.getClass().getSimpleName();
        Registry<T> baseObjRegistry = DataGenPropertyWrapper.RegistryLookupContainer.getRegistryForObject(baseObj)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to find registry for unregistered or unmapped object of type %s: %s", targetObjClassName, baseObj)));
        ResourceLocation baseObjLoc = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(baseObj);
        ResourceLocation targetObjLoc = targetObjIdMapper.apply(baseObjLoc);
        Supplier<T> targetObj = () -> baseObjRegistry instanceof DefaultedRegistry<?> defReg && Objects.equals(defReg.get(targetObjLoc), defReg.get(defReg.getDefaultKey()))
                ? null
                : baseObjRegistry.get(targetObjLoc);

        if (throwIfMissing && targetObj.get() == null) throw new IllegalArgumentException(String.format("Attempted to compute invalid object '%s' from %s '%s'", targetObjLoc, targetObjClassName, baseObjLoc));

        return Optional.ofNullable(targetObj);
    }

    /**
     * Overloaded variant of {@link #getSuppliedObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve an object
     * from the registry using the provided base object and mapping function. Optionally throws an exception if the target
     * object is not found.
     *
     * @param baseObjSup The {@link Supplier} of the base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     * @param throwIfMissing Whether to throw an exception if the target object is not found.
     *
     * @param <T> The type of the objects.
     *
     * @return An {@link Optional} containing the target object, or empty if not found and {@code throwIfMissing} is false.
     *
     * @throws IllegalArgumentException If {@code throwIfMissing} is true and the target object is not found.
     *
     * @see #getSuppliedObjectFrom(Supplier, Function, boolean)
     */
    public static <T> Optional<T> getObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper, boolean throwIfMissing) {
        return getSuppliedObjectFrom(baseObjSup, targetObjIdMapper, throwIfMissing).map(Supplier::get);
    }

    /**
     * Overloaded variant of {@link #getObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve an object from
     * the registry using the provided base object and mapping function. Optionally throws an exception if the target
     * object is not found.
     *
     * @param baseObj The base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     * @param throwIfMissing Whether to throw an exception if the target object is not found.
     *
     * @param <T> The type of the objects.
     *
     * @return An {@link Optional} containing the target object, or empty if not found and {@code throwIfMissing} is false.
     *
     * @throws IllegalArgumentException If {@code throwIfMissing} is true and the target object is not found.
     *
     * @see #getObjectFrom(Supplier, Function, boolean)
     */
    public static <T> Optional<T> getObjectFrom(T baseObj, Function<ResourceLocation, ResourceLocation> targetObjIdMapper, boolean throwIfMissing) {
        return getObjectFrom(() -> baseObj, targetObjIdMapper, throwIfMissing);
    }

    /**
     * Overloaded variant of {@link #getSuppliedObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve a
     * {@link Supplier} for an object from the registry using the provided base object and mapping function.
     * Never throws an exception if the target object is not found.
     *
     * @param baseObjSup The {@link Supplier} of the base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     *
     * @param <T> The type of the objects.
     *
     * @return An {@link Optional} containing a {@link Supplier} for the target object, or empty if not found.
     *
     * @see #getSuppliedObjectFrom(Supplier, Function, boolean)
     */
    public static <T> Optional<Supplier<T>> getSuppliedObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getSuppliedObjectFrom(baseObjSup, targetObjIdMapper, false);
    }

    /**
     * Overloaded variant of {@link #getObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve an object from
     * the registry using the provided base object and mapping function. Never throws an exception if the target
     * object is not found.
     *
     * @param baseObjSup The {@link Supplier} of the base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     *
     * @param <T> The type of the objects.
     *
     * @return An {@link Optional} containing the target object, or empty if not found.
     *
     * @see #getObjectFrom(Supplier, Function, boolean)
     */
    public static <T> Optional<T> getObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObjSup, targetObjIdMapper, false);
    }

    /**
     * Overloaded variant of {@link #getObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve an object from
     * the registry using the provided base object and mapping function. Never throws an exception if the target
     * object is not found.
     *
     * @param baseObj The base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     *
     * @param <T> The type of the objects.
     *
     * @return An {@link Optional} containing the target object, or empty if not found.
     *
     * @see #getObjectFrom(Supplier, Function, boolean)
     */
    public static <T> Optional<T> getObjectFrom(T baseObj, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObj, targetObjIdMapper, false);
    }

    /**
     * Overloaded variant of {@link #getSuppliedObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve a
     * {@link Supplier} for an object from the registry using the provided base object and mapping function.
     * Always throws an exception if the target object is not found.
     *
     * @param baseObjSup The {@link Supplier} of the base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     *
     * @param <T> The type of the objects.
     *
     * @return A {@link Supplier} for the target object.
     *
     * @throws IllegalArgumentException If the target object is not found.
     *
     * @see #getSuppliedObjectFrom(Supplier, Function, boolean)
     */
    public static <T> Supplier<T> getSuppliedObjectFromOrThrow(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getSuppliedObjectFrom(baseObjSup, targetObjIdMapper, true).get();
    }

    /**
     * Overloaded variant of {@link #getObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve an object from
     * the registry using the provided base object and mapping function. Always throws an exception if the target
     * object is not found.
     *
     * @param baseObjSup The {@link Supplier} of the base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     *
     * @param <T> The type of the objects.
     *
     * @return The target object.
     *
     * @throws IllegalArgumentException If the target object is not found.
     *
     * @see #getObjectFrom(Supplier, Function, boolean)
     */
    public static <T> T getObjectFromOrThrow(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObjSup, targetObjIdMapper, true).get();
    }

    /**
     * Overloaded variant of {@link #getObjectFrom(Supplier, Function, boolean)}. Attempts to retrieve an object from
     * the registry using the provided base object and mapping function. Always throws an exception if the target
     * object is not found.
     *
     * @param baseObj The base object to map from.
     * @param targetObjIdMapper The {@link Function} to map the base object's registry ID to the target object's registry ID.
     *
     * @param <T> The type of the objects.
     *
     * @return The target object.
     *
     * @throws IllegalArgumentException If the target object is not found.
     *
     * @see #getObjectFrom(Supplier, Function, boolean)
     */
    public static <T> T getObjectFromOrThrow(T baseObj, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObj, targetObjIdMapper, true).get();
    }

    /**
     * Modifies the registry path of the target block using the provided path mapping function. Allows for custom
     * transformation of block registry paths based on specific naming conventions or requirements.
     *
     * @param targetItemLike The {@link Supplier} of the target {@link IL} to modify the registry path for.
     * @param pathIdMapper The {@link Function} to apply to the block's registry path for transformation.
     *
     * @return A new {@link ResourceLocation} with the modified registry path.
     *
     * @param <IL> Any {@link ItemLike} type.
     *
     * @throws IllegalArgumentException If no registry entry is present for the target block.
     *
     * @see #pickBlockId(Supplier)
     */
    public static <IL extends ItemLike> ResourceLocation pickItemLikeId(Supplier<IL> targetItemLike, Function<String, String> pathIdMapper) {
        ResourceLocation baseILId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetItemLike.get());
        String baseILPath = baseILId.getPath();
        String chosenILId = pathIdMapper.apply(baseILPath);

        return baseILId.withPath(chosenILId);
    }

    /**
     * Derives a base block registry path from the given block's registry ID. This infers the "original" block name
     * from common derived variants (e.g., slabs, stairs, walls, fences, buttons, pressure plates, doors, trapdoors,
     * signs), applying standard normalization and family heuristics.
     * <p>
     *     <h3>Derivation Rules</h3>
     *     <ul>
     *         <li>Strips a known derived suffix (longest-first): {@code _hanging_sign}, {@code _pressure_plate},
     *         {@code _fence_gate}, {@code _trapdoor}, {@code _button}, {@code _stairs}, {@code _slab},
     *         {@code _fence}, {@code _wall}, {@code _door}, {@code _sign}.</li>
     *         <li>Normalizes singular family terms to plurals: {@code _brick} → {@code _bricks},
     *         {@code _plank} → {@code _planks} (applies to trailing positions).</li>
     *         <li>Wood-family heuristic: for wood-only components, if the base looks like a vanilla wood key
     *         (e.g., {@code oak}, {@code spruce}, {@code bamboo}, {@code crimson}, {@code warped}) or the block itself
     *         seems to be a wood-family block via tag checks, append {@code _planks} unless already present.</li>
     *         <li>Appends {@code _block} when appropriate: if the source contained {@code _block} anywhere, or a
     *         derived suffix was stripped and the resulting base is not a plural family ({@code _bricks}/{@code _planks})
     *         AND the provided {@code targetBlock} does not exist based on the current path.</li>
     *     </ul>
     *
     * @param targetBlock The {@link Supplier} of the target {@link Block} to derive a base block ID for.
     *
     * @return A new {@link ResourceLocation} whose path is the inferred base block ID.
     *
     * @throws IllegalArgumentException If no registry entry is present for the target block.
     *
     * @see #pickItemLikeId(Supplier, Function)
     */
    public static ResourceLocation pickBlockId(Supplier<Block> targetBlock) {
        return pickItemLikeId(targetBlock, baseBlockPath -> {
            String work = baseBlockPath;
            String removedSuffix = null;

            final boolean sourceContainedBlockToken = baseBlockPath.contains("_block");

            for (String suffix : DERIVED_BLOCK_SUFFIXES) { // Strip a known derived suffix if present (e.g., _slab, _stairs, _pressure_plate, ...)
                if (work.endsWith(suffix)) {
                    work = work.substring(0, work.length() - suffix.length());
                    removedSuffix = suffix;
                    break;
                }
            }

            // Normalize brick/plank singulars to plurals in trailing positions.
            for (String suffix : NORMALIZABLE_SUFFIXES) {
                if (work.endsWith(suffix)) {
                    work = work.substring(0, work.length() - suffix.length()).concat(StringUtil.pluralize(suffix));
                    break;
                }
            }

            boolean woodFamilyBaseDetected = work.endsWith("_planks");

            if (removedSuffix != null && StringUtil.containsSuffix(WOOD_COMPONENT_SUFFIXES, removedSuffix)) { // Wood-family heuristic: if we stripped a wood-only component and the base looks like a wood key, append _planks.
                String lastToken = StringUtil.lastToken(work);
                ResourceLocation targetBlockId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());

                if (StringUtil.containsSuffix(VANILLA_WOOD_MATERIALS, work) || StringUtil.containsSuffix(VANILLA_WOOD_MATERIALS, lastToken) || BuiltInRegistries.BLOCK.getOptional(targetBlockId.withPath(work.concat("_planks"))).isPresent() || targetBlock.get().builtInRegistryHolder().tags().anyMatch(curTag -> curTag.location().getPath().contains("wooden"))) {
                    if (!work.endsWith("_planks") && !work.contains("plank")) work = work.concat("_planks");

                    woodFamilyBaseDetected = true;
                }
            }

            boolean endsWithFamilyBase = work.endsWith("_bricks") || woodFamilyBaseDetected; // Prefer <base>_block unless it's plural or a wood family base (always keep "_block" if present)

            if (!endsWithFamilyBase) {
                /*
                 * Append "_block" when either:
                 * - Source name contains "_block" anywhere
                 * - We stripped a derived suffix and the base is not a wood family (e.g. <wood>_planks)
                 * AND
                 * - No block exists for the target ID after its suffix has been stripped
                 */
                boolean expectBlockBase = BuiltInRegistries.BLOCK.getOptional(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get()).withPath(work)).isEmpty() && (sourceContainedBlockToken || (removedSuffix != null && !woodFamilyBaseDetected));

                if (expectBlockBase && !work.endsWith("_block")) work = work.concat("_block");
            }

            return work;
        });
    }

    /**
     * Retrieves the texture location for the target block using standard block ID transformation. Attempts to find a
     * texture matching the block's registry ID, then falls back to using the transformed block ID if no direct texture
     * match is found.
     *
     * @param targetBlock The {@link Supplier} of the target {@link Block} to find the texture location for.
     *
     * @return The {@link ResourceLocation} of the texture for the target block.
     *
     * @see #getTextureLocationOrDefault(Supplier)
     * @see #pickBlockId(Supplier)
     */
    public static ResourceLocation pickBlockTexture(Supplier<Block> targetBlock) {
        return getTextureLocationOrDefault(targetBlock, "block", getTextureLocationOrDefault(pickBlockId(targetBlock), "block"));
    }

    /**
     * Retrieves the registry ID for the target material using standard material ID transformation (pre/appending). Attempts
     * to find a registry ID matching the material's mutated registry ID (based on both provided parameters,
     * {@code addedPrefix} and {@code addedSuffix}) via {@link #pickItemLikeId(Supplier, Function)}.
     *
     * @param targetMaterialObject The {@link Supplier} of the target {@link ItemLike} to find the registry ID based off
     *                             of.
     * @param addedPrefix An optional {@link String} to prepend to the material ID before lookup. May be empty.
     * @param addedSuffix An optional {@link String} to append to the material ID before lookup. May be empty.
     *
     * @return The {@linkplain ResourceLocation found corresponding material ID}.
     *
     * @see #pickItemLikeId(Supplier, Function)
     */
    public static <IL extends ItemLike> ResourceLocation pickMaterialId(Supplier<IL> targetMaterialObject, String addedPrefix, String addedSuffix) {
        return pickItemLikeId(targetMaterialObject, baseMaterialPath -> {
            String work = baseMaterialPath;

            for (String potentiallyRemovableSuffix : MATERIAL_SUFFIXES) {
                if (work.endsWith(potentiallyRemovableSuffix)) {
                    work = work.substring(0, work.length() - potentiallyRemovableSuffix.length());
                    break;
                }
            }

            if (!addedPrefix.isBlank() && !work.startsWith(addedPrefix)) work = addedPrefix.concat(work);
            if (!addedSuffix.isBlank() && !work.endsWith(addedSuffix)) work = work.concat(addedSuffix);

            return work;
        });
    }

    public static <IL extends ItemLike> ResourceLocation pickMaterialId(Supplier<IL> targetMaterialObject, String addedSuffix) {
        return pickMaterialId(targetMaterialObject, "", addedSuffix);
    }

    public static <IL extends ItemLike> ResourceLocation pickMaterialId(Supplier<IL> targetMaterialObject) {
        return pickMaterialId(targetMaterialObject, "");
    }

    public static <IL extends ItemLike> ResourceLocation pickMaterialBlockId(Supplier<IL> targetMaterialObject) {
        return pickMaterialId(targetMaterialObject, "_block");
    }

    public static <IL extends ItemLike> ResourceLocation pickMaterialOreId(Supplier<IL> targetMaterialObject) {
        return pickMaterialId(targetMaterialObject, "_ore");
    }

    public static <IL extends ItemLike> ResourceLocation pickMaterialDeepslateOreId(Supplier<IL> targetMaterialObject) {
        return pickMaterialId(targetMaterialObject, "deepslate_", "_ore");
    }

    public static <IL extends ItemLike> ResourceLocation pickMaterialIngotId(Supplier<IL> targetMaterialObject) {
        return pickMaterialId(targetMaterialObject, "_ingot");
    }

    public static <IL extends ItemLike> ResourceLocation pickMaterialNuggetId(Supplier<IL> targetMaterialObject) {
        return pickMaterialId(targetMaterialObject, "_nugget");
    }

    public static BlockSetType getOrCreateBlockSetType(BlockSetType blockSetType) {
        return BlockSetType.values()
                .filter(curBlockSetType -> Objects.equals(curBlockSetType.name(), blockSetType.name()))
                .findFirst()
                .orElseGet(() -> BlockSetType.register(blockSetType));
    }

    public static BlockSetType getOrCreateBlockSetType(String blockSetTypeName) {
        return getOrCreateBlockSetType(new BlockSetType(blockSetTypeName));
    }

    public static WoodType getOrCreateWoodType(String typeName, BlockSetType parentBlockSetType) {
        return WoodType.values()
                .filter(curWoodType -> Objects.equals(curWoodType.name(), typeName) && Objects.equals(curWoodType.setType().name(), parentBlockSetType.name()))
                .findFirst()
                .orElseGet(() -> WoodType.register(new WoodType(typeName, parentBlockSetType)));
    }

    public static WoodType getOrCreateWoodType(String typeName) {
        return getOrCreateWoodType(typeName, getOrCreateBlockSetType(typeName));
    }

    public static WoodenBlockGroup registerStandardWoodFamily(ResourceLocation familyId, Supplier<TagKey<Item>> logTag, @Nullable Collection<Supplier<Item>> itemSupCol, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<BlockEntityType<BlockEntity>>> blockEntityTypeSupCol, @Nullable Collection<Supplier<EntityType<Entity>>> entityTypeSupCol) {
        return Util.make(() -> {
            Set<Supplier<? extends Block>> woodBlockFamilySet = new ObjectOpenHashSet<>();
            Set<Supplier<? extends Item>> woodItemFamilySet = new ObjectOpenHashSet<>();
            Set<Supplier<? extends BlockEntityType<? extends BlockEntity>>> woodBlockEntityFamilySet = new ObjectOpenHashSet<>();
            Set<Supplier<? extends EntityType<? extends Entity>>> woodEntityTypeFamilySet = new ObjectOpenHashSet<>();
            BlockSetType woodBlockSetType = BlockSetType.register(new BlockSetType(familyId.toString()));
            WoodType woodBlockType = WoodType.register(new WoodType(familyId.toString(), woodBlockSetType));
            ResourceLocation standardSignId = familyId.withSuffix("_sign");
            ResourceLocation standardHangingSignId = familyId.withSuffix("_hanging_sign");

            Supplier<RotatedPillarBlock> woodenLog = BlockPropertyWrapperTemplates.registerWithItemAndReflectAndChain(familyId.withSuffix("_log"), () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)), BlockPropertyWrapperTemplates.WOODEN_LOG, blockSupCol, itemSupCol)
                    .withAdditionalTag(logTag::get)
                    .buildAndGet();
            Supplier<RotatedPillarBlock> woodBlock = BlockPropertyWrapperTemplates.registerWithItemAndChain(familyId.withSuffix("_wood"), () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)), BlockPropertyWrapperTemplates.WOOD, blockSupCol, itemSupCol)
                    .withAdditionalTag(logTag::get)
                    .buildAndGet();
            Supplier<RotatedPillarBlock> woodenStrippedLog = BlockPropertyWrapperTemplates.registerWithItemAndChain(familyId.withPrefix("stripped_").withSuffix("_log"), () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)), BlockPropertyWrapperTemplates.STRIPPED_WOODEN_LOG, blockSupCol, itemSupCol)
                    .withAdditionalTag(logTag::get)
                    .buildAndGet();

            Supplier<Block> woodenPlanks = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_planks"), () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)), BlockPropertyWrapperTemplates.WOODEN_PLANKS, blockSupCol, itemSupCol);
            Supplier<Block> woodenSlab = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_slab"), () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)), BlockPropertyWrapperTemplates.WOODEN_SLAB, blockSupCol, itemSupCol);
            Supplier<Block> woodenStairs = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_stairs"), () -> new StairBlock(woodenPlanks.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)), BlockPropertyWrapperTemplates.WOODEN_STAIRS, blockSupCol, itemSupCol);

            Supplier<Block> woodenFence = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_fence"), () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)), BlockPropertyWrapperTemplates.WOODEN_FENCE, blockSupCol, itemSupCol);
            Supplier<Block> woodenFenceGate = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_fence_gate"), () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), woodBlockType), BlockPropertyWrapperTemplates.WOODEN_FENCE_GATE, blockSupCol, itemSupCol);

            Supplier<Block> woodenDoor = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_door"), () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), woodBlockSetType), BlockPropertyWrapperTemplates.WOODEN_DOOR, blockSupCol, itemSupCol);
            Supplier<Block> woodenTrapdoor = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_trapdoor"), () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR), woodBlockSetType), BlockPropertyWrapperTemplates.WOODEN_TRAPDOOR, blockSupCol, itemSupCol);

            Supplier<Block> woodenPressurePlate = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_pressure_plate"), () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE), woodBlockSetType), BlockPropertyWrapperTemplates.WOODEN_PRESSURE_PLATE, blockSupCol, itemSupCol);
            Supplier<Block> woodenButton = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_button"), () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), woodBlockSetType, 30, true), BlockPropertyWrapperTemplates.WOODEN_BUTTON, blockSupCol, itemSupCol);

            Supplier<Block> woodenStandingSign = BlockPropertyWrapperTemplates.registerBlockFromTemplateAndReflect(standardSignId, () -> new DefaultableStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN), woodBlockType), BlockPropertyWrapperTemplates.WOODEN_STANDING_SIGN, blockSupCol);
            Supplier<Block> woodenWallSign = BlockPropertyWrapperTemplates.registerBlockFromTemplateAndReflect(familyId.withSuffix("_wall_sign"), () -> new DefaultableWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN).dropsLike(woodenStandingSign.get()), woodBlockType), BlockPropertyWrapperTemplates.WOODEN_WALL_SIGN, blockSupCol);

            Supplier<Block> woodenCeilingHangingSign = BlockPropertyWrapperTemplates.registerBlockFromTemplateAndReflect(standardHangingSignId, () -> new DefaultableCeilingHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), woodBlockType), BlockPropertyWrapperTemplates.WOODEN_CEILING_HANGING_SIGN, blockSupCol);
            Supplier<Block> woodenWallHangingSign = BlockPropertyWrapperTemplates.registerBlockFromTemplateAndReflect(familyId.withSuffix("_wall_hanging_sign"), () -> new DefaultableWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(woodenCeilingHangingSign.get()), woodBlockType), BlockPropertyWrapperTemplates.WOODEN_WALL_HANGING_SIGN, blockSupCol);

            Supplier<SignItem> woodenSignItem = ItemPropertyWrapperTemplates.registerItem(standardSignId, () -> new SignItem(new Item.Properties().stacksTo(16), woodenStandingSign.get(), woodenWallSign.get()), itemSupCol);
            Supplier<HangingSignItem> woodenHangingSignItem = ItemPropertyWrapperTemplates.registerItem(standardHangingSignId, () -> new HangingSignItem(woodenCeilingHangingSign.get(), woodenWallHangingSign.get(), new Item.Properties().stacksTo(16)), itemSupCol);

            Supplier<Item> woodenBoatItem = ItemPropertyWrapperTemplates.registerItemFromTemplate(familyId.withSuffix("_boat"), () -> new DefaultableBoatItem(false, BoatType.register(familyId.toString().replace(':', '-'), woodenPlanks), new Item.Properties().stacksTo(1)), ItemPropertyWrapperTemplates.BOAT, itemSupCol);
            Supplier<Item> woodenChestBoatItem = ItemPropertyWrapperTemplates.registerItemFromTemplate(familyId.withSuffix("_chest_boat"), () -> new DefaultableBoatItem(true, BoatType.register(familyId.toString().replace(':', '-'), woodenPlanks), new Item.Properties().stacksTo(1)), ItemPropertyWrapperTemplates.CHEST_BOAT, itemSupCol);

            BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(familyId.withPath("sign"))
                    .ifPresentOrElse(alreadyRegisteredBaseSignBlockEntity -> {
                        Supplier<BlockEntityType<DefaultableSignBlockEntity>> mappedSignBlockEntitySup = () -> (BlockEntityType<DefaultableSignBlockEntity>) alreadyRegisteredBaseSignBlockEntity;
                        BlockEntityType<DefaultableSignBlockEntity> mappedSignBlockEntity = mappedSignBlockEntitySup.get();

                        mappedSignBlockEntity.validBlocks = Util.make(new ObjectOpenHashSet<>(mappedSignBlockEntity.validBlocks), existingValidBlocks -> {
                            existingValidBlocks.add(woodenStandingSign.get());
                            existingValidBlocks.add(woodenWallSign.get());
                        });

                        woodBlockEntityFamilySet.add(mappedSignBlockEntitySup);
                    }, () -> {
                        Supplier<BlockEntityType<DefaultableSignBlockEntity>> woodenSignBlockEntityType = BlockEntityTypePropertyWrapperTemplates.registerBlockEntityTypeFromTemplateAndReflect(familyId.withPath("sign"), () -> BlockEntityType.Builder.of(
                                (targetPos, targetState) -> new DefaultableSignBlockEntity(() -> BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(familyId.withPath("sign")).orElseThrow(), targetPos, targetState),
                                woodenStandingSign.get(), woodenWallSign.get()
                        ).build(Util.fetchChoiceType(References.BLOCK_ENTITY, standardSignId.getPath())), BlockEntityTypePropertyWrapperTemplates.SIGN, blockEntityTypeSupCol);

                        woodBlockEntityFamilySet.add(woodenSignBlockEntityType);
                    });
            BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(familyId.withPath("hanging_sign"))
                    .ifPresentOrElse(alreadyRegisteredBaseHangingSignBlockEntity -> {
                        Supplier<BlockEntityType<DefaultableHangingSignBlockEntity>> mappedHangingSignBlockEntitySup = () -> (BlockEntityType<DefaultableHangingSignBlockEntity>) alreadyRegisteredBaseHangingSignBlockEntity;
                        BlockEntityType<DefaultableHangingSignBlockEntity> mappedHangingSignBlockEntity = mappedHangingSignBlockEntitySup.get();

                        mappedHangingSignBlockEntity.validBlocks = Util.make(new ObjectOpenHashSet<>(mappedHangingSignBlockEntity.validBlocks), existingValidBlocks -> {
                            existingValidBlocks.add(woodenCeilingHangingSign.get());
                            existingValidBlocks.add(woodenWallHangingSign.get());
                        });

                        woodBlockEntityFamilySet.add(mappedHangingSignBlockEntitySup);
                    }, () -> {
                        Supplier<BlockEntityType<DefaultableHangingSignBlockEntity>> woodenHangingSignBlockEntityType = BlockEntityTypePropertyWrapperTemplates.registerBlockEntityTypeFromTemplateAndReflect(familyId.withPath("hanging_sign"), () -> BlockEntityType.Builder.of(
                                (targetPos, targetState) -> new DefaultableHangingSignBlockEntity(() -> BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(familyId.withPath("hanging_sign")).orElseThrow(), targetPos, targetState),
                                woodenCeilingHangingSign.get(), woodenWallHangingSign.get()
                        ).build(Util.fetchChoiceType(References.BLOCK_ENTITY, standardHangingSignId.getPath())), BlockEntityTypePropertyWrapperTemplates.HANGING_SIGN, blockEntityTypeSupCol);

                        woodBlockEntityFamilySet.add(woodenHangingSignBlockEntityType);
                    });

            BuiltInRegistries.ENTITY_TYPE.getOptional(familyId.withPath("boat"))
                    .ifPresentOrElse(alreadyRegisteredBaseBoatEntity -> {
                        woodEntityTypeFamilySet.add(() -> alreadyRegisteredBaseBoatEntity);
                    }, () -> {
                        Supplier<EntityType<DefaultableBoat>> woodenBoatEntityType = EntityTypePropertyWrapperTemplates.registerEntityTypeFromTemplateAndReflect(familyId.withPath("boat"),
                                () -> EntityType.Builder.<DefaultableBoat>of(DefaultableBoat::new, MobCategory.MISC)
                                        .sized(1.375F, 0.5625F)
                                        .clientTrackingRange(10)
                                        .build(familyId.withPath("boat").toString()), EntityTypePropertyWrapperTemplates.BOAT, entityTypeSupCol);

                        woodEntityTypeFamilySet.add(woodenBoatEntityType);
                    });
            BuiltInRegistries.ENTITY_TYPE.getOptional(familyId.withPath("chest_boat"))
                    .ifPresentOrElse(alreadyRegisteredBaseChestBoatEntity -> {
                        woodEntityTypeFamilySet.add(() -> alreadyRegisteredBaseChestBoatEntity);
                    }, () -> {
                        Supplier<EntityType<DefaultableChestBoat>> woodenChestBoatEntityType = EntityTypePropertyWrapperTemplates.registerEntityTypeFromTemplateAndReflect(familyId.withPath("chest_boat"),
                                () -> EntityType.Builder.<DefaultableChestBoat>of(DefaultableChestBoat::new, MobCategory.MISC)
                                        .sized(1.375F, 0.5625F)
                                        .clientTrackingRange(10)
                                        .build(familyId.withPath("chest_boat").toString()), EntityTypePropertyWrapperTemplates.CHEST_BOAT, entityTypeSupCol);

                        woodEntityTypeFamilySet.add(woodenChestBoatEntityType);
                    });

            woodBlockFamilySet.add(woodenLog);
            woodBlockFamilySet.add(woodBlock);
            woodBlockFamilySet.add(woodenStrippedLog);

            woodBlockFamilySet.add(woodenPlanks);
            woodBlockFamilySet.add(woodenSlab);
            woodBlockFamilySet.add(woodenStairs);

            woodBlockFamilySet.add(woodenFence);
            woodBlockFamilySet.add(woodenFenceGate);

            woodBlockFamilySet.add(woodenDoor);
            woodBlockFamilySet.add(woodenTrapdoor);

            woodBlockFamilySet.add(woodenPressurePlate);
            woodBlockFamilySet.add(woodenButton);

            woodBlockFamilySet.add(woodenStandingSign);
            woodBlockFamilySet.add(woodenWallSign);

            woodBlockFamilySet.add(woodenCeilingHangingSign);
            woodBlockFamilySet.add(woodenWallHangingSign);

            woodItemFamilySet.add(woodenSignItem);
            woodItemFamilySet.add(woodenHangingSignItem);

            woodItemFamilySet.add(woodenBoatItem);
            woodItemFamilySet.add(woodenChestBoatItem);

            return new WoodenBlockGroup(woodBlockSetType, woodBlockType, woodBlockFamilySet, woodItemFamilySet, woodBlockEntityFamilySet, woodEntityTypeFamilySet);
        });
    }

    public static WoodenBlockGroup registerStandardWoodFamily(ResourceLocation familyId, Supplier<TagKey<Item>> logTag) {
        return registerStandardWoodFamily(familyId, logTag, null, null, null, null);
    }

    public static WoodenBlockGroup registerStandardWoodFamily(ResourceLocation familyId) {
        return registerStandardWoodFamily(familyId, Suppliers.ofInstance(null));
    }

    public static StoneBlockGroup registerStoneFamily(ResourceLocation familyId, int miningLevel, boolean includePressurePlateAndButton, boolean deepslateLike, Function<Supplier<Block>, LootTable.Builder> baseBlockLootTableBuilder, Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>> baseBlockRecipeMapper, Function<ResourceLocation, ResourceLocation> familyMemberIdMapper, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return Util.make(() -> {
            Set<Supplier<? extends Block>> stoneBlockFamilySet = new ObjectOpenHashSet<>();
            BlockSetType stoneBlockSetType = getOrCreateBlockSetType(familyId.toString());
            Function<ResourceLocation, ResourceLocation> memberMapperWrapper = familyMemberIdMapper == null ? Function.identity() : familyMemberIdMapper;

            Supplier<Block> stoneBlock = BlockPropertyWrapperTemplates.registerWithItemAndChain(memberMapperWrapper.apply(familyId), () -> new Block(BlockBehaviour.Properties.copy(deepslateLike ? Blocks.DEEPSLATE : Blocks.STONE)), baseBlockTemplate != null ? baseBlockTemplate : BlockPropertyWrapperTemplates.BASIC_PICKAXE, blockSupCol, itemSupCol)
                    .minimumMiningLevel(miningLevel)
                    .withLootTable(baseBlockLootTableBuilder == null || Objects.equals(baseBlockLootTableBuilder, Function.identity()) ? baseBlockTemplate == null ? LootUtil::dropSelf : baseBlockTemplate.getLootTableBuilder().orElse(LootUtil::dropSelf) : baseBlockLootTableBuilder)
                    .withRecipe(baseBlockRecipeMapper == null ? baseBlockTemplate == null ? null : baseBlockTemplate.getRecipeConsumer().orElse(null) : baseBlockRecipeMapper)
                    .buildAndGet();
            Supplier<StairBlock> stoneStairs = BlockPropertyWrapperTemplates.registerWithItemAndChain(memberMapperWrapper.apply(familyId.withSuffix("_stairs")), () -> new StairBlock(stoneBlock.get().defaultBlockState(), BlockBehaviour.Properties.copy(deepslateLike ? Blocks.DEEPSLATE_BRICK_STAIRS : Blocks.STONE_STAIRS)), BlockPropertyWrapperTemplates.STAIRS, blockSupCol, itemSupCol)
                    .minimumMiningLevel(miningLevel)
                    .buildAndGet();
            Supplier<SlabBlock> stoneSlab = BlockPropertyWrapperTemplates.registerWithItemAndChain(memberMapperWrapper.apply(familyId.withSuffix("_slab")), () -> new SlabBlock(BlockBehaviour.Properties.copy(deepslateLike ? Blocks.DEEPSLATE_BRICK_SLAB : Blocks.STONE_SLAB)), BlockPropertyWrapperTemplates.SLAB, blockSupCol, itemSupCol)
                    .minimumMiningLevel(miningLevel)
                    .buildAndGet();
            Supplier<WallBlock> stoneWall = BlockPropertyWrapperTemplates.registerWithItemAndChain(memberMapperWrapper.apply(familyId.withSuffix("_wall")), () -> new WallBlock(BlockBehaviour.Properties.copy(deepslateLike ? Blocks.DEEPSLATE_BRICK_WALL : Blocks.COBBLESTONE_WALL)), BlockPropertyWrapperTemplates.WALL, blockSupCol, itemSupCol)
                    .minimumMiningLevel(miningLevel)
                    .buildAndGet();

            Supplier<Block> stonePressurePlate = null;
            Supplier<Block> stoneButton = null;

            if (includePressurePlateAndButton) {
                stonePressurePlate = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(memberMapperWrapper.apply(familyId.withSuffix("_pressure_plate")), () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, BlockBehaviour.Properties.copy(Blocks.STONE_PRESSURE_PLATE), stoneBlockSetType), BlockPropertyWrapperTemplates.PRESSURE_PLATE, blockSupCol, itemSupCol);
                stoneButton = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(memberMapperWrapper.apply(familyId.withSuffix("_button")), () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON), stoneBlockSetType, 20, false), BlockPropertyWrapperTemplates.BUTTON, blockSupCol, itemSupCol);
            }

            stoneBlockFamilySet.add(stoneBlock);
            stoneBlockFamilySet.add(stoneStairs);
            stoneBlockFamilySet.add(stoneSlab);
            stoneBlockFamilySet.add(stoneWall);

            if (includePressurePlateAndButton) {
                stoneBlockFamilySet.add(stonePressurePlate);
                stoneBlockFamilySet.add(stoneButton);
            }

            return new StoneBlockGroup(stoneBlockSetType, stoneBlockFamilySet);
        });
    }

    public static StoneBlockGroup registerStoneFamily(ResourceLocation familyId, int miningLevel, boolean includePressurePlateAndButton, boolean deepslateLike, Function<Supplier<Block>, LootTable.Builder> baseBlockLootTableBuilder, Function<ResourceLocation, ResourceLocation> familyMemberIdMapper, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, miningLevel, includePressurePlateAndButton, deepslateLike, baseBlockLootTableBuilder, null, familyMemberIdMapper, baseBlockTemplate, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerStoneFamily(ResourceLocation familyId, int miningLevel, boolean includePressurePlateAndButton, boolean deepslateLike, Function<ResourceLocation, ResourceLocation> familyMemberIdMapper, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, miningLevel, includePressurePlateAndButton, deepslateLike, null, familyMemberIdMapper, baseBlockTemplate, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerStoneFamily(ResourceLocation familyId, int miningLevel, boolean includePressurePlateAndButton, boolean deepslateLike, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, miningLevel, includePressurePlateAndButton, deepslateLike, Function.identity(), baseBlockTemplate, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerStoneFamily(ResourceLocation familyId, int miningLevel, boolean includePressurePlateAndButton, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, miningLevel, includePressurePlateAndButton, false, baseBlockTemplate, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerStoneFamily(ResourceLocation familyId, int miningLevel, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, miningLevel, true, baseBlockTemplate, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerStoneFamily(ResourceLocation familyId, int miningLevel, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate) {
        return registerStoneFamily(familyId, miningLevel, baseBlockTemplate, null, null);
    }

    public static StoneBlockGroup registerRegularStoneFamily(ResourceLocation familyId, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, 0, true, false, baseStoneBlockSup -> BASE_TO_COBBLED_LOOT_TABLE_BUILDER.apply(familyId, baseStoneBlockSup), RecipeUtil::baseStoneFromCobbled, Function.identity(), baseBlockTemplate, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerRegularStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerRegularStoneFamily(familyId, null, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerRegularStoneFamily(ResourceLocation familyId, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate) {
        return registerRegularStoneFamily(familyId, baseBlockTemplate, null, null);
    }

    public static StoneBlockGroup registerRegularStoneFamily(ResourceLocation familyId) {
        return registerRegularStoneFamily(familyId, null, null, null);
    }

    public static StoneBlockGroup registerRegularReinforcedStoneFamily(ResourceLocation familyId, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, 2, true, true, baseStoneBlockSup -> BASE_TO_COBBLED_LOOT_TABLE_BUILDER.apply(familyId, baseStoneBlockSup), RecipeUtil::baseStoneFromCobbled, Function.identity(), baseBlockTemplate, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerRegularReinforcedStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerRegularReinforcedStoneFamily(familyId, null, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerRegularReinforcedStoneFamily(ResourceLocation familyId, @Nullable BlockPropertyWrapper<Block> baseBlockTemplate) {
        return registerRegularReinforcedStoneFamily(familyId, baseBlockTemplate, null, null);
    }

    public static StoneBlockGroup registerRegularReinforcedStoneFamily(ResourceLocation familyId) {
        return registerRegularReinforcedStoneFamily(familyId, null, null, null);
    }

    public static StoneBlockGroup registerStoneBrickFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, 0, false, false, memberId -> BRICK_MEMBER_ID_MAPPER.apply(familyId, memberId), BlockPropertyWrapperTemplates.BRICKS_PICKAXE, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerStoneBrickFamily(ResourceLocation familyId) {
        return registerStoneBrickFamily(familyId, null, null);
    }

    public static StoneBlockGroup registerReinforcedStoneBrickFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, 2, false, true, memberId -> BRICK_MEMBER_ID_MAPPER.apply(familyId, memberId), BlockPropertyWrapperTemplates.BRICKS_PICKAXE, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerReinforcedStoneBrickFamily(ResourceLocation familyId) {
        return registerReinforcedStoneBrickFamily(familyId, null, null);
    }

    public static StoneBlockGroup registerChiseledStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, 0, false, BlockPropertyWrapperTemplates.CHISELED_STONE_PICKAXE, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerChiseledStoneFamily(ResourceLocation familyId) {
        return registerChiseledStoneFamily(familyId, null, null);
    }

    public static StoneBlockGroup registerReinforcedChiseledStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerStoneFamily(familyId, 2, false, true, BlockPropertyWrapperTemplates.CHISELED_STONE_COBBLED_PICKAXE, blockSupCol, itemSupCol);
    }

    public static StoneBlockGroup registerReinforcedChiseledStoneFamily(ResourceLocation familyId) {
        return registerReinforcedChiseledStoneFamily(familyId, null, null);
    }

    public static StoneBlockGroup registerStandardStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return Util.make(() -> {
            StoneBlockGroup standardStoneBlockGroup = registerRegularStoneFamily(familyId, blockSupCol, itemSupCol);
            StoneBlockGroup stoneBrickBlockGroup = registerStoneBrickFamily(familyId, blockSupCol, itemSupCol);
            StoneBlockGroup chiseledStoneBlockGroup = registerChiseledStoneFamily(familyId.withPrefix("chiseled_"), blockSupCol, itemSupCol);
            StoneBlockGroup cobbledStoneBlockGroup = registerStoneFamily(familyId.withPrefix("cobbled_"), 0, false, null, blockSupCol, itemSupCol);

            return standardStoneBlockGroup
                    .chain(stoneBrickBlockGroup)
                    .chain(chiseledStoneBlockGroup)
                    .chain(cobbledStoneBlockGroup);
        });
    }

    public static StoneBlockGroup registerStandardStoneFamily(ResourceLocation familyId) {
        return registerStandardStoneFamily(familyId, null, null);
    }

    public static StoneBlockGroup registerDecorativeStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return Util.make(() -> {
            Set<Supplier<? extends Block>> decorativeStoneBlockFamilySet = new ObjectOpenHashSet<>();
            BlockSetType decorativeStoneBlockSetType = getOrCreateBlockSetType(familyId.toString());

            Supplier<Block> pillarStoneBlock = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_pillar"), () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STONE)), BlockPropertyWrapperTemplates.PILLAR_PICKAXE, blockSupCol, itemSupCol);

            decorativeStoneBlockFamilySet.add(pillarStoneBlock);

            return registerStandardStoneFamily(familyId, blockSupCol, itemSupCol)
                    .chain(new StoneBlockGroup(decorativeStoneBlockSetType, decorativeStoneBlockFamilySet));
        });
    }

    public static StoneBlockGroup registerDecorativeStoneFamily(ResourceLocation familyId) {
        return registerDecorativeStoneFamily(familyId, null, null);
    }

    public static StoneBlockGroup registerReinforcedStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return Util.make(() -> {
            StoneBlockGroup reinforcedStoneBlockGroup = registerRegularReinforcedStoneFamily(familyId, blockSupCol, itemSupCol);
            StoneBlockGroup reinforcedStoneBrickBlockGroup = registerReinforcedStoneBrickFamily(familyId, blockSupCol, itemSupCol);
            StoneBlockGroup reinforcedChiseledStoneBlockGroup = registerReinforcedChiseledStoneFamily(familyId.withPrefix("chiseled_"), blockSupCol, itemSupCol);
            StoneBlockGroup reinforcedCobbledStoneBlockGroup = registerStoneFamily(familyId.withPrefix("cobbled_"), 2, false, true, null, blockSupCol, itemSupCol);

            return reinforcedStoneBlockGroup
                    .chain(reinforcedStoneBrickBlockGroup)
                    .chain(reinforcedChiseledStoneBlockGroup)
                    .chain(reinforcedCobbledStoneBlockGroup);
        });
    }

    public static StoneBlockGroup registerReinforcedStoneFamily(ResourceLocation familyId) {
        return registerReinforcedStoneFamily(familyId, null, null);
    }

    public static StoneBlockGroup registerReinforcedDecorativeStoneFamily(ResourceLocation familyId, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return Util.make(() -> {
            Set<Supplier<? extends Block>> reinforcedDecorativeStoneBlockFamilySet = new ObjectOpenHashSet<>();
            BlockSetType reinforcedDecorativeStoneBlockSetType = getOrCreateBlockSetType(familyId.toString());

            Supplier<Block> reinforcedPillarStoneBlock = BlockPropertyWrapperTemplates.registerBlockWithItemFromTemplate(familyId.withSuffix("_pillar"), () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)), BlockPropertyWrapperTemplates.PILLAR_PICKAXE, blockSupCol, itemSupCol);

            reinforcedDecorativeStoneBlockFamilySet.add(reinforcedPillarStoneBlock);

            return registerReinforcedStoneFamily(familyId, blockSupCol, itemSupCol)
                    .chain(new StoneBlockGroup(reinforcedDecorativeStoneBlockSetType, reinforcedDecorativeStoneBlockFamilySet));
        });
    }

    public static StoneBlockGroup registerReinforcedDecorativeStoneFamily(ResourceLocation familyId) {
        return registerReinforcedDecorativeStoneFamily(familyId, null, null);
    }
}