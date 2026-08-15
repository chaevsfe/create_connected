package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.foundation.advancement.AdvancementBehaviour;
import com.hlysine.create_connected.foundation.advancement.CCAdvancements;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.kinetics.RotationPropagator;
import com.zurrtum.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class OverstressClutchBlock extends AbstractEncasedShaftBlock implements IWrenchable, IBE<OverstressClutchBlockEntity> {
    public static final EnumProperty<ClutchState> STATE = EnumProperty.create("state", ClutchState.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public OverstressClutchBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(STATE, ClutchState.COUPLED)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            @Nullable Orientation wireOrientation,
            boolean isMoving
    ) {
        super.neighborChanged(state, level, pos, block, wireOrientation, isMoving);
        boolean wasPowered = state.getValue(POWERED);
        boolean isPowered = level.getBestNeighborSignal(pos) > 0;
        if (wasPowered != isPowered) {
            level.setBlockAndUpdate(pos, state.cycle(POWERED));
            withBlockEntityDo(level, pos, OverstressClutchBlockEntity::onKineticUpdate);
        }
    }

    @Override
    public Class<OverstressClutchBlockEntity> getBlockEntityClass() {
        return OverstressClutchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OverstressClutchBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.OVERSTRESS_CLUTCH;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), OverstressClutchBlockEntity::resetClutch);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return state.getValue(STATE) == ClutchState.UNCOUPLED ? 0 : 15;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof OverstressClutchBlockEntity kte))
            return;

        ClutchState clutchState = state.getValue(STATE);

        if (state.getValue(POWERED)) {
            level.setBlockAndUpdate(pos, state.setValue(STATE, ClutchState.COUPLED));
            kte.delay = 0;
            return;
        }
        if (clutchState == ClutchState.COUPLED || clutchState == ClutchState.UNCOUPLED) {
            kte.delay = 0;
            return;
        }
        if (kte.delay <= 0) {
            level.setBlockAndUpdate(pos, state.setValue(STATE, ClutchState.UNCOUPLED));
            RotationPropagator.handleRemoved(level, pos, kte);
            RotationPropagator.handleAdded(level, pos, kte);
            AdvancementBehaviour.tryAward(kte, CCAdvancements.OVERSTRESS_CLUTCH);
            return;
        }
        kte.delay--;
    }

    public enum ClutchState implements StringRepresentable {
        COUPLED, UNCOUPLING, UNCOUPLED;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
