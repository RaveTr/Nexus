package com.mememan.nexus.util;

import net.minecraft.advancements.critereon.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

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
}
