package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LoopForInstruction extends Instruction {
    private int progress = 0;

    public LoopForInstruction(int target) {
        super(
                "loop_for",
                Background.DELAY,
                new ParameterConfig(
                        1,
                        100,
                        null,
                        10,
                        3,
                        null
                ),
                false,
                false
        );
        setParam(target);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        progress++;
        if (progress >= getParam()) {
            progress = 0;
            return InstructionResult.next(true);
        }
        return InstructionResult.backToTop(true);
    }

    @Override
    public void writeState(ValueOutput view) {
        view.putInt("Progress", progress);
    }

    @Override
    public void readState(ValueInput view) {
        progress = view.getIntOr("Progress", 0);
    }

    @Override
    public Instruction copy() {
        return new LoopForInstruction(getParam());
    }
}
