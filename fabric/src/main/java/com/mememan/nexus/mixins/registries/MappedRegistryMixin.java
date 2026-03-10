package com.mememan.nexus.mixins.registries;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mememan.nexus.internal.FabricPostInitMarker;
import com.mememan.nexus.internal.registry.NexusRegistryDataManager;
import com.mememan.nexus.loader.RegistryHookManager;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.Registrar;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Mixin {@code class} responsible for adding fallback lookup mechanisms provided by Nexus to registries in general.
 * <br></br>
 * This exists as a loader-specific mixin (as opposed to, say, a common mixin) since Forge has its own registry
 * implementation that requires additional mixins to be written for it, since they directly patch into MC source code.
 * Said code patches into multiple targets that can't be easily resolved by a single common mixin.
 * <br></br>
 * Fabric's {@code registry-sync-api} simply uses mixins to achieve Fabric-specific functionality, which makes it viable
 * for targeting via a common mixin, but that'd cause duplicate functionality since said mixin would also apply to Forge
 * and cancel common registry lookup callbacks out there (not to mention how the majority of registries in Forge use
 * their own {@code NamespacedWrapper} implementation that replaces most registries in {@link BuiltInRegistries}, meaning
 * that it'd also be redundant anyway).
 *
 * @see Registrar#getRegistryHookManager()
 * @see RegistryHookManager#getUpdatedAppellations()
 * @see NexusRegistryDataManager#handleLevelRegistryData()
 * @see NexusRegistryDataManager#updateRegistryData(LevelStorageSource.LevelDirectory)
 */
@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin {
    @Shadow
    @Final
    final ResourceKey<? extends Registry<?>> key;
    @Shadow
    @Final
    private final Map<ResourceLocation, Holder.Reference<?>> byLocation;

    private MappedRegistryMixin() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (MappedRegistryMixin)");
    }

    @Shadow
    @Nullable
    private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> holder) {
        throw new AssertionError();
    }

    @ModifyReturnValue(method = "getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;", at = @At("TAIL"))
    private <T> Optional<Holder.Reference<T>> nexus$addObjectHolderCallback(Optional<Holder.Reference<T>> original, ResourceKey<T> originalId) {
        return original.or(() -> Optional.ofNullable(nexus$getValueThroughAppellations(originalId.location())));
    }

    @ModifyReturnValue(method = "get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;", at = @At("TAIL"))
    private <T> T nexus$addObjectByIdLookupCallback(@Nullable T original, ResourceLocation originalId) {
        return Optional.ofNullable(original).orElseGet(() -> getValueFromNullable(nexus$getValueThroughAppellations(originalId)));
    }

    @ModifyReturnValue(method = "get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;", at = @At("TAIL"))
    private <T> T nexus$addObjectByKeyLookupCallback(@Nullable T original, ResourceKey<T> originalKey) {
        return Optional.ofNullable(original).orElseGet(() -> getValueFromNullable(nexus$getValueThroughAppellations(originalKey.location())));
    }

    @ModifyReturnValue(method = "containsKey(Lnet/minecraft/resources/ResourceLocation;)Z", at = @At("TAIL"))
    private boolean nexus$addObjectIdKeyLookupCallback(boolean original, ResourceLocation originalId) {
        return original || nexus$getValueThroughAppellations(originalId) != null;
    }

    @ModifyReturnValue(method = "containsKey(Lnet/minecraft/resources/ResourceKey;)Z", at = @At("TAIL"))
    private <T> boolean nexus$addObjectKeyKeyLookupCallback(boolean original, ResourceKey<T> originalKey) {
        return original || nexus$getValueThroughAppellations(originalKey.location()) != null;
    }

    @Unique
    private <T> Holder.Reference<T> nexus$getValueThroughAppellations(ResourceLocation originalId) {
        if (originalId == null) return null;

        AtomicReference<Holder.Reference<T>> result = new AtomicReference<>();

        if (FabricPostInitMarker.MarkerContainer.hasPostInitialized()) { // Avoid classloading/querying data too early and just fall back to the default return value for all early calls to the methods we're targeting
            NexusServices.REGISTRAR.getRegistryHookManager().getUpdatedAppellations()
                    .getOrDefault(key, new Object2ObjectLinkedOpenHashMap<>())
                    .getOrDefault(originalId, new ObjectArrayList<>())
                    .forEach(aliasId -> result.compareAndSet(null, (Holder.Reference<T>) byLocation.getOrDefault(aliasId, null))); // byLocation and byKey should always be the same anyway sooooo...
        }

        return result.get();
    }
}
