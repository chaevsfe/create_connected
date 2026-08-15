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

import static com.hlysine.create_connected.content.copycat.board.CopycatBoardBlock.byDirection;

public class CopycatBoxItem extends BlockItem {

    public CopycatBoxItem(Properties builder) {
        super(CCBlocks.COPYCAT_BOARD, builder.overrideDescription("item.create_connected.copycat_box"));
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item self) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack stack, BlockState state) {
        for (Direction direction : Iterate.directions) {
            state = state.setValue(byDirection(direction), true);
        }
        world.setBlockAndUpdate(pos, state);
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }

}
