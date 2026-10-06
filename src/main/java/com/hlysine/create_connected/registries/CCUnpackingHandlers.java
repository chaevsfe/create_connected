package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortUnpackingHandler;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeUnpackingHandler;
import com.zurrtum.create.api.packager.unpacking.UnpackingHandler;

public class CCUnpackingHandlers {
    public static void register() {
        UnpackingHandler.REGISTRY.register(CCBlocks.INVENTORY_ACCESS_PORT, InventoryAccessPortUnpackingHandler.INSTANCE);
        UnpackingHandler.REGISTRY.register(CCBlocks.INVENTORY_BRIDGE, InventoryBridgeUnpackingHandler.INSTANCE);
    }
}
