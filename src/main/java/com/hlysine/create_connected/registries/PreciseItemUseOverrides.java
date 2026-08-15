package com.hlysine.create_connected.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class PreciseItemUseOverrides {

    public static final Set<Identifier> OVERRIDES = new HashSet<>();

    public static void addBlock(Block block) {
        OVERRIDES.add(BuiltInRegistries.BLOCK.getKey(block));
    }
}
