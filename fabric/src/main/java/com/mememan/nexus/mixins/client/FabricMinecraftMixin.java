package com.mememan.nexus.mixins.client;

import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.mixins.server.FabricMinecraftServerMainMixin;
import com.mememan.nexus.platform.NexusServices;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.game.minecraft.Hooks;
import net.fabricmc.loader.impl.game.minecraft.patch.EntrypointPatch;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.launch.knot.Knot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Mixin {@code class} to hook into Fabric Loader's launch mechanism and ensure that post-initialization operations
 * run AFTER all entrypoints have been loaded/invoked. This is functionally equivalent to Forge's
 * {@code ModStateProvider#COMPLETE}, which dispatches its own event for running post-init hooks accordingly.
 * <br></br>
 * On both loaders, post-init initializers <b>should</b> run at roughly the same point during the game's startup cycle.
 *
 * @apiNote Separate mixins are done to both {@link Minecraft} and {@link MinecraftServer} due to the fact that trying to
 * directly mixinto any of Fabric's launch hooks directly fails, since Fabric bootstraps mixins around the same time
 * pre-launch entrypoints are invoked, which is effectively after all mod data has been gathered/cached, including
 * entrypoints.
 * <br></br>
 * This means that for Fabric's launch hook classes ({@link EntrypointPatch}, {@link Knot}, {@link Hooks}, etc.), it's
 * not possible to apply mixins since they're statically initialized before Mixin itself loads, so the mixin class itself
 * gets parsed and whatnot but never applied.
 * <br></br>
 * In the client's case, we're applying our post-init hooks right after {@link Hooks#startClient(File, Object)} is called
 * (via asm in {@link EntrypointPatch#process(FabricLauncher, Function, Consumer)}, see references below) in
 * {@linkplain Minecraft#Minecraft(GameConfig) Minecraft's constructor}, just before {@link Minecraft#gameThread} is set.
 *
 * @see <a href="https://wiki.fabricmc.net/documentation:entrypoint">Fabric Wiki: Entrypoints</a>
 * @see Hooks#startClient(File, Object)
 * @see EntrypointPatch#finishEntrypoint(EnvType, ListIterator)
 * @see PostInit
 * @see FabricMinecraftServerMainMixin
 */
@Mixin(Minecraft.class)
public abstract class FabricMinecraftMixin {

    private FabricMinecraftMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (FabricMinecraftMixin)");
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startClient(Ljava/io/File;Ljava/lang/Object;)V", shift = At.Shift.AFTER, remap = false))
    private void nexus$initializePostInitClassesForClient(GameConfig gameConfig, CallbackInfo ci) {
        NexusServices.PLATFORM_MANAGER.discoverAnnotatedClasses(PostInit.class);
    }
}
