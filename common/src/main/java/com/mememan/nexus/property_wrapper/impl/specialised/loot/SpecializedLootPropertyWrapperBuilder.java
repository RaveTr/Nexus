package com.mememan.nexus.property_wrapper.impl.specialised.loot;

import com.mememan.nexus.property_wrapper.base.specialised.loot.LootBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.loot.LootBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpecializedLootPropertyWrapperBuilder<T, SELF extends LootBasedPropertyWrapperBuilder<T, SELF, LBPW>, LBPW extends LootBasedPropertyWrapper<T, LBPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, LBPW> implements LootBasedPropertyWrapperBuilder<T, SELF, LBPW> {
    protected Optional<Function<Supplier<T>, LootTable.Builder>> lootTableBuilder = Optional.empty();

    public SpecializedLootPropertyWrapperBuilder(@NotNull LBPW ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(LBPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .withLootTable(propertyWrapper.getLootTableBuilder().orElse(null));
    }

    @Override
    public SELF withLootTable(Function<Supplier<T>, LootTable.Builder> lootTableBuilderFunc) {
        this.lootTableBuilder = Optional.ofNullable(lootTableBuilderFunc);
        return self();
    }
}
