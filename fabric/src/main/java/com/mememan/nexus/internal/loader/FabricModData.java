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
import java.lang.annotation.Annotation;
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
     *
     * @param targetModContainer The {@link ModContainer} to index the paths of. Typically defaults to the owning JAR
     *                           file of this instance's {@link #ownerModContainer}.
     *
     * @return A newly-computed {@link ObjectArrayList} of all formatted paths within a mod's JAR file, or
     * an empty/incomplete {@link ObjectArrayList} if some exception is caught.
     */
    public ObjectArrayList<String> mapAllFilePaths(ModContainer targetModContainer) {
        ObjectArrayList<String> allFilePathsLocal = new ObjectArrayList<>();

        targetModContainer.getRootPaths().forEach(rootPath -> { // We can handle all edge-cases for both standard and nested mods
            try (Stream<Path> allPaths = Files.walk(rootPath)) {
                allPaths.forEach(curSuperPath -> {
                    if (curSuperPath.endsWith(".jar")) { // In prod (or a nested mod JAR)
                        try (Stream<Path> nestedPaths = Files.walk(curSuperPath)) {
                            nestedPaths
                                    .filter(curPath -> curPath.getNameCount() > 0)
                                    .filter(Files::isRegularFile)
                                    .map(curPath -> curPath.toString().replace('/', '.'))
                                    .map(curPathString -> curPathString.substring(1)) // The very first char is always going to be an unnecessary '.'
                                    .filter(pkg-> !pkg.isEmpty())
                                    .peek(curPathString -> {
                                        if (curPathString.endsWith(".class")) {
                                            String formattedPathString = curPathString.substring(0, curPathString.lastIndexOf("."));

                                            try {
                                                ClassReader targetClassReader = new ClassReader(formattedPathString);
                                                ClassVisitor targetClassVisitor = new ClassVisitor(Opcodes.ASM9) {

                                                    @Override
                                                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                                        cachedAnnotatedClasses.computeIfAbsent(descriptor, (oK) -> new ObjectArrayList<>())
                                                                .add(formattedPathString);
                                                        return super.visitAnnotation(descriptor, visible);
                                                    }
                                                };

                                                targetClassReader.accept(targetClassVisitor, 0);
                                            } catch (IOException e) {
                                                NexusConstants.LOGGER.error("Failed to initialize ClassReader for class {} in mod {}", formattedPathString, targetModContainer.getMetadata().getId(), e);
                                            }
                                        }
                                    })
                                    .forEach(allFilePathsLocal::add);
                        } catch (IOException e) {
                            NexusConstants.LOGGER.error("Failed to load mod data for mod {}", targetModContainer.getMetadata().getId(), e);
                        }
                    } else if (Files.isDirectory(curSuperPath)) { // In the dev environment
                        try (Stream<Path> nestedPaths = Files.walk(curSuperPath)) {
                            nestedPaths
                                    .filter(Files::isRegularFile)
                                    .map(curPath -> curSuperPath.relativize(curPath).toString().replace(File.separatorChar, '.').replace('/', '.')) // We need that last replace call if we're in the dev environment
                                    .filter(pkg-> !pkg.isEmpty())
                                    .filter(curPathString -> allFilePathsLocal.stream().noneMatch(curNestedPathString -> curNestedPathString.contains(curPathString))) // Patch: In-dev, for whatever reason, directories are recursively added with one package name pruned each time, so we want to avoid that
                                    .peek(curPathString -> {
                                        if (curPathString.endsWith(".class")) {
                                            String formattedPathString = curPathString.substring(0, curPathString.lastIndexOf("."));

                                            try {
                                                ClassReader targetClassReader = new ClassReader(formattedPathString);
                                                ClassVisitor targetClassVisitor = new ClassVisitor(Opcodes.ASM9) {

                                                    @Override
                                                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                                        cachedAnnotatedClasses.computeIfAbsent(descriptor, (oK) -> new ObjectArrayList<>())
                                                                .add(formattedPathString);
                                                        return super.visitAnnotation(descriptor, visible);
                                                    }
                                                };

                                                targetClassReader.accept(targetClassVisitor, 0);
                                            } catch (IOException e) {
                                                NexusConstants.LOGGER.error("Failed to initialize ClassReader for class {} within in-dev mod {}", formattedPathString, targetModContainer.getMetadata().getId(), e);
                                            }
                                        }
                                    })
                                    .forEach(allFilePathsLocal::add);
                        } catch (IOException e) {
                            NexusConstants.LOGGER.error("Failed to load mod data for in-dev mod {}", targetModContainer.getMetadata().getId(), e);
                        }
                    }
                });
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
}
