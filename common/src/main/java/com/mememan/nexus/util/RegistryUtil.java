package com.mememan.nexus.util;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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

    public static Optional<ResourceLocation> getTextureLocation(ResourceLocation textureName, @Nullable String rawObjectRegistryKey) {
        return textureName == null ? Optional.empty() : CACHED_TEXTURE_LOOKUP.stream()
                .filter(curLoc -> curLoc.getNamespace().equals(textureName.getNamespace()) && (rawObjectRegistryKey == null || curLoc.getPath().contains("/" + rawObjectRegistryKey + "/")) && curLoc.getPath().endsWith(textureName.getPath()))
                .findFirst();
    }

    public static Optional<ResourceLocation> getTextureLocation(ResourceLocation textureName) {
        return getTextureLocation(textureName, null);
    }

    @NotNull
    public static ResourceLocation getTextureLocationOrDefault(ResourceLocation textureName, @NotNull ResourceLocation defaultTextureLocation) {
        return getTextureLocation(textureName, null).orElse(defaultTextureLocation);
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
}
