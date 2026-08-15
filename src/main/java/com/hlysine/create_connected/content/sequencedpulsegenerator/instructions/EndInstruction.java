package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EndInstruction extends Instruction {
    public EndInstruction() {
        super("end", Background.END, null, false, true);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.terminate();
    }

    @Override
    public void writeState(ValueOutput view) {

    }

    @Override
    public void readState(ValueInput view) {

    }

    @Override
    public Instruction copy() {
        return new EndInstruction();
    }
}
