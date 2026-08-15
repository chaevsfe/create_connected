package com.hlysine.create_connected.content.inventorybridge;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.block.NeighborUpdateListeningBlock;
import com.zurrtum.create.foundation.item.ItemHelper;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Container;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

public class InventoryBridgeBlock extends Block
        implements IBE<InventoryBridgeBlockEntity>, IWrenchable, NeighborUpdateListeningBlock,
        ItemInventoryProvider<InventoryBridgeBlockEntity> {

    public static BooleanProperty ATTACHED_POSITIVE = BooleanProperty.create("attached_positive");
    public static BooleanProperty ATTACHED_NEGATIVE = BooleanProperty.create("attached_negative");
    public static EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

    public InventoryBridgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(ATTACHED_POSITIVE, false)
                .setValue(ATTACHED_NEGATIVE, false)
                .setValue(AXIS, Axis.X)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(ATTACHED_POSITIVE, ATTACHED_NEGATIVE, AXIS));
    }

    @Override
    @Nullable
    public Container getInventory(
            LevelAccessor world,
            BlockPos pos,
            BlockState state,
            InventoryBridgeBlockEntity blockEntity,
            @Nullable Direction context
    ) {
        return blockEntity.getItemCapability();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();

        Direction preferredFacing = null;
        for (Direction face : context.getNearestLookingDirections()) {
            BlockPos neighbourPos = context.getClickedPos().relative(face);
            if (ItemHelper.getInventory(context.getLevel(), neighbourPos, null) != null) {
                preferredFacing = face;
                break;
            }
        }

        if (preferredFacing == null) {
            preferredFacing = context.getNearestLookingDirection();
        }

        return state.setValue(AXIS, preferredFacing.getAxis());
    }

    @Override
    protected void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        withBlockEntityDo(worldIn, pos, InventoryBridgeBlockEntity::updateConnectedInventory);
    }

    @Override
    public void neighborUpdate(
            BlockState state,
            Level level,
            BlockPos pos,
            Block sourceBlock,
            BlockPos fromPos,
            boolean isMoving
    ) {
        withBlockEntityDo(level, pos, InventoryBridgeBlockEntity::updateConnectedInventory);
        Vec3i diff = fromPos.subtract(pos);
        Direction fromSide = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ(), null);
        if (fromSide == null)
            level.updateNeighborsAt(pos, this, null);
        else
            level.updateNeighborsAtExceptFromFacing(pos, this, fromSide, null);
    }

    public static Direction getNegativeTarget(BlockState state) {
        return Direction.fromAxisAndDirection(state.getValue(AXIS), Direction.AxisDirection.NEGATIVE);
    }

    public static Direction getPositiveTarget(BlockState state) {
        return Direction.fromAxisAndDirection(state.getValue(AXIS), Direction.AxisDirection.POSITIVE);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
        BlockPos pos1 = pos.relative(getNegativeTarget(blockState));
        BlockPos pos2 = pos.relative(getPositiveTarget(blockState));
        BlockState target1 = worldIn.getBlockState(pos1);
        BlockState target2 = worldIn.getBlockState(pos2);
        int total = 0;
        if (blockState.getValue(ATTACHED_NEGATIVE) && !target1.is(this) && target1.hasAnalogOutputSignal())
            total += target1.getAnalogOutputSignal(worldIn, pos1, direction);
        if (blockState.getValue(ATTACHED_POSITIVE) && !target2.is(this) && target2.hasAnalogOutputSignal())
            total += target2.getAnalogOutputSignal(worldIn, pos2, direction);
        return total / 2;
    }

    @Override
    public Class<InventoryBridgeBlockEntity> getBlockEntityClass() {
        return InventoryBridgeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends InventoryBridgeBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.INVENTORY_BRIDGE;
    }

}
