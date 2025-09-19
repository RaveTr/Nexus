package com.mememan.nexus.internal.event.common;

import com.mememan.nexus.Nexus;
import com.mememan.nexus.NexusForge;
import com.mememan.nexus.internal.ForgeVanillaCompat;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Objects;

/**
 * Internal event {@code class} whose purpose is to handle Forge-specific event-related setup.
 *
 * @see NexusForge
 */
public class NexusForgeCommonSetupEvents {

    @SubscribeEvent
    public static void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
        Nexus.initializeDeferred();

        event.enqueueWork(() -> ForgeVanillaCompat.registerVanillaIntegration());
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab curTab = event.getTab();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> tabEntries = event.getEntries();

        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class)
                .stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<?, ?, ?>) curPW)
                .filter(curWrapper -> {
                    ItemStack parentStack = curWrapper.getParentObject().get().asItem().getDefaultInstance();

                    return !parentStack.isEmpty() && !tabEntries.contains(parentStack) && curWrapper.getParentCreativeModeTabs().stream().anyMatch(curCMTSup -> Objects.equals(curCMTSup.get(), curTab));
                })
                .forEach(curWrapper -> tabEntries.put(curWrapper.getParentObject().get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
    }
}
