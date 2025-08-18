package com.mememan.nexus.util;

import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.item.standard.ItemPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

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

        ItemPropertyWrapper.getMappedIpws().forEach((itemSup, ipwEntry) -> {
            if (foundMatch.get()) return; // Break

            foundMatch.set(itemSup.get().getDescriptionId().equals(stackToTest.getItem().getDescriptionId()) && ipwEntry.getParentCreativeModeTabs().contains(targetParentTab));
        });

        if (!foundMatch.get()) { // Avoid unnecessary lookup computation if the specified stack has already been matched with the specified parent tab
            BlockPropertyWrapper.getMappedBpws().forEach((blockSup, bpwEntry) -> {
                if (foundMatch.get()) return; // Break

                foundMatch.set(blockSup.get().asItem().getDescriptionId().equals(stackToTest.getItem().getDescriptionId()) && bpwEntry.getParentCreativeModeTabs().contains(targetParentTab));
            });
        }

        if (!foundMatch.get()) foundMatch.set(targetParentTab.get().getDisplayItems().contains(stackToTest));

        return foundMatch.get();
    }

    /**
     *
     *
     * @param propertyWrapper
     * @param builderType
     * @return
     * @param <T>
     * @param <PW>
     * @param <PB>
     */
    public static <T, PW extends PropertyWrapper<T, PW, PB>, PB extends PropertyWrapperBuilder<T, PB, PW>> boolean validateBuilderForWrapper(PW propertyWrapper, Class<PB> builderType) {
        return propertyWrapper != null && !propertyWrapper.getDisabledBuilderTypes().contains(builderType);
    }
}
