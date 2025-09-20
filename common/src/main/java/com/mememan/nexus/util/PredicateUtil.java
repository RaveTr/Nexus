package com.mememan.nexus.util;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

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
}
