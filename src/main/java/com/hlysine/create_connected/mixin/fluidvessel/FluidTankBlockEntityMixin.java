package com.hlysine.create_connected.mixin.fluidvessel;

import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlockEntity;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidTankBlockEntity.class)
public abstract class FluidTankBlockEntityMixin extends SmartBlockEntity {
    public FluidTankBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
            at = @At("HEAD"),
            method = "createRenderBoundingBox()Lnet/minecraft/world/phys/AABB;",
            cancellable = true
    )
    private void createRenderBoundingBox(CallbackInfoReturnable<AABB> cir) {
        FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
        if (self instanceof FluidVesselBlockEntity) {
            cir.setReturnValue(super.createRenderBoundingBox());
        }
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
            at = @At("HEAD"),
            method = "read(Lnet/minecraft/world/level/storage/ValueInput;Z)V",
            cancellable = true
    )
    private void read(ValueInput view, boolean clientPacket, CallbackInfo ci) {
        FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
        if (self instanceof FluidVesselBlockEntity) {
            super.read(view, clientPacket);
            ci.cancel();
        }
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
            at = @At("HEAD"),
            method = "write(Lnet/minecraft/world/level/storage/ValueOutput;Z)V",
            cancellable = true
    )
    private void write(ValueOutput view, boolean clientPacket, CallbackInfo ci) {
        FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
        if (self instanceof FluidVesselBlockEntity) {
            super.write(view, clientPacket);
            ci.cancel();
        }
    }
}
