package com.mememan.nexus.internal;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

/**
 * Internal Fabric implementation to have access to the current server if it's available.
 */
public class FabricServerHooks {
    private static MinecraftServer CURRENT_SERVER;

    public static void handleServerLifecycleHooks() {
        ServerLifecycleEvents.SERVER_STARTING.register((targetServer) -> CURRENT_SERVER = targetServer);
        ServerLifecycleEvents.SERVER_STOPPING.register((targetServer) -> CURRENT_SERVER = null);
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return CURRENT_SERVER;
    }
}
