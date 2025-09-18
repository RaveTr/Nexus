package com.mememan.nexus.property_wrapper.impl.generic.misc;

import com.mememan.nexus.property_wrapper.base.generic.misc.DefaultableBareDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.misc.DefaultableBareDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BaseDefaultableBareDataGenPropertyWrapperBuilder<T, SELF extends DefaultableBareDataGenPropertyWrapperBuilder<T, SELF, DBDGPW>, DBDGPW extends DefaultableBareDataGenPropertyWrapper<T, DBDGPW, SELF>> extends BaseDataGenPropertyWrapperBuilder<T, SELF, DBDGPW> implements DefaultableBareDataGenPropertyWrapperBuilder<T, SELF, DBDGPW> {
    protected final SpecializedLanguagePropertyWrapperBuilder<T, SELF, DBDGPW> compositeLanguageBuilder;
    protected final SpecializedTagPropertyWrapperBuilder<T, SELF, DBDGPW> compositeTagBuilder;
    
    public BaseDefaultableBareDataGenPropertyWrapperBuilder(@NotNull DBDGPW ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<T, SELF, DBDGPW>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get();
        this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<T, SELF, DBDGPW>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
    }

    @Override
    public SELF copyFrom(DBDGPW propertyWrapper) {
        DefaultableBareDataGenPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<T, SELF, DBDGPW>> getSpecializedLanguageBuilder() {
        return Optional.of(compositeLanguageBuilder);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapperBuilder<T, SELF, DBDGPW>> getSpecializedTagBuilder() {
        return Optional.of(compositeTagBuilder);
    }
}
