package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.content.kineticbattery.KineticBatteryRenderer.KineticBatteryRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getProgress;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotateAngle;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotationAxisOf;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getTintColor;

public class KineticBatteryRenderer implements BlockEntityRenderer<KineticBatteryBlockEntity, KineticBatteryRenderState> {
    public KineticBatteryRenderer(Context context) {
    }

    @Override
    public KineticBatteryRenderState createRenderState() {
        return new KineticBatteryRenderState();
    }

    @Override
    public void extractRenderState(
        KineticBatteryBlockEntity be,
        KineticBatteryRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.blockState = be.getBlockState();
        int color = getTintColor(be);
        Axis axis = getRotationAxisOf(be);
        Direction direction = axis.getPositive();
        float offset = KineticBlockEntityVisual.rotationOffset(state.blockState, axis, state.blockPos)
            + be.getRotationAngleOffset(axis);
        float progress = getProgress(be, level);
        state.topAngle = getRotateAngle(progress * be.getRotationSpeedModifier(direction), offset, direction);
        state.top = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, direction)
            .cardinalLighting(level).light(state.lightCoords).color(color).extractRenderState();
        Direction bottom = direction.getOpposite();
        state.bottomAngle = getRotateAngle(progress * be.getRotationSpeedModifier(bottom), offset, direction);
        state.bottom = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, bottom)
            .cardinalLighting(level).light(state.lightCoords).color(color).extractRenderState();
    }

    @Override
    public void submit(
        KineticBatteryRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.top == null || state.bottom == null) {
            return;
        }
        if (state.topAngle != null) {
            matrices.pushPose();
            matrices.rotateAround(state.topAngle, 0.5f, 0.5f, 0.5f);
            state.top.submit(matrices, queue);
            matrices.popPose();
        } else {
            state.top.submit(matrices, queue);
        }
        if (state.bottomAngle != null) {
            matrices.rotateAround(state.bottomAngle, 0.5f, 0.5f, 0.5f);
        }
        state.bottom.submit(matrices, queue);
    }

    public static class KineticBatteryRenderState extends BlockEntityRenderState {
        public @UnknownNullability BlockState blockState;
        public @Nullable SuperByteBufferRenderState top;
        public @Nullable SuperByteBufferRenderState bottom;
        public @Nullable Quaternionf topAngle;
        public @Nullable Quaternionf bottomAngle;
    }
}
