package com.hlysine.create_connected.mixin.redstonelinkwildcard;

import com.hlysine.create_connected.content.redstonelinkwildcard.LinkWildcardNetworkHandler;
import com.zurrtum.create.content.redstone.link.IRedstoneLinkable;
import com.zurrtum.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RedstoneLinkNetworkHandler.class, priority = 2000)
public class RedstoneLinkNetworkHandlerMixin {
    @Inject(
            method = "updateNetworkOf(Lnet/minecraft/world/level/LevelAccessor;Lcom/zurrtum/create/content/redstone/link/IRedstoneLinkable;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void updateNetworkOf(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        if (LinkWildcardNetworkHandler.updateNetworkOf((RedstoneLinkNetworkHandler) (Object) this, world, actor))
            ci.cancel();
    }

    @Inject(
            method = "addToNetwork(Lnet/minecraft/world/level/LevelAccessor;Lcom/zurrtum/create/content/redstone/link/IRedstoneLinkable;)V",
            at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/redstone/link/RedstoneLinkNetworkHandler;updateNetworkOf(Lnet/minecraft/world/level/LevelAccessor;Lcom/zurrtum/create/content/redstone/link/IRedstoneLinkable;)V")
    )
    private void addToNetwork(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        LinkWildcardNetworkHandler.addToNetwork((RedstoneLinkNetworkHandler) (Object) this, world, actor);
    }

    @Inject(
            method = "removeFromNetwork(Lnet/minecraft/world/level/LevelAccessor;Lcom/zurrtum/create/content/redstone/link/IRedstoneLinkable;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", shift = At.Shift.AFTER)
    )
    private void removeFromNetwork(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        LinkWildcardNetworkHandler.removeFromNetwork((RedstoneLinkNetworkHandler) (Object) this, world, actor);
    }
}
