package com.mememan.nexus.property_wrapper.base.generic.misc;

import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.tag.DefaultableTagBasedPropertyWrapperBuilder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Delegate extension of {@link DefaultableLanguageBasedPropertyWrapperBuilder} and {@link DefaultableTagBasedPropertyWrapperBuilder}.
 * <br></br>
 * Primarily used for objects like {@linkplain Enchantment Enchantments} and {@linkplain MobEffect MobEffects} that
 * don't need any custom resources or data to be generated for them besides localization and tags.
 * <br></br>
 * Contains appropriate override for {@linkplain #copyFrom(DBDGPW)} for implementations.
 *
 * @see DefaultableBareDataGenPropertyWrapper
 */
public interface DefaultableBareDataGenPropertyWrapperBuilder<T, SELF extends DefaultableBareDataGenPropertyWrapperBuilder<T, SELF, DBDGPW>, DBDGPW extends DefaultableBareDataGenPropertyWrapper<T, DBDGPW, SELF>> extends DefaultableLanguageBasedPropertyWrapperBuilder<T, SELF, DBDGPW>, DefaultableTagBasedPropertyWrapperBuilder<T, SELF, DBDGPW> {

    @Override
    default SELF copyFrom(DBDGPW propertyWrapper) {
        DefaultableLanguageBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        DefaultableTagBasedPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return self();
    }
}
