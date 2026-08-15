package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LoopIfInstruction extends Instruction {

    public LoopIfInstruction(int target) {
        super(
                "loop_if",
                Background.DELAY,
                new ParameterConfig(
                        0,
                        1,
                        null,
                        1,
                        1,
                        ParameterConfig.booleanFormat
                ),
                false,
                false
        );
        setParam(target);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if ((be.getCurrentInput() > 0) == (getParam() == 1)) {
            return InstructionResult.backToTop(true);
        }
        return InstructionResult.next(true);
    }

    @Override
    public void writeState(ValueOutput view) {
    }

    @Override
    public void readState(ValueInput view) {
    }

    @Override
    public Instruction copy() {
        return new LoopIfInstruction(getParam());
    }
}
