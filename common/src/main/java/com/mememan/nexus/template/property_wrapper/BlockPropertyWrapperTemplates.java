package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapperBuilder;
import com.mememan.nexus.util.LootUtil;
import com.mememan.nexus.util.ModelUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link BlockPropertyWrapper} templates, as well as some helper
 * shortcut utility methods for registration.
 */
public final class BlockPropertyWrapperTemplates {
    /**
     * Basic BPW template for plain blocks. Cube model, simple blockstate, drops self.
     */
    public static final BlockPropertyWrapper<Block> BASIC = new BlockPropertyWrapper<>()
            .builder()
            .withModelDefinition(ModelUtil::cubeAll)
            .withBlockStateDefinition(ModelUtil::simpleBlock)
            .withLootTable(LootUtil::dropSelf)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with a pickaxe. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_PICKAXE)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone pickaxe or better. Inherits from {@link #BASIC_PICKAXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron pickaxe or better. Inherits from {@link #BASIC_PICKAXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond pickaxe or better. Inherits from {@link #BASIC_PICKAXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_PICKAXE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_PICKAXE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with an axe. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_AXE)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone axe or better. Inherits from {@link #BASIC_AXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron axe or better. Inherits from {@link #BASIC_AXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond axe or better. Inherits from {@link #BASIC_AXE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_AXE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_AXE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with a hoe. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_HOE)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone hoe or better. Inherits from {@link #BASIC_HOE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron hoe or better. Inherits from {@link #BASIC_HOE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond hoe or better. Inherits from {@link #BASIC_HOE}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_HOE_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_HOE)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    /**
     * Basic BPW template for blocks that can be mined with a shovel. Inherits from {@link #BASIC}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC)
            .withTag(() -> BlockTags.MINEABLE_WITH_SHOVEL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a stone shovel or better. Inherits from {@link #BASIC_SHOVEL}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_STONE = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_STONE_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with an iron shovel or better. Inherits from {@link #BASIC_SHOVEL}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_IRON = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_IRON_TOOL)
            .build();
    /**
     * Basic BPW template for blocks that can be mined with a diamond shovel or better. Inherits from {@link #BASIC_SHOVEL}.
     */
    public static final BlockPropertyWrapper<Block> BASIC_SHOVEL_DIAMOND = new BlockPropertyWrapper<>()
            .builder()
            .copyFromType(BASIC_SHOVEL)
            .withTag(() -> BlockTags.NEEDS_DIAMOND_TOOL)
            .build();

    private BlockPropertyWrapperTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template utility class! (BlockPropertyWrapperTemplates)");
    }

    /**
     * Registers and returns the provided {@link Block}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlock(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = NexusServices.REGISTRAR.registerObject(blockId, blockSup, BuiltInRegistries.BLOCK);

        if (blockSupCol != null) blockSupCol.add((Supplier<Block>) registeredBlock);

        return registeredBlock;
    }

    /**
     * Overloaded variant of {@link #registerBlock(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Block}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlock(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBlock(blockId, blockSup, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapping it to a new {@link BlockPropertyWrapper} inheriting
     * from the provided {@link BlockPropertyWrapper} template. Optionally tracks the registered {@link Block} to a
     * custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlock(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockFromTemplate(ResourceLocation, Supplier, BlockPropertyWrapper, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerBlockFromTemplate(blockId, blockSup, templateBPW, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapping it to a new {@link BlockPropertyWrapper} inheriting
     * from the provided {@link BlockPropertyWrapper} template, and automatically creates a {@link BlockItem} for it.
     * Optionally tracks both the registered {@link Block} and its corresponding {@link BlockItem} to custom
     * {@link Collection}s.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     * @param blockItemSupCol An optional {@link Collection} to track the registered {@link BlockItem}. Primarily useful if you
     *                        want a shorthand method of tracking your own registered block items.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockWithItemFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = registerBlockFromTemplate(blockId, blockSup, templateBPW, blockSupCol);

        ItemPropertyWrapperTemplates.registerItem(blockId, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), blockItemSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW)
                .buildAndGet();
    }

    /**
     * Overloaded variant of {@link #registerBlockWithItemFromTemplate(ResourceLocation, Supplier, BlockPropertyWrapper, Collection, Collection)} that does not track the
     * registered {@link Block} or its corresponding {@link BlockItem} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the provided {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBlockWithItemFromTemplate(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerBlockWithItemFromTemplate(blockId, blockSup, templateBPW, null, null);
    }

    /**
     * Registers the provided {@link Block} and returns its {@link BlockPropertyWrapperBuilder} inheriting from the
     * provided {@link BlockPropertyWrapper} template. Optionally tracks the registered {@link Block} to a custom
     * {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol) {
        Supplier<B> registeredBlock = registerBlock(blockId, blockSup, blockSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerAndChain(ResourceLocation, Supplier, BlockPropertyWrapper, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerAndChain(blockId, blockSup, templateBPW, null);
    }

    /**
     * Registers the provided {@link Block}, automatically creates a {@link BlockItem} for it, and returns its
     * {@link BlockPropertyWrapperBuilder} inheriting from the provided {@link BlockPropertyWrapper} template.
     * Optionally tracks both the registered {@link Block} and its corresponding {@link BlockItem} to custom
     * {@link Collection}s.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     * @param blockItemSupCol An optional {@link Collection} to track the registered {@link BlockItem}. Primarily useful if you
     *                        want a shorthand method of tracking your own registered block items.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerWithItemAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW, @Nullable Collection<Supplier<Block>> blockSupCol, @Nullable Collection<Supplier<Item>> blockItemSupCol) {
        Supplier<B> registeredBlock = registerBlock(blockId, blockSup, blockSupCol);

        ItemPropertyWrapperTemplates.registerItem(blockId, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), blockItemSupCol);

        return new BlockPropertyWrapper<>(registeredBlock, blockId.getNamespace())
                .builder()
                .copyFromType(templateBPW);
    }

    /**
     * Overloaded variant of {@link #registerWithItemAndChain(ResourceLocation, Supplier, BlockPropertyWrapper, Collection, Collection)} that does not track the
     * registered {@link Block} or its corresponding {@link BlockItem} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param templateBPW The {@link BlockPropertyWrapper} template to inherit from.
     *
     * @return The {@link BlockPropertyWrapperBuilder} of the registered {@link Block}, inheriting from the provided
     * {@code templateBPW}.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> BlockPropertyWrapperBuilder<B> registerWithItemAndChain(ResourceLocation blockId, Supplier<B> blockSup, BlockPropertyWrapper<Block> templateBPW) {
        return registerWithItemAndChain(blockId, blockSup, templateBPW, null, null);
    }

    /**
     * Registers and returns the provided {@link Block}, mapped to a new {@link BlockPropertyWrapper} inheriting
     * from the {@link #BASIC} template. Optionally tracks the registered {@link Block} to a custom
     * {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     * @param blockSupCol An optional {@link Collection} to track the registered {@link Block}. Primarily useful if you
     *                    want a shorthand method of tracking your own registered blocks.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the {@code BASIC} template.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBasicBlock(ResourceLocation blockId, Supplier<B> blockSup, @Nullable Collection<Supplier<Block>> blockSupCol) {
        return registerBlockFromTemplate(blockId, blockSup, BASIC, blockSupCol);
    }

    /**
     * Overloaded variant of {@link #registerBasicBlock(ResourceLocation, Supplier, Collection)} that does not track the
     * registered {@link Block} to any custom {@link Collection}.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     * @param blockSup The {@link Block} object to register.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the {@code BASIC} template.
     *
     * @param <B> Any {@link Block} type.
     */
    public static <B extends Block> Supplier<B> registerBasicBlock(ResourceLocation blockId, Supplier<B> blockSup) {
        return registerBasicBlock(blockId, blockSup, null);
    }

    /**
     * Registers a new basic {@link Block} with default properties and returns it, mapped to a new
     * {@link BlockPropertyWrapper} inheriting from the {@link #BASIC} template.
     *
     * @param blockId The target {@linkplain Block Block's} {@linkplain ResourceLocation registry ID}.
     *
     * @return The {@link Supplier} of the registered {@link Block}, mapped to its own {@link BlockPropertyWrapper}
     * inheriting from the {@code BASIC} template.
     */
    public static Supplier<Block> registerBasicBlock(ResourceLocation blockId) {
        return registerBasicBlock(blockId, () -> new Block(BlockBehaviour.Properties.of()));
    }
}
