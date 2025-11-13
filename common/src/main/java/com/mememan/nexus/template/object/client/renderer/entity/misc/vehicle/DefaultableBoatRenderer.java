package com.mememan.nexus.template.object.client.renderer.entity.misc.vehicle;

import com.google.common.collect.ImmutableMap;
import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableBoat;
import com.mememan.nexus.template.object.entity.misc.vehicle.DefaultableChestBoat;
import com.mememan.nexus.template.object.item.entity.boat.BoatType;
import com.mememan.nexus.util.RegistryUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class DefaultableBoatRenderer extends EntityRenderer<Boat> {
    protected final boolean chestBoat;
    protected final Map<BoatType, Pair<ResourceLocation, ListModel<Boat>>> mappedBoatTypes;
    protected final Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> vanillaBoatResources;

    public DefaultableBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context);

        this.shadowRadius = 0.8F;
        this.chestBoat = chestBoat;
        this.mappedBoatTypes = Util.make(new Object2ObjectOpenHashMap<>(), existingBoatResources -> {
            BoatType.getKnownBoatTypes().forEach(knownBoatType -> {
                existingBoatResources.put(knownBoatType,
                        Pair.of(
                                RegistryUtil.pickPrefix(RegistryUtil.getTextureLocation(knownBoatType.getResourceFriendlyId(), "entity")
                                        .orElse(knownBoatType.getResourceFriendlyId()), "textures/"),
                                createBoatModel(context, knownBoatType, chestBoat)
                        ));
            });
        });
        this.vanillaBoatResources = Stream.of(Boat.Type.values())
                .collect(ImmutableMap.toImmutableMap(
                        Function.identity(),
                        (curBoatType) -> Pair.of(
                                new ResourceLocation(BoatRenderer.getTextureLocation(curBoatType, chestBoat)),
                                createVanillaBoatModel(context, curBoatType, chestBoat)
                        )
                ));
    }

    @NotNull
    protected ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, BoatType type, boolean chestBoat) {
        boolean isRaft = type.isRaft();
        ModelLayerLocation mappedModelLayerLoc = isRaft
                ?
                (chestBoat
                        ? new ModelLayerLocation(RegistryUtil.pickPrefix(type.getResourceFriendlyId(), "chest_raft/"), "main")
                        : new ModelLayerLocation(RegistryUtil.pickPrefix(type.getResourceFriendlyId(), "raft/"), "main")
                )
                :
                (chestBoat
                        ? new ModelLayerLocation(RegistryUtil.pickPrefix(type.getResourceFriendlyId(), "chest_boat/"), "main")
                        : new ModelLayerLocation(RegistryUtil.pickPrefix(type.getResourceFriendlyId(), "boat/"), "main")
                );
        ModelPart bakedRootModelPart = context.bakeLayer(mappedModelLayerLoc);

        if (isRaft) return chestBoat ? new ChestRaftModel(bakedRootModelPart) : new RaftModel(bakedRootModelPart);
        else return chestBoat ? new ChestBoatModel(bakedRootModelPart) : new BoatModel(bakedRootModelPart);
    }

    private ListModel<Boat> createVanillaBoatModel(EntityRendererProvider.Context context, Boat.Type type, boolean chestBoat) {
        ModelLayerLocation vanillaBoatLayerLoc = chestBoat ? ModelLayers.createChestBoatModelName(type) : ModelLayers.createBoatModelName(type);
        ModelPart rootBoatModelPart = context.bakeLayer(vanillaBoatLayerLoc);

        if (type == Boat.Type.BAMBOO) return chestBoat ? new ChestRaftModel(rootBoatModelPart) : new RaftModel(rootBoatModelPart);
        else return chestBoat ? new ChestBoatModel(rootBoatModelPart) : new BoatModel(rootBoatModelPart);
    }

    @Override
    public void render(Boat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float hurtTimeFrames = (float) entity.getHurtTime() - partialTicks;
        float dmgFrames = entity.getDamage() - partialTicks;

        if (dmgFrames < 0.0F) dmgFrames = 0.0F;
        if (hurtTimeFrames > 0.0F) poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTimeFrames) * hurtTimeFrames * dmgFrames / 10.0F * (float) entity.getHurtDir()));

        float adjustedBubbleAngle = entity.getBubbleAngle(partialTicks);

        if (!Mth.equal(adjustedBubbleAngle, 0.0F)) poseStack.mulPose((new Quaternionf()).setAngleAxis(entity.getBubbleAngle(partialTicks) * ((float) Math.PI / 180.0F), 1.0F, 0.0F, 1.0F));

        Pair<ResourceLocation, ListModel<Boat>> mappedModelPair = entity instanceof DefaultableBoat defaultableBoat
                ? this.mappedBoatTypes.get(defaultableBoat.getBoatType().orElseThrow(() -> new IllegalArgumentException("Tried to render DefaultableBoat without specified BoatType!")))
                : entity instanceof DefaultableChestBoat defaultableChestBoat
                ? this.mappedBoatTypes.get(defaultableChestBoat.getBoatType().orElseThrow(() -> new IllegalArgumentException("Tried to render DefaultableChestBoat without specified BoatType!")))
                : this.vanillaBoatResources.get(entity.getVariant());
        ResourceLocation modelTextureLoc = mappedModelPair.getFirst();
        ListModel<Boat> actualBoatModel = mappedModelPair.getSecond();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        actualBoatModel.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);

        VertexConsumer textureVertexBuffer = buffer.getBuffer(actualBoatModel.renderType(modelTextureLoc));

        actualBoatModel.renderToBuffer(poseStack, textureVertexBuffer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        if (!entity.isUnderWater()) {
            VertexConsumer waterMaskVertexBuffer = buffer.getBuffer(RenderType.waterMask());

            if (actualBoatModel instanceof WaterPatchModel waterPatchBoatModel) {
                waterPatchBoatModel.waterPatch().render(poseStack, waterMaskVertexBuffer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Boat entity) {
        ResourceLocation boatTypeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(entity.getType());

        if (entity instanceof DefaultableBoat defaultableBoat) boatTypeId = defaultableBoat.getBoatType().orElseThrow(() -> new IllegalArgumentException("Tried to get texture location for DefaultableBoat without specified BoatType!")).getResourceFriendlyId();
        if (entity instanceof DefaultableChestBoat defaultableChestBoat) boatTypeId = defaultableChestBoat.getBoatType().orElseThrow(() -> new IllegalArgumentException("Tried to get texture location for DefaultableChestBoat without specified BoatType!")).getResourceFriendlyId();

        return RegistryUtil.pickPrefix(RegistryUtil.getTextureLocation(boatTypeId, "entity").orElse(boatTypeId), "textures/");
    }
}
