package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.util.LootUtil;
import com.mememan.nexus.util.ModelUtil;
import com.mememan.nexus.util.RegistryUtil;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Template utility {@code class} containing common {@link BlockPropertyWrapper} templates, as well as some helper
 * shortcut utility methods.
 */
public final class BlockPropertyWrapperTemplates {
    /**
     * Basic BPW template for plain blocks.
     * <ul>
     *     <li><b>Model</b>: Uses {@link ModelUtil#cubeAll(Supplier)}, with the input being
     *     {@link RegistryUtil#getBlockTextureLocationOrDefault(Supplier)}.</li>
     *     <li><b>Block State</b>: Uses {@link ModelUtil#simpleBlock(Supplier)}, with the input being the parent
     *     {@link Block}.</li>
     *     <li><b>Loot Table</b>: Uses {@link LootUtil#dropSelf(Supplier)}, with the input being the parent
     *     {@link Block}.</li>
     * </ul>
     */
    public static final BlockPropertyWrapper<Block> BASIC = new BlockPropertyWrapper<>()
            .builder()
            .withModelDefinition(ModelUtil::cubeAll)
            .withBlockStateDefinition(ModelUtil::simpleBlock)
            .withLootTable(LootUtil::dropSelf)
            .build();


    private BlockPropertyWrapperTemplates() {
        throw new UnsupportedOperationException("Attempted to construct instance of template utility class! (BlockPropertyWrapperTemplates)");
    }
}
