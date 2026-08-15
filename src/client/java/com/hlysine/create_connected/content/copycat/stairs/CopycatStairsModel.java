package com.hlysine.create_connected.content.copycat.stairs;

import com.hlysine.create_connected.content.copycat.SimpleCopycatModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

import java.util.List;

import static com.hlysine.create_connected.content.copycat.SimpleCopycatModel.MutableCullFace.*;

public class CopycatStairsModel extends SimpleCopycatModel {

    public CopycatStairsModel(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        super(state, unbaked);
    }

    @Override
    protected void collectPieces(BlockState state, List<Piece> pieces) {
        int facing = (int) state.getValue(StairBlock.FACING).toYRot();
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        StairsShape shape = state.getValue(StairBlock.SHAPE);

        switch (shape) {
            case STRAIGHT -> {
                pieces.add(piece(facing, top,
                        vec3(0, 0, 0),
                        aabb(16, 4, 8),
                        cull(UP | SOUTH)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 4, 0),
                        aabb(16, 4, 8).move(0, 12, 0),
                        cull(DOWN | SOUTH)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 0, 8),
                        aabb(16, 8, 8).move(0, 0, 8),
                        cull(UP | NORTH)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 8),
                        aabb(16, 8, 4).move(0, 8, 0),
                        cull(DOWN | SOUTH)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 12),
                        aabb(16, 8, 4).move(0, 8, 12),
                        cull(DOWN | NORTH)
                ));
            }
            case INNER_LEFT -> {
                pieces.add(piece(facing, top,
                        vec3(0, 0, 0),
                        aabb(8, 4, 8),
                        cull(UP | SOUTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 4, 0),
                        aabb(8, 4, 8).move(0, 12, 0),
                        cull(DOWN | SOUTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 0, 8),
                        aabb(16, 8, 8).move(0, 0, 8),
                        cull(UP | NORTH)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 8, 8),
                        aabb(8, 8, 8).move(8, 8, 8),
                        cull(DOWN | NORTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 12),
                        aabb(8, 8, 4).move(0, 8, 12),
                        cull(DOWN | NORTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 8),
                        aabb(8, 8, 4).move(0, 8, 0),
                        cull(DOWN | SOUTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(12, 8, 0),
                        aabb(4, 8, 8).move(12, 8, 0),
                        cull(DOWN | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 8, 0),
                        aabb(4, 8, 8).move(0, 8, 0),
                        cull(DOWN | SOUTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 0, 0),
                        aabb(8, 8, 8).move(8, 0, 0),
                        cull(UP | SOUTH | WEST)
                ));
            }
            case INNER_RIGHT -> {
                pieces.add(piece(facing, top,
                        vec3(8, 0, 0),
                        aabb(8, 4, 8).move(8, 0, 0),
                        cull(UP | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 4, 0),
                        aabb(8, 4, 8).move(8, 12, 0),
                        cull(DOWN | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 0, 8),
                        aabb(16, 8, 8).move(0, 0, 8),
                        cull(UP | NORTH)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 8),
                        aabb(8, 8, 8).move(0, 8, 8),
                        cull(DOWN | NORTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 8, 12),
                        aabb(8, 8, 4).move(8, 8, 12),
                        cull(DOWN | NORTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 8, 8),
                        aabb(8, 8, 4).move(8, 8, 0),
                        cull(DOWN | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(4, 8, 0),
                        aabb(4, 8, 8).move(12, 8, 0),
                        cull(DOWN | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 0),
                        aabb(4, 8, 8).move(0, 8, 0),
                        cull(DOWN | SOUTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 0, 0),
                        aabb(8, 8, 8).move(0, 0, 0),
                        cull(UP | SOUTH | EAST)
                ));
            }
            case OUTER_LEFT -> {
                pieces.add(piece(facing, top,
                        vec3(0, 0, 0),
                        aabb(8, 4, 16).move(0, 0, 0),
                        cull(UP | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 4, 0),
                        aabb(8, 4, 16).move(0, 12, 0),
                        cull(DOWN | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 0, 0),
                        aabb(8, 4, 8).move(8, 0, 0),
                        cull(UP | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 4, 0),
                        aabb(8, 4, 8).move(8, 12, 0),
                        cull(DOWN | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 0, 8),
                        aabb(8, 8, 8).move(8, 0, 8),
                        cull(UP | NORTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(12, 8, 12),
                        aabb(4, 8, 4).move(12, 8, 12),
                        cull(DOWN | NORTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 8, 12),
                        aabb(4, 8, 4).move(0, 8, 12),
                        cull(DOWN | NORTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(12, 8, 8),
                        aabb(4, 8, 4).move(12, 8, 0),
                        cull(DOWN | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 8, 8),
                        aabb(4, 8, 4).move(0, 8, 0),
                        cull(DOWN | SOUTH | EAST)
                ));
            }
            case OUTER_RIGHT -> {
                pieces.add(piece(facing, top,
                        vec3(8, 0, 0),
                        aabb(8, 4, 16).move(8, 0, 0),
                        cull(UP | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(8, 4, 0),
                        aabb(8, 4, 16).move(8, 12, 0),
                        cull(DOWN | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 0, 0),
                        aabb(8, 4, 8).move(0, 0, 0),
                        cull(UP | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 4, 0),
                        aabb(8, 4, 8).move(0, 12, 0),
                        cull(DOWN | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 0, 8),
                        aabb(8, 8, 8).move(0, 0, 8),
                        cull(UP | NORTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(4, 8, 12),
                        aabb(4, 8, 4).move(12, 8, 12),
                        cull(DOWN | NORTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 12),
                        aabb(4, 8, 4).move(0, 8, 12),
                        cull(DOWN | NORTH | EAST)
                ));
                pieces.add(piece(facing, top,
                        vec3(4, 8, 8),
                        aabb(4, 8, 4).move(12, 8, 0),
                        cull(DOWN | SOUTH | WEST)
                ));
                pieces.add(piece(facing, top,
                        vec3(0, 8, 8),
                        aabb(4, 8, 4).move(0, 8, 0),
                        cull(DOWN | SOUTH | EAST)
                ));
            }
        }
    }
}
