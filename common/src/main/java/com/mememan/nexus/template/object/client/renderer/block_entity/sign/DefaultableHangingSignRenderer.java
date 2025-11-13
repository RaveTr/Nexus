package com.mememan.nexus.template.object.client.renderer.block_entity.sign;

import com.mememan.nexus.util.RegistryUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultableHangingSignRenderer extends HangingSignRenderer {
    protected final Map<WoodType, HangingSignModel> defaultableHangingSignModels;

    public DefaultableHangingSignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);

        this.defaultableHangingSignModels = WoodType.values()
                .collect(Collectors.toMap(
                        Function.identity(),
                        curWoodType -> new HangingSignModel(context.bakeLayer(new ModelLayerLocation(
                                RegistryUtil.pickPrefix(new ResourceLocation(curWoodType.name()), "hanging_sign/"),
                                "main"
                        ))),
                        (a, b) -> a,
                        Object2ObjectOpenHashMap::new
                ));
    }

    @Override
    public void render(SignBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState targetState = blockEntity.getBlockState();
        SignBlock targetHangingSignBlock = (SignBlock) targetState.getBlock();
        WoodType hangingSignWoodType = SignBlock.getWoodType(targetHangingSignBlock);
        HangingSignModel chosenHangingSignModel = defaultableHangingSignModels.get(hangingSignWoodType);

        chosenHangingSignModel.evaluateVisibleParts(targetState);

        renderSignWithText(blockEntity, poseStack, buffer, packedLight, packedOverlay, targetState, targetHangingSignBlock, hangingSignWoodType, chosenHangingSignModel);
    }
}
