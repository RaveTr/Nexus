package com.mememan.nexus.template.event.blueprint.client;

import com.mememan.nexus.event.blueprint.EventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RenderTooltipEventBlueprint {
    public static final RenderTooltipEventBlueprint.Pre PRE = new RenderTooltipEventBlueprint.Pre();
    public static final RenderTooltipEventBlueprint POST = new RenderTooltipEventBlueprint();

    private RenderTooltipEventBlueprint() {

    }

    public static class Pre implements EventBlueprint<Pre.PreRenderTooltipEvent> {

        private Pre() {
            NexusServices.EVENT_BUS.registerEventHook(this);
        }

        @Override
        public Class<PreRenderTooltipEvent> getEventInterface() {
            return PreRenderTooltipEvent.class;
        }

        @Override
        public @Nullable PreRenderTooltipEvent mergeListeners(PreRenderTooltipEvent[] existingListeners) {
            return (targetStack, graphics, mouseX, mouseY, curTooltipFont, tooltipComponents, tooltipPositioner) -> {
                boolean finalizedResult = false;

                for (PreRenderTooltipEvent listener : existingListeners) {
                    finalizedResult |= listener.preRenderToolTip(targetStack, graphics, mouseX, mouseY, curTooltipFont, tooltipComponents, tooltipPositioner);
                }

                return finalizedResult;
            };
        }

        @Override
        public ModSide getEventSide() {
            return ModSide.CLIENT;
        }

        @FunctionalInterface
        public interface PreRenderTooltipEvent {

            boolean preRenderToolTip(@NotNull ItemStack targetStack, GuiGraphics graphics, int mouseX, int mouseY, @NotNull Font curTooltipFont, @NotNull List<ClientTooltipComponent> tooltipComponents, ClientTooltipPositioner tooltipPositioner);
        }
    }
}
