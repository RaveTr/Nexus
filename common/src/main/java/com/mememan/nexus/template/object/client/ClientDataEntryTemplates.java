package com.mememan.nexus.template.object.client;

import com.mememan.nexus.client.block_entity.BlockEntityClientData;
import com.mememan.nexus.client.block_entity.BlockEntitySheetData;
import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.template.object.client.renderer.block_entity.sign.DefaultableHangingSignRenderer;
import com.mememan.nexus.template.object.client.renderer.block_entity.sign.DefaultableSignRenderer;
import com.mememan.nexus.template.object.client.renderer.entity.misc.vehicle.DefaultableBoatRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Client-only {@code class} containing base definitions for generic block/entity client data entries.
 *
 * @see EntityClientData
 * @see BlockEntityClientData
 */
public final class ClientDataEntryTemplates {
    public static final BlockEntityClientData<SignBlockEntity> SIGN_CLIENT_DATA = new BlockEntityClientData<>(DefaultableSignRenderer::new, sign ->
            WoodType.values()
                    .filter(curWoodType -> Objects.equals(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(sign.get()).getNamespace(), new ResourceLocation(curWoodType.name()).getNamespace()))
                    .map(BlockEntitySheetData::forSoleSign)
                    .collect(Collectors.toCollection(ObjectArrayList::new))
    );
    public static final BlockEntityClientData<SignBlockEntity> HANGING_SIGN_CLIENT_DATA = new BlockEntityClientData<>(DefaultableHangingSignRenderer::new, sign ->
            WoodType.values()
                    .filter(curWoodType -> Objects.equals(DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(sign.get()).getNamespace(), new ResourceLocation(curWoodType.name()).getNamespace()))
                    .map(BlockEntitySheetData::forHangingSign)
                    .collect(Collectors.toCollection(ObjectArrayList::new))
    );

    public static final EntityClientData<Boat> BOAT_CLIENT_DATA = new EntityClientData<>(renderCtx -> new DefaultableBoatRenderer(renderCtx, false));
    public static final EntityClientData<Boat> CHEST_BOAT_CLIENT_DATA = new EntityClientData<>(renderCtx -> new DefaultableBoatRenderer(renderCtx, true));
}
