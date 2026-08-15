package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.kinetics.base.DirectionalKineticBlock;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class KineticBridgeDestinationBlock extends DirectionalKineticBlock implements IBE<KineticBridgeDestinationBlockEntity> {

    public KineticBridgeDestinationBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public Direction getPreferredFacing(BlockPlaceContext context) {
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (!(blockState.getBlock() instanceof KineticBridgeBlock))
                continue;
            if (blockState.getValue(FACING) == side.getOpposite())
                return side.getOpposite();
        }
        return null;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement == null)
            return null;
        Direction preferredFacing = getPreferredFacing(context);
        if (preferredFacing == null)
            return null;
        return stateForPlacement.setValue(FACING, preferredFacing);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(CCBlocks.KINETIC_BRIDGE);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();

        if (stillValid(level, clickedPos, state)) {
            BlockPos sourcePos = getSource(clickedPos, state);
            if (!level.getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE))
                return super.onSneakWrenched(state, context);
            context = new UseOnContext(context.getPlayer(), context.getHand(),
                    new BlockHitResult(context.getClickLocation(), context.getClickedFace(), sourcePos,
                            context.isInside()));
            state = level.getBlockState(sourcePos);
        }

        return super.onSneakWrenched(state, context);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (stillValid(level, pos, state)) {
            BlockPos sourcePos = getSource(pos, state);
            if (level.getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE)) {
                level.destroyBlock(sourcePos, true);
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (stillValid(level, pos, state)) {
            BlockPos sourcePos = getSource(pos, state);
            if (!level.getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE)) {
                return super.playerWillDestroy(level, pos, state, player);
            }
            level.destroyBlockProgress(sourcePos.hashCode(), sourcePos, -1);
            if (!level.isClientSide() && player.isCreative())
                level.destroyBlock(sourcePos, false);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighbourPos,
            BlockState neighbourState,
            RandomSource random
    ) {
        if (stillValid(level, pos, state)) {
            BlockPos sourcePos = getSource(pos, state);
            if (level.getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE))
                if (!tickView.getBlockTicks().hasScheduledTick(sourcePos, CCBlocks.KINETIC_BRIDGE))
                    tickView.scheduleTick(sourcePos, CCBlocks.KINETIC_BRIDGE, 1);
            return state;
        }
        if (!tickView.getBlockTicks().hasScheduledTick(pos, this))
            tickView.scheduleTick(pos, this, 1);
        return state;
    }

    public static BlockPos getSource(BlockPos pos, BlockState state) {
        Direction direction = state.getOptionalValue(FACING).orElse(Direction.NORTH);
        return pos.relative(direction.getOpposite());
    }

    public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state) {
        if (!state.is(this))
            return false;

        Direction direction = state.getValue(FACING);
        BlockPos sourcePos = pos.relative(direction.getOpposite());
        BlockState sourceState = level.getBlockState(sourcePos);
        return sourceState.getBlock() instanceof KineticBridgeBlock
                && sourceState.getValue(KineticBridgeBlock.FACING) == direction;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!stillValid(level, pos, state))
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public BlockEntityType<? extends KineticBridgeDestinationBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.KINETIC_BRIDGE_DESTINATION;
    }

    @Override
    public Class<KineticBridgeDestinationBlockEntity> getBlockEntityClass() {
        return KineticBridgeDestinationBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }
}
