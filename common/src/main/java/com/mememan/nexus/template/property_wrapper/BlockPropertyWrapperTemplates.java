package com.mememan.nexus.template.property_wrapper;

import com.mememan.nexus.client.model.block.BlockModelDefinition;
import com.mememan.nexus.property_wrapper.def.block.BlockPropertyWrapper;
import com.mememan.nexus.util.RegistryUtil;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;

/**
 * Template utility {@code class} containing common {@link BlockPropertyWrapper} templates, as well as some helper
 * shortcut utility methods.
 */
public final class BlockPropertyWrapperTemplates {
    /**
     * Basic BPW template using {@link ModelTemplates#CUBE_ALL}, with default configurations for all other properties.
     */
    public static final BlockPropertyWrapper<? extends Block> BASIC = new BlockPropertyWrapper<>()
            .builder()
            .withModelDefinition(ownerBlockSup -> new BlockModelDefinition(ModelTemplates.CUBE_ALL)
                    .withTextureMapping(TextureMapping.cube(RegistryUtil.getTextureLocation(ownerBlockSup).orElse(TextureMapping.getBlockTexture(ownerBlockSup.get())))))
            .build();

    private BlockPropertyWrapperTemplates() {
        throw new UnsupportedOperationException("Attempted to construct instance of template utility class! (BlockPropertyWrapperTemplates)");
    }
}
