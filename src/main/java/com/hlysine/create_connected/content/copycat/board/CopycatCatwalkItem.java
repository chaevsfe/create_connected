package com.hlysine.create_connected.content.copycat.board;

import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

import static com.hlysine.create_connected.content.copycat.board.CopycatBoardBlock.DOWN;
import static com.hlysine.create_connected.content.copycat.board.CopycatBoardBlock.UP;
import static com.hlysine.create_connected.content.copycat.board.CopycatBoardBlock.byDirection;

public class CopycatCatwalkItem extends BlockItem {

    public CopycatCatwalkItem(Properties builder) {
        super(CCBlocks.COPYCAT_BOARD, builder.overrideDescription("item.create_connected.copycat_catwalk"));
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item self) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack stack, BlockState state) {
        Direction facing = player == null ? Direction.SOUTH : player.getDirection();
        for (Direction direction : Iterate.horizontalDirections) {
            state = state.setValue(byDirection(direction), direction.getAxis() != facing.getAxis());
        }
        state = state.setValue(DOWN, true);
        state = state.setValue(UP, false);
        world.setBlockAndUpdate(pos, state);
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }

}
