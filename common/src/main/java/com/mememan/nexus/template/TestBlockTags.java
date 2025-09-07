package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.tag.TagWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

@RegistrarEntry
public class TestBlockTags {

    public static final Supplier<TagKey<Block>> TEST = createWrappedBlockTag("test/sometagtest")
            .withEntries(ObjectArrayList.of(() -> Blocks.ANDESITE))
            .withTagEntry(() -> BlockTags.ACACIA_LOGS)
            .withParentTagEntry(() -> BlockTags.ANVIL)
            .getParentTag();

    private static TagWrapper<Block, TagKey<Block>> createWrappedBlockTag(String name) {
        return TagWrapper.create(createBlockTag(name));
    }

    private static Supplier<TagKey<Block>> createBlockTag(String name) {
        return () -> TagKey.create(Registries.BLOCK, NexusConstants.prefix(name));
    }
}
