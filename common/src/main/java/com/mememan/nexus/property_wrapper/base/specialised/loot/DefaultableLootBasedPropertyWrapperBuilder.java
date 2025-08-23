package com.mememan.nexus.property_wrapper.base.specialised.loot;

import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link LootBasedPropertyWrapperBuilder} that adds default builder method implementations
 * using {@link #getSpecializedLootBuilder()}.
 *
 * @see DefaultableLootBasedPropertyWrapper
 */
public interface DefaultableLootBasedPropertyWrapperBuilder<T, SELF extends LootBasedPropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends LootBasedPropertyWrapper<T, LBPW, SELF>> extends LootBasedPropertyWrapperBuilder<T, SELF, LBPW> {

    Optional<SpecializedLootPropertyWrapperBuilder<T, SELF, LBPW>> getSpecializedLootBuilder();

    @Override
    default SELF withLootTable(Function<Supplier<T>, LootTable.Builder> lootTableBuilderFunc) {
        getSpecializedLootBuilder().ifPresent(builder -> builder.withLootTable(lootTableBuilderFunc));
        return self();
    }

    @Override
    default SELF copyFrom(LBPW propertyWrapper) {
        getSpecializedLootBuilder().ifPresent(builder -> builder.copyFrom(propertyWrapper));
        return self();
    }
}
