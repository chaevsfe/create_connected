package com.hlysine.create_connected.mixin.linkedtransmitter;

import com.zurrtum.create.client.content.redstone.link.LinkBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LinkBehaviour.class)
public interface LinkBehaviourAccessor {
    @Accessor("firstSlot")
    void create_connected$setFirstSlot(ValueBoxTransform slot);

    @Accessor("secondSlot")
    void create_connected$setSecondSlot(ValueBoxTransform slot);
}
