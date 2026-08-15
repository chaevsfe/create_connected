package com.hlysine.create_connected.mixin.copycat.fence;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.fence.WrappedFenceBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FenceBlock.class, priority = 1100)
public abstract class COMPATFenceBlockMixin extends CrossCollisionBlock {

    public COMPATFenceBlockMixin(float pNodeWidth, float pExtensionWidth, float pNodeHeight, float pExtensionHeight, float pCollisionHeight, Properties pProperties) {
        super(pNodeWidth, pExtensionWidth, pNodeHeight, pExtensionHeight, pCollisionHeight, pProperties);
    }

    @Inject(
            at = @At("HEAD"),
            method = "hasProperties()Z",
            cancellable = true,
            require = 0, expect = 0
    )
    public void hasProperties(CallbackInfoReturnable<Boolean> cir) {
        if (ICopycatWithWrappedBlock.unwrap(this) instanceof WrappedFenceBlock) cir.setReturnValue(false);
    }
}
