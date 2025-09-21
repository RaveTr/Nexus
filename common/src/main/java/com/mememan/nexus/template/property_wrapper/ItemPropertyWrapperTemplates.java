package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.item.ItemPropertyWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link ItemPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class ItemPropertyWrapperTemplates {

    private ItemPropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (ItemPropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link Item}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     * @param itemSupCol An optional {@link Collection} to track the registered {@link Item}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered items.
     *
     * @return The {@link Supplier} of the registered {@link Item}.
     *
     * @param <B> Any {@link Item} type.
     */
    public static <B extends Item> Supplier<B> registerItem(ResourceLocation itemId, Supplier<B> itemSup, @Nullable Collection<Supplier<Item>> itemSupCol) {
        Supplier<B> registeredItem = NexusServices.REGISTRAR.registerObject(itemId, itemSup, BuiltInRegistries.ITEM);

        if (itemSupCol != null) itemSupCol.add((Supplier<Item>) registeredItem);

        return registeredItem;
    }

    /**
     * Overloaded variant of {@link #registerItem(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Item} to any custom {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Item}.
     *
     * @param <B> Any {@link Item} type.
     */
    public static <B extends Item> Supplier<B> registerItem(ResourceLocation itemId, Supplier<B> itemSup) {
        return registerItem(itemId, itemSup, null);
    }
}
