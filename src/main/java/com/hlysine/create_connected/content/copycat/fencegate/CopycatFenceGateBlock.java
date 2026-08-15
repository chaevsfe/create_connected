package com.hlysine.create_connected.content.copycat.fencegate;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.WaterloggedCopycatWrappedBlock;
import com.hlysine.create_connected.mixin.copycat.fencegate.FenceGateBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CopycatFenceGateBlock extends WaterloggedCopycatWrappedBlock {

    public static FenceGateBlock fenceGate;

    public CopycatFenceGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FenceGateBlock.OPEN, false)
                .setValue(FenceGateBlock.POWERED, false)
                .setValue(FenceGateBlock.IN_WALL, false)
                .setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH)
        );
    }

    @Override
    public Block getWrappedBlock() {
        return fenceGate;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FenceGateBlock.OPEN, FenceGateBlock.POWERED,
                FenceGateBlock.IN_WALL, HorizontalDirectionalBlock.FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = fenceGate.getStateForPlacement(pContext);
        if (state == null) return super.getStateForPlacement(pContext);
        return ICopycatWithWrappedBlock.copyState(state, super.getStateForPlacement(pContext), false);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return ((FenceGateBlockAccessor) fenceGate).callUseWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).getShape(pLevel, pPos, pContext);
    }

    @Override
    public BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess tickView,
                                  BlockPos pCurrentPos, Direction pDirection, BlockPos pNeighborPos,
                                  BlockState pNeighborState, RandomSource random) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fenceGate, pState, state -> state.updateShape(
                pLevel, tickView, pCurrentPos, pDirection, pNeighborPos, pNeighborState, random));
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).getBlockSupportShape(pReader, pPos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).getCollisionShape(pLevel, pPos, pContext);
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).isPathfindable(pPathComputationType);
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock,
                                   @Nullable Orientation pOrientation, boolean pIsMoving) {
        ICopycatWithWrappedBlock.wrappedState(fenceGate, pState)
                .handleNeighborChanged(pLevel, pPos, pBlock, pOrientation, pIsMoving);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fenceGate, pState, state -> state.rotate(pRotation));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fenceGate, pState, state -> state.mirror(pMirror));
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndLightGetter reader, BlockState state, Direction face,
                                             @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        return true;
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndLightGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        return false;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return false;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return true;
    }
}
