package com.hlysine.create_connected.content.invertedclutch;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.content.kinetics.transmission.ClutchBlock;
import com.zurrtum.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class InvertedClutchBlock extends ClutchBlock {

    public InvertedClutchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level worldIn,
            BlockPos pos,
            Block blockIn,
            @Nullable Orientation wireOrientation,
            boolean isMoving
    ) {
        if (worldIn.isClientSide())
            return;

        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
            worldIn.setBlock(pos, state.cycle(POWERED), 2 | 16);
            detachKinetics(worldIn, pos, !previouslyPowered);
        }
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.INVERTED_CLUTCH;
    }

}
