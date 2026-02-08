package com.mememan.nexus.mixins.forge.registries;

import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraftforge.registries.NamespacedWrapper", remap = false)
public interface NamespacedWrapperAccessor {

    @Accessor("delegate")
    ForgeRegistry<?> getDelegate();
}
