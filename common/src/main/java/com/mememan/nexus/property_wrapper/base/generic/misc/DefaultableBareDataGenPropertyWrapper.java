package com.mememan.nexus.property_wrapper.base.generic.misc;

import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.tag.DefaultableTagBasedPropertyWrapper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Delegate extension for {@link DefaultableLanguageBasedPropertyWrapper} and {@link DefaultableTagBasedPropertyWrapper}.
 * <br></br>
 * Primarily used for objects like {@linkplain Enchantment Enchantments} and {@linkplain MobEffect MobEffects} that
 * don't need any custom resources or data to be generated for them besides localization and tags.
 *
 * @see DefaultableBareDataGenPropertyWrapperBuilder
 */
public interface DefaultableBareDataGenPropertyWrapper<T, SELF extends DefaultableBareDataGenPropertyWrapper<T, SELF, BUILDER>, BUILDER extends DefaultableBareDataGenPropertyWrapperBuilder<T, BUILDER, SELF>> extends DefaultableLanguageBasedPropertyWrapper<T, SELF, BUILDER>, DefaultableTagBasedPropertyWrapper<T, SELF, BUILDER> {
}
