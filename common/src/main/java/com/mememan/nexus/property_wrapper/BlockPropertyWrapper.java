package com.mememan.nexus.property_wrapper;

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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockPropertyWrapper<B extends Block> extends BaseDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>> implements DefaultableDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>> {
    protected SpecializedLanguagePropertyWrapper<B, ?, ?> compositeLanguageWrapper;
    protected SpecializedLootPropertyWrapper<B, ?, ?> compositeLootWrapper;
    protected SpecializedModelPropertyWrapper<B, ?, ?> compositeModelWrapper;
    protected SpecializedRecipePropertyWrapper<B, ?, ?> compositeRecipeWrapper;
    protected SpecializedTagPropertyWrapper<B, ?, ?> compositeTagWrapper;

    public BlockPropertyWrapper(Supplier<B> parentObject, boolean isTemplate, Function<BlockPropertyWrapper<B>, PropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> builderFactory, String modId) {
        super(parentObject, isTemplate, builderFactory, modId);
    }

    public BlockPropertyWrapper(@NotNull Supplier<B> parentObject, Function<BlockPropertyWrapper<B>, PropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> builderFactory, String modId) {
        super(parentObject, builderFactory, modId);
    }

    public BlockPropertyWrapper(Function<BlockPropertyWrapper<B>, PropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> builderFactory) {
        super(builderFactory);
    }

    public BlockPropertyWrapper(Supplier<B> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BPWBuilder::new, modId);
    }

    public BlockPropertyWrapper(@NotNull Supplier<B> parentObject, String modId) {
        super(parentObject, BPWBuilder::new, modId);
    }

    public BlockPropertyWrapper() {
        super(BPWBuilder::new);
    }

    @Override
    public Optional<SpecializedLanguagePropertyWrapper<B, ?, ?>> getSpecializedLanguageWrapper() {
        if (compositeLanguageWrapper == null) this.compositeLanguageWrapper = SpecializedLanguagePropertyWrapper.createWithImpl(parentObject, isTemplate, modId.orElse(null), getObjectDescriptionId());
        return Optional.of(compositeLanguageWrapper);
    }

    @Override
    public @NotNull String getObjectDescriptionId() {
        return parentObject.get().getDescriptionId();
    }

    @Override
    public Optional<SpecializedLootPropertyWrapper<B, ?, ?>> getSpecializedLootWrapper() {
        if (compositeLootWrapper == null) this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, isTemplate, modId.orElse(null));
        return Optional.of(compositeLootWrapper);
    }

    @Override
    public Optional<SpecializedModelPropertyWrapper<B, ?, ?>> getSpecializedModelWrapper() {
        if (compositeModelWrapper == null) this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, isTemplate, modId.orElse(null));
        return Optional.of(compositeModelWrapper);
    }

    @Override
    public Optional<SpecializedRecipePropertyWrapper<B, ?, ?>> getSpecializedRecipeWrapper() {
        if (compositeRecipeWrapper == null) this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, isTemplate, modId.orElse(null));
        return Optional.of(compositeRecipeWrapper);
    }

    @Override
    public Optional<SpecializedTagPropertyWrapper<B, ?, ?>> getSpecializedTagWrapper() {
        if (compositeTagWrapper == null) this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId.orElse(null));
        return Optional.of(compositeTagWrapper);
    }

    public static class BPWBuilder<B extends Block> extends BaseDataGenPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> implements DefaultableDataGenPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> {
        protected SpecializedLanguagePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeLanguageBuilder;
        protected SpecializedLootPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeLootBuilder;
        protected SpecializedModelPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeModelBuilder;
        protected SpecializedRecipePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeRecipeBuilder;
        protected SpecializedTagPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeTagBuilder;

        public BPWBuilder(@NotNull BlockPropertyWrapper<B> ownerWrapper) {
            super(ownerWrapper);
        }

        @Override
        public Optional<SpecializedLanguagePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedLanguageBuilder() {
            if (compositeLanguageBuilder == null) this.compositeLanguageBuilder = new SpecializedLanguagePropertyWrapperBuilder<>(ownerWrapper);
            return Optional.of(compositeLanguageBuilder);
        }

        @Override
        public Optional<SpecializedLootPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedLootBuilder() {
            if (compositeLootBuilder == null) this.compositeLootBuilder = new SpecializedLootPropertyWrapperBuilder<>(ownerWrapper);
            return Optional.of(compositeLootBuilder);
        }

        @Override
        public Optional<SpecializedModelPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedModelBuilder() {
            if (compositeModelBuilder == null) this.compositeModelBuilder = new SpecializedModelPropertyWrapperBuilder<>(ownerWrapper);
            return Optional.of(compositeModelBuilder);
        }

        @Override
        public Optional<SpecializedRecipePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedRecipeBuilder() {
            if (compositeRecipeBuilder == null) this.compositeRecipeBuilder = new SpecializedRecipePropertyWrapperBuilder<>(ownerWrapper);
            return Optional.of(compositeRecipeBuilder);
        }

        @Override
        public Optional<SpecializedTagPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>> getSpecializedTagBuilder() {
            if (compositeTagBuilder == null) this.compositeTagBuilder = new SpecializedTagPropertyWrapperBuilder<>(ownerWrapper);
            return Optional.of(compositeTagBuilder);
        }
    }
}