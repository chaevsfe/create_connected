package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlockEntity;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity;
import com.zurrtum.create.api.packager.InventoryIdentifier;
import com.zurrtum.create.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class CCInventoryIdentifiers {

    private static final ThreadLocal<Set<BlockPos>> ACTIVE_LOOKUPS = ThreadLocal.withInitial(HashSet::new);

    public static void register() {
        InventoryIdentifier.REGISTRY.register(CCBlocks.INVENTORY_ACCESS_PORT, (Level level, BlockState state, BlockFace face) -> {
            BlockEntity be = level.getBlockEntity(face.getPos());
            if (be instanceof InventoryAccessPortBlockEntity inventoryAccessPort) {
                return findWithoutRecursion(face.getPos(), inventoryAccessPort::getInventoryId);
            }
            return null;
        });
        InventoryIdentifier.REGISTRY.register(CCBlocks.INVENTORY_BRIDGE, (Level level, BlockState state, BlockFace face) -> {
            BlockEntity be = level.getBlockEntity(face.getPos());
            if (be instanceof InventoryBridgeBlockEntity inventoryBridge) {
                return findWithoutRecursion(face.getPos(), inventoryBridge::getInventoryId);
            }
            return null;
        });
    }

    @Nullable
    private static InventoryIdentifier findWithoutRecursion(BlockPos pos, Supplier<@Nullable InventoryIdentifier> lookup) {
        BlockPos key = pos.immutable();
        Set<BlockPos> active = ACTIVE_LOOKUPS.get();
        if (!active.add(key))
            return null;
        try {
            return lookup.get();
        } finally {
            active.remove(key);
        }
    }
}
