package com.mememan.nexus.internal;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.entity.EntityTypePropertyWrapper;
import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;
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

                    curPW.getCompostMapper()
                            .filter(compostMapper -> compostMapper.apply(parentItemLikeSup) != null && !parentItemLike.asItem().getDefaultInstance().isEmpty())
                            .ifPresent(compostMapper -> CompostingChanceRegistry.INSTANCE.add(parentItemLike, Math.abs(compostMapper.apply(parentItemLikeSup))));
                    curPW.getFuelMapper()
                            .filter(fuelMapper -> fuelMapper.apply(parentItemLikeSup) != null && !parentItemLike.asItem().getDefaultInstance().isEmpty())
                            .ifPresent(fuelMapper -> FuelRegistry.INSTANCE.add(parentItemLike, Math.abs(fuelMapper.apply(parentItemLikeSup))));

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

        // EntityTypes
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(EntityTypePropertyWrapper.class)
                .stream()
                .map(curPW -> (EntityTypePropertyWrapper<?>) curPW)
                .filter(curPW -> !Objects.equals(curPW.getParentObject().get().getCategory(), MobCategory.MISC))
                .map(curPW -> (EntityTypePropertyWrapper<? extends LivingEntity>) curPW)
                .forEach(FabricVanillaCompat::registerEntityTypeAttributes);
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

            if (flammabilityProperties != null) {
                FlammableBlockRegistry.getDefaultInstance().add(parentBlock, Math.abs(flammabilityProperties.leftInt()), Math.abs(flammabilityProperties.rightInt()));
            }
        });
        targetBPW.getBlockStrippingMapper().ifPresent(strippedBlockMapper -> {
            BlockState strippedParentState = strippedBlockMapper.apply(parentBlockSup);

            if (strippedParentState != null) StrippableBlockRegistry.register(parentBlock, strippedParentState.getBlock());
        });
        targetBPW.getBlockTillingMapper().ifPresent(tillingMapper -> {
            Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> mappedTillingBehaviourPair = tillingMapper.apply(parentBlockSup);

            if (mappedTillingBehaviourPair != null) {
                Predicate<UseOnContext> ctxTillingPredicate = mappedTillingBehaviourPair.left();
                Consumer<UseOnContext> ctxTillingAction = mappedTillingBehaviourPair.right();

                if (ctxTillingPredicate != null && ctxTillingAction != null) {
                    TillableBlockRegistry.register(parentBlock, ctxTillingPredicate, ctxTillingAction);
                }
            }
        });
        targetBPW.getBlockFlatteningMapper().ifPresent(flatteningMapper -> {
            BlockState flattenedState = flatteningMapper.apply(parentBlockSup);

            if (flattenedState != null) FlattenableBlockRegistry.register(parentBlock, flattenedState);
        });
        targetBPW.getBlockOxidizationMapper().ifPresent(oxidizationMapper -> {
            Supplier<Block> oxidizedParentBlockSup = oxidizationMapper.apply(parentBlockSup);
            Block oxidizedParentBlock = oxidizedParentBlockSup == null ? null : oxidizedParentBlockSup.get();

            if (oxidizedParentBlock != null) OxidizableBlocksRegistry.registerOxidizableBlockPair(parentBlock, oxidizedParentBlock);
        });
        targetBPW.getBlockWaxingMapper().ifPresent(waxingMapper -> {
            Supplier<Block> waxedParentBlockSup = waxingMapper.apply(parentBlockSup);
            Block waxedParentBlock = waxedParentBlockSup == null ? null : waxedParentBlockSup.get();

            if (waxedParentBlock != null) OxidizableBlocksRegistry.registerWaxableBlockPair(parentBlock, waxedParentBlock);
        });
    }

    /**
     * Handles the registration of entity type attributes for a given {@link EntityTypePropertyWrapper}. Assumes the parent
     * {@link EntityType} extends {@link LivingEntity}, as per the generic defined in this method.
     *
     * @param targetWrapper The wrapper whose parent {@link EntityType} should be validated and attributes registered
     *                      accordingly.
     *
     * @param <LE> Any {@link LivingEntity} type.
     */
    private static <LE extends LivingEntity> void registerEntityTypeAttributes(EntityTypePropertyWrapper<LE> targetWrapper) {
        targetWrapper.getEntityTypeAttributes().ifPresent(entityTypeAttributes -> {
            AttributeSupplier.Builder attribBuilder = entityTypeAttributes.get();

            if (attribBuilder != null) FabricDefaultAttributeRegistry.register(targetWrapper.getParentObject().get(), attribBuilder);
        });
    }
}
