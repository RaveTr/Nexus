package com.mememan.nexus.util;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Utility {@code class} containing miscellaneous game/data-related predicates for object filtration and/or otherwise
 * shortcut methods.
 */
public final class PredicateUtil {

    private PredicateUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (PredicateUtil)");
    }

    /**
     * Checks if a given {@link ItemStack} has a parent {@link CreativeModeTab} based on its parent
     * {@code PropertyWrapper}. Additionally, checks if the {@link CreativeModeTab} passed in contains a
     * {@linkplain CreativeModeTab#getDisplayItems() Display Item} matching the {@link ItemStack} passed in if all other
     * checks fail.
     *
     * @param stackToTest The {@link ItemStack} to check for a parent {@link CreativeModeTab}.
     * @param targetParentTab The {@link CreativeModeTab} to check for the provided child {@link ItemStack} against.
     *
     * @return {@code true} if the {@link ItemStack} has a parent {@link CreativeModeTab} matching the provided,
     * {@code false} otherwise.
     *
     * @apiNote This method relies on the {@link ItemStack} passed in to either be an already-valid member of a registered
     * {@link CreativeModeTab} or to have a valid {@code PropertyWrapper} associated with it that specifies the target
     * {@link CreativeModeTab} as a parent.
     */
    public static boolean hasParentTab(ItemStack stackToTest, Supplier<CreativeModeTab> targetParentTab) { // Necessary workaround for preservation of order. Works since display items are apparently cached when tabs are opened the first time.
        AtomicBoolean foundMatch = new AtomicBoolean(false);

        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class)
                .stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<?, ?, ?>) curPW)
                .filter(curPW -> Objects.equals(stackToTest.getItem(), curPW.getParentObject().get().asItem()) && curPW.getParentCreativeModeTabs().stream().anyMatch(parentTab -> Objects.equals(parentTab, targetParentTab.get())))
                .findFirst()
                .ifPresent(curPW -> foundMatch.set(true));

        if (!foundMatch.get()) foundMatch.set(targetParentTab.get().getDisplayItems().contains(stackToTest));

        return foundMatch.get();
    }

    /**
     * Predicate method representing an advancement criterion instance (in this case,
     * {@link InventoryChangeTrigger.TriggerInstance}) whose flags will only return {@code true} if and only if a given
     * {@linkplain Entity Entity's} (in this case, {@linkplain Player Player's}) inventory contains a set of items that
     * match the {@linkplain ItemPredicate ItemPredicates} passed in.
     *
     * @param predicates The array of {@linkplain ItemPredicate ItemPredicates} used to validate the contents of a
     *                   given {@linkplain Player Player's} inventory for this criterion instance to trigger.
     *
     * @return {@code true} if the given {@linkplain Player Player's} inventory contains any items that pass the
     * validation of each {@link ItemPredicate} passed in, otherwise returns {@code false}.
     */
    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
    }

    /**
     * Convenience overload of {@link #inventoryTrigger(ItemPredicate...)} that creates an advancement criterion
     * instance which triggers when a {@linkplain Player Player's} inventory contains the specified
     * {@linkplain ItemLike ItemLike}.
     *
     * @param targetItemLike The {@linkplain ItemLike ItemLike} that must be present in a given
     *                       {@linkplain Player Player's} inventory for this criterion instance to trigger.
     *
     * @return An {@link InventoryChangeTrigger.TriggerInstance} that triggers when the specified
     * {@code targetItemLike} is found in a {@linkplain Player Player's} inventory.
     *
     * @see #inventoryTrigger(ItemPredicate...)
     * @see #has(TagKey)
     */
    public static InventoryChangeTrigger.TriggerInstance has(ItemLike targetItemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(targetItemLike).build());
    }

    /**
     * Convenience overload of {@link #inventoryTrigger(ItemPredicate...)} that creates an advancement criterion
     * instance which triggers when a {@linkplain Player Player's} inventory contains an item from the specified
     * {@linkplain TagKey tag}.
     *
     * @param targetTag The {@linkplain TagKey tag} whose items must be present in a given
     *                  {@linkplain Player Player's} inventory for this criterion instance to trigger. Any item from
     *                  the specified tag suffices.
     *
     * @return An {@link InventoryChangeTrigger.TriggerInstance} that triggers when any item from the specified
     * {@code targetTag} is found in a {@linkplain Player Player's} inventory.
     *
     * @see #inventoryTrigger(ItemPredicate...)
     * @see #has(ItemLike)
     */
    public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> targetTag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(targetTag).build());
    }
}
