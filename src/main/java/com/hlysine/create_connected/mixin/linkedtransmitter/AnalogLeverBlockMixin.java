package com.hlysine.create_connected.mixin.linkedtransmitter;

import com.hlysine.create_connected.registries.CCItems;
import com.zurrtum.create.content.redstone.analogLever.AnalogLeverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnalogLeverBlock.class)
public class AnalogLeverBlockMixin {

    @Inject(
            cancellable = true,
            at = @At("HEAD"),
            method = "useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
    )
    private void use(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.isHolding(CCItems.LINKED_TRANSMITTER)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(
            cancellable = true,
            at = @At("HEAD"),
            method = "onBlockActivated(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/ItemStack;)Z"
    )
    private static void skipSecondaryUseOverride(InteractionHand hand, BlockState state, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(CCItems.LINKED_TRANSMITTER)) {
            cir.setReturnValue(false);
        }
    }
}
