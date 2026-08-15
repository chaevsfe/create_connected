package com.hlysine.create_connected.mixin.kineticbattery;

import com.zurrtum.create.content.kinetics.KineticNetwork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = KineticNetwork.class, remap = false)
public interface KineticNetworkAccessor {
    @Accessor("unloadedStress")
    float getUnloadedStress();
}
