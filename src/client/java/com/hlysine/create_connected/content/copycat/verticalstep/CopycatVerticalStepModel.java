package com.hlysine.create_connected.content.copycat.verticalstep;

import com.hlysine.create_connected.content.copycat.SimpleCopycatModel;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static net.minecraft.core.Direction.Axis;

public class CopycatVerticalStepModel extends SimpleCopycatModel {

    public CopycatVerticalStepModel(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        super(state, unbaked);
    }

    @Override
    protected void collectPieces(BlockState state, List<Piece> pieces) {
        Direction facing = state.getOptionalValue(CopycatVerticalStepBlock.FACING).orElse(Direction.NORTH);
        Direction perpendicular = facing.getCounterClockWise();

        int xOffset = (facing.getAxis() == Axis.X ? facing : perpendicular).getAxisDirection().getStep();
        int zOffset = (facing.getAxis() == Axis.Z ? facing : perpendicular).getAxisDirection().getStep();

        Vec3 rowNormal = new Vec3(1, 0, 0);
        Vec3 columnNormal = new Vec3(0, 0, 1);
        AABB bb = UNIT_CUBE.contract(12 / 16.0, 0, 12 / 16.0);

        for (boolean row : Iterate.trueAndFalse) {
            for (boolean column : Iterate.trueAndFalse) {

                AABB bb1 = bb;
                if (row)
                    bb1 = bb1.move(rowNormal.scale(12 / 16.0));
                if (column)
                    bb1 = bb1.move(columnNormal.scale(12 / 16.0));

                Vec3 offset = Vec3.ZERO;
                Vec3 rowShift = rowNormal.scale(row
                        ? 8 * Mth.clamp(xOffset, -1, 0) / 16.0
                        : 8 * Mth.clamp(xOffset, 0, 1) / 16.0
                );
                Vec3 columnShift = columnNormal.scale(column
                        ? 8 * Mth.clamp(zOffset, -1, 0) / 16.0
                        : 8 * Mth.clamp(zOffset, 0, 1) / 16.0
                );
                offset = offset.add(rowShift);
                offset = offset.add(columnShift);

                MutableCullFace cull = cull(0);
                for (Direction direction : Iterate.directions) {
                    if (direction.getAxis() == Axis.X && row == (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE))
                        cull.add(direction);
                    if (direction.getAxis() == Axis.Z && column == (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE))
                        cull.add(direction);
                }

                pieces.add(piece(bb1, offset, cull));
            }
        }
    }
}
