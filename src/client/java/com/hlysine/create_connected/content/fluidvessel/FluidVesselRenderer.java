package com.hlysine.create_connected.content.fluidvessel;

import com.hlysine.create_connected.content.fluidvessel.FluidVesselRenderer.FluidVesselRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.FluidRenderHelper;
import com.zurrtum.create.client.catnip.render.FluidRenderHelper.FluidRenderState;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class FluidVesselRenderer implements BlockEntityRenderer<FluidVesselBlockEntity, FluidVesselRenderState> {

    private static final float CAP_SIZE = 1 / 4f;
    private static final float TANK_HULL_SIZE = 1 / 16f + 1 / 128f;
    private static final float MIN_PUDDLE_HEIGHT = 1 / 16f;
    private static final float DIAL_PIVOT_Y = 6 / 16f;
    private static final float DIAL_PIVOT_Z = 8 / 16f;

    public FluidVesselRenderer(Context context) {
    }

    @Override
    public FluidVesselRenderState createRenderState() {
        return new FluidVesselRenderState();
    }

    @Override
    public boolean shouldRender(FluidVesselBlockEntity be, Vec3 cameraPosition) {
        return be.isController() && BlockEntityRenderer.super.shouldRender(be, cameraPosition);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public void extractRenderState(
        FluidVesselBlockEntity be,
        FluidVesselRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        state.fluid = null;
        state.boiler = null;
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.blockState = be.getBlockState();
        if (!be.isController()) {
            return;
        }
        if (be.hasWindow()) {
            extractFluid(be, state, level, tickProgress);
        } else if (be.boiler.isActive()) {
            extractBoiler(be, state, level, tickProgress);
        }
    }

    private void extractFluid(
        FluidVesselBlockEntity be,
        FluidVesselRenderState state,
        @Nullable Level world,
        float tickProgress
    ) {
        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null) {
            return;
        }
        float totalHeight = be.getWidth() - 2 * TANK_HULL_SIZE - MIN_PUDDLE_HEIGHT;
        float level = fluidLevel.getValue(tickProgress);
        if (level < 1 / (512f * totalHeight)) {
            return;
        }
        FluidStack fluidStack = be.getTankInventory().getFluid();
        if (fluidStack.isEmpty()) {
            return;
        }
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);
        state.translateY = clampedLevel - totalHeight;

        Direction.Axis axis = be.getAxis();
        float xMin = axis == Direction.Axis.X ? CAP_SIZE : TANK_HULL_SIZE;
        float xMax = axis == Direction.Axis.X
            ? xMin + be.getHeight() - 2 * CAP_SIZE
            : xMin + be.getWidth() - 2 * TANK_HULL_SIZE;
        float yMin = totalHeight + TANK_HULL_SIZE + MIN_PUDDLE_HEIGHT - clampedLevel;
        float yMax = yMin + clampedLevel;
        float zMin = axis == Direction.Axis.Z ? CAP_SIZE : TANK_HULL_SIZE;
        float zMax = axis == Direction.Axis.Z
            ? zMin + be.getHeight() - 2 * CAP_SIZE
            : zMin + be.getWidth() - 2 * TANK_HULL_SIZE;

        state.fluid = FluidRenderHelper.extractFluidRenderState(
            world instanceof BlockAndTintGetter getter ? getter : null,
            state.blockPos,
            Minecraft.getInstance().getModelManager().getFluidStateModelSet(),
            fluidStack.getFluid(),
            fluidStack.getComponentChanges(),
            xMin,
            yMin,
            zMin,
            xMax,
            yMax,
            zMax,
            state.lightCoords,
            false,
            true
        );
    }

    private void extractBoiler(
        FluidVesselBlockEntity be,
        FluidVesselRenderState state,
        @Nullable Level world,
        float tickProgress
    ) {
        Direction.Axis axis = be.getAxis();
        boolean[] occluded = be.boiler.occludedDirections;
        List<Quaternionf> faces = new ArrayList<>(2);
        for (Direction d : Iterate.horizontalDirections) {
            if (occluded[d.get2DDataValue()]) {
                continue;
            }
            if (d.getAxis() != axis) {
                continue;
            }
            faces.add(Axis.YP.rotationDegrees(-d.toYRot() - 90));
        }
        if (faces.isEmpty()) {
            return;
        }
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(world);
        BoilerRenderState data = new BoilerRenderState();
        data.faces = faces;
        data.centerX = axis == Direction.Axis.X ? be.getHeight() / 2f : be.getWidth() / 2f;
        data.centerZ = axis == Direction.Axis.Z ? be.getHeight() / 2f : be.getWidth() / 2f;
        data.gaugeOffsetX = be.getWidth() / 2f - 6 / 16f;
        data.xRot = KineticBlockEntityRenderer.getXRotateAngle(-145 * be.boiler.gauge.getValue(tickProgress) + 90);
        data.gauge = CachedBuffers.partial(AllPartialModels.BOILER_GAUGE, state.blockState)
            .cardinalLighting(cardinalLighting).light(state.lightCoords).extractRenderState();
        data.gaugeDial = CachedBuffers.partial(AllPartialModels.BOILER_GAUGE_DIAL, state.blockState)
            .cardinalLighting(cardinalLighting).light(state.lightCoords).extractRenderState();
        state.boiler = data;
    }

    @Override
    public void submit(
        FluidVesselRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.fluid != null) {
            matrices.translate(0, state.translateY, 0);
            state.fluid.submit(matrices, queue);
        } else if (state.boiler != null) {
            state.boiler.submit(matrices, queue);
        }
    }

    public static class FluidVesselRenderState extends BlockEntityRenderState {
        public @UnknownNullability BlockState blockState;
        public float translateY;
        public @Nullable FluidRenderState fluid;
        public @Nullable BoilerRenderState boiler;
    }

    public static class BoilerRenderState {
        public @UnknownNullability List<Quaternionf> faces;
        public @UnknownNullability SuperByteBufferRenderState gauge;
        public @UnknownNullability SuperByteBufferRenderState gaugeDial;
        public float centerX;
        public float centerZ;
        public float gaugeOffsetX;
        public @Nullable Quaternionf xRot;

        public void submit(PoseStack matrices, SubmitNodeCollector queue) {
            matrices.pushPose();
            matrices.translate(centerX, 0.5f, centerZ);
            for (Quaternionf yRot : faces) {
                matrices.pushPose();
                matrices.mulPose(yRot);
                matrices.translate(gaugeOffsetX - 0.5f, -0.5f, -0.5f);
                gauge.submit(matrices, queue);
                if (xRot != null) {
                    matrices.rotateAround(xRot, 0, DIAL_PIVOT_Y, DIAL_PIVOT_Z);
                }
                gaugeDial.submit(matrices, queue);
                matrices.popPose();
            }
            matrices.popPose();
        }
    }
}
