package com.hlysine.create_connected.network;

import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.config.SyncConfigBase;
import com.hlysine.create_connected.content.contraption.jukebox.PlayContraptionJukeboxPacket;
import com.hlysine.create_connected.content.sequencedpulsegenerator.ConfigureSequencedPulseGeneratorPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CCNetwork {

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(
                ConfigureSequencedPulseGeneratorPacket.TYPE,
                ConfigureSequencedPulseGeneratorPacket.STREAM_CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                PlayContraptionJukeboxPacket.TYPE,
                PlayContraptionJukeboxPacket.STREAM_CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                SyncConfigPacket.TYPE,
                SyncConfigPacket.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                ConfigureSequencedPulseGeneratorPacket.TYPE,
                (packet, context) -> ConfigureSequencedPulseGeneratorPacket.handle(packet, context.player())
        );

        SyncConfigBase.setSender((player, config) -> ServerPlayNetworking.send(player, new SyncConfigPacket(config)));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CCConfigs.common().syncToPlayer(handler.player));
    }
}
