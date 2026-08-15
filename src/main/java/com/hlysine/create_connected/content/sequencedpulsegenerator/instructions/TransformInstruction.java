package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TransformInstruction extends Instruction {
    private int input = -1;

    public TransformInstruction(int transform, int constant) {
        super(
                "transform",
                Background.INSTRUCTION,
                new ParameterConfig(
                        0,
                        9,
                        null,
                        1,
                        2,
                        ParameterConfig.transformFormat
                ),
                true,
                false
        );
        setParam(transform);
        setSignal(constant);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (input == -1) {
            input = be.getCurrentInput();
        } else if (be.getCurrentInput() != input) {
            input = -1;
            return InstructionResult.next(true);
        }
        return InstructionResult.incomplete();
    }

    @Override
    public int transformOutput(SequencedPulseGeneratorBlockEntity be, int signal) {
        return Math.clamp(switch (getParam()) {
            case 0 -> be.getCurrentInput() + getSignal();
            case 1 -> be.getCurrentInput() - getSignal();
            case 2 -> getSignal() - be.getCurrentInput();
            case 3 -> (long) be.getCurrentInput() * getSignal();
            case 4 -> getSignal() == 0 ? 0 : be.getCurrentInput() / getSignal();
            case 5 -> be.getCurrentInput() & getSignal();
            case 6 -> be.getCurrentInput() | getSignal();
            case 7 -> be.getCurrentInput() ^ getSignal();
            case 8 -> ((long) be.getCurrentInput() << getSignal()) & 15;
            case 9 -> ((long) be.getCurrentInput() >> getSignal()) & 15;
            default -> signal;
        }, 0, 15);
    }

    @Override
    public void writeState(ValueOutput view) {
        view.putInt("Input", input);
    }

    @Override
    public void readState(ValueInput view) {
        input = view.getIntOr("Input", -1);
    }

    @Override
    public Instruction copy() {
        return new TransformInstruction(getParam(), getSignal());
    }
}
