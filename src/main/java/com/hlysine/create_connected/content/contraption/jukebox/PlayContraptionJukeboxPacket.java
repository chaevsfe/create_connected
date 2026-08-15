package com.hlysine.create_connected.content.contraption.jukebox;

import com.hlysine.create_connected.CreateConnected;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PlayContraptionJukeboxPacket(
        Identifier level,
        int contraptionId,
        BlockPos contraptionPos,
        BlockPos worldPos,
        int recordId,
        boolean play,
        boolean silent
) implements CustomPacketPayload {

    public static final Type<PlayContraptionJukeboxPacket> TYPE =
            new Type<>(CreateConnected.asResource("play_contraption_jukebox"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayContraptionJukeboxPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, PlayContraptionJukeboxPacket::level,
            ByteBufCodecs.VAR_INT, PlayContraptionJukeboxPacket::contraptionId,
            BlockPos.STREAM_CODEC, PlayContraptionJukeboxPacket::contraptionPos,
            BlockPos.STREAM_CODEC, PlayContraptionJukeboxPacket::worldPos,
            ByteBufCodecs.VAR_INT, PlayContraptionJukeboxPacket::recordId,
            ByteBufCodecs.BOOL, PlayContraptionJukeboxPacket::play,
            ByteBufCodecs.BOOL, PlayContraptionJukeboxPacket::silent,
            PlayContraptionJukeboxPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
