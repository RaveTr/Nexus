package com.mememan.nexus.property_wrapper.impl.specialised.loot;

import com.mememan.nexus.property_wrapper.base.specialised.loot.LootBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized implementation of {@link LootBasedPropertyWrapper}. Implements all loot-related getter methods,
 * generic types, and default behaviour for loot-based property wrapper handling.
 *
 * @see SpecializedLootPropertyWrapperBuilder
 */
public class SpecializedLootPropertyWrapper<T, SELF extends LootBasedPropertyWrapper<T, SELF, BUILDER>, BUILDER extends SpecializedLootPropertyWrapperBuilder<T, BUILDER, SELF>> extends BaseDataGenPropertyWrapper<T, SELF, BUILDER> implements LootBasedPropertyWrapper<T, SELF, BUILDER> {

    public SpecializedLootPropertyWrapper(Supplier<T> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, SpecializedLootPropertyWrapperBuilder::new, modId);
    }

    public SpecializedLootPropertyWrapper(@NotNull Supplier<T> parentObject, @NotNull String modId) {
        super(parentObject, SpecializedLootPropertyWrapperBuilder::new, modId);
    }

    public SpecializedLootPropertyWrapper() {
        super(SpecializedLootPropertyWrapperBuilder::new);
    }

    @Override
    public Optional<Function<Supplier<T>, LootTable.Builder>> getLootTableBuilder() {
        return rawBuilder().map(b -> b.lootTableBuilder);
    }
}
