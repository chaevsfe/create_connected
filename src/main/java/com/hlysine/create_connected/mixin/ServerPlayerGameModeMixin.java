package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.content.crankwheel.CrankWheelItem;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterItem;
import com.hlysine.create_connected.registries.PreciseItemUseOverrides;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.AllItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerGameMode.class, priority = 900)
public class ServerPlayerGameModeMixin {

    @Inject(
            method = "useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true
    )
    private void create_connected$itemUseFirst(
            ServerPlayer player,
            Level level,
            ItemStack stack,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir,
            @Local BlockPos pos
    ) {
        InteractionResult result = CrankWheelItem.onItemUseFirst(level, player, stack, hand, hitResult, pos);
        if (result == null)
            result = LinkedTransmitterItem.onItemUseFirst(level, player, stack, hand, hitResult, pos);
        if (result != null)
            cir.setReturnValue(result);
    }

    @WrapOperation(
            method = "useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSecondaryUseActive()Z")
    )
    private boolean create_connected$preciseItemUseOverride(
            ServerPlayer instance,
            Operation<Boolean> original,
            @Local(argsOnly = true) ItemStack stack,
            @Local BlockState state
    ) {
        if (!stack.is(AllItems.WRENCH) && PreciseItemUseOverrides.OVERRIDES.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock())))
            return false;
        return original.call(instance);
    }
}
