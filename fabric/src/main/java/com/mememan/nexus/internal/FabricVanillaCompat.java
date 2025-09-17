package com.mememan.nexus.internal;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.tag.TagWrapper;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Singleton {@code class} responsible for registering Vanilla compatibility features for blocks/items/tags on Fabric.
 */
public final class FabricVanillaCompat {

    /**
     * Internal method responsible for the registration of hardcoded Vanilla compatibility features from Block/Item/Tag
     * Property Wrappers. Additionally, handles registration of blocks and items to their respective
     * {@linkplain CreativeModeTab CreativeModeTabs}.
     *
     * @param <IL> Any {@link ItemLike} type. Primarily used for compile-time generic type safety.
     */
    public static <IL extends ItemLike> void registerVanillaCompat() {
        // General (ItemLikes)
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class)
                .stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<IL, ?, ?>) curPW)
                .forEach(curPW -> {
                    Supplier<IL> parentItemLikeSup = curPW.getParentObject();
                    IL parentItemLike = parentItemLikeSup.get();
                    Optional<Function<Supplier<IL>, Float>> compostMapperFunc = curPW.getCompostMapper();
                    Optional<Function<Supplier<IL>, Integer>> fuelMapperFunc = curPW.getFuelMapper();
                    List<Supplier<CreativeModeTab>> parentTabs = curPW.getParentCreativeModeTabs();

                    compostMapperFunc.ifPresent(compostMapper -> CompostingChanceRegistry.INSTANCE.add(parentItemLike, Math.abs(compostMapper.apply(parentItemLikeSup))));
                    fuelMapperFunc.ifPresent(fuelMapper -> FuelRegistry.INSTANCE.add(parentItemLike, Math.abs(fuelMapper.apply(parentItemLikeSup))));

                    if (!parentTabs.isEmpty()) {
                        ItemStack parentStack = parentItemLike.asItem().getDefaultInstance();

                        if (!parentStack.isEmpty()) {
                            parentTabs.forEach(parentTabSup -> {
                                CreativeModeTab parentTab = parentTabSup.get();

                                if (parentTab != null) {
                                    ResourceKey<CreativeModeTab> parentTabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(parentTab)
                                            .orElseThrow(() -> new IllegalArgumentException(String.format("Attempted to retrieve ResourceKey for unregistered or unmapped CreativeModeTab '%s'", parentTab.getDisplayName().getString())));

                                    ItemGroupEvents.modifyEntriesEvent(parentTabKey).register(tabEntries -> tabEntries.accept(parentStack));
                                }
                            });
                        }
                    }

                    if (curPW instanceof BlockPropertyWrapper<?> curBPW) { // Special handling for blocks
                        Supplier<? extends Block> parentBlockSup = curBPW.getParentObject();
                        Block parentBlock = parentBlockSup.get();


                    }
                });

        // Blocks
   /*     BlockPropertyWrapper.getMappedBpws().forEach((parentBlockSup, curBpw) -> {
            IntIntMutablePair flammabilityPair = curBpw.getFlammabilityMappingFunc() == null ? null : curBpw.getFlammabilityMappingFunc().apply(parentBlockSup);
            Supplier<Block> strippedBlockVariant = curBpw.getBlockStrippingMappingFunc() == null ? null : curBpw.getBlockStrippingMappingFunc().apply(parentBlockSup);
            ObjectObjectMutablePair<Predicate<UseOnContext>, Consumer<UseOnContext>> parentBlockTillingBehaviourPair = curBpw.getBlockTillingMappingFunc() == null ? null : curBpw.getBlockTillingMappingFunc().apply(parentBlockSup);
            BlockState flattenedBlockVariant = curBpw.getBlockFlatteningMappingFunc() == null ? null : curBpw.getBlockFlatteningMappingFunc().apply(parentBlockSup);
            Supplier<Block> oxidizedBlockVariant = curBpw.getBlockOxidizationMappingFunc() == null ? null : curBpw.getBlockOxidizationMappingFunc().apply(parentBlockSup);
            Supplier<Block> waxedBlockVariant = curBpw.getBlockWaxingMappingFunc() == null ? null : curBpw.getBlockWaxingMappingFunc().apply(parentBlockSup);
            Float blockCompostChance = curBpw.getBlockCompostingMappingFunc() == null ? null : Math.abs(curBpw.getBlockCompostingMappingFunc().apply(parentBlockSup));
            Integer blockFuelCookTime = curBpw.getBlockFuelMappingFunc() == null ? null : Math.abs(curBpw.getBlockFuelMappingFunc().apply(parentBlockSup));

            if (flammabilityPair != null) FlammableBlockRegistry.getDefaultInstance().add(parentBlockSup.get(), Math.abs(flammabilityPair.leftInt()), Math.abs(flammabilityPair.rightInt()));
            if (strippedBlockVariant != null && strippedBlockVariant.get() != null) StrippableBlockRegistry.register(parentBlockSup.get(), strippedBlockVariant.get());
            if (parentBlockTillingBehaviourPair != null && parentBlockTillingBehaviourPair.left() != null && parentBlockTillingBehaviourPair.right() != null) TillableBlockRegistry.register(parentBlockSup.get(), parentBlockTillingBehaviourPair.left(), parentBlockTillingBehaviourPair.right());
            if (flattenedBlockVariant != null) FlattenableBlockRegistry.register(parentBlockSup.get(), flattenedBlockVariant);
            if (oxidizedBlockVariant != null && oxidizedBlockVariant.get() != null) OxidizableBlocksRegistry.registerOxidizableBlockPair(parentBlockSup.get(), oxidizedBlockVariant.get());
            if (waxedBlockVariant != null && waxedBlockVariant.get() != null) OxidizableBlocksRegistry.registerWaxableBlockPair(parentBlockSup.get(), waxedBlockVariant.get());
            if (blockCompostChance != null && blockCompostChance != 0) CompostingChanceRegistry.INSTANCE.add(parentBlockSup.get(), blockCompostChance);
            if (blockFuelCookTime != null && blockFuelCookTime != 0) FuelRegistry.INSTANCE.add(parentBlockSup.get(), blockFuelCookTime);
        }); */

        // Tags
        TagWrapper.getCachedTWEntries().forEach(curTw -> {
            int tagFuelCookTime = Math.abs(curTw.getCookTime());
            IntIntMutablePair tagFlammabilitySettings = curTw.getFlammabilitySettings();
            TagKey<?> curTagKey = curTw.getParentTag().get();

            if (tagFuelCookTime != 0 && curTagKey.isFor(Registries.ITEM)) FuelRegistry.INSTANCE.add((TagKey<Item>) curTagKey, tagFuelCookTime);
            if (tagFlammabilitySettings != null && curTagKey.isFor(Registries.BLOCK)) FlammableBlockRegistry.getDefaultInstance().add((TagKey<Block>) curTagKey, Math.abs(tagFlammabilitySettings.leftInt()), Math.abs(tagFlammabilitySettings.rightInt()));
        });
    }
}
