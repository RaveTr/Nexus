package com.mememan.nexus.template.object.block.vegetation;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public class DefaultableFlowerBlock extends FlowerBlock implements ConfigurablePlant {
    protected final Set<Supplier<TagKey<Block>>> validPlacementTags;
    protected final Supplier<MobEffect> suspiciousStewEffect;

    public DefaultableFlowerBlock(Supplier<MobEffect> suspiciousStewEffect, int effectDurationSeconds, Properties properties, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        super(suspiciousStewEffect == null ? MobEffects.CONFUSION : suspiciousStewEffect.get(), effectDurationSeconds, properties);

        this.validPlacementTags = validPlacementTags;
        this.suspiciousStewEffect = suspiciousStewEffect;
    }

    public DefaultableFlowerBlock(Properties properties, Set<Supplier<TagKey<Block>>> validPlacementTags) {
        this(null, 0, properties, validPlacementTags);
    }

    public DefaultableFlowerBlock(Properties properties) {
        this(properties, ObjectOpenHashSet.of(() -> BlockTags.DIRT));
    }

    @Override
    protected boolean mayPlaceOn(BlockState targetState, BlockGetter curLevel, BlockPos targetPos) {
        return allowPlacementOn(targetState, curLevel, targetPos);
    }

    @Override
    public boolean canSurvive(BlockState targetState, LevelReader curLevel, BlockPos targetPos) { // Needed because Forge patch go brr
        return mayPlaceOn(curLevel.getBlockState(targetPos.below()), curLevel, targetPos.below());
    }

    @Override
    public @NotNull MobEffect getSuspiciousEffect() {
        return suspiciousStewEffect == null ? MobEffects.CONFUSION : suspiciousStewEffect.get();
    }

    @Override
    public Set<Supplier<TagKey<Block>>> getValidPlacementTags() {
        return validPlacementTags;
    }
}
