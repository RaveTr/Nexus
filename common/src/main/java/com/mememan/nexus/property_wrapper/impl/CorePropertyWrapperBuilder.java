package com.mememan.nexus.property_wrapper.impl;

import com.mememan.nexus.property_wrapper.base.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.PropertyWrapperBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Core implementation of Property Wrapper Builders that uses composition to attach properties from specialised property
 * wrappers. Utilises composition to attach several specialised builders rather than implementing them directly in order
 * to minimize tech debt and improve maintainability (composition).
 *
 * @see CorePropertyWrapper
 */
public class CorePropertyWrapperBuilder<T, SELF extends CorePropertyWrapperBuilder<T, SELF, CPW>, CPW extends CorePropertyWrapper<T, CPW, SELF>> extends BasePropertyWrapperBuilder<T, SELF, CPW> {
    protected final Set<PropertyWrapperBuilder<T, ?, ?>> configuredBuilders = ObjectOpenHashSet.of();

    public CorePropertyWrapperBuilder(@NotNull PropertyWrapper<T, CPW, SELF> ownerWrapper) {
        super(ownerWrapper);
    }

    @Override
    public SELF copyFrom(CPW propertyWrapper) {
        return super.copyFrom(propertyWrapper)
                .setBuilders(ObjectOpenHashSet.of(propertyWrapper.getConfiguredBuilders().toArray(PropertyWrapperBuilder[]::new)));
    }

    /**
     * Adds a configured builder to this CorePropertyWrapperBuilder instance.
     *
     * @param builder
     *
     * @return
     * 
     * @param <BUILDER>
     */
    public <BUILDER extends PropertyWrapperBuilder<T, ?, ?>> SELF withBuilder(BUILDER builder) {
        if (builder != null) configuredBuilders.add(builder.clone());
        return self();
    }

    public <PW extends PropertyWrapper<T, ?, ?>, BUILDER extends PropertyWrapperBuilder<T, ?, ?>> SELF withBuilderFrom(PW propertyWrapper) {
        BUILDER builder = (BUILDER) propertyWrapper.rawBuilder().map(curBuilder -> curBuilder.clone()).orElse(null);

        if (builder != null) configuredBuilders.add(builder);

        return self();
    }

    public <BUILDER extends PropertyWrapperBuilder<T, ?, ?>> SELF withBuilders(Iterable<BUILDER> builders) {
        builders.forEach(this::withBuilder);
        return self();
    }

    public <PW extends PropertyWrapper<T, ?, ?>, BUILDER extends PropertyWrapperBuilder<T, ?, ?>> SELF withBuildersFrom(Iterable<PW> propertyWrappers) {
        propertyWrappers.forEach(this::withBuilderFrom);
        return self();
    }


    public <BUILDER extends PropertyWrapperBuilder<T, ?, ?>> SELF setBuilders(Iterable<BUILDER> builders) {
        configuredBuilders.clear();
        return withBuilders(builders);
    }

    public <PW extends PropertyWrapper<T, ?, ?>, BUILDER extends PropertyWrapperBuilder<T, ?, ?>> SELF setBuildersFrom(Iterable<PW> propertyWrappers) {
        configuredBuilders.clear();
        return withBuildersFrom(propertyWrappers);
    }

    @Override
    public CPW build() {
        configuredBuilders.forEach(PropertyWrapperBuilder::build);
        return super.build();
    }
}
