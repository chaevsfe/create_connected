package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CreateConnected;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Method;

public class DiagonalBlocksCompat {
    private static final String TYPES_CLASS = "fuzs.diagonalblocks.common.api.v2.block.type.DiagonalBlockTypes";
    private static final String TYPE_CLASS = "fuzs.diagonalblocks.common.api.v2.block.type.DiagonalBlockType";

    public static void disableWrappedFenceVariant() {
        if (!Mods.DIAGONAL_BLOCKS.isLoaded()) return;
        try {
            Object fenceType = Class.forName(TYPES_CLASS).getField("FENCE").get(null);
            Method disable = Class.forName(TYPE_CLASS).getMethod("disableBlockFactory", Identifier.class);
            disable.invoke(fenceType, CreateConnected.asResource("wrapped_copycat_fence"));
            CreateConnected.LOGGER.info("Diagonal Blocks detected, wrapped copycat fence excluded from diagonal variants");
        } catch (ReflectiveOperationException | RuntimeException e) {
            CreateConnected.LOGGER.warn("Could not exclude the wrapped copycat fence from Diagonal Blocks", e);
        }
    }
}
