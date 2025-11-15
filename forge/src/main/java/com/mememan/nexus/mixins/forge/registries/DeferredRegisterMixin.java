package com.mememan.nexus.mixins.forge.registries;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.internal.services.ForgeRegistrar;
import com.mememan.nexus.platform.services.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

/**
 * Mixin {@code class} responsible for allowing duplicates of early-reflected registry objects (registered via
 * {@link ForgeRegistrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)}) to be gracefully skipped during
 * deferred registration, since their contained values are already directly registered to their pertaining registries in
 * the first place using {@link Registry#register(Registry, ResourceLocation, Object)}.
 *
 * @see Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)
 * @see RegisterEvent#register(ResourceKey, ResourceLocation, Supplier)
 * @see DeferredRegister#addEntries(RegisterEvent)
 */
@Mixin(value = DeferredRegister.class, remap = false)
public abstract class DeferredRegisterMixin {
    @Shadow
    @Final
    private String modid;

    private DeferredRegisterMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (DeferredRegisterMixin)");
    }

    @Shadow
    public abstract <T> ResourceKey<? extends Registry<T>> getRegistryKey();

    @Inject(method = "register(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalArgumentException;<init>(Ljava/lang/String;)V"), cancellable = true)
    private <I> void nexus$handleEarlyReflectedDuplicates(String name, Supplier<? extends I> sup, CallbackInfoReturnable<RegistryObject<I>> cir, @Local(name = "ret") RegistryObject<I> ret) {
        if (ForgeRegistrar.getEarlyReflectedRegistryEntries().containsEntry(getRegistryKey(), new ResourceLocation(modid, name))) {
            cir.cancel();
            cir.setReturnValue(ret);
        }
    }
}
