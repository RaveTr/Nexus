package com.mememan.nexus.asm.annotations;

import com.mememan.nexus.asm.ClassFinder;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.services.Registrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used by loader-specific implementations of {@link Registrar} in order to discover and load registrar
 * classes annotated with this annotation.
 *
 * @see Registrar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RegistrarEntry {

    /**
     * Determines the ordinal priority this annotation's owning {@code class} should be loaded by relative to other
     * registrar entries.
     * <br></br>
     * By default, registrar entries are loaded lexicographically provided they share the same priority value with any
     * other registrar class(es) AND that their {@link #dependencies()} are either empty or all loaded/initialized.
     *
     * @return The priority value this annotation's owning {@code class} should be loaded by. Defaults to 0. Higher
     * values are initialized first.
     */
    int priority() default 0;

    /**
     * Specifies an array of classes that should be statically initialized before this annotation's owning {@code class}
     * is initialized.
     * <br></br>
     * Leaving this empty delegates the instantiation to {@link #priority()} and/or lexicographical ordering. Otherwise,
     * the classes within this array get {@linkplain ClassFinder#forName(String) initialized} before this annotation's
     * owning {@code class} is initialized.
     * <br></br>
     * Dependency classes will be initialized in the order they're declared in this array.
     *
     * @return An array of classes that should be statically initialized before this annotation's owning {@code class}
     * is initialized.
     *
     * @apiNote {@code SomeClass.class} loads classes into memory, but it does not initialize them. All this parameter
     * does is ensure that these classes are initialized before this annotation's owning {@code class} is initialized.
     *
     * @see #softDependencies()
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-12.html#jls-12.4.1">
     *     When Initialization Occurs (JLS)</a>
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-15.html#jls-15.8.2">Class Literals (JLS)</a>
     */
    Class[] dependencies() default {};

    /**
     * Specifies an array of fully-qualified {@code class} names to be statically-initialized before this annotation's
     * owning {@code class} is initialized.
     * <br></br>
     * Leaving this empty delegates the instantiation to {@link #priority()} and/or lexicographical ordering. Otherwise,
     * the classes within this array get {@linkplain ClassFinder#forName(String) initialized} before this annotation's
     * owning {@code class} is initialized. This is iterated over AFTER {@link #dependencies()}.
     * <br></br>
     * Dependency classes will be initialized in the order they're declared in this array. Unresolvable class names will
     * be gracefully skipped.
     *
     * @return An array of fully-qualified class names whose corresponding classes should be statically initialized
     * before this annotation's owning {@code class} is initialized.
     *
     * @see #dependencies()
     * @see Class#getName()
     */
    String[] softDependencies() default {};

    /**
     * Specifies the side on which this annotation's owning {@code class} should be initialized. Takes a {@link ModSide}
     * value, but this actually affects the physical init side. For instance, {@link ModSide#CLIENT} will cause the
     * owning {@code class} to be initialized on the physical client, and skip initialization on the dedicated server.
     *
     * @return The {@link ModSide} value this annotation's owning {@code class} should be initialized on. Defaults to
     * {@link ModSide#COMMON}, which initializes the owning {@code class} on both the client and server.
     */
    ModSide initSide() default ModSide.COMMON;
}
