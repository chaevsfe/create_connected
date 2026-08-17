package com.hlysine.create_connected.foundation;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

public class ClientHitResultAccess {

    public static @Nullable HitResult getHitResult() {
        return Minecraft.getInstance().hitResult;
    }
}
