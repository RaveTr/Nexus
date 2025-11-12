package com.mememan.nexus.template.object.client.renderer.entity.misc.vehicle;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableBoat;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableChestBoat;
import com.mememan.nexus.util.RegistryUtil;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

public class DefaultableBoatRenderer extends BoatRenderer {

    public DefaultableBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Boat entity) {
        ResourceLocation boatTypeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(entity.getType());

        return entity instanceof DefaultableBoat || entity instanceof DefaultableChestBoat
                ? RegistryUtil.getTextureLocation(boatTypeId, "entity").orElse(boatTypeId)
                : super.getTextureLocation(entity);
    }
}
