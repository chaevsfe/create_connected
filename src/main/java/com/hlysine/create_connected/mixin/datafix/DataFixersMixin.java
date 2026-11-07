package com.hlysine.create_connected.mixin.datafix;

import com.hlysine.create_connected.datafix.V4536;
import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public class DataFixersMixin {
    @Inject(
            method = "addFixers(Lcom/mojang/datafixers/DataFixerBuilder;Lnet/minecraft/util/filefix/FileFixerUpper$Builder;)V",
            at = @At(value = "CONSTANT", args = "intValue=4537")
    )
    private static void create_connected$addSchema(DataFixerBuilder builder, FileFixerUpper.Builder fileFixer, CallbackInfo ci) {
        V4536.register(builder);
    }
}
