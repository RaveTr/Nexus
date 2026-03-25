package com.mememan.nexus.template.object.block.vegetation;

import com.google.common.collect.ImmutableSet;
import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
    protected static final ObjectOpenHashSet<Supplier<Block>> FULL_POTS = new ObjectOpenHashSet<>();
    protected final Supplier<Block> contentSup;

    public DefaultableFlowerPotBlock(Supplier<Block> content, Properties properties) {
        super(Blocks.AIR, properties);

        this.contentSup = content;

        FULL_POTS.add(content);
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

    public static ImmutableSet<Supplier<Block>> getFullPots() {
        return ImmutableSet.copyOf(FULL_POTS);
    }

    public static Block getFullPot(ResourceLocation flowerBlockId) {
        return FULL_POTS.stream()
                .filter(curBlockSup -> Objects.equals(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(curBlockSup.get()), flowerBlockId))
                .findFirst()
                .map(Supplier::get)
                .orElse(Blocks.AIR);
    }

    public static Supplier<Block> getFullPot(Supplier<Block> flowerBlockSup) {
        return FULL_POTS.stream()
                .filter(curBlockSup -> curBlockSup == flowerBlockSup || Objects.equals(curBlockSup.get(), flowerBlockSup.get()))
                .findFirst()
                .orElse(() -> Blocks.AIR);
    }

    @PostInit
    private static class FlowerPotContainer {

        private FlowerPotContainer() {
            throw new IllegalAccessError("Attempted to construct instance of container class! (FlowerPotContainer)");
        }

        static { // TODO Add ability to track updates made to the original map through here as well
            FULL_POTS.stream()
                    .filter(curBlock -> RegistryUtil.getObjectFrom(curBlock.get(), curBlockId -> curBlockId.withPrefix("potted_")).isPresent())
                    .map(curBlock -> Map.entry(curBlock.get(), RegistryUtil.getObjectFrom(curBlock.get(), curBlockId -> curBlockId.withPrefix("potted_")).get()))
                    .forEach(curBlock -> POTTED_BY_CONTENT.put(curBlock.getKey(), curBlock.getValue()));
        }
    }
}
