package com.hlysine.create_connected.foundation.advancement;

import net.minecraft.world.entity.player.Player;

public interface Awardable {
    void awardTo(Player player);

    boolean isAlreadyAwardedTo(Player player);
}
