package com.hlysine.create_connected.content.dashboard;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class ClientPlayerAccess {

    public static @Nullable Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}
