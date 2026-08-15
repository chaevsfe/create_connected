package com.hlysine.create_connected.content.copycat.stairs;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.WaterloggedCopycatWrappedBlock;
import com.zurrtum.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Direction.Axis;
import static net.minecraft.core.Direction.AxisDirection;
import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.world.level.block.StairBlock.HALF;

public class CopycatStairsBlock extends WaterloggedCopycatWrappedBlock {

    public static StairBlock stairs;

    public CopycatStairsBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(StairBlock.FACING, NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(StairBlock.SHAPE, StairsShape.STRAIGHT)
        );
    }

    @Override
    public Block getWrappedBlock() {
        return stairs;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(StairBlock.FACING, HALF, StairBlock.SHAPE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = stairs.getStateForPlacement(pContext);
        if (state == null) return super.getStateForPlacement(pContext);
        return ICopycatWithWrappedBlock.copyState(state, super.getStateForPlacement(pContext), false);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(stairs, pState).getShape(pLevel, pPos, pContext);
    }

    @Override
    protected void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).attack(pLevel, pPos, pPlayer);
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).onPlace(pLevel, pPos, pOldState, pIsMoving);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState pState, ServerLevel pLevel, BlockPos pPos, boolean pIsMoving) {
        super.affectNeighborsAfterRemoval(pState, pLevel, pPos, pIsMoving);
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).affectNeighborsAfterRemoval(pLevel, pPos, pIsMoving);
    }

    @Override
    protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).randomTick(pLevel, pPos, pRandom);
    }

    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).tick(pLevel, pPos, pRandom);
    }

    @Override
    public BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess tickView,
                                  BlockPos pCurrentPos, Direction pDirection, BlockPos pNeighborPos,
                                  BlockState pNeighborState, RandomSource random) {
        return ICopycatWithWrappedBlock.unwrapForOperation(stairs, pState, state -> state.updateShape(
                pLevel, tickView, pCurrentPos, pDirection, pNeighborPos, pNeighborState, random));
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return ICopycatWithWrappedBlock.unwrapForOperation(stairs, pState, state -> state.rotate(pRotation));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return ICopycatWithWrappedBlock.unwrapForOperation(stairs, pState, state -> state.mirror(pMirror));
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return ICopycatWithWrappedBlock.wrappedState(stairs, pState).isPathfindable(pPathComputationType);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndLightGetter reader, BlockState state, Direction face,
                                             @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null)
            return true;

        boolean flipped = state.getValue(HALF) == Half.TOP;
        BlockState toState = reader.getBlockState(toPos);
        BlockPos diff = toPos.subtract(fromPos);
        if (diff.equals(Vec3i.ZERO)) {
            return true;
        }

        if (toState.is(this)) {
            return false;
        } else {
            if (diff.getY() == 0) {
                int fullCount = 0;
                if (diff.getX() != 0 && getFaceShape(state, Direction.fromAxisAndDirection(Axis.X, directionOf(diff.getX()))).isFull())
                    fullCount++;
                if (diff.getZ() != 0 && getFaceShape(state, Direction.fromAxisAndDirection(Axis.Z, directionOf(diff.getZ()))).isFull())
                    fullCount++;
                return fullCount == 0;
            } else {
                return (diff.getY() > 0) != flipped;
            }
        }
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndLightGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        BlockState toState = reader.getBlockState(toPos);
        BlockPos diff = toPos.subtract(fromPos);
        if (diff.equals(Vec3i.ZERO)) {
            return true;
        }
        Direction side = ICopycatWithWrappedBlock.fromDelta(diff);

        if (side != null) {
            FaceShape sideShape = getFaceShape(state, side);
            if (!sideShape.canConnect()) return false;
            if (toState.is(this)) {
                if (!sideShape.equals(getFaceShape(toState, side.getOpposite()))) return false;
            } else {
                if (!sideShape.isFull()) return false;
            }
        }

        return true;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        int count = getFaceShape(state, face).countBlocks();
        return count == 4 || count == 3 && state.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT;
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

    private static AxisDirection directionOf(int value) {
        return value >= 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
    }

    public static FaceShape getFaceShape(BlockState state, Direction face) {
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        Direction facing = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        if (!top && face == DOWN) return new FaceShape().fillAll();
        if (top && face == UP) return new FaceShape().fillAll();

        FaceShape faceShape = new FaceShape();

        switch (shape) {
            case STRAIGHT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop().rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top);
                if (face == facing.getOpposite()) return faceShape;
                return faceShape.fillRow(!top, facing.getAxisDirection());
            }
            case INNER_LEFT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop().fillBottom(AxisDirection.POSITIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top);
                if (face == facing.getOpposite())
                    return faceShape.fillRow(!top, facing.getCounterClockWise().getAxisDirection());
                if (face == facing.getCounterClockWise()) return faceShape.fillRow(!top);
                if (face == facing.getClockWise())
                    return faceShape.fillRow(!top, facing.getAxisDirection());
            }
            case INNER_RIGHT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop().fillBottom(AxisDirection.NEGATIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top);
                if (face == facing.getOpposite())
                    return faceShape.fillRow(!top, facing.getClockWise().getAxisDirection());
                if (face == facing.getClockWise()) return faceShape.fillRow(!top);
                if (face == facing.getCounterClockWise())
                    return faceShape.fillRow(!top, facing.getAxisDirection());
            }
            case OUTER_LEFT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop(AxisDirection.POSITIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top, facing.getCounterClockWise().getAxisDirection());
                if (face == facing.getOpposite())
                    return faceShape;
                if (face == facing.getCounterClockWise()) return faceShape.fillRow(!top, facing.getAxisDirection());
                if (face == facing.getClockWise())
                    return faceShape;
            }
            case OUTER_RIGHT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop(AxisDirection.NEGATIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top, facing.getClockWise().getAxisDirection());
                if (face == facing.getOpposite())
                    return faceShape;
                if (face == facing.getClockWise()) return faceShape.fillRow(!top, facing.getAxisDirection());
                if (face == facing.getCounterClockWise())
                    return faceShape;
            }
        }
        return faceShape;
    }

    private static class FaceShape {
        public boolean topNegative;
        public boolean topPositive;
        public boolean bottomNegative;
        public boolean bottomPositive;

        public FaceShape fillTop() {
            topNegative = topPositive = true;
            return this;
        }

        public FaceShape fillTop(AxisDirection direction) {
            switch (direction) {
                case POSITIVE -> topPositive = true;
                case NEGATIVE -> topNegative = true;
            }
            return this;
        }

        public FaceShape fillBottom() {
            bottomNegative = bottomPositive = true;
            return this;
        }

        public FaceShape fillBottom(AxisDirection direction) {
            switch (direction) {
                case POSITIVE -> bottomPositive = true;
                case NEGATIVE -> bottomNegative = true;
            }
            return this;
        }

        public FaceShape fillRow(boolean top) {
            if (top) return fillTop();
            return fillBottom();
        }

        public FaceShape fillRow(boolean top, AxisDirection direction) {
            if (top) return fillTop(direction);
            return fillBottom(direction);
        }

        public FaceShape fillAll() {
            return fillTop().fillBottom();
        }

        public FaceShape rotate(float angle) {
            return rotate((int) angle);
        }

        public FaceShape rotate(int angle) {
            angle = angle % 360;
            if (angle < 0) angle += 360;
            return switch (angle) {
                case 90 -> set(topNegative, bottomNegative, topPositive, bottomPositive);
                case 180 -> set(topPositive, topNegative, bottomPositive, bottomNegative);
                case 270 -> set(bottomPositive, topPositive, bottomNegative, topNegative);
                default -> this;
            };
        }

        public FaceShape set(boolean bottomNegative, boolean bottomPositive, boolean topNegative, boolean topPositive) {
            this.bottomNegative = bottomNegative;
            this.bottomPositive = bottomPositive;
            this.topNegative = topNegative;
            this.topPositive = topPositive;
            return this;
        }

        public int countBlocks() {
            int count = 0;
            if (bottomNegative) count++;
            if (bottomPositive) count++;
            if (topNegative) count++;
            if (topPositive) count++;
            return count;
        }

        public boolean canConnect() {
            return countBlocks() >= 3;
        }

        public boolean isFull() {
            return countBlocks() == 4;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof FaceShape shape)) return false;
            return shape.bottomNegative == this.bottomNegative && shape.bottomPositive == this.bottomPositive &&
                    shape.topNegative == this.topNegative && shape.topPositive == this.topPositive;
        }
    }
}
