package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.item.ItemPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.item.ItemPropertyWrapperBuilder;
import com.mememan.nexus.util.ModelUtil;
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
    public static final ItemPropertyWrapper<Item> BASIC_GENERATED = new ItemPropertyWrapper<>()
            .builder()
            .withModelDefinition(ModelUtil::basicGenerated)
            .build();
    public static final ItemPropertyWrapper<Item> BASIC_HANDHELD = new ItemPropertyWrapper<>()
            .builder()
            .withModelDefinition(ModelUtil::basicHandheld)
            .build();

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
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerItem(ResourceLocation itemId, Supplier<I> itemSup, @Nullable Collection<Supplier<Item>> itemSupCol) {
        Supplier<I> registeredItem = NexusServices.REGISTRAR.registerObject(itemId, itemSup, BuiltInRegistries.ITEM);

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
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerItem(ResourceLocation itemId, Supplier<I> itemSup) {
        return registerItem(itemId, itemSup, null);
    }

    /**
     * Registers and returns the provided {@link Item}, mapping it to a new {@link ItemPropertyWrapper} inheriting
     * from the provided {@link ItemPropertyWrapper} template. Optionally tracks the registered {@link Item} to a
     * custom {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     * @param templateBPW The {@link ItemPropertyWrapper} template to inherit from.
     * @param itemSupCol An optional {@link Collection} to track the registered {@link Item}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered items.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerItemFromTemplate(ResourceLocation itemId, Supplier<I> itemSup, ItemPropertyWrapper<Item> templateBPW, @Nullable Collection<Supplier<Item>> itemSupCol) {
        Supplier<I> registeredItem = registerItem(itemId, itemSup, itemSupCol);

        return new ItemPropertyWrapper<>(registeredItem, itemId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerItemFromTemplate(ResourceLocation, Supplier, ItemPropertyWrapper, Collection)}
     * that does not track the registered {@link Item} to any custom {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     * @param templateBPW The {@link ItemPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerItemFromTemplate(ResourceLocation itemId, Supplier<I> itemSup, ItemPropertyWrapper<Item> templateBPW) {
        return registerItemFromTemplate(itemId, itemSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Item} and returns its {@link ItemPropertyWrapperBuilder} inheriting from the
     * provided {@link ItemPropertyWrapper} template. Optionally tracks the registered {@link Item} to a custom
     * {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     * @param templateBPW The {@link ItemPropertyWrapper} template to inherit from.
     * @param itemSupCol An optional {@link Collection} to track the registered {@link Item}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered items.
     *
     * @return The {@link ItemPropertyWrapperBuilder} of the registered {@link Item}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> ItemPropertyWrapperBuilder<I> registerAndChain(ResourceLocation itemId, Supplier<I> itemSup, ItemPropertyWrapper<Item> templateBPW, @Nullable Collection<Supplier<Item>> itemSupCol) {
        Supplier<I> registeredItem = registerItem(itemId, itemSup, itemSupCol);

        return new ItemPropertyWrapper<>(registeredItem, itemId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, ItemPropertyWrapper, Collection)} that
     * does not track the registered {@link Item} to any custom {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     * @param templateBPW The {@link ItemPropertyWrapper} template to inherit from.
     *
     * @return The {@link ItemPropertyWrapperBuilder} of the registered {@link Item}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> ItemPropertyWrapperBuilder<I> registerAndChain(ResourceLocation itemId, Supplier<I> itemSup, ItemPropertyWrapper<Item> templateBPW) {
        return registerAndChain(itemId, itemSup, templateBPW, null);
    }

    /**
     * Registers and returns the provided {@link Item}, mapped to a new {@link ItemPropertyWrapper} inheriting
     * from the {@link #BASIC_GENERATED} template. Optionally tracks the registered {@link Item} to a custom
     * {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     * @param itemSupCol An optional {@link Collection} to track the registered {@link Item}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered items.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the {@code BASIC_GENERATED} template.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerBasicItem(ResourceLocation itemId, Supplier<I> itemSup, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerItemFromTemplate(itemId, itemSup, BASIC_GENERATED, itemSupCol);
    }

    /**
     * Overloaded variant of {@link #registerBasicItem(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Item} to any custom {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the {@code BASIC_GENERATED} template.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerBasicItem(ResourceLocation itemId, Supplier<I> itemSup) {
        return registerBasicItem(itemId, itemSup, null);
    }

    /**
     * Registers a new basic {@link Item} with default properties and returns it, mapped to a new
     * {@link ItemPropertyWrapper} inheriting from the {@link #BASIC_GENERATED} template.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the {@code BASIC_GENERATED} template.
     */
    public static Supplier<Item> registerBasicItem(ResourceLocation itemId) {
        return registerBasicItem(itemId, () -> new Item(new Item.Properties()));
    }

    /**
     * Registers and returns the provided {@link Item}, mapped to a new {@link ItemPropertyWrapper} inheriting
     * from the {@link #BASIC_HANDHELD} template. Optionally tracks the registered {@link Item} to a custom
     * {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     * @param itemSupCol An optional {@link Collection} to track the registered {@link Item}. Primarily useful if you
     *                   want a shorthand method of tracking your own registered items.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the {@code BASIC_HANDHELD} template.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerBasicHandheldItem(ResourceLocation itemId, Supplier<I> itemSup, @Nullable Collection<Supplier<Item>> itemSupCol) {
        return registerItemFromTemplate(itemId, itemSup, BASIC_HANDHELD, itemSupCol);
    }

    /**
     * Overloaded variant of {@link #registerBasicHandheldItem(ResourceLocation, Supplier, Collection)} that does not
     * track the registered {@link Item} to any custom {@link Collection}.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     * @param itemSup The {@link Item} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the {@code BASIC_HANDHELD} template.
     *
     * @param <I> Any {@link Item} type.
     */
    public static <I extends Item> Supplier<I> registerBasicHandheldItem(ResourceLocation itemId, Supplier<I> itemSup) {
        return registerBasicHandheldItem(itemId, itemSup, null);
    }

    /**
     * Registers a new basic {@link Item} with default properties and returns it, mapped to a new
     * {@link ItemPropertyWrapper} inheriting from the {@link #BASIC_HANDHELD} template.
     *
     * @param itemId The target {@linkplain Item Item's} {@linkplain ResourceLocation registry ID}.
     *
     * @return The {@link Supplier} of the registered {@link Item}, mapped to its own {@link ItemPropertyWrapper}
     * inheriting from the {@code BASIC_HANDHELD} template.
     */
    public static Supplier<Item> registerBasicHandheldItem(ResourceLocation itemId) {
        return registerBasicHandheldItem(itemId, () -> new Item(new Item.Properties()));
    }
}
