package com.mememan.nexus.property_wrapper.base.specialised.loot;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link LootBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedBuilder()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableLootBasedPropertyWrapperBuilder<T, SELF extends LootBasedPropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends LootBasedPropertyWrapper<T, LBPW, SELF>> extends LootBasedPropertyWrapperBuilder<T, SELF, LBPW>, DefaultablePropertyWrapperBuilder<T, SELF, LBPW, SpecializedLootPropertyWrapperBuilder<T, SELF, LBPW>> {

    @Override
    default SELF withLootTable(Function<Supplier<T>, LootTable.Builder> lootTableBuilderFunc) {
        getSpecializedBuilder().ifPresent(builder -> builder.withLootTable(lootTableBuilderFunc));
        return self();
    }
}
