package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.content.KineticHelper;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class KineticBridgeBlockEntity extends KineticBlockEntity {

    public ServerScrollValueBehaviour stressMultiplier;
    private float previousStress = 0;
    private float previousSpeed = 0;

    public KineticBridgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        stressMultiplier = new StressImpactScrollValueBehaviour(this);
        stressMultiplier.between(0, 2048);
        stressMultiplier.setValue(40);
        stressMultiplier.withCallback(i -> this.updateSelfKinetic());
        behaviours.add(stressMultiplier);
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        updateDestinationKinetic();
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        updateDestinationKinetic();
        super.onSpeedChanged(previousSpeed);
    }

    public void updateSelfKinetic() {
        KineticHelper.updateKineticBlock(this);
    }

    public void updateDestinationKinetic() {
        if (getLevel() == null)
            return;
        float newStress = calculateStressApplied();
        if (getSpeed() == previousSpeed && newStress == previousStress)
            return;
        BlockPos destinationPos = getBlockPos().relative(getBlockState().getValue(KineticBridgeBlock.FACING));
        BlockEntity be = getLevel().getBlockEntity(destinationPos);
        if (!(be instanceof KineticBridgeDestinationBlockEntity destinationBE)) {
            return;
        }
        destinationBE.updateKineticsNextTick = true;
        previousSpeed = getSpeed();
        previousStress = newStress;
    }

    @Override
    public float calculateStressApplied() {
        this.lastStressApplied = StressImpactScrollValueBehaviour.convertValue(stressMultiplier.getValue());
        return this.lastStressApplied;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(1);
    }
}
