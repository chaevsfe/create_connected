package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LoopIfMaxInstruction extends Instruction {

    public LoopIfMaxInstruction(int target) {
        super(
                "loop_if_max",
                Background.DELAY,
                new ParameterConfig(
                        0,
                        15,
                        null,
                        5,
                        1,
                        null
                ),
                false,
                false
        );
        setParam(target);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.getCurrentInput() <= getParam()) {
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
        return new LoopIfMaxInstruction(getParam());
    }
}
