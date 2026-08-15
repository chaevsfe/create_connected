package com.hlysine.create_connected.content.fluidvessel;

import com.zurrtum.create.content.fluids.tank.CreativeFluidTankBlockEntity.CreativeFluidTankInventory;
import com.zurrtum.create.foundation.fluid.FluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeFluidVesselBlockEntity extends FluidVesselBlockEntity {

    public CreativeFluidVesselBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected FluidTank createInventory() {
        return new CreativeFluidTankInventory(getCapacityMultiplier(), this::onFluidStackChanged);
    }

}
