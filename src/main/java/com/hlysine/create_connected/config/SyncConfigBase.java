package com.hlysine.create_connected.config;

import com.zurrtum.create.catnip.config.ConfigBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public abstract class SyncConfigBase extends ConfigBase {

    private static BiConsumer<ServerPlayer, CompoundTag> sender;

    public static void setSender(BiConsumer<ServerPlayer, CompoundTag> value) {
        sender = value;
    }

    public final CompoundTag getSyncConfig() {
        CompoundTag nbt = new CompoundTag();
        writeSyncConfig(nbt);
        for (ConfigBase child : children) {
            if (child instanceof SyncConfigBase syncChild) {
                if (nbt.contains(child.getName()))
                    throw new RuntimeException("A sync config key starts with " + child.getName() + " but does not belong to the child");
                nbt.put(child.getName(), syncChild.getSyncConfig());
            }
        }
        return nbt;
    }

    protected void writeSyncConfig(CompoundTag nbt) {
    }

    public final void setSyncConfig(CompoundTag config) {
        for (ConfigBase child : children) {
            if (child instanceof SyncConfigBase syncChild) {
                syncChild.readSyncConfig(config.getCompoundOrEmpty(child.getName()));
            }
        }
        readSyncConfig(config);
    }

    protected void readSyncConfig(CompoundTag nbt) {
    }

    public void syncToPlayer(ServerPlayer player) {
        if (player == null || sender == null) return;
        sender.accept(player, getSyncConfig());
    }

    public void syncToPlayers(Iterable<ServerPlayer> players) {
        if (sender == null) return;
        CompoundTag config = getSyncConfig();
        for (ServerPlayer player : players)
            sender.accept(player, config);
    }
}
