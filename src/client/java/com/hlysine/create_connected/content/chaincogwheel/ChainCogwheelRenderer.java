package com.hlysine.create_connected.content.chaincogwheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class ChainCogwheelRenderer implements BlockEntityRenderer<SimpleKineticBlockEntity, ChainCogwheelRenderer.ChainCogwheelRenderState> {

    public ChainCogwheelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public ChainCogwheelRenderState createRenderState() {
        return new ChainCogwheelRenderState();
    }

    @Override
    public void extractRenderState(
            SimpleKineticBlockEntity blockEntity,
            ChainCogwheelRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(blockEntity, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        BlockState blockState = blockEntity.getBlockState();
        Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockState);
        Direction facing = axis.getPositive();

        int color = KineticBlockEntityRenderer.getTintColor(blockEntity);
        IRotate block = (IRotate) blockState.getBlock();
        BlockPos pos = blockEntity.getBlockPos();

        state.angle = KineticBlockEntityRenderer.getRotateAngleWithoutBeOffset(axis, facing, blockEntity, state, level);
        state.model = CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, blockState, facing)
                .cardinalLighting(cardinalLighting)
                .light(state.lightCoords)
                .color(color)
                .extractRenderState();

        state.top = block.hasShaftTowards(level, pos, blockState, facing)
                ? shaft(blockState, facing, cardinalLighting, state.lightCoords, color)
                : null;
        state.bottom = block.hasShaftTowards(level, pos, blockState, facing.getOpposite())
                ? shaft(blockState, facing.getOpposite(), cardinalLighting, state.lightCoords, color)
                : null;
    }

    @Override
    public void submit(ChainCogwheelRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (state.angle != null)
            matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
        state.model.submit(matrices, queue);
        if (state.top != null)
            state.top.submit(matrices, queue);
        if (state.bottom != null)
            state.bottom.submit(matrices, queue);
    }

    private static SuperByteBufferRenderState shaft(
            BlockState blockState,
            Direction direction,
            CardinalLighting cardinalLighting,
            int lightCoords,
            int color
    ) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, direction)
                .cardinalLighting(cardinalLighting)
                .light(lightCoords)
                .color(color)
                .extractRenderState();
    }

    public static class ChainCogwheelRenderState extends BlockEntityRenderState {
        public @UnknownNullability SuperByteBufferRenderState model;
        public @Nullable Quaternionf angle;
        public @Nullable SuperByteBufferRenderState top;
        public @Nullable SuperByteBufferRenderState bottom;
    }
}
