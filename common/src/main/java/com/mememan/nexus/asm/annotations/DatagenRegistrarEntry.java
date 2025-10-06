package com.mememan.nexus.asm.annotations;

import com.mememan.nexus.platform.services.DataGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used by loader-specific implementations of {@link DataGenerator} in order to safely load data provider
 * registry classes after the global {@link net.minecraft.data.DataGenerator} (or its loader-specific subtype(s)) has
 * been initialized to allow for proper registration context.
 *
 * @see DataGenerator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DatagenRegistrarEntry {
}
