package com.hlysine.create_connected.content.copycat.board;

import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import org.jspecify.annotations.Nullable;

import static com.hlysine.create_connected.content.copycat.board.CopycatBoardBlock.byDirection;

public class CopycatBoxItem extends BlockItem {

    public CopycatBoxItem(Properties builder) {
        super(CCBlocks.COPYCAT_BOARD, builder.overrideDescription("item.create_connected.copycat_box"));
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item self) {
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null)
            return null;
        for (Direction direction : Iterate.directions) {
            state = state.setValue(byDirection(direction), true);
        }
        return state;
    }

}
