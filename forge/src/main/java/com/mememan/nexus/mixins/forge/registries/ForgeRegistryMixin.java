package com.mememan.nexus.mixins.forge.registries;

import com.google.common.collect.BiMap;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
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
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Mixin {@code class} responsible for adding fallback lookup mechanisms provided by Nexus to Forge-exclusive registries.
 * <br></br>
 * This exists as a separate mixin from, let's say, both {@link MappedRegistry} (assume we could've had a mixin for it in
 * the {@code common} module) and {@link NamespacedWrapperMixin}, since Forge's registry implementation has overrides
 * that commonly call methods from {@link ForgeRegistry} for lookups, which saves us the hassle of having to write even more
 * mixins.
 * <br></br>
 * This also fixes infinite recursion caused by Forge's {@link ResourceLocation}-based registry lookups (the way that
 * code is written causes keys that have aliases of each other to infinitely resolve each other without end before
 * the active registry state is updated, since said keys aren't mapped to their respective objects by then).
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
    @Shadow
    @Final
    private Map<ResourceLocation, ResourceLocation> aliases;

    private ForgeRegistryMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (ForgeRegistryMixin)");
    }

    @Shadow
    protected abstract int getIDRaw(ResourceLocation name);

    @Inject(method = "getRaw", at = @At("HEAD"))
    private void nexus$captureObjectKey(ResourceLocation key, CallbackInfoReturnable<Object> cir, @Share("key") LocalRef<ResourceLocation> keyRef) {
        keyRef.set(key); // We need these to capture queried keys before they're mutated in their respective lookup methods
    }

    @Inject(method = "getRaw", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private <V> void nexus$replaceRawObjectAliasLookup(ResourceLocation key, CallbackInfoReturnable<V> cir, @Local(name = "ret") V originalObj) {
        if (originalObj == null) {
            originalObj = (V) names.get(aliases.get(key)); // key here is already mutated to be the assumed first alias tied to the original ID

            if (originalObj == null) {
                for (int attempts = 0; attempts < 10 && originalObj == null; attempts++) { // Only allow up to 10 lookups for now (yeah yeah lazy solution, but any more than 10 alias associations has never before been seen in Forge modding lmao)
                    ResourceLocation alias = aliases.get(key);

                    if (alias == null || originalObj != null) break;

                    originalObj = (V) names.get(alias);
                    key = alias;
                }
            }

            if (originalObj != null) cir.setReturnValue(originalObj);
        }
    }

    @Definition(id = "ret", local = @Local(type = Object.class, name = "ret"))
    @Definition(id = "key", local = @Local(type = ResourceLocation.class, argsOnly = true))
    @Expression({"ret == null", "key != null"})
    @ModifyExpressionValue(method = "getRaw", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$cancelBuiltInRawObjectAliasLookup(boolean original) {
        return false;
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

    @Inject(method = "getValue(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private <V> void nexus$replaceBuiltInObjectAliasLookup(ResourceLocation key, CallbackInfoReturnable<V> cir, @Local(name = "ret") V originalObj) {
        if (originalObj == null) {
            originalObj = (V) names.getOrDefault(aliases.get(key), defaultValue); // key here is already mutated to be the assumed first alias tied to the original ID

            if (originalObj == defaultValue) {
                for (int attempts = 0; attempts < 10 && originalObj == null; attempts++) { // Only allow up to 10 lookups for now (yeah yeah lazy solution, but any more than 10 alias associations has never before been seen in Forge modding lmao)
                    ResourceLocation alias = aliases.get(key);

                    if (alias == null || originalObj != null) break;

                    originalObj = (V) names.getOrDefault(alias, defaultValue);
                    key = alias;
                }
            }

            if (originalObj != defaultValue) cir.setReturnValue(originalObj);
        }
    }

    @Definition(id = "ret", local = @Local(type = Object.class, name = "ret"))
    @Definition(id = "key", local = @Local(type = ResourceLocation.class, argsOnly = true))
    @Expression({"ret == null", "key != null"})
    @ModifyExpressionValue(method = "getValue(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;", at = @At(value = "MIXINEXTRAS:EXPRESSION", slice = "aliasLookup"), slice = @Slice(to = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1), id = "aliasLookup"))
    private boolean nexus$cancelBuiltInObjectAliasLookup(boolean original) {
        return false;
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
        boolean foundThroughDelegates = original || nexus$getValueThroughAppellations(keyRef.get(), true) != defaultValue; // Not really an accurate name, given what ForgeRegistry wraps around, but whatever lmao
        return ModLoader.get().hasCompletedState(ModLoadingStage.COMPLETE.name()) && RegistryManager.FROZEN.getRegistry(key).getID(keyRef.get()) != -1
                ? getIDRaw(keyRef.get()) != -1
                : foundThroughDelegates;
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
