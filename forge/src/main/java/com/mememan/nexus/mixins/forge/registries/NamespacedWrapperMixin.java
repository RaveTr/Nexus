package com.mememan.nexus.mixins.forge.registries;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.internal.services.ForgeRegistrar;
import com.mememan.nexus.platform.services.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

/**
 * Hack-Mixin {@code class} responsible for shoving early-reflected registry entries straight into Forge-based
 * {@linkplain net.minecraftforge.registries.NamespacedWrapper NamespacedWrappers}.
 * <br></br>
 * This works out since backing registry maps are hijacked and modified early enough to where it shouldn't interfere
 * with the rest of Forge's registry lifecycle.
 *
 * @see Registrar#registerObjectAndReflect(ResourceLocation, Supplier, Registry)
 * @see ForgeRegistrar#getEarlyReflectedRegistryEntries()
 * @see DeferredRegisterMixin
 */
@Mixin(targets = "net.minecraftforge.registries.NamespacedWrapper", remap = false)
public abstract class NamespacedWrapperMixin {
    @Shadow
    @Final
    private ForgeRegistry<?> delegate;

    private NamespacedWrapperMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (NamespacedWrapperMixin)");
    }

    @Definition(id = "locked", field = "Lnet/minecraftforge/registries/NamespacedWrapper;locked:Z")
    @Expression("this.locked")
    @ModifyExpressionValue(method = "registerMapping(ILnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/Holder$Reference;", at = @At("MIXINEXTRAS:EXPRESSION"), remap = true)
    private <T> boolean nexus$forceEarlyReflectedRegistration(boolean original, @Local(argsOnly = true) ResourceKey<T> key) {
        return original && !ForgeRegistrar.getEarlyReflectedRegistryEntries().get(delegate.getRegistryKey()).contains(key.location());
    }
}
