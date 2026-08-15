package com.hlysine.create_connected.content.copycat.fence;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.WaterloggedCopycatWrappedBlock;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.CrossCollisionBlock.EAST;
import static net.minecraft.world.level.block.CrossCollisionBlock.NORTH;
import static net.minecraft.world.level.block.CrossCollisionBlock.SOUTH;
import static net.minecraft.world.level.block.CrossCollisionBlock.WEST;

public class CopycatFenceBlock extends WaterloggedCopycatWrappedBlock {

    public static FenceBlock fence;

    public CopycatFenceBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
        );
    }

    @Override
    public Block getWrappedBlock() {
        return fence;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(NORTH, SOUTH, EAST, WEST));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = fence.getStateForPlacement(pContext);
        if (state == null) return super.getStateForPlacement(pContext);
        return ICopycatWithWrappedBlock.copyState(state, super.getStateForPlacement(pContext), false);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState pState) {
        return !pState.getValue(WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(fence, pState).getShape(pLevel, pPos, pContext);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fence, pState, state -> state.rotate(pRotation));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fence, pState, state -> state.mirror(pMirror));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(fence, pState).getCollisionShape(pLevel, pPos, pContext);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(fence, pState).getVisualShape(pReader, pPos, pContext);
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return ICopycatWithWrappedBlock.wrappedState(fence, pState).isPathfindable(pPathComputationType);
    }

    @Override
    public BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess tickView,
                                  BlockPos pCurrentPos, Direction pDirection, BlockPos pNeighborPos,
                                  BlockState pNeighborState, RandomSource random) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fence, pState, state -> state.updateShape(
                pLevel, tickView, pCurrentPos, pDirection, pNeighborPos, pNeighborState, random));
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndLightGetter reader, BlockState state, Direction face,
                                             @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null)
            return true;

        BlockState toState = reader.getBlockState(toPos);
        if (!toState.is(this)) return true;
        return !canConnectTexturesToward(reader, toPos, fromPos, toState);
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndLightGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        if (toPos.getX() == fromPos.getX() && toPos.getZ() == fromPos.getZ()) {
            BlockState toState = reader.getBlockState(toPos);
            if (toState.is(this)) {
                if (isPole(state) && isPole(toState)) return true;
            }
        }
        return false;
    }

    private static boolean isPole(BlockState state) {
        for (Direction direction : Iterate.horizontalDirections) {
            if (state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction))) return false;
        }
        return true;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return false;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return true;
    }

    public static BlockState getMaterial(BlockGetter reader, BlockPos targetPos) {
        BlockState state = CopycatBlock.getMaterial(reader, targetPos);
        if (state.is(Blocks.AIR)) return reader.getBlockState(targetPos);
        return state;
    }

    public static BooleanProperty byDirection(Direction direction) {
        return PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
    }
}
