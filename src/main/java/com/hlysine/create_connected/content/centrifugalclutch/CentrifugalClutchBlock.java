package com.hlysine.create_connected.content.centrifugalclutch;

import com.hlysine.create_connected.content.CCBlockStateProperties;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.content.kinetics.RotationPropagator;
import com.zurrtum.create.content.kinetics.base.DirectionalKineticBlock;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CentrifugalClutchBlock extends DirectionalKineticBlock implements IBE<CentrifugalClutchBlockEntity> {

    public static final BooleanProperty UNCOUPLED = CCBlockStateProperties.UNCOUPLED;

    public CentrifugalClutchBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(UNCOUPLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNCOUPLED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<CentrifugalClutchBlockEntity> getBlockEntityClass() {
        return CentrifugalClutchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CentrifugalClutchBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.CENTRIFUGAL_CLUTCH;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return state.getValue(UNCOUPLED) ? 0 : 15;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof CentrifugalClutchBlockEntity kte))
            return;

        if (kte.reattachNextTick) {
            RotationPropagator.handleRemoved(level, pos, kte);
        }
    }
}
