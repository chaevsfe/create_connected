package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.Instruction;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ConfigureSequencedPulseGeneratorPacket(BlockPos pos, Tag instructions) implements CustomPacketPayload {

    public static final int MAX_RANGE = 16;

    public static final Type<ConfigureSequencedPulseGeneratorPacket> TYPE =
            new Type<>(CreateConnected.asResource("configure_sequencer"));

    public static final StreamCodec<ByteBuf, ConfigureSequencedPulseGeneratorPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConfigureSequencedPulseGeneratorPacket::pos,
            ByteBufCodecs.TAG, ConfigureSequencedPulseGeneratorPacket::instructions,
            ConfigureSequencedPulseGeneratorPacket::new
    );

    public static void handle(ConfigureSequencedPulseGeneratorPacket packet, ServerPlayer player) {
        if (player.isSpectator() || !player.mayBuild())
            return;
        if (!(packet.instructions() instanceof ListTag list))
            return;
        ServerLevel level = player.level();
        BlockPos pos = packet.pos();
        if (!level.isLoaded(pos))
            return;
        if (!pos.closerThan(player.blockPosition(), MAX_RANGE))
            return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof SequencedPulseGeneratorBlockEntity be))
            return;
        be.currentInstruction = -1;
        be.instructions = Instruction.deserializeAll(list);
        be.reset();
        be.sendData();
        be.setChanged();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
