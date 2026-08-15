package com.hlysine.create_connected;

import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.api.registry.CreateRegisterPlugin;

public final class CreateConnectedPlugin implements CreateRegisterPlugin {
    private static boolean blocksRegistered;

    @Override
    public void onBlockRegister() {
        if (blocksRegistered) {
            throw new IllegalStateException("Create Fly invoked Create: Connected block registration more than once");
        }
        CCBlocks.register();
        blocksRegistered = true;
    }

    public static void verifyEarlyRegistrationComplete() {
        if (!blocksRegistered) {
            throw new IllegalStateException("Create Fly did not invoke Create: Connected early registration (blocks=false)");
        }
    }
}
