package com.hlysine.create_connected.content.fluidvessel;

import com.hlysine.create_connected.registries.CCRegistration;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.AllClientHandle;
import com.zurrtum.create.api.contraption.storage.SyncedMountedStorage;
import com.zurrtum.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.zurrtum.create.api.contraption.storage.fluid.WrapperMountedFluidStorage;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.content.contraptions.Contraption;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.foundation.fluid.FluidTank;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FluidVesselMountedStorage extends WrapperMountedFluidStorage<FluidVesselMountedStorage.Handler> implements SyncedMountedStorage {
    public static final MapCodec<FluidVesselMountedStorage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FluidVesselMountedStorage::getCapacity),
            FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(FluidVesselMountedStorage::getFluid)
    ).apply(i, FluidVesselMountedStorage::new));

    private boolean dirty;

    protected FluidVesselMountedStorage(MountedFluidStorageType<?> type, int capacity, FluidStack stack) {
        super(type);
        wrapped = new Handler(capacity, stack);
    }

    protected FluidVesselMountedStorage(int capacity, FluidStack stack) {
        this(CCRegistration.FLUID_VESSEL, capacity, stack);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof FluidTankBlockEntity tank && tank.isController()) {
            FluidTank inventory = tank.getTankInventory();
            inventory.setFluid(wrapped.getFluid());
        }
    }

    public FluidStack getFluid() {
        return wrapped.getFluid();
    }

    public int getCapacity() {
        return wrapped.getMaxAmountPerStack();
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void markClean() {
        dirty = false;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public void afterSync(Contraption contraption, BlockPos localPos) {
        BlockEntity be = AllClientHandle.INSTANCE.getBlockEntityClientSide(contraption, localPos);
        if (!(be instanceof FluidTankBlockEntity tank))
            return;

        FluidTank inv = tank.getTankInventory();
        inv.setFluid(getFluid());
        float fillLevel = inv.getFluid().getAmount() / (float) inv.getMaxAmountPerStack();
        if (tank.getFluidLevel() == null) {
            tank.setFluidLevel(LerpedFloat.linear().startWithValue(fillLevel));
        }
        tank.getFluidLevel().chase(fillLevel, 0.5, LerpedFloat.Chaser.EXP);
    }

    public static FluidVesselMountedStorage fromVessel(FluidTankBlockEntity vessel) {
        FluidTank inventory = vessel.getTankInventory();
        return new FluidVesselMountedStorage(inventory.getMaxAmountPerStack(), inventory.getFluid().copy());
    }

    public final class Handler extends FluidTank {
        public Handler(int capacity, FluidStack stack) {
            super(capacity);
            setFluid(stack);
        }

        @Override
        public void markDirty() {
            dirty = true;
        }
    }
}
