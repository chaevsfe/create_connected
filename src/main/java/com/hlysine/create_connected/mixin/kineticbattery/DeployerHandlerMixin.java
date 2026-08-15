package com.hlysine.create_connected.mixin.kineticbattery;

import com.hlysine.create_connected.registries.CCBlocks;
import com.hlysine.create_connected.registries.CCItems;
import com.zurrtum.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DeployerHandler.class, remap = false)
public class DeployerHandlerMixin {
    @Inject(
            method = "shouldActivate(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$activateForBattery(ItemStack held, Level world, BlockPos targetPos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (held.is(CCBlocks.KINETIC_BATTERY.asItem()) || held.is(CCItems.CHARGED_KINETIC_BATTERY))
            if (world.getBlockState(targetPos).is(CCBlocks.KINETIC_BATTERY))
                cir.setReturnValue(true);
    }
}
