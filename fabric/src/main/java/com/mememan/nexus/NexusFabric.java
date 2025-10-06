package com.mememan.nexus;

import com.mememan.nexus.internal.FabricServerHooks;
import com.mememan.nexus.internal.FabricVanillaCompat;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric entrypoint/mod initializer {@code class} for Nexus API. Handles mod initialization for Nexus on Fabric.
 */
public class NexusFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Nexus.initialize();

        FabricServerHooks.handleServerLifecycleHooks();
        FabricVanillaCompat.registerVanillaCompat();
    }
}
