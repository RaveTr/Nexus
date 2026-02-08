package com.mememan.nexus.internal.event.custom;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Function;

/**
 * Simplified wrapper to handle listeners for different event types implemented through their own functional interfaces
 * on Forge.
 *
 * @param <T> The event {@code interface} type. Should be a functional {@code interface}.
 */
public class ForgeEventWrapper<T> {
    protected final Class<? super T> eventInterface;
    protected final Function<T[], T> listenerResultMerger;
    protected final Map<Integer, T[]> listeners;
    protected final Object lock = new Object();
    protected volatile T invoker; // No need for atomic ops, but we still want some degree of thread-safety since this is mainly gonna be read via calls to invoker()

    public ForgeEventWrapper(Class<? super T> eventInterface, Function<T[], T> listenerResultMerger) {
        this.eventInterface = eventInterface;
        this.listenerResultMerger = listenerResultMerger;
        this.listeners = new Int2ObjectAVLTreeMap<>();
        this.invoker = createEmptyInvoker(eventInterface);
    }

    public void addListener(T listener, int priority) {
        synchronized (lock) {
            this.listeners.compute(priority, (curPriority, potentialListenerArray) -> {
                if (potentialListenerArray != null) {
                    int curListenerArrayLength = potentialListenerArray.length;
                    T[] newListenerArray = (T[]) Array.newInstance(eventInterface, curListenerArrayLength + 1);
                    System.arraycopy(potentialListenerArray, 0, newListenerArray, 0, curListenerArrayLength);
                    newListenerArray[curListenerArrayLength] = listener;

                    return newListenerArray;
                } else {
                    T[] newListenerArray = (T[]) Array.newInstance(eventInterface, 1);
                    newListenerArray[0] = listener;

                    return newListenerArray;
                }
            });

            T[] allListeners = (T[]) Array.newInstance(eventInterface, listeners.values().stream().mapToInt(lis -> lis.length).sum());

            int listenerIndex = 0;
            for (T[] priorityListeners : listeners.values()) {
                System.arraycopy(priorityListeners, 0, allListeners, listenerIndex, priorityListeners.length);
                listenerIndex += priorityListeners.length;
            }

            this.invoker = listenerResultMerger.apply(allListeners);
        }
    }

    public T invoker() {
        return invoker;
    }

    /**
     * Attempts to create an empty lambda invoker for the provided {@code targetFunctionalInterface}.
     *
     * @param targetFunctionalInterface The (assumed) functional {@code interface} type.
     *
     * @return An empty lambda invoker of type {@link T}.
     *
     * @param <T> The functional {@code interface} type.
     *
     * @throws RuntimeException Wrapped around some {@link Throwable}, indicating that an exception occurred at some point
     * during the instantiation of the empty invoker.
     */
    public static <T> T createEmptyInvoker(Class<? super T> targetFunctionalInterface) {
        try {
            Method method = targetFunctionalInterface.getDeclaredMethods()[0]; // Assume and get the SAM
            Class<?> returnType = method.getReturnType();

            return (T) targetFunctionalInterface.cast( // I haven't figured out how to use LambdaMetafactory without catching exceptions left and right :zadge:
                    Proxy.newProxyInstance(
                            targetFunctionalInterface.getClassLoader(),
                            new Class<?>[] { targetFunctionalInterface },
                            (proxy, m, args) -> {
                                if (returnType == boolean.class) return false;
                                if (returnType == void.class) return null;
                                if (returnType.isPrimitive()) return 0;

                                return null;
                            }
                    )
            );
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Failed to create empty invoker for type: %s", targetFunctionalInterface.getName()), t);
        }
    }
}
