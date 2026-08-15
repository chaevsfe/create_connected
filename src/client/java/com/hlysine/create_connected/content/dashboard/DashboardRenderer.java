package com.hlysine.create_connected.content.dashboard;

import com.hlysine.create_connected.content.dashboard.DashboardRenderer.DashboardRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;

import java.util.List;

public class DashboardRenderer implements BlockEntityRenderer<DashboardBlockEntity, DashboardRenderState> {

    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private static final float SCALE = 0.015625f * 0.52f;

    private final Font font;

    public DashboardRenderer(Context context) {
        this.font = context.font();
    }

    @Override
    public DashboardRenderState createRenderState() {
        return new DashboardRenderState();
    }

    @Override
    public void extractRenderState(
        DashboardBlockEntity be,
        DashboardRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        SignText text = be.getText();
        Direction facing = be.getBlockState().getValue(DashboardBlock.FACING);
        state.yRot = Axis.YP.rotationDegrees(-facing.toYRot());

        int lineHeight = be.getTextLineHeight();
        int maxWidth = be.getMaxTextLineWidth();
        state.midpoint = SignText.LINES * lineHeight / 2;
        state.lineHeight = lineHeight;

        FormattedCharSequence[] sequences = text.getRenderMessages(
            Minecraft.getInstance().isTextFilteringEnabled(),
            line -> {
                List<FormattedCharSequence> list = font.split(line, maxWidth);
                return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
            }
        );

        int darkColor = AbstractSignRenderer.getDarkColor(text);
        if (text.hasGlowingText()) {
            int textColor = text.getColor().getTextColor();
            boolean glowing = textColor == DyeColor.BLACK.getTextColor() || isOutlineVisible(state.blockPos);
            state.textColor = textColor;
            state.outlineColor = glowing ? darkColor : 0;
            state.light = LightCoordsUtil.FULL_BRIGHT;
        } else {
            state.textColor = darkColor;
            state.outlineColor = 0;
            state.light = state.lightCoords;
        }

        LineRenderState[] lines = new LineRenderState[SignText.LINES];
        for (int i = 0; i < SignText.LINES; i++) {
            FormattedCharSequence sequence = sequences[i];
            lines[i] = new LineRenderState(sequence, (float) (-font.width(sequence) / 2));
        }
        state.lines = lines;
    }

    @Override
    public void submit(
        DashboardRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.lines == null) {
            return;
        }
        matrices.pushPose();

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.mulPose(state.yRot);
        matrices.translate(-0.5f, -0.5f, -0.5f);

        matrices.translate(0.5f, 12 / 16f, 9 / 16f);
        matrices.mulPose(Axis.XP.rotationDegrees(-66.80141f));
        matrices.translate(0, 3.5f / 16f, 0.15f / 16f);

        matrices.scale(SCALE, -SCALE, SCALE);

        for (int i = 0; i < state.lines.length; i++) {
            LineRenderState line = state.lines[i];
            queue.submitText(
                matrices,
                line.x(),
                i * state.lineHeight - state.midpoint,
                line.sequence(),
                false,
                Font.DisplayMode.POLYGON_OFFSET,
                state.light,
                state.textColor,
                0,
                state.outlineColor
            );
        }

        matrices.popPose();
    }

    private static boolean isOutlineVisible(BlockPos blockPos) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer != null && minecraft.options.getCameraType().isFirstPerson() && localPlayer.isScoping()) {
            return true;
        }
        Entity entity = minecraft.getCameraEntity();
        return entity != null && entity.distanceToSqr(Vec3.atCenterOf(blockPos)) < (double) OUTLINE_RENDER_DISTANCE;
    }

    public record LineRenderState(FormattedCharSequence sequence, float x) {
    }

    public static class DashboardRenderState extends BlockEntityRenderState {
        public @UnknownNullability Quaternionf yRot;
        public @Nullable LineRenderState[] lines;
        public int lineHeight;
        public int midpoint;
        public int textColor;
        public int outlineColor;
        public int light;
    }
}
