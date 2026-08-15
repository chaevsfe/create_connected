package com.hlysine.create_connected.content.brake;

import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.foundation.advancement.AdvancementBehaviour;
import com.hlysine.create_connected.foundation.advancement.CCAdvancements;
import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.DoubleSupplier;

import static com.hlysine.create_connected.content.brake.BrakeBlock.POWERED;

public class BrakeBlockEntity extends SplitShaftBlockEntity {

    private static final int TICK_INTERVAL = 5;
    private static final float MIN_ADVANCEMENT_SPEED = 8;
    private int tickTimer = 0;
    private boolean advancementAwarded = false;

    public BrakeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        return 1;
    }

    @Override
    public float calculateStressApplied() {
        if (!getBlockState().getValue(POWERED)) {
            return super.calculateStressApplied();
        }
        float impact = CCConfigs.server().brakeActiveStress.getF();
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void tick() {
        super.tick();

        if (tickTimer-- < 0) {
            tickTimer = TICK_INTERVAL;

            DoubleSupplier stressSupplier = CCConfigs.server().stressValues.getImpact(CCBlocks.BRAKE);
            double unpoweredStress = stressSupplier == null ? 0 : stressSupplier.getAsDouble();
            double poweredStress = CCConfigs.server().brakeActiveStress.get();
            boolean isBraking = getBlockState().getValue(POWERED) == (poweredStress >= unpoweredStress);
            if (unpoweredStress == poweredStress) {
                isBraking = unpoweredStress > 0;
            }
            float absSpeed = Mth.abs(getSpeed());
            if (level.isClientSide()) {
                if (isBraking && absSpeed > 0) {
                    Vec3 loc = Vec3.atBottomCenterOf(getBlockPos());
                    level.addParticle(ParticleTypes.LARGE_SMOKE, loc.x, loc.y + 0.5, loc.z, 0, 0.05, 0);
                }
            } else {
                if (isBraking && absSpeed > MIN_ADVANCEMENT_SPEED && !advancementAwarded) {
                    advancementAwarded = true;
                    AdvancementBehaviour.tryAward(this, CCAdvancements.OVERPOWERED_BRAKE);
                } else if (!isBraking) {
                    advancementAwarded = false;
                }
            }
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        AdvancementBehaviour.registerAwardables(this, behaviours, CCAdvancements.OVERPOWERED_BRAKE);
    }
}
