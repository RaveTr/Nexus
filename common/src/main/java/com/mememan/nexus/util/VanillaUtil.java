package com.mememan.nexus.util;

import com.mememan.nexus.client.block.WrappedBlockColor;
import com.mememan.nexus.template.object.item.dispense_item_behaviour.DefaultableBoatDispenseItemBehaviour;
import com.mememan.nexus.template.object.item.entity.boat.BoatType;
import com.mememan.nexus.template.object.item.entity.boat.DefaultableBoatItem;
import com.mememan.nexus.template.property_wrapper.BlockPropertyWrapperTemplates;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility {@code class} containing helpful vanilla compat shortcut/delegator helper methods.
 */
public final class VanillaUtil {

    private VanillaUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (VanillaUtil)");
    }

    /**
     * Calculates standard flammability values for wooden + other blocks, following the flammability property patterns
     * used in Minecraft's {@link FireBlock}. Returns an {@link IntIntMutablePair} where the first value represents the
     * ignition value (how easily the block catches fire) and the second value represents the burn value (probability
     * the block gets consumed by the fire).
     * <p>
     *     <h3>Flammability Patterns</h3>
     *     <ul>
     *         <li>Vegetation blocks ({@link FlowerBlock}/{@link TallFlowerBlock}/{@link TallGrassBlock} instances):
     *         (60, 100) - High flammability</li>
     *         <li>Leaf blocks (description ID ending with {@code "_leaves"}/{@code "_wool"} or {@link LeavesBlock} instances):
     *         (30, 60) - High flammability</li>
     *         <li>Carpet blocks (description ID ending with {@code "_carpet"} or {@link CarpetBlock} instances):
     *         (60, 20) - Medium flammability</li>
     *         <li>Wood log blocks (description ID ending with {@code "_log"}): (5, 5) - Low flammability</li>
     *         <li>All other (presumably wooden) blocks: (5, 20) - Low flammability</li>
     *     </ul>
     *
     * @param targetBlock The {@link Supplier} of the target {@link Block} to calculate flammability values for.
     *
     * @return An {@link IntIntMutablePair} containing the encouragement value (first) and flammability value (second).
     *
     * @see FireBlock
     * @see LeavesBlock
     * @see CarpetBlock
     */
    public static IntIntMutablePair standardFlammability(Supplier<Block> targetBlock) {
        Block targetBlockObj = targetBlock.get();

        return targetBlockObj instanceof TallGrassBlock || targetBlockObj instanceof FlowerBlock || targetBlockObj instanceof TallFlowerBlock
                ? IntIntMutablePair.of(60, 100)
                : targetBlockObj.getDescriptionId().endsWith("_leaves") || targetBlockObj instanceof LeavesBlock || targetBlockObj.getDescriptionId().endsWith("_wool")
                ? IntIntMutablePair.of(30, 60)
                : targetBlockObj.getDescriptionId().endsWith("_carpet") || targetBlockObj instanceof CarpetBlock
                ? IntIntMutablePair.of(60, 20)
                : targetBlockObj.getDescriptionId().endsWith("_log")
                ? IntIntMutablePair.of(5, 5)
                : IntIntMutablePair.of(5, 20);
    }

    /**
     * Creates a stripped log variant for the given block if a corresponding stripped version exists.
     * <p>
     * The method attempts to find the corresponding stripped log block by:
     * <ol>
     *     <li>Removing the "_wood" suffix from the block's ID and replacing it with "_log", if applicable</li>
     *     <li>Prefixing the block's ID with "stripped_"</li>
     * </ol>
     *
     * @param targetBlockState The {@link BlockState} representing the log block to create stripping behavior for, passed
     *                         as a state to allow for axis property retention.
     *
     * @return The {@link BlockState} of the corresponding stripped log block, or {@code null} if no matching stripped variant exists.
     *
     * @see #standardFlammability(Supplier)
     * @see BlockPropertyWrapperTemplates#WOODEN_LOG
     */
    public static BlockState standardWoodLogStrippingState(BlockState targetBlockState) {
        return RegistryUtil.getObjectFrom(targetBlockState.getBlock(), parentBlockId -> parentBlockId.getPath().endsWith("_wood")
                        ? RegistryUtil.pickPrefix(parentBlockId.withPath(parentBlockId.getPath().replace("_wood", "_log")), "stripped_")
                        : RegistryUtil.pickPrefix(parentBlockId, "stripped_"))
                .map(strippedState -> strippedState.defaultBlockState().setValue(RotatedPillarBlock.AXIS, targetBlockState.getValue(RotatedPillarBlock.AXIS)))
                .orElse(null);
    }

    /**
     * Creates a {@link Pair} containing the tilling behavior for converting dirt to farmland using a hoe.
     * The returned pair consists of a predicate that checks if tilling can occur (air above) and a consumer
     * that performs the tilling action by converting the block to its corresponding farmland variant.
     * <p>
     * The method attempts to find the corresponding farmland block by:
     * <ol>
     *     <li>Replacing {@code _dirt} suffix with {@code _farmland}</li>
     *     <li>Removing {@code _farmland} suffix if the first attempt fails</li>
     * </ol>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the dirt {@link Block} to create tilling behavior for.
     *
     * @return A {@link Pair} containing the tilling predicate (first) and consumer (second), or {@code null} if no
     *         corresponding farmland block is found.
     *
     * @see #grassBlockFarmlandTillingAction(Supplier)
     * @see HoeItem#onlyIfAirAbove
     * @see HoeItem#changeIntoState(BlockState)
     */
    public static Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> dirtFarmlandTillingAction(Supplier<Block> targetBlock) {
        Function<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> tillingBehaviourMapper = parentBlock -> Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(parentBlock.defaultBlockState()));

        return RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_dirt", "_farmland")))
                .or(() -> RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_farmland", ""))))
                .map(tillingBehaviourMapper)
                .orElse(null);
    }

    /**
     * Creates a {@link Pair} containing the tilling behavior for converting grass block to farmland using a hoe.
     * The returned pair consists of a predicate that checks if tilling can occur (air above) and a consumer
     * that performs the tilling action by converting the block to its corresponding farmland variant.
     * <p>
     * The method attempts to find the corresponding farmland block by:
     * <ol>
     *     <li>Replacing {@code _grass_block} suffix with {@code _farmland}</li>
     *     <li>Removing {@code _farmland} suffix if the first attempt fails</li>
     * </ol>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the dirt {@link Block} to create tilling behavior for.
     *
     * @return A {@link Pair} containing the tilling predicate (first) and consumer (second), or {@code null} if no
     *         corresponding farmland block is found.
     *
     * @see #dirtFarmlandTillingAction(Supplier)
     * @see HoeItem#onlyIfAirAbove
     * @see HoeItem#changeIntoState(BlockState)
     */
    public static Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> grassBlockFarmlandTillingAction(Supplier<Block> targetBlock) {
        Function<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> tillingBehaviourMapper = parentBlock -> Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(parentBlock.defaultBlockState()));

        return RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_grass_block", "_farmland")))
                .or(() -> RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_farmland", ""))))
                .map(tillingBehaviourMapper)
                .orElse(null);
    }

    /**
     * Creates the flattening behavior for converting dirt to path blocks when flattened by shovel. Attempts to find the
     * corresponding path block by:
     * <ol>
     *     <li>Replacing {@code _dirt} suffix with {@code _path}</li>
     *     <li>Removing {@code _path} suffix if the first attempt fails</li>
     * </ol>
     *
     * @param targetBlockState The {@link BlockState} representing the dirt {@link Block} to create path flattening for.
     *
     * @return The {@link BlockState} of the corresponding path block, or {@code null} if no corresponding path block is found.
     */
    public static BlockState dirtPathFlatteningAction(BlockState targetBlockState) {
        return RegistryUtil.getObjectFrom(targetBlockState.getBlock(), parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_dirt", "_path")))
                .or(() -> RegistryUtil.getObjectFrom(targetBlockState.getBlock(), parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_path", ""))))
                .map(Block::defaultBlockState)
                .orElse(null);
    }

    /**
     * Creates the flattening behavior for converting grass blocks to path blocks when flattened by shovel.
     * The method attempts to find the corresponding path block by:
     * <ol>
     *     <li>Replacing {@code _grass_block} suffix with {@code _path}</li>
     *     <li>Removing {@code _path} suffix if the first attempt fails</li>
     * </ol>
     *
     * @param targetBlockState The {@link BlockState} representing the grass {@link Block} to create path flattening for.
     *
     * @return The {@link BlockState} of the corresponding path block, or {@code null} if no corresponding path block is found.
     */
    public static BlockState grassBlockPathFlatteningAction(BlockState targetBlockState) {
        return RegistryUtil.getObjectFrom(targetBlockState.getBlock(), parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_grass_block", "_path")))
                .or(() -> RegistryUtil.getObjectFrom(targetBlockState.getBlock(), parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_grass_block", ""))))
                .map(Block::defaultBlockState)
                .orElse(null);
    }

    /**
     * Creates a {@link WrappedBlockColor} for the given block, using Vanilla grass color logic.
     * <br></br>
     * The returned block color mapper uses biome colors to calculate the grass color based on the block's position
     * (biome determination). If the block is not a grass block, it returns the default grass color (0.5, 1.0).
     *
     * @param targetBlock The {@link Supplier<Block>} representing the block to create the block color mapper for.
     *
     * @return The {@link WrappedBlockColor} for the given block, using the vanilla grass color logic.
     */
    @NotNull
    public static WrappedBlockColor standardGrassColor(Supplier<Block> targetBlock) {
        return (targetState, tintGetter, targetPos, tint) -> tintGetter != null && targetPos != null
                ? BiomeColors.getAverageGrassColor(tintGetter, targetPos)
                : GrassColor.get(0.5D, 1.0D);
    }

    public static <IL extends ItemLike> DispenseItemBehavior standardBoatDispenseBehavior(Supplier<IL> targetItemLike) {
        return new DefaultableBoatDispenseItemBehaviour(targetItemLike.get() instanceof DefaultableBoatItem defBoatItem ? defBoatItem.getBoatType() : BoatType.OAK);
    }

    public static <IL extends ItemLike> DispenseItemBehavior standardChestBoatDispenseBehavior(Supplier<IL> targetItemLike) {
        return new DefaultableBoatDispenseItemBehaviour(targetItemLike.get() instanceof DefaultableBoatItem defBoatItem ? defBoatItem.getBoatType() : BoatType.OAK, true);
    }
}
