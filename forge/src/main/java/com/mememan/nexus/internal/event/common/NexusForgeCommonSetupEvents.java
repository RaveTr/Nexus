package com.mememan.nexus.internal.event.common;

import com.mememan.nexus.Nexus;
import com.mememan.nexus.NexusForge;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.internal.ForgeVanillaCompat;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

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
    public static void onFMLLoadCompleteEvent(final FMLLoadCompleteEvent event) {
        NexusServices.PLATFORM_MANAGER.discoverAnnotatedClasses(PostInit.class);
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

    @SubscribeEvent
    public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(EntityTypePropertyWrapper.class)
                .stream()
                .map(curPW -> (EntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> !Objects.equals(curPW.getParentObject().get().getCategory(), MobCategory.MISC) && curPW.getEntityTypeAttributes().filter(attribBuilderSup -> attribBuilderSup.get() != null).isPresent())
                .forEach(curPW -> event.put((EntityType<? extends LivingEntity>) curPW.getParentObject().get(), curPW.getEntityTypeAttributes().get().get().build()));
    }
}
