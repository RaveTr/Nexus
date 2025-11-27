package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.blueprint.server.ServerLifeCycleEventBlueprint;

@RegistrarEntry
public class Test {

    static {
        ServerLifeCycleEventBlueprint.SERVER_STARTED.onEvent(event -> {
            NexusConstants.LOGGER.error("Hello from listener 1");
            return EventResult.success(event);
        }, 1);
        ServerLifeCycleEventBlueprint.SERVER_STARTED.onEvent(event -> {
            NexusConstants.LOGGER.error("Hello from listener 2");
            return EventResult.cancelled(event, true);
        });

        ServerLifeCycleEventBlueprint.SERVER_STARTING.onEvent(event -> {
            NexusConstants.LOGGER.error("Goodbye from listener 1");
            return EventResult.success(event);
        });

        TickEventBlueprint.LEVEL_TICK.onEvent(event -> {
            NexusConstants.LOGGER.error("{} Level Tick from listener 1", event.getEventSide() == ModSide.CLIENT ? "Client" : "Server");
            return EventResult.success(event);
        });

        TickEventBlueprint.LEVEL_TICK.onEvent(event -> {
            NexusConstants.LOGGER.error("{} Level Tick from listener 2", event.getEventSide() == ModSide.CLIENT ? "Client" : "Server");
            return EventResult.success(event);
        });
    }
}
