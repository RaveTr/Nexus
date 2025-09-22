package com.mememan.nexus.loader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Data-holding {@code interface} representing metadata/file data pertaining to a given mod.
 * <br></br>
 * This is primarily useful for performing mod-specific operations segregated from the rest of the environment, thereby
 * minimizing compat oddities and performance overhead, or to check for basic mod metadata.
 * <br></br>
 * It should be noted that loader-specific implementations of this {@code interface} cache all mod meta/data only once
 * during startup. Resource management and other later runtime-dependant tasks should therefore be handled elsewhere.
 *
 * @apiNote A {@link ModData} object may represent one mod instance in its own JAR file. If a mod happens to be nested,
 * only the contents and metadata of its JAR file are cached. The parent JAR remains separate as its own mod.
 */
public interface ModData {

    /**
     * Gets this instance's mod metadata, as commonly defined in each loader's respective MTD file
     * ({@code fabric.mod.json}, {@code mods.toml}, etc.).
     *
     * @return This instance's mod metadata.
     */
    @NotNull
    ModMetadata getModMetadata();

    /**
     * Gets a {@link List} of every single formatted path within this instance's owning mod's JAR file on
     * startup.
     * <br></br>
     * Mind that when using the word "formatted" in this case, it refers to paths that go something like
     * {@code "com.mememan.nexus.loader.ModData.class"} (I.E. NOT the canonical system path to any given {@code class}
     * file).
     * <br></br>
     * Mind that only {@code class} file paths are formatted. Paths that lead to other file types remain separated
     * by {@code '/'}.
     *
     * @return A {@link List} of every single formatted path within this instance's owning mod's JAR file.
     *
     * @apiNote This method also works for files within the dev environment under the {@code "build/..."} directory.
     */
    List<String> getAllFilePaths();

    /**
     * Overloaded variant of {@link #getAllFilePaths()} that computes/gets a lexicographically-sorted {@link List} of
     * all {@code class} files within this instance's owning mod's JAR file. Does not prune the {@code .class}
     * extension.
     *
     * @return A lexicographically-sorted {@link List} of all {@code class} files within this instance's owning mod's
     * JAR file.
     *
     * @see #getAllResourcePaths(String)
     */
    default List<String> getAllClassPaths() {
        return getAllFilePaths().stream()
                .filter(path -> path.endsWith(".class"))
                .sorted(String::compareTo)
                .toList();
    }

    /**
     * Overloaded variant of {@link #getAllFilePaths()} that computes/gets a lexicographically-sorted {@link List} of
     * all resource files within this instance's owning mod's JAR file. Does not prune their extensions. Excludes
     * {@code class} files.
     *
     * @param resourceFileType The file type of the resource files to be retrieved. May be {@code null} to indicate
     *                         that all resource files should be retrieved.
     *
     * @return A lexicographically-sorted {@link List} of all resource files within this instance's owning mod's
     * JAR file, corresponding to the specified resource file type.
     *
     * @see #getAllClassPaths()
     * @see #getAllResourcePaths()
     */
    default List<String> getAllResourcePaths(@Nullable String resourceFileType) {
        return getAllFilePaths().stream()
                .filter(curPath -> (resourceFileType == null || curPath.endsWith(resourceFileType)) && !curPath.endsWith(".class"))
                .sorted(String::compareTo)
                .toList();
    }

    /**
     * Overloaded variant of {@link #getAllResourcePaths(String)} that computes/gets a lexicographically-sorted
     * {@link List} of all resource files within this instance's owning mod's JAR file. Does not prune their
     * extensions. Excludes {@code class} files.
     *
     * @return A lexicographically-sorted {@link List} of all resource files within this instance's owning mod's
     * JAR file.
     *
     * @see #getAllClassPaths()
     * @see #getAllResourcePaths(String)
     */
    default List<String> getAllResourcePaths() {
        return getAllResourcePaths(null);
    }

    /**
     * Gets a {@link ConcurrentHashMap} of all (mod) classes that are annotated at all.
     * <br></br>
     * Note that this method gets the formatted paths of all annotated classes and their annotation keys, but does not
     * load/initialize any of the classes or annotations themselves.
     *
     * @return A {@link ConcurrentHashMap} of all (mod) classes that have any annotations.
     *
     * @apiNote We're specifically using CHM as the backing collection since different loaders have their own optimization
     * nuances during mod-loading. For instance, Forge creates several mod loading worker threads to load and initialize
     * mods in parallel, so concurrency is needed in order to avoid race conditions and/or any other undefined behaviour.
     *
     * @implNote Annotation paths (map key) themselves are formatted in typical annotation path format (e.g.
     * {@code "Lsome/package/to/Annotation;"} rather than {@code "some.package.to.Annotation"}). If you want to do any
     * annotation-based comparisons from literal annotation classes using the map keys, make sure to format the
     * annotations beforehand so that they're properly identified and matched:
     * {@code String formattedAnnotationName = "L" + annotationTypeClazz.getName().replace('.', '/') + ";";}.
     */
    ConcurrentHashMap<String, ObjectArrayList<String>> getCachedAnnotatedClasses();

    /**
     * Discovers all (mod) classes that are annotated with the specified annotation type and compiles them into a
     * {@link List}. Take note that this method <b>loads and initializes</b> (valid) discovered classes.
     *
     * @param annotationTypeClazz The annotation type class.
     * @param classLoadingSorter A {@link Comparator} for sorting the discovered classes. Mind that this sorts classes
     *                           <b>before</b> loading them. May be {@code null}.
     * @param beforeClassInitConsumer Some task to be run before class initialization but after sorting. May
     *                                be {@code null}.
     *
     * @return A {@link List} of (loaded) classes within this instance's owning mod annotated with the specified
     * annotation type. May be empty.
     *
     * @apiNote The reason this method doesn't have a {@code default} implementation using {@link #getAllClassPaths()}
     * and the likes is that each loader may have a more efficient/direct way of accessing and filtering class files
     * accordingly.
     */
    List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Comparator<String> classLoadingSorter, @Nullable Consumer<String> beforeClassInitConsumer);

    /**
     * Overloaded variant of {@link #discoverAnnotatedClasses(Class, Comparator, Consumer)} without any pre-initialization
     * operations.
     *
     * @param annotationTypeClazz The annotation type class.
     * @param classLoadingSorter A {@link Comparator} for sorting the discovered classes. Mind that this sorts classes
     *                           <b>before</b> loading them. May be {@code null}.
     *
     * @return A {@link List} of (loaded) classes within this instance's owning mod annotated with the specified
     * annotation type. May be empty.
     */
    default List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Comparator<String> classLoadingSorter) {
        return discoverAnnotatedClasses(annotationTypeClazz, classLoadingSorter, null);
    }
}
