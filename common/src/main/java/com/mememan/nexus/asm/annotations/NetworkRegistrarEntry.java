package com.mememan.nexus.asm.annotations;

import com.mememan.nexus.platform.services.NetworkManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation {@code interface} used by loader-specific implementations of {@link NetworkManager} in order to discover
 * and load network registrar classes annotated with this annotation.
 *
 * @see NetworkManager
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NetworkRegistrarEntry {
}
