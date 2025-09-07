package com.mememan.nexus.platform.services;

import com.mememan.nexus.loader.*;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A loader-agnostic {@code interface} for managing platform-specific implementations of certain loader-specific
 * features, ranging from methods that provide predicates for loader detection to methods that allow for more performant
 * ways of executing certain tasks, such as class-loading, environment-detection, etc.
 * <br></br>
 * Methods provided here are a mix of template-default utilities included in the MultiLoader template, as well as more
 * sophisticated helpers aimed at simplifying more specific tasks.
 */
public interface PlatformManager {

    /**
     * Gets the {@link ModLoader} representation of the current platform. This is an OOP'd variant of the otherwise
     * template-default method included in the MultiLoader template.
     *
     * @return The {@link ModLoader} representation of the current platform.
     */
    ModLoader getPlatform();

    /**
     * Checks if a mod with the given id is loaded. This is a template-default method included in the MultiLoader
     * template.
     *
     * @param modId The mod to check if it is loaded.
     *
     * @return {@code true} if the mod is loaded, {@code false} otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment. This is a template-default method included in the
     * MultiLoader template.
     *
     * @return {@code true} if in a development environment, {@code false} otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Discovers all (mod) classes that are annotated with the specified annotation type and compiles them into a
     * {@link List}. Take note that this method <b>loads and initializes</b> (valid) discovered classes.
     *
     * @param annotationTypeClazz The annotation type class.
     * @param classLoadingSorter A {@link Comparator} for sorting the discovered classes. Mind that this sorts classes
     *                           <b>before</b> loading them. May be {@code null}.
     * @param validModIds An optional whitelist of valid mod IDs to scan for annotated classes. Leaving this empty or
     *                    {@code null} will result in a scan for annotated classes from all mods.
     * @param beforeClassInitConsumer Some task to be run before class initialization but after sorting. May
     *                                be {@code null}.
     *
     * @return A {@link List} of (loaded) classes annotated with the specified annotation type. May be empty.
     *
     * @apiNote If multiple calls are made to this method targeting the same exact {@code annotationTypeClazz} whose
     * corresponding annotated classes are loaded, this method will simply compute and return the same result without
     * actually doing anything to the already loaded classes.
     */
    List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Comparator<String> classLoadingSorter, @Nullable List<String> validModIds, @Nullable Consumer<String> beforeClassInitConsumer);

    /**
     * Overloaded variant of {@link #discoverAnnotatedClasses(Class, Comparator, List, Consumer)} that uses the default
     * {@link String#compareTo(String)} comparator for sorting.
     *
     * @param annotationTypeClazz The annotation type class.
     * @param validModIds An optional whitelist of valid mod IDs to scan for annotated classes. Leaving this empty or
     *                    {@code null} will result in a scan for annotated classes from all mods.
     * @param beforeClassInitConsumer Some task to be run before class initialization but after sorting. May
     *                                be {@code null}.
     *
     * @return A {@link List} of (loaded) classes annotated with the specified annotation type. May be empty.
     */
    default List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable List<String> validModIds, Consumer<String> beforeClassInitConsumer) {
        return discoverAnnotatedClasses(annotationTypeClazz, String::compareTo, validModIds, beforeClassInitConsumer);
    }

    /**
     * Overloaded variant of {@link #discoverAnnotatedClasses(Class, List, Consumer)} without any pre-initialization
     * tasks.
     *
     * @param annotationTypeClazz The annotation type class.
     * @param validModIds An optional whitelist of valid mod IDs to scan for annotated classes. Leaving this empty or
     *                    {@code null} will result in a scan for annotated classes from all mods.
     *
     * @return A {@link List} of (loaded) classes annotated with the specified annotation type. May be empty.
     */
    default List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable List<String> validModIds) {
        return discoverAnnotatedClasses(annotationTypeClazz, validModIds, null);
    }

    /**
     * Overloaded variant of {@link #discoverAnnotatedClasses(Class, Comparator, List, Consumer)}. Will scan for annotated classes
     * from all mods.
     *
     * @param annotationTypeClazz The annotation type class.
     * @param classLoadingSorter A {@link Comparator} for sorting the discovered classes. Mind that this sorts classes
     *                           <b>before</b> initializing them. May be {@code null}.
     * @param beforeClassInitConsumer Some task to be run before class initialization but after sorting. May
     *                                be {@code null}.
     *
     * @return A {@link List} of (loaded) classes annotated with the specified annotation type. May be empty.
     */
    default List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Comparator<String> classLoadingSorter, @Nullable Consumer<String> beforeClassInitConsumer) {
        return discoverAnnotatedClasses(annotationTypeClazz, classLoadingSorter, null, beforeClassInitConsumer);
    }

    /**
     * Overloaded variant of {@link #discoverAnnotatedClasses(Class, Comparator, Consumer)} without any pre-initialization
     * tasks.
     *
     * @param annotationTypeClazz The annotation type class.
     * @param classLoadingSorter A {@link Comparator} for sorting the discovered classes. Mind that this sorts classes
     *                           <b>before</b> initializing them. May be {@code null}.
     *
     * @return A {@link List} of (loaded) classes annotated with the specified annotation type. May be empty.
     */
    default List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Comparator<String> classLoadingSorter) {
        return discoverAnnotatedClasses(annotationTypeClazz, classLoadingSorter, null);
    }

    /**
     * Overloaded variant of {@link #discoverAnnotatedClasses(Class, Comparator, List, Consumer)}. Will scan for annotated classes from all
     * mods. Classes will be loaded lexicographically ({@link String#compareTo(String)}).
     *
     * @param annotationTypeClazz The annotation type class.
     * @param beforeClassInitConsumer Some task to be run before class initialization but after sorting. May
     *                                be {@code null}.
     *
     * @return A {@link List} of (loaded) classes annotated with the specified annotation type. May be empty.
     */
    default List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Consumer<String> beforeClassInitConsumer) {
        return discoverAnnotatedClasses(annotationTypeClazz, null, null, beforeClassInitConsumer);
    }

    /**
     * Overloaded variant of {@link #discoverAnnotatedClasses(Class, Comparator, List, Consumer)}. Will scan for
     * annotated classes from all mods. Classes will be loaded lexicographically ({@link String#compareTo(String)})
     * without any pre-initialization tasks.
     *
     * @param annotationTypeClazz The annotation type class.
     *
     * @return A {@link List} of (loaded) classes annotated with the specified annotation type. May be empty.
     */
    default List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz) {
        return discoverAnnotatedClasses(annotationTypeClazz, null, null, null);
    }

    /**
     * Gets all loaded mods and converts them into their respective {@link ModData} representation before pooling them
     * into a {@link Set}. Different loaders have different implementations of {@link ModData}.
     * <br></br>
     * This shouldn't have any major performance overhead. At its worst, it should only really add a few extra ms
     * to the game's startup time, and that's only on its first call when nothing's cached yet.
     *
     * @return A {@link Set} of all loaded mods, represented as {@link ModData} objects.
     *
     * @implNote Mind that this collection doesn't include Minecraft or Java, as some loader implementations do.
     */
    Set<ModData> getModData();

    /**
     * Convenient shortcut method that streams through {@link #getModData()} and retrieves the {@link ModData}
     * corresponding to the provided {@code modId}.
     *
     * @param modId The {@code modId} whose {@link ModData} should be retrieved.
     *
     * @return The {@link ModData} matching the {@code modId} passed in. May be {@code null} if no such mod is loaded (or
     * somehow, in an anomalously impossible case, {@link #getModData()} is empty).
     */
    @Nullable
    default ModData getModDataById(String modId) {
        return getModData().isEmpty() ? null : getModData().stream()
                .filter(curModData -> curModData.getModMetadata().modId().equals(modId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the {@link GamePathWrapper} representing path-related operations for a given loader.
     *
     * @return The {@link GamePathWrapper} of the current platform.
     */
    GamePathWrapper getGamePathWrapper();

    /**
     * Gets the currently-running Minecraft server instance. May be {@code null} if the server isn't currently running
     * on either side, or if the server is still at its early startup phase.
     *
     * @return The currently-running Minecraft server instance. May be {@code null}.
     */
    @Nullable
    MinecraftServer getCurrentServer();

    /**
     * Whether the current loader is running data generation. Only really useful in the development environment.
     *
     * @return Whether the current loader is running data generation.
     *
     * @apiNote Calling this too early (e.g. during mixin initialization, inside your mixin config plugin
     * {@code class}) will result in this method returning {@code false}, as the data generator instance would not have
     * been initialized yet.
     */
    boolean isRunningDataGen();

    /**
     * Gets the {@link EnvironmentSide} representation of the current side. This is essentially just a wrapper that
     * determines the physical side you're working in.
     *
     * @return The {@link EnvironmentSide} representation of the current side.
     *
     * @see EnvironmentSide
     */
    EnvironmentSide getEnvironmentSide();

    /**
     * Gets the {@link EnvironmentType} representation of the current environment. This is an OOP'd variant of the
     * otherwise template-default method included in the MultiLoader template.
     *
     * @return The {@link EnvironmentType} representation of the current environment.
     */
    default EnvironmentType getEnvironmentType() {
        return isDevelopmentEnvironment() ? EnvironmentType.DEVELOPMENT : EnvironmentType.PRODUCTION;
    }
}
