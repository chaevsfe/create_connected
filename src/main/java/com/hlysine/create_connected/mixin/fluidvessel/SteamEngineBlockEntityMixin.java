package com.hlysine.create_connected.mixin.fluidvessel;

import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.zurrtum.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SteamEngineBlockEntity.class)
public class SteamEngineBlockEntityMixin {
    @Inject(
            method = "isValid()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isValid(CallbackInfoReturnable<Boolean> cir) {
        SteamEngineBlockEntity be = ((SteamEngineBlockEntity) (Object) this);
        Direction dir = SteamEngineBlock.getConnectedDirection(be.getBlockState()).getOpposite();

        Level level = be.getLevel();
        if (level == null) {
            cir.setReturnValue(false);
            return;
        }

        if (level.getBlockState(be.getBlockPos().relative(dir)).is(CCBlocks.FLUID_VESSEL)) {
            cir.setReturnValue(true);
        }
    }
}
