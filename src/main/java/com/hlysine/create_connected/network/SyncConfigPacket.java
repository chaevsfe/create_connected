package com.hlysine.create_connected.network;

import com.hlysine.create_connected.CreateConnected;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncConfigPacket(CompoundTag nbt) implements CustomPacketPayload {

    public static final Type<SyncConfigPacket> TYPE = new Type<>(CreateConnected.asResource("sync_config"));

    public static final StreamCodec<ByteBuf, SyncConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SyncConfigPacket::nbt,
            SyncConfigPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
