package com.hlysine.create_connected.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class ClutchValueBox extends CenteredSideValueBoxTransform {
    public ClutchValueBox() {
        super((state, d) -> {
            Direction.Axis axis = d.getAxis();
            Direction.Axis bearingAxis = state.getValue(FACING).getAxis();
            return bearingAxis != axis;
        });
    }

    @Override
    public void rotate(BlockState state, PoseStack ms) {
        Direction facing = getSide();
        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
        float yRot = AngleHelper.horizontalAngle(facing) + 180;

        if (facing.getAxis() == Direction.Axis.Y)
            TransformStack.of(ms).rotateYDegrees(180 + AngleHelper.horizontalAngle(state.getValue(FACING)));

        TransformStack.of(ms).rotateYDegrees(yRot).rotateXDegrees(xRot);
    }
}
