package com.mememan.nexus.util;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.services.EventBus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Utility {@code class} containing helpful shortcuts and relatively optimized checks for generic type info at runtime
 * and the likes.
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (ReflectionUtil)");
    }

    /**
     * Attempts to evaluate the functional {@code interface} class that a lambda implements. For internal use only.
     *
     * @param lambda The lambda or method reference to evaluate the functional {@code interface} of.
     *
     * @return The functional {@code interface} pertaining to the provided {@code lambda}.
     *
     * @throws IllegalArgumentException If the lambda's functional {@code interface} cannot be determined (usually only
     * really odd edge cases, should never hit under normal circumstances due to preliminary checks in
     * {@link EventBus#registerEventHook(Class, Function, ModSide)} implementations and overloads).
     */
    public static <T> Class<T> getFunctionalInterfaceClass(T lambda) {
        if (lambda == null) throw new NullPointerException("Attempted to evaluate functional interface for null lambda!");
        if (lambda instanceof Class) return (Class<T>) lambda; // For direct implementations

        Class<?>[] pertainingImplementedInterfaces = lambda.getClass().getInterfaces();

        if (pertainingImplementedInterfaces.length == 1) return (Class<T>) pertainingImplementedInterfaces[0]; // For lambdas and method references

        Class<?> superclass = lambda.getClass().getSuperclass();

        if (superclass != null && superclass != Object.class) {
            pertainingImplementedInterfaces = superclass.getInterfaces();

            if (pertainingImplementedInterfaces.length == 1) return (Class<T>) pertainingImplementedInterfaces[0]; // For method references on classes
        }

        throw new IllegalArgumentException(String.format("Could not determine functional interface for lambda function: %s", lambda));
    }

    public static boolean compareGenericInterfaceType(Class<?> baseInterface, Class<?> targetGenericType) {
        if (!baseInterface.isInterface()) return false;

        try {
            Type[] genericInterfaces = baseInterface.getGenericInterfaces();

            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType paramType) {
                    if (paramType.getRawType() == baseInterface) {
                        Type typeArg = paramType.getActualTypeArguments()[0];

                        if (typeArg instanceof Class) return typeArg == targetGenericType;
                        else if (typeArg instanceof ParameterizedType paramTypeArg) return paramTypeArg.getRawType() == targetGenericType;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            NexusConstants.LOGGER.warn("Failed to find generic type for interface '{}' (generic type being compared against: {}). Skipping...", baseInterface.getName(), targetGenericType.getName(), e);
            return false;
        }
    }
}
