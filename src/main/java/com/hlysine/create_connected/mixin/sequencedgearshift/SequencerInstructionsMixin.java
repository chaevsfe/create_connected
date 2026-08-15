package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.content.sequencedgearshift.SequencerInstructionCodec;
import com.mojang.serialization.Codec;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = SequencerInstructions.class, remap = false)
public class SequencerInstructionsMixin {

    @Shadow
    @Final
    @Mutable
    private static SequencerInstructions[] $VALUES;

    @Shadow
    @Final
    @Mutable
    public static Codec<SequencerInstructions> CODEC;

    @Unique
    private static final SequencerInstructions create_connected$TURN_AWAIT = create_connected$addMember("TURN_AWAIT");

    @Unique
    private static final SequencerInstructions create_connected$TURN_TIME = create_connected$addMember("TURN_TIME");

    @Unique
    private static final SequencerInstructions create_connected$LOOP = create_connected$addMember("LOOP");

    @Invoker("<init>")
    public static SequencerInstructions create_connected$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Unique
    private static SequencerInstructions create_connected$addMember(String internalName) {
        List<SequencerInstructions> instructions = new ArrayList<>(Arrays.asList($VALUES));
        SequencerInstructions instruction = create_connected$invokeInit(internalName, instructions.size());
        instructions.add(instruction);
        $VALUES = instructions.toArray(new SequencerInstructions[0]);
        CODEC = SequencerInstructionCodec.create();
        return instruction;
    }

    @Inject(method = "needsPropagation()Z", at = @At("HEAD"), cancellable = true)
    private void create_connected$needsPropagation(CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if (self == create_connected$TURN_AWAIT || self == create_connected$TURN_TIME) {
            cir.setReturnValue(true);
        } else if (self == create_connected$LOOP) {
            cir.setReturnValue(false);
        }
    }
}
