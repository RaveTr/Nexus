package com.mememan.nexus.property_wrapper.base.generic.misc;

import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.model.DefaultableModelBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.recipe.DefaultableRecipeBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.tag.DefaultableTagBasedPropertyWrapper;

/**
 * Shortcut delegator {@code interface} that combines all datagen-based defaultable PW {@code interface}s through
 * recursive generics.
 *
 * @see DefaultableDataGenPropertyWrapperBuilder
 * @see DefaultableLanguageBasedPropertyWrapper
 * @see DefaultableLootBasedPropertyWrapper
 * @see DefaultableModelBasedPropertyWrapper
 * @see DefaultableRecipeBasedPropertyWrapper
 * @see DefaultableTagBasedPropertyWrapper
 */
public interface DefaultableDataGenPropertyWrapper<T, SELF extends DefaultableDataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends DefaultableDataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends DefaultableLanguageBasedPropertyWrapper<T, SELF, BUILDER>, DefaultableLootBasedPropertyWrapper<T, SELF, BUILDER>, DefaultableModelBasedPropertyWrapper<T, SELF, BUILDER>, DefaultableRecipeBasedPropertyWrapper<T, SELF, BUILDER>, DefaultableTagBasedPropertyWrapper<T, SELF, BUILDER> {
}
