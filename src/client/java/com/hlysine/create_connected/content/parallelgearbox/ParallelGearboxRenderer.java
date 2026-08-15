package com.hlysine.create_connected.content.parallelgearbox;

import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxRenderer.ParallelGearboxRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getProgress;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotateAngle;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotationOffsetForPosition;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getTintColor;

public class ParallelGearboxRenderer implements BlockEntityRenderer<ParallelGearboxBlockEntity, ParallelGearboxRenderState> {
    public ParallelGearboxRenderer(Context context) {
    }

    @Override
    public ParallelGearboxRenderState createRenderState() {
        return new ParallelGearboxRenderState();
    }

    @Override
    public void extractRenderState(
        ParallelGearboxBlockEntity be,
        ParallelGearboxRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.blockState = be.getBlockState();
        Axis boxAxis = state.blockState.getValue(BlockStateProperties.AXIS);
        int color = getTintColor(be);
        float progress = getProgress(be, level);
        List<ShaftRenderState> shafts = new ArrayList<>(4);
        for (Direction direction : Iterate.directions) {
            Axis axis = direction.getAxis();
            if (boxAxis == axis) {
                continue;
            }
            float offset = getRotationOffsetForPosition(be, state.blockPos, axis);
            Quaternionf angle = getRotateAngle(progress * be.getRotationSpeedModifier(direction), offset, axis);
            SuperByteBufferRenderState model = CachedBuffers.partialFacing(
                AllPartialModels.SHAFT_HALF,
                state.blockState,
                direction
            ).cardinalLighting(level).light(state.lightCoords).color(color).extractRenderState();
            shafts.add(new ShaftRenderState(model, angle));
        }
        state.shafts = shafts;
    }

    @Override
    public void submit(
        ParallelGearboxRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.shafts == null) {
            return;
        }
        for (ShaftRenderState shaft : state.shafts) {
            if (shaft.angle() == null) {
                shaft.model().submit(matrices, queue);
                continue;
            }
            matrices.pushPose();
            matrices.rotateAround(shaft.angle(), 0.5f, 0.5f, 0.5f);
            shaft.model().submit(matrices, queue);
            matrices.popPose();
        }
    }

    public record ShaftRenderState(SuperByteBufferRenderState model, @Nullable Quaternionf angle) {
    }

    public static class ParallelGearboxRenderState extends BlockEntityRenderState {
        public @UnknownNullability BlockState blockState;
        public @Nullable List<ShaftRenderState> shafts;
    }
}
