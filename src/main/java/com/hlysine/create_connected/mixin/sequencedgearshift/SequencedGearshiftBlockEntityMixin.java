package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.registries.CCSequencerInstructions;
import com.zurrtum.create.content.kinetics.transmission.sequencer.Instruction;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.zurrtum.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SequencedGearshiftBlockEntity.class, remap = false)
public class SequencedGearshiftBlockEntityMixin {

    @Inject(method = "run(I)V", at = @At("HEAD"), cancellable = true)
    private void create_connected$runLoop(int instructionIndex, CallbackInfo ci) {
        SequencedGearshiftBlockEntity self = (SequencedGearshiftBlockEntity) (Object) this;
        Instruction instruction = self.getInstruction(instructionIndex);
        if (instruction == null)
            return;
        if (instruction.instruction != CCSequencerInstructions.LOOP)
            return;
        if (instructionIndex == 1 && self.getLevel() != null)
            self.getLevel().setBlock(self.getBlockPos(),
                    self.getBlockState().setValue(SequencedGearshiftBlock.STATE, instructionIndex + 1),
                    Block.UPDATE_ALL);
        self.run(instructionIndex == 0 ? -1 : 0);
        ci.cancel();
    }
}
