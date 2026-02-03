package com.mememan.nexus.property_wrapper.def.world_gen.biome;

import com.mememan.nexus.property_wrapper.impl.generic.DynamicPropertyWrapperBuilder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class BiomePropertyWrapperBuilder<B extends Biome> extends DynamicPropertyWrapperBuilder<B, BiomePropertyWrapperBuilder<B>, BiomePropertyWrapper<B>> {

    public BiomePropertyWrapperBuilder(@NotNull BiomePropertyWrapper<B> ownerWrapper) {
        super(ownerWrapper);
    }


}
