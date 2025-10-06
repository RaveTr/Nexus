package com.mememan.nexus.property_wrapper.base.generic.misc;

import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.model.DefaultableModelBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.recipe.DefaultableRecipeBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.tag.DefaultableTagBasedPropertyWrapperBuilder;

/**
 * Shortcut delegator {@code interface} that combines all datagen-based defaultable PW {@code interface}s through
 * recursive generics.
 *
 * @see DefaultableDataGenPropertyWrapper
 * @see DefaultableLanguageBasedPropertyWrapperBuilder
 * @see DefaultableLootBasedPropertyWrapperBuilder
 * @see DefaultableModelBasedPropertyWrapperBuilder
 * @see DefaultableRecipeBasedPropertyWrapperBuilder
 * @see DefaultableTagBasedPropertyWrapperBuilder
 */
public interface DefaultableDataGenPropertyWrapperBuilder<T, SELF extends DefaultableDataGenPropertyWrapperBuilder<T, SELF, DDGPW>, DDGPW extends DefaultableDataGenPropertyWrapper<T, DDGPW, SELF>> extends DefaultableLanguageBasedPropertyWrapperBuilder<T, SELF, DDGPW>, DefaultableLootBasedPropertyWrapperBuilder<T, SELF, DDGPW>, DefaultableModelBasedPropertyWrapperBuilder<T, SELF, DDGPW>, DefaultableRecipeBasedPropertyWrapperBuilder<T, SELF, DDGPW>, DefaultableTagBasedPropertyWrapperBuilder<T, SELF, DDGPW> {

    @Override
    default SELF copyFrom(DDGPW propertyWrapper) {
        DefaultableLanguageBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        DefaultableLootBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        DefaultableModelBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        DefaultableRecipeBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        DefaultableTagBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return self();
    }
}
