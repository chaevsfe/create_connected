package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.config.CServer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.kinetics.deployer.ManualApplicationHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ManualApplicationHelper.class)
public class ManualApplicationRecipeMixin {
    @WrapOperation(
            method = "manualApplicationRecipesApplyInWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getCraftingRemainder()Lnet/minecraft/world/item/ItemStackTemplate;")
    )
    private static ItemStackTemplate craftingRemainingItemOnApplication(Item instance, Operation<ItemStackTemplate> original) {
        if (!CServer.ApplicationRemainingItemFix.get())
            return null;
        return original.call(instance);
    }
}
