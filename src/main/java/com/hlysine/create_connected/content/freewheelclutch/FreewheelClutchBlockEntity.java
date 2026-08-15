package com.hlysine.create_connected.content.freewheelclutch;

import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.RotationPropagator;
import com.zurrtum.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollOptionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import java.util.List;

import static com.hlysine.create_connected.content.freewheelclutch.FreewheelClutchBlock.UNCOUPLED;
import static com.zurrtum.create.content.contraptions.bearing.WindmillBearingBlockEntity.RotationDirection;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class FreewheelClutchBlockEntity extends SplitShaftBlockEntity {

    public ServerScrollOptionBehaviour<RotationDirection> movementDirection;

    public boolean reattachNextTick = false;

    public FreewheelClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        movementDirection = new ServerScrollOptionBehaviour<>(RotationDirection.class, this);
        movementDirection.withCallback(i -> this.onKineticUpdate());
        behaviours.add(movementDirection);
    }

    @Override
    public void initialize() {
        onKineticUpdate();
        super.initialize();
    }

    private void onKineticUpdate() {
        boolean coupled = !getBlockState().getValue(UNCOUPLED);
        boolean correctDirection = Mth.sign(getSpeed()) == (movementDirection.getValue() * 2 - 1);
        if (coupled != correctDirection && !isOverStressed()) {
            if (level != null) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().cycle(UNCOUPLED));
                level.scheduleTick(getBlockPos(), CCBlocks.FREEWHEEL_CLUTCH, 0, TickPriority.EXTREMELY_HIGH);
                reattachNextTick = true;
            }
        }
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        onKineticUpdate();
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        onKineticUpdate();
        super.onSpeedChanged(previousSpeed);
    }

    @Override
    public void tick() {
        super.tick();
        if (reattachNextTick && level != null) {
            reattachNextTick = false;
            RotationPropagator.handleAdded(level, getBlockPos(), this);
        }
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (face == getBlockState().getValue(FACING) && getBlockState().getValue(UNCOUPLED))
            return 0;
        return 1;
    }
}
