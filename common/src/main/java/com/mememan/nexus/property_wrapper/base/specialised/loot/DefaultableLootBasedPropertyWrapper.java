package com.mememan.nexus.property_wrapper.base.specialised.loot;

import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DefaultablePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegate extension for {@link LootBasedPropertyWrapper} that adds default getter method implementations (for
 * properties in {@link LootBasedPropertyWrapperBuilder}) using {@link #getSpecializedWrapper()}.
 *
 * @see DefaultablePropertyWrapperBuilder
 */
public interface DefaultableLootBasedPropertyWrapper<T, SELF extends LootBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedLootPropertyWrapperBuilder<T, BUILDER, SELF>> extends LootBasedPropertyWrapper<T, SELF, BUILDER>, DefaultablePropertyWrapper<T, SELF, BUILDER, SpecializedLootPropertyWrapper<T, SELF, BUILDER>> {

    @Override
    default Optional<Function<Supplier<T>, LootTable.Builder>> getLootTableBuilder() {
        return getSpecializedWrapper().flatMap(SpecializedLootPropertyWrapper::getLootTableBuilder);
    }
}
