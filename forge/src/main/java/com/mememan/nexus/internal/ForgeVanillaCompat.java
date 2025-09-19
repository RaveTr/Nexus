package com.mememan.nexus.internal;

import com.mememan.nexus.internal.event.common.NexusForgeCommonMiscEvents;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapper;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Singleton {@code class} responsible for registering Vanilla compatibility features for blocks/items/tags on Forge.
 */
public final class ForgeVanillaCompat {

    /**
     * Internal method responsible for the registration of hardcoded Vanilla compatibility features from Block/Item/Tag
     * Property Wrappers.
     *
     * @apiNote Tool actions (stripping, tilling, flattening) and fuel are handled separately in their corresponding events.
     *
     * @param <IL> Any {@link ItemLike} type. Primarily used for compile-time generic type safety.
     *
     * @see NexusForgeCommonMiscEvents
     */
    public static <IL extends ItemLike> void registerVanillaIntegration() {
        // General (ItemLikes)
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(VanillaBasedPropertyWrapper.class)
                .stream()
                .map(curPW -> (VanillaBasedPropertyWrapper<IL, ?, ?>) curPW)
                .forEach(curPW -> {
                    Supplier<IL> parentItemLikeSup = curPW.getParentObject();
                    IL parentItemLike = parentItemLikeSup.get();

                    curPW.getCompostMapper().ifPresent(compostMapper -> ComposterBlock.COMPOSTABLES.put(parentItemLike, Math.abs(compostMapper.apply(parentItemLikeSup))));

                    if (curPW instanceof BlockPropertyWrapper<?> curBPW) registerBlockVanillaIntegration(curBPW);
                });

        // Tags (Blocks)
        PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(TagPropertyWrapper.class)
                .stream()
                .map(curPW -> (TagPropertyWrapper<?, ? extends TagKey<?>>) curPW)
                .forEach(curPW -> {
                    Optional<IntIntMutablePair> tagFlammabilityProperties = curPW.getFlammabilityPair();
                    Supplier<? extends TagKey<?>> parentTagKeySup = curPW.getParentObject();
                    TagKey<?> parentTagKey = parentTagKeySup.get();

                    tagFlammabilityProperties
                            .filter(flammabilityProperties -> parentTagKey.isFor(Registries.BLOCK))
                            .ifPresent(flammabilityProperties -> BuiltInRegistries.BLOCK.getTagOrEmpty((TagKey<Block>) parentTagKey).forEach(blockEntryHolder -> ((FireBlock) Blocks.FIRE).setFlammable(blockEntryHolder.get(), Math.abs(flammabilityProperties.leftInt()), Math.abs(flammabilityProperties.rightInt()))));
                });
    }

    /**
     * Handles explicit {@link Block} compat with Vanilla features using the provided {@link BlockPropertyWrapper}. This
     * includes flammability, oxidation, and waxing.
     *
     * @param targetBPW The {@link BlockPropertyWrapper} whose properties should be registered.
     *
     * @param <B> Any {@link Block} type. Primarily used for compile-time generic type safety.
     *
     * @see #registerVanillaIntegration()
     */
    private static <B extends Block> void registerBlockVanillaIntegration(BlockPropertyWrapper<B> targetBPW) {
        Supplier<B> parentBlockSup = targetBPW.getParentObject();
        B parentBlock = parentBlockSup.get();

        targetBPW.getFlammabilityMapper().ifPresent(flammabilityMapper -> {
            IntIntMutablePair flammabilityProperties = flammabilityMapper.apply(parentBlockSup);

            ((FireBlock) Blocks.FIRE).setFlammable(parentBlock, Math.abs(flammabilityProperties.leftInt()), Math.abs(flammabilityProperties.rightInt()));
        });
        targetBPW.getBlockOxidizationMapper().ifPresent(oxidizationMapper -> WeatheringCopper.NEXT_BY_BLOCK.get().put(parentBlock, oxidizationMapper.apply(parentBlockSup).get()));
        targetBPW.getBlockWaxingMapper().ifPresent(waxingMapper -> HoneycombItem.WAXABLES.get().put(parentBlock, waxingMapper.apply(parentBlockSup).get()));
    }
}
