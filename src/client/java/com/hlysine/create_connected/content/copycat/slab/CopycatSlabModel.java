package com.hlysine.create_connected.content.copycat.slab;

import com.hlysine.create_connected.content.copycat.SimpleCopycatModel;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CopycatSlabModel extends SimpleCopycatModel {

    public CopycatSlabModel(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        super(state, unbaked);
    }

    @Override
    protected void collectPieces(BlockState state, List<Piece> pieces) {
        Direction facing = state.getOptionalValue(CopycatSlabBlock.SLAB_TYPE).isPresent()
                ? CopycatSlabBlock.getApparentDirection(state)
                : Direction.UP;
        boolean isDouble = state.getOptionalValue(CopycatSlabBlock.SLAB_TYPE).orElse(SlabType.BOTTOM) == SlabType.DOUBLE;

        for (boolean front : Iterate.trueAndFalse)
            pieces.add(assemblePiece(facing, front, false, isDouble));

        if (isDouble)
            for (boolean front : Iterate.trueAndFalse)
                pieces.add(assemblePiece(facing, front, true, isDouble));
    }

    private static Piece assemblePiece(Direction facing, boolean front, boolean topSlab, boolean isDouble) {
        Vec3 normal = Vec3.atLowerCornerOf(facing.getUnitVec3i());
        Vec3 normalScaled12 = normal.scale(12 / 16f);
        Vec3 normalScaledN8 = topSlab ? normal.scale((front ? 0 : -8) / 16f) : normal.scale((front ? 8 : 0) / 16f);
        float contract = 12;
        AABB bb = UNIT_CUBE.contract(normal.x * contract / 16, normal.y * contract / 16, normal.z * contract / 16);
        if (!front)
            bb = bb.move(normalScaled12);

        MutableCullFace cull = cull(0);
        for (Direction direction : Iterate.directions) {
            if (front && direction == facing)
                cull.add(direction);
            if (!front && direction == facing.getOpposite())
                cull.add(direction);
            if (isDouble && topSlab && direction == facing)
                cull.add(direction);
            if (isDouble && !topSlab && direction == facing.getOpposite())
                cull.add(direction);
        }

        return piece(bb, normalScaledN8, cull);
    }
}
