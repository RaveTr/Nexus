package com.mememan.nexus.property_wrapper.def.world_gen.biome;

import com.mememan.nexus.property_wrapper.impl.generic.DynamicPropertyWrapper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BiomePropertyWrapper<B extends Biome> extends DynamicPropertyWrapper<B, BiomePropertyWrapper<B>, BiomePropertyWrapperBuilder<B>> {

    public BiomePropertyWrapper(Supplier<ResourceKey<B>> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BiomePropertyWrapperBuilder::new, modId);
    }

    public BiomePropertyWrapper(@NotNull Supplier<ResourceKey<B>> parentObject, String modId) {
        super(parentObject, BiomePropertyWrapperBuilder::new, modId);
    }

    public BiomePropertyWrapper() {
        super(BiomePropertyWrapperBuilder::new);
    }
}
