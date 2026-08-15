package com.hlysine.create_connected.content.inventoryaccessport;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.redstone.DirectedDirectionalBlock;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.block.NeighborUpdateListeningBlock;
import com.zurrtum.create.foundation.item.ItemHelper;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.Container;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jspecify.annotations.Nullable;

public class InventoryAccessPortBlock extends DirectedDirectionalBlock
        implements IBE<InventoryAccessPortBlockEntity>, IWrenchable, NeighborUpdateListeningBlock,
        ItemInventoryProvider<InventoryAccessPortBlockEntity> {

    public static BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;

    public InventoryAccessPortBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ATTACHED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(ATTACHED));
    }

    @Override
    @Nullable
    public Container getInventory(
            LevelAccessor world,
            BlockPos pos,
            BlockState state,
            InventoryAccessPortBlockEntity blockEntity,
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
            Direction facing = context.getNearestLookingDirection();
            preferredFacing = context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
                    ? facing : facing.getOpposite();
        }

        if (preferredFacing.getAxis() == Axis.Y) {
            state = state.setValue(TARGET, preferredFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR);
            preferredFacing = context.getHorizontalDirection();
        }

        return state.setValue(FACING, preferredFacing);
    }

    @Override
    protected void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        withBlockEntityDo(worldIn, pos, InventoryAccessPortBlockEntity::updateConnectedInventory);
    }

    @Override
    public void neighborUpdate(
            BlockState state,
            Level world,
            BlockPos pos,
            Block sourceBlock,
            BlockPos fromPos,
            boolean isMoving
    ) {
        withBlockEntityDo(world, pos, InventoryAccessPortBlockEntity::updateConnectedInventory);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
        if (!blockState.getValue(ATTACHED))
            return 0;
        BlockPos targetPos = pos.relative(DirectedDirectionalBlock.getTargetDirection(blockState));
        BlockState targetState = worldIn.getBlockState(targetPos);
        if (targetState.is(this))
            return 0;
        return targetState.hasAnalogOutputSignal()
                ? targetState.getAnalogOutputSignal(worldIn, targetPos, direction) : 0;
    }

    @Override
    public Class<InventoryAccessPortBlockEntity> getBlockEntityClass() {
        return InventoryAccessPortBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends InventoryAccessPortBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.INVENTORY_ACCESS_PORT;
    }

}
