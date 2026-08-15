package com.hlysine.create_connected.content.brake;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class BrakeBlock extends AbstractEncasedShaftBlock implements IBE<BrakeBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public BrakeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(POWERED,
                context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (!super.areStatesKineticallyEquivalent(oldState, newState))
            return false;
        return oldState.getValue(POWERED) == newState.getValue(POWERED);
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level worldIn,
            BlockPos pos,
            Block blockIn,
            @Nullable Orientation wireOrientation,
            boolean isMoving
    ) {
        if (worldIn.isClientSide())
            return;

        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
            KineticBlockEntity.switchToBlockState(worldIn, pos, state.cycle(POWERED));
        }
    }

    @Override
    public Class<BrakeBlockEntity> getBlockEntityClass() {
        return BrakeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BrakeBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.BRAKE;
    }
}
