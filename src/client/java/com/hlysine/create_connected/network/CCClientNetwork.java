package com.hlysine.create_connected.network;

import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.content.contraption.jukebox.ContraptionMusicManager;
import com.hlysine.create_connected.content.contraption.jukebox.PlayContraptionJukeboxPacket;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.JukeboxSong;

public class CCClientNetwork {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                PlayContraptionJukeboxPacket.TYPE,
                (packet, context) -> handleJukebox(packet, context.client())
        );
        ClientPlayNetworking.registerGlobalReceiver(
                SyncConfigPacket.TYPE,
                (packet, context) -> {
                    CCConfigs.common().setSyncConfig(packet.nbt());
                    FeatureToggle.refreshItemVisibility();
                }
        );
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            CCConfigs.common().clearSyncConfig();
            FeatureToggle.refreshItemVisibility();
        });
    }

    private static void handleJukebox(PlayContraptionJukeboxPacket packet, Minecraft client) {
        ClientLevel level = client.level;
        if (level == null || !level.dimension().identifier().equals(packet.level()))
            return;
        if (!level.isLoaded(packet.worldPos()))
            return;
        if (!(level.getEntity(packet.contraptionId()) instanceof AbstractContraptionEntity contraptionEntity))
            return;

        JukeboxSong song = null;
        if (packet.play()) {
            song = level.registryAccess()
                    .lookupOrThrow(Registries.JUKEBOX_SONG)
                    .get(packet.recordId())
                    .map(Holder.Reference::value)
                    .orElse(null);
            if (song == null)
                return;
        }

        ContraptionMusicManager.playContraptionMusic(
                song,
                contraptionEntity,
                packet.contraptionPos(),
                packet.worldPos(),
                packet.silent()
        );
    }
}
