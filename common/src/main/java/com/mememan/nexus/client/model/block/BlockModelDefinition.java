package com.mememan.nexus.client.model.block;

import com.mememan.nexus.client.model.general.BaseModelDefinition;
import net.minecraft.data.models.model.ModelTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * Block-oriented variant of {@link BaseModelDefinition}, whose backing directory is set to "block".
 */
public class BlockModelDefinition extends BaseModelDefinition<BlockModelDefinition> {

    public BlockModelDefinition(@NotNull ModelTemplate parentModel) {
        super(parentModel, "block");
    }
}
