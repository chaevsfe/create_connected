package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.content.sequencedgearshift.SequencerInstructionCodec;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencerInstructions;

public class CCSequencerInstructions {

    public static final SequencerInstructions TURN_AWAIT = SequencerInstructionCodec.require("TURN_AWAIT");
    public static final SequencerInstructions TURN_TIME = SequencerInstructionCodec.require("TURN_TIME");
    public static final SequencerInstructions LOOP = SequencerInstructionCodec.require("LOOP");

    public static void register() {
        SequencerInstructionCodec.verify();
    }
}
