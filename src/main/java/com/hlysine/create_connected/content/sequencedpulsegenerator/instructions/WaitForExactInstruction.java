package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WaitForExactInstruction extends Instruction {

    public WaitForExactInstruction(int target, int signal) {
        super(
                "wait_for_exact",
                Background.INSTRUCTION,
                new ParameterConfig(
                        0,
                        15,
                        null,
                        5,
                        1,
                        null
                ),
                true,
                false
        );
        setParam(target);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.getPreviousInput() != getParam() && be.getCurrentInput() == getParam())
            return InstructionResult.next(true);
        return InstructionResult.incomplete();
    }

    @Override
    public void writeState(ValueOutput view) {
    }

    @Override
    public void readState(ValueInput view) {
    }

    @Override
    public Instruction copy() {
        return new WaitForExactInstruction(getParam(), getSignal());
    }
}
