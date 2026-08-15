package com.hlysine.create_connected.mixin.nestedschematics;

import com.hlysine.create_connected.config.CServer;
import com.zurrtum.create.foundation.utility.FilesHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(FilesHelper.class)
public class FilesHelperMixin {
    @Inject(
            at = @At("HEAD"),
            method = "slug(Ljava/lang/String;)Ljava/lang/String;",
            cancellable = true
    )
    private static void slug(String name, CallbackInfoReturnable<String> cir) {
        if (CServer.SchematicsNestingDepth.get() > 0) {
            cir.setReturnValue(name.toLowerCase(Locale.ROOT)
                    .replaceAll("[^\\w/\\\\]+", "_")
                    .replaceAll("[/\\\\]+", "/"));
        }
    }
}
