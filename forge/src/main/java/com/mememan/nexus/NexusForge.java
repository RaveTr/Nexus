package com.mememan.nexus;

import com.mememan.nexus.internal.event.client.NexusForgeClientSetupEvents;
import com.mememan.nexus.internal.event.common.NexusForgeCommonMiscEvents;
import com.mememan.nexus.internal.event.common.NexusForgeCommonSetupEvents;
import com.mememan.nexus.platform.NexusServices;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main Forge entrypoint/mod {@code class} for Nexus API. Handles common mod events and initializes Nexus for Forge.
 */
@Mod(NexusConstants.MOD_ID)
public class NexusForge {

    public NexusForge(FMLJavaModLoadingContext ctx) {
        Nexus.initialize();

        IEventBus modBus = ctx.getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        if (NexusServices.PLATFORM_MANAGER.getEnvironmentSide().isClient()) modBus.register(NexusForgeClientSetupEvents.class);

        modBus.register(NexusForgeCommonSetupEvents.class);
        forgeBus.register(NexusForgeCommonMiscEvents.class); // Can't use auto-annotation registration since it'd initialize the class before everything else
    }
}