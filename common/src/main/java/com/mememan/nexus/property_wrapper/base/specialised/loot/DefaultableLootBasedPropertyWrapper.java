package com.mememan.nexus.property_wrapper.base.specialised.loot;

import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link LootBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link LootBasedPropertyWrapperBuilder}) using {@link #getSpecializedLootWrapper()}.
 *
 * @see DefaultableLootBasedPropertyWrapperBuilder
 */
public interface DefaultableLootBasedPropertyWrapper<T, SELF extends LootBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends LootBasedPropertyWrapperBuilder<T, BUILDER, SELF>> extends LootBasedPropertyWrapper<T, SELF, BUILDER> {

    /**
     * The specialized wrapper to which all getters should delegate.
     *
     * @return The specialized wrapper to which all getters should delegate. May be empty.
     */
    Optional<SpecializedLootPropertyWrapper<T, ?, ?>> getSpecializedLootWrapper();

    @Override
    default Optional<Function<Supplier<T>, LootTable.Builder>> getLootTableBuilder() {
        return getSpecializedLootWrapper().flatMap(SpecializedLootPropertyWrapper::getLootTableBuilder);
    }
}
