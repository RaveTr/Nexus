package com.mememan.nexus.mixins.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.template.event.blueprint.common.PlayerEventBlueprint;
import com.mememan.nexus.template.event.def.common.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin {@code class} that handles some client-side event hooks triggered by the player via the pause screen.
 *
 * @see PlayerEventBlueprint#PLAYER_DISCONNECT
 */
@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin {

    private PauseScreenMixin() {
        throw new IllegalArgumentException("Attempted to construct Mixin Class! (PauseScreenMixin)");
    }

    @Inject(method = "onDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;<init>()V", shift = At.Shift.BEFORE))
    private void nexus$handlePlayerDisconnectEventHook(CallbackInfo ci, @Local(ordinal = 0) boolean wasOnIntegratedServer, @Local(ordinal = 1) boolean wasOnRealms) {
        PlayerEventBlueprint.PLAYER_DISCONNECT.fireEvent(new PlayerEvent.PlayerDisconnectEvent(Minecraft.getInstance().player, wasOnIntegratedServer, wasOnRealms));
    }
}
