package com.mememan.nexus.util;

import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utility {@code class} that provides convenient shortcut/helper methods for constructing re-used {@link LootTable}
 * patterns.
 */
public final class LootUtil {
    public static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
    public static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
    public static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
    public static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
    public static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
    public static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build());
    public static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[] {0.05F, 0.0625F, 0.083333336F, 0.1F};
    public static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[] {0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

    private LootUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (LootUtil)");
    }

    /**
     * Creates a {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link ExplosionCondition#survivesExplosion()}</li>
     *  <li><b>Drops:</b> {@code targetBlock}</li>
     * </ul>
     *
     * @param targetBlock The {@code Supplier<Block>} representing the {@link Block} that will be dropped.
     *
     * @return A {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed.
     */
    public static LootTable.Builder dropSelf(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(ExplosionCondition.survivesExplosion())
                .add(LootItem.lootTableItem(targetBlock.get())));
    }

    /**
     * Creates {@link LootTable.Builder} that will treat the given {@link Block} as a slab and drop it based on whether
     * it's a half or double slab when it's destroyed, if it has the {@link SlabBlock#TYPE} property.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>Applies:</b> {@link ApplyExplosionDecay#explosionDecay()}</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>Applies:</b> {@link SetItemCountFunction#setCount(NumberProvider)} (Drops 2.0F)</li>
     *      <li><b>When:</b> {@link LootItemBlockStatePropertyCondition#hasBlockStateProperties(Block)} (Has
     *      {@link SlabBlock#TYPE}, and it's {@link SlabType#DOUBLE})</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be treated as a slab on drop.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as a slab and drop it based on whether
     * it's a half or double slab when it's destroyed.
     */
    public static LootTable.Builder dropSlab(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .apply(ApplyExplosionDecay.explosionDecay())
                .add(LootItem.lootTableItem(targetBlock.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(targetBlock.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))));
    }

    /**
     * Creates {@link LootTable.Builder} that will treat the given {@link Block} as a door and drop it based on whether
     * it has its lower half, if it has the {@link DoorBlock#HALF} property.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>Applies:</b> {@link ApplyExplosionDecay#explosionDecay()}</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>When:</b> {@link LootItemBlockStatePropertyCondition#hasBlockStateProperties(Block)} (Has
     *      {@link DoorBlock#HALF}, and it's {@link DoubleBlockHalf#LOWER})</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be treated as a door on
     *                    drop.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as a door and drop it based on whether
     * it has its lower half, if it has the {@link DoorBlock#HALF} property.
     */
    public static LootTable.Builder dropDoor(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .apply(ApplyExplosionDecay.explosionDecay())
                .add(LootItem.lootTableItem(targetBlock.get())
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(targetBlock.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)))));
    }

    /**
     * Creates {@link LootTable.Builder} that will treat the given {@link Block} as a bed and drop it based on whether
     * it has its head, if it has the {@link BedBlock#PART} property.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>Applies:</b> {@link ApplyExplosionDecay#explosionDecay()}</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>When:</b> {@link LootItemBlockStatePropertyCondition#hasBlockStateProperties(Block)} (Has
     *      {@link BedPart#HEAD}, and it's {@link BedBlock#PART})</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be treated as a bed on
     *                    drop.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as a bed and drop it based on whether
     * it has its head, if it has the {@link BedBlock#PART} property.
     */
    public static LootTable.Builder dropBed(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .apply(ApplyExplosionDecay.explosionDecay())
                .add(LootItem.lootTableItem(targetBlock.get())
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(targetBlock.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(BedBlock.PART, BedPart.HEAD)))));
    }

    /**
     * Creates {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed, but only if it's
     * destroyed with shears.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link #HAS_SHEARS}</li>
     *  <li><b>Drops:</b> {@code targetBlock}</li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be dropped when shears
     *                    are used to mine it.
     *
     * @return A {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed, but only if it's
     * destroyed with shears.
     */
    public static LootTable.Builder dropShearsOnly(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(HAS_SHEARS)
                .add(LootItem.lootTableItem(targetBlock.get())));
    }

    /**
     * Creates {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed, but only if it's
     * destroyed with a tool enchanted with silk touch.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link #HAS_SILK_TOUCH}</li>
     *  <li><b>Drops:</b> {@code targetBlock}</li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be dropped when any silk
     *                    touch tool is used to mine it.
     *
     * @return A {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed, but only if it's
     * destroyed with a tool enchanted with silk touch.
     */
    public static LootTable.Builder dropSilkTouchOnly(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(HAS_SILK_TOUCH)
                .add(LootItem.lootTableItem(targetBlock.get())));
    }

    /**
     * Creates {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed, but only if it's
     * destroyed with shears or a tool enchanted with silk touch.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link #HAS_SHEARS_OR_SILK_TOUCH}</li>
     *  <li><b>Drops:</b> {@code targetBlock}</li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be dropped when any shears
     *                    or silk touch tool is used to mine it.
     *
     * @return A {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed, but only if it's
     * destroyed with a shears or any tool enchanted with silk touch.
     */
    public static LootTable.Builder dropSilkTouchOrShears(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(HAS_SHEARS_OR_SILK_TOUCH)
                .add(LootItem.lootTableItem(targetBlock.get())));
    }

    /**
     * Creates a {@link LootTable.Builder} that will not drop anything. No loot table.
     *
     * @param targetBlock Dummy parameter for convenient function-lambda method reference.. convenience.
     *
     * @return A {@link LootTable.Builder} that will not drop anything.
     */
    public static LootTable.Builder noDrop(Supplier<Block> targetBlock) {
        return LootTable.lootTable();
    }

    /**
     * Creates {@link LootTable.Builder} that will drop the given {@link Block} when it's destroyed, but only if it's
     * destroyed with shears or a tool enchanted with silk touch. Alternatively drops saplings/sticks when
     * mined/decaying otherwise. Optionally assumes that the provided {@code targetBlock} has a sapling pertaining to it.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link #HAS_SHEARS_OR_SILK_TOUCH}</li>
     *  <li><b>Drops:</b> {@code targetBlock}</li>
     *  <li><b>Otherwise: [Optional, based on presence of corresponding sapling]</b> <ul>
     *      <li><b>When:</b> {@link BonusLevelTableCondition#bonusLevelFlatChance(Enchantment, float...)} (Passes in
     *      {@link Enchantments#BLOCK_FORTUNE} and {@link #NORMAL_LEAVES_SAPLING_CHANCES} respectively)</li></li>
     *      <li><b>When:</b> {@link ExplosionCondition#survivesExplosion()}</li></li>
     *      <li><b>Drops:</b> {@code targetBlock} or
     *      {@code RegistryUtil.getObjectFrom(targetBlock.get(), RegistryUtil.replaceSuffix("sapling"))} if present</li></li>
     *  </ul>
     * </ul>
     *
     * <h3>Pool 2</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link #HAS_NO_SHEARS_OR_SILK_TOUCH}</li>
     *  <li><b>Drops:</b> {@link Items#STICK} -> <ul>
     *      <li><b>When:</b> {@link BonusLevelTableCondition#bonusLevelFlatChance(Enchantment, float...)} (Passes in
     *      {@link Enchantments#BLOCK_FORTUNE} and {@link #NORMAL_LEAVES_STICK_CHANCES} respectively)</li></li>
     *      <li><b>Apply:</b> {@link ApplyExplosionDecay#explosionDecay()}</li></li>
     *      <li><b>Apply:</b> {@link SetItemCountFunction#setCount(NumberProvider)} (Passes in
     *      {@link UniformGenerator#between(float, float)} [1.0F, 2.0F])</li></li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the leaf {@link Block} that will be dropped when any
     *                    shears or silk touch tool is used to mine it. Alternatively drops saplings/sticks when
     *                    mined/decaying otherwise.
     *
     * @return A {@link LootTable.Builder} that will drop the given leaf {@link Block} when it's destroyed, but only if
     * it's destroyed with a shears or any tool enchanted with silk touch. Alternatively drops saplings/sticks when
     * mined/decaying.
     */
    public static LootTable.Builder dropLeaves(Supplier<Block> targetBlock) {
        LootPoolSingletonContainer.Builder<?> droppedItem = LootItem.lootTableItem(targetBlock.get());
        Optional<Block> inferredSapling = RegistryUtil.getObjectFrom(targetBlock.get(), RegistryUtil.replaceSuffix("sapling"));

        inferredSapling.ifPresent(sapling -> droppedItem
                .otherwise(LootItem.lootTableItem(sapling))
                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_SAPLING_CHANCES))
                .when(ExplosionCondition.survivesExplosion()));

        return LootTable.lootTable().withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(HAS_SHEARS_OR_SILK_TOUCH)
                        .add(droppedItem))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                        .add(LootItem.lootTableItem(Items.STICK)
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))
                                .apply(ApplyExplosionDecay.explosionDecay())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))));
    }

    /**
     * Creates a {@link LootTable.Builder} that will treat the given {@link Block} as a double plant and drop it based on whether
     * it has its lower half, if it has the {@link DoublePlantBlock#HALF} property.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link ExplosionCondition#survivesExplosion()}</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>When:</b> {@link LootItemBlockStatePropertyCondition#hasBlockStateProperties(Block)} (Has
     *      {@link DoublePlantBlock#HALF}, and it's {@link DoubleBlockHalf#LOWER})</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be treated as a double plant on drop.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as a double plant and drop it based on whether
     * it has its lower half, if it has the {@link DoublePlantBlock#HALF} property.
     */
    public static LootTable.Builder dropDoublePlant(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(ExplosionCondition.survivesExplosion())
                .add(LootItem.lootTableItem(targetBlock.get())
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(targetBlock.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)))));
    }

    /**
     * Creates a {@link LootTable.Builder} that will treat the given {@link Block} as a double plant and only drop it when
     * destroyed with shears or silk touch. The loot table ensures both upper and lower halves exist before dropping.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1 (Lower Half)</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>Applies:</b> {@link SetItemCountFunction#setCount(NumberProvider)} (Drops 1.0F)</li>
     *      <li><b>When:</b> {@link #HAS_SHEARS_OR_SILK_TOUCH}</li>
     *      <li><b>When:</b> {@link LootItemBlockStatePropertyCondition#hasBlockStateProperties(Block)} (Has
     *      {@link DoublePlantBlock#HALF}, and it's {@link DoubleBlockHalf#LOWER})</li>
     *      <li><b>When:</b> {@link LocationCheck#checkLocation(LocationPredicate.Builder, BlockPos)} (Checks for upper half at +1 Y)</li>
     *  </ul></li>
     * </ul>
     *
     * <h3>Pool 2 (Upper Half)</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>Applies:</b> {@link SetItemCountFunction#setCount(NumberProvider)} (Drops 1.0F)</li>
     *      <li><b>When:</b> {@link #HAS_SHEARS_OR_SILK_TOUCH}</li>
     *      <li><b>When:</b> {@link LootItemBlockStatePropertyCondition#hasBlockStateProperties(Block)} (Has
     *      {@link DoublePlantBlock#HALF}, and it's {@link DoubleBlockHalf#UPPER})</li>
     *      <li><b>When:</b> {@link LocationCheck#checkLocation(LocationPredicate.Builder, BlockPos)} (Checks for lower half at -1 Y)</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be treated as a double plant on drop.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as a double plant and only drop it when
     * destroyed with shears or silk touch, ensuring both halves exist.
     */
    public static LootTable.Builder dropDoublePlantShearsOrSilkTouch(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(targetBlock.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .when(HAS_SHEARS_OR_SILK_TOUCH)
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(targetBlock.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)))
                                .when(LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                        .setBlock(BlockPredicate.Builder.block()
                                                .of(targetBlock.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                                                        .build())
                                                .build()), new BlockPos(0, 1, 0)))))
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(targetBlock.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .when(HAS_SHEARS_OR_SILK_TOUCH))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(targetBlock.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)))
                        .when(LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                .setBlock(BlockPredicate.Builder.block().of(targetBlock.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                                                .build())
                                        .build()), new BlockPos(0, -1, 0))));
    }

    /**
     * Creates a {@link LootTable.Builder} that will treat the given {@link Block} as a multi-face block (like glow lichen)
     * and drop items based on how many faces of the block have the multi-face attachment.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>Applies:</b> {@link ApplyExplosionDecay#explosionDecay()}</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>When:</b> {@link #HAS_SHEARS_OR_SILK_TOUCH}</li>
     *      <li><b>Applies:</b> {@link SetItemCountFunction#setCount(NumberProvider)} for each direction where the face property is true</li>
     *      <li><b>Applies:</b> {@link SetItemCountFunction#setCount(NumberProvider)} (Drops -1.0F as base count)</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the {@link Block} that will be treated as a multi-face block on drop.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as a multi-face block and drop items
     * based on how many faces of the block have the multi-face attachment.
     */
    public static LootTable.Builder dropMultiFace(Supplier<Block> targetBlock) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .apply(ApplyExplosionDecay.explosionDecay())
                .add(LootItem.lootTableItem(targetBlock.get())
                        .when(HAS_SHEARS_OR_SILK_TOUCH)
                        .apply(Direction.values(), (curDir) -> SetItemCountFunction.setCount(ConstantValue.exactly(1.0F), true)
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(targetBlock.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(MultifaceBlock.getFaceProperty(curDir), true))))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1.0F), true))));
    }

    /**
     * Creates a {@link LootTable.Builder} that will treat the given {@link Block} as farmland and drop it when destroyed
     * with silk touch, otherwise dropping the corresponding dirt block.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link ExplosionCondition#survivesExplosion()}</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>When:</b> {@link #HAS_SILK_TOUCH} -> {@code targetBlock}</li>
     *      <li><b>Otherwise:</b> Corresponding dirt block, if applicable ({@code targetBlock} with {@code _farmland}
     *      suffix replaced or removed)</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the farmland {@link Block} to create loot table for.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as farmland and drop it when destroyed
     * with silk touch, otherwise dropping the corresponding dirt block.
     */
    public static LootTable.Builder dropFarmland(Supplier<Block> targetBlock) {
        LootPoolSingletonContainer.Builder<?> farmlandDrop = LootItem.lootTableItem(targetBlock.get());

        Optional<Block> alternateDirtBlock = RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_farmland", "_dirt")))
                .or(() -> RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_farmland", ""))));

        alternateDirtBlock.ifPresent(alternateDirt -> farmlandDrop
                .when(HAS_SILK_TOUCH)
                .otherwise(LootItem.lootTableItem(alternateDirt)));

        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(ExplosionCondition.survivesExplosion())
                .add(farmlandDrop));
    }

    /**
     * Creates a {@link LootTable.Builder} that will treat the given {@link Block} as a grass block and drop it when destroyed
     * with silk touch, otherwise dropping the corresponding dirt block.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *  <li><b>Rolls:</b> 1.0</li>
     *  <li><b>When:</b> {@link ExplosionCondition#survivesExplosion()}</li>
     *  <li><b>Loot Pool Entries:</b> <ul>
     *      <li><b>Loot Table Item:</b> {@code targetBlock}</li>
     *      <li><b>When:</b> {@link #HAS_SILK_TOUCH} -> {@code targetBlock}</li>
     *      <li><b>Otherwise:</b> Corresponding dirt block ({@code targetBlock} with {@code _grass_block} suffix replaced with {@code _dirt})</li>
     *  </ul></li>
     * </ul>
     *
     * @param targetBlock The {@link Supplier<Block>} representing the grass {@link Block} to create loot table for.
     *
     * @return A {@link LootTable.Builder} that will treat the given {@link Block} as a grass block and drop it when destroyed
     * with silk touch, otherwise dropping the corresponding dirt block.
     */
    public static LootTable.Builder dropGrassBlock(Supplier<Block> targetBlock) {
        LootPoolSingletonContainer.Builder<?> grassBlockDrop = LootItem.lootTableItem(targetBlock.get());

        Optional<Block> alternateDirtBlock = RegistryUtil.getObjectFrom(targetBlock, parentBlockId -> parentBlockId.withPath(parentBlockId.getPath().replace("_grass_block", "_dirt")));

        alternateDirtBlock.ifPresent(alternateDirt -> grassBlockDrop
                .when(HAS_SILK_TOUCH)
                .otherwise(LootItem.lootTableItem(alternateDirt)));

        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(ExplosionCondition.survivesExplosion())
                .add(grassBlockDrop));
    }

    /**
     * Creates a {@link LootTable.Builder} that will drop the potted plant and its contents when destroyed.
     * <p>
     * <h2>LOOT TABLE</h2>
     * <h3>Pool 1</h3>
     * <ul>
     *     <li>{@link #dropSelf(Supplier)} -> ({@link Blocks#FLOWER_POT}</li>
     * </ul>
     * <h3>Pool 2 (Only if {@code targetBlock.get()} is a {@link FlowerPotBlock})</h3>
     * <ul>
     *      <li><b>Rolls:</b> 1.0</li>
     *      <li><b>Loot Table Item:</b> {@code targetBlock.get().getContent()}</li>
     *      <li><b>When:</b> {@link ExplosionCondition#survivesExplosion()}</li>
     * </ul>
     *
     * @param targetBlock A {@code Supplier<Block>} representing the potted plant block.
     *
     * @return A {@link LootTable.Builder} configured to drop both the pot and its contents.
     *
     * @see #dropSelf(Supplier)
     */
    public static LootTable.Builder dropPottedContents(Supplier<Block> targetBlock) {
        LootTable.Builder basePotBuilder = dropSelf(() -> Blocks.FLOWER_POT);

        if (targetBlock.get() instanceof FlowerPotBlock targetFlowerPotBlock) {
            basePotBuilder.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .when(ExplosionCondition.survivesExplosion())
                    .add(LootItem.lootTableItem(targetFlowerPotBlock.getContent())));
        }

        return basePotBuilder;
    }
}
