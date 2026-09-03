package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlockEntity;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity;
import com.zurrtum.create.api.packager.InventoryIdentifier;
import com.zurrtum.create.catnip.math.BlockFace;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CCInventoryIdentifiers {

    public static void register() {
        InventoryIdentifier.REGISTRY.register(CCBlocks.INVENTORY_ACCESS_PORT, (Level level, BlockState state, BlockFace face) -> {
            BlockEntity be = level.getBlockEntity(face.getPos());
            if (be instanceof InventoryAccessPortBlockEntity inventoryAccessPort) {
                return inventoryAccessPort.getInventoryId();
            }
            return null;
        });
        InventoryIdentifier.REGISTRY.register(CCBlocks.INVENTORY_BRIDGE, (Level level, BlockState state, BlockFace face) -> {
            BlockEntity be = level.getBlockEntity(face.getPos());
            if (be instanceof InventoryBridgeBlockEntity inventoryBridge) {
                return inventoryBridge.getInventoryId();
            }
            return null;
        });
    }
}
