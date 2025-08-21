package com.mememan.nexus.property_wrapper;

import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.language.DefaultableLanguageBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.loot.DefaultableLootBasedPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapperBuilder;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.specialised.loot.SpecializedLootPropertyWrapperBuilder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockPropertyWrapper<B extends Block> extends BaseDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>> implements DefaultableLanguageBasedPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>>, DefaultableLootBasedPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>> {
    protected SpecializedLanguagePropertyWrapper<B, ?, ?> compositeLanguageWrapper;
    protected SpecializedLootPropertyWrapper<B, ?, ?> compositeLootWrapper;

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

    public static class BPWBuilder<B extends Block> extends BaseDataGenPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> implements DefaultableLanguageBasedPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>>, DefaultableLootBasedPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> {
        protected SpecializedLanguagePropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeLanguageBuilder;
        protected SpecializedLootPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> compositeLootBuilder;

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
    }
}