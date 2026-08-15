package com.hlysine.create_connected.mixin.inventoryaccess;

import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlockEntity;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity;
import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.api.entity.FakePlayerHandler;
import com.zurrtum.create.content.logistics.packager.PackagerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PackagerBlock.class)
public class PackagerBlockMixin {
    @Inject(
            method = "getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void rejectInventoryAccessBlocks(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (cir.getReturnValue() == null) return;
        Player player = context.getPlayer();
        Direction preferredFacing = cir.getReturnValue().getValue(PackagerBlock.FACING);
        if (player != null && !FakePlayerHandler.has(player)) {
            BlockPos targetPos = context.getClickedPos().relative(preferredFacing.getOpposite());
            BlockState targetState = context.getLevel().getBlockState(targetPos);
            if (targetState.is(CCBlocks.INVENTORY_ACCESS_PORT) || targetState.is(CCBlocks.INVENTORY_BRIDGE)) {
                BlockEntity targetBlockEntity = context.getLevel().getBlockEntity(targetPos);
                if (targetBlockEntity == null) return;
                if (targetBlockEntity instanceof InventoryAccessPortBlockEntity inventoryAccess) {
                    BlockState attached = inventoryAccess.getAttachedBlock();
                    if (attached != null && attached.is(AllBlocks.PORTABLE_STORAGE_INTERFACE)) {
                        player.sendOverlayMessage(Component.translatable("create.packager.no_portable_storage"));
                        cir.setReturnValue(null);
                        return;
                    }
                }
                if (targetBlockEntity instanceof InventoryBridgeBlockEntity bridge) {
                    BlockState attached = bridge.getPositiveAttachedBlock();
                    if (attached != null && attached.is(AllBlocks.PORTABLE_STORAGE_INTERFACE)) {
                        player.sendOverlayMessage(Component.translatable("create.packager.no_portable_storage"));
                        cir.setReturnValue(null);
                        return;
                    }
                    attached = bridge.getNegativeAttachedBlock();
                    if (attached != null && attached.is(AllBlocks.PORTABLE_STORAGE_INTERFACE)) {
                        player.sendOverlayMessage(Component.translatable("create.packager.no_portable_storage"));
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
