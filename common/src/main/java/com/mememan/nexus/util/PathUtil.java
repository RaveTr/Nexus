package com.mememan.nexus.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility {@code class} providing helper/shortcut methods revolving around Java {@linkplain Path Paths}.
 */
public final class PathUtil {

    private PathUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (PathUtil)");
    }

    /**
     * Truncates a specific component from a {@link Path} if it exists. For instance, specifying 'a' as the
     * {@code componentToRemove} from the {@link Path} "b/a/c" will return a new {@link Path} "b/c".
     *
     * @param path The {@link Path} to modify.
     * @param componentToRemove The component within the specified {@link Path} to remove.
     * @param specificIndexBound Specified to remove a specific number of occurrences of the target component to remove
     *                           rather than removing all instances of it. Setting this to a value <= 0 will just
     *                           remove all present instances.
     *
     * @return A {@link Path} with the specified component removed, or the same {@link Path} if the component doesn't
     * exist within said {@link Path}.
     */
    public static Path removePathComponent(Path path, String componentToRemove, int specificIndexBound) {
        if (path == null) return null;

        ObjectArrayList<Path> separatePathComponents = new ObjectArrayList<>();
        AtomicInteger removedInstances = specificIndexBound <= 0 ? new AtomicInteger(-1) : new AtomicInteger();

        for (int i = 0; i < path.getNameCount(); i++) {
            separatePathComponents.add(path.getName(i));
        }

        separatePathComponents.removeIf(targetComponent -> {
            if (removedInstances.get() >= 0 && targetComponent.toString().equals(componentToRemove)) removedInstances.getAndIncrement();
            return targetComponent.toString().equals(componentToRemove) && (removedInstances.get() < 0 || removedInstances.get() < specificIndexBound);
        });

        if (separatePathComponents.isEmpty()) return path.getRoot() != null ? path.getRoot() : path.getFileSystem().getPath("");

        Path resultPath = separatePathComponents.get(0);

        for (int i = 1; i < separatePathComponents.size(); i++) {
            resultPath = resultPath.resolve(separatePathComponents.get(i));
        }

        if (path.isAbsolute()) { // Handle absolute paths
            Path pathRoot = path.getRoot();

            return pathRoot != null ? pathRoot.resolve(resultPath) : resultPath;
        }

        return resultPath;
    }
}
