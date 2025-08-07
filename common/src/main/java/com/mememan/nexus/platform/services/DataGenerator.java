package com.mememan.nexus.platform.services;

import com.google.common.collect.HashMultimap;
import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A loader-agnostic {@code interface} whose purpose is to handle the registration of segregated mod-specific data
 * providers.
 * <br></br>
 * Primarily designed to accept provider implementations regardless of whether they're loader-specific or
 * common implementations.
 * <br></br>
 * Main use case is to allow for mods to modify Nexus API's behaviour when auto-generating related data. Mods should
 * otherwise prefer writing/implementing their own data providers.
 *
 * @see <a href="https://github.com/RaveTr/Nexus/wiki/Data-Generation">Nexus Wiki: Data Generation</a>
 */
public interface DataGenerator {

    /**
     * Bootstrap method responsible for setting the data generator service up and notifying/calling all data provider
     * registrars for processing/setup.
     * <br></br>
     * Should <b>NOT</b> be called anywhere else!
     */
    @ApiStatus.Internal
    @ApiStatus.OverrideOnly
    void setupDataGenerator();

    /**
     * Registers a {@link DataProvider} with minimal context (using only a {@link PackOutput} and
     * {@code CompletableFuture<HolderLookup.Provider>}). Providers are run based on their own data, meaning that if you
     * were to pass a {@link ModDataProvider} subtype, it would output data based on the {@link ModDataProvider#getModId()}
     * specified within the output {@code DP's} constructor.
     * <br></br>
     * If you're registering loader-specific data providers that require loader-specific parameters beyond what vanilla
     * (or your own code) offers, you should use your respective loader to register them as necessary. Nexus only aims to
     * provide a data provider registration entrypoint for code within {@code common} modules and allow for mods to
     * configure Nexus API's native datagen.
     *
     * @param modId The parent mod ID under which the target provider should be registered.
     * @param dataProvider The provider to register. Takes an input of {@link PackOutput} and
     *                     {@code CompletableFuture<HolderLookup.Provider>} to be passed into the output
     *                     {@linkplain Pair} of whether the provided {@code DP} is client or server-side and the {@code DP}
     *                     itself.
     *
     * @param <DP> Any subtype of {@link DataProvider} to be registered and run during datagen.
     *
     * @apiNote For clarity, it is completely plausible to write data providers for custom objects within your {@code common}
     * module and pass them into here, since you would be controlling the parameters they need in order to be constructed
     * and run. The loader-specific providers referred to above are in reference to providers supplied by Neo/Forge, Fabric,
     * etc. which are typically extensions of Vanilla providers with their own additions and/or nuances.
     *
     * @implNote Loader-specific implementations will ignore the left output {@code boolean} if the {@code DP} is an
     * instance of {@linkplain ModDataProvider}, since {@linkplain ModDataProvider#getProviderType()} would be used to
     * validate sides instead.
     *
     * @see #registerModDataProvider(String, BiFunction)
     */
    <DP extends DataProvider> void registerDataProvider(String modId, final BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, Pair<Boolean, DP>> dataProvider);

    /**
     * Alternate variant of {@linkplain #registerDataProvider(String, BiFunction)}, specifically tailored for registering
     * {@link ModDataProvider} types. Useful as a shortcut method that additionally handles providing the metadata needed
     * to run generators that rely on storing/consuming mod IDs or otherwise simply extend or implement {@link ModDataProvider}.
     *
     * @param modId The parent ID mod under which the target provider should be registered.
     * @param modDataProvider The mod provider to register. Takes an input of {@link PackOutput} and
     *                        {@code CompletableFuture<HolderLookup.Provider>} to be passed into the output {@code DP}.
     *
     * @param <MDP> Any subtype of {@link ModDataProvider} to be registered and run during datagen.
     */
    <MDP extends ModDataProvider> void registerModDataProvider(String modId, final BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, MDP> modDataProvider);

    /**
     * Registers a {@link ModDatagenConfig}. Allows for more flexibility and control over how Nexus API handles datagen
     * for specific mods.
     * <br></br>
     * You cannot have more than 1 config registered for a mod at any given time. If an attempt is made to registered
     * additional configs tied to the same mod ID, the originally-registered config will take precedence.
     *
     * @param modDatagenConfig The datagen config to register.
     *
     * @return The registered {@link ModDatagenConfig}.
     */
    ModDatagenConfig registerConfigForMod(ModDatagenConfig modDatagenConfig);

    /**
     * Gets the existing {@link Set} of datagen configs for mods intending to utilise Nexus' datagen.
     * <br></br>
     * If this {@link Set} happens to be empty, Nexus will skip running its providers altogether.
     *
     * @return The {@link Set} of {@linkplain ModDatagenConfig ModDatagenConfigs} Nexus API uses to run its providers.
     * May be empty.
     */
    Set<ModDatagenConfig> getModDatagenConfigs();

    /**
     * Gets the {@link ModDatagenConfig} associated with the {@code modId} passed in. May be {@code null} if no such
     * config is registered or {@link #getModDatagenConfigs()} is empty.
     *
     * @param modId The mod ID of the target mod to try and get the {@link ModDatagenConfig} for.
     *
     * @return The associated {@link ModDatagenConfig}, or {@code null} if no such config exists for the given mod ID.
     */
    @Nullable
    default ModDatagenConfig getConfigForMod(String modId) {
        return getModDatagenConfigs().isEmpty() ? null : getModDatagenConfigs().stream()
                .filter(curConfig -> curConfig.modId().equals(modId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Computes a {@link HashMultimap} of disabled {@linkplain ProviderType ProviderTypes} pertaining to their respective
     * mod IDs via {@link #getModDatagenConfigs()}.
     *
     * @return A {@link HashMultimap} of disabled {@linkplain ProviderType ProviderTypes} mapped to their mod IDs. May be
     * empty.
     */
    default HashMultimap<String, ProviderType> getDisabledDataProviders() {
        HashMultimap<String, ProviderType> mappedDisabledProviders = HashMultimap.create();

        if (getModDatagenConfigs().isEmpty()) return mappedDisabledProviders;

        getModDatagenConfigs().forEach(curModDGConfig -> {
            mappedDisabledProviders.asMap()
                    .computeIfAbsent(curModDGConfig.modId(), oK -> new ObjectOpenHashSet<>())
                    .addAll(curModDGConfig.disabledProviderTypes());
        });

        return mappedDisabledProviders;
    }

    /**
     * Computes an {@link ObjectOpenHashSet} of mods whose configs explicitly specify that Nexus API should not generate
     * data or register providers for them. Note that this does not include mods whose configs are {@code null}/haven't
     * been declared.
     *
     * @return An {@link ObjectOpenHashSet} of mods whose configs explicitly specify that Nexus API should not generate
     * data or register providers for them, using {@link #getModDatagenConfigs()}. May be empty
     */
    default ObjectOpenHashSet<ModDatagenConfig> getDisabledMods() {
        return getModDatagenConfigs().isEmpty() ? ObjectOpenHashSet.of() : getModDatagenConfigs().stream()
                .filter(curModDGConfig -> !curModDGConfig.enableDatagen())
                .collect(Collectors.toCollection(ObjectOpenHashSet::new));
    }

    /**
     * Gets the global {@link net.minecraft.data.DataGenerator} instance running, if available.
     * <br></br>
     * Should be fairly obvious that this will return {@code null} if you're not running a datagen Gradle task (or you
     * try accessing it too early).
     *
     * @return The globally-running {@link net.minecraft.data.DataGenerator} instance. May be {@code null}.
     */
    @Nullable
    net.minecraft.data.DataGenerator getDataGenerator();
}
