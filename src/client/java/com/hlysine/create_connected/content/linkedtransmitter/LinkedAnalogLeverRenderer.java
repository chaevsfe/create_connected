package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.content.linkedtransmitter.LinkedAnalogLeverRenderer.LinkedAnalogLeverRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.catnip.theme.Color;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.redstone.link.LinkRenderer;
import com.zurrtum.create.client.content.redstone.link.LinkRenderer.LinkRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.RAD_180;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.RAD_90;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getEastRadiansRotateAngle;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getUpRotateAngle;

public class LinkedAnalogLeverRenderer implements BlockEntityRenderer<LinkedAnalogLeverBlockEntity, LinkedAnalogLeverRenderState> {

    private final ItemModelResolver itemModelManager;

    public LinkedAnalogLeverRenderer(Context context) {
        this.itemModelManager = context.itemModelResolver();
    }

    @Override
    public LinkedAnalogLeverRenderState createRenderState() {
        return new LinkedAnalogLeverRenderState();
    }

    @Override
    public void extractRenderState(
        LinkedAnalogLeverBlockEntity be,
        LinkedAnalogLeverRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level world = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(world);
        BlockState blockState = be.getBlockState();
        state.blockState = blockState;

        float level = be.clientState.getValue(tickProgress) / 15;
        state.angle = getEastRadiansRotateAngle((float) (level / 2 * Math.PI));
        state.handle = CachedBuffers.partial(AllPartialModels.ANALOG_LEVER_HANDLE, blockState)
            .cardinalLighting(cardinalLighting).light(state.lightCoords).extractRenderState();

        AttachFace face = blockState.getValue(LinkedAnalogLeverBlock.FACE);
        if (face != AttachFace.FLOOR) {
            state.xRot = new Quaternionf().setAngleAxis(face == AttachFace.WALL ? RAD_90 : RAD_180, 1, 0, 0);
        }
        state.yRot = getUpRotateAngle(AngleHelper.horizontalAngle(blockState.getValue(LinkedAnalogLeverBlock.FACING)));

        int color = Color.mixColors(0xFF2C0300, 0xFFCD0000, level);
        state.indicator = CachedBuffers.partial(AllPartialModels.ANALOG_LEVER_INDICATOR, blockState)
            .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();

        state.link = LinkRenderer.getLinkRenderState(
            be,
            itemModelManager,
            be.isVirtual() ? -1 : cameraPos.distanceToSqr(VecHelper.getCenterOf(state.blockPos))
        );
    }

    @Override
    public void submit(
        LinkedAnalogLeverRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.handle == null || state.indicator == null) {
            return;
        }
        matrices.pushPose();
        if (state.yRot != null) {
            matrices.rotateAround(state.yRot, 0.5f, 0.5f, 0.5f);
        }
        if (state.xRot != null) {
            matrices.rotateAround(state.xRot, 0.5f, 0.5f, 0.5f);
        }
        state.indicator.submit(matrices, queue);
        if (state.angle != null) {
            matrices.rotateAround(state.angle, 0.5f, 0.0625f, 0.5f);
        }
        state.handle.submit(matrices, queue);
        matrices.popPose();

        if (state.link != null) {
            state.link.render(state.blockState, queue, matrices, state.lightCoords);
        }
    }

    public static class LinkedAnalogLeverRenderState extends BlockEntityRenderState {
        public @UnknownNullability BlockState blockState;
        public @Nullable SuperByteBufferRenderState handle;
        public @Nullable SuperByteBufferRenderState indicator;
        public @Nullable Quaternionf angle;
        public @Nullable Quaternionf xRot;
        public @Nullable Quaternionf yRot;
        public @Nullable LinkRenderState link;
    }
}
