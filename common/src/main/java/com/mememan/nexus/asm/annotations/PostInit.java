package com.mememan.nexus.asm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation {@code interface} used to statically-initialize annotated classes after mod-loading is complete. May be
 * used for miscellaneous cases, like adding event listeners for post-load events.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PostInit {
}
