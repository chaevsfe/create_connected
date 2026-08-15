package com.hlysine.create_connected.content.fancatalyst;

import com.hlysine.create_connected.content.fancatalyst.FanCatalystRotatingHeadRenderer.FanCatalystRotatingHeadRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class FanCatalystRotatingHeadRenderer implements BlockEntityRenderer<FanCatalystRotatingHeadBlockEntity, FanCatalystRotatingHeadRenderState> {

    private static final Vector3f TRANSLATION = new Vector3f(0.5f, 0.25f, 0.5f);
    private static final Vector3f DRAGON_SCALE = new Vector3f(-0.5f, -0.5f, 0.5f);
    private static final Vector3f CREEPER_SCALE = new Vector3f(-1f, -1f, 1f);

    private final SkullModelBase model;
    private final RenderType renderType;
    private final Vector3f scale;

    private FanCatalystRotatingHeadRenderer(Context context, SkullBlock.Type type, Vector3f scale) {
        this.model = SkullBlockRenderer.createModel(context.entityModelSet(), type);
        this.renderType = SkullBlockRenderer.getSkullRenderType(type, null);
        this.scale = scale;
    }

    public static FanCatalystRotatingHeadRenderer creeper(Context context) {
        return new FanCatalystRotatingHeadRenderer(context, SkullBlock.Types.CREEPER, CREEPER_SCALE);
    }

    public static FanCatalystRotatingHeadRenderer dragon(Context context) {
        return new FanCatalystRotatingHeadRenderer(context, SkullBlock.Types.DRAGON, DRAGON_SCALE);
    }

    @Override
    public FanCatalystRotatingHeadRenderState createRenderState() {
        return new FanCatalystRotatingHeadRenderState();
    }

    @Override
    public void extractRenderState(
        FanCatalystRotatingHeadBlockEntity be,
        FanCatalystRotatingHeadRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.animation = AnimationTickHolder.getRenderTime(level) % 360;
    }

    @Override
    public void submit(
        FanCatalystRotatingHeadRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        SkullModelBase.State modelState = new SkullModelBase.State();
        modelState.yRot = state.animation;
        matrices.pushPose();
        matrices.translate(TRANSLATION.x, TRANSLATION.y, TRANSLATION.z);
        matrices.scale(scale.x, scale.y, scale.z);
        queue.submitModel(
            model,
            modelState,
            matrices,
            renderType,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            -1,
            state.breakProgress
        );
        matrices.popPose();
    }

    public static class FanCatalystRotatingHeadRenderState extends BlockEntityRenderState {
        public float animation;
    }
}
