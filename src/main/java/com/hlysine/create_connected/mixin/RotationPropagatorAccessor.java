package com.hlysine.create_connected.mixin;

import com.zurrtum.create.content.kinetics.RotationPropagator;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RotationPropagator.class)
public interface RotationPropagatorAccessor {
    @Invoker
    static KineticBlockEntity callFindConnectedNeighbour(KineticBlockEntity currentTE, BlockPos neighbourPos) {
        throw new UnsupportedOperationException();
    }
}
