package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.registries.CCSequencerInstructions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.client.content.kinetics.transmission.sequencer.SequencedGearshiftScreen;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.widget.ScrollInput;
import com.zurrtum.create.content.kinetics.transmission.sequencer.Instruction;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Vector;

@Mixin(value = SequencedGearshiftScreen.class, remap = false)
public class SequencedGearshiftScreenMixin {

    @Shadow
    @Final
    private Vector<Instruction> instructions;

    @Shadow
    private Vector<Vector<ScrollInput>> inputs;

    @Shadow
    private static String translationKey(SequencerInstructions instruction) {
        throw new AssertionError();
    }

    @Inject(
            method = "hasValueParameter(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$hasValueParameter(SequencerInstructions instruction, CallbackInfoReturnable<Boolean> cir) {
        if (instruction == CCSequencerInstructions.TURN_TIME)
            cir.setReturnValue(true);
        else if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.LOOP)
            cir.setReturnValue(false);
    }

    @Inject(
            method = "hasSpeedParameter(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$hasSpeedParameter(SequencerInstructions instruction, CallbackInfoReturnable<Boolean> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.TURN_TIME)
            cir.setReturnValue(true);
        else if (instruction == CCSequencerInstructions.LOOP)
            cir.setReturnValue(false);
    }

    @Inject(
            method = "maxValue(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$maxValue(SequencerInstructions instruction, CallbackInfoReturnable<Integer> cir) {
        if (instruction == CCSequencerInstructions.TURN_TIME)
            cir.setReturnValue(600);
        else if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.LOOP)
            cir.setReturnValue(-1);
    }

    @Inject(
            method = "parameterKey(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;)Ljava/lang/String;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$parameterKey(SequencerInstructions instruction, CallbackInfoReturnable<String> cir) {
        if (instruction == CCSequencerInstructions.TURN_TIME)
            cir.setReturnValue(translationKey(instruction) + ".duration");
        else if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.LOOP)
            cir.setReturnValue(translationKey(instruction));
    }

    @Inject(
            method = "background(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;)Lcom/zurrtum/create/client/foundation/gui/AllGuiTextures;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$background(SequencerInstructions instruction, CallbackInfoReturnable<AllGuiTextures> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.TURN_TIME)
            cir.setReturnValue(AllGuiTextures.SEQUENCER_INSTRUCTION);
        else if (instruction == CCSequencerInstructions.LOOP)
            cir.setReturnValue(AllGuiTextures.SEQUENCER_END);
    }

    @Inject(
            method = "shiftStep(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$shiftStep(SequencerInstructions instruction, CallbackInfoReturnable<Integer> cir) {
        if (instruction == CCSequencerInstructions.TURN_TIME)
            cir.setReturnValue(20);
        else if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.LOOP)
            cir.setReturnValue(-1);
    }

    @Inject(
            method = "defaultValue(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$defaultValue(SequencerInstructions instruction, CallbackInfoReturnable<Integer> cir) {
        if (instruction == CCSequencerInstructions.TURN_TIME)
            cir.setReturnValue(10);
        else if (instruction == CCSequencerInstructions.TURN_AWAIT || instruction == CCSequencerInstructions.LOOP)
            cir.setReturnValue(-1);
    }

    @Inject(
            method = "formatValue(Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;I)Ljava/lang/String;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void create_connected$formatValue(SequencerInstructions instruction, int value, CallbackInfoReturnable<String> cir) {
        if (instruction != CCSequencerInstructions.TURN_TIME)
            return;
        if (value >= 20)
            cir.setReturnValue((value / 20) + "s");
        else
            cir.setReturnValue(value + "t");
    }

    @Inject(method = "updateParamsOfRow(I)V", at = @At("TAIL"))
    private void create_connected$updateParamsOfRow(int row, CallbackInfo ci) {
        if (instructions.get(row).instruction != CCSequencerInstructions.TURN_TIME)
            return;
        ScrollInput value = inputs.get(row).get(1);
        value.withStepFunction(context -> {
            int v = context.currentValue;
            if (!context.forward)
                v--;
            if (v < 20)
                return context.shift ? 20 : 1;
            return context.shift ? 100 : 20;
        });
    }

    @ModifyExpressionValue(
            method = "instructionUpdated(II)V",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;END:Lcom/zurrtum/create/content/kinetics/transmission/sequencer/SequencerInstructions;",
                    ordinal = 0
            )
    )
    private SequencerInstructions create_connected$loopIsTerminal(
            SequencerInstructions original,
            @Local(argsOnly = true, ordinal = 1) int state
    ) {
        if (SequencerInstructions.values()[state] == CCSequencerInstructions.LOOP)
            return CCSequencerInstructions.LOOP;
        return original;
    }
}
