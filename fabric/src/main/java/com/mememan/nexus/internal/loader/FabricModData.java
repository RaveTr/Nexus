package com.mememan.nexus.internal.loader;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.ClassFinder;
import com.mememan.nexus.loader.ModData;
import com.mememan.nexus.loader.ModMetadata;
import com.mememan.nexus.loader.ModSide;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.api.metadata.Person;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fabric-specific implementation of {@link ModData}.
 * <br></br>
 * Pre-emptively caches mod data on startup, using {@link ModContainer} as the backing source for mod meta/data. Additionally,
 * handles mapping Fabric-specific metadata and logs general operations executed on object construction.
 */
public class FabricModData implements ModData {
    private final ModContainer ownerModContainer;
    private final ModMetadata ownerModMetadata;
    private final ObjectArrayList<String> allFilePaths;
    private final ConcurrentHashMap<String, ObjectArrayList<String>> cachedAnnotatedClasses = new ConcurrentHashMap<>(); // Stored as strings to avoid unnecessary classloading

    public FabricModData(ModContainer ownerModContainer) {
        long startTime = System.currentTimeMillis();

        this.ownerModContainer = ownerModContainer;

        net.fabricmc.loader.api.metadata.ModMetadata ownerFabricModMetadata = ownerModContainer.getMetadata();
        ObjectArrayList<Triple<String, RangeSet<String>, Boolean>> dependencies = ownerFabricModMetadata.getDependencies().isEmpty() ? ObjectArrayList.of() : ownerFabricModMetadata.getDependencies().stream()
                .map(curDependency -> {
                    RangeSet<String> dependencyVersions = TreeRangeSet.create();

                    if (!curDependency.getVersionIntervals().isEmpty()) {
                        curDependency.getVersionIntervals().stream()
                                .map(curVersionInterval -> curVersionInterval.getMin() == null && curVersionInterval.getMax() == null
                                        ? null
                                        : curVersionInterval.getMin() == null // Not my proudest nested terop
                                        ? Range.atMost(curVersionInterval.getMax().toString())
                                        : curVersionInterval.getMax() == null
                                        ? Range.atLeast(curVersionInterval.getMin().toString())
                                        : curVersionInterval.isMinInclusive() && !curVersionInterval.isMaxInclusive()
                                        ? Range.closedOpen(curVersionInterval.getMin().toString(), curVersionInterval.getMax().toString())
                                        : !curVersionInterval.isMinInclusive() && curVersionInterval.isMaxInclusive()
                                        ? Range.openClosed(curVersionInterval.getMin().toString(), curVersionInterval.getMax().toString())
                                        : !curVersionInterval.isMinInclusive() && !curVersionInterval.isMaxInclusive()
                                        ? Range.open(curVersionInterval.getMin().toString(), curVersionInterval.getMax().toString())
                                        : Range.closed(curVersionInterval.getMin().toString(), curVersionInterval.getMax().toString()))
                                .filter(Objects::nonNull)
                                .forEach(dependencyVersions::add);
                    }

                    return Triple.of(curDependency.getModId(), dependencyVersions, curDependency.getKind() == ModDependency.Kind.DEPENDS);
                })
                .collect(Collectors.toCollection(ObjectArrayList::new));

        this.ownerModMetadata = new ModMetadata(ownerFabricModMetadata.getId(), ownerFabricModMetadata.getName(),
                ownerFabricModMetadata.getVersion().getFriendlyString(), Arrays.toString(ownerFabricModMetadata.getLicense().toArray()),
                ownerFabricModMetadata.getDescription(), ownerFabricModMetadata.getAuthors().stream()
                .map(Person::getName)
                .collect(Collectors.toCollection(ObjectArrayList::new)), dependencies,
                ownerFabricModMetadata.getEnvironment() == ModEnvironment.CLIENT
                        ? ModSide.CLIENT
                        : ownerFabricModMetadata.getEnvironment() == ModEnvironment.SERVER
                        ? ModSide.SERVER
                        : ModSide.COMMON);

        this.allFilePaths = mapAllFilePaths(ownerModContainer);

        long endTime = System.currentTimeMillis();
        long milliDuration = endTime - startTime;

        String targetFileLoc = String.valueOf(ownerModContainer.getOrigin());

        NexusConstants.LOGGER.info("Loaded mod data for mod {} (within{} file {}) in {} ms", ownerFabricModMetadata.getId(),
                ownerModContainer.getContainingMod().isPresent() ? " nested" : "",
                targetFileLoc.substring(targetFileLoc.lastIndexOf(File.separatorChar) + 1), milliDuration);
    }

    public ModContainer getOwnerModContainer() {
        return ownerModContainer;
    }

    /**
     * Path-mapping method capable of handling both standard and nested mods within scanned JAR files. Additionally,
     * handles caching annotation data.
     * <br></br>
     * Has an overall O(m * (n + p)) time complexity (where {@code m} is the number of root paths (usually 1) in the given
     * {@code targetModContainer}, {@code n} is the number of files/paths per root path, and {@code p} is the number
     * of {@code class} files scanned (for annotation parsing)).
     *
     * @param targetModContainer The {@link ModContainer} to index the paths of. Typically defaults to the owning JAR
     *                           file of this instance's {@link #ownerModContainer}.
     *
     * @return A newly-computed {@link ObjectArrayList} of all formatted paths within a mod's JAR file, or
     * an empty/incomplete {@link ObjectArrayList} if some exception is caught.
     */
    public ObjectArrayList<String> mapAllFilePaths(ModContainer targetModContainer) {
        ObjectArrayList<String> allFilePathsLocal = new ObjectArrayList<>();

        targetModContainer.getRootPaths().forEach(rootPath -> {
            try {
                if (Files.isDirectory(rootPath)) processDirectory(rootPath, allFilePathsLocal, targetModContainer);
                else if (rootPath.toString().endsWith(".jar")) processJar(rootPath, allFilePathsLocal, targetModContainer);
            } catch (IOException e) {
                NexusConstants.LOGGER.error("Failed to load mod data for mod {}", targetModContainer.getMetadata().getId(), e);
            }
        });

        return allFilePathsLocal;
    }

    @Override
    public @NotNull ModMetadata getModMetadata() {
        return ownerModMetadata;
    }

    @Override
    public List<String> getAllFilePaths() {
        return allFilePaths;
    }

    @Override
    public ConcurrentHashMap<String, ObjectArrayList<String>> getCachedAnnotatedClasses() {
        return cachedAnnotatedClasses;
    }

    @Override
    public List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Comparator<String> classLoadingSorter, @Nullable Consumer<String> beforeClassInitConsumer) {
        String formattedAnnotationName = "L" + annotationTypeClazz.getName().replace('.', '/') + ";";

        return cachedAnnotatedClasses.get(formattedAnnotationName) == null ? ObjectArrayList.of() : cachedAnnotatedClasses.get(formattedAnnotationName)
                .stream()
                .sorted(classLoadingSorter != null ? classLoadingSorter : String::compareTo)
                .peek(name -> {
                    if (beforeClassInitConsumer != null) beforeClassInitConsumer.accept(name);
                })
                .map(ClassFinder::forName)
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    /**
     * Delegator method for processing paths found within a directory, primarily intended for use in-dev (particularly
     * for scanning the {@code "build/"} directory).
     * <br></br>
     * Handles edge cases, formats files, and caches annotation data. Additionally, deduplicates paths (which for some
     * reason happen to be on Fabric - where paths are recursively added with 1 package pruned from the left) with
     * roughly O(n * log(n) + n * k) time complexity (where {@code n} is the number of files/paths, including
     * subdirectories, and {@code k} is the average length of all file paths).
     *
     * @param rootDir The mod's root directory (usually {@code "build/"}, but anything goes).
     * @param allFilePathsLocal The {@link ObjectArrayList} to store all formatted paths found within the mod's JAR file.
     * @param targetModContainer The {@link ModContainer} to index the paths of (typically just the {@code main} mod
     *                           in the dev environment, AKA your current mod).
     *
     * @throws IOException If some exception is caught while reading the mod's root directory.
     *
     * @see #processClassAnnotations(String, InputStream, ModContainer)
     */
    protected void processDirectory(Path rootDir, ObjectArrayList<String> allFilePathsLocal, ModContainer targetModContainer) throws IOException {
        ObjectArrayList<String> potentialPaths; // All paths found in the directory, including the weird duplicates Fabric slaps in there for some reason

        try (Stream<Path> paths = Files.walk(rootDir)) {
            potentialPaths = paths
                    .filter(Files::isRegularFile)
                    .map(rootDir::relativize)
                    .map(Path::toString)
                    .collect(Collectors.toCollection(ObjectArrayList::new));
        } catch (IOException e) {
            NexusConstants.LOGGER.error("Failed to load mod data for mod {} (Failed to read data from mod's root directory)", targetModContainer.getMetadata().getId(), e);
            return;
        }

        potentialPaths.sort(Comparator.comparingInt(String::length).reversed()); // Sort recursive paths by length (longest to shortest)

        ObjectArrayList<String> acceptedPaths = filterDuplicates(potentialPaths);

        for (String pathStr : acceptedPaths) {
            if (pathStr.endsWith(".class")) {
                String className = pathStr.substring(0, pathStr.length() - 6).replace('/', '.');

                try (InputStream is = Files.newInputStream(rootDir.resolve(pathStr.replace('/', File.separatorChar)))) {
                    processClassAnnotations(className, is, targetModContainer);
                } catch (IOException e) {
                    NexusConstants.LOGGER.error("Failed to read class file {} in mod {}", pathStr, targetModContainer.getMetadata().getId(), e);
                }
                allFilePathsLocal.add(className);
            } else allFilePathsLocal.add(pathStr);
        }
    }

    /**
     * Delegator method for processing paths found within a JAR file (typically for prod).
     * <br></br>
     * Handles edge cases, formats files, and caches annotation data. Unlike
     * {@link #processDirectory(Path, ObjectArrayList, ModContainer)}, this doesn't have to deal with Fabric's odd recursive
     * path duplication, and it thus has a time complexity of roughly O(n * k), where {@code n} is the number of
     * files/paths within the target JAR, and {@code k} is the average processing time of each JAR file (if any nested
     * JARs are present).
     *
     * @param jarPath The {@link Path} to the root JAR file.
     * @param allFilePathsLocal The {@link ObjectArrayList} to store all formatted paths found within the mod's JAR file.
     * @param targetModContainer The target {@link ModContainer} to index the paths of.
     *
     * @throws IOException If some exception is caught while reading the mod's root JAR file.
     */
    protected void processJar(Path jarPath, ObjectArrayList<String> allFilePathsLocal, ModContainer targetModContainer) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(jarPath, (ClassLoader) null)) {
            for (Path root : fs.getRootDirectories()) {
                try (Stream<Path> paths = Files.walk(root)) {
                    paths
                            .filter(Files::isRegularFile)
                            .map(root::relativize)
                            .map(Path::toString)
                            .filter(pathStr -> !pathStr.isEmpty())
                            .forEach(pathStr -> {
                                if (pathStr.endsWith(".class")) {
                                    String className = pathStr.substring(0, pathStr.length() - 6).replace('/', '.');

                                    try (InputStream is = Files.newInputStream(root.resolve(pathStr))) {
                                        processClassAnnotations(className, is, targetModContainer);
                                    } catch (IOException e) {
                                        NexusConstants.LOGGER.error("Failed to read class file {} from JAR {} in mod {}", pathStr, jarPath, targetModContainer.getMetadata().getId(), e);
                                    }
                                    allFilePathsLocal.add(className);
                                } else allFilePathsLocal.add(pathStr);
                            });
                }
            }
        }
    }

    /**
     * Directly processes a {@code class} file's annotations using {@link ClassReader} and {@link ClassVisitor}.
     * <br></br>
     * Prunes all {@code class} byte code metadata to only retain the name and annotations for maximum O(n) performance.
     *
     * @param className The name of the target {@code class} file.
     * @param classInputStream The converted {@code class} file's byte code.
     * @param targetModContainer The target {@link ModContainer} to index the paths of (primarily for logging purposes).
     */
    protected void processClassAnnotations(String className, InputStream classInputStream, ModContainer targetModContainer) {
        try {
            ClassReader targetClassReader = new ClassReader(classInputStream);
            ClassVisitor targetClassVisitor = new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    cachedAnnotatedClasses.computeIfAbsent(descriptor, (oK) -> new ObjectArrayList<>())
                            .add(className);
                    return super.visitAnnotation(descriptor, visible);
                }
            };

            targetClassReader.accept(targetClassVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES); // Retain only name and annotation metadata per class
        } catch (IOException e) {
            NexusConstants.LOGGER.error("Failed to initialize ClassReader for class {} in mod {}", className, targetModContainer.getMetadata().getId(), e);
        }
    }

    /**
     * De-duplicates paths found within the provided {@code potentialPaths} (typically from a mod's JAR file).
     * <br></br>
     * Averages approximately O(n * log(n)) time complexity, where {@code n} is the total number of paths.
     *
     * @param potentialPaths The overall path {@link ObjectArrayList}.
     *
     * @return The de-duplicated path {@link ObjectArrayList}.
     */
    protected static @NotNull ObjectArrayList<String> filterDuplicates(ObjectArrayList<String> potentialPaths) {
        ObjectArrayList<String> acceptedPaths = new ObjectArrayList<>();
        StringBuilder pathBuilder = new StringBuilder(256); // Re-use StringBuilder to reduce memory allocation

        for (String rawPath : potentialPaths) {
            if (rawPath.isEmpty()) continue;

            pathBuilder.setLength(0);

            for (int i = 0; i < rawPath.length(); i++) {
                char c = rawPath.charAt(i);
                pathBuilder.append(c == File.separatorChar ? '/' : c);
            }

            String currentPath = pathBuilder.toString();

            boolean isContained = false;

            for (String acceptedPath : acceptedPaths) {
                if (acceptedPath.contains(currentPath)) {
                    isContained = true;
                    break;
                }
            }

            if (!isContained) acceptedPaths.add(currentPath);
        }
        return acceptedPaths;
    }
}
