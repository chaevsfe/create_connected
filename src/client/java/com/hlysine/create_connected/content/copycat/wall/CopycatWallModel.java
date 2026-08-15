package com.hlysine.create_connected.content.copycat.wall;

import com.hlysine.create_connected.content.copycat.SimpleCopycatModel;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hlysine.create_connected.content.copycat.SimpleCopycatModel.MutableCullFace.*;
import static com.hlysine.create_connected.content.copycat.wall.CopycatWallBlock.byDirection;

public class CopycatWallModel extends SimpleCopycatModel {

    public CopycatWallModel(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        super(state, unbaked);
    }

    @Override
    protected void collectPieces(BlockState state, List<Piece> pieces) {
        boolean pole = state.getValue(WallBlock.UP);
        if (pole) {
            for (Direction direction : Iterate.horizontalDirections) {
                pieces.add(piece((int) direction.toYRot(), false,
                        vec3(4, 0, 4),
                        aabb(4, 16, 4),
                        cull(SOUTH | EAST)
                ));
            }

            for (Direction direction : Iterate.horizontalDirections) {
                int rot = (int) direction.toYRot();
                switch (state.getValue(byDirection(direction))) {
                    case NONE -> {
                        continue;
                    }
                    case LOW -> {
                        pieces.add(piece(rot, false,
                                vec3(5, 0, 12),
                                aabb(3, 7, 4),
                                cull(UP | NORTH | EAST)
                        ));
                        pieces.add(piece(rot, false,
                                vec3(8, 0, 12),
                                aabb(3, 7, 4).move(13, 0, 0),
                                cull(UP | NORTH | WEST)
                        ));
                        pieces.add(piece(rot, false,
                                vec3(5, 7, 12),
                                aabb(3, 7, 4).move(0, 9, 0),
                                cull(DOWN | NORTH | EAST)
                        ));
                        pieces.add(piece(rot, false,
                                vec3(8, 7, 12),
                                aabb(3, 7, 4).move(13, 9, 0),
                                cull(DOWN | NORTH | WEST)
                        ));
                    }
                    case TALL -> {
                        pieces.add(piece(rot, false,
                                vec3(5, 0, 12),
                                aabb(3, 16, 4),
                                cull(NORTH | EAST)
                        ));
                        pieces.add(piece(rot, false,
                                vec3(8, 0, 12),
                                aabb(3, 16, 4).move(13, 0, 0),
                                cull(NORTH | WEST)
                        ));
                    }
                }
            }
            return;
        }

        boolean tall = false;
        Map<Direction, WallSide> sides = new HashMap<>();
        for (Direction direction : Iterate.horizontalDirections) {
            WallSide wall = state.getValue(byDirection(direction));
            sides.put(direction, wall);
            if (wall == WallSide.TALL) tall = true;
        }

        if (sides.get(Direction.SOUTH) == sides.get(Direction.NORTH) &&
                sides.get(Direction.EAST) == sides.get(Direction.WEST) &&
                (sides.get(Direction.NORTH) == WallSide.NONE || sides.get(Direction.EAST) == WallSide.NONE) &&
                (sides.get(Direction.NORTH) != WallSide.NONE || sides.get(Direction.EAST) != WallSide.NONE)) {
            int rot = sides.get(Direction.SOUTH) == WallSide.NONE ? 90 : 0;

            if (!tall) {
                pieces.add(piece(rot, false,
                        vec3(5, 0, 0),
                        aabb(3, 7, 16),
                        cull(UP | EAST)
                ));
                pieces.add(piece(rot, false,
                        vec3(8, 0, 0),
                        aabb(3, 7, 16).move(13, 0, 0),
                        cull(UP | WEST)
                ));
                pieces.add(piece(rot, false,
                        vec3(5, 7, 0),
                        aabb(3, 7, 16).move(0, 9, 0),
                        cull(DOWN | EAST)
                ));
                pieces.add(piece(rot, false,
                        vec3(8, 7, 0),
                        aabb(3, 7, 16).move(13, 9, 0),
                        cull(DOWN | WEST)
                ));
            } else {
                pieces.add(piece(rot, false,
                        vec3(5, 0, 0),
                        aabb(3, 16, 16).move(0, 0, 0),
                        cull(EAST)
                ));
                pieces.add(piece(rot, false,
                        vec3(8, 0, 0),
                        aabb(3, 16, 16).move(13, 0, 0),
                        cull(WEST)
                ));
            }

            return;
        }

        Direction extendSide = null;
        long sideCount = sides.values().stream().filter(s -> s != WallSide.NONE).count();
        if (sideCount == 1) {
            extendSide = sides.entrySet().stream().filter(s -> s.getValue() != WallSide.NONE).findFirst()
                    .map(Map.Entry::getKey).orElse(null);
        } else {
            for (Direction direction : Iterate.horizontalDirections) {
                int rot = (int) direction.toYRot();
                if (tall) {
                    boolean cullCurrent = sides.get(direction.getOpposite()) == WallSide.TALL;
                    boolean cullAdjacent = sides.get(direction.getClockWise()) == WallSide.TALL;
                    pieces.add(piece(rot, false,
                            vec3(5, 0, 5),
                            aabb(3, 16, 3).move(0, 0, 0),
                            cull(SOUTH | EAST | (cullCurrent ? NORTH : 0) | (cullAdjacent ? WEST : 0))
                    ));
                } else {
                    boolean cullCurrent = sides.get(direction.getOpposite()) != WallSide.NONE;
                    boolean cullAdjacent = sides.get(direction.getClockWise()) != WallSide.NONE;
                    pieces.add(piece(rot, false,
                            vec3(5, 0, 5),
                            aabb(3, 7, 3).move(0, 0, 0),
                            cull(UP | SOUTH | EAST | (cullCurrent ? NORTH : 0) | (cullAdjacent ? WEST : 0))
                    ));
                    pieces.add(piece(rot, false,
                            vec3(5, 7, 5),
                            aabb(3, 7, 3).move(0, 9, 0),
                            cull(DOWN | SOUTH | EAST | (cullCurrent ? NORTH : 0) | (cullAdjacent ? WEST : 0))
                    ));
                }
            }
        }

        for (Direction direction : Iterate.horizontalDirections) {
            int rot = (int) direction.toYRot();
            boolean extend = extendSide == direction;
            boolean cullEnd = !extend;

            switch (sides.get(direction)) {
                case NONE -> {
                    continue;
                }
                case LOW -> {
                    pieces.add(piece(rot, false,
                            vec3(5, 0, extend ? 5 : 11),
                            aabb(3, 7, extend ? 11 : 5).move(0, 0, 0),
                            cull(UP | (cullEnd ? NORTH : 0) | EAST)
                    ));
                    pieces.add(piece(rot, false,
                            vec3(8, 0, extend ? 5 : 11),
                            aabb(3, 7, extend ? 11 : 5).move(13, 0, 0),
                            cull(UP | (cullEnd ? NORTH : 0) | WEST)
                    ));
                    pieces.add(piece(rot, false,
                            vec3(5, 7, extend ? 5 : 11),
                            aabb(3, 7, extend ? 11 : 5).move(0, 9, 0),
                            cull(DOWN | (cullEnd ? NORTH : 0) | EAST)
                    ));
                    pieces.add(piece(rot, false,
                            vec3(8, 7, extend ? 5 : 11),
                            aabb(3, 7, extend ? 11 : 5).move(13, 9, 0),
                            cull(DOWN | (cullEnd ? NORTH : 0) | WEST)
                    ));
                }
                case TALL -> {
                    pieces.add(piece(rot, false,
                            vec3(5, 0, extend ? 5 : 11),
                            aabb(3, 16, extend ? 11 : 5).move(0, 0, 0),
                            cull((cullEnd ? NORTH : 0) | EAST)
                    ));
                    pieces.add(piece(rot, false,
                            vec3(8, 0, extend ? 5 : 11),
                            aabb(3, 16, extend ? 11 : 5).move(13, 0, 0),
                            cull((cullEnd ? NORTH : 0) | WEST)
                    ));
                }
            }
        }
    }
}
