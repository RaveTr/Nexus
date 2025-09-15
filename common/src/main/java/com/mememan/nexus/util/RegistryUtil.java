package com.mememan.nexus.util;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
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

    public static Optional<ResourceLocation> getTextureLocation(ResourceLocation objectRegistryId, @Nullable String rawObjectRegistryKey) {
        return objectRegistryId == null ? Optional.empty() : CACHED_TEXTURE_LOOKUP.stream()
                .filter(curLoc -> curLoc.getNamespace().equals(objectRegistryId.getNamespace()) && (rawObjectRegistryKey == null || curLoc.getPath().contains("/" + rawObjectRegistryKey + "/")) && curLoc.getPath().endsWith(objectRegistryId.getPath()))
                .findFirst();
    }

    public static Optional<ResourceLocation> getTextureLocation(ResourceLocation objectRegistryId) {
        return getTextureLocation(objectRegistryId, null);
    }

    public static <T> Optional<ResourceLocation> getTextureLocation(Supplier<T> targetObj) {
        return getTextureLocation(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryId(targetObj.get()).orElse(null));
    }
}
