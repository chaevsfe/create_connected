package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.content.kineticbridge.KineticBridgeRenderer.KineticBridgeRenderState;
import com.hlysine.create_connected.registries.CCPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
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

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotateAngleForBe;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotationAxisOf;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getTintColor;

public class KineticBridgeRenderer implements BlockEntityRenderer<KineticBlockEntity, KineticBridgeRenderState> {

    private final boolean isDestination;

    private KineticBridgeRenderer(Context context, boolean isDestination) {
        this.isDestination = isDestination;
    }

    public static KineticBridgeRenderer source(Context ctx) {
        return new KineticBridgeRenderer(ctx, false);
    }

    public static KineticBridgeRenderer destination(Context ctx) {
        return new KineticBridgeRenderer(ctx, true);
    }

    @Override
    public KineticBridgeRenderState createRenderState() {
        return new KineticBridgeRenderState();
    }

    @Override
    public void extractRenderState(
        KineticBlockEntity be,
        KineticBridgeRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.blockState = be.getBlockState();
        Direction facing = state.blockState.getValue(BlockStateProperties.FACING);
        Direction modelFacing = isDestination ? facing : facing.getOpposite();
        int color = getTintColor(be);
        int lightBehind = SmartBlockEntityRenderer.getLightCoords(level, state.blockPos.relative(facing.getOpposite()));
        int lightInFront = SmartBlockEntityRenderer.getLightCoords(level, state.blockPos.relative(facing));
        Axis axis = getRotationAxisOf(be);
        state.angle = getRotateAngleForBe(axis, axis.getPositive(), be, state, level);
        state.shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, modelFacing)
            .cardinalLighting(level).light(lightBehind).color(color).extractRenderState();
        state.coupling = CachedBuffers.partialFacing(
            isDestination ? CCPartialModels.KINETIC_BRIDGE_DESTINATION : CCPartialModels.KINETIC_BRIDGE_SOURCE,
            state.blockState,
            modelFacing
        ).cardinalLighting(level).light(lightInFront).color(color).extractRenderState();
    }

    @Override
    public void submit(
        KineticBridgeRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.shaft == null || state.coupling == null) {
            return;
        }
        if (state.angle != null) {
            matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
        }
        state.shaft.submit(matrices, queue);
        state.coupling.submit(matrices, queue);
    }

    public static class KineticBridgeRenderState extends BlockEntityRenderState {
        public @UnknownNullability BlockState blockState;
        public @Nullable SuperByteBufferRenderState shaft;
        public @Nullable SuperByteBufferRenderState coupling;
        public @Nullable Quaternionf angle;
    }
}
