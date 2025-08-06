package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.DatagenRegistrarEntry;
import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.platform.NexusServices;

@DatagenRegistrarEntry
public class TestDatagenRegistrar {
    public static final ModDatagenConfig NEXUS = NexusServices.DATA_GENERATOR.registerConfigForMod(ModDatagenConfig.defaultConfig(NexusConstants.MOD_ID));
}
