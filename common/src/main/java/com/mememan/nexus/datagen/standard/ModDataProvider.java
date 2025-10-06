package com.mememan.nexus.datagen.standard;

import com.google.gson.JsonElement;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Loader-agnostic mod-oriented version of {@link DataProvider} designed for mod-specific data generation tasks in Nexus API.
 * This {@code interface} ensures that data generation tasks are contextually segregated and tied to a specific mod ID.
 */
public interface ModDataProvider extends DataProvider {

    /**
     * Gets the mod ID of the mod this provider belongs to. Always formatted in locale-lowercase.
     *
     * @return The mod ID of the mod this provider belongs to, e.g. {@code "nexus"}.
     */
    @NotNull
    String getModId();

    /**
     * Whether this particular provider instance should validate the existence of a data entry for all objects pertaining
     * to this provider's type.
     * <br></br>
     * For instance, if this is a block model provider, returning {@code true} would ensure that all blocks under
     * {@link #getModId()}'s namespace have at least 1 generated block model, throwing a {@link NullPointerException} or
     * {@link IllegalStateException} otherwise.
     * <br></br>
     * Property wrappers excluded from datagen (e.g. via {@link DataGenPropertyWrapper#isExcludedFromDataGen()}) will be
     * ignored.
     *
     * @return Whether object entries pertaining to this provider should validate the existence of at least 1 mapped data
     * entry.
     */
    boolean validateAllEntries();

    /**
     * The {@link ProviderType} representing this data provider.
     *
     * @return The {@link ProviderType} representing this data provider.
     */
    @NotNull
    ProviderType getProviderType();

    /**
     * The {@link DuplicateDataPolicy} to use whenever duplicate data is encountered.
     *
     * @return This instance's {@link DuplicateDataPolicy}.
     */
    @NotNull
    DuplicateDataPolicy getDuplicateDataPolicy();

    /**
     * Runs this data provider. This is the method that does the actual data generation.
     *
     * @param cachedOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return A {@link CompletableFuture} that completes when the data generation is complete.
     *
     * @implNote Implementations should return a {@link CompletableFuture} regardless of whether any data has
     * actually been generated. By conventional standard, this method returns {@code CompletableFuture.completedFuture(null)}
     * if no data was generated. Otherwise, it returns a {@code CompletableFuture#allOf(...)} that maps data to
     * {@link DataProvider#saveStable(CachedOutput, JsonElement, Path)}, then gathers the stream of these futures
     * into a single array (or similar).
     */
    @Override
    @NotNull
    CompletableFuture<?> run(CachedOutput cachedOutput);

    /**
     * Default override for {@link DataProvider#getName()}.
     * <br></br>
     * Minecraft uses this to check for duplicate data providers.
     * Obviously, most vanilla classes implement this as {@code final}, but we're modders with the ability to AT/AW, so
     * it doesn't really matter.
     * <br></br>
     * As an end-developer, you don't really need to worry about this all too much unless you're implementing your own
     * custom provider(s).
     *
     * @return The name of this data provider instance.
     */
    @Override
    default @NotNull String getName() {
        return " [" + getModId() + "]";
    }
}
