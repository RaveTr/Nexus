package com.mememan.nexus.template.object.block.vegetation;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class DefaultableGrassBlock extends DefaultableSpreadingSnowyDirtBlock implements BonemealableBlock {
    protected final boolean allowBiomeFeaturePlacement;
    protected final Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey;

    public DefaultableGrassBlock(Properties properties, boolean allowSnowyVariant, Set<Supplier<TagKey<Block>>> validSnowyTags, Supplier<Block> baseDirtBlock, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties, allowSnowyVariant, validSnowyTags, baseDirtBlock);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, boolean allowSnowyVariant, Set<Supplier<TagKey<Block>>> validSnowyTags, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties, allowSnowyVariant, validSnowyTags);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, Set<Supplier<TagKey<Block>>> validSnowyTags, Supplier<Block> baseDirtBlock, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties, validSnowyTags, baseDirtBlock);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, boolean allowSnowyVariant, Supplier<Block> baseDirtBlock, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties, allowSnowyVariant, baseDirtBlock);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, boolean allowSnowyVariant, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties, allowSnowyVariant);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, Set<Supplier<TagKey<Block>>> validSnowyTags, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties, validSnowyTags);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, Supplier<Block> baseDirtBlock, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties, baseDirtBlock);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, boolean allowBiomeFeaturePlacement, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        super(properties);

        this.allowBiomeFeaturePlacement = allowBiomeFeaturePlacement;
        this.bonemealFeatureKey = bonemealFeatureKey == null ? Suppliers.ofInstance(null) : bonemealFeatureKey;
    }

    public DefaultableGrassBlock(Properties properties, Supplier<ResourceKey<PlacedFeature>> bonemealFeatureKey) {
        this(properties, true, bonemealFeatureKey);
    }

    public DefaultableGrassBlock(Properties properties) {
        this(properties, false, Suppliers.ofInstance(null));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean isClient) {
        return bonemealFeatureKey.get() != null && levelReader.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return bonemealFeatureKey.get() != null && level.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        BlockPos abovePos = blockPos.above();

        attemptLoop:
        for (int i = 0; i < 128; ++i) {
            BlockPos currentPos = abovePos;

            for (int j = 0; j < i / 16; ++j) {
                currentPos = currentPos.offset(randomSource.nextInt(3) - 1, (randomSource.nextInt(3) - 1) * randomSource.nextInt(3) / 2, randomSource.nextInt(3) - 1);

                if (!serverLevel.getBlockState(currentPos.below()).is(this) || serverLevel.getBlockState(currentPos).isCollisionShapeFullBlock(serverLevel, currentPos)) {
                    continue attemptLoop;
                }
            }

            BlockState currentState = serverLevel.getBlockState(currentPos);

            if (currentState.getBlock() instanceof BonemealableBlock targetBonemealableBlock && randomSource.nextInt(10) == 0) targetBonemealableBlock.performBonemeal(serverLevel, randomSource, currentPos, currentState);
            if (currentState.isAir()) {
                Holder<PlacedFeature> placedFeatureHolder = null;

                if (allowBiomeFeaturePlacement && randomSource.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> flowerFeatures = serverLevel.getBiome(currentPos).value().getGenerationSettings().getFlowerFeatures();

                    if (flowerFeatures.isEmpty()) continue;

                    placedFeatureHolder = ((RandomPatchConfiguration) flowerFeatures.get(0).config()).feature(); // Presumptuous cast moment
                } else if (bonemealFeatureKey != null && bonemealFeatureKey.get() != null) {
                    placedFeatureHolder = serverLevel.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(bonemealFeatureKey.get());
                }

                if (placedFeatureHolder != null) placedFeatureHolder.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, currentPos);
            }
        }
    }

    public boolean allowsBiomeFeaturePlacement() {
        return allowBiomeFeaturePlacement;
    }

    @NotNull
    public Supplier<ResourceKey<PlacedFeature>> getBonemealFeatureKey() {
        return bonemealFeatureKey;
    }
}
