package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.registries.CCSequencerInstructions;
import com.zurrtum.create.content.kinetics.transmission.sequencer.Instruction;
import com.zurrtum.create.content.kinetics.transmission.sequencer.OnIsPoweredResult;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Instruction.class, remap = false)
public class InstructionMixin {

    @Inject(method = "getDuration(FF)I", at = @At("HEAD"), cancellable = true)
    private void create_connected$getDuration(float currentProgress, float speed, CallbackInfoReturnable<Integer> cir) {
        Instruction self = (Instruction) (Object) this;
        SequencerInstructions instruction = self.instruction;
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(-1);
        } else if (instruction == CCSequencerInstructions.TURN_TIME) {
            cir.setReturnValue((int) (self.value - currentProgress));
        } else if (instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "getTickProgress(F)F", at = @At("HEAD"), cancellable = true)
    private void create_connected$getTickProgress(float speed, CallbackInfoReturnable<Float> cir) {
        Instruction self = (Instruction) (Object) this;
        SequencerInstructions instruction = self.instruction;
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(0f);
        } else if (instruction == CCSequencerInstructions.TURN_TIME) {
            cir.setReturnValue(1f);
        } else if (instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(0f);
        }
    }

    @Inject(method = "getSpeedModifier()I", at = @At("HEAD"), cancellable = true)
    private void create_connected$getSpeedModifier(CallbackInfoReturnable<Integer> cir) {
        Instruction self = (Instruction) (Object) this;
        SequencerInstructions instruction = self.instruction;
        if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.TURN_TIME) {
            cir.setReturnValue(self.speedModifier.value);
        } else if (instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "onRedstonePulse()Lcom/zurrtum/create/content/kinetics/transmission/sequencer/OnIsPoweredResult;", at = @At("HEAD"), cancellable = true)
    private void create_connected$onRedstonePulse(CallbackInfoReturnable<OnIsPoweredResult> cir) {
        Instruction self = (Instruction) (Object) this;
        SequencerInstructions instruction = self.instruction;
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(OnIsPoweredResult.CONTINUE);
        } else if (instruction == CCSequencerInstructions.TURN_TIME || instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(OnIsPoweredResult.NOTHING);
        }
    }
}
