package com.hlysine.create_connected.mixin.copycat.fence;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.registries.CCBlocks;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
    @Inject(
            at = @At("HEAD"),
            method = "isSameFence(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            cancellable = true
    )
    private void connectToCopycatFence(BlockState pState, CallbackInfoReturnable<Boolean> cir) {
        if (pState.is(BlockTags.FENCES) &&
                (((FenceBlock) (Object) this).defaultBlockState().is(CCBlocks.COPYCAT_FENCE) ||
                        ((FenceBlock) (Object) this).defaultBlockState().is(CCBlocks.WRAPPED_COPYCAT_FENCE) ||
                        pState.is(CCBlocks.COPYCAT_FENCE) ||
                        pState.is(CCBlocks.WRAPPED_COPYCAT_FENCE)))
            cir.setReturnValue(true);
    }

    @Inject(
            at = @At("HEAD"),
            method = "connectsTo(Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/core/Direction;)Z",
            cancellable = true
    )
    private void connectsToCopycat(BlockState pState, boolean pIsSideSolid, Direction pDirection, CallbackInfoReturnable<Boolean> cir) {
        if (ICopycatWithWrappedBlock.unwrap(pState.getBlock()) instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(pState, pDirection))
            cir.setReturnValue(true);
    }
}
