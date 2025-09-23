package com.mememan.nexus.util;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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

    @NotNull
    public static ResourceLocation getTextureLocationOrDefault(ResourceLocation textureName, @NotNull ResourceLocation defaultTextureLocation) {
        return getTextureLocation(textureName).orElse(defaultTextureLocation);
    }

    @NotNull
    public static ResourceLocation getTextureLocationOrDefault(ResourceLocation textureName) {
        return getTextureLocationOrDefault(textureName, textureName);
    }

    public static <T> Optional<ResourceLocation> getTextureLocation(Supplier<T> targetObj) {
        return getTextureLocation(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetObj.get()).orElse(null));
    }

    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefault(Supplier<T> targetObj, @NotNull ResourceLocation defaultTextureLocation) {
        return getTextureLocation(targetObj).orElse(defaultTextureLocation);
    }

    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefault(Supplier<T> targetObj) {
        return getTextureLocationOrDefault(targetObj, new ResourceLocation("invalid"));
    }

    @NotNull
    public static <T> ResourceLocation getTextureLocationOrDefaultWithPrefix(Supplier<T> targetObj, String prefix) {
        return getTextureLocationOrDefault(targetObj).withPrefix(prefix);
    }

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

    public static Function<ResourceLocation, ResourceLocation> pickPrefix(String prefix) {
        return baseLoc -> baseLoc.getPath().startsWith(prefix) ? baseLoc : baseLoc.withPrefix(prefix);
    }

    public static Function<ResourceLocation, ResourceLocation> pickSuffix(String suffix) {
        return baseLoc -> baseLoc.getPath().endsWith(suffix) ? baseLoc : baseLoc.withSuffix(suffix);
    }

    public static Function<ResourceLocation, ResourceLocation> pickPrefixAndSuffix(String prefix, String suffix) {
        return baseLoc -> baseLoc.getPath().startsWith(prefix) && baseLoc.getPath().endsWith(suffix) ? baseLoc : baseLoc.withPrefix(prefix).withSuffix(suffix);
    }

    public static Function<ResourceLocation, ResourceLocation> replacePrefix(String replacedPrefix) {
        return baseLoc -> {
            String baseLocPath = baseLoc.getPath();

            return baseLocPath.startsWith(replacedPrefix) || !baseLocPath.contains("_")
                    ? baseLoc
                    : baseLoc.withPath(baseLocPath.replace(StringUtils.substringBefore(baseLocPath, baseLoc.getPath().indexOf('_')), replacedPrefix));
        };
    }

    public static Function<ResourceLocation, ResourceLocation> replaceSuffix(String replacedSuffix) {
        return baseLoc -> {
            String baseLocPath = baseLoc.getPath();

            return baseLocPath.endsWith(replacedSuffix) || !baseLocPath.contains("_")
                    ? baseLoc
                    : baseLoc.withPath(baseLocPath.replace(StringUtils.substringAfter(baseLocPath, baseLoc.getPath().lastIndexOf('_')), replacedSuffix));
        };
    }

    public static <T> Optional<Supplier<T>> getSuppliedObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper, boolean throwIfMissing) {
        T baseObj = baseObjSup.get();
        String targetObjClassName = baseObj.getClass().getSimpleName();
        Registry<T> baseObjRegistry = DataGenPropertyWrapper.RegistryLookupContainer.getRegistryForObject(baseObj)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to find registry for unregistered or unmapped object of type %s: %s", targetObjClassName, baseObj)));
        ResourceLocation baseObjLoc = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(baseObj)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No registry entry present for object of type %s: %s", targetObjClassName, baseObj)));
        ResourceLocation targetObjLoc = targetObjIdMapper.apply(baseObjLoc);
        Supplier<T> targetObj = () -> baseObjRegistry instanceof DefaultedRegistry<?> defReg && Objects.equals(defReg.get(targetObjLoc), defReg.get(defReg.getDefaultKey()))
                ? null
                : baseObjRegistry.get(targetObjLoc);

        if (throwIfMissing && targetObj.get() == null) throw new IllegalArgumentException(String.format("Attempted to compute invalid object '%s' from %s '%s'", targetObjLoc, targetObjClassName, baseObjLoc));

        return Optional.of(targetObj);
    }

    public static <T> Optional<T> getObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper, boolean throwIfMissing) {
        return getSuppliedObjectFrom(baseObjSup, targetObjIdMapper, throwIfMissing).map(Supplier::get);
    }

    public static <T> Optional<T> getObjectFrom(T baseObj, Function<ResourceLocation, ResourceLocation> targetObjIdMapper, boolean throwIfMissing) {
        return getObjectFrom(() -> baseObj, targetObjIdMapper, throwIfMissing);
    }

    public static <T> Optional<Supplier<T>> getSuppliedObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getSuppliedObjectFrom(baseObjSup, targetObjIdMapper, false);
    }

    public static <T> Optional<T> getObjectFrom(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObjSup, targetObjIdMapper, false);
    }

    public static <T> Optional<T> getObjectFrom(T baseObj, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObj, targetObjIdMapper, false);
    }

    public static <T> Supplier<T> getSuppliedObjectFromOrThrow(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getSuppliedObjectFrom(baseObjSup, targetObjIdMapper, true).get();
    }

    public static <T> T getObjectFromOrThrow(Supplier<T> baseObjSup, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObjSup, targetObjIdMapper, true).get();
    }

    public static <T> T getObjectFromOrThrow(T baseObj, Function<ResourceLocation, ResourceLocation> targetObjIdMapper) {
        return getObjectFrom(baseObj, targetObjIdMapper, true).get();
    }
}
