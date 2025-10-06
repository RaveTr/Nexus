package com.mememan.nexus.property_wrapper.base.specialised.loot;

import com.ibm.icu.impl.PluralRulesLoader;
import com.ibm.icu.text.PluralFormat;
import com.ibm.icu.text.PluralRules;
import com.mememan.nexus.datagen.standard.data_pack.StandardLootProvider;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.util.StringUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataResolver;
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

    /**
     * Gets the location to check against for the parent object's loot table if it couldn't be resolved via
     * the {@link LootDataResolver} in {@link StandardLootProvider}.
     * <br></br>
     * Works by attempting to pluralize the parent object's registry path, then using that as the loot table folder.
     * If the parent object has no registry path, it defaults to the parent object's class name in lowercase.
     *
     * @return The parent object's loot table folder.
     */
    @NotNull
    default String getLootTableDir() {
        return getObjectRegistryKey()
                .map(regKey -> StringUtil.pluralize(regKey.location().getPath()).concat("/"))
                .orElse(getParentObject().get().getClass().getSimpleName().toLowerCase(Locale.ROOT).concat("/"));
    }
}
