package com.mememan.nexus.util;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LeavesBlock;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
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
            "_door", "_trapdoor", "_button", "_pressure_plate", "_fence", "_fence_gate", "_sign", "_hanging_sign"
    };
    private static final String[] VANILLA_WOOD_MATERIALS = new String[] {
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped"
    };

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
                .filter(curLoc -> curLoc.getNamespace().equals(textureName.getNamespace()) && (rawObjectRegistryKey == null || curLoc.getPath().contains("/" + rawObjectRegistryKey + "/")) && curLoc.getPath().endsWith(textureName.getPath()))
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
        return getTextureLocation(textureName).orElse(defaultTextureLocation);
    }

    /**
     * Overloaded variant of {@link #getTextureLocationOrDefault(ResourceLocation, ResourceLocation)}. Attempts to
     * retrieve the {@link ResourceLocation} of the texture with the given {@code textureName} from
     * {@link #CACHED_TEXTURE_LOOKUP}, returning the {@code textureName} itself if no matching texture is found.
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
        return getTextureLocation(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetObj.get()).orElse(null));
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
        return getTextureLocation(targetObj).orElse(defaultTextureLocation);
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
        return getTextureLocationOrDefault(targetObj).withPrefix(prefix);
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
        return getTextureLocationOrDefault(targetObj).withSuffix(suffix);
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
        return baseLoc -> baseLoc.getPath().startsWith(prefix) ? baseLoc : baseLoc.withPrefix(prefix);
    }

    /**
     * Returns a {@link Function} that modifies a {@link ResourceLocation} by appending the provided {@code suffix}
     * to its path if it isn't already suffixed with said suffix.
     *
     * @param suffix The path suffix to search for/append.
     *
     * @return A {@link Function} that appends the provided {@code suffix} to a {@link ResourceLocation}'s path.
     */
    public static Function<ResourceLocation, ResourceLocation> pickSuffix(String suffix) {
        return baseLoc -> baseLoc.getPath().endsWith(suffix) ? baseLoc : baseLoc.withSuffix(suffix);
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

        return Optional.of(targetObj);
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
     * Calculates standard flammability values for wooden blocks following the flammability property patterns used in
     * Minecraft's {@link net.minecraft.world.level.block.FireBlock}. Returns an {@link IntIntMutablePair} where the first
     * value represents the encouragement value (how easily fire spreads from this block) and the second value represents
     * the flammability value (how easily this block catches fire).
     * <p>
     *     <h3>Flammability Patterns</h3>
     *     <ul>
     *         <li>Leaf blocks (description ID ending with {@code "_leaves"} or {@link LeavesBlock} instances):
     *         (30, 60) - High flammability</li>
     *         <li>Carpet blocks (description ID ending with {@code "_carpet"} or {@link CarpetBlock} instances):
     *         (60, 20) - Medium flammability</li>
     *         <li>All other (presumably wooden) blocks: (5, 20) - Low flammability</li>
     *     </ul>
     *
     * @param targetBlock The {@link Supplier} of the target {@link Block} to calculate flammability values for.
     *
     * @return An {@link IntIntMutablePair} containing the encouragement value (first) and flammability value (second).
     *
     * @see FireBlock
     * @see LeavesBlock
     * @see CarpetBlock
     */
    public static IntIntMutablePair standardWoodFlammability(Supplier<Block> targetBlock) {
        Block targetBlockObj = targetBlock.get();

        return targetBlockObj.getDescriptionId().endsWith("_leaves") || targetBlockObj instanceof LeavesBlock
                ? IntIntMutablePair.of(30, 60)
                : targetBlockObj.getDescriptionId().endsWith("_carpet") || targetBlockObj instanceof CarpetBlock
                ? IntIntMutablePair.of(60, 20)
                : IntIntMutablePair.of(5, 20);
    }

    /**
     * Modifies the registry path of the target block using the provided path mapping function. This method allows for
     * custom transformation of block registry paths based on specific naming conventions or requirements.
     *
     * @param targetBlock The {@link Supplier} of the target {@link Block} to modify the registry path for.
     * @param pathIdMapper The {@link Function} to apply to the block's registry path for transformation.
     *
     * @return A new {@link ResourceLocation} with the modified registry path.
     *
     * @throws IllegalArgumentException If no registry entry is present for the target block.
     *
     * @see #pickBlockId(Supplier)
     */
    public static ResourceLocation pickBlockId(Supplier<Block> targetBlock, Function<String, String> pathIdMapper) {
        ResourceLocation baseBlockId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(targetBlock.get());
        String baseBlockPath = baseBlockId.getPath();
        String chosenBlockId = pathIdMapper.apply(baseBlockPath);

        return baseBlockId.withPath(chosenBlockId);
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
     *         {@code _plank} → {@code _planks} (applies to middle and trailing positions).</li>
     *         <li>Wood-family heuristic: for wood-only components, if the base looks like a vanilla wood key
     *         (e.g., {@code oak}, {@code spruce}, {@code bamboo}, {@code crimson}, {@code warped}), append
     *         {@code _planks} unless already present.</li>
     *         <li>Appends {@code _block} when appropriate: if the source contained {@code _block} anywhere, or a
     *         derived suffix was stripped and the resulting base is not a plural family ({@code _bricks}/{@code _planks}).</li>
     *     </ul>
     *
     * @param targetBlock The {@link Supplier} of the target {@link Block} to derive a base block ID for.
     *
     * @return A new {@link ResourceLocation} whose path is the inferred base block ID.
     *
     * @throws IllegalArgumentException If no registry entry is present for the target block.
     *
     * @see #pickBlockId(Supplier, Function)
     */
    public static ResourceLocation pickBlockId(Supplier<Block> targetBlock) {
        return pickBlockId(targetBlock, baseBlockPath -> {
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

            // Normalize brick/plank singulars to plurals in both middle and trailing positions.
            if (work.contains("_brick_")) work = work.replace("_brick_", "_bricks_");
            if (work.endsWith("_brick")) work = work.substring(0, work.length() - 6).concat("_bricks");

            if (work.contains("_plank_")) work = work.replace("_plank_", "_planks_");
            if (work.endsWith("_plank")) work = work.substring(0, work.length() - 6).concat("_planks");

            boolean woodFamilyBaseDetected = work.endsWith("_planks");

            if (removedSuffix != null && StringUtil.containsSuffix(WOOD_COMPONENT_SUFFIXES, removedSuffix)) { // Wood-family heuristic: if we stripped a wood-only component and the base looks like a wood key, append _planks.
                String lastToken = StringUtil.lastToken(work);

                if (StringUtil.containsSuffix(VANILLA_WOOD_MATERIALS, work) || StringUtil.containsSuffix(VANILLA_WOOD_MATERIALS, lastToken)) {
                    if (!work.endsWith("_planks") && !work.contains("plank")) work = work.concat("_planks");

                    woodFamilyBaseDetected = true;
                }
            }

            boolean endsWithFamilyBase = work.endsWith("_bricks") || work.endsWith("_planks"); // Prefer <base>_block unless it's plural or a wood family base (always keep "_block" if present)

            if (!endsWithFamilyBase) {
                /*
                 * Append "_block" when either:
                 * - Source name contains "_block" anywhere
                 * - We stripped a derived suffix and the base is not a wood family (e.g. <wood>_planks)
                 */
                boolean expectBlockBase = sourceContainedBlockToken || (removedSuffix != null && !woodFamilyBaseDetected);

                if (expectBlockBase && !work.endsWith("_block")) work = work.concat("_block");
            }

            return work;
        });
    }

    /**
     * Retrieves the texture location for the target block using standard block ID transformation. This method first
     * attempts to find a texture matching the block's registry ID, then falls back to using the transformed block ID
     * if no direct texture match is found.
     *
     * @param targetBlock The {@link Supplier} of the target {@link Block} to find the texture location for.
     *
     * @return The {@link ResourceLocation} of the texture for the target block.
     *
     * @see #getTextureLocationOrDefault(Supplier)
     * @see #pickBlockId(Supplier)
     */
    public static ResourceLocation pickBlockTexture(Supplier<Block> targetBlock) {
        return RegistryUtil.getTextureLocationOrDefault(targetBlock, RegistryUtil.getTextureLocationOrDefault(pickBlockId(targetBlock)));
    }
}
