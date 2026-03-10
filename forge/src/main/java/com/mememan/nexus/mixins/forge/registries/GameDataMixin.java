package com.mememan.nexus.mixins.forge.registries;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mememan.nexus.internal.loader.ForgeRegistryHookManager;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

/**
 * Mixin {@code class} responsible for allowing re-entry of old registry entries during the loading of frozen registry
 * data to the staging registry regardless of whether they're aliased in the active registry state.
 * <br></br>
 * This is necessary since Forge's {@link ForgeRegistry#containsKey(ResourceLocation)} will return {@code true} if the
 * input ID has any aliases that are associated with valid objects/numerical IDs/etc. (basically registered).
 *
 * @see ForgeRegistryAccessor#nexus$getIDRaw(ResourceLocation)
 * @see ForgeRegistryMixin#nexus$addObjectKeyLookupCallback(boolean, LocalRef)
 * @see NexusRegistryDataManager
 * @see ForgeRegistryHookManager#getUpdatedAppellations()
 */
@Mixin(value = GameData.class, remap = false)
public abstract class GameDataMixin {

    private GameDataMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (GameDataMixin)");
    }

    @ModifyArg(method = "loadFrozenDataToStagingRegistry", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"))
    private static <V> Predicate<ResourceLocation> nexus$allowReEntryOfOldRegistryEntries(Predicate<ResourceLocation> original, @Local(name = "frozen") ForgeRegistry<V> frozen, @Local(name = "newRegistry") ForgeRegistry<V> newRegistry) {
        if (!(frozen instanceof ForgeRegistryAccessor frozenAccessor) || !(newRegistry instanceof ForgeRegistryAccessor newRegistryAccessor)) return original;
        return original.or(key -> frozenAccessor.nexus$getIDRaw(key) != -1 && newRegistryAccessor.nexus$getIDRaw(key) == -1);
    }
}
