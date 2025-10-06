package com.mememan.nexus.datagen;

/**
 * Basic object-holder {@code enum} representing different policies for handling potential duplicate objects that appear
 * throughout datagen using Nexus API.
 * <br></br>
 * Note that this only affects Nexus API's native datagen. Dependant mods will have to implement these themselves for
 * custom providers.
 */
public enum DuplicateDataPolicy {
    /**
     * Nexus datagen will crash upon encountering any instance of object duplication.
     */
    CRASH,
    /**
     * Nexus will simply warn the end-developer in the console during datagen and skip generating any found duplicates.
     */
    EXCLUDE_WARN,
    /**
     * Nexus will silently skip generating any duplicate objects altogether.
     */
    EXCLUDE_SILENT,
    /**
     * Nexus will simply warn the end-developer in the console during datagen and override the existing object with its
     * next duplicate instance. Note that there will still only be 1 instance of said object, that being the latest one.
     */
    OVERRIDE_WARN,
    /**
     * Nexus will silently override the existing object with its duplicate. Note that there will still only be 1 instance
     * of said object, that being the latest one.
     */
    OVERRIDE_SILENT
}
