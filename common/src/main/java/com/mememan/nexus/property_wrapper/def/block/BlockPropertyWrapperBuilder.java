package com.mememan.nexus.property_wrapper.def.block;

import com.mememan.nexus.property_wrapper.base.generic.DefaultableDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapperBuilder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockPropertyWrapperBuilder<B extends Block> extends BaseDataGenPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> implements DefaultableDataGenPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> {
    protected final SpecializedLanguagePropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> compositeLanguageBuilder;
    protected final SpecializedLootPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> compositeLootBuilder;
    protected final SpecializedModelPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> compositeModelBuilder;
    protected final SpecializedRecipePropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> compositeRecipeBuilder;
    protected final SpecializedTagPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>> compositeTagBuilder;

    public BlockPropertyWrapperBuilder(@NotNull BlockPropertyWrapper<B> ownerWrapper) {
        super(ownerWrapper);

        this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get();
        this.compositeLootBuilder = (SpecializedLootPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedLootWrapper().map(SpecializedLootPropertyWrapper::builder).get();
        this.compositeModelBuilder = (SpecializedModelPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedModelWrapper().map(SpecializedModelPropertyWrapper::builder).get();
        this.compositeRecipeBuilder = (SpecializedRecipePropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedRecipeWrapper().map(SpecializedRecipePropertyWrapper::builder).get();
        this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
    }

    @Override
    public BlockPropertyWrapperBuilder<B> copyFrom(BlockPropertyWrapper<B> propertyWrapper) {
        DefaultableDataGenPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
        return super.copyFrom(propertyWrapper);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedLanguageBuilder() {
        return Optional.of(compositeLanguageBuilder);
    }

    @Override
    public Optional<SpecializedLootPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedLootBuilder() {
        return Optional.of(compositeLootBuilder);
    }

    @Override
    public Optional<SpecializedModelPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedModelBuilder() {
        return Optional.of(compositeModelBuilder);
    }

    @Override
    public Optional<SpecializedRecipePropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedRecipeBuilder() {
        return Optional.of(compositeRecipeBuilder);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapperBuilder<B, BlockPropertyWrapperBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedTagBuilder() {
        return Optional.of(compositeTagBuilder);
    }
}
