package com.mememan.nexus.property_wrapper.base.specialised.loot;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapperBuilder} with builder methods towards object loot tables.
 * <br></br>
 * This PWB extension goes hand-in-hand with {@link LootBasedPropertyWrapper}.
 *
 * @see LootBasedPropertyWrapper
 */
public interface LootBasedPropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends LootBasedPropertyWrapper<T, LBPW, SELF>> extends DataGenPropertyWrapperBuilder<T, SELF, LBPW> {

    /**
     * Specifies the loot table to be built using the parent object as an input.
     *
     * @param lootTableBuilderFunc The mapping {@code Function<Supplier<T>, LootTable.Builder>} used to build the
     *                             parent object's loot table in datagen.
     *
     * @return {@link #self()} (builder method).
     */
    SELF withLootTable(Function<Supplier<T>, LootTable.Builder> lootTableBuilderFunc);
}
