package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.ClutchState;
import com.hlysine.create_connected.foundation.advancement.AdvancementBehaviour;
import com.hlysine.create_connected.foundation.advancement.CCAdvancements;
import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.RotationPropagator;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.ticks.TickPriority;

import java.util.List;

import static com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.POWERED;
import static com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.STATE;

public class OverstressClutchBlockEntity extends SplitShaftBlockEntity {

    public int delay;
    public ServerScrollValueBehaviour maxDelay;

    public OverstressClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        AdvancementBehaviour.registerAwardables(this, behaviours, CCAdvancements.OVERSTRESS_CLUTCH);
        maxDelay = new TimeDelayScrollValueBehaviour(this);
        maxDelay.between(1, 60 * 20 * 60);
        maxDelay.setValue(1);
        maxDelay.withCallback(this::onMaxDelayChanged);
        behaviours.add(maxDelay);
    }

    private void onMaxDelayChanged(int newMax) {
        delay = Mth.clamp(delay, 0, newMax);
        sendData();
    }

    public boolean isIdle() {
        return delay == 0;
    }

    @Override
    public void initialize() {
        onKineticUpdate();
        super.initialize();
    }

    public void onKineticUpdate() {
        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLED && getBlockState().getValue(POWERED)) {
            resetClutch();
            return;
        }
        if (IRotate.StressImpact.isEnabled() && !getBlockState().getValue(POWERED)) {
            if (isOverStressed() && getBlockState().getValue(STATE) == ClutchState.COUPLED) {
                if (level != null) {
                    level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.UNCOUPLING), 2 | 16);
                    delay = maxDelay.getValue() - 1;
                    sendData();
                    return;
                }
            }
        }
        if (!isOverStressed() && getBlockState().getValue(STATE) == ClutchState.UNCOUPLING) {
            if (level != null) {
                level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.COUPLED), 2 | 16);
            }
        }
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        onKineticUpdate();
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face != getSourceFacing() && getBlockState().getValue(STATE) == ClutchState.UNCOUPLED)
                return 0;
        }
        return 1;
    }

    public void resetClutch() {
        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLED && !isOverStressed()) {
            if (level == null)
                return;
            level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.COUPLED), 3);
            RotationPropagator.handleRemoved(level, getBlockPos(), this);
            RotationPropagator.handleAdded(level, getBlockPos(), this);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLING && level != null && !level.isClientSide()) {
            level.scheduleTick(getBlockPos(), CCBlocks.OVERSTRESS_CLUTCH, 0, TickPriority.EXTREMELY_HIGH);
        }
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        delay = view.getIntOr("Delay", 0);
        super.read(view, clientPacket);
    }

    @Override
    protected void write(ValueOutput view, boolean clientPacket) {
        view.putInt("Delay", delay);
        super.write(view, clientPacket);
    }

    public static class TimeDelayScrollValueBehaviour extends ServerScrollValueBehaviour {

        public TimeDelayScrollValueBehaviour(SmartBlockEntity be) {
            super(be);
        }

        @Override
        public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
            int value = valueSetting.value();
            int multiplier = switch (valueSetting.row()) {
                case 0 -> 1;
                case 1 -> 20;
                default -> 60 * 20;
            };
            if (!valueSetting.equals(getValueSettings()))
                playFeedbackSound(this);
            setValue(Math.max(1, Math.max(1, value) * multiplier));
        }

        @Override
        public ValueSettings getValueSettings() {
            int row = 0;
            int value = this.value;

            if (value > 60 * 20) {
                value = value / (60 * 20);
                row = 2;
            } else if (value > 60) {
                value = value / 20;
                row = 1;
            }

            return new ValueSettings(row, value);
        }

        @Override
        public String getClipboardKey() {
            return "Timings";
        }

    }
}
