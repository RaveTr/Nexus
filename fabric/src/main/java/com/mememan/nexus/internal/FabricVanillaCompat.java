package com.mememan.nexus.internal;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Singleton {@code class} responsible for registering Vanilla compatibility features for blocks/items/tags on Fabric.
 */
public final class FabricVanillaCompat {

    /**
     * Internal method responsible for the registration of hardcoded Vanilla compatibility features from Block/Item/Tag
     * Property Wrappers. Additionally, handles registration of {@linkplain ItemLike ItemLikes} to their respective
     * {@linkplain CreativeModeTab CreativeModeTabs}.
     *
     * @param <IL> Any {@link ItemLike} type. Primarily used for compile-time generic type safety.
     *
     * @see #registerBlockVanillaCompat(BlockPropertyWrapper)
     */
    public static <IL extends ItemLike> void registerVanillaCompat() {
        // General (ItemLikes)
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class)
                .stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<IL, ?, ?>) curPW)
                .forEach(curPW -> {
                    Supplier<IL> parentItemLikeSup = curPW.getParentObject();
                    IL parentItemLike = parentItemLikeSup.get();

                    curPW.getCompostMapper().ifPresent(compostMapper -> CompostingChanceRegistry.INSTANCE.add(parentItemLike, Math.abs(compostMapper.apply(parentItemLikeSup))));
                    curPW.getFuelMapper().ifPresent(fuelMapper -> FuelRegistry.INSTANCE.add(parentItemLike, Math.abs(fuelMapper.apply(parentItemLikeSup))));

                    List<Supplier<CreativeModeTab>> parentTabs = curPW.getParentCreativeModeTabs();

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

                    if (curPW instanceof BlockPropertyWrapper<?> curBPW) registerBlockVanillaCompat(curBPW);
                });

        // Tags (Blocks/Items)
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(TagPropertyWrapper.class)
                .stream()
                .map(curPW -> (TagPropertyWrapper<?, ? extends TagKey<?>>) curPW)
                .forEach(curPW -> {
                    Optional<Integer> tagCookTime = curPW.getCookTime();
                    Optional<IntIntMutablePair> tagFlammabilityProperties = curPW.getFlammabilityPair();
                    Supplier<? extends TagKey<?>> parentTagKeySup = curPW.getParentObject();
                    TagKey<?> parentTagKey = parentTagKeySup.get();

                    tagCookTime
                            .filter(cookTime -> parentTagKey.isFor(Registries.ITEM))
                            .ifPresent(cookTime -> FuelRegistry.INSTANCE.add((TagKey<Item>) parentTagKey, Math.abs(cookTime)));
                    tagFlammabilityProperties
                            .filter(flammabilityProperties -> parentTagKey.isFor(Registries.BLOCK))
                            .ifPresent(flammabilityProperties -> FlammableBlockRegistry.getDefaultInstance().add((TagKey<Block>) parentTagKey, Math.abs(flammabilityProperties.leftInt()), Math.abs(flammabilityProperties.rightInt())));
                });
    }

    /**
     * Handles explicit {@link Block} compat with Vanilla features using the provided {@link BlockPropertyWrapper}. This
     * includes flammability, stripping, tilling, flattening, oxidation, and waxing.
     *
     * @param targetBPW The {@link BlockPropertyWrapper} whose properties should be registered.
     *
     * @param <B> Any {@link Block} type. Primarily used for compile-time generic type safety.
     */
    private static <B extends Block> void registerBlockVanillaCompat(BlockPropertyWrapper<B> targetBPW) {
        Supplier<B> parentBlockSup = targetBPW.getParentObject();
        B parentBlock = parentBlockSup.get();

        targetBPW.getFlammabilityMapper().ifPresent(flammabilityMapper -> {
            IntIntMutablePair flammabilityProperties = flammabilityMapper.apply(parentBlockSup);

            FlammableBlockRegistry.getDefaultInstance().add(parentBlock, Math.abs(flammabilityProperties.leftInt()), Math.abs(flammabilityProperties.rightInt()));
        });
        targetBPW.getBlockStrippingMapper().ifPresent(strippedBlockMapper -> StrippableBlockRegistry.register(parentBlock, strippedBlockMapper.apply(parentBlockSup).getBlock()));
        targetBPW.getBlockTillingMapper().ifPresent(tillingMapper -> {
            Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> mappedTillingBehaviourPair = tillingMapper.apply(parentBlockSup);

            TillableBlockRegistry.register(parentBlock, mappedTillingBehaviourPair.left(), mappedTillingBehaviourPair.right());
        });
        targetBPW.getBlockFlatteningMapper().ifPresent(flatteningMapper -> FlattenableBlockRegistry.register(parentBlock, flatteningMapper.apply(parentBlockSup)));
        targetBPW.getBlockOxidizationMapper().ifPresent(oxidizationMapper -> OxidizableBlocksRegistry.registerOxidizableBlockPair(parentBlock, oxidizationMapper.apply(parentBlockSup).get()));
        targetBPW.getBlockWaxingMapper().ifPresent(waxingMapper -> OxidizableBlocksRegistry.registerWaxableBlockPair(parentBlock, waxingMapper.apply(parentBlockSup).get()));
    }
}
