package com.mememan.nexus.template.object.client.renderer.block_entity.sign;

import com.mememan.nexus.util.RegistryUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultableSignRenderer extends SignRenderer {
    protected final Map<WoodType, SignModel> defaultableSignModels;

    public DefaultableSignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);

        this.defaultableSignModels = WoodType.values()
                .collect(Collectors.toMap(
                        Function.identity(),
                        curWoodType -> new SignModel(context.bakeLayer(new ModelLayerLocation(
                                RegistryUtil.pickPrefix(new ResourceLocation(curWoodType.name()), "sign/"),
                                "main"
                        ))),
                        (a, b) -> a,
                        Object2ObjectOpenHashMap::new
                ));
    }

    @Override
    public void render(SignBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState targetState = blockEntity.getBlockState();
        SignBlock targetSignBlock = (SignBlock) targetState.getBlock();
        WoodType signWoodType = SignBlock.getWoodType(targetSignBlock);
        SignModel chosenSignModel = defaultableSignModels.get(signWoodType);

        chosenSignModel.stick.visible = targetState.getBlock() instanceof StandingSignBlock;

        renderSignWithText(blockEntity, poseStack, buffer, packedLight, packedOverlay, targetState, targetSignBlock, signWoodType, chosenSignModel);
    }
}
