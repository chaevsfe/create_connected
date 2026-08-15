package com.hlysine.create_connected.content.sixwaygearbox;

import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.kinetics.base.IRotate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class VerticalSixWayGearboxItem extends BlockItem {

    public VerticalSixWayGearboxItem(Properties builder) {
        super(CCBlocks.SIX_WAY_GEARBOX, builder);
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item self) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
            BlockPos pos,
            Level world,
            @Nullable Player player,
            ItemStack stack,
            BlockState state
    ) {
        Direction.Axis prefferedAxis = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockState blockState = world.getBlockState(pos.relative(side));
            if (blockState.getBlock() instanceof IRotate rotate) {
                if (rotate.hasShaftTowards(world, pos.relative(side), blockState, side.getOpposite())) {
                    if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                        prefferedAxis = null;
                        break;
                    }
                    prefferedAxis = side.getAxis();
                }
            }
        }

        Direction.Axis axis = prefferedAxis == null ? player.getDirection().getClockWise().getAxis()
                : prefferedAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AXIS, axis));
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }

}
