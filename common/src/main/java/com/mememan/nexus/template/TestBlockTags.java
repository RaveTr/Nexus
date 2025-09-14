package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.property_wrapper.def.tag.TagPropertyWrapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

@RegistrarEntry
public class TestBlockTags {

    public static final Supplier<TagKey<Block>> TEST = createWrappedBlockTag("test/sometagtest")
            .builder()
            .withAdditionalTag(() -> BlockTags.ACACIA_LOGS)
            .withChildTag(() -> BlockTags.ACACIA_LOGS)
            .withTaggedObjectsOfType(TestBlockRegistrar.BLAH, () -> Blocks.ACACIA_BUTTON)
            .buildAndGet();

    private static TagPropertyWrapper<Block, TagKey<Block>> createWrappedBlockTag(String name) {
        return new TagPropertyWrapper<>(createBlockTag(name), NexusConstants.MOD_ID);
    }

    private static Supplier<TagKey<Block>> createBlockTag(String name) {
        return () -> TagKey.create(Registries.BLOCK, NexusConstants.prefix(name));
    }
}
