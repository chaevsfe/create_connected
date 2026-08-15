package com.hlysine.create_connected.content;

import com.zurrtum.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class KineticHelper {
    public static void updateKineticBlock(KineticBlockEntity kineticTE) {
        if (kineticTE.hasNetwork())
            kineticTE.getOrCreateNetwork().remove(kineticTE);
        kineticTE.detachKinetics();
        kineticTE.removeSource();
        BlockState state = kineticTE.getBlockState();
        BlockPos pos = kineticTE.getBlockPos();
        Level level = Objects.requireNonNull(kineticTE.getLevel());
        BlockHelper.markAndNotifyBlock(level, pos, level.getChunkAt(pos), state, state, 3);
        if (kineticTE instanceof GeneratingKineticBlockEntity generatingBlockEntity) {
            generatingBlockEntity.reActivateSource = true;
        }
    }
}
