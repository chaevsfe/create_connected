package com.hlysine.create_connected.content.sequencedgearshift;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.zurrtum.create.content.kinetics.transmission.sequencer.Instruction;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity.SequenceContext;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;

public final class SequencerInstructionCodec {

    private static final String[] EXTENSIONS = {"TURN_AWAIT", "TURN_TIME", "LOOP"};

    private SequencerInstructionCodec() {
    }

    public static Codec<SequencerInstructions> create() {
        return ExtraCodecs.orCompressed(
                Codec.STRING.comapFlatMap(SequencerInstructionCodec::byName, SequencerInstructions::getSerializedName),
                ExtraCodecs.idResolverCodec(SequencerInstructions::ordinal, SequencerInstructionCodec::byOrdinal, -1)
        );
    }

    public static SequencerInstructions require(String name) {
        for (SequencerInstructions value : SequencerInstructions.values()) {
            if (value.name().equals(name))
                return value;
        }
        throw new IllegalStateException("Sequencer instruction " + name + " is missing from SequencerInstructions");
    }

    public static void verify() {
        for (String name : EXTENSIONS) {
            SequencerInstructions value = require(name);
            verifyInstruction(name, value);
            verifySequenceContext(name, value);
        }
    }

    private static DataResult<SequencerInstructions> byName(String name) {
        for (SequencerInstructions value : SequencerInstructions.values()) {
            if (value.getSerializedName().equals(name))
                return DataResult.success(value);
        }
        return DataResult.error(() -> "Unknown sequencer instruction: " + name);
    }

    private static SequencerInstructions byOrdinal(int id) {
        SequencerInstructions[] values = SequencerInstructions.values();
        return id >= 0 && id < values.length ? values[id] : null;
    }

    private static void verifyInstruction(String name, SequencerInstructions value) {
        Tag encoded = Instruction.CODEC.encodeStart(NbtOps.INSTANCE, new Instruction(value, 1))
                .result()
                .orElseThrow(() -> new IllegalStateException("Instruction.CODEC cannot encode " + name));
        Instruction decoded = Instruction.CODEC.parse(NbtOps.INSTANCE, encoded)
                .result()
                .orElseThrow(() -> new IllegalStateException("Instruction.CODEC cannot decode " + name));
        if (decoded.instruction != value)
            throw new IllegalStateException("Instruction.CODEC round trip turned " + name + " into " + decoded.instruction);
    }

    private static void verifySequenceContext(String name, SequencerInstructions value) {
        Tag encoded = SequenceContext.CODEC.encodeStart(NbtOps.INSTANCE, new SequenceContext(value, 1))
                .result()
                .orElseThrow(() -> new IllegalStateException("SequenceContext.CODEC cannot encode " + name));
        SequenceContext decoded = SequenceContext.CODEC.parse(NbtOps.INSTANCE, encoded)
                .result()
                .orElseThrow(() -> new IllegalStateException("SequenceContext.CODEC cannot decode " + name));
        if (decoded.instruction() != value)
            throw new IllegalStateException("SequenceContext.CODEC round trip turned " + name + " into " + decoded.instruction());
    }
}
