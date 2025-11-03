package com.mememan.nexus.template.object.client;

import com.mememan.nexus.client.block_entity.BlockEntityClientData;
import com.mememan.nexus.client.block_entity.BlockEntitySheetData;
import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.util.RegistryUtil;
import com.mememan.nexus.util.StringUtil;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.function.Supplier;

/**
 * Client-only {@code class} containing base definitions for generic block/entity client data entries.
 *
 * @see EntityClientData
 * @see BlockEntityClientData
 */
public final class ClientDataEntryTemplates {
    public static final Supplier<BlockEntityClientData<SignBlockEntity>> SIGN_CLIENT_DATA = () -> new BlockEntityClientData<>(SignRenderer::new, sign -> BlockEntitySheetData.forSign(
            RegistryUtil.getOrCreateWoodType(StringUtil.subLastToken(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(sign.get()).toString()))
    ));
}
