package com.mememan.nexus.util;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

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
     * Calculates standard flammability values for wooden + other blocks following the flammability property patterns used in
     * Minecraft's {@link net.minecraft.world.level.block.FireBlock}. Returns an {@link IntIntMutablePair} where the first
     * value represents the ignition value (how easily the block catches fire) and the second value represents
     * the burn value (probability the block gets consumed by the fire).
     * <p>
     *     <h3>Flammability Patterns</h3>
     *     <ul>
     *         <li>Vegetation blocks ({@link FlowerBlock}/{@link TallGrassBlock} instances): (60, 100) - High flammability</li>
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

        return targetBlockObj instanceof TallGrassBlock || targetBlockObj instanceof FlowerBlock
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
     * Creates the flattening behavior for converting dirt to path blocks when walked on by entities.
     * The method attempts to find the corresponding path block by:
     * <ol>
     *     <li>Replacing {@code _dirt} suffix with {@code _path}</li>
     *     <li>Removing {@code _path} suffix if the first attempt fails</li>
     * </ol>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the dirt {@link Block} to create path flattening for.
     *
     * @return The {@link BlockState} of the corresponding path block, or {@code null} if no corresponding path block is found.
     */
    public static BlockState dirtPathFlatteningAction(Supplier<Block> targetBlock) {
        return RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_dirt", "_path")))
                .or(() -> RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_path", ""))))
                .map(Block::defaultBlockState)
                .orElse(null);
    }
}
