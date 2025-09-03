package com.mememan.nexus.resource.reload_listeners;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mememan.nexus.util.ClientUtil;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Universal resource reload listener for codec-based resources. Can be either server-based (data pack) or client-based
 * (resource pack).
 *
 * @param <T> The object type being wrapped by this listener. Typically, this should have a corresponding {@link Codec}
 *            to pass into here for de/serialization.
 */
public class DefaultedCodecResourceReloadListener<T> extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @NotNull
    protected final ResourceLocation listenerDirectoryId;
    @NotNull
    protected final Codec<T> objectCodec;
    @Nullable
    protected Map<ResourceLocation, T> objectMap;

    public DefaultedCodecResourceReloadListener(ResourceLocation listenerDirectoryId, @NotNull Codec<T> objectCodec) {
        super(GSON, listenerDirectoryId.getPath());

        this.listenerDirectoryId = listenerDirectoryId;
        this.objectCodec = objectCodec;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> serializedObjectMap, ResourceManager resourceManager, ProfilerFiller profileFiller) {
        if (serializedObjectMap.isEmpty()) return;

        Object2ObjectOpenHashMap<ResourceLocation, T> updatedObjectMap = new Object2ObjectOpenHashMap<>(serializedObjectMap.size());

        for (Map.Entry<ResourceLocation, JsonElement> entry : serializedObjectMap.entrySet()) {
            ResourceLocation targetObjId = entry.getKey();

            objectCodec.parse(JsonOps.INSTANCE, entry.getValue())
                    .resultOrPartial(errorMsg -> LOGGER.warn("Failed to parse entry of resource location {} with error: {}", targetObjId, errorMsg))
                    .ifPresent(parsedObj -> updatedObjectMap.put(targetObjId, parsedObj));
        }

        this.objectMap = updatedObjectMap; // We're clearing the whole map + we're using fastutil, not concurrent collections
    }

    @NotNull
    public ResourceLocation getListenerDirectoryId() {
        return listenerDirectoryId;
    }

    @NotNull
    public String getDirectory() {
        return getListenerDirectoryId().getPath();
    }

    @NotNull
    public Codec<T> getObjectCodec() {
        return objectCodec;
    }

    public Map<ResourceLocation, T> getMappedObjectData() {
        return objectMap == null ? ImmutableMap.of() : ImmutableMap.copyOf(objectMap);
    }

    @ApiStatus.Internal
    public void updateSyncedObjectData(Map<ResourceLocation, T> syncedObjectMap) { // Should ONLY be called on the main client thread
        if (ClientUtil.onClient()) this.objectMap = syncedObjectMap; // Prevent overrides on the server
    }
}
