package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class OutputInstruction extends Instruction {
    private int progress = 0;

    public OutputInstruction(int duration, int signal) {
        super(
                "output",
                Background.INSTRUCTION,
                new ParameterConfig(
                        1,
                        600,
                        ParameterConfig.timeStep,
                        20,
                        10,
                        ParameterConfig.timeFormat
                ),
                true,
                false
        );
        setParam(duration);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        progress++;
        if (progress >= getParam()) {
            progress = 0;
            return InstructionResult.next(getParam() <= 0);
        }
        return InstructionResult.incomplete();
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
        return new OutputInstruction(getParam(), getSignal());
    }
}
