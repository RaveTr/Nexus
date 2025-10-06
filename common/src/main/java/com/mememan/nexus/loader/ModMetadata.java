package com.mememan.nexus.loader;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/**
 * Data-holding {@code record} representing mod metadata, commonly defined in each loader's respective MTD files
 * ({@code fabric.mod.json}, {@code mods.toml}, etc.).
 *
 * @param modId The mod's ID, following standard <code>[a-z0-9_.-]</code> convention.
 * @param modName The mod's display name.
 * @param modVersion The mod's version. This is typically a string-ified version of {@code ArtifactVersion},
 *                   since its owning library isn't included with VanillaGradle.
 * @param modLicense The mod's license (only the license's title, NOT the full license!).
 * @param modDescription The mod's description. Usually a multi-line string.
 * @param modAuthors The mod's authors. May be empty if none are defined.
 * @param modDependencies The mod's dependencies, represented as a {@link List} of {@linkplain Triple Triples}
 *                        comprised of the dependency's mod ID, version range(s), and whether the dependency is required.
 *                        May be empty if none are defined. The {@link RangeSet} here may be unbounded/empty.
 * @param modSide The mod's target environment side.
 *
 * @apiNote Each dependency version range will contain versions formatted as {@code "[minVersion..+∞)"} for lower bounds,
 * {@code "(-∞..maxVersion]"} for upper bounds, and {@code "[minVersion..maxVersion)"} for both bounds (Note that the
 * usage of square/standard brackets depends on whether each bound is inclusive "[]" or exclusive "()"). See
 * {@link Range} for more info.
 */
public record ModMetadata(String modId, String modName, String modVersion, String modLicense, String modDescription,
                          List<String> modAuthors, List<Triple<String, RangeSet<String>, Boolean>> modDependencies,
                          ModSide modSide) {
}
