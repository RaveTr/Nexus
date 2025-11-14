package com.mememan.nexus.internal;

import com.mememan.nexus.internal.event.common.NexusForgeCommonMiscEvents;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.vanilla.VanillaBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;

import java.util.function.Supplier;

/**
 * Singleton {@code class} responsible for registering Vanilla compatibility features for blocks/items/tags on Forge.
 */
public final class ForgeVanillaCompat {

    /**
     * Internal method responsible for the registration of hardcoded Vanilla compatibility features from Block/Item
     * Property Wrappers.
     *
     * @apiNote Tool actions (stripping, tilling, flattening) and fuel + flammability are handled separately in their
     * corresponding events, see references below.
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

                    curPW.getCompostMapper()
                            .filter(compostMapper -> compostMapper.apply(parentItemLikeSup) != null && !parentItemLike.asItem().getDefaultInstance().isEmpty())
                            .ifPresent(compostMapper -> ComposterBlock.COMPOSTABLES.put(parentItemLike.asItem(), Math.abs(compostMapper.apply(parentItemLikeSup))));
                    curPW.getDispenseBehaviourMapper()
                            .filter(dispenseBehaviourMapper -> dispenseBehaviourMapper.apply(parentItemLikeSup) != null)
                            .ifPresent(dispenseBehaviourMapper -> DispenserBlock.registerBehavior(parentItemLike.asItem(), dispenseBehaviourMapper.apply(parentItemLikeSup)));

                    if (curPW instanceof BlockPropertyWrapper<?> curBPW) registerBlockVanillaIntegration(curBPW);
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

            if (flammabilityProperties != null) {
                ((FireBlock) Blocks.FIRE).setFlammable(parentBlock, Math.abs(flammabilityProperties.leftInt()), Math.abs(flammabilityProperties.rightInt())); // No need to act stingy and safe about it here in this case since blocks aren't reloadable resources in 1.20.1 (duh)
            }
        });
        targetBPW.getBlockOxidizationMapper().ifPresent(oxidizationMapper -> {
            Supplier<Block> oxidizedParentBlockSup = oxidizationMapper.apply(parentBlockSup);
            Block oxidizedParentBlock = oxidizedParentBlockSup == null ? null : oxidizedParentBlockSup.get();

            if (oxidizedParentBlock != null) WeatheringCopper.NEXT_BY_BLOCK.get().put(parentBlock, oxidizedParentBlock);
        });
        targetBPW.getBlockWaxingMapper().ifPresent(waxingMapper -> {
            Supplier<Block> waxedParentBlockSup = waxingMapper.apply(parentBlockSup);
            Block waxedParentBlock = waxedParentBlockSup == null ? null : waxedParentBlockSup.get();

            if (waxedParentBlock != null) HoneycombItem.WAXABLES.get().put(parentBlock, waxedParentBlock);
        });
    }
}
