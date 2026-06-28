package com.mememan.nexus.mixins;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class NexusFabricMixinConfigPlugin implements IMixinConfigPlugin {
    private static final String REA_MOD_ID = "reach-entity-attributes";
    private static final BooleanSupplier TRUE = () -> true;
    private static final Map<String, BooleanSupplier> CONDITIONALLY_LOADED_MIXINS = ImmutableMap.of( // TODO This is almost certainly gonna have to be split into more specialized compat
            "com.mememan.nexus.mixins.entity.container.AbstractContainerMenuMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID), // Can't use NexusServices cuz early classloading
            "com.mememan.nexus.mixins.entity.container.ContainerEntityMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.entity.container.ContainerMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.entity.container.HorseInventoryMenuMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.entity.container.InventoryMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.entity.container.ItemCombinerMenuMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.entity.FabricPlayerMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.item.ItemMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.server.ServerGamePacketListenerImplMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID),
            "com.mememan.nexus.mixins.server.ServerPlayerGameModeMixin", () -> !FabricLoader.getInstance().isModLoaded(REA_MOD_ID)
    );

    public NexusFabricMixinConfigPlugin() {

    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONALLY_LOADED_MIXINS.getOrDefault(mixinClassName, TRUE).getAsBoolean();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
