package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.creative_mode_tab.CreativeModeTabPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.creative_mode_tab.CreativeModeTabPropertyWrapperBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link CreativeModeTabPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class CreativeModePropertyWrapperTemplates {

    private CreativeModePropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (CreativeModePropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link CreativeModeTab}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     * @param tabSupCol An optional {@link Collection} to track the registered {@link CreativeModeTab}. Primarily useful
     *                  if you want a shorthand method of tracking your own registered creative mode tabs.
     *
     * @return The {@link Supplier} of the registered {@link CreativeModeTab}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> Supplier<CMT> registerCreativeModeTab(ResourceLocation tabId, Supplier<CMT> tabSup, @Nullable Collection<Supplier<CreativeModeTab>> tabSupCol) {
        Supplier<CMT> registeredTab = NexusServices.REGISTRAR.registerObject(tabId, tabSup, BuiltInRegistries.CREATIVE_MODE_TAB);

        if (tabSupCol != null) tabSupCol.add((Supplier<CreativeModeTab>) registeredTab);

        return registeredTab;
    }

    /**
     * Overloaded variant of {@link #registerCreativeModeTab(ResourceLocation, Supplier, Collection)} that does not track
     * the registered {@link CreativeModeTab} to any custom {@link Collection}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     *
     * @return The {@link Supplier} of the registered {@link CreativeModeTab}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> Supplier<CMT> registerCreativeModeTab(ResourceLocation tabId, Supplier<CMT> tabSup) {
        return registerCreativeModeTab(tabId, tabSup, null);
    }

    /**
     * Registers and returns the provided {@link CreativeModeTab}, mapping it to a new {@link CreativeModeTabPropertyWrapper} inheriting
     * from the provided {@link CreativeModeTabPropertyWrapper} template. Optionally tracks the registered {@link CreativeModeTab} to a
     * custom {@link Collection}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     * @param templateBPW The {@link CreativeModeTabPropertyWrapper} template to inherit from.
     * @param tabSupCol An optional {@link Collection} to track the registered {@link CreativeModeTab}. Primarily useful if you
     *                  want a shorthand method of tracking your own registered creative mode tabs.
     *
     * @return The {@link Supplier} of the registered {@link CreativeModeTab}, mapped to its own {@link CreativeModeTabPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> Supplier<CMT> registerCreativeModeTabFromTemplate(ResourceLocation tabId, Supplier<CMT> tabSup, CreativeModeTabPropertyWrapper<CreativeModeTab> templateBPW, @Nullable Collection<Supplier<CreativeModeTab>> tabSupCol) {
        Supplier<CMT> registeredTab = registerCreativeModeTab(tabId, tabSup, tabSupCol);

        return new CreativeModeTabPropertyWrapper<>(registeredTab, tabId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerCreativeModeTabFromTemplate(ResourceLocation, Supplier, CreativeModeTabPropertyWrapper, Collection)} that does not track the
     * registered {@link CreativeModeTab} to any custom {@link Collection}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     * @param templateBPW The {@link CreativeModeTabPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link CreativeModeTab}, mapped to its own {@link CreativeModeTabPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> Supplier<CMT> registerCreativeModeTabFromTemplate(ResourceLocation tabId, Supplier<CMT> tabSup, CreativeModeTabPropertyWrapper<CreativeModeTab> templateBPW) {
        return registerCreativeModeTabFromTemplate(tabId, tabSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link CreativeModeTab} and returns its {@link CreativeModeTabPropertyWrapperBuilder} inheriting from the
     * provided {@link CreativeModeTabPropertyWrapper} template. Optionally tracks the registered {@link CreativeModeTab} to a custom
     * {@link Collection}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     * @param templateBPW The {@link CreativeModeTabPropertyWrapper} template to inherit from.
     * @param tabSupCol An optional {@link Collection} to track the registered {@link CreativeModeTab}. Primarily useful if you
     *                  want a shorthand method of tracking your own registered creative mode tabs.
     *
     * @return The {@link CreativeModeTabPropertyWrapperBuilder} of the registered {@link CreativeModeTab}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> CreativeModeTabPropertyWrapperBuilder<CMT> registerAndChain(ResourceLocation tabId, Supplier<CMT> tabSup, CreativeModeTabPropertyWrapper<CreativeModeTab> templateBPW, @Nullable Collection<Supplier<CreativeModeTab>> tabSupCol) {
        Supplier<CMT> registeredTab = registerCreativeModeTab(tabId, tabSup, tabSupCol);

        return new CreativeModeTabPropertyWrapper<>(registeredTab, tabId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, CreativeModeTabPropertyWrapper, Collection)} that does not track the
     * registered {@link CreativeModeTab} to any custom {@link Collection}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     * @param templateBPW The {@link CreativeModeTabPropertyWrapper} template to inherit from.
     *
     * @return The {@link CreativeModeTabPropertyWrapperBuilder} of the registered {@link CreativeModeTab}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> CreativeModeTabPropertyWrapperBuilder<CMT> registerAndChain(ResourceLocation tabId, Supplier<CMT> tabSup, CreativeModeTabPropertyWrapper<CreativeModeTab> templateBPW) {
        return registerAndChain(tabId, tabSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link CreativeModeTab} and returns its {@link CreativeModeTabPropertyWrapperBuilder}.
     * Optionally tracks the registered {@link CreativeModeTab} to a custom {@link Collection}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     * @param tabSupCol An optional {@link Collection} to track the registered {@link CreativeModeTab}. Primarily
     *                  useful if you want a shorthand method of tracking your own registered creative mode tabs.
     *
     * @return The {@link CreativeModeTabPropertyWrapperBuilder} of the registered {@link CreativeModeTab}, chaining from its own
     * {@link CreativeModeTabPropertyWrapperBuilder}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> CreativeModeTabPropertyWrapperBuilder<CMT> registerAndChain(ResourceLocation tabId, Supplier<CMT> tabSup, @Nullable Collection<Supplier<CreativeModeTab>> tabSupCol) {
        Supplier<CMT> registeredTab = registerCreativeModeTab(tabId, tabSup, tabSupCol);

        return new CreativeModeTabPropertyWrapper<>(registeredTab, tabId.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, CreativeModeTabPropertyWrapper, Collection)}
     * that does not track the registered {@link CreativeModeTab} to any custom {@link Collection}.
     *
     * @param tabId The target {@linkplain CreativeModeTab CreativeModeTab's} {@linkplain ResourceLocation registry ID}.
     * @param tabSup The {@link CreativeModeTab} object to register.
     *
     * @return The {@link CreativeModeTabPropertyWrapperBuilder} of the registered {@link CreativeModeTab}, chaining from its own
     * {@link CreativeModeTabPropertyWrapperBuilder}.
     *
     * @param <CMT> Any {@link CreativeModeTab} type.
     */
    public static <CMT extends CreativeModeTab> CreativeModeTabPropertyWrapperBuilder<CMT> registerAndChain(ResourceLocation tabId, Supplier<CMT> tabSup) {
        return registerAndChain(tabId, tabSup, (Collection<Supplier<CreativeModeTab>>) null);
    }
}
