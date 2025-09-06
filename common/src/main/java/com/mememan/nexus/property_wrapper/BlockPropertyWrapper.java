package com.mememan.nexus.property_wrapper;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DefaultableDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.generic.DefaultableDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockPropertyWrapper<B extends Block> extends BaseDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>> implements DefaultableDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>> {
    protected final SpecializedLanguagePropertyWrapper<B, ?, ?> compositeLanguageWrapper;
    protected final SpecializedLootPropertyWrapper<B, ?, ?> compositeLootWrapper;
    protected final SpecializedModelPropertyWrapper<B, ?, ?> compositeModelWrapper;
    protected final SpecializedRecipePropertyWrapper<B, ?, ?> compositeRecipeWrapper;
    protected final SpecializedTagPropertyWrapper<B, ?, ?> compositeTagWrapper;

    public BlockPropertyWrapper(Supplier<B> parentObject, boolean isTemplate, Function<BlockPropertyWrapper<B>, PropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> builderFactory, String modId) {
        super(parentObject, isTemplate, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BlockPropertyWrapper(@NotNull Supplier<B> parentObject, Function<BlockPropertyWrapper<B>, PropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> builderFactory, String modId) {
        super(parentObject, builderFactory, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public BlockPropertyWrapper(Function<BlockPropertyWrapper<B>, PropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> builderFactory) {
        super(builderFactory);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>();
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>();
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    public BlockPropertyWrapper(Supplier<B> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BPWBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BlockPropertyWrapper(@NotNull Supplier<B> parentObject, String modId) {
        super(parentObject, BPWBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public BlockPropertyWrapper() {
        super(BPWBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>();
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>();
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
    }

    @Override
    public Optional<ResourceKey<Registry<? super B>>> getObjectRegistryKey() {
        return DataGenPropertyWrapper.ofRegistryKey(Registries.BLOCK);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<B, ?, ?>> getSpecializedLanguageWrapper() {
        return Optional.of(compositeLanguageWrapper);
    }

    @Override
    public Optional<SpecializedLootPropertyWrapper<B, ?, ?>> getSpecializedLootWrapper() {
        return Optional.of(compositeLootWrapper);
    }

    @Override
    public Optional<SpecializedModelPropertyWrapper<B, ?, ?>> getSpecializedModelWrapper() {
        return Optional.of(compositeModelWrapper);
    }

    @Override
    public Optional<SpecializedRecipePropertyWrapper<B, ?, ?>> getSpecializedRecipeWrapper() {
        return Optional.of(compositeRecipeWrapper);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapper<B, ?, ?>> getSpecializedTagWrapper() {
        return Optional.of(compositeTagWrapper);
    }

    public static class BPWBuilder<B extends Block> extends BaseDataGenPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> implements DefaultableDataGenPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> {
        protected final SpecializedLanguagePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeLanguageBuilder;
        protected final SpecializedLootPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeLootBuilder;
        protected final SpecializedModelPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeModelBuilder;
        protected final SpecializedRecipePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeRecipeBuilder;
        protected final SpecializedTagPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeTagBuilder;

        public BPWBuilder(@NotNull BlockPropertyWrapper<B> ownerWrapper) {
            super(ownerWrapper);

            this.compositeLanguageBuilder = (SpecializedLanguagePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedLanguageWrapper().map(SpecializedLanguagePropertyWrapper::builder).get();
            this.compositeLootBuilder = (SpecializedLootPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedLootWrapper().map(SpecializedLootPropertyWrapper::builder).get();
            this.compositeModelBuilder = (SpecializedModelPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedModelWrapper().map(SpecializedModelPropertyWrapper::builder).get();
            this.compositeRecipeBuilder = (SpecializedRecipePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedRecipeWrapper().map(SpecializedRecipePropertyWrapper::builder).get();
            this.compositeTagBuilder = (SpecializedTagPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>) ownerWrapper.getSpecializedTagWrapper().map(SpecializedTagPropertyWrapper::builder).get();
        }

        @Override
        public BPWBuilder<B> copyFrom(BlockPropertyWrapper<B> propertyWrapper) {
            DefaultableDataGenPropertyWrapperBuilder.super.copyFrom(propertyWrapper);
            return super.copyFrom(propertyWrapper);
        }

        @Override
        public Optional<SpecializedLanguagePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedLanguageBuilder() {
            return Optional.of(compositeLanguageBuilder);
        }

        @Override
        public Optional<SpecializedLootPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedLootBuilder() {
            return Optional.of(compositeLootBuilder);
        }

        @Override
        public Optional<SpecializedModelPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedModelBuilder() {
            return Optional.of(compositeModelBuilder);
        }

        @Override
        public Optional<SpecializedRecipePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedRecipeBuilder() {
            return Optional.of(compositeRecipeBuilder);
        }

        @Override
        public Optional<SpecializedTagPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedTagBuilder() {
            return Optional.of(compositeTagBuilder);
        }
    }
}