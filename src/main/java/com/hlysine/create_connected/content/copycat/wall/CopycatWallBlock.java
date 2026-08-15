package com.hlysine.create_connected.content.copycat.wall;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.WaterloggedCopycatWrappedBlock;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static net.minecraft.core.Direction.Axis;

public class CopycatWallBlock extends WaterloggedCopycatWrappedBlock {

    public static WallBlock wall;

    public CopycatWallBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(WallBlock.UP, true)
                .setValue(WallBlock.NORTH, WallSide.NONE)
                .setValue(WallBlock.SOUTH, WallSide.NONE)
                .setValue(WallBlock.EAST, WallSide.NONE)
                .setValue(WallBlock.WEST, WallSide.NONE)
        );
    }

    @Override
    public Block getWrappedBlock() {
        return wall;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(WallBlock.UP, WallBlock.NORTH, WallBlock.SOUTH, WallBlock.EAST, WallBlock.WEST));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = wall.getStateForPlacement(pContext);
        if (state == null) return super.getStateForPlacement(pContext);
        return ICopycatWithWrappedBlock.copyState(state, super.getStateForPlacement(pContext), false);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(wall, pState).getShape(pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(wall, pState).getCollisionShape(pLevel, pPos, pContext);
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return ICopycatWithWrappedBlock.wrappedState(wall, pState).isPathfindable(pPathComputationType);
    }

    @Override
    public BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess tickView,
                                  BlockPos pCurrentPos, Direction pDirection, BlockPos pNeighborPos,
                                  BlockState pNeighborState, RandomSource random) {
        return ICopycatWithWrappedBlock.unwrapForOperation(wall, pState, state -> state.updateShape(
                pLevel, tickView, pCurrentPos, pDirection, pNeighborPos, pNeighborState, random));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState pState) {
        return !pState.getValue(WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return ICopycatWithWrappedBlock.unwrapForOperation(wall, pState, state -> state.rotate(pRotation));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return ICopycatWithWrappedBlock.unwrapForOperation(wall, pState, state -> state.mirror(pMirror));
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndLightGetter reader, BlockState state, Direction face,
                                             @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null)
            return true;

        BlockState toState = reader.getBlockState(toPos);
        if (!toState.is(this) || !state.is(this)) return true;

        boolean isCross = true;
        for (Direction direction : Iterate.horizontalDirections) {
            if (toState.getValue(byDirection(direction)) == WallSide.NONE) {
                isCross = false;
                break;
            }
        }
        return isCross;
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndLightGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        BlockState toState = reader.getBlockState(toPos);
        if (!toState.is(this)) return false;

        long sideCount = Arrays.stream(Iterate.horizontalDirections).filter(s -> state.getValue(byDirection(s)) != WallSide.NONE).count();
        if (sideCount > 2)
            return false;
        if (sideCount == 2 && (state.getValue(WallBlock.NORTH) != state.getValue(WallBlock.SOUTH) || state.getValue(WallBlock.EAST) != state.getValue(WallBlock.WEST))) {
            return false;
        }

        BlockPos diff = toPos.subtract(fromPos);
        if (diff.equals(Vec3i.ZERO)) {
            return true;
        }
        Direction face = ICopycatWithWrappedBlock.fromDelta(diff);
        if (face == null) {
            if (diff.distManhattan(Vec3i.ZERO) > 2) return false;
            if (diff.getY() == 0) return false;
            Direction horizontalDiff = Direction.fromAxisAndDirection(diff.getX() == 0 ? Axis.Z : Axis.X,
                    (diff.getX() + diff.getZ() > 0) ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
            if (diff.getY() > 0) {
                if (state.getValue(byDirection(horizontalDiff)) != WallSide.TALL) return false;
                if (toState.getValue(byDirection(horizontalDiff.getOpposite())) == WallSide.NONE) return false;
            } else {
                if (state.getValue(byDirection(horizontalDiff)) == WallSide.NONE) return false;
                if (toState.getValue(byDirection(horizontalDiff.getOpposite())) != WallSide.TALL) return false;
            }
            return true;
        } else if (face == Direction.DOWN || face == Direction.UP) {
            return canConnectVertically(state) && canConnectVertically(toState);
        } else {
            if (state.getValue(WallBlock.UP)) return false;
            if (state.getValue(byDirection(face)) == WallSide.NONE) return false;
            return true;
        }
    }

    private boolean canConnectVertically(BlockState state) {
        if (!state.getValue(WallBlock.UP)) return false;
        for (Direction direction : Iterate.horizontalDirections) {
            WallSide side = state.getValue(byDirection(direction));
            if (side != WallSide.NONE) return false;
        }
        return true;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        if (face.getAxis().isHorizontal()) {
            WallSide side = state.getValue(byDirection(face));
            return side != WallSide.NONE &&
                    !state.getValue(WallBlock.UP) &&
                    side == state.getValue(byDirection(face.getOpposite())) &&
                    state.getValue(byDirection(face.getClockWise())) == WallSide.NONE &&
                    state.getValue(byDirection(face.getCounterClockWise())) == WallSide.NONE;
        }
        return false;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return !canFaceBeOccluded(state, face);
    }

    public static BlockState getMaterial(BlockGetter reader, BlockPos targetPos) {
        BlockState state = CopycatBlock.getMaterial(reader, targetPos);
        if (state.is(Blocks.AIR)) return reader.getBlockState(targetPos);
        return state;
    }

    public static EnumProperty<WallSide> byDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> WallBlock.NORTH;
            case SOUTH -> WallBlock.SOUTH;
            case WEST -> WallBlock.WEST;
            case EAST -> WallBlock.EAST;
            default -> throw new IllegalArgumentException("Vertical directions not supported");
        };
    }
}
