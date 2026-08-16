package com.hlysine.create_connected.content.chaincogwheel;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedSmallCogRenderer;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ChainCogwheelRenderer extends EncasedSmallCogRenderer {

    public ChainCogwheelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(
            SimpleKineticBlockEntity blockEntity,
            EncasedSmallCogRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(blockEntity, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        BlockState blockState = blockEntity.getBlockState();
        Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockState);
        Direction facing = axis.getPositive();

        state.angle = KineticBlockEntityRenderer.getRotateAngleWithoutBeOffset(axis, facing, blockEntity, state, level);
        state.model = CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, blockState, facing)
                .cardinalLighting(cardinalLighting)
                .light(state.lightCoords)
                .color(KineticBlockEntityRenderer.getTintColor(blockEntity))
                .extractRenderState();
    }
}
