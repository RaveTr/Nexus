package com.mememan.nexus.client.model.item;

import com.mememan.nexus.client.model.general.BaseModelDefinition;
import net.minecraft.data.models.model.ModelTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * Item-oriented variant of {@link BaseModelDefinition}, whose backing directory is set to "item".
 */
public class ItemModelDefinition extends BaseModelDefinition {

    public ItemModelDefinition(@NotNull ModelTemplate parentModel) {
        super(parentModel, "item");
    }
}
