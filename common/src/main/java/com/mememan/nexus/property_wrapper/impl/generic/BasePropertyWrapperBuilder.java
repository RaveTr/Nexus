package com.mememan.nexus.property_wrapper.impl.generic;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Base implementation for Property Wrapper Builders, implementing {@link PropertyWrapperBuilder}.
 *
 * @see BasePropertyWrapper
 */
public class BasePropertyWrapperBuilder<T, SELF extends PropertyWrapperBuilder<T, SELF, PW>, PW extends PropertyWrapper<T, PW, SELF>> implements PropertyWrapperBuilder<T, SELF, PW> {
    @NotNull
    protected final PW ownerWrapper;
    protected final List<BiFunction<Supplier<T>, SELF, SELF>> contextualizedBuilders = new ObjectArrayList<>();

    public BasePropertyWrapperBuilder(@NotNull PropertyWrapper<T, PW, SELF> ownerWrapper) {
        this.ownerWrapper = (PW) ownerWrapper;
    }

    @Override
    public SELF copyFrom(PW propertyWrapper) {
        contextualizedBuilders.clear();
        contextualizedBuilders.addAll(propertyWrapper.rawBuilder().map(PropertyWrapperBuilder::getContextualizedBuilders).orElse(ObjectArrayList.of()));

        return self();
    }

    @Override
    public SELF compose(BiFunction<Supplier<T>, SELF, SELF> contextualizedBuilder) {
        contextualizedBuilders.add(contextualizedBuilder);
        return self();
    }

    @Override
    public PW build() {
        if (!ownerWrapper.isTemplate()) contextualizedBuilders.forEach(builder -> builder.apply(ownerWrapper.getParentObject(), self()));
        return PropertyWrapper.PropertyWrappersContainer.registerPropertyWrapper(ownerWrapper.getParentObject(), ownerWrapper);
    }

    @Override
    public @NotNull PW getCurrentOwnerWrapper() {
        return ownerWrapper;
    }

    @Override
    public List<BiFunction<Supplier<T>, SELF, SELF>> getContextualizedBuilders() {
        return new ObjectArrayList<>(contextualizedBuilders);
    }

    @Override
    public SELF clone() {
        try {
            SELF self = (SELF) super.clone();

            return self.copyFrom(ownerWrapper);
        } catch (CloneNotSupportedException e) {
            throw new CloneFailedException("Failed to clone property wrapper builder", e);
        }
    }
}
