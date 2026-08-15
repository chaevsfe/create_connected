package com.hlysine.create_connected.compat;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public class DyeDepotCompat {
    public static String getColorNamespace(DyeColor color) {
        return Identifier.DEFAULT_NAMESPACE;
    }
}
