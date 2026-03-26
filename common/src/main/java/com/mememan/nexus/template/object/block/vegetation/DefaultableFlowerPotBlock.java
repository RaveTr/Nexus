package com.mememan.nexus.template.object.block.vegetation;

import com.google.common.collect.ImmutableMap;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultableFlowerPotBlock extends FlowerPotBlock {
    protected static final Object2ObjectOpenHashMap<Supplier<Block>, Supplier<Block>> FULL_POTS = new Object2ObjectOpenHashMap<>();
    protected final Supplier<Block> contentSup;

    public DefaultableFlowerPotBlock(Supplier<Block> content, Properties properties) {
        super(Blocks.AIR, properties);

        this.contentSup = content;

        FULL_POTS.put(content, () -> this);
        POTTED_BY_CONTENT.remove(Blocks.AIR); // Undo defined default FlowerPotBlock behavior
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();
        BlockState potentialFlowerPotState = (heldItem instanceof BlockItem heldBlockItem ? getFullPot(heldBlockItem::getBlock).get() : Blocks.AIR).defaultBlockState();
        boolean isLiterallyAir = potentialFlowerPotState.is(Blocks.AIR);
        boolean isEmpty = isEmpty();

        if (isLiterallyAir != isEmpty) {
            if (isEmpty) {
                level.setBlock(pos, potentialFlowerPotState, Block.UPDATE_ALL);
                player.awardStat(Stats.POT_FLOWER);

                if (!player.getAbilities().instabuild) heldStack.shrink(1);
            } else {
                ItemStack potContentStack = new ItemStack(getContent());

                if (heldStack.isEmpty()) player.setItemInHand(hand, potContentStack);
                else if (!player.addItem(potContentStack)) player.drop(potContentStack, false);

                level.setBlock(pos, Blocks.FLOWER_POT.defaultBlockState(), Block.UPDATE_ALL);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return isEmpty() ? super.getCloneItemStack(level, pos, state) : new ItemStack(getContent());
    }

    @Override
    public boolean isEmpty() {
        return getContent() == Blocks.AIR;
    }

    @Override
    public @NotNull Block getContent() {
        return contentSup.get();
    }

    public static ImmutableMap<Supplier<Block>, Supplier<Block>> getFullPots() {
        return ImmutableMap.copyOf(FULL_POTS);
    }

    public static Block getFullPot(ResourceLocation flowerBlockId) {
        return FULL_POTS.entrySet().stream()
                .filter(curEntry -> Objects.equals(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(curEntry.getKey().get()), flowerBlockId))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(Supplier::get)
                .orElse(Blocks.AIR);
    }

    public static Supplier<Block> getFullPot(Supplier<Block> flowerBlockSup) {
        return FULL_POTS.entrySet().stream()
                .filter(curEntry -> Objects.equals(curEntry.getKey().get(), flowerBlockSup.get()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(() -> Blocks.AIR);
    }
}
