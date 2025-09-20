package com.mememan.nexus.property_wrapper.base.specialised.loot;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link DataGenPropertyWrapper} with methods tailored towards object loot tables.
 * <br></br>
 * This PW extension goes hand-in-hand with {@link LootBasedPropertyWrapperBuilder}.
 *
 * @see LootBasedPropertyWrapperBuilder
 */
public interface LootBasedPropertyWrapper<T, SELF extends PropertyWrapper<T, SELF, BUILDER>, BUILDER extends PropertyWrapperBuilder<T, BUILDER, SELF>> extends DataGenPropertyWrapper<T, SELF, BUILDER> {

    /**
     * Gets the {@code Function<Supplier<T>, LootTable.Builder>} responsible for building a loot table corresponding
     * to the parent object.
     *
     * @return The loot table builder function for the parent object. May be empty.
     */
    Optional<Function<Supplier<T>, LootTable.Builder>> getLootTableBuilder();

    @NotNull
    default String getLootTableDir() {
        return getObjectRegistryKey()
                .map(regKey -> regKey.location().getPath().concat("/"))
                .orElse(getParentObject().get().getClass().getSimpleName().toLowerCase(Locale.ROOT).concat("/"));
    }
}
