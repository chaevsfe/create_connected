package com.hlysine.create_connected.mixin.copycat.fence;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ValueBoxRenderer.class, remap = false)
public class ValueBoxRendererMixin {

    @WrapOperation(
            method = "customZOffset(Lnet/minecraft/world/item/Item;)F",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;getBlock()Lnet/minecraft/world/level/block/Block;")
    )
    private static Block create_connected$getWrappedBlock(BlockItem instance, Operation<Block> original) {
        return ICopycatWithWrappedBlock.unwrap(original.call(instance));
    }
}
