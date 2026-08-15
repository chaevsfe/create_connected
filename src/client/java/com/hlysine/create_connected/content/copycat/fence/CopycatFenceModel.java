package com.hlysine.create_connected.content.copycat.fence;

import com.hlysine.create_connected.content.copycat.SimpleCopycatModel;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.hlysine.create_connected.content.copycat.SimpleCopycatModel.MutableCullFace.*;
import static com.hlysine.create_connected.content.copycat.fence.CopycatFenceBlock.byDirection;

public class CopycatFenceModel extends SimpleCopycatModel {

    public CopycatFenceModel(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        super(state, unbaked);
    }

    @Override
    protected void collectPieces(BlockState state, List<Piece> pieces) {
        for (Direction direction : Iterate.horizontalDirections) {
            pieces.add(piece((int) direction.toYRot(), false,
                    vec3(6, 0, 6),
                    aabb(2, 16, 2),
                    cull(SOUTH | EAST)
            ));
        }

        for (Direction direction : Iterate.horizontalDirections) {
            if (!state.getValue(byDirection(direction))) continue;

            int rot = (int) direction.toYRot();
            pieces.add(piece(rot, false,
                    vec3(7, 6, 10),
                    aabb(1, 1, 6),
                    cull(UP | NORTH | EAST)
            ));
            pieces.add(piece(rot, false,
                    vec3(8, 6, 10),
                    aabb(1, 1, 6).move(15, 0, 0),
                    cull(UP | NORTH | WEST)
            ));
            pieces.add(piece(rot, false,
                    vec3(7, 7, 10),
                    aabb(1, 2, 6).move(0, 14, 0),
                    cull(DOWN | NORTH | EAST)
            ));
            pieces.add(piece(rot, false,
                    vec3(8, 7, 10),
                    aabb(1, 2, 6).move(15, 14, 0),
                    cull(DOWN | NORTH | WEST)
            ));

            pieces.add(piece(rot, false,
                    vec3(7, 12, 10),
                    aabb(1, 1, 6),
                    cull(UP | NORTH | EAST)
            ));
            pieces.add(piece(rot, false,
                    vec3(8, 12, 10),
                    aabb(1, 1, 6).move(15, 0, 0),
                    cull(UP | NORTH | WEST)
            ));
            pieces.add(piece(rot, false,
                    vec3(7, 13, 10),
                    aabb(1, 2, 6).move(0, 14, 0),
                    cull(DOWN | NORTH | EAST)
            ));
            pieces.add(piece(rot, false,
                    vec3(8, 13, 10),
                    aabb(1, 2, 6).move(15, 14, 0),
                    cull(DOWN | NORTH | WEST)
            ));
        }
    }
}
