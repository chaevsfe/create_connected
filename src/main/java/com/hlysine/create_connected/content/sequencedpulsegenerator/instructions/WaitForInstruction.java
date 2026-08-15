package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WaitForInstruction extends Instruction {

    public WaitForInstruction(int target, int signal) {
        super(
                "wait_for",
                Background.INSTRUCTION,
                new ParameterConfig(
                        0,
                        1,
                        null,
                        1,
                        1,
                        ParameterConfig.booleanFormat
                ),
                true,
                false
        );
        setParam(target);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.getPreviousInput() == 0 && be.getCurrentInput() > 0 && getParam() == 1)
            return InstructionResult.next(true);
        if (be.getPreviousInput() > 0 && be.getCurrentInput() == 0 && getParam() == 0)
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
        return new WaitForInstruction(getParam(), getSignal());
    }
}
