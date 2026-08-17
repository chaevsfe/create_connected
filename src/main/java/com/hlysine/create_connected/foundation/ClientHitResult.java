package com.hlysine.create_connected.foundation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class ClientHitResult {

    private static Supplier<@Nullable HitResult> supplier = () -> null;

    public static void setSupplier(Supplier<@Nullable HitResult> value) {
        supplier = value;
    }

    public static @Nullable BlockHitResult at(BlockPos pos) {
        if (supplier.get() instanceof BlockHitResult hit && hit.getBlockPos().equals(pos))
            return hit;
        return null;
    }
}
