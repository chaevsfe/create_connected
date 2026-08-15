package com.hlysine.create_connected.content.crossconnector;

import com.hlysine.create_connected.content.IConnectionForwardingBlock;
import com.hlysine.create_connected.content.KineticHelper;
import com.hlysine.create_connected.registries.CCShapes;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.decoration.encasing.EncasableBlock;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CrossConnectorBlock extends Block implements IWrenchable, IConnectionForwardingBlock, IRotate, EncasableBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public CrossConnectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    public static Direction.@Nullable Axis getPreferredAxis(BlockPlaceContext context) {
        Direction.Axis prefferedAxis = null;
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (blockState.getBlock() instanceof IRotate rotate) {
                if (rotate.hasShaftTowards(context.getLevel(), context.getClickedPos().relative(side), blockState,
                        side.getOpposite())) {
                    if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                        prefferedAxis = null;
                        break;
                    }
                    prefferedAxis = side.getAxis();
                }
            }
        }
        return prefferedAxis;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis preferredAxis = getPreferredAxis(context);
        if (preferredAxis != null && (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown())) {
            Direction.Axis lookingAxis = context.getNearestLookingDirection().getAxis();
            if (lookingAxis == preferredAxis)
                return this.defaultBlockState()
                        .setValue(AXIS, preferredAxis.isVertical() ? Direction.Axis.X : Direction.Axis.Y);
            return this.defaultBlockState().setValue(AXIS, lookingAxis);
        }
        return this.defaultBlockState()
                .setValue(AXIS, preferredAxis != null && context.getPlayer().isShiftKeyDown()
                        ? context.getClickedFace().getAxis()
                        : context.getNearestLookingDirection().getAxis());
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        return tryEncase(state, level, pos, stack, player, hand, hitResult);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CCShapes.CROSS_CONNECTOR.get(state.getValue(AXIS));
    }

    public void updateConnections(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide())
            return;
        Direction.Axis axis = state.getValue(AXIS);
        for (Direction direction : Iterate.directions) {
            if (direction.getAxis() == axis)
                continue;
            BlockPos sourcePos = pos;
            BlockPos neighborPos = pos.relative(direction);
            while (sourcePos != neighborPos
                    && level.getBlockState(neighborPos).getBlock() instanceof IConnectionForwardingBlock forwardingBlock) {
                BlockPos tempSource = sourcePos;
                sourcePos = neighborPos;
                neighborPos = forwardingBlock.forwardConnection(level, tempSource,
                        tempSource.equals(pos) ? state : level.getBlockState(tempSource), neighborPos);
            }
            BlockEntity neighbourTE = level.getBlockEntity(neighborPos);
            if (neighbourTE instanceof KineticBlockEntity kineticTE) {
                KineticHelper.updateKineticBlock(kineticTE);
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        updateConnections(level, pos, state);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        updateConnections(level, pos, state);
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    public BlockPos forwardConnection(Level level, BlockPos sourcePos, BlockState sourceState, BlockPos neighbourPos) {
        BlockState state = level.getBlockState(neighbourPos);
        if (state.getBlock() != this)
            return neighbourPos;

        Vec3i offset = neighbourPos.subtract(sourcePos);
        if (!(sourceState.getBlock() instanceof IRotate rotatingBlock))
            return neighbourPos;
        Direction offsetDirection = Direction.getNearest(offset.getX(), offset.getY(), offset.getZ(), null);
        if (offsetDirection == null)
            return neighbourPos;
        if (!rotatingBlock.hasShaftTowards(level, sourcePos, sourceState, offsetDirection))
            return neighbourPos;
        if (offsetDirection.getAxis() == state.getValue(AXIS))
            return neighbourPos;
        return neighbourPos.offset(offset);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() != state.getValue(AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }
}
