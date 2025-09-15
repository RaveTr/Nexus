package com.mememan.nexus.property_wrapper.def.block;

import com.mememan.nexus.property_wrapper.base.generic.DefaultableDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.model.SpecializedModelPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.recipe.SpecializedRecipePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.tag.SpecializedTagPropertyWrapper;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockPropertyWrapper<B extends Block> extends BaseDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapperBuilder<B>> implements DefaultableDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapperBuilder<B>> {
    protected final SpecializedLanguagePropertyWrapper<B, ?, ?> compositeLanguageWrapper;
    protected final SpecializedLootPropertyWrapper<B, ?, ?> compositeLootWrapper;
    protected final SpecializedModelPropertyWrapper<B, ?, ?> compositeModelWrapper;
    protected final SpecializedRecipePropertyWrapper<B, ?, ?> compositeRecipeWrapper;
    protected final SpecializedTagPropertyWrapper<B, ?, ?> compositeTagWrapper;

    public BlockPropertyWrapper(Supplier<B> parentObject, boolean isTemplate, String modId) {
        super(parentObject, isTemplate, BlockPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, isTemplate, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, isTemplate, modId);
    }

    public BlockPropertyWrapper(@NotNull Supplier<B> parentObject, String modId) {
        super(parentObject, BlockPropertyWrapperBuilder::new, modId);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>(parentObject, modId);
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>(parentObject, modId);
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>(parentObject, modId);
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>(parentObject, modId);
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>(parentObject, modId);
    }

    public BlockPropertyWrapper() {
        super(BlockPropertyWrapperBuilder::new);

        this.compositeLanguageWrapper = new SpecializedLanguagePropertyWrapper<>();
        this.compositeLootWrapper = new SpecializedLootPropertyWrapper<>();
        this.compositeModelWrapper = new SpecializedModelPropertyWrapper<>();
        this.compositeRecipeWrapper = new SpecializedRecipePropertyWrapper<>();
        this.compositeTagWrapper = new SpecializedTagPropertyWrapper<>();
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
}