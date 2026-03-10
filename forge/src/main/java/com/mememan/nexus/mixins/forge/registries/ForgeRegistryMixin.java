package com.mememan.nexus.mixins.forge.registries;

import com.google.common.collect.BiMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import com.mememan.nexus.loader.RegistryHookManager;
import com.mememan.nexus.platform.NexusServices;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Mixin {@code class} responsible for adding fallback lookup mechanisms provided by Nexus to Forge-exclusive registries.
 * <br></br>
 * This exists as a separate mixin from, let's say, both {@link MappedRegistry} (assume we could've had a mixin for it in
 * the {@code common} module) and {@link NamespacedWrapperMixin}, since Forge's registry implementation has overrides
 * that commonly call methods from {@link ForgeRegistry} for lookups, which saves us the hassle of having to write even more
 * mixins.
 *
 * @see RegistryHookManager#getUpdatedAppellations()
 * @see NexusRegistryDataManager#handleLevelRegistryData()
 * @see NexusRegistryDataManager#updateRegistryData(LevelStorageSource.LevelDirectory)
 */
@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class ForgeRegistryMixin {
    @Shadow
    private Object defaultValue;
    @Shadow
    @Final
    private ResourceKey<Registry<Object>> key;
    @Shadow
    @Final
    private BiMap<ResourceLocation, Object> names;

    private ForgeRegistryMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (ForgeRegistryMixin)");
    }

    @Inject(method = "getRaw", at = @At("HEAD"))
    private void nexus$captureObjectKey(ResourceLocation key, CallbackInfoReturnable<Object> cir, @Share("key") LocalRef<ResourceLocation> keyRef) {
        keyRef.set(key); // We need these to capture queried keys before they're mutated in their respective lookup methods
    }

    @ModifyReturnValue(method = "getRaw", at = @At("RETURN"))
    private <V> V nexus$addRawObjectLookupCallback(V original, @Share("key") LocalRef<ResourceLocation> keyRef) {
        return original == defaultValue
                ? nexus$getValueThroughAppellations(keyRef.get(), true)
                : original;
    }

    @Inject(method = "getValue(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;", at = @At("HEAD"))
    private void nexus$captureValueKey(ResourceLocation key, CallbackInfoReturnable<Object> cir, @Share("key") LocalRef<ResourceLocation> keyRef) {
        keyRef.set(key);
    }

    @ModifyReturnValue(method = "getValue(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;", at = @At("RETURN"))
    private <V> V nexus$addObjectLookupCallback(V original, @Share("key") LocalRef<ResourceLocation> keyRef) {
        return original == defaultValue
                ? nexus$getValueThroughAppellations(keyRef.get(), true)
                : original;
    }

    @Inject(method = "containsKey", at = @At(value = "HEAD"))
    private void nexus$captureKey(ResourceLocation key, CallbackInfoReturnable<Boolean> cir, @Share("key") LocalRef<ResourceLocation> keyRef) {
        keyRef.set(key);
    }

    @ModifyReturnValue(method = "containsKey", at = @At("TAIL"))
    private boolean nexus$addObjectKeyLookupCallback(boolean original, @Share("key") LocalRef<ResourceLocation> keyRef) {
        return original || nexus$getValueThroughAppellations(keyRef.get(), true) != defaultValue;
    }

    @Unique
    private <V> V nexus$getValueThroughAppellations(ResourceLocation originalId, boolean useDefaultReturnValue) {
        V defVal = (V) (useDefaultReturnValue ? defaultValue : null);

        if (originalId == null) return defVal;

        AtomicReference<V> result = new AtomicReference<>(defVal);

        if (ModLoader.get().hasCompletedState(ModLoadingStage.COMPLETE.name())) { // Avoid classloading/querying data too early and just fall back to the default return value for all early calls to the methods we're targeting
            NexusServices.REGISTRAR.getRegistryHookManager().getUpdatedAppellations()
                    .getOrDefault(key, new Object2ObjectLinkedOpenHashMap<>())
                    .getOrDefault(originalId, new ObjectArrayList<>())
                    .forEach(aliasId -> result.compareAndSet(defVal, (V) names.getOrDefault(aliasId, defVal)));
        }

        return result.get();
    }
}
