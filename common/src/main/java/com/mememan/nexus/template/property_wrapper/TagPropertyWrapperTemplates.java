package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapperBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link TagPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class TagPropertyWrapperTemplates {

    private TagPropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (TagPropertyWrapperTemplates)");
    }

    /**
     * Creates and returns a {@link Supplier} that provides a {@link TagKey} for the specified registry and tag location.
     *
     * @param parentRegistryKey The {@link ResourceKey} of the parent registry containing the target tag.
     * @param tagLoc The {@link ResourceLocation} of the target tag.
     *
     * @return A {@link Supplier} that provides the created {@link TagKey}.
     *
     * @param <T> The type of objects in the registry.
     * @param <R> The registry type that extends {@link Registry}.
     */
    public static <T, R extends Registry<T>> Supplier<TagKey<T>> registerTagKey(ResourceKey<R> parentRegistryKey, ResourceLocation tagLoc) {
        return () -> TagKey.create(parentRegistryKey, tagLoc);
    }

    /**
     * Registers a {@link TagKey} and returns a {@link TagPropertyWrapperBuilder} for chaining additional configuration.
     * Creates a new {@link TagPropertyWrapper} with the registered tag and returns its builder for further customization.
     *
     * @param parentRegistryKey The {@link ResourceKey} of the parent registry containing the target tag.
     * @param tagLoc The {@link ResourceLocation} of the target tag.
     *
     * @return A {@link TagPropertyWrapperBuilder} for the newly registered tag, allowing for chained configuration.
     *
     * @param <T> The type of objects in the registry.
     * @param <R> The registry type that extends {@link Registry}.
     */
    public static <T, R extends Registry<T>> TagPropertyWrapperBuilder<T, TagKey<T>> registerAndChain(ResourceKey<R> parentRegistryKey, ResourceLocation tagLoc) {
        Supplier<TagKey<T>> registeredTagKey = registerTagKey(parentRegistryKey, tagLoc);

        return new TagPropertyWrapper<>(registeredTagKey, tagLoc.getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceKey, ResourceLocation)}. Registers a {@link TagKey} and
     * returns a {@link TagPropertyWrapperBuilder} that inherits configuration from the provided template. Creates a new
     * {@link TagPropertyWrapper} with the registered tag, copies configuration from the template, and returns its builder
     * for further customization.
     *
     * @param parentRegistryKey The {@link ResourceKey} of the parent registry containing the target tag.
     * @param tagLoc The {@link ResourceLocation} of the target tag.
     * @param templateTPW The {@link TagPropertyWrapper} template to copy configuration from.
     *
     * @return A {@link TagPropertyWrapperBuilder} for the newly registered tag, inheriting configuration from the template.
     *
     * @param <T> The type of objects in the registry.
     * @param <R> The registry type that extends {@link Registry}.
     */
    public static <T, R extends Registry<T>> TagPropertyWrapperBuilder<T, TagKey<T>> registerAndChain(ResourceKey<R> parentRegistryKey, ResourceLocation tagLoc, TagPropertyWrapper<T, TagKey<T>> templateTPW) {
        return registerAndChain(parentRegistryKey, tagLoc)
                .copyFrom(templateTPW);
    }

    /**
     * Creates a {@link TagPropertyWrapperBuilder} from a pre-registered {@link TagKey} {@link Supplier} for chaining
     * additional configuration. Wraps an existing {@link TagKey} {@link Supplier} in a new {@link TagPropertyWrapper}
     * and returns its builder.
     *
     * @param registeredParentTagKey The {@link Supplier} providing the pre-registered {@link TagKey}.
     *
     * @return A {@link TagPropertyWrapperBuilder} for the provided tag, allowing for chained configuration.
     *
     * @param <T> The type of objects in the registry.
     */
    public static <T> TagPropertyWrapperBuilder<T, TagKey<T>> registerAndChain(Supplier<TagKey<T>> registeredParentTagKey) {
        return new TagPropertyWrapper<>(registeredParentTagKey, registeredParentTagKey.get().location().getNamespace())
                .builder();
    }

    /**
     * Overloaded variant of {@link #registerAndChain(Supplier)}. Creates a {@link TagPropertyWrapperBuilder} from a
     * pre-registered {@link TagKey} {@link Supplier} that inherits configuration from the provided template. Wraps an
     * existing {@link TagKey} {@link Supplier} in a new {@link TagPropertyWrapper}, copies configuration from the template,
     * and returns its builder for further customization.
     *
     * @param registeredParentTagKey The {@link Supplier} providing the pre-registered {@link TagKey}.
     * @param templateTPW The {@link TagPropertyWrapper} template to copy configuration from.
     *
     * @return A {@link TagPropertyWrapperBuilder} for the provided tag, inheriting configuration from the template.
     *
     * @param <T> The type of objects in the registry.
     */
    public static <T> TagPropertyWrapperBuilder<T, TagKey<T>> registerAndChain(Supplier<TagKey<T>> registeredParentTagKey, TagPropertyWrapper<T, TagKey<T>> templateTPW) {
        return registerAndChain(registeredParentTagKey)
                .copyFrom(templateTPW);
    }
}
